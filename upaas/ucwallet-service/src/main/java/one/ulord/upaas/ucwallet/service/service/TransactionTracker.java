package one.ulord.upaas.ucwallet.service.service;

import com.alibaba.fastjson.JSON;
import one.ulord.upaas.ucwallet.service.base.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.crypto.TransactionDecoder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;

import static one.ulord.upaas.ucwallet.service.base.common.Constants.INVALID_NONCE_VALUE;
import static one.ulord.upaas.ucwallet.service.base.common.Constants.NO_ENOUGH_SUT;

/**
 * Transaction tracker for transaction double confirm
 * @since 2018.10
 * @author yinhaibo(Ulord Dev Team)
 */
@Component
public class TransactionTracker {
    private static final Logger logger = LoggerFactory.getLogger(TransactionTracker.class);
    private static final String ADDRESS_SET = "ucwallet-service:address";
    private static final String PENDING_TX  = "ucwallet-service:pending-tx";
    private static final String CONFIRM_TX  = "ucwallet-service:confirm-tx";
    private static final String CURRENT_HEIGHT = "ucwallet-service:block-height";
    private static final String CONFIRM_TX_LIST  = "ucwallet-service:confirm-tx-list";
    private static final String PENDING_TX_ZLIST  = "ucwallet-service:addr-zlist:";
    @Value("${ucwallet.address.update-balance.time:5000}")
    long addressUpdateBalanceMillis;

    @Autowired
    private SUTService sutService;

    @Autowired
    RedisTemplate<String, AddressItem> addressItemRedisTemplate;

    @Autowired
    RedisTemplate<String, TransactionItem> transactionItemRedisTemplate;

    @Autowired
    RedisTemplate<String, TransactionPos> transactionPosRedisTemplate;

    @Autowired
    RedisTemplate<String, String> stringItemRedisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Value("${ucwallet-service.mq.exchange-req}")
    private String exechageReq;
    @Value("${ucwallet-service.mq.exchange-resp}")
    private String exechageResp;
    @Value("${ucwallet-service.mq.binding-key}")
    private String bindingKey;
    @Value("${ucwallet-service.mq.sendrawtx-resp}")
    private String sendRawTxRespTopic;
    @Value("${ucwallet-service.mq.sendrawtx-confirm}")
    private String sendRawTxRespConfirmTopic;
    @Value("${ucwallet-service.mq.sendrawtx-dbl-confirm}")
    private String sendRawTxRespDblConfirmTopic;

    @Value("${ucwallet-service.reset.max:10}")
    private int maxRetryCount;

    @Value("${ucwallet-service.check.period:3}")
    int blockCheckPeriodSec;
    @Value("${ucwallet-service.check.confirm-blocks:20}")
    int blockConfirmBlocks;

    private TransactionChecker transactionChecker;

    @PostConstruct
    private void init() throws IOException {
        String currentHeight = stringItemRedisTemplate.opsForValue().get(CURRENT_HEIGHT);
        if (currentHeight == null){
            // clear all other data, we need to re-initialized
            stringItemRedisTemplate.delete(ADDRESS_SET);
            stringItemRedisTemplate.delete(PENDING_TX);
            stringItemRedisTemplate.delete(CONFIRM_TX);
            // reset current height
            currentHeight = sutService.getBlockHeight().toString();
            stringItemRedisTemplate.opsForValue().set(CURRENT_HEIGHT, currentHeight);
        }

        transactionChecker = new TransactionChecker(blockCheckPeriodSec, Integer.valueOf(currentHeight),
                blockConfirmBlocks, sutService, this);
        Thread threadTransactionChecker = new Thread(transactionChecker);
        threadTransactionChecker.start();
    }

    public void submitRawTransaction(SendRawTransactionRequest request, String routingKey) throws Exception {
        // decode raw tx
        RawTransaction tx;
        tx = TransactionDecoder.decode(request.getRawTransaction());
        if (tx instanceof SignedRawTransaction) {
            try {
                String from = ((SignedRawTransaction) tx).getFrom().toLowerCase();
                BigInteger nonce = tx.getNonce();
                BigDecimal gasFee = SUTService.fromWei(tx.getGasLimit().multiply(tx.getGasPrice()));
                BigDecimal value = SUTService.fromWei(tx.getValue());

                // get address object from redis cache
                BoundHashOperations addressHashOp = addressItemRedisTemplate.boundHashOps(ADDRESS_SET);
                AddressItem addressItem = (AddressItem)addressHashOp.get(from);
                boolean needUpdateAddressInfo = false;
                long currentMillis = System.currentTimeMillis();
                if (addressItem == null ||
                        currentMillis - addressItem.getUpdateTimestamp() > addressUpdateBalanceMillis){
                    // update address balance
                    BigDecimal balance = sutService.getBalance(from);
                    if (addressItem == null){
                        addressItem = new AddressItem();
                    }
                    addressItem.setBalance(balance);
                    // update minimum nonce value
                    if (addressItem.getMinNonce() == null){
                        addressItem.setMinNonce(sutService.getTransactionCount(from));
                    }
                    // update update time
                    addressItem.setUpdateTimestamp(currentMillis);

                    // set update flag to update redis
                    needUpdateAddressInfo = true;
                }

                // update maximum once value
                if (addressItem.getMaxNonce() == null){
                    addressItem.setMaxNonce(nonce);
                    needUpdateAddressInfo = true;
                }else if(addressItem.getMaxNonce().compareTo(nonce) < 0) {
                    addressItem.setMaxNonce(nonce);
                    needUpdateAddressInfo = true;
                }

                // check balance
                if (addressItem.getBalance().subtract(gasFee).subtract(value).signum() < 0){
                    // No more sUT
                    MQErrorMessage rv = new MQErrorMessage(request.getReqId(), NO_ENOUGH_SUT,
                            "No enough sUT.");
                    logger.info("Request transaction no enough sUT:{}", request.getReqId());
                    amqpTemplate.convertAndSend(exechageResp, routingKey,
                            JSON.toJSONString(rv));
                    return;
                }

                // check nonce value
                if (nonce.compareTo(addressItem.getMinNonce()) < 0
                        || nonce.compareTo(addressItem.getMaxNonce()) > 0){
                    // Invalid nonce value
                    MQErrorMessage rv = new MQErrorMessage(request.getReqId(), INVALID_NONCE_VALUE,
                            "Invalid nonce value");
                    logger.info("Request transaction have an invalid nonce value:{}", request.getReqId());
                    amqpTemplate.convertAndSend(exechageResp, routingKey,
                            JSON.toJSONString(rv));
                    return;
                }

                // submit transaction to sUT network
                TransactionItem txItem = new TransactionItem(request.getReqId(), routingKey,
                        from, request.getRawTransaction(), nonce, gasFee,
                        0, getCurrentBlockHeight()); // using current block height
                String txHash = sutService.sendRawTransaction(request.getRawTransaction());
                transactionItemRedisTemplate.boundHashOps(PENDING_TX).put(txHash, txItem);
                stringItemRedisTemplate.boundZSetOps(PENDING_TX_ZLIST + from).add(
                        txHash, nonce.doubleValue());
                if (needUpdateAddressInfo){
                    addressHashOp.put(from, addressItem);
                }
                logger.trace("Send raw transaction:{}, txItem:{}", txHash, txItem);
                // back a message to queue
                amqpTemplate.convertAndSend(exechageResp, routingKey,
                        JSON.toJSONString(new SendRawTransactionResponse(request.getReqId(), txHash)));
            } catch (Exception e) {
                logger.warn("Cannot get sender address from raw tx:{}, exception:{}", request.getRawTransaction(), e.getMessage());
                throw e;
            }
        }
    }

    public TransactionItem getPendingTransaction(String txHash) {
        BoundHashOperations pendingTxOp = transactionItemRedisTemplate.boundHashOps(PENDING_TX);
        return (TransactionItem) pendingTxOp.get(txHash);
    }

    public TransactionItem getConfirmedTransaction(String txHash) {
        BoundHashOperations pendingTxOp = transactionItemRedisTemplate.boundHashOps(CONFIRM_TX);
        return (TransactionItem) pendingTxOp.get(txHash);
    }

    public void confirmTransaction(String txHash, TransactionItem txItem, boolean status){
        logger.trace("Confirm transaction:{}, status:{}, txItem:{}", txHash, status, txItem);
        // 1. send response to queue
        SendRawTransactionConfirm transactionConfirm = new SendRawTransactionConfirm(txItem.getReqId(), txHash, status);
        amqpTemplate.convertAndSend(exechageResp, txItem.getRoutingKey(), JSON.toJSONString(transactionConfirm));
        // 2. move tx from pending tx buffer to confirm queue
        addressItemRedisTemplate.boundHashOps(CONFIRM_TX).put(txHash, txItem);
        addressItemRedisTemplate.boundHashOps(PENDING_TX).delete(txHash);
        transactionPosRedisTemplate.boundListOps(CONFIRM_TX_LIST).rightPush(
                new TransactionPos(txHash, txItem.getBlockHeight()));
        // remove pending transaction hash
        Long pos = stringItemRedisTemplate.boundZSetOps(PENDING_TX_ZLIST + txItem.getFrom()).remove(txHash);
        if (pos != null || pos == 1) {
            logger.trace("remove from pending tx:{} zlist successfully.", txHash);
        }else{
            logger.warn("Transaction cannot found in address {} first position:{}, block:{}",
                    txItem.getFrom(), txHash, txItem.getBlockHeight());
        }
    }

    public void doubleConfirmTransaction(String txHash, int blockConfirmBlocks){
        logger.trace("Double confirm transaction:{}, confirms:{}", txHash, blockConfirmBlocks);
        // 1. send response to queue
        TransactionItem txItem = this.getConfirmedTransaction(txHash);
        SendRawTransactionDblConfirm dblConfirm = new SendRawTransactionDblConfirm(
                txItem.getReqId(), txHash, blockConfirmBlocks);
        amqpTemplate.convertAndSend(exechageResp, txItem.getRoutingKey(), JSON.toJSONString(dblConfirm));
        // 2. remove from confirm cache
        addressItemRedisTemplate.boundHashOps(CONFIRM_TX).delete(txHash);
        stringItemRedisTemplate.boundListOps(CONFIRM_TX_LIST).leftPop(); // Remove first item
    }

    public TransactionPos getFirstConfirmTxHeight(){
        return transactionPosRedisTemplate.boundListOps(CONFIRM_TX_LIST).index(0);
    }

    public int updateCurrentBlockHeight(int blockHeight){
        stringItemRedisTemplate.opsForValue().set(CURRENT_HEIGHT, String.valueOf(blockHeight));
        return getCurrentBlockHeight();
    }

    public Set<String> getPendingAddresses(){
        Set<String> addressKeys = stringItemRedisTemplate.keys(PENDING_TX_ZLIST + "*");
        return addressKeys.stream()
                .map(key -> key.substring(PENDING_TX_ZLIST.length()))
                .collect(Collectors.toSet());
    }

    public int getCurrentBlockHeight(){
        return Integer.valueOf(stringItemRedisTemplate.opsForValue().get(CURRENT_HEIGHT));
    }

    public void resendTransaction(String txHash, TransactionItem txItem, int newestHeight) throws Exception {
        if (txItem == null) {
            return;
        }
        // recheck balance and nonce value
        BoundHashOperations addressHashOp = addressItemRedisTemplate.boundHashOps(ADDRESS_SET);
        AddressItem addressItem = (AddressItem)addressHashOp.get(txItem.getFrom());
        BigDecimal balance = sutService.getBalance(txItem.getFrom());
        if (balance.subtract(txItem.getGasFee()).signum() < 0){
            // No more sUT
            logger.warn("Address current balance {} have no money:{}", balance.toString(),
                    txItem.getGasFee().toString());
            MQErrorMessage rv = new MQErrorMessage(txItem.getReqId(), NO_ENOUGH_SUT,
                    "No enough sUT.");
            amqpTemplate.convertAndSend(exechageResp, txItem.getRoutingKey(),
                    JSON.toJSONString(rv));
            return;
        }

        BigInteger nonce = sutService.getTransactionCount(txItem.getFrom());
        if (txItem.getNonce().compareTo(addressItem.getMinNonce()) < 0
                || txItem.getNonce().compareTo(addressItem.getMaxNonce()) > 0){
            // Invalid nonce value
            MQErrorMessage rv = new MQErrorMessage(txItem.getReqId(), INVALID_NONCE_VALUE,
                    "Invalid nonce value");
            amqpTemplate.convertAndSend(exechageResp, txItem.getRoutingKey(),
                    JSON.toJSONString(rv));
            return;
        }

        if (txItem.getRetryCnt() > maxRetryCount){
            MQErrorMessage rv = new MQErrorMessage(txItem.getReqId(), INVALID_NONCE_VALUE,
                    "Max retry count has exceeded.");
            amqpTemplate.convertAndSend(exechageResp, txItem.getRoutingKey(),
                    JSON.toJSONString(rv));

            // remove pending transaction hash
            Long pos = stringItemRedisTemplate.boundZSetOps(PENDING_TX_ZLIST + txItem.getFrom()).remove(txHash);
            if (pos != null || pos == 1) {
                logger.trace("remove from pending tx:{} zlist successfully.", txHash);
            }else{
                logger.warn("Transaction cannot found in address {} first position:{}, block:{}",
                        txItem.getFrom(), txHash, txItem.getBlockHeight());
            }
            return;
        }

        // update address item
        addressItem.setBalance(balance);
        addressItem.setMinNonce(nonce);
        addressHashOp.put(txItem.getFrom(), addressItem);
        // update transaction item
        txItem.setBlockHeight(newestHeight);
        txItem.incRetryCnt();
        transactionItemRedisTemplate.boundHashOps(CONFIRM_TX).put(txHash, txItem);

        String newHash = sutService.sendRawTransaction(txItem.getRawTransaction());
        logger.trace("Re-Send raw transaction:{}, from:{}, nonce:{}", txHash, txItem.getFrom(), txItem.getNonce());
        if (!newHash.equalsIgnoreCase(txHash)){
            logger.warn("Transaction hash not equal for same raw transaction.");
        }
    }

    public String getFirstPendingTxHash(String address) {
        Set<String> txHashSet = stringItemRedisTemplate.boundZSetOps(PENDING_TX_ZLIST + address).range(0, 1);
        if (txHashSet != null && txHashSet.size() > 0){
            return (String) txHashSet.toArray()[0];
        }
        return null;
    }
}

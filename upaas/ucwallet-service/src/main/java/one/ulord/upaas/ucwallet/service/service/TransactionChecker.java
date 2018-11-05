package one.ulord.upaas.ucwallet.service.service;

import one.ulord.upaas.ucwallet.service.base.common.TransactionItem;
import one.ulord.upaas.ucwallet.service.base.common.TransactionPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Transaction checker for transaction double confirm
 * @since 2018.10
 * @author yinhaibo(Ulord Dev Team)
 */
public class TransactionChecker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TransactionChecker.class);

    private long checkPeriodMillisecond;
    private boolean isRunning = true;
    private int currentHeight;
    private int confirmBlocks;

    private SUTService sutService;
    private TransactionTracker transactionTracker;

    public TransactionChecker(long checkPeriodMillisecond, int startHeight, int confirmBlocks,
                              SUTService sutService,
                              TransactionTracker transactionTracker){
        this.checkPeriodMillisecond = checkPeriodMillisecond;
        this.currentHeight = startHeight;
        this.confirmBlocks = confirmBlocks;
        this.sutService = sutService;
        this.transactionTracker = transactionTracker;
    }

    public void stop(){
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning){
            long currentMillisec = System.currentTimeMillis();
            try {
                // check current block height
                int newestHeight = sutService.getBlockHeight().intValue();
                if (newestHeight > currentHeight) {
                    logger.trace("Block height has changed:{}", newestHeight);
                    // A. block has update, we need to check all block for pending transaction
                    for (int i = currentHeight + 1; i <= newestHeight; i++) {
                        final int height = i;
                        List<String> hashList = sutService.getBlockTransaction(height);
                        // check every hash...
                        hashList.forEach(txHash -> {
                            // 1. get tx from pending cache
                            TransactionItem txItem = transactionTracker.getPendingTransaction(txHash);
                            if (txItem != null) {
                                // 2. get receipt, and back to caller
                                TransactionReceipt txReceipt = null;
                                try {
                                    txReceipt = sutService.getTransactionReceipt(txHash);
                                    if (txReceipt != null) {
                                        // back message to message queue
                                        txItem.setBlockHeight(height);
                                        transactionTracker.confirmTransaction(txHash, txItem, txReceipt.isStatusOK());
                                    }
                                } catch (IOException e) {
                                    logger.error("Get transaction {} receipt failed.", txHash, e);
                                }
                            }
                        });
                    }

                    // B. get tx from confirm cache
                    do {
                        TransactionPos txPos = transactionTracker.getFirstConfirmTxHeight();
                        if (txPos != null) {
                            // check block confirms
                            int currentConfirmBlocks = newestHeight - txPos.getBlockHeight();
                            if (currentConfirmBlocks >= confirmBlocks) {
                                // back message to message queue for double confirm
                                transactionTracker.doubleConfirmTransaction(txPos.getTxHash(), currentConfirmBlocks);

                                // continue to check next confirm transaction
                            }else{
                                break;
                            }
                        }else {
                            break;
                        }
                    }while(true);

                    // C. get tx from pending cache
                    Set<String> pendingAddress = transactionTracker.getPendingAddresses();
                    if (pendingAddress != null && pendingAddress.size() > 0){
                        pendingAddress.forEach(address -> {
                            String txHash = transactionTracker.getFirstPendingTxHash(address);
                            logger.trace("scan address :{} pending transaction：{}.", address, txHash);
                            if (txHash != null) {
                                TransactionItem txItem = transactionTracker.getPendingTransaction(txHash);
                                logger.trace("transaction item:{}", txItem);
                                if (txItem != null) {
                                    int currentConfirmBlocks = newestHeight - txItem.getBlockHeight();
                                    if (currentConfirmBlocks >= confirmBlocks) {
                                        try {
                                            // the transaction cannot be package, we need to resend
                                            transactionTracker.resendTransaction(txHash, txItem, newestHeight);
                                        }catch (Exception e){
                                            logger.warn("Re-send transaction error:", e);
                                        }
                                    }
                                }
                            }
                        });
                    }

                    // update currentHeight
                    currentHeight = transactionTracker.updateCurrentBlockHeight(newestHeight);
                }
            }catch (Exception e){
                logger.warn("Error in check transaction:", e);
            }

            // wait for next time to check
            long elapseSeconds = System.currentTimeMillis() - currentMillisec;
            if (elapseSeconds < checkPeriodMillisecond){
                try {
                    Thread.sleep(checkPeriodMillisecond - elapseSeconds);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}

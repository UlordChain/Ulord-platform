/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.contract;


import one.ulord.upaas.ucwallet.service.base.contract.generates.BridgeContract;
import one.ulord.upaas.ucwallet.service.base.contract.generates.ERC20Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * Common contract which can query balance and token balance, query transaction status,
 * and execute raw transaction which has signed.
 * @author haibo
 * @since 7/5/18
 */
public class CommonContract {
    final Logger logger = LoggerFactory.getLogger(CommonContract.class);
    private static final BigDecimal SUT_DECIMAILS = BigDecimal.TEN.pow(18);

    MyRawTransactionManager transactionManager;
    ContractGasProvider contractGasProvider;

    private Web3j web3j;

    private HashMap<String, ERC20Token> mapContract = new HashMap<>();
    private HashMap<String, Integer> mapTokenDecimals = new HashMap<>();

    private BridgeContract bridgeContract = null;

    /**
     * Build a new content smart contract instance. It will take more that 10 seconds to build connection
     * and contract instance.
     * @param ulordProvider Ulord side provider, such as http://xxxx:yyy, which is a RPC endpoint
     * @throws IOException
     * @throws CipherException
     */
    public CommonContract(String ulordProvider) throws IOException {
        this.web3j = Web3j.build(new HttpService(ulordProvider));
        Web3ClientVersion web3ClientVersion = null;
        web3ClientVersion = web3j.web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        if (clientVersion == null){
            throw new IOException("Ulord provider cannot connect.");
        }else{
            logger.info("web3j version:{}", clientVersion);
        }

        // we just create a fake credentials address
        transactionManager = new MyRawTransactionManager(web3j);
    }


    /**
     * Get ulord side chain balance(sUT) for a specified address
     * @return balance, Unit sUT, 1:1 UT
     * @throws IOException
     */
    public BigDecimal getBalance(String address) throws IOException {
        BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        BigDecimal bigDecimal = new BigDecimal(balance).divide(SUT_DECIMAILS);
        return bigDecimal;
    }


    /**
     * Get token balance
     * @param contractAddress Token contract address which is compatible ERC20 standard
     * @param userAddress A user address
     * @return Balance, unit is wei
     * @throws Exception
     */
    public BigDecimal getTokenBalance(String contractAddress, String userAddress) throws Exception {
        String lcAddress = contractAddress.toLowerCase();
        ERC20Token erc20Token = getErc20Token(lcAddress);
        BigInteger value = erc20Token.balanceOf(userAddress).send();
        Integer decimals = mapTokenDecimals.get(lcAddress);
        if (decimals == null){
            BigInteger rv = erc20Token.decimals().send();
            if (rv == null){
                decimals = 18;
            }else{
                decimals = rv.intValue();
            }
            mapTokenDecimals.put(contractAddress, decimals);
        }

        BigDecimal balance = new BigDecimal(value).divide(new BigDecimal(decimals));

        return balance;
    }

    /**
     * Get erc20 token
     * @param address contract address
     * @return
     * @throws Exception
     */
    private ERC20Token getErc20Token(String address) throws Exception {
        ERC20Token erc20Token = mapContract.get(address);
        if (erc20Token == null){
            erc20Token = ERC20Token.load(address, web3j, transactionManager, contractGasProvider);
            if (erc20Token == null){
                throw new Exception("Cannot load token address:" + address);
            }
            mapContract.put(address, erc20Token);
        }
        return erc20Token;
    }


    /**
     * Query transaction info
     * @param txhash
     * @return a transaction object, if return null, the transaction has drop by chain, if return an object and the
     * @throws IOException
     */
    public Transaction queryTransaction(String txhash) throws IOException {
        return web3j.ethGetTransactionByHash(txhash).send().getResult();
    }
     /**
     * Return a raw transaction hash. [Sync]
     * @param txhash transaction hash
     * @return hash
     */
    public TransactionReceipt queryTransactionReceipt(String txhash) throws IOException {
        return web3j.ethGetTransactionReceipt(txhash).send().getResult();
    }

    /**
     * Send raw transaction
     *
     * @param rawTransaction raw transaction data stream, which has signed.
     * @return hash
     */
    public String sendRawTransaction(String rawTransaction) throws Exception {
        logger.debug("send raw transaction:{}", rawTransaction);
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(rawTransaction).sendAsync().get();
        String transactionHash = ethSendTransaction.getTransactionHash();
        return transactionHash;

    }

    /**
     * query transaction count
     *
     * @param address
     * @return hash
     */
    public BigInteger getTransactionCount(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        return nonce;
    }

    /**
     * query current block height
     * @return
     */
    public BigInteger getBlockHeight() throws IOException {
        return web3j.ethBlockNumber().send().getBlockNumber();
    }

    /**
     * Get block's all transaction result
     * @return
     * @throws IOException
     */
    public List<EthBlock.TransactionResult> getBlockTransaction(BigInteger blockHeight) throws IOException {
        return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockHeight), false)
                .send().getBlock().getTransactions();
    }

    /**
     * Get UT Fed Address from Bridge contract
     * @param birderContractAddress
     * @return Fed Address
     * @throws Exception
     */
    public String getUTFedAddress(String birderContractAddress) throws Exception {
        if (bridgeContract == null){
            bridgeContract = BridgeContract.load(birderContractAddress, web3j, transactionManager, contractGasProvider);
        }
        if (bridgeContract == null){
            throw new Exception("Cannot load token address:" + birderContractAddress);
        }
        return bridgeContract.getFederationAddress().sendAsync().get();
    }
}

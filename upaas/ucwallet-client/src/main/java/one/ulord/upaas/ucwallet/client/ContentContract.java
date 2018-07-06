/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.client;

import one.ulord.upaas.ucwallet.client.contract.generates.CenterPublish;
import one.ulord.upaas.ucwallet.client.contract.generates.DBControl;
import one.ulord.upaas.ucwallet.client.contract.generates.USHToken;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.*;

/**
 * Content contract
 * @author haibo
 * @since 7/5/18
 */
public class ContentContract {
    /**
     * Block max gas limit
     */
    public static BigInteger BLOCK_GAS_LIMIT = BigInteger.valueOf(6700000);
    /**
     * Min gas price
     */
    public static BigInteger GAS_PRICE = BigInteger.valueOf(200000000); //0.2GWei

    private String tokenAddress;
    private String adminAddress;
    private String publishAddress;
    private String keystoreFile;
    private String mainAddress;

    private USHToken ushToken;
    private DBControl dbControl;
    private CenterPublish centerPublish;

    private TransactionActionHandler handler;

    private Web3j web3j;


    /**
     * Build a new content smart contract instance. It will take more that 10 seconds to build connection
     * and contract instance.
     * @param ulordProvider Ulord side provider, such as http://xxxx:yyy, which is a RPC endpoint
     * @param tokenAddress token address which has deploy to ulord side chain
     * @param adminAddress a admin contract for token which has deploy to ulord side chain
     * @param publishAddress a publish smart contract which has deply to ulord side chain
     * @param keystoreFile user account keystore file, which include user account private key
     * @param keystorePassword user account keystore password
     * @param handler a {@line TransactionActionHandler} instance
     * @throws IOException
     * @throws CipherException
     */
    public ContentContract(String ulordProvider, String tokenAddress, String adminAddress, String publishAddress,
                           String keystoreFile, String keystorePassword, TransactionActionHandler handler)
            throws IOException, CipherException {
        this.tokenAddress = tokenAddress;
        this.adminAddress = adminAddress;
        this.publishAddress = publishAddress;

        this.keystoreFile = keystoreFile;
        this.handler = handler;

        this.web3j = Web3j.build(new HttpService(ulordProvider));
        Web3ClientVersion web3ClientVersion = null;
        web3ClientVersion = web3j.web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        if (clientVersion == null){
            throw new IOException("Ulord provider cannot connect.");
        }

        File file = new File(keystoreFile);
        if (!file.exists()){
            // try to get file from classpath
            String resourcePath = ContentContract.class.getClassLoader().getResource("").toString();
            int typeSplitePos = resourcePath.indexOf(":");
            if (typeSplitePos > 0){
                resourcePath = resourcePath.substring(typeSplitePos+1);
            }

            file = new File(resourcePath + keystoreFile);
            if (!file.exists()){
                throw new IOException("Cannot found keystore file.");
            }
        }
        Credentials credentials = WalletUtils.loadCredentials(keystorePassword, file);
        this.mainAddress = credentials.getAddress();

        // load contract object
        this.ushToken = USHToken.load(tokenAddress, web3j, credentials,
                DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
        this.dbControl = DBControl.load(adminAddress, web3j, credentials,
                DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
        this.centerPublish = CenterPublish.load(publishAddress, web3j, credentials,
                DefaultGasProvider.GAS_PRICE, ContentContract.BLOCK_GAS_LIMIT); // Using block max limit
    }


    /**
     * Get ulord side chain gas balance
     * @return gas balance (Unit SUT)
     * @throws IOException
     */
    public BigDecimal getGasBalance() throws IOException {
        BigInteger balance = web3j.ethGetBalance(this.mainAddress, DefaultBlockParameterName.LATEST).send().getBalance();
        return Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER);
    }

    /**
     * Get ulord side chain token balance
     * @return token balance (Unit UX)
     * @throws Exception
     */
    public BigDecimal getTokenBanalce() throws Exception {
        return getTokenBalance(this.mainAddress);
    }

    public BigDecimal getTokenBalance(String address) throws Exception {
        BigInteger value = ushToken.balanceOf(address).send();
        return new BigDecimal(value).divide(BigDecimal.valueOf(10).pow(18));
    }

    /**
     * Transfer amount of token to a specified address
     * @param reqId request id
     * @param toAddress a target address
     * @param quantity quantity
     */
    public void transferToken(final String reqId, String toAddress, BigInteger quantity){
        ushToken.transfer(toAddress, quantity).sendAsync().whenCompleteAsync((receipt, e) -> {
            if (e == null){
                processTransactionReceipt(reqId, receipt);
            }else{
                this.handler.fail(reqId, e.getMessage());
            }
        });
    }

    /**
     * Approve publish contract to use a quality tokens
     * @param reqId request id
     * @param quantity a quality
     */
    public void approveContractQuality(final String reqId, BigInteger quantity){
        CompletableFuture<TransactionReceipt> future = ushToken.approve(publishAddress, quantity).sendAsync();
        future.whenCompleteAsync((receipt, e)->{
           if (e == null){
               processTransactionReceipt(reqId, receipt);
           }else{
               this.handler.fail(reqId, e.getMessage());
           }
        });

    }

    /**
     * Publish a resource to ulord smart contract.
     * While
     * @param reqId request id
     * @param udfsHash UDFS hash, must get from UDFS {@link UDFSClient}
     * @param authorAddress author address
     * @param price price
     * @param deposit deposit
     */
    public void publishResource(final String reqId, String udfsHash,
                                String authorAddress, BigInteger price, BigInteger deposit){
        // Using RxJava to process sync
        centerPublish.createClaim(udfsHash, authorAddress,
                price, deposit, new BigInteger("1")).sendAsync().whenCompleteAsync((receipt, e)-> {
                    if (e == null){
                        processTransactionReceipt(reqId, receipt);
                    }else{
                        this.handler.fail(reqId, e.getMessage());
                    }
                });
    }

    /**
     * Transfer to multiple address using different quality from current address
     * @param reqId request id
     * @param address a set of target address
     * @param quality a set of quality need to transfer
     */
    public void transferTokens(final String reqId, List<String> address, List<BigInteger> quality){
        centerPublish.mulTransfer(address, quality).sendAsync().whenCompleteAsync((receipt, e)-> {
            if (e == null){
                processTransactionReceipt(reqId, receipt);
            }else{
                this.handler.fail(reqId, e.getMessage());
            }
        });
    }

    private void processTransactionReceipt(String reqId, TransactionReceipt transactionReceipt) {
        if (transactionReceipt.isStatusOK()) {
            if (this.handler != null){
                this.handler.success(reqId, transactionReceipt.getTransactionHash());
            }
        }else{
            if (this.handler != null){
                this.handler.fail(reqId,
                        "Unknown exception, the receipt has received:"
                                + transactionReceipt.getTransactionHash());
            }
        }
    }

}

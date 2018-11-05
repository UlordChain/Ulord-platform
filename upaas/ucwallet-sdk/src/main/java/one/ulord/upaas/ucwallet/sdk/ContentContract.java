/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk;

import one.ulord.upaas.ucwallet.sdk.contract.generates.CenterPublish;
import one.ulord.upaas.ucwallet.sdk.contract.generates.MulTransfer;
import one.ulord.upaas.ucwallet.sdk.contract.generates.UshareToken;
import one.ulord.upaas.ucwallet.sdk.utils.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static one.ulord.upaas.ucwallet.sdk.contract.generates.UshareToken.FUNC_TRANSFER;

/**
 * Content contract
 * Message use sender confirms asynchronous mode
 *
 * @author chenxin
 * @since 13/8/18
 */
public class ContentContract{

    final Logger logger = LoggerFactory.getLogger(ContentContract.class);

    /**
     * Block max gas limit
     */
    public static BigInteger BLOCK_GAS_LIMIT = BigInteger.valueOf(6700000);
    /**
     * Min gas price
     */
    public static BigInteger GAS_PRICE = BigInteger.valueOf(2000000000); //2GWei

    public static long TX_CONFIRM_TIME_MS = 80000;
    public static long TX_QUERY_LOOP_MS = 5000;

    private String tokenAddress;
    private String uxCandyAddress;
    private String publishAddress;
    private String keystoreFile;
    private String mainAddress;

    private UshareToken ushToken;
    private MulTransfer uxCandy;
    private CenterPublish centerPublish;
    private Transfer transfer;


    Credentials credentials;

    MyRawTransactionManager transactionManager;
    ContractGasProvider contractGasProvider;

    private Web3j web3j;


    /**
     * Build a new content smart contract instance. It will take more that 10 seconds to build connection
     * and contract instance.
     * @param tokenAddress token address which has deploy to ulord side chain
     * @param candyAddress a candy contract for Token which is for multi-pay function
     * @param publishAddress a publish smart contract which has deply to ulord side chain
     * @param keystoreFile user account keystore file, which include user account private key
     * @param keystorePassword user account keystore password
     * @throws IOException
     * @throws CipherException
     */
    public ContentContract(UCWalletRPCInterface rpcInterface, String tokenAddress, String candyAddress, String publishAddress,
                           String keystoreFile, String keystorePassword)
            throws IOException, CipherException {
        this.tokenAddress = tokenAddress;
        this.uxCandyAddress = candyAddress;
        this.publishAddress = publishAddress;

        this.keystoreFile = keystoreFile;


        this.web3j = Web3j.build(new HttpService());

        URL fileUrl = Loader.getResource(keystoreFile);
        File file = null;
        if (fileUrl == null){
            file = new File(this.keystoreFile);
            if (!file.exists()){
                throw new IOException("Cannot found keystore file.");
            }
        }else{
            try {
                file = new File(fileUrl.toURI());
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        }
        this.credentials = WalletUtils.loadCredentials(keystorePassword, file);
        this.mainAddress = credentials.getAddress();

        // we need using fast transaction manager
        transactionManager = new MyRawTransactionManager(rpcInterface, credentials);

        // we using default contract gas provider
        contractGasProvider = new StaticGasProvider(DefaultGasProvider.GAS_PRICE, ContentContract.BLOCK_GAS_LIMIT);

//        transfer = new Transfer(web3j, transactionManager);

        // load contract object
        this.ushToken = UshareToken.load(tokenAddress, web3j, transactionManager, contractGasProvider);
        this.uxCandy = MulTransfer.load(uxCandyAddress, web3j, transactionManager, contractGasProvider);
        this.centerPublish = CenterPublish.load(publishAddress, web3j, transactionManager, contractGasProvider); // Using block max limit

        logger.info("Load Content Contract Success.");
    }


    /**
     * Get ulord side chain gas balance
     * @return gas balance (Unit SUT)
     * @throws IOException
     */
    public BigInteger getGasBalance() throws IOException {
        BigInteger balance = web3j.ethGetBalance(this.mainAddress, DefaultBlockParameterName.LATEST).send().getBalance();
        return balance;
    }

    /**
     * Get ulord side chain gas balance for a specified address
     * @return Gas balance, Unit SUT
     * @throws IOException
     */
    public BigInteger getGasBalance(String address) throws IOException {
        BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        return balance;
    }

    /**
     * Get ulord side chain token balance
     * @return token balance (Unit UX)
     * @throws Exception
     */
    public BigInteger getTokenBalance() throws Exception {
        return getTokenBalance(this.mainAddress);
    }

    public BigInteger getTokenBalance(String address) throws Exception {
        BigInteger value = ushToken.balanceOf(address).send();
        return value;
    }

    /**
     * Get keystore address
     * @return
     */
    public String getMainAddress(){return this.mainAddress;}

    /**
     * Transfer amount of gas from main address to specified address
     * @param toAddress target address
     * @param value gas value
     * @return signed raw transaction string
     * @throws IOException IOException while send a RPC call
     */
    public String transferSut(String toAddress, BigInteger value) throws IOException {
        // transfer using fast transaction manager
        return transactionManager.signTransaction(GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
                toAddress, "", value);
    }

    /**
     * Transfer amount of token to a specified address
     * @param toAddress a target address
     * @param quantity quantity
     */
    public String transferToken(String toAddress, BigInteger quantity) throws IOException {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new Address(toAddress),
                        new Uint256(quantity)),
                Collections.<TypeReference<?>>emptyList());
        return transactionManager.signTransaction(
                contractGasProvider.getGasPrice(function.getName()),
                contractGasProvider.getGasLimit(function.getName()),
                tokenAddress,
                FunctionEncoder.encode(function), BigInteger.ZERO);

    }

    /**
     * Approve publish contract to use a quality tokens
     * @param quantity a quality
     */
    public String approveContractQuality(BigInteger quantity) throws IOException {
        final Function function = new Function(
                UshareToken.FUNC_APPROVE,
                Arrays.<Type>asList(new Address(publishAddress),
                        new Uint256(quantity)),
                Collections.<TypeReference<?>>emptyList());
        return transactionManager.signTransaction(
                contractGasProvider.getGasPrice(function.getName()),
                contractGasProvider.getGasLimit(function.getName()),
                ushToken.getContractAddress(),
                FunctionEncoder.encode(function), BigInteger.ZERO);
    }

    /**
     * Publish a resource to ulord smart contract, return immediately
     * @param udfsHash UDFS hash, must get from UDFS {@link UDFSClient}
     * @param authorAddress author address
     * @param price price
     * @param storage storage into contract?
     */
    public String publishResource(String udfsHash,
                                  String authorAddress, BigInteger price, Boolean storage) throws IOException {
        final Function function = new Function(
                CenterPublish.FUNC_CREATECLAIM,
                Arrays.<Type>asList(new Utf8String(udfsHash),
                        new Address(authorAddress),
                        new Uint256(price),
                        new Uint8(1),
                        new Bool(storage)),
                Collections.<TypeReference<?>>emptyList());

        return transactionManager.signTransaction(
                contractGasProvider.getGasPrice(function.getName()),
                contractGasProvider.getGasLimit(function.getName()),
                centerPublish.getContractAddress(),
                FunctionEncoder.encode(function), BigInteger.ZERO);
    }

    /**
     * Transfer to multiple address using different quality from current address
     * @param address a set of target address
     * @param quality a set of quality need to transfer
     */
    public String transferTokens(List<String> address, List<BigInteger> quality) throws IOException {
        if (address == null || quality == null || address.size() == 0 || address.size() != quality.size()){
            throw new RuntimeException("Invalid parameters, master equal.");
        }
        if (address.size() > 200){
            logger.warn("Submit address amount more than 200, the transaction maybe out of gas.");
        }
        final Function function = new Function(
                MulTransfer.FUNC_MULPAYDIFF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<>(
                                org.web3j.abi.Utils.typeMap(address, Address.class)),
                        new org.web3j.abi.datatypes.DynamicArray<>(
                                org.web3j.abi.Utils.typeMap(quality, org.web3j.abi.datatypes.generated.Uint256.class))),
                Collections.<TypeReference<?>>emptyList());

        return transactionManager.signTransaction(
                contractGasProvider.getGasPrice(function.getName()),
                contractGasProvider.getGasLimit(function.getName()),
                uxCandy.getContractAddress(),
                FunctionEncoder.encode(function), BigInteger.ZERO);
    }

    /**
     * Transfer to multiple address using same quality from current address
     * @param address a set of target address
     * @param quality a set of quality need to transfer
     */
    public String transferTokens(BigInteger quality, List<String> address) throws IOException {
        if (address == null || quality == null || address.size() == 0){
            throw new RuntimeException("Invalid parameters, master equal.");
        }
        if (address.size() > 200){
            logger.warn("Submit address amount more than 200, the transaction maybe out of gas.");
        }
        final Function function = new Function(
                MulTransfer.FUNC_MULPAYSAME,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(quality),
                        new org.web3j.abi.datatypes.DynamicArray<>(
                                org.web3j.abi.Utils.typeMap(address, Address.class))),
                Collections.<TypeReference<?>>emptyList());

        return transactionManager.signTransaction(
                contractGasProvider.getGasPrice(function.getName()),
                contractGasProvider.getGasLimit(function.getName()),
                uxCandy.getContractAddress(),
                FunctionEncoder.encode(function), BigInteger.ZERO);
    }

    /**
     * Return a raw transaction hash. [Sync]
     * @param txhash transaction hash
     * @return hash
     */
    public TransactionReceipt queryTransactionReceipt(String txhash) throws IOException {
        return web3j.ethGetTransactionReceipt(txhash).send().getResult();
    }

}

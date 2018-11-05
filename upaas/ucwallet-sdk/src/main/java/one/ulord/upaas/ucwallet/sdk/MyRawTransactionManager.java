package one.ulord.upaas.ucwallet.sdk;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.ChainId;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author yinhaibo
 * @since 2018/9/17
 */
public class MyRawTransactionManager extends TransactionManager{
    private UCWalletRPCInterface rpcInterface;
    final Credentials credentials;
    private volatile BigInteger nonce = BigInteger.valueOf(-1);

    private final byte chainId;

    public MyRawTransactionManager(UCWalletRPCInterface rpcInterface, Credentials credentials) {
        super((Web3j) null, credentials.getAddress());
        this.rpcInterface = rpcInterface;
        this.credentials = credentials;
        this.chainId = ChainId.NONE;
    }

    public MyRawTransactionManager(
            UCWalletRPCInterface rpcInterface, Credentials credentials, byte chainId) {
        super((Web3j) null, credentials.getAddress());
        this.credentials = credentials;
        this.chainId = chainId;
    }

    public String signTransaction(BigInteger gasPrice, BigInteger gasLimit, String to,
                                  String data, BigInteger value) throws IOException {
        BigInteger nonce = getNonce();

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                data);

        byte[] signedMessage;

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        return Numeric.toHexString(signedMessage);
    }


    protected BigInteger getChainNonce() throws IOException {

        return rpcInterface.getTransactionCount(credentials.getAddress());
    }

    public synchronized BigInteger getNonce() throws IOException {
        if (nonce.signum() == -1) {
            // obtain lock
            nonce = getChainNonce();
        } else {
            nonce = nonce.add(BigInteger.ONE);
        }
        return nonce;
    }

    public BigInteger getCurrentNonce() {
        return nonce;
    }

    public synchronized void resetNonce() throws IOException {
        nonce = getChainNonce();
    }

    public synchronized void setNonce(BigInteger value) {
        nonce = value;
    }

    @Override
    public EthSendTransaction sendTransaction(BigInteger bigInteger, BigInteger bigInteger1, String s, String s1, BigInteger bigInteger2) throws IOException {
        throw new RuntimeException("Cannot send transaction using current transaction manager.");
    }
}

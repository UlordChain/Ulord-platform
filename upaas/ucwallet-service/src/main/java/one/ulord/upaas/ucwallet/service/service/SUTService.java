package one.ulord.upaas.ucwallet.service.service;

import one.ulord.upaas.ucwallet.service.base.contract.ContractHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class SUTService {
    private static final BigDecimal SUT_DECIMAILS = BigDecimal.TEN.pow(18);
    private static final Logger logger = LoggerFactory.getLogger(SUTService.class);

    @Autowired(required=true)
    private ContractHelper contractHelper;

    public BigDecimal getBalance(String address) throws IOException {
        return contractHelper.getContentContract().getBalance(address);
    }

    public BigDecimal getTokenBalance(String contractAddress, String address) throws Exception {
        return contractHelper.getContentContract().getTokenBalance(contractAddress, address);
    }

    public BigInteger getTransactionCount(String address) throws Exception {
        return contractHelper.getContentContract().getTransactionCount(address);
    }

    public String sendRawTransaction(String hexValue) throws Exception {

        return contractHelper.getContentContract().sendRawTransaction(hexValue);
    }

    public Transaction getTransaction(String txhash) throws IOException {
        return contractHelper.getContentContract().queryTransaction(txhash);
    }

    public TransactionReceipt getTransactionReceipt(String txhash) throws IOException {
        return contractHelper.getContentContract().queryTransactionReceipt(txhash);
    }

    public static BigDecimal fromWei(BigDecimal value){
        return value.divide(SUT_DECIMAILS);
    }

    public static BigDecimal fromWei(BigInteger value){
        return new BigDecimal(value).divide(SUT_DECIMAILS);
    }

    public BigInteger getBlockHeight() throws IOException {
        return contractHelper.getContentContract().getBlockHeight();
    }

    public List<String> getBlockTransaction(long blockHeight) throws IOException {
        List<String> lstTransaction = new ArrayList<>();
        List<EthBlock.TransactionResult> transactionResults = contractHelper.getContentContract()
                .getBlockTransaction(BigInteger.valueOf(blockHeight));
        transactionResults.forEach(transactionResult -> {
            lstTransaction.add(transactionResult.get().toString());
        });

        return lstTransaction;
    }

    public String queryUTFedAddress() throws Exception {
        return contractHelper.getFedAddress();
    }
}

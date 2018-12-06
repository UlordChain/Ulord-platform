package one.ulord.upaas.ucwallet.service.base.contract.generates;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.util.Arrays;

public class BridgeContract  extends Contract {
    public static final String FUNC_GETFEDADDRESS = "getFederationAddress";

    protected BridgeContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
        super(null, contractAddress, web3j, transactionManager, gasProvider);
    }

    public RemoteCall<String> getFederationAddress() {
        final Function function = new Function(FUNC_GETFEDADDRESS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static BridgeContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                  ContractGasProvider gasProvider) {
        return new BridgeContract(contractAddress, web3j, transactionManager, gasProvider);
    }
}

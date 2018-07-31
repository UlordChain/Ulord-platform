package one.ulord.upaas.ucwallet.client.contract.generates;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class TeamDevTokens extends Contract {
    private static final String BINARY = "60806040526301e133806005556b019d971e4fe8401e7400000060065561003f60056006546101d564010000000002610d25179091906401000000009004565b60075534801561004e57600080fd5b50604051604080610fbc8339810180604052810190808051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060008273ffffffffffffffffffffffffffffffffffffffff161415801561013a575060008173ffffffffffffffffffffffffffffffffffffffff1614155b151561014557600080fd5b81600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600960006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055504260048190555050506101f0565b60008082848115156101e357fe5b0490508091505092915050565b610dbd806101ff6000396000f3006080604052600436106100db576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680632ddbd13a146100e0578063557ed1ba1461010b5780635be7cc1614610136578063718b414214610179578063787e9137146101d057806378e97925146101fb57806379ba5097146102265780637ff3366f1461023d5780638da5cb5b14610294578063950da0c8146102eb578063d74afaa114610316578063dd05db9a1461036d578063ed10e33c14610398578063f2fde38b146103c7578063f851a4401461040a575b600080fd5b3480156100ec57600080fd5b506100f5610461565b6040518082815260200191505060405180910390f35b34801561011757600080fd5b50610120610467565b6040518082815260200191505060405180910390f35b34801561014257600080fd5b50610177600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061046f565b005b34801561018557600080fd5b5061018e61061e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156101dc57600080fd5b506101e5610644565b6040518082815260200191505060405180910390f35b34801561020757600080fd5b5061021061064a565b6040518082815260200191505060405180910390f35b34801561023257600080fd5b5061023b610650565b005b34801561024957600080fd5b506102526107ef565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156102a057600080fd5b506102a9610815565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156102f757600080fd5b5061030061083a565b6040518082815260200191505060405180910390f35b34801561032257600080fd5b5061032b6109ac565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561037957600080fd5b506103826109d2565b6040518082815260200191505060405180910390f35b3480156103a457600080fd5b506103ad6109d8565b604051808215151515815260200191505060405180910390f35b3480156103d357600080fd5b50610408600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610c0b565b005b34801561041657600080fd5b5061041f610ce6565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b60065481565b600042905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614806105175750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b151561052257600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415151561055e57600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a380600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60085481565b60045481565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156106ac57600080fd5b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a3600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600960009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600080600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166370a08231306040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b1580156108fd57600080fd5b505af1158015610911573d6000803e3d6000fd5b505050506040513d602081101561092757600080fd5b8101908080519060200190929190505050925061097e61096d60055461095f600454610951610467565b610d0c90919063ffffffff16565b610d2590919063ffffffff16565b600754610d4090919063ffffffff16565b915061099560085483610d0c90919063ffffffff16565b9050828111156109a3578290505b80935050505090565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60075481565b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610a3657600080fd5b610a3e61083a565b90506000811415610a4e57600080fd5b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb600960009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16836040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b158015610b3557600080fd5b505af1158015610b49573d6000803e3d6000fd5b505050506040513d6020811015610b5f57600080fd5b81019080805190602001909291905050501515610b7857fe5b600960009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f6352c5382c4a4578e712449ca65e83cdb392d045dfcf1cad9615189db2da244b826040518082815260200191505060405180910390a2610bfd81600854610d7390919063ffffffff16565b600881905550600191505090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610c6657600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614151515610ca257600080fd5b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000828211151515610d1a57fe5b818303905092915050565b6000808284811515610d3357fe5b0490508091505092915050565b60008082840290506000841480610d615750828482811515610d5e57fe5b04145b1515610d6957fe5b8091505092915050565b6000808284019050838110151515610d8757fe5b80915050929150505600a165627a7a72305820dc0f37dc0eea8f9528deea2a0666bfcf22e37dbf3cbb2042db08f726427cc8cd0029";

    public static final String FUNC_TOTAL = "total";

    public static final String FUNC_GETTIME = "getTime";

    public static final String FUNC_TRANSFERADMINSHIP = "transferAdminship";

    public static final String FUNC_UX = "UX";

    public static final String FUNC_COLLECTEDTOKENS = "collectedTokens";

    public static final String FUNC_STARTTIME = "startTime";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_TEAMADDRESS = "TeamAddress";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_CALCULATION = "calculation";

    public static final String FUNC_NEWOWNER = "newowner";

    public static final String FUNC_AMOUNTPERRELEASE = "amountPerRelease";

    public static final String FUNC_UNLOCK = "unLock";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_ADMIN = "admin";

    public static final Event TOKENSWITHDRAWN_EVENT = new Event("TokensWithdrawn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGTRANSFERADMIN_EVENT = new Event("LogTransferAdmin", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    protected TeamDevTokens(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TeamDevTokens(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<BigInteger> total() {
        final Function function = new Function(FUNC_TOTAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getTime() {
        final Function function = new Function(FUNC_GETTIME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _new) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP, 
                Arrays.<Type>asList(new Address(_new)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> UX() {
        final Function function = new Function(FUNC_UX,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> collectedTokens() {
        final Function function = new Function(FUNC_COLLECTEDTOKENS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> startTime() {
        final Function function = new Function(FUNC_STARTTIME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                FUNC_ACCEPTOWNERSHIP,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> TeamAddress() {
        final Function function = new Function(FUNC_TEAMADDRESS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> calculation() {
        final Function function = new Function(FUNC_CALCULATION,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> newowner() {
        final Function function = new Function(FUNC_NEWOWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> amountPerRelease() {
        final Function function = new Function(FUNC_AMOUNTPERRELEASE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> unLock() {
        final Function function = new Function(
                FUNC_UNLOCK,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _new) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP,
                Arrays.<Type>asList(new Address(_new)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> admin() {
        final Function function = new Function(FUNC_ADMIN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<TeamDevTokens> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _token, String _team) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_team)));
        return deployRemoteCall(TeamDevTokens.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<TeamDevTokens> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _token, String _team) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_team)));
        return deployRemoteCall(TeamDevTokens.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<TokensWithdrawnEventResponse> getTokensWithdrawnEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TOKENSWITHDRAWN_EVENT, transactionReceipt);
        ArrayList<TokensWithdrawnEventResponse> responses = new ArrayList<TokensWithdrawnEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TokensWithdrawnEventResponse typedResponse = new TokensWithdrawnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._holder = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TokensWithdrawnEventResponse> tokensWithdrawnEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TokensWithdrawnEventResponse>() {
            @Override
            public TokensWithdrawnEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TOKENSWITHDRAWN_EVENT, log);
                TokensWithdrawnEventResponse typedResponse = new TokensWithdrawnEventResponse();
                typedResponse.log = log;
                typedResponse._holder = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TokensWithdrawnEventResponse> tokensWithdrawnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TOKENSWITHDRAWN_EVENT));
        return tokensWithdrawnEventObservable(filter);
    }

    public List<LogTransferAdminEventResponse> getLogTransferAdminEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGTRANSFERADMIN_EVENT, transactionReceipt);
        ArrayList<LogTransferAdminEventResponse> responses = new ArrayList<LogTransferAdminEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogTransferAdminEventResponse typedResponse = new LogTransferAdminEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._old = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._new = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogTransferAdminEventResponse> logTransferAdminEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogTransferAdminEventResponse>() {
            @Override
            public LogTransferAdminEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGTRANSFERADMIN_EVENT, log);
                LogTransferAdminEventResponse typedResponse = new LogTransferAdminEventResponse();
                typedResponse.log = log;
                typedResponse._old = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._new = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogTransferAdminEventResponse> logTransferAdminEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGTRANSFERADMIN_EVENT));
        return logTransferAdminEventObservable(filter);
    }

    public static TeamDevTokens load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TeamDevTokens(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TeamDevTokens load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TeamDevTokens(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TokensWithdrawnEventResponse {
        public Log log;

        public String _holder;

        public BigInteger _amount;
    }

    public static class LogTransferAdminEventResponse {
        public Log log;

        public String _old;

        public String _new;
    }
}

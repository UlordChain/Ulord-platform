package one.ulord.upaas.ucwallet.client.contract.generates;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
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
import org.web3j.tx.gas.ContractGasProvider;
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
public class PoolToken extends Contract {
    private static final String BINARY = "6080604052600a6004556301e1338060055534801561001d57600080fd5b50604051606080610dc8833981018060405281019080805190602001909291908051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060008373ffffffffffffffffffffffffffffffffffffffff16141580156100d2575060008273ffffffffffffffffffffffffffffffffffffffff1614155b15156100dd57600080fd5b82600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600860006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555042600381905550806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505050610c10806101b86000396000f3006080604052600436106100db576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063557ed1ba146100e0578063715018a61461010b578063718b414214610122578063787e91371461017957806378e97925146101a457806379ba5097146101cf5780638da5cb5b146101e65780639ccc903d1461023d578063b85a8b2014610294578063c1586ad3146102bf578063d4ee1d90146102ee578063dfaf734a14610345578063ed10e33c14610370578063f27c3bf61461039f578063f2fde38b146103ca575b600080fd5b3480156100ec57600080fd5b506100f561040d565b6040518082815260200191505060405180910390f35b34801561011757600080fd5b50610120610415565b005b34801561012e57600080fd5b50610137610517565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561018557600080fd5b5061018e61053d565b6040518082815260200191505060405180910390f35b3480156101b057600080fd5b506101b9610543565b6040518082815260200191505060405180910390f35b3480156101db57600080fd5b506101e4610549565b005b3480156101f257600080fd5b506101fb6106e8565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561024957600080fd5b5061025261070d565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156102a057600080fd5b506102a9610733565b6040518082815260200191505060405180910390f35b3480156102cb57600080fd5b506102d4610739565b604051808215151515815260200191505060405180910390f35b3480156102fa57600080fd5b50610303610761565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561035157600080fd5b5061035a610787565b6040518082815260200191505060405180910390f35b34801561037c57600080fd5b5061038561078d565b604051808215151515815260200191505060405180910390f35b3480156103ab57600080fd5b506103b4610a80565b6040518082815260200191505060405180910390f35b3480156103d657600080fd5b5061040b600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610a86565b005b600042905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561047057600080fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f2dde1bc8b58ef192e7df3e7a354521d958331021e450d544346b936936916ef460405160405180910390a260008060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60075481565b60035481565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156105a557600080fd5b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167fdb6d05f3295cede580affa301a1eb5297528f3b3f6a56b075887ce6f61c45f2160405160405180910390a3600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60045481565b600060055461075a60065461074c61040d565b610b6190919063ffffffff16565b1015905090565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60065481565b60008060008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156107ed57600080fd5b6107f5610739565b151561080057600080fd5b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166370a08231306040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b1580156108bd57600080fd5b505af11580156108d1573d6000803e3d6000fd5b505050506040513d60208110156108e757600080fd5b81019080805190602001909291905050509150610922606461091460045485610b7a90919063ffffffff16565b610bb290919063ffffffff16565b9050600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16836040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b158015610a0b57600080fd5b505af1158015610a1f573d6000803e3d6000fd5b505050506040513d6020811015610a3557600080fd5b81019080805190602001909291905050501515610a4e57fe5b610a6381600754610bc890919063ffffffff16565b600781905550610a7161040d565b60068190555060019250505090565b60055481565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610ae157600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614151515610b1d57600080fd5b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000828211151515610b6f57fe5b818303905092915050565b600080831415610b8d5760009050610bac565b8183029050818382811515610b9e57fe5b04141515610ba857fe5b8090505b92915050565b60008183811515610bbf57fe5b04905092915050565b60008183019050828110151515610bdb57fe5b809050929150505600a165627a7a723058206c5499c83c9623fe51ddcdc302601b8f5f24c1cad878551fd1f4c9a82db814e00029";

    public static final String FUNC_GETTIME = "getTime";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_UX = "UX";

    public static final String FUNC_COLLECTEDTOKENS = "collectedTokens";

    public static final String FUNC_STARTTIME = "startTime";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_POOLADDRESS = "PoolAddress";

    public static final String FUNC_PERCENT = "PERCENT";

    public static final String FUNC_CANUNLOCK = "canUnlock";

    public static final String FUNC_NEWOWNER = "newOwner";

    public static final String FUNC_LASTRELEASETIME = "lastReleaseTime";

    public static final String FUNC_UNLOCK = "unLock";

    public static final String FUNC_ONEYEAR = "oneYear";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event LOGOWNERSHIPRENOUNCED_EVENT = new Event("LogOwnershipRenounced", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event LOGOWNERSHIPTRANSFERRED_EVENT = new Event("LogOwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    protected PoolToken(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                        ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, gasProvider);
    }

    public RemoteCall<BigInteger> getTime() {
        final Function function = new Function(FUNC_GETTIME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
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

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> PoolAddress() {
        final Function function = new Function(FUNC_POOLADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> PERCENT() {
        final Function function = new Function(FUNC_PERCENT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> canUnlock() {
        final Function function = new Function(FUNC_CANUNLOCK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> newOwner() {
        final Function function = new Function(FUNC_NEWOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> lastReleaseTime() {
        final Function function = new Function(FUNC_LASTRELEASETIME, 
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

    public RemoteCall<BigInteger> oneYear() {
        final Function function = new Function(FUNC_ONEYEAR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new Address(_newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<PoolToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _token, String _pool, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_pool),
                new Address(_owner)));
        return deployRemoteCall(PoolToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<PoolToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _token, String _pool, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_pool),
                new Address(_owner)));
        return deployRemoteCall(PoolToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<LogOwnershipRenouncedEventResponse> getLogOwnershipRenouncedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGOWNERSHIPRENOUNCED_EVENT, transactionReceipt);
        ArrayList<LogOwnershipRenouncedEventResponse> responses = new ArrayList<LogOwnershipRenouncedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogOwnershipRenouncedEventResponse typedResponse = new LogOwnershipRenouncedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogOwnershipRenouncedEventResponse> logOwnershipRenouncedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogOwnershipRenouncedEventResponse>() {
            @Override
            public LogOwnershipRenouncedEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGOWNERSHIPRENOUNCED_EVENT, log);
                LogOwnershipRenouncedEventResponse typedResponse = new LogOwnershipRenouncedEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogOwnershipRenouncedEventResponse> logOwnershipRenouncedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGOWNERSHIPRENOUNCED_EVENT));
        return logOwnershipRenouncedEventObservable(filter);
    }

    public List<LogOwnershipTransferredEventResponse> getLogOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGOWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<LogOwnershipTransferredEventResponse> responses = new ArrayList<LogOwnershipTransferredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogOwnershipTransferredEventResponse typedResponse = new LogOwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogOwnershipTransferredEventResponse> logOwnershipTransferredEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogOwnershipTransferredEventResponse>() {
            @Override
            public LogOwnershipTransferredEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGOWNERSHIPTRANSFERRED_EVENT, log);
                LogOwnershipTransferredEventResponse typedResponse = new LogOwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogOwnershipTransferredEventResponse> logOwnershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGOWNERSHIPTRANSFERRED_EVENT));
        return logOwnershipTransferredEventObservable(filter);
    }

    public static PoolToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                 ContractGasProvider gasProvider) {
        return new PoolToken(contractAddress, web3j, transactionManager, gasProvider);
    }

    public static class LogOwnershipRenouncedEventResponse {
        public Log log;

        public String previousOwner;
    }

    public static class LogOwnershipTransferredEventResponse {
        public Log log;

        public String previousOwner;

        public String newOwner;
    }
}

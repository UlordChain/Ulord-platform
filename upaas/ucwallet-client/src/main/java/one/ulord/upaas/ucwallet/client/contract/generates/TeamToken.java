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
public class TeamToken extends Contract {
    private static final String BINARY = "60806040526301e133806004556b019d971e4fe8401e7400000060055561003e600580546101de64010000000002610bc4179091906401000000009004565b60065534801561004d57600080fd5b50604051606080610e5d833981018060405281019080805190602001909291908051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060008373ffffffffffffffffffffffffffffffffffffffff1614158015610102575060008273ffffffffffffffffffffffffffffffffffffffff1614155b151561010d57600080fd5b82600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600860006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555042600381905550806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050506101f4565b600081838115156101eb57fe5b04905092915050565b610c5a806102036000396000f3006080604052600436106100d0576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680632ddbd13a146100d5578063557ed1ba14610100578063715018a61461012b578063718b414214610142578063787e91371461019957806378e97925146101c457806379ba5097146101ef5780637ff3366f146102065780638da5cb5b1461025d578063950da0c8146102b4578063d4ee1d90146102df578063dd05db9a14610336578063ed10e33c14610361578063f2fde38b14610390575b600080fd5b3480156100e157600080fd5b506100ea6103d3565b6040518082815260200191505060405180910390f35b34801561010c57600080fd5b506101156103d9565b6040518082815260200191505060405180910390f35b34801561013757600080fd5b506101406103e1565b005b34801561014e57600080fd5b506101576104e3565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156101a557600080fd5b506101ae610509565b6040518082815260200191505060405180910390f35b3480156101d057600080fd5b506101d961050f565b6040518082815260200191505060405180910390f35b3480156101fb57600080fd5b50610204610515565b005b34801561021257600080fd5b5061021b6106b4565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561026957600080fd5b506102726106da565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156102c057600080fd5b506102c96106ff565b6040518082815260200191505060405180910390f35b3480156102eb57600080fd5b506102f4610871565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561034257600080fd5b5061034b610897565b6040518082815260200191505060405180910390f35b34801561036d57600080fd5b5061037661089d565b604051808215151515815260200191505060405180910390f35b34801561039c57600080fd5b506103d1600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610ad0565b005b60055481565b600042905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561043c57600080fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f2dde1bc8b58ef192e7df3e7a354521d958331021e450d544346b936936916ef460405160405180910390a260008060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60075481565b60035481565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561057157600080fd5b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167fdb6d05f3295cede580affa301a1eb5297528f3b3f6a56b075887ce6f61c45f2160405160405180910390a3600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600080600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166370a08231306040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b1580156107c257600080fd5b505af11580156107d6573d6000803e3d6000fd5b505050506040513d60208110156107ec57600080fd5b810190808051906020019092919050505092506108436108326004546108246003546108166103d9565b610bab90919063ffffffff16565b610bc490919063ffffffff16565b600654610bda90919063ffffffff16565b915061085a60075483610bab90919063ffffffff16565b905082811115610868578290505b80935050505090565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60065481565b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156108fb57600080fd5b6109036106ff565b9050600081141561091357600080fd5b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16836040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1580156109fa57600080fd5b505af1158015610a0e573d6000803e3d6000fd5b505050506040513d6020811015610a2457600080fd5b81019080805190602001909291905050501515610a3d57fe5b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f6352c5382c4a4578e712449ca65e83cdb392d045dfcf1cad9615189db2da244b826040518082815260200191505060405180910390a2610ac281600754610c1290919063ffffffff16565b600781905550600191505090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610b2b57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614151515610b6757600080fd5b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000828211151515610bb957fe5b818303905092915050565b60008183811515610bd157fe5b04905092915050565b600080831415610bed5760009050610c0c565b8183029050818382811515610bfe57fe5b04141515610c0857fe5b8090505b92915050565b60008183019050828110151515610c2557fe5b809050929150505600a165627a7a72305820fc8c309a1fd97830734c9336399d3c5193c9fb0ac4b5f45675410d7b59cf4d430029";

    public static final String FUNC_TOTAL = "total";

    public static final String FUNC_GETTIME = "getTime";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_UX = "UX";

    public static final String FUNC_COLLECTEDTOKENS = "collectedTokens";

    public static final String FUNC_STARTTIME = "startTime";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_TEAMADDRESS = "TeamAddress";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_CALCULATION = "calculation";

    public static final String FUNC_NEWOWNER = "newOwner";

    public static final String FUNC_AMOUNTPERRELEASE = "amountPerRelease";

    public static final String FUNC_UNLOCK = "unLock";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event TOKENSWITHDRAWN_EVENT = new Event("TokensWithdrawn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGOWNERSHIPRENOUNCED_EVENT = new Event("LogOwnershipRenounced", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event LOGOWNERSHIPTRANSFERRED_EVENT = new Event("LogOwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    protected TeamToken(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                        ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, gasProvider);
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

    public RemoteCall<String> newOwner() {
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

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new Address(_newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<TeamToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _token, String _team, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_team),
                new Address(_owner)));
        return deployRemoteCall(TeamToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<TeamToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _token, String _team, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_team),
                new Address(_owner)));
        return deployRemoteCall(TeamToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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

    public static TeamToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                 ContractGasProvider gasProvider) {
        return new TeamToken(contractAddress, web3j, transactionManager, gasProvider);
    }


    public static class TokensWithdrawnEventResponse {
        public Log log;

        public String _holder;

        public BigInteger _amount;
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

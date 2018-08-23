package one.ulord.upaas.ucwallet.client.contract.generates;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
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
public class MulTransfer extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060405160408061189e8339810180604052810190808051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555033600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555050506116f6806101a86000396000f3006080604052600436106100db576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806335e60e89146100e05780635be7cc161461015e578063715018a6146101a157806379ba5097146101b85780637a80760e146101cf5780638da5cb5b14610226578063a8e1fba31461027d578063c2584958146102e4578063d4ee1d90146103a5578063e56a7c5d146103fc578063ec0525fe14610457578063eded105514610538578063eec7faa1146105c0578063f2fde38b146105eb578063f851a4401461062e575b600080fd5b3480156100ec57600080fd5b5061014460048036038101908080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050610685565b604051808215151515815260200191505060405180910390f35b34801561016a57600080fd5b5061019f600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506108d9565b005b3480156101ad57600080fd5b506101b6610a4c565b005b3480156101c457600080fd5b506101cd610b4e565b005b3480156101db57600080fd5b506101e4610ced565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561023257600080fd5b5061023b610d13565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561028957600080fd5b506102ca600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803515159060200190929190505050610d38565b604051808215151515815260200191505060405180910390f35b3480156102f057600080fd5b5061038b6004803603810190808035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050610f71565b604051808215151515815260200191505060405180910390f35b3480156103b157600080fd5b506103ba611138565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561040857600080fd5b5061043d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061115e565b604051808215151515815260200191505060405180910390f35b34801561046357600080fd5b5061051e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091929192908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929050505061117e565b604051808215151515815260200191505060405180910390f35b34801561054457600080fd5b506105a6600480360381019080803590602001909291908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929050505061132a565b604051808215151515815260200191505060405180910390f35b3480156105cc57600080fd5b506105d56114ca565b6040518082815260200191505060405180910390f35b3480156105f757600080fd5b5061062c600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506115c9565b005b34801561063a57600080fd5b506106436116a4565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141580156107335750600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15610787577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600f6040518082601e81111561076b57fe5b60ff16815260200191505060405180910390a1600091506108d3565b600090505b82518110156108ce57600073ffffffffffffffffffffffffffffffffffffffff1683828151811015156107bb57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff16141515156107e857600080fd5b60016003600085848151811015156107fc57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550828181518110151561086557fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff167f9b3b880c67f4fab4215412179771cffd326a045e6016a7e363eb38460f7e91766001604051808215151515815260200191505060405180910390a2808060010191505061078c565b600191505b50919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614806109815750600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b151561098c57600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167ff73aa4459d935dbacd9313fffe485016147551d99ef401461704cd39e6e9028760405160405180910390a380600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610aa757600080fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f2dde1bc8b58ef192e7df3e7a354521d958331021e450d544346b936936916ef460405160405180910390a260008060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610baa57600080fd5b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167fdb6d05f3295cede580affa301a1eb5297528f3b3f6a56b075887ce6f61c45f2160405160405180910390a3600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614158015610de55750600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15610e39577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600f6040518082601e811115610e1d57fe5b60ff16815260200191505060405180910390a160009050610f6b565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415610ebd577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360026040518082601e811115610ea157fe5b60ff16815260200191505060405180910390a160009050610f6b565b81600360008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508273ffffffffffffffffffffffffffffffffffffffff167f9b3b880c67f4fab4215412179771cffd326a045e6016a7e363eb38460f7e917683604051808215151515815260200191505060405180910390a2600190505b92915050565b60008060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610fd357600080fd5b82518451141515610fe357600080fd5b600090505b835181101561112d57600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb858381518110151561103d57fe5b90602001906020020151858481518110151561105557fe5b906020019060200201516040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1580156110e457600080fd5b505af11580156110f8573d6000803e3d6000fd5b505050506040513d602081101561110e57600080fd5b8101908080519060200190929190505050508080600101915050610fe8565b600191505092915050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60036020528060005260406000206000915054906101000a900460ff1681565b600080600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151415156111e257600080fd5b835185511415156111f257600080fd5b859150600090505b845181101561131d578173ffffffffffffffffffffffffffffffffffffffff1663a9059cbb868381518110151561122d57fe5b90602001906020020151868481518110151561124557fe5b906020019060200201516040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1580156112d457600080fd5b505af11580156112e8573d6000803e3d6000fd5b505050506040513d60208110156112fe57600080fd5b81019080805190602001909291905050505080806001019150506111fa565b6001925050509392505050565b60008060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151514151561138c57600080fd5b600090505b82518110156114bf57600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb84838151811015156113e657fe5b90602001906020020151866040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b15801561147657600080fd5b505af115801561148a573d6000803e3d6000fd5b505050506040513d60208110156114a057600080fd5b8101908080519060200190929190505050508080600101915050611391565b600191505092915050565b6000600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166370a08231306040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b15801561158957600080fd5b505af115801561159d573d6000803e3d6000fd5b505050506040513d60208110156115b357600080fd5b8101908080519060200190929190505050905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561162457600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415151561166057600080fd5b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16815600a165627a7a723058206c59906949a9edd164085c7ffaaca403f5da153fd5f8e495ba586adb6f1ad13f0029";

    public static final String FUNC_MULINSERTWHITE = "mulInsertWhite";

    public static final String FUNC_TRANSFERADMINSHIP = "transferAdminship";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_ERC20TOKEN = "ERC20Token";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_MANGEWHITELIST = "mangeWhiteList";

    public static final String FUNC_MULPAYDIFF = "mulPayDiff";

    public static final String FUNC_NEWOWNER = "newOwner";

    public static final String FUNC_WHITELIST_ = "whitelist_";

    public static final String FUNC_MULTOKENPAY = "mulTokenPay";

    public static final String FUNC_MULPAYSAME = "mulPaySame";

    public static final String FUNC_TOKENAMOUNT = "tokenAmount";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_ADMIN = "admin";

    public static final Event LOGWHILECHANGED_EVENT = new Event("LogWhileChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event LOGERROR_EVENT = new Event("LogError", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final Event LOGADMINSHIPTRANSFERRED_EVENT = new Event("LogAdminshipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event LOGOWNERSHIPRENOUNCED_EVENT = new Event("LogOwnershipRenounced", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event LOGOWNERSHIPTRANSFERRED_EVENT = new Event("LogOwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    protected MulTransfer(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                          ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, gasProvider);
    }


    public RemoteCall<TransactionReceipt> mulInsertWhite(List<String> _addresses) {
        final Function function = new Function(
                FUNC_MULINSERTWHITE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, Address.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _newAdmin) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP,
                Arrays.<Type>asList(new Address(_newAdmin)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                FUNC_ACCEPTOWNERSHIP,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> ERC20Token() {
        final Function function = new Function(FUNC_ERC20TOKEN,
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

    public RemoteCall<TransactionReceipt> mangeWhiteList(String _target, Boolean _allow) {
        final Function function = new Function(
                FUNC_MANGEWHITELIST,
                Arrays.<Type>asList(new Address(_target),
                new Bool(_allow)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mulPayDiff(List<String> _addresses, List<BigInteger> _value) {
        final Function function = new Function(
                FUNC_MULPAYDIFF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, Address.class)),
                new org.web3j.abi.datatypes.DynamicArray<Uint256>(
                        org.web3j.abi.Utils.typeMap(_value, Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> newOwner() {
        final Function function = new Function(FUNC_NEWOWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> whitelist_(String param0) {
        final Function function = new Function(FUNC_WHITELIST_,
                Arrays.<Type>asList(new Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> mulTokenPay(String _token, List<String> _addresses, List<BigInteger> _value) {
        final Function function = new Function(
                FUNC_MULTOKENPAY,
                Arrays.<Type>asList(new Address(_token),
                new org.web3j.abi.datatypes.DynamicArray<Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, Address.class)),
                new org.web3j.abi.datatypes.DynamicArray<Uint256>(
                        org.web3j.abi.Utils.typeMap(_value, Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mulPaySame(BigInteger _amount, List<String> _addresses) {
        final Function function = new Function(
                FUNC_MULPAYSAME,
                Arrays.<Type>asList(new Uint256(_amount),
                new org.web3j.abi.datatypes.DynamicArray<Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, Address.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> tokenAmount() {
        final Function function = new Function(FUNC_TOKENAMOUNT,
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

    public RemoteCall<String> admin() {
        final Function function = new Function(FUNC_ADMIN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<MulTransfer> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _token, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_owner)));
        return deployRemoteCall(MulTransfer.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<MulTransfer> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _token, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_token),
                new Address(_owner)));
        return deployRemoteCall(MulTransfer.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<LogWhileChangedEventResponse> getLogWhileChangedEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGWHILECHANGED_EVENT, transactionReceipt);
        ArrayList<LogWhileChangedEventResponse> responses = new ArrayList<LogWhileChangedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogWhileChangedEventResponse typedResponse = new LogWhileChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._target = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._allow = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogWhileChangedEventResponse> logWhileChangedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogWhileChangedEventResponse>() {
            @Override
            public LogWhileChangedEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGWHILECHANGED_EVENT, log);
                LogWhileChangedEventResponse typedResponse = new LogWhileChangedEventResponse();
                typedResponse.log = log;
                typedResponse._target = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._allow = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogWhileChangedEventResponse> logWhileChangedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGWHILECHANGED_EVENT));
        return logWhileChangedEventObservable(filter);
    }

    public List<LogErrorEventResponse> getLogErrorEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGERROR_EVENT, transactionReceipt);
        ArrayList<LogErrorEventResponse> responses = new ArrayList<LogErrorEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogErrorEventResponse typedResponse = new LogErrorEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._errorNumber = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogErrorEventResponse> logErrorEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogErrorEventResponse>() {
            @Override
            public LogErrorEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGERROR_EVENT, log);
                LogErrorEventResponse typedResponse = new LogErrorEventResponse();
                typedResponse.log = log;
                typedResponse._errorNumber = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogErrorEventResponse> logErrorEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGERROR_EVENT));
        return logErrorEventObservable(filter);
    }

    public List<LogAdminshipTransferredEventResponse> getLogAdminshipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGADMINSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<LogAdminshipTransferredEventResponse> responses = new ArrayList<LogAdminshipTransferredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogAdminshipTransferredEventResponse typedResponse = new LogAdminshipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._old = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._new = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogAdminshipTransferredEventResponse> logAdminshipTransferredEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogAdminshipTransferredEventResponse>() {
            @Override
            public LogAdminshipTransferredEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGADMINSHIPTRANSFERRED_EVENT, log);
                LogAdminshipTransferredEventResponse typedResponse = new LogAdminshipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse._old = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._new = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogAdminshipTransferredEventResponse> logAdminshipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGADMINSHIPTRANSFERRED_EVENT));
        return logAdminshipTransferredEventObservable(filter);
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

    public static MulTransfer load(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                   ContractGasProvider gasProvider) {
        return new MulTransfer(contractAddress, web3j, transactionManager, gasProvider);
    }

    public static class LogWhileChangedEventResponse {
        public Log log;

        public String _target;

        public Boolean _allow;
    }

    public static class LogErrorEventResponse {
        public Log log;

        public BigInteger _errorNumber;
    }

    public static class LogAdminshipTransferredEventResponse {
        public Log log;

        public String _old;

        public String _new;
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

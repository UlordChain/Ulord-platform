package one.ulord.upaas.ucwallet.client.contract.generates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class CenterPublish extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506040516040806117698339810180604052810190808051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050611618806101516000396000f3006080604052600436106100db576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806351c875d2146100e057806351e6fb5e146101375780635be7cc16146101f85780636c431bf91461023b57806379ba5097146102925780638da5cb5b146102a957806392d4347e146103005780639b3c2f92146103b5578063a7aeb5241461040c578063c545f677146104d8578063ca85a1b31461056f578063d74afaa114610650578063f1727954146106a7578063f2fde38b1461075e578063f851a440146107a1575b600080fd5b3480156100ec57600080fd5b506100f56107f8565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561014357600080fd5b506101de600480360381019080803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091929192908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929050505061081e565b604051808215151515815260200191505060405180910390f35b34801561020457600080fd5b50610239600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506109aa565b005b34801561024757600080fd5b50610250610b59565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561029e57600080fd5b506102a7610b7f565b005b3480156102b557600080fd5b506102be610d1e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561030c57600080fd5b50610391600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610d43565b6040518082600d8111156103a157fe5b60ff16815260200191505060405180910390f35b3480156103c157600080fd5b506103ca610ebb565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561041857600080fd5b506104b4600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919080359060200190929190803560ff169060200190929190505050610ee1565b6040518082600d8111156104c457fe5b60ff16815260200191505060405180910390f35b3480156104e457600080fd5b5061054b600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803515159060200190929190505050611071565b6040518082600d81111561055b57fe5b60ff16815260200191505060405180910390f35b34801561057b57600080fd5b506105d6600480360381019080803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506111b8565b604051808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200187815260200186815260200185815260200184151515158152602001831515151581526020018260ff1660ff16815260200197505050505050505060405180910390f35b34801561065c57600080fd5b50610665611349565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156106b357600080fd5b5061073a600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080351515906020019092919050505061136f565b6040518082600d81111561074a57fe5b60ff16815260200191505060405180910390f35b34801561076a57600080fd5b5061079f600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114eb565b005b3480156107ad57600080fd5b506107b66115c6565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600090505b835181101561099f57600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166323b872dd33868481518110151561087c57fe5b90602001906020020151868581518110151561089457fe5b906020019060200201516040518463ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018281526020019350505050602060405180830381600087803b15801561095657600080fd5b505af115801561096a573d6000803e3d6000fd5b505050506040513d602081101561098057600080fd5b8101908080519060200190929190505050508080600101915050610826565b600191505092915050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161480610a525750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b1515610a5d57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614151515610a9957600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a380600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610bdb57600080fd5b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a3600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166392d4347e8585856040518463ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180806020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001838152602001828103825285818151815260200191508051906020019080838360005b83811015610e29578082015181840152602081019050610e0e565b50505050905090810190601f168015610e565780820380516001836020036101000a031916815260200191505b50945050505050602060405180830381600087803b158015610e7757600080fd5b505af1158015610e8b573d6000803e3d6000fd5b505050506040513d6020811015610ea157600080fd5b810190808051906020019092919050505090509392505050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a7aeb52487878787876040518663ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180806020018673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018581526020018481526020018360ff1660ff168152602001828103825287818151815260200191508051906020019080838360005b83811015610fdb578082015181840152602081019050610fc0565b50505050905090810190601f1680156110085780820380516001836020036101000a031916815260200191505b509650505050505050602060405180830381600087803b15801561102b57600080fd5b505af115801561103f573d6000803e3d6000fd5b505050506040513d602081101561105557600080fd5b8101908080519060200190929190505050905095945050505050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663c545f67784846040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200183151515158152602001828103825284818151815260200191508051906020019080838360005b8381101561112857808201518184015260208101905061110d565b50505050905090810190601f1680156111555780820380516001836020036101000a031916815260200191505b509350505050602060405180830381600087803b15801561117557600080fd5b505af1158015611189573d6000803e3d6000fd5b505050506040513d602081101561119f57600080fd5b8101908080519060200190929190505050905092915050565b6000806000806000806000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663ca85a1b3896040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018080602001828103825283818151815260200191508051906020019080838360005b8381101561126d578082015181840152602081019050611252565b50505050905090810190601f16801561129a5780820380516001836020036101000a031916815260200191505b509250505060e060405180830381600087803b1580156112b957600080fd5b505af11580156112cd573d6000803e3d6000fd5b505050506040513d60e08110156112e357600080fd5b81019080805190602001909291908051906020019092919080519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509650965096509650965096509650919395979092949650565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f17279548585856040518463ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180806020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200183151515158152602001828103825285818151815260200191508051906020019080838360005b8381101561145957808201518184015260208101905061143e565b50505050905090810190601f1680156114865780820380516001836020036101000a031916815260200191505b50945050505050602060405180830381600087803b1580156114a757600080fd5b505af11580156114bb573d6000803e3d6000fd5b505050506040513d60208110156114d157600080fd5b810190808051906020019092919050505090509392505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561154657600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415151561158257600080fd5b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16815600a165627a7a72305820d1126b9b4296bf842c4c0a7acd075b377c4cece13ed1aa8549e1eb1ab027d4e30029";

    public static final String FUNC_DB = "DB";

    public static final String FUNC_MULTRANSFER = "mulTransfer";

    public static final String FUNC_TRANSFERADMINSHIP = "transferAdminship";

    public static final String FUNC_CLAIMPOOL = "ClaimPool";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_UPDATECLAIMPRICE = "updateClaimPrice";

    public static final String FUNC_USH = "USH";

    public static final String FUNC_CREATECLAIM = "createClaim";

    public static final String FUNC_UPDATECLAIMFORBIDDEN = "updateClaimForbidden";

    public static final String FUNC_SEARCHCLAIM = "SearchClaim";

    public static final String FUNC_NEWOWNER = "newowner";

    public static final String FUNC_UPDATECLAIMDROP = "updateClaimDrop";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_ADMIN = "admin";

    public static final Event LOGTRANSFERADMIN_EVENT = new Event("LogTransferAdmin", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event LOGERROR_EVENT = new Event("LogError", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    protected CenterPublish(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CenterPublish(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> DB() {
        final Function function = new Function(FUNC_DB, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> mulTransfer(List<String> _addressed, List<BigInteger> _val) {
        final Function function = new Function(
                FUNC_MULTRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_addressed, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(_val, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _new) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_new)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> ClaimPool() {
        final Function function = new Function(FUNC_CLAIMPOOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteCall<TransactionReceipt> updateClaimPrice(String _cid, String _author, BigInteger _newprice) {
        final Function function = new Function(
                FUNC_UPDATECLAIMPRICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cid), 
                new org.web3j.abi.datatypes.Address(_author), 
                new org.web3j.abi.datatypes.generated.Uint256(_newprice)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> USH() {
        final Function function = new Function(FUNC_USH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> createClaim(String _cid, String _author, BigInteger _price, BigInteger _deposit, BigInteger _type) {
        final Function function = new Function(
                FUNC_CREATECLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cid), 
                new org.web3j.abi.datatypes.Address(_author), 
                new org.web3j.abi.datatypes.generated.Uint256(_price), 
                new org.web3j.abi.datatypes.generated.Uint256(_deposit), 
                new org.web3j.abi.datatypes.generated.Uint8(_type)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateClaimForbidden(String _cid, Boolean _forbidden) {
        final Function function = new Function(
                FUNC_UPDATECLAIMFORBIDDEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cid), 
                new org.web3j.abi.datatypes.Bool(_forbidden)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple7<String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>> SearchClaim(String _cid) {
        final Function function = new Function(FUNC_SEARCHCLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cid)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple7<String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>>(
                new Callable<Tuple7<String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>>() {
                    @Override
                    public Tuple7<String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (Boolean) results.get(4).getValue(), 
                                (Boolean) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue());
                    }
                });
    }

    public RemoteCall<String> newowner() {
        final Function function = new Function(FUNC_NEWOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> updateClaimDrop(String _cid, String _author, Boolean _drop) {
        final Function function = new Function(
                FUNC_UPDATECLAIMDROP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cid), 
                new org.web3j.abi.datatypes.Address(_author), 
                new org.web3j.abi.datatypes.Bool(_drop)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _new) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_new)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> admin() {
        final Function function = new Function(FUNC_ADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<CenterPublish> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _ushare, String _db) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_ushare), 
                new org.web3j.abi.datatypes.Address(_db)));
        return deployRemoteCall(CenterPublish.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<CenterPublish> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _ushare, String _db) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_ushare), 
                new org.web3j.abi.datatypes.Address(_db)));
        return deployRemoteCall(CenterPublish.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<LogTransferAdminEventResponse> getLogTransferAdminEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGTRANSFERADMIN_EVENT, transactionReceipt);
        ArrayList<LogTransferAdminEventResponse> responses = new ArrayList<LogTransferAdminEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGTRANSFERADMIN_EVENT, log);
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

    public List<LogErrorEventResponse> getLogErrorEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGERROR_EVENT, transactionReceipt);
        ArrayList<LogErrorEventResponse> responses = new ArrayList<LogErrorEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGERROR_EVENT, log);
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

    public static CenterPublish load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CenterPublish(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static CenterPublish load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CenterPublish(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogTransferAdminEventResponse {
        public Log log;

        public String _old;

        public String _new;
    }

    public static class LogErrorEventResponse {
        public Log log;

        public BigInteger _errorNumber;
    }
}

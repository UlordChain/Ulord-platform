package one.ulord.upaas.ucwallet.service.base.contract.generates;

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
import org.web3j.tuples.generated.Tuple7;
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
import java.util.concurrent.Callable;

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
    private static final String BINARY = "608060405234801561001057600080fd5b50604051604080611f128339810180604052810190808051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555080600660006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050611dc1806101516000396000f3006080604052600436106100f1576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063173fff82146100f65780632cc576bb1461018257806351c875d2146102245780635be7cc161461027b5780636c431bf9146102be578063718b41421461031557806379ba50971461036c5780638da5cb5b146103835780639b19251a146103da578063a3f827af14610435578063a7aeb5241461054e578063a8e1fba31461061a578063d74afaa11461068b578063e41c2ad8146106e2578063f2fde38b14610770578063f851a440146107b3578063fa86de411461080a575b600080fd5b34801561010257600080fd5b5061015e60048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506108dc565b6040518082601881111561016e57fe5b60ff16815260200191505060405180910390f35b34801561018e57600080fd5b5061020060048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610abf565b6040518082601881111561021057fe5b60ff16815260200191505060405180910390f35b34801561023057600080fd5b50610239610cce565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561028757600080fd5b506102bc600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610cf4565b005b3480156102ca57600080fd5b506102d3610ea3565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561032157600080fd5b5061032a610ec9565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561037857600080fd5b50610381610eef565b005b34801561038f57600080fd5b5061039861108e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156103e657600080fd5b5061041b600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506110b3565b604051808215151515815260200191505060405180910390f35b34801561044157600080fd5b5061047360048036038101908080356fffffffffffffffffffffffffffffffff191690602001909291905050506110d3565b604051808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001878152602001868152602001858152602001841515151581526020018360ff1660ff168152602001828103825288818151815260200191508051906020019080838360005b8381101561050d5780820151818401526020810190506104f2565b50505050905090810190601f16801561053a5780820380516001836020036101000a031916815260200191505b509850505050505050505060405180910390f35b34801561055a57600080fd5b506105f6600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919080359060200190929190803560ff16906020019092919050505061127f565b6040518082601881111561060657fe5b60ff16815260200191505060405180910390f35b34801561062657600080fd5b50610667600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035151590602001909291905050506115fd565b6040518082601881111561067757fe5b60ff16815260200191505060405180910390f35b34801561069757600080fd5b506106a0611836565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156106ee57600080fd5b5061074c60048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080351515906020019092919050505061185c565b6040518082601881111561075c57fe5b60ff16815260200191505060405180910390f35b34801561077c57600080fd5b506107b1600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611a43565b005b3480156107bf57600080fd5b506107c8611b1e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561081657600080fd5b506108b860048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929080359060200190929190505050611b44565b604051808260188111156108c857fe5b60ff16815260200191505060405180910390f35b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610987577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360066040518082601881111561096b57fe5b60ff16815260200191505060405180910390a160069050610ab8565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663173fff828585856040518463ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180846fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018281526020019350505050602060405180830381600087803b158015610a7a57600080fd5b505af1158015610a8e573d6000803e3d6000fd5b505050506040513d6020811015610aa457600080fd5b810190808051906020019092919050505090505b9392505050565b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610b6a577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600660405180826018811115610b4e57fe5b60ff16815260200191505060405180910390a160069050610cc7565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16632cc576bb8585856040518463ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180846fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019350505050602060405180830381600087803b158015610c8957600080fd5b505af1158015610c9d573d6000803e3d6000fd5b505050506040513d6020811015610cb357600080fd5b810190808051906020019092919050505090505b9392505050565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161480610d9c5750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b1515610da757600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614151515610de357600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a380600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610f4b57600080fd5b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a3600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60036020528060005260406000206000915054906101000a900460ff1681565b600060606000806000806000600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a3f827af896040518263ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180826fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001915050600060405180830381600087803b15801561119657600080fd5b505af11580156111aa573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f8201168201806040525060e08110156111d457600080fd5b810190808051906020019092919080516401000000008111156111f657600080fd5b8281019050602081018481111561120c57600080fd5b815185600182028301116401000000008211171561122957600080fd5b505092919060200180519060200190929190805190602001909291908051906020019092919080519060200190929190805190602001909291905050509650965096509650965096509650919395979092949650565b60008060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151514151561132b577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360066040518082601881111561130f57fe5b60ff16815260200191505060405180910390a1600691506115f3565b86866040516020018083805190602001908083835b6020831015156113655780518252602082019150602081019050602083039250611340565b6001836020036101000a0380198251168184511680821785525050505050509050018273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166c01000000000000000000000000028152601401925050506040516020818303038152906040526040518082805190602001908083835b60208310151561141057805182526020820191506020810190506020830392506113eb565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405180910390209050600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16632fb8e3b68289898989896040518763ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180876fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001806020018673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018581526020018481526020018360ff1660ff168152602001828103825287818151815260200191508051906020019080838360005b83811015611564578082015181840152602081019050611549565b50505050905090810190601f1680156115915780820380516001836020036101000a031916815260200191505b50975050505050505050602060405180830381600087803b1580156115b557600080fd5b505af11580156115c9573d6000803e3d6000fd5b505050506040513d60208110156115df57600080fd5b810190808051906020019092919050505091505b5095945050505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141580156116aa5750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b156116fe577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc36003604051808260188111156116e257fe5b60ff16815260200191505060405180910390a160039050611830565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415611782577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600a6040518082601881111561176657fe5b60ff16815260200191505060405180910390a1600a9050611830565b81600360008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508273ffffffffffffffffffffffffffffffffffffffff167f034aa244844c4b4471cae4afc6b61354f390a8755d044fa93313ecfa6f9e78b983604051808215151515815260200191505060405180910390a2600090505b92915050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515611907577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc36006604051808260188111156118eb57fe5b60ff16815260200191505060405180910390a160069050611a3c565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663e41c2ad88585856040518463ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180846fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001821515151581526020019350505050602060405180830381600087803b1580156119fe57600080fd5b505af1158015611a12573d6000803e3d6000fd5b505050506040513d6020811015611a2857600080fd5b810190808051906020019092919050505090505b9392505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611a9e57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614151515611ada57600080fd5b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515611bef577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600660405180826018811115611bd357fe5b60ff16815260200191505060405180910390a160069050611d8d565b600660009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663fa86de41868686866040518563ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180856fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001838152602001828103825284818151815260200191508051906020019080838360005b83811015611d00578082015181840152602081019050611ce5565b50505050905090810190601f168015611d2d5780820380516001836020036101000a031916815260200191505b5095505050505050602060405180830381600087803b158015611d4f57600080fd5b505af1158015611d63573d6000803e3d6000fd5b505050506040513d6020811015611d7957600080fd5b810190808051906020019092919050505090505b9493505050505600a165627a7a72305820a8267ed7b872c3e3da026c554333805c461c4aa8ef9ab2085b3329f0559ac0bc0029";

    public static final String FUNC_UPDATECLAIMPRICE = "updateClaimPrice";

    public static final String FUNC_UPDATECLAIMAUTHOR = "updateClaimAuthor";

    public static final String FUNC_DB = "DB";

    public static final String FUNC_TRANSFERADMINSHIP = "transferAdminship";

    public static final String FUNC_CLAIMPOOL = "ClaimPool";

    public static final String FUNC_UX = "UX";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_WHITELIST = "whitelist";

    public static final String FUNC_SEARCHCLAIM = "SearchClaim";

    public static final String FUNC_CREATECLAIM = "createClaim";

    public static final String FUNC_MANGEWHITELIST = "mangeWhiteList";

    public static final String FUNC_NEWOWNER = "newowner";

    public static final String FUNC_UPDATECLAIMDROP = "updateClaimDrop";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_ADMIN = "admin";

    public static final String FUNC_UPDATECLAIM = "updateClaim";

    public static final Event LOGMANGEWHILE_EVENT = new Event("LogMangeWhile",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event LOGERROR_EVENT = new Event("LogError",
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final Event LOGTRANSFERADMIN_EVENT = new Event("LogTransferAdmin",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;


    protected CenterPublish(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                            ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, gasProvider);
    }

    public RemoteCall<TransactionReceipt> updateClaimPrice(byte[] _cid, String _author, BigInteger _newprice) {
        final Function function = new Function(
                FUNC_UPDATECLAIMPRICE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_cid),
                        new Address(_author),
                        new Uint256(_newprice)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateClaimAuthor(byte[] _cid, String _author, String _newAuthor) {
        final Function function = new Function(
                FUNC_UPDATECLAIMAUTHOR,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_cid),
                        new Address(_author),
                        new Address(_newAuthor)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> DB() {
        final Function function = new Function(FUNC_DB,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _new) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP,
                Arrays.<Type>asList(new Address(_new)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> ClaimPool() {
        final Function function = new Function(FUNC_CLAIMPOOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> UX() {
        final Function function = new Function(FUNC_UX,
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

    public RemoteCall<Boolean> whitelist(String param0) {
        final Function function = new Function(FUNC_WHITELIST,
                Arrays.<Type>asList(new Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Tuple7<String, String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger>> SearchClaim(byte[] _cid) {
        final Function function = new Function(FUNC_SEARCHCLAIM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_cid)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple7<String, String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger>>(
                new Callable<Tuple7<String, String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger>>() {
                    @Override
                    public Tuple7<String, String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<String, String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger>(
                                (String) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (BigInteger) results.get(3).getValue(),
                                (BigInteger) results.get(4).getValue(),
                                (Boolean) results.get(5).getValue(),
                                (BigInteger) results.get(6).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> createClaim(String _udfs, String _author, BigInteger _price, BigInteger _deposit, BigInteger _type) {
        final Function function = new Function(
                FUNC_CREATECLAIM,
                Arrays.<Type>asList(new Utf8String(_udfs),
                        new Address(_author),
                        new Uint256(_price),
                        new Uint256(_deposit),
                        new Uint8(_type)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mangeWhiteList(String _dest, Boolean _stauts) {
        final Function function = new Function(
                FUNC_MANGEWHITELIST,
                Arrays.<Type>asList(new Address(_dest),
                        new Bool(_stauts)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> newowner() {
        final Function function = new Function(FUNC_NEWOWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> updateClaimDrop(byte[] _cid, String _author, Boolean _drop) {
        final Function function = new Function(
                FUNC_UPDATECLAIMDROP,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_cid),
                        new Address(_author),
                        new Bool(_drop)),
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

    public RemoteCall<TransactionReceipt> updateClaim(byte[] _cid, String _author, String _newudfs, BigInteger _newprice) {
        final Function function = new Function(
                FUNC_UPDATECLAIM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes16(_cid),
                        new Address(_author),
                        new Utf8String(_newudfs),
                        new Uint256(_newprice)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<CenterPublish> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _ushare, String _db) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_ushare),
                new Address(_db)));
        return deployRemoteCall(CenterPublish.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<CenterPublish> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _ushare, String _db) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Address(_ushare),
                new Address(_db)));
        return deployRemoteCall(CenterPublish.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<LogMangeWhileEventResponse> getLogMangeWhileEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGMANGEWHILE_EVENT, transactionReceipt);
        ArrayList<LogMangeWhileEventResponse> responses = new ArrayList<LogMangeWhileEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogMangeWhileEventResponse typedResponse = new LogMangeWhileEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._dest = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._allow = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogMangeWhileEventResponse> logMangeWhileEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogMangeWhileEventResponse>() {
            @Override
            public LogMangeWhileEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGMANGEWHILE_EVENT, log);
                LogMangeWhileEventResponse typedResponse = new LogMangeWhileEventResponse();
                typedResponse.log = log;
                typedResponse._dest = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._allow = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogMangeWhileEventResponse> logMangeWhileEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGMANGEWHILE_EVENT));
        return logMangeWhileEventObservable(filter);
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


    public static CenterPublish load(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                     ContractGasProvider gasProvider) {
        return new CenterPublish(contractAddress, web3j, transactionManager, gasProvider);
    }

    public static class LogMangeWhileEventResponse {
        public Log log;

        public String _dest;

        public Boolean _allow;
    }

    public static class LogErrorEventResponse {
        public Log log;

        public BigInteger _errorNumber;
    }

    public static class LogTransferAdminEventResponse {
        public Log log;

        public String _old;

        public String _new;
    }
}

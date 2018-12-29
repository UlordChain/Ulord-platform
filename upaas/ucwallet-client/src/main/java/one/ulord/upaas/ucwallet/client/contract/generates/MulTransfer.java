package one.ulord.upaas.ucwallet.client.contract.generates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class MulTransfer extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50604051604080611d8a8339810180604052810190808051906020019092919080519060200190929190505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555033600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555081600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505050611be2806101a86000396000f3006080604052600436106100fc576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806335e60e891461010157806341c0e1b51461017f578063529f4284146101965780635be7cc1614610211578063715018a61461025457806379ba50971461026b5780637a80760e146102825780638da5cb5b146102d9578063a8e1fba314610330578063c258495814610397578063d4ee1d9014610458578063e56a7c5d146104af578063ec0525fe1461050a578063eded1055146105eb578063eec7faa114610673578063f2fde38b1461069e578063f851a440146106e1578063f9792db314610738575b600080fd5b34801561010d57600080fd5b50610165600480360381019080803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091929192905050506107ec565b604051808215151515815260200191505060405180910390f35b34801561018b57600080fd5b50610194610a40565b005b6101f76004803603810190808035906020019092919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050610ad5565b604051808215151515815260200191505060405180910390f35b34801561021d57600080fd5b50610252600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610c34565b005b34801561026057600080fd5b50610269610da7565b005b34801561027757600080fd5b50610280610ea9565b005b34801561028e57600080fd5b50610297611048565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156102e557600080fd5b506102ee61106e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561033c57600080fd5b5061037d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803515159060200190929190505050611093565b604051808215151515815260200191505060405180910390f35b3480156103a357600080fd5b5061043e60048036038101908080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091929192905050506112cc565b604051808215151515815260200191505060405180910390f35b34801561046457600080fd5b5061046d611493565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156104bb57600080fd5b506104f0600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114b9565b604051808215151515815260200191505060405180910390f35b34801561051657600080fd5b506105d1600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091929192905050506114d9565b604051808215151515815260200191505060405180910390f35b3480156105f757600080fd5b506106596004803603810190808035906020019092919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050611685565b604051808215151515815260200191505060405180910390f35b34801561067f57600080fd5b50610688611825565b6040518082815260200191505060405180910390f35b3480156106aa57600080fd5b506106df600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611924565b005b3480156106ed57600080fd5b506106f66119ff565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6107d26004803603810190808035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919291929080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509192919290505050611a25565b604051808215151515815260200191505060405180910390f35b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415801561089a5750600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b156108ee577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600f6040518082601d8111156108d257fe5b60ff16815260200191505060405180910390a160009150610a3a565b600090505b8251811015610a3557600073ffffffffffffffffffffffffffffffffffffffff16838281518110151561092257fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff161415151561094f57600080fd5b600160036000858481518110151561096357fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555082818151811015156109cc57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff167f9b3b880c67f4fab4215412179771cffd326a045e6016a7e363eb38460f7e91766001604051808215151515815260200191505060405180910390a280806001019150506108f3565b600191505b50919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610a9b57600080fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16ff5b600080600080600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610b3c57600080fd5b348787510211151515610b4b57fe5b60ff865111151515610b5957fe5b34935060009250600091505b85518260ff161015610bda578683019250858260ff16815181101515610b8757fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff166108fc889081150290604051600060405180830381858888f193505050501515610bcd57fe5b8180600101925050610b65565b82840390506000811115610c26573373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f193505050501515610c2557fe5b5b600194505050505092915050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161480610cdc5750600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b1515610ce757600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167ff73aa4459d935dbacd9313fffe485016147551d99ef401461704cd39e6e9028760405160405180910390a380600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610e0257600080fd5b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f2dde1bc8b58ef192e7df3e7a354521d958331021e450d544346b936936916ef460405160405180910390a260008060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610f0557600080fd5b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167fdb6d05f3295cede580affa301a1eb5297528f3b3f6a56b075887ce6f61c45f2160405160405180910390a3600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141580156111405750600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15611194577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600f6040518082601d81111561117857fe5b60ff16815260200191505060405180910390a1600090506112c6565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415611218577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360026040518082601d8111156111fc57fe5b60ff16815260200191505060405180910390a1600090506112c6565b81600360008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508273ffffffffffffffffffffffffffffffffffffffff167f9b3b880c67f4fab4215412179771cffd326a045e6016a7e363eb38460f7e917683604051808215151515815260200191505060405180910390a2600190505b92915050565b60008060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151514151561132e57600080fd5b8251845114151561133e57600080fd5b600090505b835181101561148857600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb858381518110151561139857fe5b9060200190602002015185848151811015156113b057fe5b906020019060200201516040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b15801561143f57600080fd5b505af1158015611453573d6000803e3d6000fd5b505050506040513d602081101561146957600080fd5b8101908080519060200190929190505050508080600101915050611343565b600191505092915050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60036020528060005260406000206000915054906101000a900460ff1681565b600080600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151514151561153d57600080fd5b8351855114151561154d57600080fd5b859150600090505b8451811015611678578173ffffffffffffffffffffffffffffffffffffffff1663a9059cbb868381518110151561158857fe5b9060200190602002015186848151811015156115a057fe5b906020019060200201516040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b15801561162f57600080fd5b505af1158015611643573d6000803e3d6000fd5b505050506040513d602081101561165957600080fd5b8101908080519060200190929190505050508080600101915050611555565b6001925050509392505050565b60008060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151415156116e757600080fd5b600090505b825181101561181a57600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663a9059cbb848381518110151561174157fe5b90602001906020020151866040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b1580156117d157600080fd5b505af11580156117e5573d6000803e3d6000fd5b505050506040513d60208110156117fb57600080fd5b81019080805190602001909291905050505080806001019150506116ec565b600191505092915050565b6000600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166370a08231306040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001915050602060405180830381600087803b1580156118e457600080fd5b505af11580156118f8573d6000803e3d6000fd5b505050506040513d602081101561190e57600080fd5b8101908080519060200190929190505050905090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561197f57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16141515156119bb57600080fd5b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600080600080600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515611a8c57600080fd5b86518651141515611a9957fe5b60ff865111151515611aa757fe5b34935060009250600091505b85518260ff161015611b5c57868260ff16815181101515611ad057fe5b9060200190602002015183019250858260ff16815181101515611aef57fe5b9060200190602002015173ffffffffffffffffffffffffffffffffffffffff166108fc888460ff16815181101515611b2357fe5b906020019060200201519081150290604051600060405180830381858888f193505050501515611b4f57fe5b8180600101925050611ab3565b82840390506000811115611ba8573373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f193505050501515611ba757fe5b5b6001945050505050929150505600a165627a7a72305820e66273449838a021fdd0d9a93fe577911724db86d0462c127cc541ef94eda8ba0029";

    public static final String FUNC_MULINSERTWHITE = "mulInsertWhite";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_SENDETHSAME = "sendEthSame";

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

    public static final String FUNC_SENDETHDIFF = "sendEthDiff";

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

    @Deprecated
    protected MulTransfer(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    protected MulTransfer(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MulTransfer(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> mulInsertWhite(List<String> _addresses) {
        final Function function = new Function(
                FUNC_MULINSERTWHITE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sendEthSame(BigInteger _amount, List<String> _addresses, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_SENDETHSAME,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_amount),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _newAdmin) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newAdmin)),
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_target),
                        new org.web3j.abi.datatypes.Bool(_allow)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mulPayDiff(List<String> _addresses, List<BigInteger> _value) {
        final Function function = new Function(
                FUNC_MULPAYDIFF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class)),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                                org.web3j.abi.Utils.typeMap(_value, org.web3j.abi.datatypes.generated.Uint256.class))),
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> mulTokenPay(String _token, List<String> _addresses, List<BigInteger> _value) {
        final Function function = new Function(
                FUNC_MULTOKENPAY,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_token),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class)),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                                org.web3j.abi.Utils.typeMap(_value, org.web3j.abi.datatypes.generated.Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mulPaySame(BigInteger _amount, List<String> _addresses) {
        final Function function = new Function(
                FUNC_MULPAYSAME,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_amount),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class))),
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
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> admin() {
        final Function function = new Function(FUNC_ADMIN,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> sendEthDiff(List<BigInteger> _amount, List<String> _addresses, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_SENDETHDIFF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                                org.web3j.abi.Utils.typeMap(_amount, org.web3j.abi.datatypes.generated.Uint256.class)),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                org.web3j.abi.Utils.typeMap(_addresses, org.web3j.abi.datatypes.Address.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    @Deprecated
    public static RemoteCall<MulTransfer> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _token, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_token),
                new org.web3j.abi.datatypes.Address(_owner)));
        return deployRemoteCall(MulTransfer.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<MulTransfer> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _token, String _owner) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_token),
                new org.web3j.abi.datatypes.Address(_owner)));
        return deployRemoteCall(MulTransfer.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public List<LogWhileChangedEventResponse> getLogWhileChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGWHILECHANGED_EVENT, transactionReceipt);
        ArrayList<LogWhileChangedEventResponse> responses = new ArrayList<LogWhileChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGWHILECHANGED_EVENT, log);
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

    public List<LogAdminshipTransferredEventResponse> getLogAdminshipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGADMINSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<LogAdminshipTransferredEventResponse> responses = new ArrayList<LogAdminshipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGADMINSHIPTRANSFERRED_EVENT, log);
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
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGOWNERSHIPRENOUNCED_EVENT, transactionReceipt);
        ArrayList<LogOwnershipRenouncedEventResponse> responses = new ArrayList<LogOwnershipRenouncedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGOWNERSHIPRENOUNCED_EVENT, log);
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
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGOWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<LogOwnershipTransferredEventResponse> responses = new ArrayList<LogOwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGOWNERSHIPTRANSFERRED_EVENT, log);
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

    @Deprecated
    public static MulTransfer load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MulTransfer(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MulTransfer load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MulTransfer(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MulTransfer load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MulTransfer(contractAddress, web3j, transactionManager, contractGasProvider);
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

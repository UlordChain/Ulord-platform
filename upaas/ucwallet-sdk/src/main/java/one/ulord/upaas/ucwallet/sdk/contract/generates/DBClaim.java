package one.ulord.upaas.ucwallet.sdk.contract.generates;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes16;
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
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
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
public class DBClaim extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550612db3806100f96000396000f3006080604052600436106100f1576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff168063173fff82146100f65780632cc576bb146101825780632fb8e3b6146102245780635be7cc161461030d5780635cdfd92e1461035057806379ba5097146103be5780638b8c84b7146103d55780638da5cb5b146104375780639b19251a1461048e578063a3f827af146104e9578063a8e1fba314610602578063af9773a014610673578063d74afaa114610797578063e41c2ad8146107ee578063f2fde38b1461087c578063f851a440146108bf578063fa86de4114610916575b600080fd5b34801561010257600080fd5b5061015e60048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291905050506109e8565b6040518082601881111561016e57fe5b60ff16815260200191505060405180910390f35b34801561018e57600080fd5b5061020060048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610ccf565b6040518082601881111561021057fe5b60ff16815260200191505060405180910390f35b34801561023057600080fd5b506102e960048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919080359060200190929190803560ff169060200190929190505050610ff0565b604051808260188111156102f957fe5b60ff16815260200191505060405180910390f35b34801561031957600080fd5b5061034e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611477565b005b34801561035c57600080fd5b5061039a60048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803515159060200190929190505050611626565b604051808260188111156103aa57fe5b60ff16815260200191505060405180910390f35b3480156103ca57600080fd5b506103d3611973565b005b3480156103e157600080fd5b5061041360048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190505050611b12565b6040518082601881111561042357fe5b60ff16815260200191505060405180910390f35b34801561044357600080fd5b5061044c611e21565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561049a57600080fd5b506104cf600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611e46565b604051808215151515815260200191505060405180910390f35b3480156104f557600080fd5b5061052760048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190505050611e66565b604051808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001878152602001868152602001858152602001841515151581526020018360ff1660ff168152602001828103825288818151815260200191508051906020019080838360005b838110156105c15780820151818401526020810190506105a6565b50505050905090810190601f1680156105ee5780820380516001836020036101000a031916815260200191505b509850505050505050505060405180910390f35b34801561060e57600080fd5b5061064f600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080351515906020019092919050505061225e565b6040518082601881111561065f57fe5b60ff16815260200191505060405180910390f35b34801561067f57600080fd5b506106b160048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190505050612497565b604051808973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018060200188815260200187815260200186815260200185151515158152602001841515151581526020018360ff1660ff168152602001828103825289818151815260200191508051906020019080838360005b8381101561075557808201518184015260208101905061073a565b50505050905090810190601f1680156107825780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390f35b3480156107a357600080fd5b506107ac6125be565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156107fa57600080fd5b5061085860048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035151590602001909291905050506125e4565b6040518082601881111561086857fe5b60ff16815260200191505060405180910390f35b34801561088857600080fd5b506108bd600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506128f3565b005b3480156108cb57600080fd5b506108d46129ce565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561092257600080fd5b506109c460048036038101908080356fffffffffffffffffffffffffffffffff19169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001909291905050506129f4565b604051808260188111156109d457fe5b60ff16815260200191505060405180910390f35b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610a93577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600460405180826018811115610a7757fe5b60ff16815260200191505060405180910390a160049050610cc8565b8273ffffffffffffffffffffffffffffffffffffffff1660046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141515610b73577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600560405180826018811115610b5757fe5b60ff16815260200191505060405180910390a160059050610cc8565b8160046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020600401541415610c06577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600e60405180826018811115610bea57fe5b60ff16815260200191505060405180910390a1600e9050610cc8565b8273ffffffffffffffffffffffffffffffffffffffff167f555de76a4a6471b084ffc3b9acaa9eeef455935369e85d02cc98edf67e2c6328858460405180836fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020018281526020019250505060405180910390a28160046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060040181905550600090505b9392505050565b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610d7a577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600460405180826018811115610d5e57fe5b60ff16815260200191505060405180910390a160049050610fe9565b8273ffffffffffffffffffffffffffffffffffffffff1660046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141515610e5a577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600560405180826018811115610e3e57fe5b60ff16815260200191505060405180910390a160059050610fe9565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415610ede577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600a60405180826018811115610ec257fe5b60ff16815260200191505060405180910390a1600a9050610fe9565b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167f8e06c2cfcef793685a2ccba7a42b25752d38d5e6ce9121224f4a3ecf80f596328660405180826fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200191505060405180910390a38160046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600090505b9392505050565b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151514151561109b577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360046040518082601881111561107f57fe5b60ff16815260200191505060405180910390a16004905061146d565b600060046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141515611166577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600c6040518082601881111561114a57fe5b60ff16815260200191505060405180910390a1600c905061146d565b8460046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508560046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020019081526020016000206001019080519060200190611231929190612c9a565b504260046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020600201819055508260046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020600301819055508360046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060040181905550600060046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160006101000a81548160ff021916908315150217905550600060046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160016101000a81548160ff0219169083151502179055508160046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160026101000a81548160ff021916908360ff1602179055508473ffffffffffffffffffffffffffffffffffffffff167f275e740d4f976e07c5fa36650f24fac7c75e7793994b188e35d3dbae717a2d588860405180826fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200191505060405180910390a2600090505b9695505050505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16148061151f5750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b151561152a57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415151561156657600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a380600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141580156116d35750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15611727577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360036040518082601881111561170b57fe5b60ff16815260200191505060405180910390a16003905061196d565b600073ffffffffffffffffffffffffffffffffffffffff1660046000856fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415611807577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600d604051808260188111156117eb57fe5b60ff16815260200191505060405180910390a1600d905061196d565b81151560046000856fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160019054906101000a900460ff16151514156118ab577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600e6040518082601881111561188f57fe5b60ff16815260200191505060405180910390a1600e905061196d565b7fa7403bf420bc21921afd2c34304326fbbea8ed6d859906a015241eab84efaddb838360405180836fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001821515151581526020019250505060405180910390a18160046000856fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160016101000a81548160ff021916908315150217905550600090505b92915050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156119cf57600080fd5b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a3600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614158015611bbf5750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15611c13577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600360405180826018811115611bf757fe5b60ff16815260200191505060405180910390a160039050611e1c565b600073ffffffffffffffffffffffffffffffffffffffff1660046000846fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415611cf3577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600d60405180826018811115611cd757fe5b60ff16815260200191505060405180910390a1600d9050611e1c565b60046000836fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020600080820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600182016000611d649190612d1a565b6002820160009055600382016000905560048201600090556005820160006101000a81549060ff02191690556005820160016101000a81549060ff02191690556005820160026101000a81549060ff021916905550507f4fb671e7288f880995b4d2a324a951acad9fd29c129f7161bd83c3ed329abd2d8260405180826fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200191505060405180910390a1600090505b919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60036020528060005260406000206000915054906101000a900460ff1681565b6000606060008060008060008073ffffffffffffffffffffffffffffffffffffffff16600460008a6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161480611f58575060001515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515145b15611fc457600080600080600160008595506040805190810160405280600481526020017f4e756c6c0000000000000000000000000000000000000000000000000000000081525094939291908494508393508292508090509650965096509650965096509650612253565b60046000896fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16600460008a6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020600101600460008b6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060020154600460008c6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060030154600460008d6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060040154600460008e6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160009054906101000a900460ff16600460008f6fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160029054906101000a900460ff16858054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561223d5780601f106122125761010080835404028352916020019161223d565b820191906000526020600020905b81548152906001019060200180831161222057829003601f168201915b5050505050955096509650965096509650965096505b919395979092949650565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415801561230b5750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b1561235f577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360036040518082601881111561234357fe5b60ff16815260200191505060405180910390a160039050612491565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614156123e3577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600a604051808260188111156123c757fe5b60ff16815260200191505060405180910390a1600a9050612491565b81600360008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508273ffffffffffffffffffffffffffffffffffffffff167f034aa244844c4b4471cae4afc6b61354f390a8755d044fa93313ecfa6f9e78b983604051808215151515815260200191505060405180910390a2600090505b92915050565b60046020528060005260406000206000915090508060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690806001018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156125695780601f1061253e57610100808354040283529160200191612569565b820191906000526020600020905b81548152906001019060200180831161254c57829003601f168201915b5050505050908060020154908060030154908060040154908060050160009054906101000a900460ff16908060050160019054906101000a900460ff16908060050160029054906101000a900460ff16905088565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151514151561268f577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360046040518082601881111561267357fe5b60ff16815260200191505060405180910390a1600490506128ec565b8273ffffffffffffffffffffffffffffffffffffffff1660046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614151561276f577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360056040518082601881111561275357fe5b60ff16815260200191505060405180910390a1600590506128ec565b81151560046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160009054906101000a900460ff1615151415612813577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600e604051808260188111156127f757fe5b60ff16815260200191505060405180910390a1600e90506128ec565b8273ffffffffffffffffffffffffffffffffffffffff167fb43ff3d060fd28ed60e44726ebb697ebcc32e9d02fb54ebcfb762c4959818f7e858460405180836fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001821515151581526020019250505060405180910390a28160046000866fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060050160006101000a81548160ff021916908315150217905550600090505b9392505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561294e57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415151561298a57600080fd5b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060011515600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515612a9f577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600460405180826018811115612a8357fe5b60ff16815260200191505060405180910390a160049050612c92565b8373ffffffffffffffffffffffffffffffffffffffff1660046000876fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff1916815260200190815260200160002060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141515612b7f577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600560405180826018811115612b6357fe5b60ff16815260200191505060405180910390a160059050612c92565b8260046000876fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020019081526020016000206001019080519060200190612bcf929190612c9a565b508160046000876fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff19168152602001908152602001600020600401819055508373ffffffffffffffffffffffffffffffffffffffff167f8fbda8d8c575a16d0a4260d8f5036924910260bded2cfb19b020237f3514b487868460405180836fffffffffffffffffffffffffffffffff19166fffffffffffffffffffffffffffffffff191681526020018281526020019250505060405180910390a2600090505b949350505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10612cdb57805160ff1916838001178555612d09565b82800160010185558215612d09579182015b82811115612d08578251825591602001919060010190612ced565b5b509050612d169190612d62565b5090565b50805460018160011615610100020316600290046000825580601f10612d405750612d5f565b601f016020900490600052602060002090810190612d5e9190612d62565b5b50565b612d8491905b80821115612d80576000816000905550600101612d68565b5090565b905600a165627a7a72305820437c23ac2b88d063461b6fdfdd7be9eabfb2c394a39ec2b1b8f528a1148558c00029";

    public static final String FUNC_UPDATECLAIMPRICE = "updateClaimPrice";

    public static final String FUNC_UPDATECLAIMAUTHOR = "updateClaimAuthor";

    public static final String FUNC_CREATECLAIM = "createClaim";

    public static final String FUNC_TRANSFERADMINSHIP = "transferAdminship";

    public static final String FUNC_UPDATECLAIMFORBIDDEN = "updateClaimForbidden";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_DELETECLAIM = "deleteClaim";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_WHITELIST = "whitelist";

    public static final String FUNC_SEARCHCLAIM = "SearchClaim";

    public static final String FUNC_MANGEWHITELIST = "mangeWhiteList";

    public static final String FUNC_STORE = "store";

    public static final String FUNC_NEWOWNER = "newowner";

    public static final String FUNC_UPDATECLAIMDROP = "updateClaimDrop";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_ADMIN = "admin";

    public static final String FUNC_UPDATECLAIM = "updateClaim";

    public static final Event LOGNEWCLAIM_EVENT = new Event("LogNewClaim", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}));
    ;

    public static final Event LOGUPDATECLAIMUDFS_EVENT = new Event("LogUpdateClaimUdfs", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGUPDATECLAIMAUTHOR_EVENT = new Event("LogUpdateClaimAuthor", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}));
    ;

    public static final Event LOGUPDATECLAIMPRICE_EVENT = new Event("LogUpdateClaimPrice", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGUPDATECLAIMDROP_EVENT = new Event("LogUpdateClaimDrop", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event LOGUPDATECLAIMFORBIDDEN_EVENT = new Event("LogUpdateClaimForbidden", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event LOGDELETECLAIM_EVENT = new Event("LogDeleteCLaim", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes16>() {}));
    ;

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

    protected DBClaim(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DBClaim(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> updateClaimPrice(byte[] _cid, String _author, BigInteger _newprice) {
        final Function function = new Function(
                FUNC_UPDATECLAIMPRICE, 
                Arrays.<Type>asList(new Bytes16(_cid),
                new Address(_author),
                new Uint256(_newprice)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateClaimAuthor(byte[] _cid, String _author, String _newAuthor) {
        final Function function = new Function(
                FUNC_UPDATECLAIMAUTHOR,
                Arrays.<Type>asList(new Bytes16(_cid),
                new Address(_author),
                new Address(_newAuthor)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> createClaim(byte[] _cid, String _udfs, String _author, BigInteger _price, BigInteger _deposit, BigInteger _type) {
        final Function function = new Function(
                FUNC_CREATECLAIM,
                Arrays.<Type>asList(new Bytes16(_cid),
                new Utf8String(_udfs),
                new Address(_author),
                new Uint256(_price),
                new Uint256(_deposit),
                new Uint8(_type)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _new) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP,
                Arrays.<Type>asList(new Address(_new)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateClaimForbidden(byte[] _cid, Boolean _forbidden) {
        final Function function = new Function(
                FUNC_UPDATECLAIMFORBIDDEN,
                Arrays.<Type>asList(new Bytes16(_cid),
                new Bool(_forbidden)),
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

    public RemoteCall<TransactionReceipt> deleteClaim(byte[] _cid) {
        final Function function = new Function(
                FUNC_DELETECLAIM,
                Arrays.<Type>asList(new Bytes16(_cid)),
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

    public RemoteCall<Tuple7<String, String, BigInteger, BigInteger, BigInteger, Boolean, BigInteger>> SearchClaim(byte[] _claimId) {
        final Function function = new Function(FUNC_SEARCHCLAIM,
                Arrays.<Type>asList(new Bytes16(_claimId)),
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

    public RemoteCall<TransactionReceipt> mangeWhiteList(String _dest, Boolean _stauts) {
        final Function function = new Function(
                FUNC_MANGEWHITELIST,
                Arrays.<Type>asList(new Address(_dest),
                new Bool(_stauts)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple8<String, String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>> store(byte[] param0) {
        final Function function = new Function(FUNC_STORE,
                Arrays.<Type>asList(new Bytes16(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint8>() {}));
        return new RemoteCall<Tuple8<String, String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>>(
                new Callable<Tuple8<String, String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>>() {
                    @Override
                    public Tuple8<String, String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, String, BigInteger, BigInteger, BigInteger, Boolean, Boolean, BigInteger>(
                                (String) results.get(0).getValue(),
                                (String) results.get(1).getValue(),
                                (BigInteger) results.get(2).getValue(),
                                (BigInteger) results.get(3).getValue(),
                                (BigInteger) results.get(4).getValue(),
                                (Boolean) results.get(5).getValue(),
                                (Boolean) results.get(6).getValue(),
                                (BigInteger) results.get(7).getValue());
                    }
                });
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
                Arrays.<Type>asList(new Bytes16(_cid),
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

    public RemoteCall<TransactionReceipt> updateClaim(byte[] _cid, String _author, String _udfs, BigInteger _price) {
        final Function function = new Function(
                FUNC_UPDATECLAIM,
                Arrays.<Type>asList(new Bytes16(_cid),
                new Address(_author),
                new Utf8String(_udfs),
                new Uint256(_price)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<DBClaim> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DBClaim.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DBClaim> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DBClaim.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<LogNewClaimEventResponse> getLogNewClaimEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGNEWCLAIM_EVENT, transactionReceipt);
        ArrayList<LogNewClaimEventResponse> responses = new ArrayList<LogNewClaimEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogNewClaimEventResponse typedResponse = new LogNewClaimEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogNewClaimEventResponse> logNewClaimEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogNewClaimEventResponse>() {
            @Override
            public LogNewClaimEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGNEWCLAIM_EVENT, log);
                LogNewClaimEventResponse typedResponse = new LogNewClaimEventResponse();
                typedResponse.log = log;
                typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogNewClaimEventResponse> logNewClaimEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGNEWCLAIM_EVENT));
        return logNewClaimEventObservable(filter);
    }

    public List<LogUpdateClaimUdfsEventResponse> getLogUpdateClaimUdfsEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMUDFS_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimUdfsEventResponse> responses = new ArrayList<LogUpdateClaimUdfsEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimUdfsEventResponse typedResponse = new LogUpdateClaimUdfsEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._price = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimUdfsEventResponse> logUpdateClaimUdfsEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimUdfsEventResponse>() {
            @Override
            public LogUpdateClaimUdfsEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMUDFS_EVENT, log);
                LogUpdateClaimUdfsEventResponse typedResponse = new LogUpdateClaimUdfsEventResponse();
                typedResponse.log = log;
                typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._price = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogUpdateClaimUdfsEventResponse> logUpdateClaimUdfsEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGUPDATECLAIMUDFS_EVENT));
        return logUpdateClaimUdfsEventObservable(filter);
    }

    public List<LogUpdateClaimAuthorEventResponse> getLogUpdateClaimAuthorEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMAUTHOR_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimAuthorEventResponse> responses = new ArrayList<LogUpdateClaimAuthorEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimAuthorEventResponse typedResponse = new LogUpdateClaimAuthorEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._newAuthor = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimAuthorEventResponse> logUpdateClaimAuthorEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimAuthorEventResponse>() {
            @Override
            public LogUpdateClaimAuthorEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMAUTHOR_EVENT, log);
                LogUpdateClaimAuthorEventResponse typedResponse = new LogUpdateClaimAuthorEventResponse();
                typedResponse.log = log;
                typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._newAuthor = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogUpdateClaimAuthorEventResponse> logUpdateClaimAuthorEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGUPDATECLAIMAUTHOR_EVENT));
        return logUpdateClaimAuthorEventObservable(filter);
    }

    public List<LogUpdateClaimPriceEventResponse> getLogUpdateClaimPriceEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMPRICE_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimPriceEventResponse> responses = new ArrayList<LogUpdateClaimPriceEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimPriceEventResponse typedResponse = new LogUpdateClaimPriceEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._newprice = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimPriceEventResponse> logUpdateClaimPriceEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimPriceEventResponse>() {
            @Override
            public LogUpdateClaimPriceEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMPRICE_EVENT, log);
                LogUpdateClaimPriceEventResponse typedResponse = new LogUpdateClaimPriceEventResponse();
                typedResponse.log = log;
                typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._newprice = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogUpdateClaimPriceEventResponse> logUpdateClaimPriceEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGUPDATECLAIMPRICE_EVENT));
        return logUpdateClaimPriceEventObservable(filter);
    }

    public List<LogUpdateClaimDropEventResponse> getLogUpdateClaimDropEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMDROP_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimDropEventResponse> responses = new ArrayList<LogUpdateClaimDropEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimDropEventResponse typedResponse = new LogUpdateClaimDropEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._drop = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimDropEventResponse> logUpdateClaimDropEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimDropEventResponse>() {
            @Override
            public LogUpdateClaimDropEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMDROP_EVENT, log);
                LogUpdateClaimDropEventResponse typedResponse = new LogUpdateClaimDropEventResponse();
                typedResponse.log = log;
                typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._drop = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogUpdateClaimDropEventResponse> logUpdateClaimDropEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGUPDATECLAIMDROP_EVENT));
        return logUpdateClaimDropEventObservable(filter);
    }

    public List<LogUpdateClaimForbiddenEventResponse> getLogUpdateClaimForbiddenEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMFORBIDDEN_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimForbiddenEventResponse> responses = new ArrayList<LogUpdateClaimForbiddenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimForbiddenEventResponse typedResponse = new LogUpdateClaimForbiddenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._forbidden = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimForbiddenEventResponse> logUpdateClaimForbiddenEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimForbiddenEventResponse>() {
            @Override
            public LogUpdateClaimForbiddenEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMFORBIDDEN_EVENT, log);
                LogUpdateClaimForbiddenEventResponse typedResponse = new LogUpdateClaimForbiddenEventResponse();
                typedResponse.log = log;
                typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._forbidden = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogUpdateClaimForbiddenEventResponse> logUpdateClaimForbiddenEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGUPDATECLAIMFORBIDDEN_EVENT));
        return logUpdateClaimForbiddenEventObservable(filter);
    }

    public List<LogDeleteCLaimEventResponse> getLogDeleteCLaimEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(LOGDELETECLAIM_EVENT, transactionReceipt);
        ArrayList<LogDeleteCLaimEventResponse> responses = new ArrayList<LogDeleteCLaimEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            LogDeleteCLaimEventResponse typedResponse = new LogDeleteCLaimEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogDeleteCLaimEventResponse> logDeleteCLaimEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogDeleteCLaimEventResponse>() {
            @Override
            public LogDeleteCLaimEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(LOGDELETECLAIM_EVENT, log);
                LogDeleteCLaimEventResponse typedResponse = new LogDeleteCLaimEventResponse();
                typedResponse.log = log;
                typedResponse._claimId = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogDeleteCLaimEventResponse> logDeleteCLaimEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGDELETECLAIM_EVENT));
        return logDeleteCLaimEventObservable(filter);
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

    public static DBClaim load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBClaim(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DBClaim load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBClaim(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogNewClaimEventResponse {
        public Log log;

        public String _author;

        public byte[] _claimId;
    }

    public static class LogUpdateClaimUdfsEventResponse {
        public Log log;

        public String _author;

        public byte[] _claimId;

        public BigInteger _price;
    }

    public static class LogUpdateClaimAuthorEventResponse {
        public Log log;

        public String _author;

        public String _newAuthor;

        public byte[] _claimId;
    }

    public static class LogUpdateClaimPriceEventResponse {
        public Log log;

        public String _author;

        public byte[] _claimId;

        public BigInteger _newprice;
    }

    public static class LogUpdateClaimDropEventResponse {
        public Log log;

        public String _author;

        public byte[] _claimId;

        public Boolean _drop;
    }

    public static class LogUpdateClaimForbiddenEventResponse {
        public Log log;

        public byte[] _claimId;

        public Boolean _forbidden;
    }

    public static class LogDeleteCLaimEventResponse {
        public Log log;

        public byte[] _claimId;
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

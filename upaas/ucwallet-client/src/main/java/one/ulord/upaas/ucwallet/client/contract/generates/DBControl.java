package one.ulord.upaas.ucwallet.client.contract.generates;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class DBControl extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550612a6d806100a16000396000f3006080604052600436106100d0576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680635be7cc16146100d557806379ba5097146101185780638da5cb5b1461012f57806392d4347e146101865780639b19251a1461023b578063a7aeb52414610296578063c02dca7b14610362578063c3676a021461042d578063c545f6771461049e578063ca85a1b314610535578063d74afaa114610616578063f17279541461066d578063f2fde38b14610724578063f851a44014610767575b600080fd5b3480156100e157600080fd5b50610116600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506107be565b005b34801561012457600080fd5b5061012d61096d565b005b34801561013b57600080fd5b50610144610b0c565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561019257600080fd5b50610217600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610b31565b6040518082600d81111561022757fe5b60ff16815260200191505060405180910390f35b34801561024757600080fd5b5061027c600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610eed565b604051808215151515815260200191505060405180910390f35b3480156102a257600080fd5b5061033e600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919080359060200190929190803560ff169060200190929190505050610f0d565b6040518082600d81111561034e57fe5b60ff16815260200191505060405180910390f35b34801561036e57600080fd5b50610409600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061153c565b6040518082600d81111561041957fe5b60ff16815260200191505060405180910390f35b34801561043957600080fd5b5061047a600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803515159060200190929190505050611a4b565b6040518082600d81111561048a57fe5b60ff16815260200191505060405180910390f35b3480156104aa57600080fd5b50610511600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803515159060200190929190505050611c38565b6040518082600d81111561052157fe5b60ff16815260200191505060405180910390f35b34801561054157600080fd5b5061059c600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061205a565b604051808873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200187815260200186815260200185815260200184151515158152602001831515151581526020018260ff1660ff16815260200197505050505050505060405180910390f35b34801561062257600080fd5b5061062b612519565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561067957600080fd5b50610700600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080351515906020019092919050505061253f565b6040518082600d81111561071057fe5b60ff16815260200191505060405180910390f35b34801561073057600080fd5b50610765600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612940565b005b34801561077357600080fd5b5061077c612a1b565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614806108665750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b151561087157600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16141515156108ad57600080fd5b8073ffffffffffffffffffffffffffffffffffffffff16600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a380600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156109c957600080fd5b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff167f0ad836e1614da10ff391cfc802a39f547f3cdc42900fa72de16b085855c169d560405160405180910390a3600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060011515600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610bdc577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360046040518082600d811115610bc057fe5b60ff16815260200191505060405180910390a160049050610ee6565b8273ffffffffffffffffffffffffffffffffffffffff166003856040518082805190602001908083835b602083101515610c2b5780518252602082019150602081019050602083039250610c06565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141515610cee577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360056040518082600d811115610cd257fe5b60ff16815260200191505060405180910390a160059050610ee6565b816003856040518082805190602001908083835b602083101515610d275780518252602082019150602081019050602083039250610d02565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020600301541415610db3577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600c6040518082600d811115610d9757fe5b60ff16815260200191505060405180910390a1600c9050610ee6565b816003856040518082805190602001908083835b602083101515610dec5780518252602082019150602081019050602083039250610dc7565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020600301819055508273ffffffffffffffffffffffffffffffffffffffff167f9448ddc6c70991d74089fd18cc9ec3777d41e96dc3e33fcb714d296c7a848c8885846040518080602001838152602001828103825284818151815260200191508051906020019080838360005b83811015610ea6578082015181840152602081019050610e8b565b50505050905090810190601f168015610ed35780820380516001836020036101000a031916815260200191505b50935050505060405180910390a2600090505b9392505050565b60046020528060005260406000206000915054906101000a900460ff1681565b600060011515600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515141515610fb8577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360046040518082600d811115610f9c57fe5b60ff16815260200191505060405180910390a160049050611533565b60006003876040518082805190602001908083835b602083101515610ff25780518252602082019150602081019050602083039250610fcd565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415156110b5577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600a6040518082600d81111561109957fe5b60ff16815260200191505060405180910390a1600a9050611533565b846003876040518082805190602001908083835b6020831015156110ee57805182526020820191506020810190506020830392506110c9565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550426003876040518082805190602001908083835b60208310151561119b5780518252602082019150602081019050602083039250611176565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060010181905550826003876040518082805190602001908083835b60208310151561120e57805182526020820191506020810190506020830392506111e9565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060020181905550836003876040518082805190602001908083835b602083101515611281578051825260208201915060208101905060208303925061125c565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206003018190555060006003876040518082805190602001908083835b6020831015156112f557805182526020820191506020810190506020830392506112d0565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160006101000a81548160ff02191690831515021790555060006003876040518082805190602001908083835b60208310151561137c5780518252602082019150602081019050602083039250611357565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160016101000a81548160ff021916908315150217905550816003876040518082805190602001908083835b60208310151561140257805182526020820191506020810190506020830392506113dd565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160026101000a81548160ff021916908360ff1602179055507fac1e108941336ec8d5e39e14628168f13853302b0bde9b90881fb75b10225228858784604051808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001806020018360ff1660ff168152602001828103825284818151815260200191508051906020019080838360005b838110156114f25780820151818401526020810190506114d7565b50505050905090810190601f16801561151f5780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a1600090505b95945050505050565b600060011515600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151415156115e7577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360046040518082600d8111156115cb57fe5b60ff16815260200191505060405180910390a160049050611a44565b8273ffffffffffffffffffffffffffffffffffffffff166003856040518082805190602001908083835b6020831015156116365780518252602082019150602081019050602083039250611611565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415156116f9577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360056040518082600d8111156116dd57fe5b60ff16815260200191505060405180910390a160059050611a44565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16141561177d577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360086040518082600d81111561176157fe5b60ff16815260200191505060405180910390a160089050611a44565b8273ffffffffffffffffffffffffffffffffffffffff166003856040518082805190602001908083835b6020831015156117cc57805182526020820191506020810190506020830392506117a7565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141561188e577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600c6040518082600d81111561187257fe5b60ff16815260200191505060405180910390a1600c9050611a44565b816003856040518082805190602001908083835b6020831015156118c757805182526020820191506020810190506020830392506118a2565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055507fe0b6d1b06e8fff790dd637867051110bba71732683c7483c6e8c901ea646abd484848460405180806020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001828103825285818151815260200191508051906020019080838360005b83811015611a035780820151818401526020810190506119e8565b50505050905090810190601f168015611a305780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a1600090505b9392505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161480611af55750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b1515611b0057600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415611b84577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360086040518082600d811115611b6857fe5b60ff16815260200191505060405180910390a160089050611c32565b81600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508273ffffffffffffffffffffffffffffffffffffffff167f034aa244844c4b4471cae4afc6b61354f390a8755d044fa93313ecfa6f9e78b983604051808215151515815260200191505060405180910390a2600090505b92915050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614158015611ce55750600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614155b15611d39577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360036040518082600d811115611d1d57fe5b60ff16815260200191505060405180910390a160039050612054565b600073ffffffffffffffffffffffffffffffffffffffff166003846040518082805190602001908083835b602083101515611d895780518252602082019150602081019050602083039250611d64565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415611e4b577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600b6040518082600d811115611e2f57fe5b60ff16815260200191505060405180910390a1600b9050612054565b8115156003846040518082805190602001908083835b602083101515611e865780518252602082019150602081019050602083039250611e61565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160019054906101000a900460ff1615151415611f21577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600c6040518082600d811115611f0557fe5b60ff16815260200191505060405180910390a1600c9050612054565b816003846040518082805190602001908083835b602083101515611f5a5780518252602082019150602081019050602083039250611f35565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160016101000a81548160ff0219169083151502179055507ff207ad1dc55ca1d246431eedfbf89de7e751c8bcc8f3f89da017a4f4e45cfb988383604051808060200183151515158152602001828103825284818151815260200191508051906020019080838360005b83811015612014578082015181840152602081019050611ff9565b50505050905090810190601f1680156120415780820380516001836020036101000a031916815260200191505b50935050505060405180910390a1600090505b92915050565b60008060008060008060008073ffffffffffffffffffffffffffffffffffffffff166003896040518082805190602001908083835b6020831015156120b4578051825260208201915060208101905060208303925061208f565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16148061217d575060001515600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515145b156121af576000806000806001806000869650859550849450839350809050965096509650965096509650965061250e565b6003886040518082805190602001908083835b6020831015156121e757805182526020820191506020810190506020830392506121c2565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff166003896040518082805190602001908083835b6020831015156122765780518252602082019150602081019050602083039250612251565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206001015460038a6040518082805190602001908083835b6020831015156122e557805182526020820191506020810190506020830392506122c0565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206002015460038b6040518082805190602001908083835b602083101515612354578051825260208201915060208101905060208303925061232f565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206003015460038c6040518082805190602001908083835b6020831015156123c3578051825260208201915060208101905060208303925061239e565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160009054906101000a900460ff1660038d6040518082805190602001908083835b60208310151561243f578051825260208201915060208101905060208303925061241a565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160019054906101000a900460ff1660038e6040518082805190602001908083835b6020831015156124bb5780518252602082019150602081019050602083039250612496565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160029054906101000a900460ff1696509650965096509650965096505b919395979092949650565b600260009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600060011515600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151415156125ea577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360046040518082600d8111156125ce57fe5b60ff16815260200191505060405180910390a160049050612939565b8273ffffffffffffffffffffffffffffffffffffffff166003856040518082805190602001908083835b6020831015156126395780518252602082019150602081019050602083039250612614565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161415156126fc577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc360056040518082600d8111156126e057fe5b60ff16815260200191505060405180910390a160059050612939565b8115156003856040518082805190602001908083835b6020831015156127375780518252602082019150602081019050602083039250612712565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160009054906101000a900460ff16151514156127d2577f0489f2369368f4688acd012107ac5a7d98ca739b913449d852f35d871e433cc3600c6040518082600d8111156127b657fe5b60ff16815260200191505060405180910390a1600c9050612939565b816003856040518082805190602001908083835b60208310151561280b57805182526020820191506020810190506020830392506127e6565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060040160006101000a81548160ff0219169083151502179055507f1062332b48e83b264a947e9c88c6c649cf9ef38858a621ed5efbcf220120745884848460405180806020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200183151515158152602001828103825285818151815260200191508051906020019080838360005b838110156128f85780820151818401526020810190506128dd565b50505050905090810190601f1680156129255780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a1600090505b9392505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561299b57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16141515156129d757600080fd5b80600260006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16815600a165627a7a723058207f816ba4229e42dc03289938261f4428e72e7d82e5c1e8d20793b246911e492c0029";

    public static final String FUNC_TRANSFERADMINSHIP = "transferAdminship";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_UPDATECLAIMPRICE = "updateClaimPrice";

    public static final String FUNC_WHITELIST = "whitelist";

    public static final String FUNC_CREATECLAIM = "createClaim";

    public static final String FUNC_UPDATECLAIMAUTHOR = "updateClaimAuthor";

    public static final String FUNC_MANGEWHILELIST = "mangeWhileList";

    public static final String FUNC_UPDATECLAIMFORBIDDEN = "updateClaimForbidden";

    public static final String FUNC_SEARCHCLAIM = "SearchClaim";

    public static final String FUNC_NEWOWNER = "newowner";

    public static final String FUNC_UPDATECLAIMDROP = "updateClaimDrop";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_ADMIN = "admin";

    public static final Event LOGMANGEWHILE_EVENT = new Event("LogMangeWhile", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event LOGNEWCLAIM_EVENT = new Event("LogNewClaim", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint8>() {}));
    ;

    public static final Event LOGUPDATECLAIMAUTHOR_EVENT = new Event("LogUpdateClaimAuthor", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event LOGUPDATECLAIMPRICE_EVENT = new Event("LogUpdateClaimPrice", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGUPDATECLAIMDROP_EVENT = new Event("LogUpdateClaimDrop", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event LOGUPDATECLAIMFORBIDDEN_EVENT = new Event("LogUpdateClaimForbidden", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event LOGTRANSFERADMIN_EVENT = new Event("LogTransferAdmin", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event LOGERROR_EVENT = new Event("LogError", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    protected DBControl(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DBControl(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> transferAdminship(String _new) {
        final Function function = new Function(
                FUNC_TRANSFERADMINSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_new)), 
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

    public RemoteCall<Boolean> whitelist(String param0) {
        final Function function = new Function(FUNC_WHITELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteCall<TransactionReceipt> updateClaimAuthor(String _cid, String _author, String _newAuthor) {
        final Function function = new Function(
                FUNC_UPDATECLAIMAUTHOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_cid), 
                new org.web3j.abi.datatypes.Address(_author), 
                new org.web3j.abi.datatypes.Address(_newAuthor)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mangeWhileList(String _dest, Boolean _stauts) {
        final Function function = new Function(
                FUNC_MANGEWHILELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_dest), 
                new org.web3j.abi.datatypes.Bool(_stauts)), 
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

    public static RemoteCall<DBControl> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DBControl.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DBControl> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DBControl.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<LogMangeWhileEventResponse> getLogMangeWhileEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGMANGEWHILE_EVENT, transactionReceipt);
        ArrayList<LogMangeWhileEventResponse> responses = new ArrayList<LogMangeWhileEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
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
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGMANGEWHILE_EVENT, log);
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

    public List<LogNewClaimEventResponse> getLogNewClaimEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGNEWCLAIM_EVENT, transactionReceipt);
        ArrayList<LogNewClaimEventResponse> responses = new ArrayList<LogNewClaimEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogNewClaimEventResponse typedResponse = new LogNewClaimEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._author = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._cid = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._type = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogNewClaimEventResponse> logNewClaimEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogNewClaimEventResponse>() {
            @Override
            public LogNewClaimEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGNEWCLAIM_EVENT, log);
                LogNewClaimEventResponse typedResponse = new LogNewClaimEventResponse();
                typedResponse.log = log;
                typedResponse._author = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._cid = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._type = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<LogNewClaimEventResponse> logNewClaimEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGNEWCLAIM_EVENT));
        return logNewClaimEventObservable(filter);
    }

    public List<LogUpdateClaimAuthorEventResponse> getLogUpdateClaimAuthorEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMAUTHOR_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimAuthorEventResponse> responses = new ArrayList<LogUpdateClaimAuthorEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimAuthorEventResponse typedResponse = new LogUpdateClaimAuthorEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._author = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._newAuthor = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimAuthorEventResponse> logUpdateClaimAuthorEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimAuthorEventResponse>() {
            @Override
            public LogUpdateClaimAuthorEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMAUTHOR_EVENT, log);
                LogUpdateClaimAuthorEventResponse typedResponse = new LogUpdateClaimAuthorEventResponse();
                typedResponse.log = log;
                typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._author = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._newAuthor = (String) eventValues.getNonIndexedValues().get(2).getValue();
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
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMPRICE_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimPriceEventResponse> responses = new ArrayList<LogUpdateClaimPriceEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimPriceEventResponse typedResponse = new LogUpdateClaimPriceEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._newprice = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimPriceEventResponse> logUpdateClaimPriceEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimPriceEventResponse>() {
            @Override
            public LogUpdateClaimPriceEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMPRICE_EVENT, log);
                LogUpdateClaimPriceEventResponse typedResponse = new LogUpdateClaimPriceEventResponse();
                typedResponse.log = log;
                typedResponse._author = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
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
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMDROP_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimDropEventResponse> responses = new ArrayList<LogUpdateClaimDropEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimDropEventResponse typedResponse = new LogUpdateClaimDropEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._author = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._drop = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimDropEventResponse> logUpdateClaimDropEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimDropEventResponse>() {
            @Override
            public LogUpdateClaimDropEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMDROP_EVENT, log);
                LogUpdateClaimDropEventResponse typedResponse = new LogUpdateClaimDropEventResponse();
                typedResponse.log = log;
                typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._author = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._drop = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
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
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOGUPDATECLAIMFORBIDDEN_EVENT, transactionReceipt);
        ArrayList<LogUpdateClaimForbiddenEventResponse> responses = new ArrayList<LogUpdateClaimForbiddenEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogUpdateClaimForbiddenEventResponse typedResponse = new LogUpdateClaimForbiddenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._forbidden = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LogUpdateClaimForbiddenEventResponse> logUpdateClaimForbiddenEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogUpdateClaimForbiddenEventResponse>() {
            @Override
            public LogUpdateClaimForbiddenEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGUPDATECLAIMFORBIDDEN_EVENT, log);
                LogUpdateClaimForbiddenEventResponse typedResponse = new LogUpdateClaimForbiddenEventResponse();
                typedResponse.log = log;
                typedResponse._cid = (String) eventValues.getNonIndexedValues().get(0).getValue();
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

    public static DBControl load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBControl(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DBControl load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DBControl(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LogMangeWhileEventResponse {
        public Log log;

        public String _dest;

        public Boolean _allow;
    }

    public static class LogNewClaimEventResponse {
        public Log log;

        public String _author;

        public String _cid;

        public BigInteger _type;
    }

    public static class LogUpdateClaimAuthorEventResponse {
        public Log log;

        public String _cid;

        public String _author;

        public String _newAuthor;
    }

    public static class LogUpdateClaimPriceEventResponse {
        public Log log;

        public String _author;

        public String _cid;

        public BigInteger _newprice;
    }

    public static class LogUpdateClaimDropEventResponse {
        public Log log;

        public String _cid;

        public String _author;

        public Boolean _drop;
    }

    public static class LogUpdateClaimForbiddenEventResponse {
        public Log log;

        public String _cid;

        public Boolean _forbidden;
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

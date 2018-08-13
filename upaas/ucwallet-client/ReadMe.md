# UCWallet Client (Java)
Ulord content wallet
## Test private key
[Test private key](./usc-private-key.md)

**NOTE: PLEASE DON'T USING THESE KEYS IN YOU PRODUCT ENVIRONMENT AND DON'T TRANSFER ALL TOKEN TO YOUR ADDRESS**

## Deploy
[UShare deploy record](./ushare-ce-deploy.md)

## Usage
### Dependence
 gradle
 ```
 compile ('org.web3j:core:3.4.0')
 compile ('com.github.ipfs:java-ipfs-api:v1.2.0')
 ```
 maven
 ```
 <repositories>
  <repository>
     <id>jitpack.io</id>
     <url>https://jitpack.io</url>
  </repository>
</repositories>
 
 
 <dependency>
   <groupId>org.web3j</groupId>
   <artifactId>core</artifactId>
   <version>3.4.0</version>
 </dependency>
 
<dependencies>
 <dependency>
   <groupId>com.github.ipfs</groupId>
   <artifactId>java-ipfs-api</artifactId>
   <version>v1.2.0</version>
 </dependency>
</dependencies> 
 ```
 
 ## API
 
 ### TransactionActionHandler
 This interface is a transaction action handler for every transaction execute with ContentContract.
 
 ### ContentContract
 The class is for content distribute smart contract.
 
 
 ### UDFSClient
 The class is for content access, which can store a file to UDFS and get content form UDFS.
 
 ## Demo
 
 ```
// create a content contract instance using test environment
// Ulurd side chain https://github.com/UlordChain/Ulord-Sidechain
ContentContract contentContract = new ContentContract(
     "http://192.168.14.197:44444", // Ulord side chain
     "0xa0544b7124c36d50f2580a67750f10cd5a16056c", // UX token test contract address
     "0xcbe2540ce8543d15ec157749295e35cc9593fe8b", // UX token admin contract address
     "0x300d7fd299d1994b0c9da55c64f78fc9fe32c301", // UX token publisher contract address
     "keystore/keystore/ulord-testnet-rsk.wallet.json", // a keystore file for current user account
     "12345678", // keystore password
     this // we just using test class for transaction action handler
     );
  
  
// We transfer 1000000000 UXwei to  0x24fd610e1769f1f051e6d25a9099588df13d7feb account
contentContract.transferToken("tranfer token:",
                      "0x24fd610e1769f1f051e6d25a9099588df13d7feb",
                      BigInteger.valueOf(1000000000));

// Using UDFSClient publish a resource to UDFS
UDFSClient udfsClient = new UDFSClient("/ip4/114.67.37.2/tcp/20418"); // Test UDFS network
String udfsHash = udfsClient.publishResource("test",
        ("Hello Ulord Platform").getBytes());
// We can get content from UDFS network
byte[] udfsContent = udfsClient.getContent(udfsHash);

// we can publish to ulord side chain
// the author address is 0x3f16131ac9203656a9ca790f23878ae165c3eb4f
// the content price is 100000000000000L UXwei
// and there is no depoist for current content
contentContract.publishResource("publish resource", udfsHash,
                    "0x3f16131ac9203656a9ca790f23878ae165c3eb4f",
                    BigInteger.valueOf(100000000000000L), BigInteger.ZERO);
                    
List<String> addressList = new ArrayList<>();
            addressList.add("0x24fd610e1769f1f051e6d25a9099588df13d7feb");
            addressList.add("0x3f16131ac9203656a9ca790f23878ae165c3eb4f");
            addressList.add("0x2e836371bf20107837da6ad9bb4d08d8f53f65ba");
            addressList.add("0x597ed0bf61b741a80d4774a4a4d318b431a07b07");
            addressList.add("0x9babfbae60ad466a5b68d29e127bb59429828216");
            List<BigInteger> valueList = new ArrayList<>();
            valueList.add(new BigInteger("100000000000000000"));
            valueList.add(new BigInteger("200000000000000000"));
            valueList.add(new BigInteger("300000000000000000"));
            valueList.add(new BigInteger("400000000000000000"));
            valueList.add(new BigInteger("500000000000000000"));
            contentContract.transferTokens("transferMultipleAddress", addressList, valueList);                    
 ```
 
We can also get balance of side chain and token balance.
```
// get SUT balance
public BigInteger getGasBalance() throws IOException；

// Get token balance
public BigInteger getTokenBanalce() throws Exception；
``` 
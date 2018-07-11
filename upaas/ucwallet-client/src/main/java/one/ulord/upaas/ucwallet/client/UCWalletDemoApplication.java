/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.client;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import one.ulord.upaas.ucwallet.client.contract.generates.CenterPublish;
import one.ulord.upaas.ucwallet.client.contract.generates.DBControl;
import one.ulord.upaas.ucwallet.client.contract.generates.USHToken;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author haibo
 * @since 7/4/18
 */
public class UCWalletDemoApplication implements TransactionActionHandler{

    public void testContentContract(){
        try {
            System.out.println("Create a content contract object....");
            ContentContract contentContract = new ContentContract(
                    "http://192.168.14.197:44444",
                    "0xa0544b7124c36d50f2580a67750f10cd5a16056c",
                    "0xcbe2540ce8543d15ec157749295e35cc9593fe8b",
                    "0x300d7fd299d1994b0c9da55c64f78fc9fe32c301",
                    "keystore/ulord-testnet-rsk.wallet.json",
                    "12345678",
                    this
                    );

            // get gas balance
            System.out.println("Gas balance:" + contentContract.getGasBalance().toString());
            try {
                System.out.println("Token balance main:" + contentContract.getTokenBalance().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // set gas balance
            System.out.println("Transfer some gas to...");
            contentContract.transferGas("transfer gas", "0x2e836371bf20107837da6ad9bb4d08d8f53f65ba",
                    new BigInteger("1000000000000"));

            // test token transfer
            System.out.println("Transfer 100000000000000000(1Token) to address 0x24fd610e1769f1f051e6d25a9099588df13d7feb... ");
            contentContract.transferToken("tranfer token:",
                    "0x24fd610e1769f1f051e6d25a9099588df13d7feb",
                    BigInteger.valueOf(1000000000));

            // test publish content
            System.out.println("Connect to UDFS network...");
            UDFSClient udfsClient = new UDFSClient("/ip4/114.67.37.2/tcp/20418"); // Test UDFS network
            System.out.println("Publish a sentence to UDFS ...");
            String udfsHash = udfsClient.publishResource("test",
                    ("Hello Ulord Platform" + Calendar.getInstance().toString()).getBytes());
            byte[] udfsContent = udfsClient.getContent(udfsHash);
            if (udfsContent != null) {
                System.out.println(new String(udfsContent));
            }else{
                System.out.println("Cannot get content from UDFS.");
            }

            System.out.println("publish a resource " + udfsHash
                    + " to ulord using address:0x3f16131ac9203656a9ca790f23878ae165c3eb4f" ) ;
            contentContract.publishResource("publish resource", udfsHash,
                    "0x3f16131ac9203656a9ca790f23878ae165c3eb4f",
                    BigInteger.valueOf(100000000000000L), BigInteger.ZERO);

            // first, we nedd to approve publish contract to using current user address tokens
            // !! A user address only can approve once, you can approve it again after approve with a ZERO parameter
            // Current action must be successfully execute before transfer tokens
//            contentContract.approveContractQuality("Clear Approve", BigInteger.ZERO);
//            contentContract.approveContractQuality("Approve", new BigInteger("10000000000000000000"));

            // test multiple transfer action
            System.out.println("Transfer some quality to multiple address");
            List<String> addressList = new ArrayList<>();
            addressList.add("0x3f16131ac9203656a9ca790f23878ae165c3eb4f");
            addressList.add("0x2e836371bf20107837da6ad9bb4d08d8f53f65ba");
            addressList.add("0x597ed0bf61b741a80d4774a4a4d318b431a07b07");
            addressList.add("0x9babfbae60ad466a5b68d29e127bb59429828216");
            List<BigInteger> valueList = new ArrayList<>();
            valueList.add(new BigInteger("200000000000000000"));
            valueList.add(new BigInteger("300000000000000000"));
            valueList.add(new BigInteger("400000000000000000"));
            valueList.add(new BigInteger("500000000000000000"));
            contentContract.transferTokens("transferMultipleAddress", addressList, valueList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        UCWalletDemoApplication application = new UCWalletDemoApplication();
        application.testContentContract();
    }

    // only for development test
    public void testContract(){
        Web3j web3j = Web3j.build(new HttpService("http://192.168.14.197:44444"));

        try {
            Web3ClientVersion web3ClientVersion = null;
            web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();

            System.out.println(clientVersion);

            String resourcePath = UCWalletDemoApplication.class.getClassLoader().getResource("").toString();
            int typeSpliterPos = resourcePath.indexOf(":");
            if (typeSpliterPos > 0){
                resourcePath = resourcePath.substring(typeSpliterPos+1);
            }
//            Credentials credentials = WalletUtils.loadCredentials("12345678", resourcePath + "keystore/usc.test.wallet.json");
//            Credentials credentials = WalletUtils.loadCredentials("12345678", resourcePath + "keystore/ulord-testnet-rsk.wallet.json");
            Credentials credentials = WalletUtils.loadCredentials("abcd1234", resourcePath + "keystore/usc.test.lk.wallet.json");


            String address = credentials.getAddress();
            EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).sendAsync().get();
            System.out.println("Balance " + address + " -> " + Convert.fromWei(new BigDecimal(balance.getBalance()), Convert.Unit.ETHER).toString());

//            String USHTokenContractAddress = "0xaae829dfbe7c95899b730db1faaf7c4a6493faf9";
            String USHTokenContractAddress = "0xa0544b7124c36d50f2580a67750f10cd5a16056c";
            USHToken ushToken = USHToken.load(USHTokenContractAddress, web3j, credentials,
                    DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
            if (false) {
                // Now lets deploy a smart contract (USHToken)
                System.out.println("Deploying smart contract");
                ushToken = USHToken.deploy(
                        web3j, credentials, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT).send();

                USHTokenContractAddress = ushToken.getContractAddress();
                System.out.println("Smart contract deployed to address " + USHTokenContractAddress);
            }

            DBControl dbControl;
//            String dbControlAddrress = "0x5624ea837b66be1db3ebc53673b505d1065097a2";
            String dbControlAddrress = "0xcbe2540ce8543d15ec157749295e35cc9593fe8b";
            dbControl = DBControl.load(dbControlAddrress, web3j, credentials,
                    DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
            if (false) {
                dbControl = DBControl.deploy(
                        web3j, credentials, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT).send();
                dbControlAddrress = dbControl.getContractAddress();
                System.out.println("Smart contract deployed to address " + dbControlAddrress);
            }

            CenterPublish centerPublish;
//            String centerPublishContractAddress = "0xe8c267f8bc8f72b94f62c90ab90c9680609b4984";
//            String centerPublishContractAddress = "0xf2a079e8940dad9e22d91b5967e8bebf0163dd7e";
//            String centerPublishContractAddress = "0x5bcb00efaffaa9096a5514e4572e04b930c92d8d";
            String centerPublishContractAddress = "0x300d7fd299d1994b0c9da55c64f78fc9fe32c301";
            centerPublish = CenterPublish.load(centerPublishContractAddress, web3j, credentials,
                    DefaultGasProvider.GAS_PRICE, ContentContract.BLOCK_GAS_LIMIT); // GAS LIMIT每区块最大限制值
            if (false) {
                centerPublish = CenterPublish.deploy(
                        web3j, credentials, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
                        USHTokenContractAddress, dbControlAddrress).send();
                centerPublishContractAddress = centerPublish.getContractAddress();
                System.out.println("Smart contract deployed to address " + centerPublishContractAddress);
            }




//
            // 调用合约的decimals方法
            System.out.println(ushToken.decimals().send().toString());

            // 调用合约的totalSupply方法
            System.out.println(ushToken.totalSupply().send().toString());

            // 调用合约的name方法
            System.out.println(ushToken.name().send().toString());

            // 调用合约的symbol方法
            System.out.println(ushToken.symbol().send().toString());

            // 查看USH代币余额
            System.out.println(ushToken.balanceOf(address).send().toString());

            if(false){
                TransactionReceipt receipt = ushToken.transfer("0x24fd610e1769f1f051e6d25a9099588df13d7feb", new BigInteger("100000000000")).send();
                if (receipt.isStatusOK()) {
                    System.out.println("转账成功");
                } else {
                    System.out.println("转账失败");
                }
            }
            // 发布文章
            if(true) {
                // 发布内容到UDFS
                IPFS ipfs = new IPFS("/ip4/114.67.37.2/tcp/20418");
                NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper("hello.txt", "G'day world! IPFS rocks!".getBytes());
                MerkleNode addResult = ipfs.add(file).get(0);
                System.out.println(addResult.toJSONString());

//                TransactionReceipt receipt = centerPublish.createClaim("QmXYZxya1234567890AbcdE", address,
//                        new BigInteger("10"), new BigInteger("0"), new BigInteger("1")).send();
//                if (receipt.isStatusOK()) {
//                    System.out.println("发布成功");
//                } else {
//                    System.out.println("发布失败");
//                }
            }

            if(false) {
                // 授权
                TransactionReceipt receipt = ushToken.approve(centerPublishContractAddress, new BigInteger("1000000000000000000000000")).send();
                if (receipt.isStatusOK()) {
                    System.out.println("授权成功");
                } else {
                    System.out.println("授权失败");
                }
            }

            // 给多个地址转钱 (最多200笔每次交易)
            if (false){
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
                TransactionReceipt receipt = centerPublish.mulTransfer(addressList, valueList).send();
                if (receipt.isStatusOK()) {
                    System.out.println("批量发送代币成功");
                } else {
                    System.out.println("批量发送代币失败.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void success(String id, String txhash) {
        System.out.println("--->id:" + id + ", txhash:" + txhash);
    }

    @Override
    public void fail(String id, String message) {
        System.out.println("--->id:" + id + ", message:" + message);
    }
}

/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.client;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
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
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author haibo
 * @since 7/4/18
 */
public class UCWalletDemoApplication implements TransactionActionHandler {

    public void testContentContract() {
        try {
            System.out.println("Create a content contract object....");
            ContentContract contentContract = new ContentContract(
                    "http://192.168.12.231",
                    "0x8345306e5104262a6719636ef6cd2af80406a7ca", // UShare UX
                    "0xdd74e976399d6bfd8427bb6c090730165a8802fe",
                    "0xba6baf26e4d7aa8062a8921843ad4b5714afbb57",
                    "keystore/no6.keystore",
                    "12345678",
                    this
            );

            // get gas balance
            System.out.println("SUT balance:" + contentContract.getGasBalance().toString());
            try {
                System.out.println("Token balance main:" + contentContract.getTokenBalance().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (false) {
                // set gas balance
                System.out.println("Transfer some SUT to...");
                contentContract.transferGas("transfer gas", "0x2e836371bf20107837da6ad9bb4d08d8f53f65ba",
                        new BigInteger("1000000000000"));
            }

            if (false) {
                // set gas balance
                System.out.println("Transfer some SUT to...");
                String hash = contentContract.transferGas("0x2e836371bf20107837da6ad9bb4d08d8f53f65ba",
                        new BigInteger("1000000000000"));
                System.out.println(hash);
            }


            String address = "0x5664c1ff2476a0b3f27d8679e278033611462df4,0x8b126bf7b2838b100187f8e06d3ac9e37037d717,0x99ab337ecbacc695bc7eed5c9ab238b380f8ebdd,0x23f11a98e486a2b97ef5aaa7456fa9857cd08f10,0xafc78706305d97e026cca3b835b38164d83c3aeb,0xf90d9c35eb0f13e4bd5991b30e88d9f5576a9c69,0xba601c6e22c10a78c87016730c91a4a4b9468c8e,0xbfc4e591b9820a6bb9061dd120b5b0245b802dfc,0x8c2489bc48097b210b6df0ea78e1639d6162766b,0x8e6e83778a196f90b42644f8f57196ce4734ca69,0xbaaac6b6b6eb8762c58c7c6db7287062ac7ee1f6,0xde3f0ad8d65dae9c2b9c93c91a42099f2e6e9177,0x5c102b64128a15b30b2d66c87ada828952d46228,0x563dfcec423f263443850e18de43f9c93a0b0d3d,0xf9d3b7a1c17f0b986d307e26e613f8f824e283bf,0xe8846884d70ea4d27b95f0747b1baa95046efee9,0xb80e12127008c04ae9d605e0ce4d4fbcb2ac807b,0x0f32b212b150f5d41b7912b92bf55d706b6df8e5,0xc8a3ab25699a8167a7a27e92324b791bd41d00c0,0x8a11bb662a2d3d974453e93e22eba8a18cff8b18,0x1352b21cc15478cfe50ccc409401a1a6db689ee3,0xcb1269b97d480fae225e92913f40d7c353b7c0c9,0x451b280a2ed9bd33d460665720dd5b81d760ee5d,0x12bf46545dde61b75bf580955b3fd156ec52233f,0xafd5e24df35d35d5428a9ac734b5dd290ea337e0,0x2db147df6b61e621ed9c121b17a02aea8f0adc62,0x5d61134a556a3b60aa704ad87ee7a459aaeabaac,0xb7f09632b75b2e7dc3f27b1631903e8934dd747c,0x2c62ebcc41ef06a597dceb8c709510d97a323525,0x3cc1b9c06fc9df12766ed15cfd113954ed095c01,0xb53a21243e3c68c4df09009c66b74deb7166f551,0x69e2e7a43e166b7498ddd325040fc81218b0c588,0xf8cafd3ded3e78276038f48b6182490da7d347e2,0x48d04a741bdfb3a84fdf480622f7b8e3e9978a9f,0x652f95f3dc4a01dfd42ec5d4a36c5a7dd5119109,0xa19c23bf3cc8d3508e6c11264c161989ab32e29e,0x161317920769d813f2fb708c2d5477f2fb89f790,0xa326e134eb699c1c7f73d15bdef03f8eeb138ca2,0x7d09af0ddfc8ef5ababd9e3d0ec1253dd75c0355,0xaa976ff23849dc8713aca8edfddb38c93134fc18,0x83eee3eb4b476ea080eeb85b4c7df65a5a5ee85a,0x961e9aac716f7616643cd444c5eb19b66a8947cd,0xe5599530acf7eb103bdddced14f943a8fc3eb834,0x1524ebf684113758e61257326a010a2aebaddc4b,0xfd2851ad1a654b737fbdc4858b56ecfaf659d272,0xfc9c07ebba30c5c1fcb55284068200569fb5a8f0,0x4f3ecdce48f2b6e869a3054f4a1a15efba13db8d,0xc2172737391ea980bf13e0016256a080a4a5f010,0x91f80583378e86046599818090c24c1267987814,0xc6daa5d0cb953187819f9cae8945c39ed352003c,0x18a5322ee887b7b6438815f1d2a4d05c78673731,0x461fd2a1de2c6f2f3f00058a7ba770653357942d,0xdd1a56084e417dfa01ec848cbed519e81c6484d6,0x1491c3cc67187db54be9f88ad3f9690860558106,0xf20103569e2236193e6c609c84226b741b6e3258,0x7aadef08e26d934202cc294a474e6eeeb7ed5e29,0xa34ec2d91dcf0ca80f39bf7869f924418425ccbd,0xb9bbbb9768be65a41b0c466bf2550760213711bd,0x91b4713c2513860e5b35e1b1b08f9a5d814e110f,0x8ee7424e718a8672d2eab23a1e610fa0c8df7d9a,0x7e71a181ea7f2a565f06d76911a3c67508097078,0x1f42fea2dfa64943c57e601b9b99a923f0f515a8,0x0aba8d5809ba2b9098686c784896dcca7b39ab11,0xab5a9dfb22f421d925cec0c352a7193ff0b5c895,0x8aa90cf7111738d5e0ee8452a07f7138353c55ce,0xf9e8a85f370ca50dc76e2acef00b5190fdf62941,0xeac97ecaaa826b61cc8510eee0b4fd33577c8efe,0x5ac1fbe0fbb0ac714f46a7396970da29d26afeae,0x3cb541255490458fa27cf7962b000fca50d7f642,0xe133191c8ea6297f5cedbfea8d9c4f2ef09b4dff,0x8b67b78c02d92b5656a51ca7b5c1c82e69394a8e,0x4f2470250a4391b4f82a24b4fbc071ce407fca70,0x9de000fee90ff1bc9ec9519bacd127f129c22dc7,0xd85db55c258e54e2addd29bba0969fa591e4d133,0x2fc31aeb2f8bf5450e795b635629a86200908537,0x2136dae874f83c9a5c811edaf5b1e7c4dc9075df,0x8e61398f71b82a56a30a6f7ab4f2ffe98346e1e3,0xa6a76afc290ad0d65d12eda3c00fe643d75cda0d,0x8090ed17457b61eca63901ab13c4e37b1c95228a,0xa7b0020d279440e304cfee48eb9e81859ce19177,0x799b8570218f0f0c0f8bea268cb02752a394c31a,0xddeac518920ef420c49dc4de7fab50b5e32498fb,0x9fe6f5270911456cdb20460883637080e15790cc,0x55e0f800169aacb4c3c4c0a28c3c239b54b73287,0xd44a272c14a4a78baf872cbd8bd95b860f5fb3a5,0xd968848a1841cfe47516c820161bbb947bc0b5a0,0x6aecf244de9b4d7c907868ca620c5e4d0c68bbc8,0x2032f14eac37b02e6164f8a3ba6ffbef2e64850e,0x04bc4a205751ebc9398d22f137a697bd8d852ab8,0x6047e2d23b39549874e0cf0d595a9cd6c63cb96a,0xa77e6bfa55c638c0caf926053475600b59658986,0xef567fad386b5dd877dfe939a22fc4a5bcff78e5,0x533b365a931ac6194506bb7c7fd6f535210c5901,0xab08717baa9e706e29606fd3bc6f59e785b803c1,0x4a8446aa471fa8520ee0a062beb758e8bcf1e2ff,0x47030cdfb79c28533ffe4114ffeb89a3df773a5b,0x3f0b2b0bb9bc0f7ce653f2a958cdbab132788ece,0xd049c40b474228ddfba85685139df68df38311d3,0xc6f665e1f10d30015ec1e5ed6a07ad1b419b17d4,0x93b7337e9fadddf68b6c58db67861ecf2e019cef,0xd9eefde3ffd384111f569d7143a725b5eb786e09,0x10881129b38e51a29bfd14ad59f2cc32572083bd,0x5a0dead224e2564a12ce23c23c84857af66874a3,0x9ed03dafa80b08dd202e5bb70a4cafad1ff0c1a7,0x2aa40cc8a82072502317f2fe96de7ea58fbc40e9,0xc418ea12db9592712f771ca1451f594e148f8d6d,0x043f34b4c85e853cd4c81fcef5ea2a2430b74c72,0x14d73c2132f9eb1d4daad56050c8f9581fa36064,0xd8ae638a02c756cabadc9ca272f6b722a1cc8536,0x346c71ebf0cbb21efcd4cc264ae7063ab2a0569a,0x6f6ad2bc4140dbd5cefece66a2a2cd539f7692a0,0x3964905e8292d1103b3ab7124e4e70b194dcfcd6,0xb87f006bca8d7999928001f4f3a1af41ba1d8cf7,0xb0583b5fdabb647f50731b2f04c85d11feb3b457,0xfc42bf35650e454d4fcceee58dbdef056ce0dc90,0xa68d48ef8d1cbdb5ddde3091512553657fd16575,0xaf03929895878ad415dc565e407a27c521b7a66e,0x0e7971ed65323b7312bf8981b20b1eab4b02616f,0xb6d9ee2d994f6058491285498f7ceba8f4a4af67,0x13d851794650b84dd978a26d36346957f3468817,0xf9b97b5b21a55d5112a939b5a11a0d113b8d5866,0xee3d3312aad20b1083f05c04e47c3fc06195cf3b,0xf7ef2fa6a9578df9b401315ec0d423dd588b267b,0x3677d2671afb78a09ba049e5b03e5faa3bb0ab6f,0x151227346c8075e174112c167eaa6ad1ffb191f0,0x5566f9e807fbc9824eeb6d2e989d19ab94905fcb,0x883af4a8d9610e7f289ca2f6bf617c1b31812af1,0xc46597a520814598b2982a468842e7a78a01637e,0x115f6f4c26bd7a4938736ad8f70100be19d1e23c,0x0f249f4d00d00ed2fe2a1122b7023ca330c13963,0x3e43dd4feeac42b47b037b16501b96c8cd0cc398,0xe5a5a14555d87dab4cbc126b5e17d56135f1a8e9,0x3181299816bf27aa0ce2785fe8932eed4a098f5f,0xb84f217a234c8435e9ca27a1072f07dfd94e1f67,0x27ec63319a899be21fe1dae5e6cc5debca22ed5a,0xb94a91e4c84c2d815ad33b2e8f6b0da546d77087,0x1f3748f3da272e89782b4e3419727f9d184c55c3,0x14fdb1ad3b727668078a626696459f2fa6ed216f,0xc4fccb54d96b56f0ae3f032ebc76e5438bc2a5f0,0x1ab52eafe7f8ed28fce8b2531c67a69f95f44ec2,0x533988912d623d9d0a5584a45c6f4853abe03626,0xe6f714cfa98416bfd640d55c6818e0014a1674c2,0xbda7f7c536b9b3d23c4e19963ff69cbdbc6dbae3,0xe8794521c8b0472bc8ccc7f06ca02dc608ab02d8,0xd6a4de19623d09e820c5a0fa1a7f8141a9000fa2,0xa0ae92c2f9169561680f022caa15573c7441d4d4,0x32314b4d7bfa04dd784458b301f31b5b01eee1bf,0xa19057875a250425e220d7d729164a27caccd68f,0x374851c85981d9ec12b5fc6d9f0265b132e0d0fd,0xb097ff7eaf1f0b1fb7718725a8e5245486414822,0x50850b12f8931e5934bf5274b383cf1c36dc0df1,0x52a053e15bb579a9d0d77b4ed58903452911a55c,0xdead7af703546211cc6ca605e3de9fe52bfd72bf,0x26c181d91d5509e7d34cf525cdafb9c0ef46db6f,0x63621621f17c89843d6b5dd819c63f216576bb5e,0x13a81f131b52841cf1d85dc7301956405d34b192,0xf0712bfca51c8a65effe8b6919b31cf201619d73,0xa866f4e83ac4e94fd1a0ee2779e73a7848bc29e7,0x2ccb57676098795a838e4541ab88fb0c59790db6,0xda62174f452fb1c62c7de823a1cdc679908f9db4,0x08806669e5aaefaaea2e05449f20e1637cc39e6f,0x0afed5a0cb32547e2cc5034dfeb8bd093643d98a,0x0613f1c0d8aefcb7dcf57153cbcc20d6f8b786c9,0x07761bcf26962ada7c90647f5559a95f47afe618,0xb5b900aeb69c94e23909a86148a43d298a91cc0b,0xfde0ef4bc29d896e227469e1bd88e146bd362c13,0x52971285e9546dab6e0836eea388f8e87fccfd1a,0x78bee4c4696de65cd18c9b5f3eeb899c2e08d8a5,0xdecf44283dfcc56d04071b2f2074955bfa68012b,0x9d40718a69f79fc720558a912ad9a899c5c1802a,0x56a60677e448ceff946b0d156caf524256959d51,0x26b2fd9522e529e0af4922cdf5de8d5ff7ee1964,0xbdefb1a2de359f7dbd84bc94a112522e15514af5,0xf967cbd88c2cc844c75d77a2d341b11bce72b911,0x0507c58e86a34bb91eff513556e64d8cede22055,0xf0097aad48d76067e09cc704f8c6db156ecf6989,0xba4984e626614284c4126564579bd6f1bce3c5b0,0x25e1265947542d00100b7300264f813babe36e45,0x21f4e19e15fd07a0b2545127722d22b5b710406c,0xd11336e25f61ce0191b637a3934d06a9c2a35dbe,0x91d120853c0be1b54f2038d92c2768dad43dc39b,0x1a6956757745da390f057c15a3a42cc0fc2e6bad,0xb9bf6ad2fa1aebd0b2d43a2d6da3e996baf2f245,0xdf8bfb3f9c76290232f691629ee36898163d713f,0xad6c81b810fe9ea2269e8107598ce7e0a0a944e4,0x5d6162904feb6ffe2eb0f01ee996f959995520c9,0x1ba876168a116163cc31924abbae7e822851bb61,0xa2cd52630e655c9cf12e14d5caed124266472359,0xcb49f35c36e7ab946dca988ddd0a4dd187ed15ae,0xe3d1fd0ed59c90ffcf99b36eef81e270860dd6ec,0xb81dd462a41a89a92c63ccb6061f6daecb89bc76,0x06ba3cc5f84f2a33c3908230ed740ba0030d15e2,0xc28f2c2193b87c7bc6e0dcbf0adc1054a9a879ad,0x3e2d87a67ce385d9b989d5cdfe7e4a4b7002f804,0x37f44b4eee2827c80e657aa3099119ae2d503427,0x5c2375a24140c01065838495c5e18a2b1eddebf2,0x6bd3d361163c2f3b8a4633c187f49e2e9e85f0ac,0x23a4f04d0d8bbc009e6cace100ae60216106a7f5,0xc7073cb2b2b85e910fa614788fe0dd8b81a8a259,0x2ca16cc80a3bc4f70d92876d82a2b3d828166bf3";

            String[] addr = address.split(",");

            List<String> addressList = Arrays.asList(addr);

            if (false) {

                System.out.println("Transfer some SUT to...");
                contentContract.transferSuts("transfer suts", new BigInteger("10000000000000000"), addressList,
                        new BigInteger("2000000000000000000"));
            }

            if (false) {

                System.out.println("Transfer some SUT to...");
                String hash = contentContract.transferSuts(new BigInteger("10000000000000000"), addressList, new BigInteger("2000000000000000000"));
                System.out.println(hash);
            }

            if (false) {
                // test token transfer
                System.out.println("Transfer 100000000000000000(1Token) to address 0x24fd610e1769f1f051e6d25a9099588df13d7feb... ");
                contentContract.transferToken("tranfer token:",
                        "0x24fd610e1769f1f051e6d25a9099588df13d7feb",
                        BigInteger.valueOf(1000000000));
            }

            if (false) {
                // test publish content
                System.out.println("Connect to UDFS network...");
                UDFSClient udfsClient = new UDFSClient("/ip4/114.67.37.2/tcp/20418"); // Test UDFS network
                System.out.println("Publish a sentence to UDFS ...");
                String udfsHash = udfsClient.publishResource("test",
                        ("Hello Ulord Platform" + Calendar.getInstance().toString()).getBytes());
                byte[] udfsContent = udfsClient.getContent(udfsHash);
                if (udfsContent != null) {
                    System.out.println(new String(udfsContent));
                } else {
                    System.out.println("Cannot get content from UDFS.");
                }

                System.out.println("publish a resource " + udfsHash
                        + " to ulord using address:0x3f16131ac9203656a9ca790f23878ae165c3eb4f");
                contentContract.publishResource("publish resource", udfsHash,
                        "0x3f16131ac9203656a9ca790f23878ae165c3eb4f",
                        BigInteger.valueOf(100000000000000L), false);
            }

            // first, we nedd to approve publish contract to using current user address tokens
            // !! A user address only can approve once, you can approve it again after approve with a ZERO parameter
            // Current action must be successfully execute before transfer tokens
//            contentContract.approveContractQuality("Clear Approve", BigInteger.ZERO);
//            contentContract.approveContractQuality("Approve", new BigInteger("10000000000000000000"));

            // test multiple transfer action
            if (false) {
                System.out.println("Transfer some quality to multiple address");
                List<String> list = new ArrayList<>();
                list.add("0x3f16131ac9203656a9ca790f23878ae165c3eb4f");
                list.add("0x2e836371bf20107837da6ad9bb4d08d8f53f65ba");
                list.add("0x597ed0bf61b741a80d4774a4a4d318b431a07b07");
                list.add("0x9babfbae60ad466a5b68d29e127bb59429828216");
                List<BigInteger> valueList = new ArrayList<>();
                valueList.add(new BigInteger("200000000000000000"));
                valueList.add(new BigInteger("300000000000000000"));
                valueList.add(new BigInteger("400000000000000000"));
                valueList.add(new BigInteger("500000000000000000"));
                contentContract.transferTokens("transferMultipleAddress", list, valueList);
            }
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

    @Override
    public void success(String id, String txhash) {
        System.out.println("--->id:" + id + ", txhash:" + txhash);
    }

    @Override
    public void fail(String id, String message) {
        System.out.println("--->id:" + id + ", message:" + message);
    }
}

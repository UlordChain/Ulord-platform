package one.ulord.upaas.ucwallet;

import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.crypto.TransactionDecoder;


public class DecodeRawTx {
//    @Test
    public void testDecodeRawTx(){
        String signedMessage = "0xf9016d82135d85051f4d5c0083663be0948835ca4b43866e96b395ad8114f81f5beb4c057f80b901046c1533480000000000000000" +
                "0000000000000000000000000000000000000000000000a0000000000000000000000000e3a5d7cdd1a0ad4b4ab0a791a6f08e379697a33c000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010" +
                "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002e516d62444a57713542346e33747639753871626133326971317373334858623132736367486d7666755a6a6a39420000000000000000000000" +
                "000000000000001ba08a41efd9c53428984b7aa6e97b9f479e877faede8e919c3a0fc44b2921495980a0575998eff22fb72b7ce924c1c78a04b8a4ddd4821f43c944882afdf9d2f5edaa";
        RawTransaction tx;
        try {
            tx = TransactionDecoder.decode(signedMessage);
            if (tx instanceof SignedRawTransaction) {
                String from;
                int chain;
                try {
                    from = ((SignedRawTransaction) tx).getFrom();
                    Integer chainId = ((SignedRawTransaction) tx).getChainId();
                    chain = null != chainId ? chainId : 0;

                    System.out.println("from:" + from);
                    System.out.println("chain:" + chain);
                    System.out.println("gasPrice:" + tx.getGasPrice());
                    System.out.println("gasLimit:" + tx.getGasLimit());
                    System.out.println("to:" + tx.getTo());
                    System.out.println("nonce:" + tx.getNonce());
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    @Test
    public void testDecodeRawTx2(){
        System.out.println("-----------------2---------------");
        String signedMessage = "0xf86d808504a817c80083015f9094876eabf441b2ee5b5b0554fd502a8e0600950cfa888317a0bb5c410c00001ba09d34fcf061a7156740e9fadb1b33718e479e08592c6e8589b768db81baff51e8a05d64d6691e45f272f08031a0533676afd3042f76bfcc9637d828a1d7de1fd59d";
        RawTransaction tx;
        try {
            tx = TransactionDecoder.decode(signedMessage);

            if (tx instanceof SignedRawTransaction) {
                String from;
                int chain;
                try {
                    from = ((SignedRawTransaction) tx).getFrom();
                    Integer chainId = ((SignedRawTransaction) tx).getChainId();
                    chain = null != chainId ? chainId : 0;

                    System.out.println("from:" + from);
                    System.out.println("chain:" + chain);
                    System.out.println("gasPrice:" + tx.getGasPrice());
                    System.out.println("gasLimit:" + tx.getGasLimit());
                    System.out.println("to:" + tx.getTo());
                    System.out.println("nonce:" + tx.getNonce());
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }
}

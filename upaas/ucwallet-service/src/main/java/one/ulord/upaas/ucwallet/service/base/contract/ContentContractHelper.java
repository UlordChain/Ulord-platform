/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * ContentContract helper
 *
 * @author chenxin
 * @since 2018-08-14
 */
@Component
public class ContentContractHelper implements TransactionActionHandler {

    @Autowired
    private Provider provider;

    private ContentContract contentContract;

    @PostConstruct
    public void init() throws Exception {
        if (contentContract == null) {
            try {
                contentContract = new ContentContract(provider.getUlordProvider(),provider.getTokenAddress(),provider.getCandyAddress(),
                        provider.getPublishAddress(),provider.getKeystoreFile(),provider.getKeystorePassword(),this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            }
        }
    }


    public ContentContract getContentContract(){
        return this.contentContract;
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

/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.contract;

import one.ulord.upaas.ucwallet.service.base.contract.generates.BridgeContract;
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
public class ContractHelper{

    @Autowired
    private Provider provider;

    private CommonContract commonContract;


    @PostConstruct
    public void init() throws Exception {
        if (commonContract == null) {
            try {
                commonContract = new CommonContract(provider.getUlordProvider());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public CommonContract getContentContract(){
        return this.commonContract;
    }

    public String getFedAddress() throws Exception {
        return commonContract.getUTFedAddress(provider.getFedContractAddress());
    }
}

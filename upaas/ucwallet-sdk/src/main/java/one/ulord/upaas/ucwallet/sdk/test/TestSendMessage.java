/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.test;

import one.ulord.upaas.ucwallet.sdk.ContentContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigInteger;

/**
 * Test Controller
 *
 * @author  chenxin
 * @since  2018/8/12
 */
@RestController
@RequestMapping("/test")
public class TestSendMessage {

    final Logger logger = LoggerFactory.getLogger(TestSendMessage.class);

    @Autowired
    private ContentContract cc;

    /**
     * Test transferGas
     * http://127.0.0.1:9091/sdk/test/transferGas
     */
    @GetMapping("/transferGas")
    public void transferGas(@RequestParam String reqId,String toAddress,String value) {
        cc.transferGas(reqId, toAddress,new BigInteger(value));
    }


    /**
     * Test transferToken
     * http://127.0.0.1:9091/sdk/test/transferToken
     */
    @GetMapping("/transferToken")
    public void transferToken(@RequestParam String reqId,String toAddress,String value) {
        cc.transferToken(reqId, toAddress,new BigInteger(value));
    }


    /**
     * Test publishResource
     * http://127.0.0.1:9091/sdk/test/publishResource
     */
    @GetMapping("/publishResource")
    public void publishResource(@RequestParam String reqId,String toAddress,String value) {
        cc.publishResource(reqId, toAddress,new BigInteger(value), BigInteger.ZERO);
    }

}

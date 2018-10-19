/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.test;

import one.ulord.upaas.ucwallet.sdk.ContentContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void transferGas(@RequestParam String reqId, String toAddress, String value) {
        cc.transferGas(reqId, toAddress, new BigInteger(value));
    }


    /**
     * Test transferToken
     * http://127.0.0.1:9091/sdk/test/transferToken
     */
    @GetMapping("/transferToken")
    public void transferToken(@RequestParam String reqId, String toAddress, String value) {
        cc.transferToken(reqId, toAddress, new BigInteger(value));
    }


    /**
     * Test transferTokenList
     * http://127.0.0.1:9091/sdk/test/transferTokenList
     */
    @PostMapping("/transferTokenList")
    public void transferTokenList() {
        String reqId = "123456";
        String value = "1000000000000";
        List<String> toAddressList = new ArrayList<>();
        toAddressList.add("0x4826f115806862afcf2fbafb8ac69e61481426f6");
        toAddressList.add("0x02f5156e457c55a55dc5699d03d3927fc158fdf7");
        cc.transferTokenList(reqId, toAddressList,new BigInteger(value));
    }


    /**
     * Test publishResource
     * http://127.0.0.1:9091/sdk/test/publishResource
     */
    @PostMapping("/publishResource")
    public void publishResource(@RequestParam String reqId, String authorAddress, String value,String udfsHash) {
        cc.publishResource(reqId, authorAddress, new BigInteger(value), udfsHash);
    }

}
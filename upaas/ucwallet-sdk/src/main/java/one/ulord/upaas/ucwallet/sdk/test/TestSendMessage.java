/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.test;

import com.alibaba.fastjson.JSON;
import one.ulord.upaas.ucwallet.sdk.ContentContract;
import one.ulord.upaas.ucwallet.sdk.remote.SendRawTransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static one.ulord.upaas.ucwallet.sdk.contract.generates.UshareToken.FUNC_TRANSFER;

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
    private ContentContract contentContract;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RestTemplate restTemplate;

    @Value("${ucwallet-service.mq.exchange-req}")
    private String exchangeReq;

    @Value("${ucwallet-service.mq.routing-key}")
    private String routingKey;

    /**
     * Test transferGas
     * http://127.0.0.1:9091/sdk/test/transferGas
     */
    @GetMapping("/transferGas")
    public String transferSut(@RequestParam String reqId, @RequestParam String toAddress, @RequestParam String value) throws IOException {
        String signedRawTransaction = contentContract.transferSut(toAddress,
                new BigDecimal(value).multiply(BigDecimal.TEN.pow(18)).toBigInteger());
        SendRawTransactionRequest rawTransactionRequest = new SendRawTransactionRequest(reqId, signedRawTransaction);
        String message = JSON.toJSONString(rawTransactionRequest);
        amqpTemplate.convertAndSend(exchangeReq, routingKey, message);
        logger.trace("Success to send a transfer {} SUT to {} message to service.", value, toAddress);

        return message;
    }


    /**
     * Test transferToken
     * http://127.0.0.1:9091/sdk/test/transferToken
     */
    @GetMapping("/transferToken")
    public String transferToken(@RequestParam String reqId, String toAddress,String value) throws IOException {
        String signedRawTransaction = contentContract.transferToken(toAddress,
                new BigInteger(value).multiply(BigInteger.TEN).pow(18));
        SendRawTransactionRequest rawTransactionRequest = new SendRawTransactionRequest(reqId, signedRawTransaction);
        String message = JSON.toJSONString(rawTransactionRequest);
        amqpTemplate.convertAndSend(exchangeReq, routingKey, message);
        logger.trace("Success to send a transfer token {} SUT to {} message to service.", value, toAddress);

        return message;
    }


    /**
     * Test publishResource
     * http://127.0.0.1:9091/sdk/test/publishResource
     */
    @GetMapping("/publishResource")
    public String publishResource(@RequestParam String reqId, String udfsHash, String authorAddress, String price)
            throws IOException {
        String signedRawTransaction = contentContract.publishResource(udfsHash, authorAddress,
                new BigInteger(price).multiply(BigInteger.TEN).pow(18), false);
        SendRawTransactionRequest rawTransactionRequest = new SendRawTransactionRequest(reqId, signedRawTransaction);
        String message = JSON.toJSONString(rawTransactionRequest);
        amqpTemplate.convertAndSend(exchangeReq, routingKey, JSON.toJSONString(rawTransactionRequest));
        logger.trace("Success to public resource {} SUT for {} with price {} to service.",
                udfsHash, authorAddress, price);

        return message;
    }

}

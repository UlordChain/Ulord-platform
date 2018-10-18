/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk;

import one.ulord.upaas.ucwallet.sdk.utils.Constants;
import one.ulord.upaas.ucwallet.sdk.utils.Provider;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

/**
 * Content contract
 * Message use sender confirms asynchronous mode
 *
 * @author chenxin
 * @since 13/8/18
 */
@Component
public class ContentContract {

    final Logger logger = LoggerFactory.getLogger(ContentContract.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private Provider provider;


    /**
     * Transfer gas
     * @param reqId
     * @param toAddress
     * @param value
     */
    public void transferGas(final String reqId, String toAddress, BigInteger value){
        logger.info("send message of transferGas to queue ： "+Constants.QUEUE_TRANSFER_GAS );
        String message = reqId+"|"+toAddress+"|"+value+"|"+provider.getNodeName();
        logger.info("message is ："+message);

        // send message of transferGas to queue
        this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC,Constants.ROUTING_KEY_TRANSFER_GAS+"."+provider.getNodeName(),message);
    }


    /**
     * Transfer token
     * @param reqId
     * @param toAddress
     * @param quantity
     */
    public void transferToken(final String reqId, String toAddress, BigInteger quantity){
        logger.info("send message of transferToken token queue ："+Constants.QUEUE_TRANSFER_TOKEN);
        String message = reqId+"|"+toAddress+"|"+quantity+"|"+provider.getNodeName();
        logger.info("message is ："+message);

        // send message of transferToken to queue
        this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC,Constants.ROUTING_KEY_TRANSFER_TOKEN+"."+provider.getNodeName(),message);
    }


    /**
     * Transfer token list
     * @param reqId
     * @param toAddressList
     * @param value
     */
    public void transferTokenList(final String reqId, List<String> toAddressList, BigInteger value){
        logger.info("send message of transferTokenList token queue ："+Constants.QUEUE_TRANSFER_TOKEN_LIST);
        JSONObject msg = new JSONObject();
        msg.put("reqId", reqId);
        msg.put("toAddress", toAddressList);
        msg.put("quantity", value);
        msg.put("nodeName", provider.getNodeName());
        logger.info("message is ："+ msg);

        // send message of transferToken to queue
        this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC,Constants.ROUTING_KEY_TRANSFER_TOKEN_LIST+"."+provider.getNodeName(),msg.toString());
    }


    /**
     * Publish resource
     * @param reqId
     * @param authorAddress
     * @param price
     * @param deposit
     */
    public void publishResource(final String reqId, String authorAddress, BigInteger price, BigInteger deposit){
        logger.info("send message of publishResource to queue："+Constants.QUEUE_PUBLISH_RESOURCE);
        try {
            logger.info("Connect to UDFS network...");
            UDFSClient udfsClient = new UDFSClient("/ip4/114.67.37.2/tcp/20418"); // Test UDFS network
            logger.info("Publish a sentence to UDFS ...");
            String udfsHash = udfsClient.publishResource("test",("Hello Ulord Platform" + Calendar.getInstance().toString()).getBytes());
            byte[] udfsContent = udfsClient.getContent(udfsHash);
            if (udfsContent != null) {
                logger.info(new String(udfsContent));
            } else {
                logger.info("Cannot get content from UDFS.");
            }
            logger.info("publish a resource " + udfsHash + " to ulord using address:"+authorAddress);

            String message = reqId+"|"+udfsHash+"|"+authorAddress+"|"+price+"|"+deposit+"|"+provider.getNodeName();
            logger.info("message is ："+message);

            // send message of publishResource to queue
            this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC,Constants.ROUTING_KEY_PUBLISH_RESOURCE+"."+provider.getNodeName(),message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

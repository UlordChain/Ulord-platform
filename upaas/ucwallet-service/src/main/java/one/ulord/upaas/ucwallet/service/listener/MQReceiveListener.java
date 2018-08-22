/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.listener;

import com.rabbitmq.client.Channel;
import one.ulord.upaas.ucwallet.service.base.common.Constants;
import one.ulord.upaas.ucwallet.service.base.common.RedisUtil;
import one.ulord.upaas.ucwallet.service.base.contract.ContentContract;
import one.ulord.upaas.ucwallet.service.base.contract.ContentContractHelper;
import one.ulord.upaas.ucwallet.service.base.contract.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigInteger;

/**
 * RabbitMQ Listener
 *
 * @author chenxin
 * @since 2018-08-13
 */
@Component
public class MQReceiveListener {

    private static final Logger logger = LoggerFactory.getLogger(MQReceiveListener.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ContentContractHelper contentContractHelper;

    @Autowired
    private Provider provider;

    /**
     * Listenning message of transfer gas
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "#{${mq.listener.transferGas}}")
    @RabbitHandler
    public void transferGasReceiver(final Message message, Channel channel) throws IOException {
        String m = new String(message.getBody());
        logger.info("======================  listener.transferGas......received a message:" + m);
        String[] mArr = m.split("\\|",-1);
        String reqId = mArr[0];
        String toAddress = mArr[1];
        String value = mArr[2];
        String dappKey = mArr[3];

        try{
            // message confirm
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            // remove key from redis , prevent repeated reads
            redisUtil.remove(reqId);

            // business handle
            ContentContract cc = contentContractHelper.getContentContract();
            String hash = cc.transferGas(toAddress,new BigInteger(value));
            logger.info("======================  listener.transferGas......reqId:"+reqId+", hash:" + hash);

            if(null != hash){
                // Reply mq message（the first time return hash value）
                String returnStr = "1|"+reqId+"|"+hash+"|"+dappKey;
                this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC,Constants.ROUTING_KEY_TRANSFER_GAS_BACK+"."+dappKey,returnStr);

                // Record second messages to redis
                String msg1 = "2|"+reqId+"|"+Constants.ROUTING_KEY_TRANSFER_GAS_BACK+"."+dappKey+"|"+provider.getQuerySleep1()+"|"+hash+"|"+dappKey;
                redisUtil.set("mq|"+reqId+"|2",msg1);

                // Record third messages to redis
                String msg2 = "3|"+reqId+"|"+Constants.ROUTING_KEY_TRANSFER_GAS_BACK+"."+dappKey+"|"+provider.getQuerySleep2()+"|"+hash+"|"+dappKey;
                redisUtil.set("mq|"+reqId+"|3",msg2);
            }

        }catch(Exception e){
            e.printStackTrace();
            // message nack
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
            logger.info("======================  listener.transferGas......reqId:"+reqId+", message isNacked. ");
        }
        logger.info("======================  listener.transferGas......reqId:"+reqId+", message isAcked. ");

    }


    /**
     * Listenning message of transfer token
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "#{${mq.listener.transferToken}}")
    @RabbitHandler
    public void transferTokenReceiver(final Message message, Channel channel) throws IOException {
        String m = new String(message.getBody());
        logger.info("======================  listener.transferToken......received a message:" + m);
        String[] mArr = m.split("\\|",-1);
        String reqId = mArr[0];
        String toAddress = mArr[1];
        String quantity = mArr[2];
        String dappKey = mArr[3];

        try{
            // message confirm
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            // business handle
            ContentContract cc = contentContractHelper.getContentContract();
            String hash = cc.transferToken(toAddress,new BigInteger(quantity));
            logger.info("======================  listener.transferToken......reqId:"+reqId+", hash:" + hash);

            if(null != hash){
                // Reply mq message（the first time return hash value）
                String returnStr = "1|"+reqId+"|"+hash+"|"+dappKey;
                this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC,Constants.ROUTING_KEY_TRANSFER_TOKEN_BACK+"."+dappKey,returnStr);

                // Record second messages to redis
                String msg1 = "2|"+reqId+"|"+Constants.ROUTING_KEY_TRANSFER_TOKEN_BACK+"."+dappKey+"|"+provider.getQuerySleep1()+"|"+hash+"|"+dappKey;
                redisUtil.set("mq|"+reqId+"|2",msg1);

                // Record third messages to redis
                String msg2 = "3|"+reqId+"|"+Constants.ROUTING_KEY_TRANSFER_TOKEN_BACK+"."+dappKey+"|"+provider.getQuerySleep2()+"|"+hash+"|"+dappKey;
                redisUtil.set("mq|"+reqId+"|3",msg2);
            }
        }catch(Exception e){
            e.printStackTrace();
            // message nack
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
            logger.info("======================  listener.transferToken......reqId:"+reqId+", message isNacked. ");
        }
        logger.info("======================  listener.transferToken......reqId:"+reqId+", message isAcked. ");
    }


    /**
     * Listenning message of publish resource
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = "#{${mq.listener.publishResource}}")
    @RabbitHandler
    public void publishResourceReceiver(final Message message, Channel channel) throws IOException {
        String m = new String(message.getBody());
        logger.info("======================  listener.publishResource......received a message:" + m);
        String[] mArr = m.split("\\|", -1);
        String reqId = mArr[0];
        String udfsHash = mArr[1];
        String authorAddress = mArr[2];
        String price = mArr[3];
        String deposit = mArr[4];
        String dappKey = mArr[5];

        try {
            // message confirm
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            // business handle
            ContentContract cc = contentContractHelper.getContentContract();
            String hash = cc.publishResource(udfsHash, authorAddress, new BigInteger(price), new BigInteger(deposit));
            logger.info("======================  listener.publishResource...... reqId:" + reqId + ", hash:" + hash);

            if (null != hash) {
                // Reply mq message（the first time return hash value）
                String returnStr = "1|" + reqId + "|" + hash+"|"+dappKey;
                this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC, Constants.ROUTING_KEY_PUBLISH_RESOURCE_BACK+"."+dappKey, returnStr);

                // Record second messages to redis
                String msg1 = "2|" + reqId + "|" + Constants.ROUTING_KEY_PUBLISH_RESOURCE_BACK+"."+dappKey + "|" + provider.getQuerySleep1() + "|" + hash+"|"+dappKey;
                redisUtil.set("mq|" + reqId + "|2", msg1);

                // Record third messages to redis
                String msg2 = "3|" + reqId + "|" + Constants.ROUTING_KEY_PUBLISH_RESOURCE_BACK+"."+dappKey + "|" + provider.getQuerySleep2() + "|" + hash+"|"+dappKey;
                redisUtil.set("mq|" + reqId + "|3", msg2);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // message nack
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            logger.info("======================  listener.publishResource......reqId:" + reqId + ", message isNacked. ");
        }
        logger.info("======================  listener.publishResource......reqId:" + reqId + ", message isAcked. ");
    }

}
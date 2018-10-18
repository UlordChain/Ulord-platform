/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.listener;

import com.rabbitmq.client.Channel;
import one.ulord.upaas.ucwallet.sdk.utils.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Listenning message of RabbitMQ
 *
 * @author chenxin
 * @since 13/8/18
 */
@Component
public class MQReceiveListener {

    final Logger logger = LoggerFactory.getLogger(MQReceiveListener.class);

    @Autowired
    private IReceiveMessage iReceiveMessage;


    /**
     * Listenning results of transfer gas
     * @param message
     * @param channel
     */
    @RabbitListener(queues = "#{"+"'transferGas-back."+"${mq.dapp.node.name}'}")
    @RabbitHandler
    public void transferGasBackReceiver(final Message message, Channel channel) throws IOException {
        String m = new String(message.getBody());
        String[] mArr = m.split("\\|",-1);
        String type = mArr[0];
        String reqId = mArr[1];
        String value = mArr[2];
        String dappKey = mArr[3];
        logger.info("");
        logger.info("======================  Listener.transferGasBack......type:"+type+",reqId:"+reqId+",value:"+value+",dappKey:"+dappKey);

        MessageType messageType = null;
        if(type.equals("1")){
            messageType = MessageType.FIRST;
        }
        if(type.equals("2")){
            messageType = MessageType.SECOND;
        }
        if(type.equals("3")){
            messageType = MessageType.THIRD;
        }

        try{
            // message handle
            iReceiveMessage.handleTransferGas(messageType,reqId,value);
            // message confirm
            if(channel.isOpen()){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }catch(Exception e){
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
        }

    }


    /**
     * Listenning results of transfer token
     * @param message
     * @param channel
     */
    @RabbitListener(queues = "#{"+"'transferToken-back."+"${mq.dapp.node.name}'}")
    @RabbitHandler
    public void transferTokenBackReceiver(final Message message, Channel channel) throws IOException {
        String m = new String(message.getBody());
        String[] mArr = m.split("\\|",-1);
        String type = mArr[0];
        String reqId = mArr[1];
        String value = mArr[2];
        String dappKey = mArr[3];
        logger.info("");
        logger.info("======================  Listener.transferTokenBack......type:"+type+",reqId:"+reqId+",value:"+value+",dappKey:"+dappKey);

        MessageType messageType = null;
        if(type.equals("1")){
            messageType = MessageType.FIRST;
        }
        if(type.equals("2")){
            messageType = MessageType.SECOND;
        }
        if(type.equals("3")){
            messageType = MessageType.THIRD;
        }

        try{
            // message handle
            iReceiveMessage.handleTransferToken(messageType,reqId,value);
            // message confirm
            if(channel.isOpen()){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }catch(Exception e){
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
        }
    }


    /**
     * Listenning results of transfer token list
     * @param message
     * @param channel
     */
    @RabbitListener(queues = "#{"+"'transferTokenList-back."+"${mq.dapp.node.name}'}")
    @RabbitHandler
    public void transferTokenListBackReceiver(final Message message, Channel channel) throws IOException {
        String m = new String(message.getBody());
        String[] mArr = m.split("\\|",-1);
        String type = mArr[0];
        String reqId = mArr[1];
        String value = mArr[2];
        String dappKey = mArr[3];
        logger.info("");
        logger.info("======================  Listener.transferTokenBack......type:"+type+",reqId:"+reqId+",value:"+value+",dappKey:"+dappKey);

        MessageType messageType = null;
        if(type.equals("1")){
            messageType = MessageType.FIRST;
        }
        if(type.equals("2")){
            messageType = MessageType.SECOND;
        }
        if(type.equals("3")){
            messageType = MessageType.THIRD;
        }

        try{
            // message handle
            iReceiveMessage.handleTransferTokenList(messageType,reqId,value);
            // message confirm
            if(channel.isOpen()){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }catch(Exception e){
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
        }
    }


    /**
     * Listenning results of publishing resources
     * @param message
     * @param channel
     */
    @RabbitListener(queues = "#{"+"'publishResource-back."+"${mq.dapp.node.name}'}")
    @RabbitHandler
    public void publishResourceBackReceiver(final Message message, Channel channel) throws IOException {
        String m = new String(message.getBody());
        String[] mArr = m.split("\\|",-1);
        String type = mArr[0];
        String reqId = mArr[1];
        String value = mArr[2];
        String dappKey = mArr[3];
        logger.info("");
        logger.info("======================  Listener.publishResourceBack......type:"+type+",reqId:"+reqId+",value:"+value+",dappKey:"+dappKey);

        MessageType messageType = null;
        if(type.equals("1")){
            messageType = MessageType.FIRST;
        }
        if(type.equals("2")){
            messageType = MessageType.SECOND;
        }
        if(type.equals("3")){
            messageType = MessageType.THIRD;
        }

        try{
            // message handle
            iReceiveMessage.handlePublishResource(messageType,reqId,value);
            // message confirm
            if(channel.isOpen()){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }catch(Exception e){
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
        }


    }

}
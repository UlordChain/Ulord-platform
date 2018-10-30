/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.rabbitmq.client.Channel;
import one.ulord.upaas.ucwallet.sdk.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
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
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ucwallet-service.mq.sendrawtx-resp}", durable = "true"),
            exchange = @Exchange(value = "${ucwallet-service.mq.exchange-resp}", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${ucwallet-service.mq.routing-key}"))
    public void transferGasBackReceiver(final Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());

        try{
            // message handle
            JSONObject jsonObject = JSONObject.parseObject(msg);
            String messageType = jsonObject.getString("type");

            switch (MQMessageEnum.valueOf(messageType)){
                case RESPONSE:
                    SendRawTransactionResponse transactionResponse = JSON.parseObject(msg, SendRawTransactionResponse.class);
                    iReceiveMessage.handleResponse(transactionResponse);
                    break;
                case CONFIRM:
                    SendRawTransactionConfirm transactionConfirm = JSON.parseObject(msg, SendRawTransactionConfirm.class);
                    iReceiveMessage.handleResponse(transactionConfirm);
                    break;
                case DBLCONFIRM:
                    SendRawTransactionDblConfirm transactionDblConfirm = JSON.parseObject(msg, SendRawTransactionDblConfirm.class);
                    iReceiveMessage.handleResponse(transactionDblConfirm);
                    break;
                case ERROR:
                    MQErrorMessage errorMessage = JSONObject.parseObject(msg, MQErrorMessage.class);
                    iReceiveMessage.handleResponse(errorMessage);
                    break;
            }
            // message confirm
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch(Exception e){
            logger.warn("Cannot process message:", e);
        }

    }
}
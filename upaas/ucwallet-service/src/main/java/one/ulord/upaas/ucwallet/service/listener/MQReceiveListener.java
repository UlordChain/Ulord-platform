/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import one.ulord.upaas.ucwallet.service.base.common.Constants;
import one.ulord.upaas.ucwallet.service.base.common.RedisUtil;
import one.ulord.upaas.ucwallet.service.base.common.SendRawTransactionRequest;
import one.ulord.upaas.ucwallet.service.base.contract.CommonContract;
import one.ulord.upaas.ucwallet.service.base.contract.ContractHelper;
import one.ulord.upaas.ucwallet.service.base.contract.Provider;
import one.ulord.upaas.ucwallet.service.service.TransactionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigInteger;

/**
 * RabbitMQ Listener
 * Receive a message from message queue
 * @author chenxin, yinhaibo
 * @since 2018-08-13
 */
@Component
public class MQReceiveListener {

    private static final Logger logger = LoggerFactory.getLogger(MQReceiveListener.class);

    @Autowired
    TransactionTracker transactionTracker;

    /**
     * execute raw transaction
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ucwallet-service.mq.sendrawtx-req}", durable = "true"),
            exchange = @Exchange(value = "${ucwallet-service.mq.exchange-req}", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "${ucwallet-service.mq.binding-key}"))
    public void executeRawTransaction(final Message message, Channel channel,
                                      @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String receiveRoutingKey) throws IOException {
        String msg = new String(message.getBody());
        logger.info("received a message:{} from routing key:{}", msg, receiveRoutingKey);


        try{
            SendRawTransactionRequest request = JSON.parseObject(msg, SendRawTransactionRequest.class);
            transactionTracker.submitRawTransaction(request, receiveRoutingKey);

            // message confirm
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch(Exception e){
            logger.warn("Process mq message failed:", e);
        }
    }




}
/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.utils;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * init rabbitmq queue and bind routingKey
 *
 * @author chenxin
 * @since 13/8/18
 */
@Configuration
public class TopicRabbitConfig {

    @Autowired
    private Provider provider;

    @Bean
    public Queue queueTransferGas() {
        return new Queue(Constants.QUEUE_TRANSFER_GAS+"."+provider.getNodeName());
    }
    @Bean
    public Queue queueTransferGasBack() {
        return new Queue(Constants.QUEUE_TRANSFER_GAS_BACK+"."+provider.getNodeName());
    }

    @Bean
    public Queue queueTransferToken() {
        return new Queue(Constants.QUEUE_TRANSFER_TOKEN+"."+provider.getNodeName());
    }
    @Bean
    public Queue queueTransferTokenBack() {
        return new Queue(Constants.QUEUE_TRANSFER_TOKEN_BACK+"."+provider.getNodeName());
    }

    @Bean
    public Queue queuePublishResource() {
        return new Queue(Constants.QUEUE_PUBLISH_RESOURCE+"."+provider.getNodeName());
    }
    @Bean
    public Queue queuePublishResourceBack() {
        return new Queue(Constants.QUEUE_PUBLISH_RESOURCE_BACK+"."+provider.getNodeName());
    }


    @Bean
    TopicExchange exchange() {
        return new TopicExchange(Constants.EXCHANGE_TOPIC);
    }


    @Bean
    Binding bindingExchangeTransferGas(Queue queueTransferGas, TopicExchange exchange) {
        return BindingBuilder.bind(queueTransferGas).to(exchange).with(Constants.ROUTING_KEY_TRANSFER_GAS+"."+provider.getNodeName());
    }
    @Bean
    Binding bindingExchangeTransferGasBack(Queue queueTransferGasBack, TopicExchange exchange) {
        return BindingBuilder.bind(queueTransferGasBack).to(exchange).with(Constants.ROUTING_KEY_TRANSFER_GAS_BACK+"."+provider.getNodeName());
    }

    @Bean
    Binding bindingExchangeTransferToken(Queue queueTransferToken, TopicExchange exchange) {
        return BindingBuilder.bind(queueTransferToken).to(exchange).with(Constants.ROUTING_KEY_TRANSFER_TOKEN+"."+provider.getNodeName());
    }
    @Bean
    Binding bindingExchangeTransferTokenBack(Queue queueTransferTokenBack, TopicExchange exchange) {
        return BindingBuilder.bind(queueTransferTokenBack).to(exchange).with(Constants.ROUTING_KEY_TRANSFER_TOKEN_BACK+"."+provider.getNodeName());
    }

    @Bean
    Binding bindingExchangePublishResource(Queue queuePublishResource, TopicExchange exchange) {
        return BindingBuilder.bind(queuePublishResource).to(exchange).with(Constants.ROUTING_KEY_PUBLISH_RESOURCE+"."+provider.getNodeName());
    }
    @Bean
    Binding bindingExchangePublishResourceBack(Queue queuePublishResourceBack, TopicExchange exchange) {
        return BindingBuilder.bind(queuePublishResourceBack).to(exchange).with(Constants.ROUTING_KEY_PUBLISH_RESOURCE_BACK+"."+provider.getNodeName());
    }

}

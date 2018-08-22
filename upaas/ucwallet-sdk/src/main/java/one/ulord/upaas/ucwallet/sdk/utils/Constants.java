/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.utils;

public class Constants {

    // RabbitMQ EXCHANGE
    public final static String EXCHANGE_TOPIC = "topic_exchanges";

    // RabbitMQ QUEUE
    public final static String QUEUE_TRANSFER_GAS = "transferGas";
    public final static String QUEUE_TRANSFER_GAS_BACK = "transferGas-back";
    public final static String QUEUE_TRANSFER_TOKEN = "transferToken";
    public final static String QUEUE_TRANSFER_TOKEN_BACK = "transferToken-back";
    public final static String QUEUE_PUBLISH_RESOURCE = "publishResource";
    public final static String QUEUE_PUBLISH_RESOURCE_BACK = "publishResource-back";

    // RabbitMQ OUTING_KEY
    public final static String ROUTING_KEY_TRANSFER_GAS = "transferGas-routingKey";
    public final static String ROUTING_KEY_TRANSFER_TOKEN = "transferToken-routingKey";
    public final static String ROUTING_KEY_PUBLISH_RESOURCE = "publishResource-routingKey";
    public final static String ROUTING_KEY_TRANSFER_GAS_BACK = "transferGas-back-routingKey";
    public final static String ROUTING_KEY_TRANSFER_TOKEN_BACK  = "transferToken-back-routingKey";
    public final static String ROUTING_KEY_PUBLISH_RESOURCE_BACK = "publishResource-back-routingKey";

}

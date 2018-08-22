/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.common;

/**
 * 常量工具类
 *
 * @author chenxin
 * @since 2018-08-10
 */
public class Constants {

	public static final String CONTENT_TYPE = "application/json; charset=utf-8";
	
	public static final String CHARACTER_ENCODING = "utf-8";

    // sessionId
    public static final String REDIS_MQ_MESSAGES = "redisMqMessages";
	
	// sessionId
	public static final String JSESSIONID = "jsessionid";

	// 返回成功
	public static final int SUCCESSFUL = 0;
    // 无数据
    public static final int NODATA = 1;
    // 超时
    public static final int TIMEOUT = 2;
    // 参数错误
    public static final int VALIDATE_ERROR = 3;
    // 系统异常
    public static final int FAILURE = 9;


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

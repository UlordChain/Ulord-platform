# ucwallet-sdk接入文档

## **1、接入说明**

​	ucwallet-sdk，要求DAPP使用springboot框架。

## 2、JAR包说明

2.1 软件要求

| **软件** | 版本                                                         |
| -------- | ------------------------------------------------------------ |
| JDK      | 1.8 以上版本                                                 |
| RabbitMQ | rabbitmq-server-3.7.4-1.el7.noarch.rpm ，erlang-19.3.6.8-1.el7.centos.x86_64.rpm |

2.2 jar包

| **类型** | 说明                          |
| -------- | ----------------------------- |
| 部署包   | ucwallet-sdk-1.0-SNAPSHOT.jar |
| 编码     | UTF-8                         |

## 3、接入流程

**步骤一：**

DAPP加入ucwallet-sdk-1.0-SNAPSHOT.jar依赖

**步骤二：**

在application.properties 配置文件中加入以下配置项

| **配置项**                         | **值**         | **配置说明** |
| ---------------------------------- | -------------- | ------------ |
| #Dapp服务器节点名称                |                |              |
| mq.dapp.node.name                  | node1          | 节点名称     |
|                                    |                |              |
| #rabbitmq配置                      |                |              |
| spring.rabbitmq.host               | 192.168.12.245 | 地址         |
| spring.rabbitmq.port               | 5672           | 端口         |
| spring.rabbitmq.username           | admin          | 用户名       |
| spring.rabbitmq.password           | 12345678       | 密码         |
| spring.rabbitmq.publisher-confirms | true           | 配置确认机制 |

**步骤三：**

加入依赖

gradle

```
compile ('org.web3j:core:3.4.0')
compile ('com.github.ipfs:java-ipfs-api:v1.2.0')
compile group: 'org.springframework.boot', name: 'spring-boot-starter-web', version: '1.5.8.RELEASE'
compile group: 'org.springframework.boot', name: 'spring-boot-starter-actuator', version: '1.5.8.RELEASE'
compile group: 'org.springframework.boot', name: 'spring-boot-starter-tomcat', version: '1.5.8.RELEASE'
compile group: 'org.springframework.boot', name: 'spring-boot-starter-amqp', version: '1.5.8.RELEASE'
```

maven

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
       <groupId>org.web3j</groupId>
       <artifactId>core</artifactId>
       <version>3.4.0</version>
    </dependency>
    <dependency>
       <groupId>com.github.ipfs</groupId>
       <artifactId>java-ipfs-api</artifactId>
       <version>v1.2.0</version>
    </dependency>
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
       <version>1.5.8.RELEASE</version>
    </dependency>
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
       <version>1.5.8.RELEASE</version>
    </dependency>
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-tomcat</artifactId>
       <version>1.5.8.RELEASE</version>
    </dependency>
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
       <version>1.5.8.RELEASE</version>
    </dependency>
</dependencies> 
```



## 4、业务流程

**步骤一：**

DAPP调用SDK的业务方法（如transferGas），并带上业务reqId，SDK会自动往RabbitMQ中添加一条待处理的业务消息。

```
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
    public void transferGas(@RequestParam String reqId,String toAddress,String value) {
        cc.transferGas(reqId, toAddress,new BigInteger(value));
    }


    /**
     * Test transferToken
     * http://127.0.0.1:9091/sdk/test/transferToken
     */
    @GetMapping("/transferToken")
    public void transferToken(@RequestParam String reqId,String toAddress,String value) {
        cc.transferToken(reqId, toAddress,new BigInteger(value));
    }


    /**
     * Test publishResource
     * http://127.0.0.1:9091/sdk/test/publishResource
     */
    @GetMapping("/publishResource")
    public void publishResource(@RequestParam String reqId,String toAddress,String value) {
        cc.publishResource(reqId, toAddress,new BigInteger(value), BigInteger.ZERO);
    }

}
```

**步骤二：**

ucwallet-service会处理RabbitMQ中的消息，并立即回复一个hash值给sdk，这时需要DAPP实现sdk的IReceiveMessage接口来接收hash值。

30秒后ucwallet-service会发送第一条确认消息，这时需要DAPP第二次通过接口来进行处理。

3分钟后ucwallet-service会发送第二条确认消息，这时需要DAPP第三次通过接口来进行处理。

```
/**
 * Test Business implementation
 *
 * @author chenxin
 * @since 14/8/18
 */
@Component
public class TestReceiveMessageImpl implements IReceiveMessage{

    final Logger logger = LoggerFactory.getLogger(TestReceiveMessageImpl.class);

    /**
     * Receive results of message by transfer gas , and handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     */
    public void handleTransferGas(String type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handleTransferGas......type:"+type+",reqId:"+reqId+",value:"+value);
    }

    /**
     * Receive results of message by transfer token , and handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     */
    public void handleTransferToken(String type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handleTransferToken......type:"+type+",reqId:"+reqId+",value:"+value);
    }

    /**
     * Receive results of message by publish resource , and handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     */
    public void handlePublishResource(String type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handlePublishResource......type:"+type+",reqId:"+reqId+",value:"+value);
    }


}
```






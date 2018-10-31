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

DAPP加入ucwallet-sdk-<VERSION>.jar依赖

**步骤二：**
application.properties指定了当前采用的profile版本。
在application-<profile>.properties 配置文件中加入以下配置项

| **配置项**                         | **值**         | **配置说明** |
| ---------------------------------- | -------------- | ------------ |
|server.port|9091|服务端口（测试用）|
|ucwallet-sdk.test.token-address|0xbc353d8cc6c73d95f2ec59573d1f47ed7f12e922|代币地址（测试网）|
|ucwallet-sdk.test.center-publish-address|0x39f2eaf366b713ead8396202fb96779d8da46330|内容发布合约地址（测试网）|
|ucwallet-sdk.test.multransfer-address|0xee9b6a4060c3e68259a58725fe93982f994cb5e9|批量发代币合约地址(测试网络)|
|ucwallet-sdk.test.keystore-file|keystore/no6.keystore|Ulord USC的密钥文件|
|ucwallet-sdk.test.keystore-password|12345678|密钥密码|
|ucwallet-service.http|http://127.0.0.1:9090/ucwallet-service|ucwallet-service API接口，用于查询交易序号|
|                                    |                |              |
|rabbitmq配置                      |                |              |
|spring.rabbitmq.host               | 192.168.12.245 | RabbitMQ服务器地址         |
|spring.rabbitmq.port               | 5672           | RabbitMQ服务器端口         |
|spring.rabbitmq.username           | admin          | RabbitMQ服务器用户名       |
|spring.rabbitmq.password           | 12345678       | RabbitMQ服务器密码         |
|spring.rabbitmq.publisher-confirms | true           | RabbitMQ服务器确认机制 |
|spring.rabbitmq.listener.simple.acknowledge-mode|manual|RabbitMQ监听消息的确认模式，要求配置未手动|
||||
|ucwallet-service.mq.exchange-req|ucwallet-service-exchange-req|RabbitMQ交换机名称，用于请求转发|
|ucwallet-service.mq.exchange-resp|ucwallet-service-exchange-resp|RabbitMQ交换机名称，用于响应转发|
|ucwallet-service.mq.sendrawtx-req|ucwallet-service-rawtx-req|RabbitMQ消息队列名称，用于原始交易请求|
|ucwallet-service.mq.sendrawtx-resp|ucwallet-service-rawtx-resp|RabbitMQ消息队列名称，用于原始交易执行响应|
|ucwallet-service.mq.routing-key|rawtx.1|RabbitMQ消息的路由键，如果存在多个模块，可以采用不同的序号，服务端通过rawtx.*接收消息|
||||
|logging.config|classpath:log4j2.xml|日志文件配置|
||||
|udfs.gateway|/dns4/udfs1.ulord.one/tcp/5001|UDFS API网关，用于上传，下载内容|

Ulord USC的Ulord私钥到USC侧链私钥和Keystore文件转换工具地址（可离线使用）：
http://usc.ulord.one:8088/

**步骤三：**

启动ucwallet-service, 文档参见[ucwallet-service](../ucwallet-service)



** 步骤四：**
启动ucwallet-sdk, 通过访问测试接口执行交易，并查看日志，检查交易执行的二次确认过程。
例如，在测试网络上执行一笔转账交易，会出现如下日志：
执行如下请求:
http://127.0.0.1:9091/test/transferGas?reqId=2&toAddress=0xd38e4650a069209d9629938fa95f540c31678818&value=0.01
产生日志：

```
20:21:40.494 [SimpleAsyncTaskExecutor-1] INFO  one.ulord.upaas.ucwallet.sdk.test.TestReceiveMessageImpl:67 - DEMO: reqId:2 has submit, txHash is 0x8d50a007424efe040726b07604a813fcba9d384e96e240b3c86da1034fa97104.
20:21:40.495 [SimpleAsyncTaskExecutor-1] INFO  one.ulord.upaas.ucwallet.sdk.test.TestReceiveMessageImpl:53 - DEMO: reqId:2 has confirmed, txHash is 0x8d50a007424efe040726b07604a813fcba9d384e96e240b3c86da1034fa97104, status is true.
20:21:40.496 [SimpleAsyncTaskExecutor-1] INFO  one.ulord.upaas.ucwallet.sdk.test.TestReceiveMessageImpl:60 - DEMO: reqId:2 has confirmed, txHash is 0x8d50a007424efe040726b07604a813fcba9d384e96e240b3c86da1034fa97104, 3 blocks has confirmed.
```
上面日志说明：
第一条日志：通过RabbitMQ提交原始交易到ucwallet-service后，交易成功提交到链，返回交易hash。
第二条日志：ucwallet-service检查到区块中已经存在上面的交易，把交易的状态发送回，并告知交易的执行状态。
第三条日志：ucwallet-service不断检查区块，如果区块达到二次确认的条件，交易仍然可以从链上查询到，交易得到确认。

**注意**
如果ucwallet-service在提交交易前会检查发送地址的余额是否足够，或者nonce是否在允许范围内（已经确认的交易数量到最大交易之间），
否则会送错误的消息响应。
如果ucwallet-service检查到交易在确认的区块仍没有打包，会继续把交易提交到链，重复执行。（该问题主要时区块链节点没有义务
保存用户排队的交易，会在超期后丢弃，ucwallet-service可以检查这个问题，并尝试重发多次）。








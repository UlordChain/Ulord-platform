# ucwallet-sdk
ucwallet-sdk is a Java SDK package for Ulord USC to execute transaction.

[中文](./ReadMe_zh.md)

Current demo using ucwallet-service to execute transaction. BUT transaction must be signed by sdk program.
If you want to use smart contract, you can generate java code from sol file by web3j tools.

## Configuration
application.properties is want to select current profile configuration. application-<profile>.profiles include the configuration of program.
dev, test and prod is current list source code tree.


| **Configuration**                         | **Value**         | **Memo** |
| ---------------------------------- | -------------- | ------------ |
|server.port|9091|Service port|
|ucwallet-sdk.test.token-address|0xbc353d8cc6c73d95f2ec59573d1f47ed7f12e922|Ushare Token address (testnet)|
|ucwallet-sdk.test.center-publish-address|0x39f2eaf366b713ead8396202fb96779d8da46330|Content contract(testnet)|
|ucwallet-sdk.test.multransfer-address|0xee9b6a4060c3e68259a58725fe93982f994cb5e9|Batch transfer token contract(testnet)|
|ucwallet-sdk.test.keystore-file|keystore/no6.keystore|Ulord USC's keystore|
|ucwallet-sdk.test.keystore-password|12345678|password|
|ucwallet-service.http|http://127.0.0.1:9090/ucwallet-service|ucwallet-service API, for query address transaction count|
|                                    |                |              |
|rabbitmq Configuration                      |                |              |
|spring.rabbitmq.host               | 192.168.12.245 | RabbitMQ host         |
|spring.rabbitmq.port               | 5672           | RabbitMQ port         |
|spring.rabbitmq.username           | admin          | RabbitMQ username       |
|spring.rabbitmq.password           | 12345678       | RabbitMQ password         |
|spring.rabbitmq.publisher-confirms | true           | RabbitMQ ACK mode |
|spring.rabbitmq.listener.simple.acknowledge-mode|manual|RabbitMQ Listener ACK mode, manual is ok|
||||
|ucwallet-service.mq.exchange-req|ucwallet-service-exchange-req|RabbitMQ exchange name for request|
|ucwallet-service.mq.exchange-resp|ucwallet-service-exchange-resp|RabbitMQ exchange name for response|
|ucwallet-service.mq.sendrawtx-req|ucwallet-service-rawtx-req|RabbitMQ queue name for request|
|ucwallet-service.mq.sendrawtx-resp|ucwallet-service-rawtx-resp|RabbitMQ queue name for response|
|ucwallet-service.mq.routing-key|rawtx.1|RabbitMQ message routing key, you can using different sequence for rawtx.<seq>, the ucwallet-service use rawtx.* to receive message|
||||
|logging.config|classpath:log4j2.xml|log4j configuration|
||||
|udfs.gateway|/dns4/udfs1.ulord.one/tcp/5001|UDFS API gateway for uploading and downloading|

Ulord USC's keystore file form private key, you can transfer from UT private key. The convert tools is ready for you:
(You can cut down network after page have loaded for safety)
http://usc.ulord.one:8088/

**Step 3**

Start ucwallet-service, Document please see [ucwallet-service](../ucwallet-service)



**Step 4**

After start ucwallet-sdk program, you can execute by testing API, and check log for executing process.
For example(Open it in you browser):
http://127.0.0.1:9091/test/transferGas?reqId=2&toAddress=0xd38e4650a069209d9629938fa95f540c31678818&value=0.01
The program will output:

```
20:21:40.494 [SimpleAsyncTaskExecutor-1] INFO  one.ulord.upaas.ucwallet.sdk.test.TestReceiveMessageImpl:67 - DEMO: reqId:2 has submit, txHash is 0x8d50a007424efe040726b07604a813fcba9d384e96e240b3c86da1034fa97104.
20:21:40.495 [SimpleAsyncTaskExecutor-1] INFO  one.ulord.upaas.ucwallet.sdk.test.TestReceiveMessageImpl:53 - DEMO: reqId:2 has confirmed, txHash is 0x8d50a007424efe040726b07604a813fcba9d384e96e240b3c86da1034fa97104, status is true.
20:21:40.496 [SimpleAsyncTaskExecutor-1] INFO  one.ulord.upaas.ucwallet.sdk.test.TestReceiveMessageImpl:60 - DEMO: reqId:2 has confirmed, txHash is 0x8d50a007424efe040726b07604a813fcba9d384e96e240b3c86da1034fa97104, 3 blocks has confirmed.
```
You can see:
First line：ucwallet-sdk submit a message by rabbitmq to ucwallet-service, the raw transaction has subumit to USC, and back a valid transaction hash.
Second line: ucwallet-service have checked the transaction above, and it has package in a block, the confirmation response send to ucwallet-sdk by message queue.
Three line: ucwallet-service have checked the transaction above have confirm by 3 blocks, the double confirmation response send to ucwallet-sdk.

**Note**
You may receive a error response if your address have no enough sUT or the nonce is invalid.
The ucwallet-service can re-execute transaction after some blocks while transaction was not package by USC.



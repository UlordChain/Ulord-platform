# ucwallet-service部署文档

## **1、简介**

​	ucwallet-service作为一个上链服务，实现了监听接收DAPP的服务请求，如（“执行交易gas”、执行交易token”、“发布资源”等），service监听到消息后会进行业务处理，并立即回复一个hash值给DAPP。service增加了两次确认的功能，返回hash值后默认30秒会返回第一次确认结果，3分钟后会返回第二次确认结果，时间可配置。
	另外service还提供了规范的http服务-基于restful 的API接口，供DAPP直接调用，比如查询余额、发送原始交易等功能都可以通过调用API直接来完成。

## 2、**部署说明**

2.1 系统及软件要求

| **软件** | **版本及包**                                                 |
| -------- | ------------------------------------------------------------ |
| JDK      | 1.8 以上版本                                                 |
| Tomcat   | 7 以上版本                                                   |
| RabbitMQ | rabbitmq-server-3.7.4-1.el7.noarch.rpm ，erlang-19.3.6.8-1.el7.centos.x86_64.rpm |
| Redis    | 3.2.1 以上版本                                               |

2.2 部署包

| **类型** | **版本及包**                                     |
| -------- | ------------------------------------------------ |
| 部署包   | ucwallet-service-1.0-SNAPSHOT.war                |
| 编码     | UTF-8                                            |
| 访问地址 | http://ip:port/ucwallet-service/swagger-ui.html  |
| 启动说明 | 先要启动一次ucwallet-sdk，启动时会自动创建Queues |

2.3 修改application.properties 

| **配置项**                           | **值**                                            | **配置说明**                                   |
| ------------------------------------ | ------------------------------------------------- | ---------------------------------------------- |
| #服务配置                           |                                                                               ||
| server.port                          | 9090          | 服务访问端口                               |
| server.context-path                  | /ucwallet-service                                 | 服务访问路径                               |
| spring.profiles.active               | dev                                               | 环境                                       |
|                                      |                                                   |                                                |
| #redis配置                          |                                                   |                                                |
| spring.redis.host                    | 192.168.12.222                                    | 地址                                           |
| spring.redis.password                | 123456 | 密码                                           |
| spring.redis.port                    | 6379                                              | 端口                                           |
| spring.redis.pool.max-idle           | 100                                               | 连接池中的最大空闲连接                         |
| spring.redis.pool.min-idle           | 1                                                 | 连接池中的最小空闲连接                         |
| spring.redis.pool.max-active         | 1000                                              | 连接池最大连接数（使用负值表示没有限制）       |
| spring.redis.pool.max-wait           | -1                                                | 连接池最大阻塞等待时间（使用负值表示没有限制） |
|                                      |                                                   |                                                |
| #定时任务配置                       |                                                   |                                                |
| task.mq.cron                         | */2 * * * * ?                                     | 每两秒执行一次定时任务，读取redis中未处理的消息记录，将两次确认的线程放入线程池中进行处理。 |
|                                      |                                                   |                                                |
| #rabbitmq配置                       |                                                   |                                                |
| spring.rabbitmq.host                 | 192.168.12.245                                    | 地址                                           |
| spring.rabbitmq.port                 | 5672                                              | 端口                                           |
| spring.rabbitmq.username             | admin                                             | 用户名                                         |
| spring.rabbitmq.password             | 12345678                                          | 密码                                           |
| spring.rabbitmq.publisher<br>-confirms | true                                              | 配置确认机制                                   |
|                                      |                                                   |                                                |
| #rabbitmq监听器配置                 |                                                   |                                                |
| mq.listener.transferGas              | {"transferGas.node1",<br>"transferGas.node2"}     | 监听器-Gas交易，支持多个节点配置               |
| mq.listener.transferToken            | {"transferToken.node1",<br>"transferToken.node2"} | 监听器-Token交易，支持多个节点配置             |
| mq.listener.publishResource          | {"publishResource.node1",<br>"publishResource.node2"} | 监听器-发布资源，支持多个节点配置              |
|                                      |                                                   |                                                |
| #侧链连接配置                       |                                                   |                                                |
| ulord.side.provider.ulordProvider    | http://192.168.12.231:80                          |                                                |
| ulord.side.provider.tokenAddress     | 0x33E8CAb23563Ef05194Ca<br>7c04213E0C8796cD8dF    |                                                |
| ulord.side.provider.candyAddress     | 0xb8dD202d141b3bF7361A<br>414C60e1Ee714fa6C4Cf    |                                                |
| ulord.side.provider.publishAddress   | 0xf95E268a6755D7099C9d0<br>8d9e53f48120572EC63    |                                                |
| ulord.side.provider.keystoreFile     | keystore/no6.keystore                             |                                                |
| ulord.side.provider.keystorePassword | 12345678                                          |                                                |
|                                      |                                                   |                                                |
| #确认等待时间 |                                                   |                                                |
| query.transaction.sleep1 | 30 | 第一次确认等待时间 |
| query.transaction.sleep2 | 180 | 第二次确认等待时间 |
|                                      |                                                   |                                                |
| #线程池配置 | | |
| executor.corePoolSize | 8 | 核心线程数 |
| executor.maxPoolSize | 500 | 最大线程数 |
| executor.keepAliveSeconds | 60 | 线程活跃时间（秒） |
| executor.queueCapacity | 0 | 线程池所使用的缓冲队列 |
| executor.awaitTerminationSeconds | 60 | 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止 |
| executor.threadNamePrefix | async-thread- | 线程池中的线程的名称前缀 |

## 3、接口服务

### 3.1 查询Gas余额接口

```
# 接口方式: http
# 接口名称: 查询Gas余额
# 接口方法名: getGasBalance
# 访问URL: http://192.168.12.222:8080/ucwallet-service/api/getGasBalance
# 返回值类型: JSON

# 返回值: 
{
    "resultCode": 0,
    "resultMsg": "successed",
    "reslut": "99997496336000000000"
}
```

### 3.2 根据钱包地址查询Gas余额接口

```
# 接口方式: http
# 接口名称: 根据钱包地址查询Gas余额
# 接口方法名: getGasBalanceByAddress
# 访问URL: http://192.168.12.222:8080/ucwallet-service/api/getGasBalanceByAddress
# 参数类型: string	
# 参数: address 钱包地址	
# 返回值类型: JSON

# 返回值: 
{
    "resultCode": 0,
    "resultMsg": "successed",
    "reslut": "99997496336000000000"
}
```

### 3.3 查询Token余额接口

```
# 接口方式: http
# 接口名称: 查询Token余额
# 接口方法名: getTokenBalance
# 访问URL: http://192.168.12.222:8080/ucwallet-service/api/getTokenBalance
# 返回值类型: JSON

# 返回值: 
{
    "resultCode": 0,
    "resultMsg": "successed",
    "reslut": "99997496336000000000"
}
```

### 3.4 根据钱包地址查询Token余额接口

```
# 接口方式: http
# 接口名称: 根据钱包地址查询Token余额
# 接口方法名: getTokenBalanceByAddress
# 访问URL: http://192.168.12.222:8080/ucwallet-service/api/getTokenBalanceByAddress
# 参数类型: string	
# 参数: address：钱包地址	
# 返回值类型: JSON

# 返回值: 
{
    "resultCode": 0,
    "resultMsg": "successed",
    "reslut": "99997496336000000000"
}
```

### 3.5 发送原始交易接口

```
# 接口方式: http
# 接口名称: 发送原始交易
# 接口方法名: sendRawTransaction
# 访问URL: http://192.168.12.222:8080/ucwallet-service/api/sendRawTransaction
# 参数类型: string	
# 参数: toAddress：接收方钱包地址, quality：交易数量
# 备注: 
# 返回值类型: JSON

# 返回值: 
{
    "resultCode": 0,
    "resultMsg": "successed",
    "reslut": "99997496336000000000"
}
```



```
# 返回值说明
resultCode：返回结果码	
	0 //操作成功
	1 //无数据
	2 //超时
	3 //参数错误
	9 //系统异常 
resultMsg：返回结果消息
result：返回值
```


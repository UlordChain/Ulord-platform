# Ulord Platform
Ulord platform 主要由Ulord platform application, UDFS, Index Cache和Ulord blockchain client构成。

## Ulord platform application
Ulord platform applcation主要提供Ulord platform的应用层功能，主要依托于其他底层技术实现企业应用的管理服务。
Ulord platform application主要提供如下功能：
- content publish
- content pay
- content download
- content search

以上接口主要提供给企业应用实现基于内容的应用软件。下面以一个提供电子书阅读的应用为例，简要概述实现该应用的一个模式：

需求：提供一个付费电子书阅读的应用程序/小程序

实现：
在Ulord platform注册一个企业应用ID(AppID)，利用Ulord platform提供的SDK开发服务端应用和客户端应用。企业通过Ulord platform提供的SDK用自己的迷药加密和上传具有版权的电子书到Ulord，由于Ulord是基于付费的内容平台，上传需要支付一定押金作为内容的信用凭证，如果内容违规，.......（问题复杂，TBD）

Ulord platform主要分三个模块：Platform service, blockchain binding service, wallet service.

### Platform service
平台服务提供企业应用的服务，包括：
- 企业用户注册，权限管理
- 企业用户内容管理功能
- 企业用户内容统计查询功能

### Blockchain binding service
内容发布主要是实现去中心化的内容发布和下载功能。

### Wallet service
钱包服务主要提供两种模式的服务：
#### Ulord wallet service
该服务直接由底层Ulord blockchain直接提供，用户的应用使用Ulord platform的钱包SDK即可。
#### Ulord wallet proxy service
该服务提供钱包代理功能，通过代理企业用户的钱包功能，企业用户的客户端就不需要提醒用户备份私钥等繁琐功能，还可以借用平台优势，实现多企业用户共享同一Ulord币价值，实现内容支付。

## UDFS(Ulord Distributed File System)
为Ulord应用提供的一种内容存储服务，实现内容的分布式存储。

## Index cache
基于UDFS和Ulord blockchain提供的元数据构建的内容索引服务。如果内容必须依据由企业加密，企业必须提供内容摘要以提供检索服务。

## Ulord blockchain client
基于Ulord blockchain提供的记账功能和内容元数据记账功能，实现内容数据的分发记账。


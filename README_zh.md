# Ulord Platform - The Most Powerfull Infrastructure for Content Distribution Applications
[English](./README.md)

欢迎，这里是Ulord平台(UPaaS)原代码仓库！Ulord平台软件可以让开发者有能力创建和部署高性能，可水平扩展，基于区块链的内容分发应用。
目前代码仍在Alpah版本状态，并在快速开发中，欢迎加入！

Ulord是一个去中心化的内容分发平台，平台提供内容存储，内容检索和分发功能。平台可以承载不同的企业应用，提供给企业用户内容分发服务。
平台提供如下功能：
- 内容去中心化存储服务
- 内容交易服务
- 内容检索服务

平台提供如下保障：
- 内容归企业所有，只有具有企业私钥才可以访问企业内容
- 内容版权确认和内容审查由企业负责，平台提供技术实现
- 内容分发服务由平台提供

## Resources
1. [Ulord官网](http://ulord.one)
2. [文档](https://github.com/UlordChain/documentation)
3. [Wiki](https://github.com/UlordChain/Ulord-platform/wiki)
4. [Vocabulary](./doc/vocabulary_zh.md)

## Ulord平台（UPaaS）
Ulord平台，又名UPaaS，面向企业和个人，提供内容分发的区块链平台，同时也是一个内容服务平台。
UPaaS基于Ulord公链和Ulord分布式文件系统（UDFS）构建，UlordChain和UDFS构成了UPaaS的内容存储子系统（Ulord Content Storage System, UCSS)。基于UlordChain，构建一个中心化托管钱包UCWallet，该钱包实现用户托管账户的交易，内容上链服务，是UPaaS的一个核心子系统，具有横向扩展能力，可以满足内容交易高并发要求。基于UCWallet和UDFS构建了一个内容和链的绑定服务（UBinding），实现链内容和区块链的绑定，并通过索引数据缓存（Index Data Cache）和关系型数据库（DB Cache）提供快速的查询服务。UPaaS基于UCWallet，UBinding，Index data cache和DB Cache构建，对外主要提供企业服务，版权确权服务，内容审计服务和内容分发服务。
详情请参考：[平台文档](doc/ulord_paas_zh.md)

## Ulord钱包
Ulord钱包是用于保存用户资金，签名交易的工具。
详情请参考：[钱包文档](doc/ulord_wallet_zh.md)

## 源代码参考
Ulord平台主要由Python开发，企业端SDK目前支持Python，未来会开发各主流语言的SDK。

- [ulord](./ulord) Ulord平台源代码
- [Uschema](./Uschema) Ulord平台上链信息格式定义（ProtoBuff)
- [UWallet](./Uwallet) Ulord平台托管钱包客户端源代码
- [UCWallet-server](https://github.com/UlordChain/Uwallet-server) Ulord平台托管钱包服务端源代码

## 开始使用Ulord平台
您可以查看Ulord平台的[百科](https://github.com/UlordChain/Ulord-platform/wiki)获得帮助和进化，或者参考[博客示例](https://github.com/UlordChain/ulord-blog-demo)开始使用Ulord。

## 如何共同开发
Ulord平台目前正在快速开发中，文档完善，源代码开发都在积极推进中，如果您有兴趣参与开发，您可以向本仓库提交源码，或者测试目前平台功能，发现问题或者好的建议，请在issue中向我们反馈，我们会尽快的答复。

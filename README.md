# Ulord Platform - The Most Powerful Infrastructure for Content Distribution Applications
[中文](./README_zh.md)

Welcome to the Ulord Platform(UPaaS) source code repository! Ulord platform software enables developers to create and deploy high-performance, horizontally scalable, blockchain infrastructure upon which content distribution applications can be built.

This code is currently alpha-quality and under rapid development. 

Ulord platform is decenterlize content distribution platform which can supply content repo, distribution and search ability. Ulord platform make enterprise development content distribution more effective.
Ulord platform have:
- decenterlize content storage service (UDFS)
- content transaction service
- content search service
- content copyright detected service

Ulord platform guarantee:
- Content is belong to enterprise
- Content copyright and audit is belong to enterprise 


## Resources
1. [Ulord Website](http://ulord.one)
2. [Documentation](https://github.com/UlordChain/documentation)
3. [Wiki](https://github.com/UlordChain/Ulord-platform/wiki)


## Ulord Platform（UPaaS）
Ulord Platform(UPaaS), for enterprises and individuals, it provides a blockchain platform with content distribution, as well as a content distribution service platform. UPaaS is on the base of the Ulord blockchain and the Ulord distributed file system (UDFS). UlordChain and UDFS constitute the UPaaS's content storage subsystem (Ulord Content Storage System, UCSS). UPaaS has a central managed purse wallet, we called UCWallet, which submit the transaction of the user managed account and add some content abstract to Ulord blockchain. It is a core subsystem of the UPaaS, and it is a distributed software, which can horizentionally adjust node number to meet the high concurrency request of the content transaction. We build a binding service (UBinding) base UCWallet and UDFS, which can bind content that come from UDFS and chain that come from Ulord blockchain. UBinding provide fast query services through the index data cache (Index Data Cache) and the relational database (DB Cache). UPaaS is based on UCWallet, UBinding, Index data cache and DB Cache to provide business services, copyright confirmation services, content audit services and content distribution services.
You can refer to [Ulord platform documention](doc/ulord_paas_en.md) for more details

## Ulord Wallet
Ulord wallet is to protected your asset, and execute signuature transaction.
More detail please to see：[Wallet document](doc/ulord_wallet_en.md)

## Source code
Ulord platform mainly develop by python, and enterprise SDK only support python currently, and recently we will implements in main programming language.

In current project, we group different function into different directory.
- [Ulord](./ulord) Ulord platform source code
- [Uschema](./Uschema) Protocol buffer schema for content abstract which can put into ulord chain.
- [UWallet](./Uwallet) Ulord UWallet source code
- [UWallet-server](https://github.com/UlordChain/Uwallet-server) Ulord UWallet server source code.

## Geting Started
You can see our [Wiki](https://github.com/UlordChain/Ulord-platform/wiki) for some help or get involve from [blog demo project](https://github.com/UlordChain/ulord-blog-demo).

## How to contribute
Ulord platform current move fastly, document and source code are pushing positionly. You can join us by submit source code to repo, or test current source code, or find bugs, or give you good ideas in issue. Welcome to Ulord family.


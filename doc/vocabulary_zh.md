# 词汇表
[English](./vocabulary.md)
#### UDFS
Ulord分布式文件系统，基于IPFS（星际互联文件系统）。

#### TxID
交易ID，在Ulord区块链上执行交易时产生的唯一编码，通过该TxID可以获取交易的信息

#### AppID
企业应用ID，在Ulord平台上标识一个企业的唯一编码

#### UDFS hash
UDFS内的一个对象哈希，可以表示一个对象，在本平台中表示一个内容的根哈希，通过该哈希可以在UDFS定位一个对象及其所有子对象节点（内容块）

#### Ulord Address
Ulord地址是Ulord区块链中由私钥，公钥生成的一个唯一地址，用于控制一个区块链的账户地址。

#### Content metadata 
内容元数据，与内容相关的元数据，包括内容标题，作者，创建时间，备注和摘要 

#### Content ID
内容ID，内容ID由内容存在UDFS上的UDFS hash，存在Ulord链上的TxID，保存应用的AppID和用户提交的Content metadata信息构成。通过Content ID可以在Ulord上定位一个唯一的资源以及和资源相关的信息。

#### Content Key
内容密钥是内容存储在UDFS之前由企业自行加密的密钥。企业可以选择所有内容采用统一的密钥，或者每一个内容都采用不同的密钥。

#### Content Index File
内容索引文件是一个由Ulord平台写入到UDFS节点的索引文件，任何可以访问Ulord平台的都可以获得该文件，该文件包括Content ID以及生成Content的相关细节。通过该文件，可以对该文件进行检索，以快速定位Contnet ID，实现基于内容的检索功能。
应用通过专属于应用的Key，可是实现全文检索功能。


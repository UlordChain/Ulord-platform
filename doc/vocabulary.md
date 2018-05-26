# Abbreviation
[中文](./vocabulary_zh.md)
#### UPaaS
Ulord Platform as a Service

#### UCSS
Ulord Content Storage System

#### UCWallet
Ulord Centerinalized Wallet

#### UDFS
Ulord Distribution File System, base on IPFS.

#### TxID
Transaction ID，a unique hash code which execute a transaction on ulord blockchain, you can get transaction detail from Ulord blockchain from transaction id.

#### AppID
App ID, a id for enterprise application. Every enterprise registed in Ulord platform has a global unique application code.

#### UDFS hash
UDFS hash is a UDFS object hash code. It is a content root hash to present a document in ulord platform. You can get document by content hash.

#### Ulord Address
Ulord address is a unique address generate by priviate key and public key. A user can create more than one ulord address.

#### Content metedata 
Content metadata, include content title, create time, author and abstract, even include documnt content.

#### Content ID or Cid
Content id or cid is a unique id for a document(content). Content id will save to ulord blockchain and UDFS index node. Index node also include content hash and metedata. You can get content metadata and content from a content id.

#### Content Key
Content key is encryption key for enterprise's user document. A enterprise can use a standard content key for every content published ulord platform, and also use a unqiue for every document.

#### Content Index File
Content index file is a UDFS file store to UDFS for index-rebuilding. Any content publishing or content trasaction will write to index file.
Index file include content metadata, content id and transaction id.
Any one for our platform user can rebuild index library to finished a search task.
Ulord platform will build a simple way to search data in ulord platform.


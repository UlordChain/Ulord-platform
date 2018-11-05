# UCWallet-Service
[中文文档](./ReadMe_zh.md)

UCWallet-Service is API service for query and send transaction to USC network.
The service can receive request from http and message queue.
Http request can only execute query and submit transaction, it mainly for APP access, such as query balance and submit a signed transaction.
Message queue for back-end use. It can provided double-confirmations for a signed transaction. 

## API
### Get sUT Balance
```
GET /ucwallet-service/api/getBalance/{address}
{
    "resultCode": 0,
    "resultMsg": "successed",
    "reslut": "0.99"
}
```

### Get sUT Balance
**GET /ucwallet-service/api/getTokenBalance/{address}**

eg:
```

>curl http://localhost:9090/ucwallet-service/api/balance/0xba36792ef3f1b5e06ed7ea1c94c3b52450026198
{                                            
  "resultCode": 0,                           
  "resultMsg": "successed",                  
  result":"0.582928388"
}  
```

### Get ERC20 Token Balance

**GET /ucwallet-service/api/getTokenBalance/{address}?token={contract address}**

address is a USC address, and contract address is a ERC20 contract address.
You will get default token balance if contract address is not specified.
Using ulord.side.provider.tokenAddress configuration item to define default token contract address.

```
eg:
>curl http://localhost:9090/ucwallet-service/api/tokenBalance/0xcd2a3d9f938e13cd947ec05abc7fe734df8dd826?token=0xbc353d8cc6c73d95f2ec59573d1f47ed7f12e922
 {
   "resultCode": 0,
   "resultMsg": "successed",
   "result": "0"
 }   
```

### Get transaction count
**GET /ucwallet-service/api/transactionCount/{address}**

eg:
```
>curl http://localhost:9090/ucwallet-service/api/transactionCount/0xb3ec03e42098b84e2e8d4d5a5d8de2f934ba5546
{
  "resultCode": 0,
  "resultMsg": "successed",
  "result": "21"
}
```

### Send raw transaction
**POST /ucwallet-service/api/rawTransaction?hexValue={raw transaction}**

eg:
```
> curl http://localhost:9090/ucwallet-service/api/rawTransaction?txhash=0xa45c88d8b0a4c6addf590eea851b28794bdcb4d68f99ef7d05166052a0b88cb5
{
    "resultCode": 0,
    "resultMsg": "successed",
    "reslut": "0xa3338b107bf4d57be41ef64ad81bd24d1ef40b4cc7ca9f236d50f9963b2180ce"
}
```

### Query transaction info
**GET /ucwallet-service/api/transaction?txhash={txhash}**

eg:
```
> curl http://localhost:9090/ucwallet-service/api/transaction?txhash=0xa45c88d8b0a4c6addf590eea851b28794bdcb4d68f99ef7d05166052a0b88cb5
{
    "result": {
        "blockHash": "0x093fecd5b6763ed2d03395e1214261a7cf0e8e09466ca65a8826abbad5049e82",
        "blockNumber": 68742,
        "blockNumberRaw": "0x10c86",
        "chainId": -17,
        "from": "0xa13d7dbabac37d9b756f573ecd7c0e652ff043c5",
        "gas": 90000,
        "gasPrice": 10000,
        "gasPriceRaw": "0x2710",
        "gasRaw": "0x015f90",
        "hash": "0xa45c88d8b0a4c6addf590eea851b28794bdcb4d68f99ef7d05166052a0b88cb5",
        "input": "0x00",
        "nonce": 13863,
        "nonceRaw": "0x3627",
        "to": "0xb3ec03e42098b84e2e8d4d5a5d8de2f934ba5546",
        "transactionIndex": 1,
        "transactionIndexRaw": "0x1",
        "v": 0,
        "value": 100000000000000,
        "valueRaw": "0x5af3107a4000"
    },
    "resultCode": 0,
    "resultMsg": "succeeded"
}
```

### Query transaction receipt
**GET /ucwallet-service/api/queryTransactionReceipt?txhash={txhash}**

eg:
```
>curl http://localhost:9090/ucwallet-service/api/transactionReceipt?txhash=0xa45c88d8b0a4c6addf590eea851b28794bdcb4d68f99ef7d05166052a0b88cb5

{
    "result": {
        "blockHash": "0x093fecd5b6763ed2d03395e1214261a7cf0e8e09466ca65a8826abbad5049e82",
        "blockNumber": 68742,
        "blockNumberRaw": "0x10c86",
        "cumulativeGasUsed": 21000,
        "cumulativeGasUsedRaw": "0x5208",
        "from": "0xa13d7dbabac37d9b756f573ecd7c0e652ff043c5",
        "gasUsed": 21000,
        "gasUsedRaw": "0x5208",
        "logs": [],
        "root": "0x01",
        "status": "0x01",
        "statusOK": true,
        "to": "0xb3ec03e42098b84e2e8d4d5a5d8de2f934ba5546",
        "transactionHash": "0xa45c88d8b0a4c6addf590eea851b28794bdcb4d68f99ef7d05166052a0b88cb5",
        "transactionIndex": 1,
        "transactionIndexRaw": "0x1"
    },
    "resultCode": 0,
    "resultMsg": "succeeded"
}
```

## Message Queue
We use two message queue for message.
```
+----------------+
|ucwallet-service|
+----------------+
   |             ^
   |              \
   v               \
 +--------------+   +-------------+   
 | Exchange（1）|   | Exchange(2) |
 +--------------+   +-------------+
      |
   +---------+      +---------+
   |Qeueue(1)|      |Qeueue(2)|
   +---------+      +--^------+
       |               |
+------v-------------------+
|      ucwallet-sdk        |
+--------------------------+
```

ucwallet-sdk send a raw transaction which has signed to Queue(Req), ucwallet-service receive message from queue,
and it decode raw transaction to get from address and nonce information to check, it will be submit to USC while there
is no problem.

ucwallet-service periodlly check block height, and to check transaction confirm result, and send response to queue.

ucwallet-sdk recieve response from queue to check current transaction execute result.


## Install
- Install Redis Server
- Install RabbitMQ Server
- Install RabbitMQ management 插件
- Add a user for root(/) host
- Install Java environment
- Start ucwallet-service

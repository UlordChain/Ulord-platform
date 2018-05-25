# UPaaS Common Library

## RealTime Communicatio Protocol

+------------------------------------------------------------+
| LEN | SEQ | TYPE | CMD |            STREAM/MAP             |
+------------------------------------------------------------+

| Item   | Type | Description |
|--------|-------|---------------------------------------------|
| LEN    | INT32 | Data package length, 2^63                   |
| SEQ    | INT16 | We just want to peer response a exact same number for a request seq|
| TYPE   | INT16 | Command type |
| CMD    | INT32 | A command code |
| STREAM |  LEN  | Data, depends on type and cmd, the total length = LEN + 4 + 2 + 2 + 4 = LEN + 12 |


| TYPE CODE | Description|
|-----------|------------|
|     0     | Raw stream, such as file binary stream or others, encoding with MessagePack |
|     1     | Map, encoding with MessagePack|

|  CMD CODE | Description|
|-----------|------------|
|     0     | Null Command, For Testing |
|     1     | Keep Live Command Request and Response |
|     2     | Content Auth Request  |
|     3     | Content Auth Response |
|     4     | Content Auth Result Request |
|     5     | Content Auth Result Response |

- [MsgPack Library](https://msgpack.org/)

Message pack is a json object, which can support by most programming languange.


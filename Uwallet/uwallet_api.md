# uwallet 接口文档(rpc)


## 1. 发布资源 `publish`


- **接口说明：** 发布资源（rpc调用）

- **接口调用：** （python2 为例）

  ```python
  from jsonrpclib import Server

  metadata = {
          "license": "LBRY Inc",
          "description": "What is LBRY? An introduction with Alex Tabarrok",
          "language": "en",
          "title": "What is LBRY?",
          "author": "Samuel Bryan",
          "version": "_0_1_0",
          "nsfw": False,
          "licenseUrl": "",
          "preview": "",
          "thumbnail": "https://s3.amazonaws.com/files.lbry.io/logo.png",
          "tag": ["action"]
      }

  sourceHash = "QmVcVaHhMeWNNetSLTZArmqaHMpu5ycqntx7mFZaci63VF"
  contentType = "video/mp4"
  currency = "ULD"
  amount = 1.2
  bid = 0.57
  claim_name = 'het1ao111'

  server = Server('http://127.0.0.1:8000')
  res = server.publish(user, password, claim_name, bid, metadata,
                       content_type, source_hash, currency, amount)
  print res
  ```

- **参数说明：**

    `user`：用户名，

    `password`：用户的支付密码

    `claim_name`：资源名

    `bid`：发布这笔资源需要支付的费用

    `metadata`：资源元数据， 用户输入（dict）

    `content_type`：资源文件类型

    `source_hash`：资源的ipfs哈希地址

    `currency`：观看这笔资源支付的数字货币的种类

    `amount`：观看这笔资源支付的数字货币的数量


- **返回结果：**

  ```python
  {
      'fee': u'0.000359', # 手续费
      'success': True,  # 是否成功
      'tx':  '01000000026cd6a90cd9e7d486aa9e52261bf74969181ac7a691a398df243bfccb24daee7d000000006a47304402205c07adc5ab1f5ae4830f8c2a71355e10fa647bd598ba55ce95f2cb326c8d3f8e02205f524450cb52d35dd91cac39e2ff8cce66444bd9e21087c367a49ee234b715e0012102521dcd489d7740aac35616320091cad6656b127a86a7a84fbe7622557ad14be6ffffffff6cd6a90cd9e7d486aa9e52261bf74969181ac7a691a398df243bfccb24daee7d010000006b483045022100f1061ea1eef46c97c2e9d3d511523ddb1753687283848bc578d4301a90bc6d37022002dd3868ad8e5931000b7d9c8f6b109385dbc25e2c0c5dfb3b26442929acdf49012102521dcd489d7740aac35616320091cad6656b127a86a7a84fbe7622557ad14be6ffffffff023fc0650300000000fd3a01b70968657431616f31313114e190209f6b4e6d5d3ea17c191d75d7a5e01f97504cfd080110011af601080112bc01080410011a0d57686174206973204c4252593f223057686174206973204c4252593f20416e20696e74726f64756374696f6e207769746820416c6578205461626172726f6b2a0c53616d75656c20427279616e32084c42525920496e6338004224080110011a19824fe527d791ec681899527ab5902de65c247e84792da6ac4c259a99993f4a2f68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f66696c65732e6c6272792e696f2f6c6f676f2e706e6752005a0060021a33080110011a2212206c10c5cece33226e2cc5c56d999fd7874494a1bebf47be0416a09bf53b8954262209766964656f2f6d70346d6d76a9144fe527d791ec681899527ab5902de65c247e847988ac5871263f170000001976a9144fe527d791ec681899527ab5902de65c247e847988ac00000000', # 交易
      'txid': '325dbf4ed36ccc50f84118e322b1452aeb0f385d71f5accbd326ecd4df3df121',  # 交易id
      'amount': '0.56999999', # 资源绑定的币的数量
      'claim_id': '50971fe0a5d7751d197ca13e5d6d4e6b9f2090e1',  # 资源id
      'nout': 0  # 是此次交易中的第几个
  }
  ```

## 2. 消费资源 `consume`


- **接口说明：** 消费资源（rpc调用）

- **接口调用：** （python2 为例）

  ```python
  from jsonrpclib import Server

  user = 'justin'
  password = '123'
  claim_id = "50971fe0a5d7751d197ca13e5d6d4e6b9f2090e1"

  server = Server('http://127.0.0.1:8000')
  res = server.consume(user, password, claim_id)
  print res
  ```

- **参数说明：**

  `user`：用户名，

  `password`：用户的支付密码

  `claim_id`：资源id

- **返回结果：**

  ```python
  {
      'success': True, # 是否成功
      'txid':'3ecce656dbfeea5b38f385549ac51e550bfa6d70bba9d2042dacdd3c1def662a'   # 交易id
  }
  ```


## 3. 创建钱包 `create`


- **接口说明：** 创建钱包（rpc调用）

- **接口调用：** （python2 为例）

```python
from jsonrpclib import Server

user = 'justin'
password = '123'

server = Server('http://127.0.0.1:8000')
res = server.create(user, password)
print res
```

- **参数说明：**

`user`：用户名，

`password`：用户的支付密码

- **返回结果：**

```python
{
    'success': True, # 是否成功
    'seed': u'faculty claim ghost cushion helmet sweet solution dirt night bottom gift trophy' # 用于恢复钱包
```

## 4. 查询余额 `getbalance`


- **接口说明：** 查询指定用户的余额（rpc调用）

- **接口调用：** （python2 为例）

  ```python
  from jsonrpclib import Server

  user = 'justin'
  password = '123'

  server = Server('http://127.0.0.1:8000')
  res = server.getbalance(user, password)
  print res
  ```

- **参数说明：**

  `user`：用户名，

  `password`：用户的支付密码

- **返回结果：**

  ```python
  {
      'success': True, # 是否成功
      'confirmed': '9999.99965899', # 已经确认的余额
      'unconfirmed': '1.33',  # 未确认的余额
      'unmatured': '9.2' # 未成熟的余额， 挖矿所得， 100个块才成熟
      'total': '10010.32965899' # 总的余额
  }
  ```

## 5. 转帐 `pay`


- **接口说明：** 一个用户给另一个用户转帐（rpc调用）

- **接口调用：** （python2 为例）

  ```python
  from jsonrpclib import Server

  user = 'justin'
  password = '123'

  server = Server('http://127.0.0.1:8000')
  receive_user = 'liuqiping'
  res = server.pay(send_user, password, receive_user, amount)
  print res
  ```

- **参数说明：**

  `send_user`：用户名，

  `password`：用户的支付密码

  `receive_user`: 接收的用户名

  `amount`：转帐的金额

- **返回结果：**

  ```python
  {
      'success': True,
      'result:': {
          'txid':'3ecce656dbfeea5b38f385549ac51e550bfa6d70bba9d2042dacdd3c1def662a'   # 交易id
        'user': user,
    }
  }
  ```
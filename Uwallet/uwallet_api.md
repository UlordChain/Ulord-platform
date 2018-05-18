# uwallet 接口文档(rpc)


## 1. 发布资源 `publish`


- **接口说明：** 发布资源（rpc调用)

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

  source_hash = "QmVcVaHhMeWNNetSLTZArmqaHMpu5ycqntx7mFZaci63VF"
  content_type = "video/mp4"
  currency = "UT"
  amount = 1.2
  bid = 0.57
  claim_name = 'het1ao111'

  address=None
  tx_fee=None
  skip_update_check=False

  server = Server('http://127.0.0.1:8000')
  res = server.publish(user, password, claim_name, metadata,
                       content_type, source_hash, currency, amount, bid, address, tx_fee, skip_update_check)
  print res
  ```

- **参数说明：**

    `user`：用户名，

    `password`：用户的支付密码

    `claim_name`：资源名

    `metadata`：资源元数据， 用户输入（dict）

    `content_type`：资源文件类型

    `source_hash`：资源的udfs哈希地址

    `currency`：观看这笔资源支付的数字货币的种类

    `amount`：观看这笔资源支付的数字货币的数量
    
    `bid`：发布这笔资源需要支付的费用, 默认为1
    
    `address`：这笔资源绑定的地址, 默认为钱包第一个地址
    
    `tx_fee`：支付的矿工费用, 默认系统默认值
    
    `skip_update_check`：是否跳过更新验证, 为`False`同名资源会更新, 默认值 `False`
    

- **返回结果：**

  ```python
  {'success': True,  # 是否成功
    'result':{
      'fee': u'0.000359', # 手续费
      'txid': '325dbf4ed36ccc50f84118e322b1452aeb0f385d71f5accbd326ecd4df3df121',  # 交易id
      'claim_id': '50971fe0a5d7751d197ca13e5d6d4e6b9f2090e1',  # 资源id
      'nout': 0  # 是此次交易中的第几个
    }
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
      'result': {
          'txid':'3ecce656dbfeea5b38f385549ac51e550bfa6d70bba9d2042dacdd3c1def662a'   # 交易id
      }
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
      'success': True,
      'result:': {
          'txid':'3ecce656dbfeea5b38f385549ac51e550bfa6d70bba9d2042dacdd3c1def662a',   # 交易id
          'user': user,
      }
  }
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
      'result': {
          # 这几个字段不一定都存在
          'confirmed': '9999.99965899', # 已经确认的余额
          'unconfirmed': '1.33',  # 未确认的余额
          'unmatured': '9.2', # 未成熟的余额， 挖矿所得， 100个块才成熟
          'total': '10010.32965899' # 总的余额
    }
      
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
          'txid':'3ecce656dbfeea5b38f385549ac51e550bfa6d70bba9d2042dacdd3c1def662a',   # 交易id
      }
  }
  ```
## 6. 更新资源 `update_claim`
- **接口说明：** 更新资源（rpc调用), 可以把发布资源的 `skip_update_check` 设置为 `True` , 从而手动调用 来更新资源, 
这样在你确定你的资源是更新还是发布的情况下, 可以省去检测有没有发布过同名资源的过程

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

  source_hash = "QmVcVaHhMeWNNetSLTZArmqaHMpu5ycqntx7mFZaci63VF"
  content_type = "video/mp4"
  currency = "UT"
  amount = 1.2
  bid = 0.57
  claim_name = 'het1ao111'

  address=None
  tx_fee=None

  claim_id='00d19d6216fe37e0c300b171cd0b43c956685616'
  txid='d79ae8e3cad958723a35fd837115afa0e949c43db75c134d50dab7a72b44d71d'
  nout=0

  server = Server('http://127.0.0.1:8000')
  res = server.update_claim(user, password, claim_name, claim_id, txid, nout, metadata,
                       content_type, source_hash, currency, amount, bid, address, tx_fee)
  print res
  ```

- **参数说明：**

    `user`：用户名，

    `password`：用户的支付密码

    `claim_name`：待更新的资源名

    `claim_id`：待更新的资源id (发布时候返回了)

    `txid`：待更新的资源的交易id (发布时候返回了)

    `nout`：待更新的资源的处在发布时候交易中的第几个 (发布时候返回了)

    `metadata`：资源元数据， 用户输入（dict）

    `content_type`：资源文件类型

    `source_hash`：资源的udfs哈希地址

    `currency`：观看这笔资源支付的数字货币的种类

    `amount`：观看这笔资源支付的数字货币的数量
    
    `bid`：发布这笔资源需要支付的费用, 默认为1
    
    `address`：这笔资源绑定的地址, 默认为钱包第一个地址
    
    `tx_fee`：支付的矿工费用, 默认系统默认值
    

- **返回结果：**

  ```python
  {'success': True,  # 是否成功
    'result':{
      'fee': u'0.000359', # 手续费
      'txid': '325dbf4ed36ccc50f84118e322b1452aeb0f385d71f5accbd326ecd4df3df121',  # 交易id
      'claim_id': '50971fe0a5d7751d197ca13e5d6d4e6b9f2090e1',  # 资源id
      'nout': 0,  # 是此次交易中的第几个
      "bid": 1.177  # 更新支付的费用
    }
  }
  
  ```
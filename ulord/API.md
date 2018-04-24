# 一. API平台管理界面相关接口(内部接口)

#### 说明: 需要登录开发这帐号才能进行相关操作.

### 1. 开发者角色接口

##### A. 角色添加  `POST`    `/v1/role/add/`
```
# 请求参数:

{
	"name": 角色名,
	"des": 角色描述
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "id": 11
    }
}
失败
{
    "errcode": 错误码,
    "reason": "错误原因",
}
```

##### B. 角色列表  `GET`    `/v1/role/list/`
```
# 请求参数:
无

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": [
        {
            "des": "管理员",
            "id": 4,
            "name": "admin"
        },
        {
            "des": "标准用户",
            "id": 7,
            "name": "normal"
        },
        {
            "des": "封禁用户",
            "id": 8,
            "name": "blocked"
        }
    ]
}
```

##### C. 角色修改  `POST`    `/v1/role/edit/`
```
# 请求参数:

{
    "id":id值,
	"name": 角色名,
	"des":角色描述
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
}
```

##### D. 角色删除  `POST`    `/v1/role/remove/`
```
# 请求参数:

{
    "id":id值,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "num": 1
    }
}
```




### 2. 应用类型接口

##### A. 类型添加  `POST`    `/v1/type/add/`
```
# 请求参数:

{
    "parent_id":4,  # 顶级分类不需要此参数
    "name":类型名,
    "des":类型描述,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "id": 1
    }
}
```

##### B. 类型列表  `GET`    `/v1/type/list/`
```
# 请求参数:

无

# 返回值:

成功
{ # 无限多级分类
    "errcode": 0,
    "reason": "success",
    "result": [
        {
            "children": [
                {
                    "des": "boke",
                    "id": 7,
                    "name": "博客",
                    "parent_id": 2
                }
            ],
            "des": "博客,论坛等等",
            "id": 2,
            "name": "文字",
            "parent_id": null
        },
        {
            "des": "图片",
            "id": 3,
            "name": "图片",
            "parent_id": null
        },
        {
            "des": "视频",
            "id": 4,
            "name": "视频",
            "parent_id": null
        }
    ]
}
}
```

##### C. 类型修改  `POST`    `/v1/type/edit/`
```
# 请求参数:

{
    "id":id值,
    "name":类型名,
    "des":类型描述,
    "parent_id":父类id,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
}
```

##### D. 类型删除  `POST`    `/v1/type/remove/`
```
# 请求参数:

{
    "id":id值,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "num":1,
    }
}
```

### 3. 开发者帐号接口

##### A. 开发者注册  `POST`    `/v1/users/reg/`
```
# 请求参数:

{
    "username":开发这帐号,
    "password":帐号密码,
    "email":邮箱(可空),
    "telphone":电话(可空),
    "role_id":角色id,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "id":1,
    }
}
```

##### B. 开发者登录  `POST`    `/v1/users/login/`
```
# 请求参数:

{
    "username":开发这帐号,
    "password":帐号密码,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "token":token(令牌认证暂时还未实现),
    }
}
```

##### C. 开发者密码修改  `POST`    `/v1/users/edit/`
```
# 请求参数:

{
    "username":开发这帐号,
    "password":帐号密码,
    "new_password":新密码,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
}
```

### 4. 应用接口

##### A. 应用添加  `POST`    `/v1/app/add/`
```
# 请求参数:

{
    "user_id":用户id,
    "appname":应用名称,
    "apptype_id":应用类型id,
    "appdes":应用描述,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "id": 应用id
    }
}
```

##### B. 应用列表  `POST`    `/v1/app/list/`
```
# 请求参数:

{
    "user_id":登录用户id(以后可以在登录状态中获取,暂时传入),
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": [
        {
            "appdes": null,
            "appkey": "31f7e6703d5c11e893fcf48e3889c8ab",
            "appname": "boke",
            "create_timed": "2018-04-12T10:56:19.394662+00:00",
            "id": 1,
            "secret": "ba5fe8403dfe11e89f1bf48e3889c8ab",
            "type": 7,
            "update_timed": "2018-04-12T11:08:09.265021+00:00"
        }
    ]
}
```

##### C. 应用修改secret  `POST`    `/v1/app/edit/`
```
# 请求参数:

{
    "id":应用id,
    "user_id":用户id,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "secret":签名私钥,
    }
}
```
##### D. 应用删除  `POST`    `/v1/app/remove/`
```
# 请求参数:

{
    "id":3,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "num":删除条数,
    }
}
```


# 二. API平台外部调用接口

#### 说明: 将appkey将如到请求头headers中.如: `appkey:123412`
​
### 交易相关

##### 1. 创建钱包  `POST`    `/v1/transactions/createwallet/`
```
# 请求参数:

{
    "username": 钱包用户名,
    "pay_passwrod":支付密码
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
            "id": "justin",
        },
}
```

##### 2. 转账  `POST`    `/v1/transactions/paytouser/`
```
# 请求参数:
{
    'is_developer':bool,  # 是否是开发者帐号转账
    # 如果is_developer为True, 下面两个参数不需要
    'send_user':转账的支付用户,
    'pay_password':转账支付密码,
    'recv_user': 转账的接收用户,
    'amount':金额,
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
}
```


##### 3. 查询余额  `POST`    `/v1/transactions/balance/`
```
# 请求参数:

{
        'username':消费者用户名,
        'pay_password':支付密码(在创建用户时指定),
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "total":总余额,
        "confirmed":已确认余额,
        "unconfirmed":未确认余额,
        "unmatured":未成熟的余额(挖矿所得,100个块才成熟),
    }
}
```

##### 4. 发布资源  `POST`    `/v1/transactions/publish/`
```
# 请求参数:

{
        'author': 发布者用户名,
        'title': 资源标题,
        'tag': 标签列表,
        'ipfs_hash': 星际文件系统哈希,
        'price': 定价,
        'content_type': 资源类型(文件后缀名),
        'pay_password': 支付密码,
        'description':资源描述
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "id":数据库id,
        "claim_id":资源在链上的id,
    }
}
```

##### 5. 检查是否付费  `POST`    `/v1/transactions/check/`
```
# 请求参数:

{
        "username":"user2",
        "claim_ids":[
        	"ec3c93680884d8b1aee25242f64f79f8bd847c57",
        	"a5b899fe01d633b6f0b809c4af2312524c081576",
        	"25e48b12694b4704aeff32ba0a568c21ad8dd5d6",
        	"e1b98bcc018950ac4684c663d0ea4fa9fc19543d",
        	"e1b98bcc018950ac4684c663d0ea4fa9fc19543f",
        	"2d4bbaf369464feeb90ac957af72a641f9a1bc9c"
    	]
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "25e48b12694b4704aeff32ba0a568c21ad8dd5d6": "QmUH2NbKrURA6hAmJnhfP4VTDtkjUs3fVCN2L7DoE3JLmm",
        "2d4bbaf369464feeb90ac957af72a641f9a1bc9c": false,  # 未付费
        "a5b899fe01d633b6f0b809c4af2312524c081576": "QmUH2NbKrURA6hAmJnhfP4VTDtkjUs3fVCN2L7DoE3JLmm",
        "e1b98bcc018950ac4684c663d0ea4fa9fc19543d": null,  # 没有此记录
        "e1b98bcc018950ac4684c663d0ea4fa9fc19543f": false,
        "ec3c93680884d8b1aee25242f64f79f8bd847c57": "QmUH2NbKrURA6hAmJnhfP4VTDtkjUs3fVCN2L7DoE3JLmm"
    }
}
```

##### 6. 消费资源  `POST`    `/v1/transactions/consume/`
```
# 请求参数:

{
        'username':消费者用户名,
        'claim_id':'45cdb43d78bd12ee3acfa9be7c56ae02d6c88d3e'
        'customer_pay_password':消费密码(登录用户的支付密码,消费正常资源时传入),
        "author_pay_password":资源发布者的支付密码(点击广告时传入)
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result":{
        "ipfs_hash":ipfs_hash,
    }
}
```

##### 7. 收入账单  `POST`    `/v1/transactions/account/in/<page>/<num>/`
```
# 请求参数:
{
    'username':719355782  # 用户
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "pages": 1,
        "records": [
            {  # 用户作为消费这, 点击广告收入
                "author": "yyy",
                "claim_id": "798aedf4fab2fa77a77b56528abe6e50afce37e6",
                "create_timed": "2018-04-21T13:37:41.983595+00:00",
                "customer": "719355782",
                "price": 0.6,
                "title": "666",
                "txid": "d162db3c4185720d287b7fabbe560546c9bce06f0812fadeb9d78c8d0fe2a2aa"
            },
            {  # 用户作为发布者, 发布资源收入
                "author": "719355782",
                "claim_id": "870c3a35a8b82f1d4f8e89b89b5c7d3b80d6bc5b",
                "create_timed": "2018-04-21T09:47:05.902228+00:00",
                "customer": "zyding",
                "price": 0.525,
                "title": "123123123123",
                "txid": "b781f7c12aa7b7a43c22a5bea2ac56d6d15a1dbde7eeea9e2774f7e5f168df56"
            }
        ],
        "total": 2
    }
}
```


##### 8. 支出账单  `POST`    `/v1/transactions/account/out/<page>/<num>/`
```
# 请求参数:
{
    'username':719355782  # 用户
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "pages": 1,
        "records": [
            {  # 用户作为发布者 支出广告费
                "author": "719355782",  # 发布者
                "claim_id": "870c3a35a8b82f1d4f8e89b89b5c7d3b80d6bc5b",
                "create_timed": "2018-04-21T08:39:25.883777+00:00",
                "customer": "935827234",  # 消费者
                "price": 0.525,  # 交易金额
                "title": "123123123123",
                "txid": "31af05db89decfcd561ba79fbd130aacb8f02de4b75e55f4548626c1d9732c51"
            },
            {  # 用户作为消费者, 支出资源消费
                "author": "tttttttttttt",
                "claim_id": "010d23be8ce1e23da9dad94c61618d1e0b484c77",
                "create_timed": "2018-04-21T11:55:43.680047+00:00",
                "customer": "719355782",
                "price": 0.02,
                "title": "the first blog",
                "txid": "f672a32a11c1eb82a7a1b17e93bc823132c7bc75c3ed990fd1e797c8a11fbe50"
            }
        ],
        "total": 2
    }
}
```

##### 9. 收支总额  `POST`    `/v1/transactions/account/`
```
# 请求参数:
{
    'username':登录用户
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "customer_out": {  # 消费者支出
            "count": 0,
            "sum": null
        },
        "customer_in": {  # 消费者收入
            "count": 0,
            "sum": null
        },
        "publisher_out": {  # 发布者支出
            "count": 2,
            "sum": 0.04
        },
        "publisher_in": {  # 发布者收入
            "count": 0,
            "sum": null
        }
    }
}
```

##### 10. 资源总数  `POST`    `/v1/transactions/publish/count/`
```
# 请求参数:
{
    'author':发布者
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "count": 0
    }
}
```


### 资源相关

##### 1. 资源列表查询  `GET`    `/v1/content/list/<page>/<num>`
```
# 请求参数:
    page: 页码
    num: 每页条数

无 (接下来可以实现条件查询以及分页)

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        total:总条数,
        pages:总页数,
        data:
        [
            {
                "author": "justin",
                "claim_id": "45cdb43d78bd12ee3acfa9be7c56ae02d6c88d3e",
                "content_type": ".txt",
                "create_timed": "2018-04-12T15:47:34.446858+00:00",
                "currency": "ULD",
                "des": "这是使用IPFS和区块链生成的第2篇博客的描述信息",
                "id": 5,
                "price": 1.3,
                "status": 1,
                "tags": [
                    "C++",
                    "java",
                    "javascript",
                ],
                "title": "第2篇技术博客",
                "update_timed": null
            }
        ]
    }
}
```

##### 2. 单用户已消费资源列表  `POST`    `/v1/content/consume/list/<page>/<num>/`
```
# 请求参数:
{
    'customer':消费者,
    'category':条件查询 (0:消费支出 1:广告收入 ,不传:all)
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "pages": 1,
        "records": [
            {
                "author": 资源发布者,
                "claim_id": 资源在链上的claim_id,
                "create_timed": 消费时间,
                "enabled": 资源是否删除,
                "id": 资源在DB中id,
                "price": 0.5, # 价格为正时, 是消费者的支出
                "title": 资源标题,
                "txid": 此条消费的txid
            }
        ],
        "total": 7
    }
}
```

##### 3. 单用户已发布资源列表  `POST`    `/v1/content/publish/list/<page>/<num>/`
```
# 请求参数:
{
    'author':消费者,
    'category':条件查询 # 0: 资源收入 1: 广告支出 其他:all
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "data": [
            {
                "claim_id": 资源的claim_id,
                "create_timed": 此条消费的时间,
                "customer": 消费者,
                "enabled": 资源是否删除,
                "id": 资源在DB中的id,
                "price": 0.6,  # 价格为正, 是发布者的收入
                "title": 资源标题,
                "txid": 此条消费的txid
            }
        ],
        "pages": 1,
        "total": 4
    }
}
```

##### 4. 浏览量增加  `POST`    `/v1/content/publish/list/<page>/<num>/`
```
# 请求参数:
{
    'id':资源id
}

# 返回值:

成功
{
    "errcode": 0,
    "reason": "success",
    "result": {
        "num": 修改影响行数
    }
}
```

### 附录A: 错误码对照表
```
{
    # 正常
    0:{'errcode':0,'reason':'success'},  # 可以重写reason与result内容

    # HTTP协议错误码
    400:{'errcode':400,'reason':'Bad Request.'},
    403:{'errcode':403,'reason':'Forbidden.'},
    404:{'errcode':404,'reason':'Not found.'},
    405:{'errcode':405,'reason':'Method Not Allowed.'},
    500:{'errcode':500,'reason':'Internal Server Error.'},

    # 系统级错误码
    10001:{'errcode':10001,'reason':'错误的请求KEY.'},
    10002:{'errcode':10002,'reason':'该KEY无请求权限.'},
    10003:{'errcode':10003,'reason':'KEY过期.'},
    10004:{'errcode':10004,'reason':'被禁止的IP.'},
    10005:{'errcode':10005,'reason':'被禁止的KEY.'},
    10006:{'errcode':10006,'reason':'当前IP请求超过限制.'},
    10007:{'errcode':10007,'reason':'请求超过次数限制.'},
    10008:{'errcode':10008,'reason':'系统内部异常.'},
    10009:{'errcode':10009,'reason':'接口维护.'},
    10010:{'errcode':10010,'reason':'接口停用.'},
    10011:{'errcode':10011,'reason':'当前没有登录用户,请登录.'},
    10012:{'errcode':10012,'reason':'缺少应用KEY值.'},
    10013:{'errcode':10013,'reason':'无权限进行此操作.'},

    # 服务级错误码
    # 1. DB查询验证
    20000:{'errcode':20000,'reason':'用户已存在.'},
    20001:{'errcode':20001,'reason':'邮箱已存在.'},
    20002:{'errcode':20002,'reason':'应用名已存在.'},
    20003:{'errcode':20003,'reason':'用户不存在.'},
    20004:{'errcode':20004,'reason':'密码错误.'},
    20005:{'errcode':20005,'reason':'数据不存在.'},
    20006:{'errcode':20006,'reason':'用户被禁用.'},
    20007:{'errcode':20007,'reason':'资源不存在.'},
    20008:{'errcode':20008,'reason':'资源需付费.'},

    # 2. 请求参数验证相关
    20100:{'errcode':20100,'reason':'缺少参数.'},
    20101:{'errcode':20101,'reason':'参数长度不符.'},
    20102:{'errcode':20102,'reason':'参数必须为json格式.'},
    # 3. 钱包相关接口调用
    20200:{'errcode':20200,'reason':'调用钱包接口失败.'},
    20201:{'errcode':20201,'reason':'资源发布失败.'},
    20202:{'errcode':20202,'reason':'资源消费失败.'},
    20203:{'errcode':20203,'reason':'查询余额失败.'},
    20204:{'errcode':20204,'reason':'创建钱包失败.'},
    20205:{'errcode':20205,'reason':'用户注册失败,因为没有成功创建ulord钱包.'},
    20206:{'errcode':20206,'reason':'支付失败.'},
}
```
### 附录B: 数据库ER图

![er](DBER.png)
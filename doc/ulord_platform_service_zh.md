# Ulord platform service API

接口说明：
接口采用HTTPS RESTFULL接口，数据采用JSON数据格式。

## 通用接口
### 用户注册接口
### 用户登陆接口
### 用户退出接口
### 用户管理接口
用户管理接口包括对自己的信息进行管理，还可以管理用户相关的资源，包括应用，子用户等。

## 应用管理接口
管理应用信息，一个用户可以有多个应用，一个应用可以有多个应用。
应用可以委托代理人进行管理。（委托管理需求1.0版本不实现）

### 注册应用接口(/api/v0/app)
请求方法： POST
请求URL：/api/v0/app
认证参数：要求已登陆
请求参数
```
{
    name: 'App simple name',
	desc: 'App description',
    website: 'App website',
	telphone: 'App owner's telphone',
	email: 'App owner's email',
    memo: 'Other information for register',
    allowips:'ip1, ip2...' //Application server ips, such 1.2.3.0/24,1.2.5.5
}
```

响应参数
```
    {
        error: '',
        result:{
            appId: '000000001'
        }
    }
```

### 获取应用信息接口(/api/v0/app)
请求方法： GET
请求URL：/api/v0/app/{enterpirse id}
认证参数：要求登陆
请求参数: 无
响应结果
```
    {
        error: '',
        result:{
            name: 'app simple name',
            app: 'app Name',
            country: 'app belong Contry',
            telphone: 'app telphone',
            email: 'app contract email',
            memo: 'Other information for register',
        }
}
```

### 修改应用信息接口(/api/v0/app/{enterpirse id})
请求方法：PUT
请求URL：/api/v0/app/{enterpirse id}
认证参数：要求登陆
请求参数：
```
{
    name: 'app simple name',
    app: 'app Name',
    country: 'app belong Contry',
    telphone: 'app telphone',
    email: 'app contract email',
    memo: 'Other information for register',
}
```
响应结果
```
    {
        error: '',
        result: 1 // 0 - 失败, 1 - 成功
    }
```
### 删除应用信息接口(/api/v0/app/{enterpirse id})
请求方法：DELTE
请求URL：/api/v0/app/{enterpirse id}
认证参数：要求登陆
请求参数：无
响应结果
```
    {
        error: '',
        result: 1 // 0 - 失败, 1 - 成功
    }
```

## 内容管理接口
内容必须归属于特定的应用
### 发布资源(/api/v0/content)
请求方法：PUT
请求URL：/api/v0/content
认证参数：要求登陆
请求参数：
```
{
    contentHash: 'Qmxxxxxxxxxxxxxx',
    author: 'alian',
    title: 'Content title',
    subtitle: 'Content subtitle',
    description: 'Content description',
    contentTags:[{tagName: 'Tag name', tagDesc: 'Tag description'}],
    ulordAddress: 'Pay address',
    contract: '' // Base contract
}
```
说明：
contract为资源发布合约，前期可以简单构建一个合约，例如：
{
    type: 'base',
    code: '[{role: 'author', receiveAddress:'Uxdkisldlf', ratio: 50}, 
            {role: 'reviser', ratio: 40},
            {role: 'trader', ratio: 10, formula: '-n+10'}
           ]'
}
通过类似这样的JOSN对象，确定作者，修订者和交易者（传播者）的分成比率。

响应结果：
```
{
    error: '',
    result:{
        contentId: 'Abcd191juxsdeFd'
    }
}
```

### 购买资源(/api/v0/content/{contentId})
请求方法：GET
请求URL：/api/v0/content/{contentId}
请求参数：
```
    {
        contentId: 'Abcd191juxsdeFd',
    }
```

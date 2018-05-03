# -*- coding: utf-8 -*-
# @Date    : 2018/3/28
# @Author  : Shu
# @Email   : httpservlet@yeah.net


# 错误格式说明: 20101
# 2: 服务级错误
# 01: 服务模块代码(服务类别)
# 01:具体错误代码
import copy

_errcodes={
    # 正常
    0:{'errcode':0,'reason':'success'},  # 可以重写reason与result内容

    # HTTP协议错误码
    400:{'errcode':400,'reason':'Bad Request.'},
    401:{'errcode':401,'reason':'Authentication Required.'},
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
    20100:{'errcode':20100,'reason':'参数错误.'},
    20101:{'errcode':20101,'reason':'参数必须为json格式.'},
    # 3. 钱包相关接口调用
    20200:{'errcode':20200,'reason':'调用钱包接口失败.'},
    20201:{'errcode':20201,'reason':'资源发布失败.'},
    20202:{'errcode':20202,'reason':'资源消费失败.'},
    20203:{'errcode':20203,'reason':'查询余额失败.'},
    20204:{'errcode':20204,'reason':'创建钱包失败.'},
    20205:{'errcode':20205,'reason':'用户注册失败,因为没有成功创建ulord钱包.'},
    20206:{'errcode':20206,'reason':'支付失败.'},
    20207:{'errcode':20207,'reason':'修改钱包密码失败.'},
}

def return_result(errcode=0,reason=None,result=None):
    if errcode==0:
        res=copy.deepcopy(_errcodes[0])
        if reason is not None:
            res.update({'reason':reason})
        if result is not None:
            res.update({'result':result})
        return res
    else:
        rs= _errcodes[errcode]
        if reason:
            rs.update(dict(reason=reason))
        if result:
            rs.update(dict(result=result))
        return rs
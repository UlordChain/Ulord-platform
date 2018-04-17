# -*- coding: utf-8 -*-
# @Date    : 2018/3/30
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# @Des     : 生成各种数据的通用类
import uuid


def generate_appkey():
    """生成开发者的appkey"""
    appkey = str(uuid.uuid1())
    return appkey.replace('-', '')

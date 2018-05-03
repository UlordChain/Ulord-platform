# -*- coding: utf-8 -*-
# @Date    : 2018/5/2
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import auth
from ulord.utils import return_result

__all__=['error_401']

@auth.error_handler
def error_401():
    return return_result(401),401
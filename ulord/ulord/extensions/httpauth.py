# -*- coding: utf-8 -*-
# @Date    : 2018/5/2
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import auth
from ulord.utils import return_result
from flask import g
from ulord.models import User

__all__=['error_401','verify_token']

@auth.error_handler
def error_401():
    return return_result(401),401

@auth.verify_token
def verify_token(token):
    g.user=None
    user=User.verify_auth_token(token)
    if isinstance(user,User):
        g.user=user
        return True
    return False
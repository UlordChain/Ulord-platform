# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from flask import Blueprint, request, g,current_app
from functools import wraps
from ulord.utils import return_result
from ulord.models import Application
from jsonrpclib import Server

def appkey_check(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        appkey = request.headers.get('appkey')
        if not appkey:
            return return_result(10012)
        # 可以换成从redis中获取
        uapp = Application.query.filter_by(appkey=appkey).first()
        if not uapp:
            return return_result(10001)
        user=uapp.user
        if user.role.name=='blocked':
            return return_result(20006)

        g.user = user
        return f(*args, **kwargs)

    return decorator


def login_check(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        username = request.json.get('username')
        password = request.json.get('password')
        if not username or not password:
            return return_result(10011)

        user = User.query.filter_by(username=username).first()
        if user is None:
            return return_result(10011)

        if not user.verify_password(password):
            return return_result(10011)

        return f(*args, **kwargs)

    return decorator

def get_jsonrpc_server():
    server = Server(
        'http://{}:{}'.format(current_app.config['WALLET_JSONRPC_HOST'], current_app.config['WALLET_JSONRPC_PORT']))
    return server


# args1: 蓝图名(端点的一部分, 加上视图那部分端点才完整)
bpv1 = Blueprint('apiv1', __name__, url_prefix='/v1')
from . import app_type, role, transactions, users, uapp,content

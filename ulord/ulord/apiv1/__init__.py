# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from flask import Blueprint, request, g, current_app
from functools import wraps
from ulord.utils import return_result
from ulord.models import Application
from jsonrpclib import Server
import time

# def verify_sign(curtime,sign):


def appkey_check(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        appkey = request.headers.get('U-AppKey')
        curtime = request.headers.get('U-CurTime')
        sign = request.headers.get('U-Sign')
        if not appkey:
            return return_result(10012)

        # if not curtime:
        #     return return_result(10017)
        # else:
        #     if not curtime.isdigit():
        #         return return_result(20100)
        #     if time.time() - int(curtime) > 60 * 5:
        #         return return_result(10017)
        # if not sign:
        #     return return_result(10013)
        # if not verify_sign(curtime,sign):
        #     return return_result(20102)

        # insert to redis
        uapp = Application.query.filter_by(appkey=appkey).first()
        if not uapp:
            return return_result(10001)
        user = uapp.user
        if user.role.name == 'blocked':
            return return_result(20006)

        g.user = user
        g.appkey = appkey
        return f(*args, **kwargs)

    return decorator


def admin_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        # Need to be used with @auth.login_required
        if g.role != 'admin':
            return return_result(10014)
        return f(*args, **kwargs)

    return decorator


def blocked_check(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        if g.role == 'blocked':
            return return_result(10015)
        return f(*args, **kwargs)

    return decorator


def get_jsonrpc_server():
    server = Server(
        'http://{}:{}'.format(current_app.config['WALLET_JSONRPC_HOST'], current_app.config['WALLET_JSONRPC_PORT']))
    return server


bpv1 = Blueprint('apiv1', __name__, url_prefix='/v1')
from . import app_type, role, transactions, users, uapp, content

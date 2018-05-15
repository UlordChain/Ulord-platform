# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from flask import Blueprint, request, g, current_app
from functools import wraps
from ulord.utils import return_result
from ulord.models import Application
from jsonrpclib import Server
import time, hashlib


def verify_sign(appkey, secret, curtime, old_sign):
    j = request.get_json(silent=True)
    params = j if j is not None else {}
    params = sorted(params.items(), key=lambda i: i[0])
    sign = ''

    for k, v in params:
        if isinstance(v, bool):
            # In josn, False is false.
            if v is True:
                v = 'true'
            else:
                v = 'false'
        if isinstance(v, list):
            tv = ''
            for i in v:
                tv += str(i)
            v = tv
        sign += str(k) + str(v)

    sign = appkey + sign + secret + str(curtime)
    print(sign)
    sign = hashlib.md5(sign.encode('u8')).hexdigest().upper()
    # print(sign,old_sign)
    if sign == old_sign.upper():
        return True
    return False


def appkey_check(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        appkey = request.headers.get('U-AppKey')
        curtime = request.headers.get('U-CurTime')
        sign = request.headers.get('U-Sign')
        if not appkey:
            return return_result(10012)

        # insert to redis
        uapp = Application.query.filter_by(appkey=appkey).first()
        if not uapp:
            return return_result(10001)
        user = uapp.user
        secret = uapp.secret
        if user.role.name == 'blocked':
            return return_result(20006)

        # Verify digital signature
        if not curtime:
            return return_result(10017)
        else:
            if not curtime.isdigit():
                return return_result(20100)
            if time.time() - int(curtime) > current_app.config['SIGN_EXPIRES']:
                return return_result(10017)
        if not sign:
            return return_result(10013)
        if not verify_sign(appkey, secret, curtime, sign):
            return return_result(20102)

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

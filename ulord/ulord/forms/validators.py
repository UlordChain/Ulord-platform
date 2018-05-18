# -*- coding: utf-8 -*-
# @Date    : 2018/4/27
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# 自定义验证

from wtforms.validators import ValidationError
from flask import g
from ulord.utils.rsa import rsahelper
from ulord.apiv1 import get_jsonrpc_server
from flask import g

__all__ = ['Unique', 'Exists', 'RsaCheck', 'WalletUnique', 'WalletExists']


class Unique(object):
    def __init__(self, model, field, message='This data already exists.'):
        self.model = model
        self.field = field
        self.message = message

    def __call__(self, form, field):
        check = self.model.query.filter(self.field == field.data).first()
        if check:
            raise ValidationError(self.message)


class Exists(object):
    def __init__(self, model, field, message='This data does not exist.'):
        self.model = model
        self.field = field
        self.message = message

    def __call__(self, form, field):
        exists = self.model.query.filter(self.field == field.data).first()
        if not exists:
            raise ValidationError(self.message)


class RsaCheck(object):
    def __init__(self, message='This data does not exist.'):
        self.message = message

    def __call__(self, form, field):
        try:
            password = rsahelper.decrypt(field.data).decode('u8')
        except Exception as e:
            print(e)
            self.message="The encryption format is not correct."
            raise ValidationError(self.message)
        if len(password) < 3 or len(password) > 128:
            self.message="Password must be between 3 and 128 characters long."
            raise ValidationError(self.message)


class WalletUnique(object):
    def __init__(self, message='The user already exists'):
        self.message = message

    def __call__(self, form, field):
        server = get_jsonrpc_server()
        username_wallet = '{}_{}'.format(g.appkey, field.data)
        result = server.is_wallet_exists(username_wallet)
        if result.get('result'):
            raise ValidationError(self.message)
        field.data = username_wallet


class WalletExists(object):
    def __init__(self, message="The user doesn't exist"):
        self.message = message

    def __call__(self, form, field):
        server = get_jsonrpc_server()
        username_wallet = '{}_{}'.format(g.appkey, field.data)
        result = server.is_wallet_exists(username_wallet)
        if not result.get('result'):
            raise ValidationError(self.message)
        field.data = username_wallet

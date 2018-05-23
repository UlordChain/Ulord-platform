# -*- coding: utf-8 -*-
# @Date    : 2018/4/27
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# 自定义验证

from wtforms.validators import ValidationError
from flask import g
from ulord.utils.rsa import rsahelper
from ulord.apiv1 import get_jsonrpc_server

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
    def __init__(self, message='The user already exists.',is_wallet_name=True):
        self.message = message
        self.is_wallet_name=is_wallet_name

    def __call__(self, form, field):
        server = get_jsonrpc_server()
        username_wallet = '{}_{}'.format(g.appkey, field.data)
        try:
            result = server.is_wallet_exists(username_wallet)
        except Exception as e:
            print(type(e),e)
            raise ValidationError('Wallet service exception.')
        if result.get('result'):
            raise ValidationError(self.message)
        if self.is_wallet_name is True:
            field.data = username_wallet


class WalletExists(object):
    def __init__(self, message="The user doesn't exist.",is_wallet_name=True):
        self.message = message
        self.is_wallet_name=is_wallet_name

    def __call__(self, form, field):
        server = get_jsonrpc_server()
        username_wallet = '{}_{}'.format(g.appkey, field.data)
        try:
            result = server.is_wallet_exists(username_wallet)
        except Exception as e:
            print(type(e),e)
            raise ValidationError('Wallet service exception.')
        if not result.get('result'):
            raise ValidationError(self.message)
        if self.is_wallet_name is True:
            field.data = username_wallet

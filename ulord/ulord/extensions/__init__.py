# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from flask_sqlalchemy import SQLAlchemy
from flask_httpauth import HTTPTokenAuth
from flask_marshmallow import Marshmallow

__all__ = ['db', 'auth', 'ma']

db = SQLAlchemy()
auth = HTTPTokenAuth()
ma = Marshmallow()

# The import is to preprocess the auth decorator.
from .httpauth import error_401

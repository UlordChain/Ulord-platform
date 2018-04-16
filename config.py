# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net


class Config(object):
    SECRET_KEY = 'bb649c83dd1ea5c9d9dec9a18df0ffe9'
    SQLALCHEMY_COMMIT_ON_TEARDOWN = True
    FLASKY_MAIL_SUBJECT_PREFIX = '[Flasky]'
    FLASKY_MAIL_SENDER = 'shuxudong@ulord.net'
    FLASKY_ADMIN = 'ulord'
    WALLET_JSONRPC_HOST = '192.168.14.244'  # 自定义
    WALLET_JSONRPC_PORT = '8000'  # 自定义

    @staticmethod
    def init_app(app):
        pass


class DevelopmentConfig(Config):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = "postgres://postgres:123@127.0.0.1:5432/ulord_development"
    SQLALCHEMY_TRACK_MODIFICATIONS = True
    SQLALCHEMY_ECHO=True


class TestingConfig(Config):
    TESTING = True
    WTF_CSRF_ENABLED = False
    SQLALCHEMY_DATABASE_URI = "postgres://postgres:123@127.0.0.1:5432/ulord_testing"
    SQLALCHEMY_TRACK_MODIFICATIONS = True


class ProductionConfig(Config):
    SQLALCHEMY_DATABASE_URI = "postgres://postgres:123@127.0.0.1:5432/ulord_production"
    SQLALCHEMY_TRACK_MODIFICATIONS = True


dconfig = {'development': DevelopmentConfig, 'testing': TestingConfig, 'prodection': ProductionConfig,
          'default': DevelopmentConfig}

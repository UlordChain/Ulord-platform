# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net


class Config(object):
    SECRET_KEY = 'bb649c83dd1ea5c9d9dec9a18df0ffe9'
    SQLALCHEMY_COMMIT_ON_TEARDOWN = True
    FLASKY_MAIL_SUBJECT_PREFIX = '[Ulord]'
    FLASKY_MAIL_SENDER = 'Ulord Admin <shuxudong@ulord.net>'
    FLASKY_ADMIN = 'Ulord'
    WALLET_JSONRPC_HOST = '192.168.14.240'
    WALLET_JSONRPC_PORT = '8000'
    PUBLISH_BID = 0.01  # Amount paid to Ulord Platform
    PUBLISH_CURRENCY = 'UT'  # Token unit
    EXPIRATION = 60 * 60 * 6  # Login token expiration time
    UAPP_MAX_COUNT = 10  # The maximum number of new applications available.
    SIGN_EXPIRES = 60 * 1000  # API signature expiration time.

    @staticmethod
    def init_app(app):
        pass


class DevelopmentConfig(Config):
    DEBUG = True
    WTF_CSRF_ENABLED = False  # Whether to open CSRF protection.
    SQLALCHEMY_DATABASE_URI = "postgres://postgres:123@127.0.0.1:5432/ulord_development"
    SQLALCHEMY_TRACK_MODIFICATIONS = True
    SQLALCHEMY_ECHO = False  # Display sql statement


class TestingConfig(Config):
    TESTING = True
    WTF_CSRF_ENABLED = False
    SQLALCHEMY_DATABASE_URI = "postgres://postgres:123@127.0.0.1:5432/ulord_testing"
    SQLALCHEMY_TRACK_MODIFICATIONS = True


class ProductionConfig(Config):
    SQLALCHEMY_DATABASE_URI = "postgres://postgres:123@127.0.0.1:5432/ulord_production"
    SQLALCHEMY_TRACK_MODIFICATIONS = True


dconfig = {
    'development': DevelopmentConfig, 'testing': TestingConfig, 'prodection': ProductionConfig,
    'default': DevelopmentConfig}

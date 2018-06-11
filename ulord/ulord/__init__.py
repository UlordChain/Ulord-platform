# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from flask import Flask, Response, jsonify, abort, request
from .extensions import db, ma
from config import dconfig
from .utils import return_result
from .apiv1 import bpv1
from .utils.log import init_logging
from flask_cors import CORS
from sqlalchemy.exc import DatabaseError


class JSONResponse(Response):
    @classmethod
    def force_type(cls, response, environ=None):
        if isinstance(response, (list, dict)):
            response = jsonify(response)
        return super(JSONResponse, cls).force_type(response, environ)


class ULORDFlask(Flask):
    response_class = JSONResponse


app = ULORDFlask(__name__)
CORS(app, supports_credentials=True)


def config_app(app, config_name):
    app.logger.info('Setting up application...')
    config = dconfig[config_name]
    app.config.from_object(config)
    config.init_app(app)
    db.init_app(app)
    ma.init_app(app)
    init_logging(app)

    @app.before_request
    def before_request():
        # silent: if set to ``True`` this method will fail silently and return ``None``.
        if request.method == 'POST' and not request.get_json(silent=True):
            return return_result(20101)

    @app.teardown_request
    def teardown_request(exception):
        if isinstance(exception, DatabaseError):
            # 上一次查询发生异常不回滚, 会导致后面无法查询
            # <class 'sqlalchemy.exc.InternalError'> (psycopg2.InternalError)
            # current transaction is aborted, commands ignored until end of transaction block
            db.session.rollback()



def dispatch_apps(app):
    app.register_blueprint(bpv1)


def dispatch_handlers(app):
    @app.errorhandler(400)
    def bad_request_error(error):
        app.logger.error(error)
        return return_result(400), 400

    @app.errorhandler(403)
    def permission_error(error):
        app.logger.error(error)
        return return_result(403), 403

    @app.errorhandler(404)
    def page_not_found(error):
        app.logger.error(request.url+" - "+str(error))
        return return_result(404), 404

    @app.errorhandler(405)
    def page_not_found(error):
        app.logger.error(error)
        return return_result(405), 405

    @app.errorhandler(500)
    def internal_server_error(error):
        app.logger.error(error)
        return return_result(500), 500

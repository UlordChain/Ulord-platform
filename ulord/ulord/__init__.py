# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
import logging

logger = logging.getLogger(__name__)
from flask import Flask, Response, jsonify, abort,request
from .extensions import db,ma
from config import dconfig
from .utils import return_result
from .apiv1 import bpv1
from ulord.utils.log import init_logging
from flask_cors import CORS


class JSONResponse(Response):
    @classmethod
    def force_type(cls, response, environ=None):
        if isinstance(response, (list, dict)):
            response = jsonify(response)
        return super(JSONResponse, cls).force_type(response, environ)


class ULORDFlask(Flask):
    response_class = JSONResponse


app = ULORDFlask(__name__)
CORS(app,supports_credentials=True)

def config_app(app, config_name):
    logger.info('Setting up application...')
    config = dconfig[config_name]
    app.config.from_object(config)
    config.init_app(app)
    db.init_app(app)
    ma.init_app(app)
    init_logging(app)

    @app.before_request
    def before():
        # silent: if set to ``True`` this method will fail silently and return ``None``.
        if request.method=='POST' and not request.get_json(silent=True):
            return return_result(20101)


def dispatch_apps(app):
    app.register_blueprint(bpv1)


def dispatch_handlers(app):
    @app.errorhandler(400)
    def Bad_request_error(error):
        app.logger.error(error)
        return return_result(400), 400

    @app.errorhandler(403)
    def permission_error(error):
        app.logger.error(error)
        return return_result(403), 403

    @app.errorhandler(404)
    def page_not_found(error):
        app.logger.error(error)
        return return_result(404),404

    @app.errorhandler(405)
    def page_not_found(error):
        app.logger.error(error)
        return return_result(405), 405

    @app.errorhandler(500)
    def page_error(error):
        app.logger.error(error)
        return return_result(500), 500

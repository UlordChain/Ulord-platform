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
from ulord.apiv1 import bpv1


class JSONResponse(Response):
    @classmethod
    def force_type(cls, response, environ=None):
        if isinstance(response, (list, dict)):
            response = jsonify(response)
        return super(JSONResponse, cls).force_type(response, environ)


class ULORDFlask(Flask):
    response_class = JSONResponse


app = ULORDFlask(__name__)


def config_app(app, config_name):
    logger.info('Setting up application...')
    config = dconfig[config_name]
    app.config.from_object(config)
    config.init_app(app)
    db.init_app(app)
    ma.init_app(app)

    @app.before_request
    def before():
        if request.method=='POST' and not request.json:
            return return_result(20102)

    # @app.after_request  # def after_request(response):  #     try:  #         db.session.commit()  #     except Exception as e:  #         db.session.rollback()  #         print (e)  #         abort(500)  #     return response


def dispatch_apps(app):
    app.register_blueprint(bpv1)


def dispatch_handlers(app):
    @app.errorhandler(400)
    def Bad_request_error(error):
        return return_result(400), 400

    @app.errorhandler(403)
    def permission_error(error):
        return return_result(403), 403

    @app.errorhandler(404)
    def page_not_found(error):
        return return_result(404),404

    @app.errorhandler(405)
    def page_not_found(error):
        return return_result(405), 405

    @app.errorhandler(500)
    def page_error(error):
        return return_result(500), 500

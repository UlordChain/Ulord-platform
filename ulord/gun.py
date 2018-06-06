# -*- coding: utf-8 -*-
# @Date    : 2018/6/5
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from ulord import config_app, dispatch_apps, dispatch_handlers, app

"""Start the Server with Gunicorn"""
config_app(app, 'production')
dispatch_handlers(app)
dispatch_apps(app)

if __name__ == '__main__':
    app.run(port=4999)

# -*- coding: utf-8 -*-
# !/usr/bin/env python3
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# User Python3.6

# flask_script 使用来在运行web server之前运行其他脚本或者命令的插件
# 具体可参考: https://my.oschina.net/lijsf/blog/158828

import sys
from flask_script import Manager, Shell
from ulord import config_app, dispatch_apps, dispatch_handlers, db, app

# import logging
# logging.basicConfig(level=logging.INFO)
manager = Manager(app)


def make_shell_context():
    return dict(app=app, db=db)


manager.add_command("shell", Shell(make_context=make_shell_context))


@manager.option('-H', '--host', dest='host',help='host', default='0.0.0.0')
@manager.option('-P', '--port', dest='port',help='port', default=4999, type=int)
def runserver(host,port):
    config_app(app, 'development')
    dispatch_handlers(app)
    dispatch_apps(app)
    # print(app.url_map)
    app.run(host=host, port=port)


@manager.command
def initdb():
    config_app(app, 'development')
    try:
        # db.drop_all()
        db.create_all()
        print('Create tables success')
    except Exception as e:
        print('Create tables fail:', e)
        sys.exit(0)


if __name__ == '__main__':
    if len(sys.argv) == 1:
        sys.argv = sys.argv[:1]
        sys.argv.append('runserver')
    manager.run()

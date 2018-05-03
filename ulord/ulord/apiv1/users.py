# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from ulord.utils import return_result
from flask import request, g
from ulord.models import User
from ulord.extensions import db,auth
from werkzeug.security import generate_password_hash
from . import bpv1, get_jsonrpc_server
from ulord.forms import RegForm, LoginForm
from ulord.forms import validate_form


@auth.verify_password
def get_password(username,password):
    from werkzeug.security import check_password_hash
    p = "pbkdf2:sha256:50000$TuvgizYw$37c603e8e802145da79123862a80ac723fe774b5dc972d372ba2f6f39f95e4b2"
    print('password:%s'%password)
    if check_password_hash(p,password):
        return True


@bpv1.route('/index/')
@auth.login_required
def index():
    return "Hello, %s"%auth.username()

@bpv1.route('/users/reg/', methods=['POST'])
@validate_form(form_class=RegForm)
def reg():
    """User register"""
    form = g.form

    # The user login password is also the wallet's payment password.
    pay_password = generate_password_hash(form.password.data)
    form.password.data = pay_password

    try:
        server = get_jsonrpc_server()
        result = server.create(form.username.data, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20205)
    except Exception as e:
        print(e)
        return return_result(20205)
    user = User(**form.data)
    db.session.add(user)
    db.session.commit()
    return return_result(result=dict(id=user.id))


@bpv1.route('/users/login/', methods=['POST'])
@validate_form(form_class=LoginForm)
def login():
    username = g.form.username.data
    password = g.form.password.data

    user = User.query.filter_by(username=username).first()
    if user is None:
        return return_result(20003)

    if not user.check_password(password):
        return return_result(20004)

    return return_result(reason='success', result=dict(token='token'))


@bpv1.route('/users/edit/', methods=['POST'])
def edit():
    """ 修改密码, 同时更新appkey
    todo:同时需要修改钱包的密码
    """
    username = request.json.get('username')  # 正式环境需要获取已登录用户
    password = request.json.get('password')
    new_password = request.json.get('new_password')

    user = User.query.filter_by(username=username).first()
    if not user:
        return return_result(20003)

    if not user.check_password(password):
        return return_result(20004)

    user.set_password(new_password)
    return return_result(reason='success.')

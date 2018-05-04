# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from ulord.utils import return_result
from flask import request, g, current_app
from ulord.models import User
from ulord.extensions import db, auth
from werkzeug.security import generate_password_hash
from . import bpv1, get_jsonrpc_server
from ulord.forms import validate_form, RegForm, LoginForm, EditForm, ChangePasswordForm


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
        # print(form.username.data, pay_password)
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

    token = user.generate_auth_token(expiration=current_app.config['EXPIRATION'])
    return return_result(result=dict(token=token))


@bpv1.route('/users/edit/', methods=['POST'])
@validate_form(form_class=EditForm)
@auth.login_required
def edit():
    """ Modify developer data."""
    g.user.telphone = g.form.telphone.data
    g.user.email = g.form.email.data
    return return_result()


@bpv1.route('/users/changepassword/', methods=['POST'])
@validate_form(form_class=ChangePasswordForm)
@auth.login_required
def change_password():
    user = g.user
    old_password_hash = user.password_hash
    if not user.check_password(g.form.password.data):
        return return_result(20004)

    user.set_password(g.form.new_password.data)
    try:
        server = get_jsonrpc_server()
        result = server.password(user.username, old_password_hash, user.password_hash)
        print('result:', result)
        if result.get('success') is True:
            return return_result()
    except Exception as e:
        print(e)

    db.session.rollback()
    return return_result(20207)

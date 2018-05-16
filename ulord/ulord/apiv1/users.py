# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net


from Crypto import Random
from ulord.utils import return_result
from flask import request, g, current_app
from ulord.models import User,Role
from ulord.extensions import db, auth
from werkzeug.security import generate_password_hash
from . import bpv1, get_jsonrpc_server,admin_required,blocked_check
from ulord.forms import validate_form, RegForm, LoginForm, EditForm, ChangePasswordForm,EditUserRoleForm
from ulord.utils.rsa import rsahelper

@bpv1.route('/getpubkey')
def getpubkey():
    return rsahelper.pubkeybytes

@bpv1.route('/users/reg', methods=['POST'])
@validate_form(form_class=RegForm)
def reg():
    """User register"""
    form = g.form

    # The user login password is also the wallet's payment password.
    password = rsahelper.decrypt(form.password.data).decode('u8')
    pay_password = generate_password_hash(password)
    form.password.data = pay_password

    try:
        server = get_jsonrpc_server()
        # print(form.username.data, pay_password)
        result = server.create(form.username.data, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20205,result=result)
    except Exception as e:
        print(e)
        return return_result(20205)

    role=Role.query.filter_by(name='normal').first()
    user = User(**form.data)
    user.role_id=role.id
    db.session.add(user)
    db.session.commit()
    return return_result(result=dict(id=user.id))


@bpv1.route('/users/login', methods=['POST'])
@validate_form(form_class=LoginForm)
def login():
    username = g.form.username.data
    password = rsahelper.decrypt(g.form.password.data).decode('u8')
    user = User.query.filter_by(username=username).first()
    if user is None:
        return return_result(20003)

    if not user.check_password(password):
        return return_result(20004)

    token = user.generate_auth_token(expiration=current_app.config['EXPIRATION'])
    return return_result(result=dict(token=token))


@bpv1.route('/users/edit', methods=['POST'])
@auth.login_required
@blocked_check
@validate_form(form_class=EditForm)
def edit():
    """ Modify developer data."""
    g.user.telphone = g.form.telphone.data
    g.user.email = g.form.email.data
    return return_result()


@bpv1.route('/users/changepassword', methods=['POST'])
@auth.login_required
@blocked_check
@validate_form(form_class=ChangePasswordForm)
def change_password():
    user = g.user
    old_password_hash = user.password_hash

    password = rsahelper.decrypt(g.form.password.data).decode('u8')
    if not user.check_password(password):
        return return_result(20004)

    new_password_hash = generate_password_hash(rsahelper.decrypt(g.form.new_password.data).decode('u8'))
    try:
        server = get_jsonrpc_server()
        result = server.password(user.username, old_password_hash, new_password_hash)
        if result.get('success') is not True:
            print(result)
            return return_result(20207,result=result)
    except Exception as e:
        print(e)
        return return_result(20207)

    user.password=new_password_hash  # update password, auto commit
    return return_result()

@bpv1.route('/users/role/edit',methods=['POST'])
@auth.login_required
@admin_required
@validate_form(form_class=EditUserRoleForm)
def edit_user_role():
    """The administrator changes the developer account role."""
    user=User.query.get(g.form.id.data)
    if not user:
        return return_result(20003)
    user.role_id=g.form.role_id.data
    return return_result()
# -*- coding: utf-8 -*-
# @Time    : 2018/3/22
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.utils import return_result
from flask import request
from ulord.models import User
from ulord.extensions import db
from werkzeug.security import generate_password_hash
from . import bpv1, get_jsonrpc_server


@bpv1.route('/users/reg/', methods=['POST'])
def reg():
    """注册用户,返回appkey"""
    # 验证请求参数
    # args = self.reqparse.parse_args()
    username = request.json.get('username')
    password = request.json.get('password')
    email = request.json.get('email')
    telphone = request.json.get('telphone')
    role_id = request.json.get('role_id')
    # 与db存储相同的加密方式, 作为开发者钱包的支付密码
    pay_password = generate_password_hash(password)
    print(pay_password)
    # 注册前, 先创建钱包
    server = get_jsonrpc_server()
    try:
        result=server.create(username, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20205)
    except Exception as e:
        print (e)
        return return_result(20205)

    user = User(username=username, email=email, telphone=telphone, role_id=role_id, password_hash=pay_password)
    db.session.add(user)
    db.session.commit()
    return return_result(result=dict(id=user.id))


@bpv1.route('/users/login/', methods=['POST'])
def login():
    """ 获取appkey, 可以把appkey信息写入redis,作为查询 """
    username = request.json.get('username')
    password = request.json.get('password')

    user = User.query.filter_by(username=username).first()
    if user is None:
        return return_result(20003)

    if not user.check_password(password):
        return return_result(20004)

    return return_result(reason='success', result=dict(token='token'))


@bpv1.route('/users/edit/', methods=['POST'])
def edit():
    """ 修改密码, 同时更新appkey"""
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

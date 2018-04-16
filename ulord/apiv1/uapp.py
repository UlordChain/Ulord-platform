# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from ulord import return_result
from . import bpv1
from flask import request
from ulord.models import Application, User
from ulord.extensions import db
from ulord.utils.generate import generate_appkey
from ulord.schema import apps_schema


@bpv1.route("/app/add/", methods=['POST'])
def app_add():
    user_id = request.json.get('user_id')
    appname = request.json.get('appname')
    apptype_id = request.json.get('apptype_id')
    appdes = request.json.get('appdes')
    appkey = generate_appkey()
    secret = generate_appkey()

    uapp = Application(user_id=user_id, appname=appname, apptype_id=apptype_id, appdes=appdes, appkey=appkey,
                       secret=secret)
    db.session.add(uapp)
    db.session.commit()
    return return_result(result={'id': uapp.id,'appkey':uapp.appkey,'secret':uapp.secret})


@bpv1.route("/app/list/", methods=['POST'])
def app_list():
    user_id = request.json.get('user_id')  # 正式场景, 可能不需要传参,直接从登录状态中获得
    user = User.query.filter_by(id=user_id).first()
    uapps = user.app.all()
    result = apps_schema.dump(uapps).data
    return return_result(result=result)


@bpv1.route("/app/remove/", methods=['POST'])
def app_remove():
    _id = request.json.get('id')
    num = Application.query.filter_by(id=_id).delete()
    return return_result(result=dict(num=num))


@bpv1.route("/app/rebuild/", methods=['POST'])
def app_edit():
    """重新生成secret"""
    user_id = request.json.get('user_id')  # 登录用户
    _id = request.json.get('id')

    secret = generate_appkey()

    uapp = Application.query.get(_id)
    print(type(uapp.user_id),type(user_id))
    if str(uapp.user_id) != user_id:
        return return_result(10013)
    uapp.secret=secret
    return return_result(result=dict(secret=secret))

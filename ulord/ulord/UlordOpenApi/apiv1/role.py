# -*- coding: utf-8 -*-
# @Date    : 2018/4/10
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1
from flask import request
from ulord.extensions import db
from ulord.models.users import Role
from ulord.utils import return_result
from ulord.schema import roles_schema


@bpv1.route('/role/add/', methods=['POST'])
def role_add():
    """管理员才能调用,需要权限检查"""
    name = request.json.get('name')
    des = request.json.get('des')
    role = Role(name=name, des=des)
    db.session.add(role)
    db.session.commit()  # 先提交,才能取id值
    return return_result(result={'id': role.id})


@bpv1.route('/role/list/')
def role_list():
    roles = Role.query.all()
    result=roles_schema.dump(roles).data
    return return_result(result=result)


@bpv1.route('/role/edit/', methods=['POST'])
def role_edit():
    id = request.json.get('id')
    name = request.json.get('name')
    des = request.json.get('des')

    role = Role.query.get(id)
    if not role:
        return return_result(errcode=20005)

    if name is not None:
        role.name = name
    if des is not None:
        role.des = des
    db.session.add(role)
    return return_result()



@bpv1.route('/role/remove/', methods=['POST'])
def role_remove():
    _id = request.json.get('id')
    # db.session.delete(role)  # 需要先查询,在删除
    # 下面的方式只需要一步
    num = Role.query.filter_by(id=_id).delete()
    return return_result(result=dict(num=num))

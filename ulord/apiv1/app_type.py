# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from . import bpv1
from flask import request
from ulord.models import Type
from ulord.extensions import db
from . import return_result
from ulord.models import Type
from ulord.schema import types_schema
from ulord.utils.formatter import get_tree


@bpv1.route('/type/add/', methods=['POST'])
def type_add():
    """管理员权限"""
    parent_id = request.json.get('parent_id', None)
    name = request.json.get('name')
    des = request.json.get('des')

    t = Type(parent_id=parent_id, name=name, des=des)
    db.session.add(t)
    db.session.commit()
    return return_result(result=dict(id=t.id))


@bpv1.route('/type/list/')
def type_list():
    tmodel_list = Type.query.all()
    tlist = types_schema.dump(tmodel_list).data
    result = get_tree(tlist, None)
    return return_result(result=result)


@bpv1.route('/type/edit/', methods=['POST'])
def type_edit():
    id = request.json.get('id')
    name = request.json.get('name')
    des = request.json.get('des')
    parent_id = request.json.get('parent_id')

    t=Type.query.get_or_404(id)
    if name is not None:
        t.name=name
    if des is not None:
        t.des=des
    if parent_id is not None:
        t.parent_id=parent_id
    db.session.add(t)
    return return_result()

@bpv1.route('/type/remove/',methods=['POST'])
def type_remove():
    _id =request.json.get('id')
    num=Type.query.filter_by(id=_id).delete()
    return return_result(result=dict(num=num))
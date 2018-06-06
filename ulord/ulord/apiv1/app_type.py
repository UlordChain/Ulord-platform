# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from . import bpv1, admin_required, blocked_check
from flask import g
from ulord.extensions import db, auth
from . import return_result
from ulord.models import Type
from ulord.schema import types_schema
from ulord.utils.formatter import get_tree
from ulord.forms import validate_form, AddTypeForm, EditTypeForm, RemoveTypeForm


@bpv1.route('/type/add', methods=['POST'])
@auth.login_required
@blocked_check
@admin_required
@validate_form(form_class=AddTypeForm)
def type_add():
    t = Type(**g.form.data)
    db.session.add(t)
    db.session.commit()
    return return_result(result=dict(id=t.id))


@bpv1.route('/type/list')
@auth.login_required
@blocked_check
def type_list():
    types = Type.query.all()
    tlist = types_schema.dump(types).data
    result = get_tree(tlist, None)
    return return_result(result=result)


@bpv1.route('/type/edit', methods=['POST'])
@auth.login_required
@blocked_check
@admin_required
@validate_form(form_class=EditTypeForm)
def type_edit():
    t = Type.query.get_or_404(g.form.id.data)
    t.des = g.form.des.data
    t.parent_id = g.form.parent_id.data
    db.session.commit()
    return return_result()


@bpv1.route('/type/remove', methods=['POST'])
@auth.login_required
@blocked_check
@admin_required
@validate_form(form_class=RemoveTypeForm)
def type_remove():
    num = Type.query.filter_by(id=g.form.id.data).delete()
    db.session.commit()
    return return_result(result=dict(num=num))

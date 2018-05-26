# -*- coding: utf-8 -*-
# @Date    : 2018/4/10
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1,admin_required,blocked_check
from flask import g
from ulord.extensions import db,auth
from ulord.models.users import Role
from ulord.utils import return_result
from ulord.schema import roles_schema
from ulord.forms import validate_form,AddRoleForm,EditRoleForm,RemoveRoleForm


@bpv1.route('/role/add', methods=['POST'])
@auth.login_required
@blocked_check
@admin_required
@validate_form(form_class=AddRoleForm)
def role_add():
    role = Role(**g.form.data)
    db.session.add(role)
    # You submit it, and then you take the id.
    db.session.commit()
    return return_result(result={'id': role.id})


@bpv1.route('/role/list/<int:page>/<int:num>')
@auth.login_required
@blocked_check
@admin_required
def role_list(page,num):
    roles = Role.query.order_by(Role.id.asc()).paginate(page,num,error_out=False)
    total=roles.total
    pages=roles.pages
    result=roles_schema.dump(roles.items).data
    return return_result(result=dict(total=total,pages=pages,records=result))


@bpv1.route('/role/edit', methods=['POST'])
@auth.login_required
@blocked_check
@admin_required
@validate_form(form_class=EditRoleForm)
def role_edit():
    id = g.form.id.data
    des = g.form.des.data
    role=Role.query.get(id)
    role.des=des
    db.session.commit()
    return return_result()


@bpv1.route('/role/remove', methods=['POST'])
@auth.login_required
@blocked_check
@admin_required
@validate_form(form_class=RemoveRoleForm)
def role_remove():
    num = Role.query.filter_by(id=g.form.id.data).delete()  # Delete directly
    db.session.commit()
    return return_result(result=dict(num=num))

# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from ulord import return_result
from . import bpv1, blocked_check
from flask import g, current_app
from ulord.models import Application, User
from ulord.extensions import db, auth
from ulord.utils.generate import generate_appkey
from ulord.utils.formatter import add_timestamp
from ulord.schema import apps_schema
from ulord.forms import validate_form, AddAppForm, RebuildAppForm, RemoveAppForm,EditAppForm


@bpv1.route("/app/add", methods=['POST'])
@auth.login_required
@blocked_check
@validate_form(form_class=AddAppForm)
def app_add():
    uapp_max_count=current_app.config['UAPP_MAX_COUNT']
    if g.user.app.count() >= uapp_max_count:
        return return_result(10016, reason='用户可新建的应用数已达最大值({})'.format(uapp_max_count))

    uapp = Application(user_id=g.user.id, appkey=generate_appkey(), secret=generate_appkey(), **g.form.data)
    db.session.add(uapp)
    db.session.commit()
    return return_result(result={'id': uapp.id, 'appkey': uapp.appkey, 'secret': uapp.secret})


@bpv1.route("/app/list/<int:page>/<int:num>")
@auth.login_required
@blocked_check
def app_list(page, num):
    uapps = g.user.app.order_by(Application.id.desc()).paginate(page,num,error_out=False)
    records=apps_schema.dump(uapps.items).data
    records=add_timestamp(records)
    return return_result(result=dict(total=uapps.total,pages=uapps.pages,records=records))


@bpv1.route("/app/rebuild", methods=['POST'])
@auth.login_required
@blocked_check
@validate_form(form_class=RebuildAppForm)
def app_rebuild():
    """To regenerate the secret"""
    uapp=Application.query.filter_by(id=g.form.id.data, user_id=g.user.id).first()
    if not uapp:
        return return_result(20005)
    secret = generate_appkey()
    uapp.secret = secret
    db.session.commit()
    return return_result(result=dict(secret=secret))


@bpv1.route("/app/edit", methods=['POST'])
@auth.login_required
@blocked_check
@validate_form(form_class=EditAppForm)
def app_edit():
    """To regenerate the secret"""

    uapp=Application.query.filter_by(id=g.form.id.data, user_id=g.user.id).first()
    if not uapp:
        return return_result(20005)
    uapp.appdes=g.form.appdes.data
    db.session.commit()
    return return_result()


@bpv1.route("/app/remove", methods=['POST'])
@auth.login_required
@blocked_check
@validate_form(form_class=RemoveAppForm)
def app_remove():
    num = Application.query.filter_by(id=g.form.id.data,user_id=g.user.id).delete()
    db.session.commit()
    return return_result(result=dict(num=num))




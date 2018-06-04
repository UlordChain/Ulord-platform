# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1, appkey_check
from flask import g
from ulord.extensions import db
from ulord.models import Content, Consume
from ulord.schema import contents_schema, consumes_schema
from ulord import return_result
from ulord.utils.formatter import add_timestamp
from ulord.forms import (validate_form, PublishedForm, PurchaseForm, ContentListForm, GetsForm, ClaimIdConsumeForm,
                        ClaimAccountForm)


@bpv1.route("/content/gets", methods=['POST'])
@appkey_check
@validate_form(form_class=GetsForm)
def gets():
    appkey = g.appkey
    ids = g.form.ids.data
    query = Content.query.filter(Content.appkey == appkey, Content.enabled == True)
    if ids:
        query = query.filter(Content.id.in_(ids))
    contents = query.all()
    records = contents_schema.dump(contents).data
    records = add_timestamp(records)
    return return_result(result=records)


@bpv1.route("/content/list/<int:page>/<int:num>", methods=['GET', 'POST'])
@appkey_check
@validate_form(form_class=ContentListForm)
def content_list(page, num):
    """因为price使用了Numeric类型, 在转换为python对象时,对应 Decimal 类型
    Decimal类型,在使用jsonify进行json转换时会发生异常, 这是因为标准库json不支持转换Decimal

    解决办法: 安装simplejson, 此库的dumps有一个use_decimal参数(默认True,可以转换Decimal)
    当你安装了simplejson, flask的jsonify会自动使用它,而放弃系统标准库中的json库
    ref: https://github.com/pallets/flask/issues/835
    """
    appkey = g.appkey
    keyword = g.form.keyword.data
    query = Content.query.filter(Content.appkey == appkey, Content.enabled == True)
    if keyword:
        query = query.filter(Content.title.like('%{}%'.format(keyword)))
    contents = query.order_by(Content.create_timed.desc()).paginate(page, num, error_out=False)
    total = contents.total
    pages = contents.pages
    records = contents_schema.dump(contents.items).data
    records = add_timestamp(records)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route("/content/publish/list/<int:page>/<int:num>", methods=['POST'])
@appkey_check
@validate_form(form_class=PublishedForm)
def published(page, num):
    """List of published resources"""
    appkey = g.appkey
    author = g.form.author.data
    contents = Content.query.filter_by(author=author, appkey=appkey, enabled=True).paginate(page, num, error_out=False)
    total = contents.total
    pages = contents.pages
    records = contents_schema.dump(contents.items).data
    records = add_timestamp(records)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route("/content/claim/list/<int:page>/<int:num>", methods=['POST'])
@appkey_check
@validate_form(form_class=ClaimIdConsumeForm)
def claim_id_consume(page, num):
    """All relevant consumption records of a resource"""
    appkey = g.appkey
    claim_id = g.form.claim_id.data

    query = Consume.query.filter_by(appkey=appkey, claim_id=claim_id)
    consumes = query.order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
    total = consumes.total
    pages = consumes.pages
    records = consumes_schema.dump(consumes.items).data
    records = add_timestamp(records)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route("/content/claim/account", methods=['POST'])
@appkey_check
@validate_form(form_class=ClaimAccountForm)
def claim_account():
    """Multiple resource statistics"""
    appkey = g.appkey
    claim_ids = g.form.claim_ids.data
    query = Consume.query.with_entities(Consume.claim_id, db.func.sum(Consume.price).label('sum'),
                                db.func.count(Consume.price).label('count')).filter(Consume.appkey == appkey)
    if claim_ids:
        query = query.group_by(Consume.claim_id).having(Consume.claim_id.in_(claim_ids))
    records = query.all()
    return return_result(result=records)


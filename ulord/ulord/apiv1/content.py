# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1, appkey_check
from flask import request, g
from ulord.models import Content, Consume
from ulord.schema import contents_schema
from ulord import return_result
from ulord.utils.formatter import add_timestamp


@bpv1.route("/content/list/<int:page>/<int:num>")
@appkey_check
def content_list(page, num):
    """因为price使用了Numeric类型, 在转换为python对象时,对应 Decimal 类型
    Decimal类型,在使用jsonify进行json转换时会发生异常, 这是因为标准库json不支持转换Decimal

    解决办法: 安装simplejson, 此库的dumps有一个use_decimal参数(默认True,可以转换Decimal)
    当你安装了simplejson, flask的jsonify会自动使用它,而放弃系统标准库中的json库
    ref: https://github.com/pallets/flask/issues/835
    """
    appkey = g.appkey
    contents = Content.query.filter(Content.appkey == appkey, Content.enabled == True).order_by(
        Content.create_timed.desc()).paginate(page, num, error_out=False)
    total = contents.total
    pages = contents.pages
    result = contents_schema.dump(contents.items).data
    return return_result(result=dict(total=total, pages=pages, records=result))


@bpv1.route("/content/consume/list/<int:page>/<int:num>", methods=['POST'])
@appkey_check
def consumed(page, num):
    """用户已消费的资源记录"""
    appkey = g.appkey
    customer = request.json.get('customer')
    category = request.json.get('category')  # 0: 消费支出 1: 广告收入 其他:all
    query = Content.query.with_entities(Content.id, Content.author, Content.title, Consume.txid, Content.enabled,
                                        Content.claim_id, Consume.price,
                                        Consume.create_timed). \
                        join(Consume,Content.claim_id==Consume.claim_id). \
                        filter(Content.appkey == appkey, Consume.customer == customer)
    if category == 0:
        query = query.filter(Consume.price > 0)
    if category == 1:
        query = query.filter(Consume.price < 0)

    records = query.order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages

    records=add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route("/content/publish/list/<int:page>/<int:num>", methods=['POST'])
@appkey_check
def published(page, num):
    """作者已发布资源被消费记录"""
    appkey = g.appkey
    author = request.json.get('author')
    category = request.json.get('category')  # 0: 资源收入 1: 广告支出 其他:all

    query = Content.query.with_entities(Content.id, Content.title, Consume.txid, Content.enabled,
                                        Content.claim_id, Consume.customer,Consume.price, Consume.create_timed). \
                        join(Consume,Content.claim_id==Consume.claim_id). \
                        filter(Content.appkey == appkey, Content.author==author)
    if category == 0:
        query = query.filter(Consume.price > 0)
    if category == 1:
        query = query.filter(Consume.price < 0)

    records = query.order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records=add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route("/content/view", methods=['POST'])
@appkey_check
def view():
    """资源购买量"""
    appkey=g.appkey
    claim_id = request.json.get('claim_id')
    count = Consume.query.filter_by(claim_id=claim_id,appkey=appkey).count()
    return return_result(result=dict(count=count))

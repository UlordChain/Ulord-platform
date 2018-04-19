# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1, appkey_check
from flask import request, g
from ulord.models import Content, Consume
from ulord.schema import contents_schema, consumes_schema
from ulord import return_result


@bpv1.route("/content/list/<int:page>/<int:num>/")
@appkey_check
def content_list(page, num):
    """因为price使用了Numeric类型, 在转换为python对象时,对应 Decimal 类型
    Decimal类型,在使用jsonify进行json转换时会发生异常, 这是因为标准库json不支持转换Decimal

    解决办法: 安装simplejson, 此库的dumps有一个use_decimal参数(默认True,可以转换Decimal)
    当你安装了simplejson, flask的jsonify会自动使用它,而放弃系统标准库中的json库
    ref: https://github.com/pallets/flask/issues/835
    """
    appkey = g.appkey
    contents = Content.query.filter(Content.appkey == appkey, Content.enabled == True).paginate(page, num,error_out=False)
    total = contents.total
    pages = contents.pages
    result = contents_schema.dump(contents.items).data
    return return_result(result=dict(total=total, pages=pages, data=result))


@bpv1.route("/content/bought/<int:page>/<int:num>/", methods=['POST'])
@appkey_check
def bought(page, num):
    """已购买"""
    appkey = g.appkey
    customer = request.json.get('customer')

    consumes = Consume.query.filter_by(appkey=appkey,customer=customer).paginate(page, num,error_out=False)
    total = consumes.total
    pages = consumes.pages
    consumes = consumes_schema.dump(consumes.items).data
    contents=[consume['content'] for consume in consumes ]
    result=contents_schema.dump(contents).data
    consumes=[]
    ads=[]
    for r in result:
        if r.get('price')>=0:
            consumes.append(r)
        else:
            ads.append(r)
    return return_result(result=dict(total=total,pages=pages,consumes=consumes,ads=ads))

@bpv1.route("/content/published/<int:page>/<int:num>/", methods=['POST'])
@appkey_check
def published (page, num):
    """已发布"""
    appkey = g.appkey
    author = request.json.get('author')

    contents = Content.query.filter_by(appkey=appkey,author=author).paginate(page, num,error_out=False)
    total = contents.total
    pages = contents.pages
    result = contents_schema.dump(contents.items).data
    return return_result(result=dict(total=total,pages=pages,data=result))
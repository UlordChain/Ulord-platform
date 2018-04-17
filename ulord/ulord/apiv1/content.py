# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1
from flask import request
from ulord.models import Content
from ulord.schema import contents_schema
from ulord import return_result
from . import appkey_check

@bpv1.route("/content/list/<int:page>/<int:num>/")
@appkey_check
def content_list(page,num):
    """因为price使用了Numeric类型, 在转换为python对象时,对应 Decimal 类型
    Decimal类型,在使用jsonify进行json转换时会发生异常, 这是因为标准库json不支持转换Decimal

    解决办法: 安装simplejson, 此库的dumps有一个use_decimal参数(默认True,可以转换Decimal)
    当你安装了simplejson, flask的jsonify会自动使用它,而放弃系统标准库中的json库
    ref: https://github.com/pallets/flask/issues/835
    """
    appkey = request.headers.get('appkey')

    # error_out:超过页数, 显示404
    contents = Content.query.filter(Content.appkey == appkey, Content.enabled == True).paginate(page,num,error_out=True)
    result=contents_schema.dump(contents.items).data
    return return_result(result=result)
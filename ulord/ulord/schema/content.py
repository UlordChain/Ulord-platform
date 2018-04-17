# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Content

_all__=['content_schema','contents_schema']

class AppSchema(ma.ModelSchema):
    class Meta:
        model=Content
        exclude=('txid','enabled','consumes','ipfs_hash')

content_schema=AppSchema()
contents_schema=AppSchema(many=True)
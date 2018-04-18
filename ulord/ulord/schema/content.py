# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Content,Consume

_all__=['content_schema','contents_schema','consume_schema','consumes_schema']

class ContentSchema(ma.ModelSchema):
    class Meta:
        model=Content
        exclude=('txid','enabled','consumes','ipfs_hash')

content_schema=ContentSchema()
contents_schema=ContentSchema(many=True)

class ConsumeSchema(ma.ModelSchema):
    class Meta:
        fields=['content']
        # model=Consume
        # exclude = ('txid', 'appkey','customer')

consume_schema=ConsumeSchema()
consumes_schema=ConsumeSchema(many=True)
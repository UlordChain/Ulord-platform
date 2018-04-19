# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Content,Consume

_all__ = ['content_schema','contents_schema','content_consume_schema','content_consumes_schema']

class ContentSchema(ma.ModelSchema):
    class Meta:
        model=Content
        exclude=('txid','enabled','consumes','ipfs_hash')

content_schema=ContentSchema()
contents_schema=ContentSchema(many=True)

class ContentConsumeSchema(ma.ModelSchema):
    class Meta:
        fields=['content']

content_consume_schema=ContentConsumeSchema()
content_consumes_schema=ContentConsumeSchema(many=True)


class ConsumeSchema(ma.ModelSchema):
    class Meta:
        fields=('txid','claim_id','customer','price','create_timed')

consume_schema = ConsumeSchema()
consume_schema = ConsumeSchema(many=True)




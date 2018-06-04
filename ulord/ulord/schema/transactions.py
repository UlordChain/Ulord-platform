# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Content, Consume, Tag

_all__ = ['content_schema', 'contents_schema', 'consume_schema', 'consumes_schema', 'contenthistory_schema',
          'contenthistorys_schema']


class TagSchema(ma.ModelSchema):
    class Meta:
        fields = ('name',)


class ContentSchema(ma.ModelSchema):
    class Meta:
        # model = Content
        fields = (
            'id', 'claim_id', 'author', 'title', 'price', 'content_type', 'currency', 'des', 'status', 'create_timed',
            'update_timed', 'tags', 'create_timed_timestamp', 'update_timed_timestamp', 'enabled')

    # 参考 http://marshmallow.readthedocs.io/en/latest/nesting.html?highlight=Nested
    # 如果是使用model,就不需要nested了, 内部实现应该做了处理
    tags = ma.Nested(TagSchema, many=True, only='name')


content_schema = ContentSchema()
contents_schema = ContentSchema(many=True)


class ConsumeSchema(ma.ModelSchema):
    class Meta:
        model = Consume
        exclude = ('appkey', 'content')


consume_schema = ConsumeSchema()
consumes_schema = ConsumeSchema(many=True)


class ContentHistorySchema(ma.ModelSchema):
    class Meta:
        model = Content
        exclude = ('create_timed', 'update_timed', 'id', 'tags', 'enabled', 'consumes')


contenthistory_schema = ContentHistorySchema()
contenthistorys_schema = ContentHistorySchema(many=True)
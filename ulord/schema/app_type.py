# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Type

_all__ = ['type_schema', 'types_schema']


class TypeSchema(ma.ModelSchema):
    class Meta:
        fields=('id','parent_id','name','des')
        # model = Type
        # exclude = ('app',)


type_schema = TypeSchema()
types_schema = TypeSchema(many=True)

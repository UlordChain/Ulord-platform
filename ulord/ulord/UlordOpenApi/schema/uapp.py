# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Application

_all__=['app_schema','apps_schema']

class AppSchema(ma.ModelSchema):
    class Meta:
        # fields=('id','name','des')
        model=Application
        exclude=('user_id','user')

app_schema=AppSchema()
apps_schema=AppSchema(many=True)
# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Role

_all__=['role_schema','roles_schema']

class RoleSchema(ma.ModelSchema):
    class Meta:
        # fields=('id','name','des')
        model=Role
        exclude=('user',)

role_schema=RoleSchema()
roles_schema=RoleSchema(many=True)
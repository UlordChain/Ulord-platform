# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import ma
from ulord.models import Application, Role, Type,User

_all__ = ['app_schema', 'apps_schema', 'role_schema', 'roles_schema', 'type_schema',
          'types_schema','user_schema','users_schema']


class TypeSchema(ma.ModelSchema):
    class Meta:
        fields = ('id', 'parent_id', 'name', 'des')  # model = Type  # exclude = ('app',)
type_schema = TypeSchema()
types_schema = TypeSchema(many=True)


class AppSchema(ma.ModelSchema):
    class Meta:
        # fields=('id','name','des')
        model = Application
        exclude = ('user_id', 'user')
    type = ma.Nested(TypeSchema, only='name')
app_schema = AppSchema()
apps_schema = AppSchema(many=True)


class RoleSchema(ma.ModelSchema):
    class Meta:
        # fields=('id','name','des')
        model = Role
        exclude = ('user',)
role_schema = RoleSchema()
roles_schema = RoleSchema(many=True)


class UserSchema(ma.ModelSchema):
    class Meta:
        # fields=('id','name','des')
        model = User
        exclude = ('app','password_hash')
user_schema = UserSchema()
users_schema = UserSchema(many=True)
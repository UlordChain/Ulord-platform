# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField
from wtforms.validators import Email, DataRequired, Length, Optional
from .validators import Unique,Exists
from ulord.models import Role

__all__ = ['AddRoleForm','EditRoleForm','RemoveRoleForm']


class AddRoleForm(FlaskForm):
    name = StringField('name', validators=[DataRequired(), Length(max=32), Unique(Role, Role.name)])
    des = StringField('des', validators=[DataRequired(), Length(max=500)])

class EditRoleForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired(),Exists(Role,Role.id)])
    des = StringField('des', validators=[DataRequired(), Length(max=500)])

class RemoveRoleForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired(), Exists(Role, Role.id)])


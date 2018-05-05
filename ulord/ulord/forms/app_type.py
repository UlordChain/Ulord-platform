# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField
from wtforms.validators import DataRequired, Length, Optional
from .validators import Exists, Unique
from ulord.models import Type

__all__ = ['AddTypeForm', 'EditTypeForm', 'RemoveTypeForm']


class AddTypeForm(FlaskForm):
    parent_id = IntegerField('parent_id', validators=[Optional(),Exists(Type,Type.id)])
    name = StringField('name', validators=[DataRequired(), Length(max=32), Unique(Type, Type.name)])
    des = StringField('des', validators=[Optional(), Length(max=256)], filters=[lambda x: x or None])


class EditTypeForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired(),Exists(Type,Type.id)])
    des = StringField('des', validators=[Optional(), Length(max=256)], filters=[lambda x: x or None])
    parent_id = IntegerField('parent_id', validators=[Optional(), Exists(Type, Type.id)])

class RemoveTypeForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired(),Exists(Type,Type.id)])

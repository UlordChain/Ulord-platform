# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField
from wtforms.validators import Email, DataRequired, Length, Optional,ValidationError
from .validators import Exists,Unique
from ulord.models import Application,Type
from flask import g
from . import return_result

__all__ = ['AddAppForm','RebuildAppForm','RemoveAppForm','EditAppForm']


class AddAppForm(FlaskForm):
    appname = StringField('appname', validators=[DataRequired(), Length(max=128)
                                                 ])
    appdes = StringField('appdes', validators=[Optional(), Length(max=500)],filters=[lambda x: x or None])
    apptype_id=IntegerField('apptype_id',validators=[DataRequired(),Exists(Type,Type.id)])

    def validate_appname(self,field):
        check=Application.query.filter_by(appname=field.data,user_id=g.user.id).first()
        if check:
            raise ValidationError('This filed already exists.')

class RebuildAppForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired()])

class EditAppForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired()])
    appdes = StringField('appdes', validators=[DataRequired(), Length(max=500)], filters=[lambda x: x or None])

class RemoveAppForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired()])


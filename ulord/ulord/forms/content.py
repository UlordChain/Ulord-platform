# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField, BooleanField, FloatField
from wtforms.validators import Email, DataRequired, Length, Optional, ValidationError,StopValidation
from .validators import Unique, Exists, RsaCheck, WalletUnique, WalletExists
from ulord.models import User, Role
from .custom_fields import TagListField
from ulord.models import Content
from flask import g

__all__ = ['ConsumedForm', 'PublishedForm', 'PurchaseForm']


class ConsumedForm(FlaskForm):
    customer = StringField('customer', validators=[DataRequired(),Length(max=64)])
    category = IntegerField('category', validators=[Optional()])

class PublishedForm(FlaskForm):
    author = StringField('author', validators=[DataRequired(), Length(max=64)])
    category = IntegerField('category', validators=[Optional()])

class PurchaseForm(FlaskForm):
    claim_id = StringField('claim_id', validators=[DataRequired(), Length(max=40)])


# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField
from wtforms.validators import DataRequired, Length, Optional
from .custom_fields import TagListField as IdListField

__all__ = ['PublishedForm', 'PurchaseForm', 'ClaimIdConsumeForm', 'ClaimAccountForm', 'ContentListForm','GetsForm']


class PublishedForm(FlaskForm):
    author = StringField('author', validators=[DataRequired(), Length(max=64)])


class ClaimIdConsumeForm(FlaskForm):
    claim_id = StringField('claim_id', validators=[DataRequired(), Length(max=40)])


class ClaimAccountForm(FlaskForm):
    claim_ids = IdListField('claim_ids', validators=[DataRequired()])


class PurchaseForm(FlaskForm):
    claim_id = StringField('claim_id', validators=[DataRequired(), Length(max=40)])


class ContentListForm(FlaskForm):
    keyword = StringField('keyword', validators=[Optional(), Length(max=128)])


class GetsForm(FlaskForm):
    ids = IdListField('ids', validators=[DataRequired()])

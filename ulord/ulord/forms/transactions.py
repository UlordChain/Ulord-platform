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

__all__ = ['CreateWalletForm', 'PayToUserForm', 'BalanceForm', 'PublishForm', 'CheckForm', 'ConsumeForm',
           'AccountInForm','AccountOutForm','AccountInOutForm','PublishCountForm','AccountForm']


class CreateWalletForm(FlaskForm):
    username = StringField('username', validators=[DataRequired()])
    pay_password = StringField('pay_password', validators=[DataRequired()])


class PayToUserForm(FlaskForm):
    is_developer = BooleanField('is_developer', false_values=(0, "false", False))
    # send_user not use Optional validator, It will stop the validation chain.
    send_user = StringField('send_user')
    pay_password = StringField('pay_password')
    recv_user = StringField('recv_user', validators=[DataRequired(), WalletExists()])
    amount = IntegerField('amount', validators=[DataRequired("Error parameter values.")])

    def validate_send_user(self, field):
        if self.is_developer.data is False:
            field.validate(self, [DataRequired(), WalletExists()])

    def validate_pay_password(self, field):
        if self.is_developer.data is False:
            field.validate(self, [DataRequired()])

    def validate_amount(self, field):
        if field.data <= 0:
            raise ValidationError('The amount must be greater than 0.')


class BalanceForm(FlaskForm):
    is_developer = BooleanField('is_developer', false_values=(0, "false", False))
    username = StringField('username', validators=[DataRequired(), WalletExists()])
    pay_password = StringField('pay_password', validators=[DataRequired()])


class PublishForm(FlaskForm):
    author = StringField('author', validators=[DataRequired(), Length(max=64), WalletExists(is_wallet_name=False)])
    pay_password = StringField('pay_password', validators=[DataRequired()])
    title = StringField('title', validators=[DataRequired(), Length(max=64)])
    tags = TagListField('tags', validators=[DataRequired()])
    udfs_hash = StringField('udfs_hash', validators=[DataRequired(), Length(max=46)])
    price = FloatField('price', validators=[DataRequired()])
    content_type = StringField('content_type', validators=[DataRequired(), Length(max=16)])
    description = StringField('description', validators=[Optional()])


class CheckForm(FlaskForm):
    customer = StringField('customer', validators=[DataRequired(), WalletExists(is_wallet_name=False)])
    claim_ids = TagListField('claim_ids', validators=[DataRequired()])


class ConsumeForm(FlaskForm):
    customer = StringField('customer', validators=[DataRequired(), WalletExists(is_wallet_name=False)])
    claim_id = StringField('claim_id', validators=[DataRequired(), Length(max=40)])
    customer_pay_password = StringField('customer_pay_password')
    author_pay_password = StringField('author_pay_password')

    def validate_claim_id(self, field):
        content = Content.query.filter_by(claim_id=field.data, appkey=g.appkey).first()
        if not content:
            raise ValidationError("Claim id doesn't exist")
        self.content = content
        if content.price >= 0:  # Is it advertising
            self.is_ad = False
        else:
            self.is_ad = True


    def validate_customer_pay_password(self, field):
        is_ad=getattr(self,'is_ad',None)
        if is_ad is False:
            field.validate(self, [DataRequired()])

    def validate_author_pay_password(self, field):
        is_ad = getattr(self, 'is_ad', None)
        if is_ad is True:
            field.validate(self, [DataRequired()])

class AccountInForm(FlaskForm):
    username = StringField('username', validators=[DataRequired(),Length(max=64)])

class AccountOutForm(FlaskForm):
    username = StringField('username', validators=[DataRequired(), Length(max=64)])

class AccountInOutForm(FlaskForm):
    username = StringField('username', validators=[DataRequired(), Length(max=64)])

class PublishCountForm(FlaskForm):
    author = StringField('author', validators=[DataRequired(), Length(max=64)])

class AccountForm(FlaskForm):
    username = StringField('username', validators=[DataRequired(), Length(max=64)])
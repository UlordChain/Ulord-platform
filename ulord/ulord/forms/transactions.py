# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField, BooleanField,FloatField
from wtforms.validators import Email, DataRequired, Length, Optional, ValidationError
from .validators import Unique, Exists, RsaCheck, WalletUnique, WalletExists
from ulord.models import User, Role
from .custom_fields import TagListField

__all__ = ['CreateWalletForm', 'PayToUserForm','BalanceForm','PublishForm']


class CreateWalletForm(FlaskForm):
    username = StringField('username', validators=[DataRequired(), WalletUnique()])
    pay_password = StringField('pay_password', validators=[DataRequired()])


class PayToUserForm(FlaskForm):
    is_developer = BooleanField('is_developer',false_values=(0,"false",False,"0"))
    # send_user not use Optional validator, It will stop the validation chain.
    send_user = StringField('send_user')
    pay_password = StringField('pay_password')
    recv_user = StringField('recv_user', validators=[DataRequired(), WalletExists()])
    amount = IntegerField('amount', validators=[DataRequired("Error parameter values.")])

    def validate_send_user(self,field):
        if self.is_developer.data is False:
            field.validate(self,[DataRequired(),WalletExists()])

    def validate_pay_password(self,field):
        if self.is_developer.data is False:
            field.validate(self,[DataRequired()])

    def validate_amount(self, field):
        if field.data <= 0:
            raise ValidationError('The amount must be greater than 0.')


class BalanceForm(FlaskForm):
    is_developer = BooleanField('is_developer', false_values=(0, "false", False, "0"))
    username = StringField('username', validators=[DataRequired(), WalletExists()])
    pay_password = StringField('pay_password', validators=[DataRequired()])


class PublishForm(FlaskForm):
    author=StringField('author',validators=[DataRequired(),Length(max=64),WalletExists()])
    pay_password = StringField('pay_password', validators=[DataRequired()])
    title=StringField('title',validators=[DataRequired(),Length(max=64)])
    tags=TagListField('tags')
    udfs_hash = StringField('udfs_hash', validators=[DataRequired(),Length(max=46)])
    price=FloatField('price',validators=[DataRequired()])
    content_type=StringField('content_type', validators=[DataRequired(),Length(max=16)])
    description=StringField('description', validators=[Optional()])

class EditUserRoleForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired(), Exists(User, User.id)])
    role_id = IntegerField('id', validators=[DataRequired(), Exists(Role, Role.id)])

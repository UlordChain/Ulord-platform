# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField
from wtforms.validators import Email, DataRequired, Length, Optional
from .validators import Unique, Exists,RsaCheck
from ulord.models import User, Role

__all__ = ['RegForm', 'LoginForm', 'EditForm', 'ChangePasswordForm','EditUserRoleForm','UserListForm']


class RegForm(FlaskForm):
    """
    The solution to the StringField default value of "" not None.
    https://stackoverflow.com/questions/21831216/get-none-from-a-fields-data-in-instead-of-an-empty-string
    """
    username = StringField('username', validators=[DataRequired(), Length(3, 32), Unique(User, User.username)])
    password = StringField('username', validators=[DataRequired(), RsaCheck()])
    # Telephone Numbers vary from country to country.
    telphone = StringField('telphone', validators=[Optional(), Unique(User, User.telphone), ],
                           filters=[lambda x: x or None])
    # The default value goes from '' to None.
    email = StringField('email', validators=[Optional(), Email(), Unique(User, User.email)],
                        filters=[lambda x: x or None])


class LoginForm(FlaskForm):
    username = StringField('username', validators=[DataRequired(), Length(3, 32)])
    password = StringField('username', validators=[DataRequired(),RsaCheck()])


class EditForm(FlaskForm):
    # Telephone Numbers vary from country to country.
    telphone = StringField('telphone', validators=[Optional(), Unique(User, User.telphone), ],
                           filters=[lambda x: x or None])
    email = StringField('email', validators=[Optional(), Email(), Unique(User, User.email)],
                        filters=[lambda x: x or None])


class ChangePasswordForm(FlaskForm):
    password = StringField('password', validators=[DataRequired(),RsaCheck()])
    new_password = StringField('new_password', validators=[DataRequired(),RsaCheck()])


class EditUserRoleForm(FlaskForm):
    id = IntegerField('id', validators=[DataRequired(), Exists(User, User.id)])
    role_id = IntegerField('id', validators=[DataRequired(), Exists(Role, Role.id)])

class UserListForm(FlaskForm):
    username = StringField('username', validators=[Optional(), Length(max=32)])
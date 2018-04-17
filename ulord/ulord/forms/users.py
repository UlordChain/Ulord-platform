# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from flask_wtf import Form
from wtforms import StringField
from wtforms.validators import Email,DataRequired,Length

class UserForm(Form):
    username=StringField('username',validators=[
        DataRequired(message=1),
    ])

    def __init__(self,*args,**kwargs):
        super(UserForm,self).__init__(*args,**kwargs)
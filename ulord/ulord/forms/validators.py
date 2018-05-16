# -*- coding: utf-8 -*-
# @Date    : 2018/4/27
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# 自定义验证

from wtforms.validators import ValidationError
from flask import g
from ulord.utils.rsa import rsahelper

class Unique(object):
    def __init__(self,model,field,message='This data already exists.'):
        self.model=model
        self.field=field
        self.message=message

    def __call__(self,form,field):
        check=self.model.query.filter(self.field==field.data).first()
        if check:
            raise ValidationError(self.message)

class Exists(object):
    def __init__(self,model,field,message='This data does not exist.'):
        self.model=model
        self.field=field
        self.message=message

    def __call__(self,form,field):
        exists=self.model.query.filter(self.field==field.data).first()
        if not exists:
            raise ValidationError(self.message)

class RsaCheck(object):
    def __init__(self,message='This data does not exist.'):
        self.message=message

    def __call__(self,form,field):
        try:
            password = rsahelper.decrypt(field.data).decode('u8')
        except Exception as e:
            print(e)
            raise ValidationError("The encryption format is not correct.")
        if len(password) < 3 or len(password) > 128:
            raise ValidationError("Password must be between 3 and 128 characters long.")
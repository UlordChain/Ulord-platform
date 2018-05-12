# -*- coding: utf-8 -*-
# @Date    : 2018/4/27
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# 自定义验证

from wtforms.validators import ValidationError
from flask import g

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
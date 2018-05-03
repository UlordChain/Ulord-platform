# -*- coding: utf-8 -*-
# @Date    : 2018/4/27
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# 自定义验证

from wtforms.validators import ValidationError

class Unique(object):
    def __init__(self,model,field,message='This filed already exists.'):
        self.model=model
        self.field=field
        self.message=message

    def __call__(self,form,field):
        check=self.model.query.filter(self.field==field.data).first()
        if check:
            raise ValidationError(self.message)
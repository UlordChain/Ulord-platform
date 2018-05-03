# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net
import functools
from flask import g
from ulord import return_result

from .users import *

def validate_form(form_class):
    def decorator(view_func):
        @functools.wraps(view_func)
        def inner(*args,**kwargs):
            # FlaskForm类的wrap_formdata方法实现了从request.form和json中取值
            form=form_class()
            if not form.validate():
                return return_result(errcode=20100, result=form.errors)
            g.form=form
            return view_func(*args,**kwargs)
        return inner
    return decorator
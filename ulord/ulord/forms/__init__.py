# -*- coding: utf-8 -*-
# @Date    : 2018/3/29
# @Author  : Shu
# @Email   : httpservlet@yeah.net
import functools
from flask import g
from ulord import return_result

from .users import *
from .role import *
from .uapp import *
from .app_type import *

def validate_form(form_class):
    def decorator(f):
        @functools.wraps(f)
        def inner(*args,**kwargs):
            # The method for wrap_formdata of the FlaskForm class
            # implements the values from request.form and json.
            form=form_class()
            if not form.validate():
                return return_result(errcode=20100, result=form.errors)
            g.form=form
            return f(*args,**kwargs)
        return inner
    return decorator
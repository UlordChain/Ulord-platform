# -*- coding: utf-8 -*-
# @Date    : 18-4-25 下午6:22
# @Author  : hetao
# @Email   : 18570367466@163.com
import logging

log = logging.getLogger(__name__)

# global var
# usage:
#   commands.py
#   transaction.py

# To fix abandon and update bug.
# The purpose is to fix an error signatures
"""usage:
    1. commands.abandon
    2. transaction.pay_script
    3. commands.update
"""
flag_claim = False

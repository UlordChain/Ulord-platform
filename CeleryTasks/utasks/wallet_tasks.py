# -*- coding: utf-8 -*-
# @Date    : 18-5-18 上午12:11
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers

from __future__ import absolute_import, unicode_literals
import logging
from utasks import app, configs
from jsonrpclib import Server

log = logging.getLogger(__name__)

@app.task
def publish(user, password, claim_name, metadata, contentType, sourceHash, currency, amount, bid=1, address=None, tx_fee=None,
                   skip_update_check=False):

    server = Server(configs.get('wallet_rpc'))
    res = server.publish(user, password, claim_name, metadata, contentType, sourceHash, currency, amount, bid, address, tx_fee,
                   skip_update_check)



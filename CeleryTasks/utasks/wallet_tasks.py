# -*- coding: utf-8 -*-
# @Date    : 18-5-18 上午12:11
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers

from __future__ import absolute_import, unicode_literals
import logging
from utasks import app, configs
from jsonrpclib import Server
import time

from utasks.utils import Timekeeping

log = logging.getLogger(__name__)

@app.task
def publish1(user, password, claim_name, metadata, contentType, sourceHash, currency, amount, bid=1, address=None, tx_fee=None,
                   skip_update_check=True, callback=None):

    server = Server(configs.get('wallet_rpc'))
    tp = Timekeeping()
    res = server.publish(user, password, claim_name, metadata, contentType, sourceHash, currency, amount, bid, address, tx_fee,
                   skip_update_check)
    tp.print_interval('publish')
    if callback is not None:
        return callback(res)

    return res

@app.task
def update_claim(user, password, claim_name, claim_id, txid, nout, metadata,
                         content_type, source_hash, currency, amount, bid, address=None, tx_fee=None, callback=None):

    server = Server(configs.get('wallet_rpc'))
    tp = Timekeeping()
    res = server.update_claim(user, password, claim_name, claim_id, txid, nout, metadata,
                         content_type, source_hash, currency, amount, bid, address, tx_fee)
    tp.print_interval('update_claim')
    if callback is not None:
        return callback(res)

    return res

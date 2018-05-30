# -*- coding: utf-8 -*-
# @Date    : 18-5-23 下午8:19
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers
from configs import SimpleConfig
from celery import Celery

__version__ = '1.0.0.0'

configs = SimpleConfig()

app = Celery(
    'utasks',
    broker=configs.get('broker'),
    backend=configs.get('backend'),
    # The include argument is a list of modules to import when the worker starts. You need to add our tasks module here so that the worker is able to find our tasks.
    include=[
        'utasks.wallet_tasks'
    ],
)
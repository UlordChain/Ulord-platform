# -*- coding: utf-8 -*-
# @Date    : 18-5-18 上午2:05
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers

import logging
import os
import ConfigParser
import threading

from uwallet.util import user_dir

log = logging.getLogger(__name__)

DEFAULT_CONFIG = {
    'debug': True,
    'wallet_rpc': 'http://192.168.14.241:8000',
    # 'wallet_rpc': 'http://192.168.14.240:8080',
    'config_path': '/etc/utasks.conf',
    'broker': 'amqp://guest:guest@192.168.14.240',
    'backend': 'redis://192.168.14.240',
}


class SimpleConfig(dict):
    # todo: need thread safe?
    ''' not thread safe '''
    _instance_lock = threading.Lock()

    def __new__(cls, options=None):
        """ single_instance --hetao
        >>>config = SimpleConfig({'a': 1, 'b':2})
        >>>print config.get('a'), config.get('b')
        1 2
        >>>config = SimpleConfig({'a': 2})
        >>>print config.get('a'), config.get('b')
        2 2
        """

        with SimpleConfig._instance_lock:
            if not hasattr(SimpleConfig, "_instance"):
                SimpleConfig._instance = dict.__new__(cls)
            else:
                if options:
                    SimpleConfig._instance.update(options)
        return SimpleConfig._instance

    def __init__(self, options=None):
        super(SimpleConfig, self).__init__()

        # set default config
        self.update(DEFAULT_CONFIG)

        # set system config path from command line
        if options is not None and 'config_path' in options:
            self.update({'config_path': options.pop('config_path')})

        # set system config
        system_config = self.read_system_config()
        if system_config:
            self.update(system_config)

        # set command line config
        if options is not None and isinstance(options, dict):
            self.update(options)

    def read_system_config(self):
        """Parse and return the system config settings in /etc/uwallet.conf."""
        result = {}
        if os.path.exists(self.get('config_path')):
            p = ConfigParser.ConfigParser()
            try:
                p.read(self.get('config_path'))
                for k, v in p.items('client'):
                    result[k] = v
            except (ConfigParser.NoSectionError, ConfigParser.MissingSectionHeaderError):
                pass

        return result

# -*- coding: utf-8 -*-
# @Date    : 18-5-24 上午12:34
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers

import logging
import time

log = logging.getLogger(__name__)

class Timekeeping:

    def __init__(self):
        t = time.time()
        self.start_time = t
        self.temp = t

    def __run(self):
        t = time.time()
        interval = t - self.temp
        self.temp = t
        return interval

    def get_interval(self):
        return self.__run()

    def print_interval(self, func_name=None):
        if func_name is None:
            print('the interval is:', self.__run())
        else:
            print('running {} cost {}'.format(func_name, self.__run()))

    def get_total(self):
        return time.time() - self.start_time

    def print_total(self, func_name=None):
        if func_name is None:
            print('the total is:', self.__run())
        else:
            print('running {} total cost {}'.format(func_name, self.__run()))
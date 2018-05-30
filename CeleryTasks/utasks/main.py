# -*- coding: utf-8 -*-
# @Date    : 18-5-18 上午12:00
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers

import logging

from utasks import app, configs

log = logging.getLogger('utasks')


def main():
    if configs.get('debug'):
        logging.getLogger("utasks").setLevel(logging.DEBUG)
    else:
        logging.getLogger("utasks").setLevel(logging.ERROR)
    handler = logging.StreamHandler()
    handler.setFormatter(logging.Formatter(
        "%(asctime)s %(levelname)-8s "
        "%(name)s:[%(funcName)s]:%(lineno)d: %(message)s")
    )
    logging.getLogger("utasks").addHandler(handler)

    log.info('start')
    app.conf.update(
        result_expires=3600,
    )

    app.start()


if __name__ == '__main__':
    # todo:
    import sys

    if 'worker' not in sys.argv:
        sys.argv.append('worker')
        sys.argv.append('-A')
        sys.argv.append('wallet_tasks')
    print(sys.argv)

    main()

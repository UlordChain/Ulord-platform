# -*- coding: utf-8 -*-
# @Date    : 2018/5/24
# @Author  : Shu
# @Email   : httpservlet@yeah.net

#coding: utf-8
import logging, os
from logging.handlers import RotatingFileHandler


def init_logging(app):
    formatter = logging.Formatter("%(levelname)s [%(asctime)s] (%(filename)s:%(lineno)d): %(message)s")
    error_log = os.path.join(app.root_path, app.config["ERROR_LOG"])
    if not os.path.exists(os.path.dirname(error_log)):
        os.makedirs(os.path.dirname(error_log))
    error_file_handler = RotatingFileHandler(
        error_log, mode='a', maxBytes=1024*1024*1024, backupCount=5, encoding='utf-8')
    error_file_handler.setLevel(logging.ERROR)
    error_file_handler.setFormatter(formatter)
    app.logger.addHandler(error_file_handler)

    info_log = os.path.join(app.root_path, app.config["INFO_LOG"])
    if not os.path.exists(os.path.dirname(info_log)):
        os.makedirs(os.path.dirname(info_log))
    info_file_handler = RotatingFileHandler(
        info_log, mode='a', maxBytes=1024*1024*1024, backupCount=10, encoding='utf-8')
    info_file_handler.setLevel(logging.INFO)
    info_file_handler.setFormatter(formatter)
    app.logger.addHandler(info_file_handler)

    app.logger.setLevel(logging.DEBUG)


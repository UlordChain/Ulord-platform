# -*- coding: utf-8 -*-
# @Date    : 18-5-17 下午11:41
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers

from setuptools import setup, find_packages
from utasks import __version__

requires={
    'celery',
}

console_scripts = [
    'utasks = utasks.main:main',
]

setup(
    name='utasks',
    version=__version__,
    install_requires=requires,
    packages=find_packages(exclude=['tests']),
    entry_points={'console_scripts': console_scripts},
    description="ulord tasks manage",
    author="HeTao",
    author_email="18570367466@163.com",
    license="MIT",
    url="http://ulord.one/",
    zip_safe=False
)

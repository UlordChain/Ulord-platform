# -*- coding: utf-8 -*-
# @Date    : 18-4-16 下午8:05
# @Author  : hetao
# @Email   : 18570367466@163.com

import json
from pymongo.errors import DuplicateKeyError, BulkWriteError
import logging
from uwallet import settings
log = logging.getLogger(__name__)

class Connection(object):
    """获取单例的连接对象

    >>> obj1 = Connection()
    >>> obj2 = Connection()
    >>> id(obj1)==id(obj2)
    False
    >>> id(obj1.mongo_con()) == id(obj2.mongo_con())
    True

    注意: 就算给定的host和port不能连接, mongodb 和 redis 也不会发生异常
    只有当使用连接对象去做操作时, 才会发生ConnectionError异常
    """

    __mongodb_connection = None

    @classmethod
    def mongo_con(cls):
        """ Get the mongodb connection object """

        if cls.__mongodb_connection is None:
            from pymongo import MongoClient

            cls.__mongodb_connection = MongoClient(host=settings.DATABASE_HOST, port=settings.DATABASE_PORT)

        return cls.__mongodb_connection


class ExecuteMongodb(Connection):
    """ 这个类直接用来定义对Mongodb数据库的增删改查, 不对数据进行任何处理 """

    def __init__(self, db_name, collection_name):
        if db_name is not None:
            self._db = self.mongo_con()[db_name]

            if collection_name is not None:
                self._col = self._db[collection_name]


    def create_index(self, index_names=()):
        if index_names:
            for index_name in index_names:
                self._col.create_index(index_name)

    def update_collection(self, collection_name):
        """更改 collection连接"""
        self._col = self._db[collection_name]

    def insert_one_doc(self, document):
        try:
            _id = self._col.insert_one(document)
            return _id
        except DuplicateKeyError as err:
            log.error('数据重复了, {}'.format(err.details['errmsg']))

    def insert_many_doc(self, documents, ordered=False):
        """ 插入多条数据, 如果数据多条, 比分批插入速度快

        :param documents: 数据
        :param ordered: 若为False, 数据重复错误不会影响其他数据插入
        :return:
        """
        try:
            res = self._col.insert_many(documents, ordered=ordered)
            return res
        except BulkWriteError as err:
            log.error('数据重复了, {}'.format(err.details['writeErrors']))

    def update_one_doc(self, filter, update, upsert=True, **kwargs):
        """
        >>> executeMongodb = ExecuteMongodb('Test', 'test_col')
        >>> executeMongodb.update_one_doc({'_id': 11}, {'$set': {'tet': '333'}})
        """
        _id = self._col.update_one(filter, update, upsert, **kwargs)
        return _id

    def find_one_doc(self, filter=None, *args, **kwargs):
        """

        >>> executeMongodb = ExecuteMongodb('Test', 'test_col')
        >>> executeMongodb.find_one_doc({'_id': 11}, {'tet': 1})
        """
        rs = self._col.find_one(filter, *args, **kwargs)
        return rs

    def find_doc(self, filter=None, *args, **kwargs):
        rs = self._col.find(filter, *args, **kwargs)
        return rs

    def del_doc(self, filter=None, *args, **kwargs):
        if filter is not None:
            rs = self._col.delete_one(filter, *args, **kwargs)
            return rs


    def get_con(self):
        return self.mongo_con()

    def get_all_database_names(self):
        return self.mongo_con().database_names()

    def get_all_collection_names(self):
        cols = self._db.collection_names()
        # 删除索引表
        if 'system.indexes' in cols:
            cols.remove('system.indexes')
        return cols


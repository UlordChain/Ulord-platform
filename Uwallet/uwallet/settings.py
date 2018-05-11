# -*- coding: utf-8 -*-
# @Date    : 18-4-16 下午8:07
# @Author  : hetao
# @Email   : 18570367466@163.com


# 正在使用的环境 0: 开发环境, 1: 测试环境, 2: 生产环境
ENVIRONMENT = 0

# ===========================================================
# 本地调试
# ===========================================================
# mongodb
DATABASE_HOST = '192.168.14.147'
# redis
REDIS_HOST = '192.168.14.240'


# ===========================================================
# 通用配置
# ===========================================================

# mongodb
DATABASE_ENGINE = 'mongodb'  # 留着备用
DATABASE_PORT = 27017

# redis
REDIS_NAME = 2
REDIS_NAME_PRO = 1
REDIS_PORT = 6379

# wallet_field
# 只和数据库进行交互的dict类型字段
DICT_DB_FIELD = ('master_private_keys', 'master_public_keys', 'addr_history',

                  'transactions', 'txi', 'txo', 'pruned_txo',
                 'claimtrie_transactions', 'verified_tx3',
                 'accounts',
                 )
# 既和数据库进行交互, 又和内存交互的dict类型字段
DICT_BOTH_FIELD = ()
# 只和数据库进行交互的 list 类型字段
LIST_DB_FIELD = ('addresses',)
# 既和数据库进行交互, 又和内存交互的 list 类型字段
LIST_BOTH_FIELD = ()
# 单个长度字段
NOR_FIELD = ('seed', 'stored_height')
WALLET_FIELD = DICT_BOTH_FIELD + DICT_DB_FIELD + LIST_DB_FIELD + LIST_BOTH_FIELD + NOR_FIELD
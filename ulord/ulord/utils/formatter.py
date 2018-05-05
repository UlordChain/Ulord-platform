# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net
import time, datetime, dateutil
from ulord.extensions import db


def get_tree(data, parent_id):
    """根据list返回多级树形分类

    Args:
        data: 原始list数据
        parent_id: 从哪级菜单开始生成
    """
    result = []
    for d in data:
        if d['parent_id'] == parent_id:
            _id = d['id']
            d.update(dict(children=get_tree(data, _id)))
            if not d['children']: d.pop('children')
            result.append(d)

    return result


def add_timestamp(records):
    """给返回数据添加一个时间戳

    # 联表查询的结果是sqlalchemy.util._collections.result对象, 不是一个model对象
    # 所以不能够想content_list接口那样格式化数据
    """
    # from sqlalchemy.util._collections import result
    datas = list()
    if isinstance(records, list):
        for r in records:
            if not isinstance(r, dict):
                r = r._asdict()
            if r.get('create_timed'):
                create_timed = r.get('create_timed')
                if isinstance(create_timed, str):
                    create_timed = dateutil.parser.parse(create_timed)
                create_timed_timestamp = int(time.mktime(create_timed.timetuple()))
                create_timed = create_timed.strftime("%Y-%m-%d %H:%M:%S")
                r.update(dict(create_timed=create_timed, create_timed_timestamp=create_timed_timestamp))
            if r.get('update_timed'):
                update_timed = r.get('update_timed')
                if isinstance(update_timed, str):
                    update_timed = dateutil.parser.parse(update_timed)
                update_timed_timestamp = int(time.mktime(update_timed.timetuple()))
                update_timed = update_timed.strftime("%Y-%m-%d %H:%M:%S")
                r.update(dict(update_timed=update_timed, update_timed_timestamp=update_timed_timestamp))
            datas.append(r)
    return datas

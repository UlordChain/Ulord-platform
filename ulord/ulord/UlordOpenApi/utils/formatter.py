# -*- coding: utf-8 -*-
# @Date    : 2018/4/11
# @Author  : Shu
# @Email   : httpservlet@yeah.net

def get_tree(data, parent_id):
    """根据list返回多级树形分类

    Args:
        data: 原始list数据
        parent_id: 从哪级菜单开始生成
    """
    result=[]
    for d in data:
        if d['parent_id'] == parent_id:
            _id = d['id']
            d.update(dict(children=get_tree(data, _id)))
            if not d['children']: d.pop('children')
            result.append(d)

    return result
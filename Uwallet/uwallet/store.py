# -*- coding: UTF-8 -*-
import logging
import os
import json
import traceback

from uwallet.errors import ParamsError
from uwallet.settings import  DICT_BOTH_FIELD,  LIST_BOTH_FIELD
log = logging.getLogger(__name__)

class StoreDict(dict):
    def __init__(self, config, name):
        self.config = config
        self.path = os.path.join(self.config.path, name)
        self.load()

    def load(self):
        try:
            with open(self.path, 'r') as f:
                self.update(json.loads(f.read()))
        except:
            pass

    def save(self):
        with open(self.path, 'w') as f:
            s = json.dumps(self, indent=4, sort_keys=True)
            r = f.write(s)

    def __setitem__(self, key, value):
        dict.__setitem__(self, key, value)
        self.save()

    def pop(self, key):
        if key in self.keys():
            dict.pop(self, key)
            self.save()


class Dict_Field(dict):
    __both_field = DICT_BOTH_FIELD

    def __init__(self, storage, field, *args, **kwargs):
        self.__field_name = field
        self.__field = field.split('.')[0]
        self.storage = storage
        super(Dict_Field, self).__init__(*args, **kwargs)

    def __getitem__(self, item):
        res = super(Dict_Field, self).__getitem__(item)
        if isinstance(res, dict):
            return Dict_Field(self.storage, self.__field_name + '.'+ item, res)
        elif isinstance(res, list):
            return List_Field(self.storage, self.__field_name + '.'+ item, res)
        else:
            return res

    def get(self, item, default=None):
        res = super(Dict_Field, self).get(item, default)
        if isinstance(res, dict):
            return Dict_Field(self.storage, self.__field_name + '.' + item, res)
        elif isinstance(res, list):
            return List_Field(self.storage, self.__field_name + '.' + item, res)
        else:
            return res


    def __setitem__(self, key, value):
        self.storage.update_col(self.__field_name + '.' + key, value)
        if self.__field in self.__both_field:
            super(Dict_Field, self).__setitem__(key, value)

    def __delitem__(self, key):
        self.storage.update_col(self.__field_name + '.' + key, 1, '$unset')
        if self.__field in self.__both_field:
            super(Dict_Field, self).__delitem__(key)

    def pop(self, key, default=None):
        self.storage.update_col(self.__field_name + '.' + key, 1, '$unset')
        if self.__field in self.__both_field:
            super(Dict_Field, self).pop(key, default)


class List_Field(list):
    __both_field = LIST_BOTH_FIELD

    def __init__(self, storage, field, *args, **kwargs):
        self.__field_name = field
        self.__field = field.split('.')[0]
        self.storage = storage
        super(List_Field, self).__init__(*args, **kwargs)

    def __getitem__(self, item):
        try:
            int_item = int(item)
        except ValueError:
            log.error(traceback.format_exc())
            raise ParamsError('51010', item)
        res = super(List_Field, self).__getitem__(int_item)
        if isinstance(res, dict):
            return Dict_Field(self.storage, self.__field_name + '.'+ item, res)
        elif isinstance(res, list):
            return List_Field(self.storage, self.__field_name + '.'+ item, res)
        else:
            return res

    def __setitem__(self, key, value):
        key_ = "{}.{}".format(self.__field_name, key)
        self.storage.update_col(key_, value)
        if self.__field in self.__both_field:
            super(List_Field, self).__setitem__(key, value)

    def remove(self, value):
        self.storage.update_col(self.__field_name, value, '$pull')
        if self.__field in self.__both_field:
            super(List_Field, self).remove(value)

    def append(self, value):
        self.storage.update_col(self.__field_name, value, '$push')
        if self.__field in self.__both_field:
            super(List_Field, self).append(value)


if __name__ == '__main__':

    a = Dict_Field(1, 'test')
    if a:
        print 111
    else:
        print 22
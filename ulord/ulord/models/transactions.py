# -*- coding: utf-8 -*-
# @Date    : 2018/4/3
# @Author  : Shu
# @Email   : httpservlet@yeah.net

from ulord.extensions import db
import time

content_tag = db.Table('content_tag', db.Column('tag_name', db.String(32), db.ForeignKey('tag.name')),
                       db.Column('content_id', db.Integer, db.ForeignKey('content.id')),
                       db.PrimaryKeyConstraint('tag_name', 'content_id'))


class Tag(db.Model):
    """ 资源标签表 """
    __tablename__ = 'tag'

    name = db.Column(db.String(32), primary_key=True, comment=u'主键,标签名称')
    create_timed = db.Column(db.DateTime, server_default=db.func.now(), comment=u'创建时间')


class Consume(db.Model):
    """ 资源消费表 """
    __tablename__ = 'consume'
    __table_args__ = (db.UniqueConstraint('claim_id', 'customer', 'appkey'),)  # 消费者对某资源只需一次付费

    txid = db.Column(db.String(64), primary_key=True, comment=u'消费资源,交易id')
    claim_id = db.Column(db.String(64), db.ForeignKey('content.claim_id'), nullable=False, comment=u'外键,资源交易id')
    customer = db.Column(db.String(64), nullable=False, index=True, comment=u'消费者(某个应用的用户名)')
    appkey = db.Column(db.String(32), db.ForeignKey('apps.appkey'), index=True, nullable=False)
    price = db.Column(db.Numeric(20, 8), nullable=False, default=0, comment=u'消费价格')
    create_timed = db.Column(db.DateTime, server_default=db.func.now(), comment=u'资源消费时间, 默认为当前时间')

    @property
    def create_timed_str(self):
        """输出日期字符串"""
        return self.create_timed.strftime("%Y-%m-%d %H:%M:%S")

    @property
    def create_timed_timestamp(self):
        """输出时间戳"""
        return int(time.mktime(self.create_timed.timetuple()))


class Content(db.Model):
    """ 发布资源内容表

    在ulord链上, 一个资源claim_id不会变, 而txid可以生成一笔新交易(在更新时)
    删除: 在链上会将claim_id解绑, 从一个特殊交易转变为普通交易(新建: 普通交易变为特殊带claim_id的交易)

    这张表只存储最新的数据, 如果有更新, 则更新表中的数据
    """
    __tablename__ = 'content'

    id = db.Column(db.Integer, primary_key=True, comment='自增id')
    claim_id = db.Column(db.String(40), unique=True, nullable=False, comment='元数据在ulord链上的claim_id(资源的唯一标识)')
    claim_name = db.Column(db.String(32), unique=True, nullable=False, comment='资源在链上的更新标识')
    txid = db.Column(db.String(64), unique=True, nullable=False, comment='元数据上链交易id')
    fee = db.Column(db.Float, default=0, comment='手续费')
    nout = db.Column(db.Integer, default=0, comment='此次交易中的第几个交易')
    author = db.Column(db.String(64), nullable=False, index=True, comment='资源发布者(某个应用的用户名)')
    appkey = db.Column(db.String(32), db.ForeignKey('apps.appkey'), nullable=False, index=True, comment='应用的appkey')
    title = db.Column(db.String(64), nullable=False, comment='资源标题')
    udfs_hash = db.Column(db.String(46), nullable=False, comment='内容文件在UDFS中的hash')
    price = db.Column(db.Float, nullable=False, default=0, comment='定价')
    content_type = db.Column(db.String(16), nullable=False, comment='资源类型(后缀)')
    currency = db.Column(db.String(8), default='UT', nullable=False, comment='货币类型')
    bid = db.Column(db.Float, nullable=False, comment="支付给平台的费用")
    des = db.Column(db.String(1024), comment='资源描述')
    status = db.Column(db.Integer, nullable=False, index=True, default=1, comment='状态:1.新增 2.更新 3.删除')
    enabled = db.Column(db.Boolean, nullable=False, default=True, comment='标识资源是否可用')
    create_timed = db.Column(db.DateTime, server_default=db.func.now(), comment='资源创建时间, 默认为当前时间')
    update_timed = db.Column(db.DateTime, onupdate=db.func.now(), comment='最后更新时间')
    thumbnail = db.Column(db.String(1024), comment='缩略图')
    preview = db.Column(db.String(1024), comment='预览')
    language = db.Column(db.String(8), comment='语言')
    license = db.Column(db.String(16), comment='许可证')
    license_url = db.Column(db.String(128), comment='许可证url')
    tags = db.relationship('Tag', secondary='content_tag', backref=db.backref('content', lazy='dynamic'))
    consumes = db.relationship('Consume', backref=db.backref('content'), lazy='dynamic')

    @property
    def create_timed_str(self):
        """输出日期字符串"""
        return self.create_timed.strftime("%Y-%m-%d %H:%M:%S")

    @property
    def create_timed_timestamp(self):
        """输出时间戳"""
        return int(time.mktime(self.create_timed.timetuple()))

    @property
    def update_timed_str(self):
        """输出日期字符串"""
        if self.update_timed:
            return self.update_timed.strftime("%Y-%m-%d %H:%M:%S")
        else:
            return None

    @property
    def update_timed_timestamp(self):
        """输出时间戳"""
        if self.update_timed:
            return int(time.mktime(self.update_timed.timetuple()))
        else:
            return None


class ContentHistory(db.Model):
    """资源历史记录(新增/更新/删除)"""
    __tablename__ = 'content_history'

    id = db.Column(db.Integer, primary_key=True, comment='自增id')
    txid = db.Column(db.String(64), comment='元数据上链交易id')
    claim_id = db.Column(db.String(40), nullable=False, comment='元数据在ulord链上的claim_id(资源的唯一标识)')
    claim_name = db.Column(db.String(32), nullable=False, comment='资源在链上的更新标识')
    fee = db.Column(db.Float, default=0, comment='手续费')
    nout = db.Column(db.Integer, default=0, comment='此次交易中的第几个交易')
    author = db.Column(db.String(64), nullable=False, index=True, comment='资源发布者(某个应用的用户名)')
    appkey = db.Column(db.String(32), db.ForeignKey('apps.appkey'), nullable=False, index=True, comment='应用的appkey')
    title = db.Column(db.String(64), nullable=False, comment='资源标题')
    udfs_hash = db.Column(db.String(46), nullable=False, comment='内容文件在UDFS中的hash')
    price = db.Column(db.Float, nullable=False, default=0, comment='定价')
    content_type = db.Column(db.String(16), nullable=False, comment='资源类型(后缀)')
    currency = db.Column(db.String(8), default='UT', nullable=False, comment='货币类型')
    bid = db.Column(db.Float, nullable=False, comment="支付给平台的费用")
    des = db.Column(db.String(1024), comment='资源描述')
    status = db.Column(db.Integer, nullable=False, index=True, default=1, comment='状态:1.新增 2.更新 3.删除')
    create_timed = db.Column(db.DateTime, server_default=db.func.now(), comment='资源创建时间, 默认为当前时间')
    thumbnail = db.Column(db.String(1024), comment='缩略图')
    preview = db.Column(db.String(1024), comment='预览')
    language = db.Column(db.String(8), comment='语言')
    license = db.Column(db.String(16), comment='许可证')
    license_url = db.Column(db.String(128), comment='许可证url')

    @property
    def create_timed_str(self):
        """输出日期字符串"""
        return self.create_timed.strftime("%Y-%m-%d %H:%M:%S")

    @property
    def create_timed_timestamp(self):
        """输出时间戳"""
        return int(time.mktime(self.create_timed.timetuple()))

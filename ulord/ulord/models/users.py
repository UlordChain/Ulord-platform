# -*- coding: utf-8 -*-
# @Date    : 2018/3/28
# @Author  : Shu
# @Email   : httpservlet@yeah.net
import time
from ulord.extensions import db
from werkzeug.security import generate_password_hash, check_password_hash
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from flask import current_app


class StatisticsAppUser(db.Model):
    """ Statistical user """
    __tablename__ = 'statistics_appuser'
    __table_args__ = (db.UniqueConstraint('appkey', 'app_username'),)

    id = db.Column(db.Integer, primary_key=True, comment=u'自增id')
    appkey = db.Column(db.String(32), db.ForeignKey('apps.appkey'), nullable=False)
    app_username = db.Column(db.String(64), nullable=False, comment=u'app的用户名')
    create_timed = db.Column(db.DateTime, nullable=False, server_default=db.func.now(), comment=u'创建时间')


class Application(db.Model):
    """ User type table, administrator insert """
    __tablename__ = 'apps'
    __table_args__ = (db.UniqueConstraint('user_id', 'appname'),)

    id = db.Column(db.Integer, primary_key=True, comment=u'自增id')
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment=u'应用所属用户')
    appname = db.Column(db.String(128), nullable=False, comment=u'应用名')
    apptype_id = db.Column(db.Integer, db.ForeignKey('app_type.id'), nullable=False, comment=u'应用分类')
    appdes = db.Column(db.String(500), comment=u'应用描述')
    appkey = db.Column(db.String(32), unique=True, index=True, nullable=False, comment=u'应用的标识')
    secret = db.Column(db.String(32), unique=True, nullable=False, comment=u'私钥, 用来签名请求')
    create_timed = db.Column(db.DateTime, server_default=db.func.now(), comment=u'创建时间')
    update_timed = db.Column(db.DateTime, onupdate=db.func.now(), comment=u'最后更新时间')


class Type(db.Model):
    """ app type table, admin """
    __tablename__ = 'app_type'

    id = db.Column(db.Integer, primary_key=True, comment=u'自增id')
    parent_id = db.Column(db.Integer, db.ForeignKey('app_type.id'), index=True, comment=u'父类别id')
    name = db.Column(db.String(32), unique=True, nullable=False, index=True, comment=u'类型名称')
    des = db.Column(db.String(256), comment=u'类型描述')

    app = db.relationship('Application', backref='type', lazy='dynamic')
    parent = db.relationship('Type', uselist=False)


class User(db.Model):
    """ Developer user table """
    __tablename__ = 'user'

    id = db.Column(db.Integer, primary_key=True, comment=u'自增id')
    username = db.Column(db.String(32), unique=True, nullable=False, index=True, comment=u'开发者帐号')
    password_hash = db.Column('password', db.String(128), nullable=False, comment=u'开发者密码')
    telphone = db.Column(db.String(24), unique=True, comment=u'开发者电话')
    email = db.Column(db.String(128), unique=True, comment=u'开发者邮箱')
    create_timed = db.Column(db.DateTime, server_default=db.func.now(), comment=u'创建时间')
    update_timed = db.Column(db.DateTime, onupdate=db.func.now(), comment=u'最后更新时间(新纪录为空值)')
    role_id = db.Column(db.Integer, db.ForeignKey('user_role.id'), nullable=False, comment='角色id')

    app = db.relationship('Application', backref='user', lazy='dynamic')

    @property
    def password(self):
        raise AttributeError("Password don't allow read.")

    @password.setter
    def password(self, password):
        self.password_hash = password

    def __repr__(self):
        return '<User {}>'.format(self.username)

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    def generate_auth_token(self, expiration=60 * 60 * 3):
        s = Serializer(current_app.config['SECRET_KEY'], expires_in=expiration)
        return s.dumps({'id': self.id})

    @staticmethod
    def verify_auth_token(token):
        s = Serializer(current_app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
            if 'id' not in data:
                return None
            user = User.query.get(data['id'])
            return user
        except Exception as e:
            print('e: ', e)
            return None

    @property
    def create_timed_str(self):
        return self.create_timed.strftime("%Y-%m-%d %H:%M:%S")

    @property
    def create_timed_timestamp(self):
        return int(time.mktime(self.create_timed.timetuple()))

    @property
    def update_timed_str(self):
        if self.update_timed:
            return self.update_timed.strftime("%Y-%m-%d %H:%M:%S")
        else:
            return None

    @property
    def update_timed_timestamp(self):
        if self.update_timed:
            return int(time.mktime(self.update_timed.timetuple()))
        else:
            return None


class Role(db.Model):
    """ User role permissions table

    normal, blocked, admin
    """
    __tablename__ = 'user_role'

    id = db.Column(db.Integer, primary_key=True, comment=u'角色id')
    name = db.Column(db.String(32), unique=True, nullable=False, comment=u'角色名')
    des = db.Column(db.String(500), nullable=False, comment=u'角色描述')

    user = db.relationship('User', backref='role', lazy='dynamic')

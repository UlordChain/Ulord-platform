# -*- coding: utf-8 -*-
# @Date    : 2018/3/30
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# @Des     : 交易相关,都在这里
import decimal
from . import bpv1
from ulord.utils import return_result
from flask import request, g
from . import appkey_check, get_jsonrpc_server
from ulord.models import Content, Tag, ContentHistory, Consume, AppUser, Application
from ulord.extensions import db
from ulord.utils.generate import generate_appkey


@bpv1.route('/transactions/createwallet/', methods=['POST'])
@appkey_check
def create_address():
    """生成钱包地址,应该调用钱包jsonrpc"""
    appkey = request.headers.get('appkey')
    username = request.json.get('username', '')
    username_wallet = get_wallet_name(username)
    pay_password = request.json.get('pay_password')

    server = get_jsonrpc_server()
    try:
        if server.create(username_wallet, pay_password).get('success') is not True:
            return return_result(20204)
        app_user = AppUser(appkey=appkey, app_username=username)
        db.session.add(app_user)
    except Exception as e:
        print (type(e),e)
        return return_result(20204,result=dict(wallet_reason=str(e)))

    return return_result()


@bpv1.route('/transactions/paytouser/', methods=['POST'])
@appkey_check
def pay_to_user():
    """开发者转给用户"""
    is_developer = request.json.get('is_developer')
    if is_developer is True:
        send_user_wallet = g.user.username
        pay_password=g.user.password_hash
    else:
        send_user_wallet = get_wallet_name(request.json.get('send_user'))
        pay_password = request.json.get('pay_password')

    recv_wallet_username = get_wallet_name(request.json.get('recv_user'))
    amount = request.json.get('amount')

    server = get_jsonrpc_server()
    try:
        result = server.pay(send_user_wallet, pay_password, recv_wallet_username, amount)
        if result.get('success') is not True:
            return return_result(20206)
    except Exception as e:
        print(e)
        return return_result(20206,result=dict(wallet_reason=str(e)))
    return return_result()


@bpv1.route('/transactions/publish/', methods=['POST'])
@appkey_check
def publish():
    appkey = request.headers.get('appkey')
    author = request.json.get('author', '')
    username_wallet = get_wallet_name(author)
    pay_password = request.json.get('pay_password')

    title = request.json.get('title')
    tags = request.json.get('tag')
    bid = 1  # 支付给平台的金额
    ipfs_hash = request.json.get('ipfs_hash')
    price = request.json.get('price')
    content_type = request.json.get('content_type')
    currency = 'ULD'
    description = request.json.get('description')

    sourcename = generate_appkey()  # 以后去掉

    metadata = dict(title=title, author=author, tag=['action'],  # 枚举
                    description='', language='en',  # 枚举
                    license='', licenseUrl='', nsfw=False, preview='', thumbnail='', )

    server = get_jsonrpc_server()
    try:
        result = server.publish(username_wallet, sourcename, bid, metadata, content_type, ipfs_hash, currency, price,
                                 pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20201)
    except Exception as e:
        print(e)
        return return_result(20201,result=dict(wallet_reason=str(e)))

    claim_id = result.get('claim_id')
    txid = result.get('txid')
    status = 1
    tags = save_tag(tags)
    history = save_content_history(txid=txid, claim_id=claim_id, author=author, appkey=appkey, title=title,
                                   ipfs_hash=ipfs_hash, price=price, content_type=content_type, currency=currency,
                                   des=description, status=status)
    content = save_content(claim_id=claim_id, author=author, appkey=appkey, txid=txid, title=title, ipfs_hash=ipfs_hash,
                           price=price, content_type=content_type, currency=currency, des=description, status=status,
                           tags=tags)
    db.session.commit()
    return return_result(result=dict(id=content.id, claim_id=claim_id))


@bpv1.route('/transactions/check/', methods=['POST'])
@appkey_check
def check():
    appkey = request.headers.get('appkey')
    customer = request.json.get('username')
    claim_id = request.json.get('claim_id')

    # appkey作为条件,是为了避免查询别的应用的资源
    content = Content.query.filter_by(claim_id=claim_id, appkey=appkey).first()
    if not content:
        return return_result(20007)
    if content.author != customer:  # 消费者与发布者不是同一人
        consume = Consume.query.filter_by(claim_id=claim_id, customer=customer, appkey=appkey).first()
        if not consume:
            return return_result(20008)
    return return_result(result=dict(ipfs_hash=content.ipfs_hash))


@bpv1.route('/transactions/consume/', methods=['POST'])
@appkey_check
def consume():
    appkey = request.headers.get('appkey')
    customer = request.json.get('username')
    wallet_username = get_wallet_name(customer)
    claim_id = request.json.get('claim_id')
    pay_password = request.json.get('pay_password')

    content = Content.query.filter_by(claim_id=claim_id, appkey=appkey).first()
    if not content:
        return return_result(20007)
    if content.author != customer:
        consume = Consume.query.filter_by(claim_id=claim_id, customer=customer, appkey=appkey).first()
        if not consume:
            server = get_jsonrpc_server()
            try:
                result = server.consume(wallet_username, claim_id, pay_password)
                if result.get('success') is not True:
                    print(result)
                    return return_result(20202)
            except Exception as e:
                print(e)
                return return_result(20202,result=dict(wallet_reason=str(e)))

            txid = result.get('txid')
            consume = Consume(txid=txid, claim_id=claim_id, customer=customer, appkey=appkey)
            db.session.add(consume)

    return return_result(result=dict(ipfs_hash=content.ipfs_hash))


@bpv1.route('/transactions/balance/', methods=['POST'])
@appkey_check
def balance():

    username_wallet = get_wallet_name(request.json.get('username', ''))
    pay_password = request.json.get('pay_password')

    server = get_jsonrpc_server()
    try:
        result = server.getbalance(username_wallet, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20203)
    except Exception as e:
        print (e)
        return return_result(20203,result=dict(wallet_reason=str(e)))

    confirmed = result.get('confirmed', '0')
    unconfirmed = result.get('unconfirmed', '0')
    unmatured = result.get('unmatured', '0')
    total = result.get('total', '0')
    return return_result(result=dict(total=total, confirmed=confirmed, unconfirmed=unconfirmed, unmatured=unmatured))


def save_content(**kwargs):
    content = Content(**kwargs)
    db.session.add(content)
    return content


def save_content_history(**kwargs):
    history = ContentHistory(**kwargs)
    db.session.add(history)
    return history


def save_tag(tag_names):
    tags = list()
    for name in tag_names:
        tag = Tag.query.filter_by(name=name).first()
        if not tag:
            tag = Tag(name=name)
            tag.name = name
            db.session.add(tag)
        tags.append(tag)

    db.session.commit()
    return tags


def get_wallet_name(username):
    appkey = request.headers.get('appkey')
    return '{}_{}'.format(appkey, username)

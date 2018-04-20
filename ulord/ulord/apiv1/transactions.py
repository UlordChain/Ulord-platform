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
from ulord.schema import consume_schema,consumeinouts_schema


@bpv1.route('/transactions/createwallet/', methods=['POST'])
@appkey_check
def create_address():
    """生成钱包地址,应该调用钱包jsonrpc"""
    appkey = g.appkey
    username = request.json.get('username', '')
    username_wallet = get_wallet_name(username)
    pay_password = request.json.get('pay_password')

    server = get_jsonrpc_server()
    try:
        result = server.create(username_wallet, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20204, result=result)
        app_user = AppUser(appkey=appkey, app_username=username)
        db.session.add(app_user)
    except Exception as e:
        print(type(e), e)
        return return_result(20204, result=dict(wallet_reason=str(e)))

    return return_result()


@bpv1.route('/transactions/paytouser/', methods=['POST'])
@appkey_check
def pay_to_user():
    """转账

    Args:
        is_developer:是否从开发者账户转给其用户
    """
    is_developer = request.json.get('is_developer')
    if is_developer is True:
        send_user_wallet = g.user.username
        pay_password = g.user.password_hash
    else:
        send_user_wallet = get_wallet_name(request.json.get('send_user'))
        pay_password = request.json.get('pay_password')

    recv_wallet_username = get_wallet_name(request.json.get('recv_user'))
    amount = request.json.get('amount')

    server = get_jsonrpc_server()
    try:
        print(send_user_wallet, pay_password, recv_wallet_username, amount)
        result = server.pay(send_user_wallet, pay_password, recv_wallet_username, amount)
        print(result)
        if result.get('success') is not True:
            print(result)
            return return_result(20206, result=result)
    except Exception as e:
        print(e)
        return return_result(20206, result=dict(wallet_reason=str(e)))
    return return_result()


@bpv1.route('/transactions/publish/', methods=['POST'])
@appkey_check
def publish():
    appkey = g.appkey
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
            return return_result(20201, result=result)
    except Exception as e:
        print(e)
        return return_result(20201, result=dict(wallet_reason=str(e)))

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
    """检查用户是否付费

    Args:
        username: 消费者
        claim_ids: 资源claim_id列表
    Returns:
        返回值三种状态:
        None: 没有此条资源记录
        False: 未付费
        ipfs_hash: 已付费
    """
    appkey = g.appkey
    customer = request.json.get('username')
    claim_ids = request.json.get('claim_ids')

    result = dict(zip(claim_ids, [None for claim_id in claim_ids]))
    # appkey作为条件,是为了避免查询别的应用的资源
    contents = Content.query.filter(Content.claim_id.in_(claim_ids), appkey == appkey).all()
    for content in contents:
        result.update({content.claim_id: False})
        if content.author == customer:  # # 消费者与发布者是同一人
            result.update({content.claim_id: content.ipfs_hash})
        else:
            consume = Consume.query.filter_by(claim_id=content.claim_id, customer=customer, appkey=appkey).first()
            if consume:
                result.update({content.claim_id: content.ipfs_hash})
    return return_result(result=result)


@bpv1.route('/transactions/consume/', methods=['POST'])
@appkey_check
def consume():
    appkey = g.appkey
    customer = request.json.get('username')
    wallet_username = get_wallet_name(customer)
    claim_id = request.json.get('claim_id')
    # 正常消费传值
    customer_pay_password = request.json.get('customer_pay_password')
    # 广告点击传值
    author_pay_password = request.json.get('author_pay_password')

    content = Content.query.filter_by(claim_id=claim_id, appkey=appkey).first()
    if not content:
        return return_result(20007)
    price = content.price
    if content.author != customer:
        if content.price != 0:  # 非免费资源(收费资源/广告)
            consume = Consume.query.filter_by(claim_id=claim_id, customer=customer, appkey=appkey).first()
            if not consume:
                server = get_jsonrpc_server()
                try:
                    if price >= 0:  # 普通消费
                        result = server.consume(wallet_username, claim_id, customer_pay_password)
                    else:  # 广告
                        send_wallet_username = get_wallet_name(content.author)
                        recv_wallet_username = get_wallet_name(customer)
                        abs_price = abs(float(price))  # decimal 类型 会是这样 0.030000, 要去掉后面的0
                        print(send_wallet_username, author_pay_password, recv_wallet_username, abs_price)
                        result = server.pay(send_wallet_username, author_pay_password, recv_wallet_username, abs_price)
                    if result.get('success') is not True:
                        print(result)
                        return return_result(20202, result=result)
                except Exception as e:
                    print(e)
                    return return_result(20202, result=dict(wallet_reason=str(e)))

                txid = result.get('txid')
                print(result)
                c = Consume(txid=txid, claim_id=claim_id, customer=customer, appkey=appkey, price=price)
                db.session.add(c)

    return return_result(result=dict(ipfs_hash=content.ipfs_hash))


@bpv1.route('/transactions/balance/', methods=['POST'])
@appkey_check
def balance():
    is_developer = request.json.get('is_developer')
    if is_developer:
        username_wallet = request.json.get('username')
    else:
        username_wallet = get_wallet_name(request.json.get('username'))
    pay_password = request.json.get('pay_password')

    server = get_jsonrpc_server()
    try:
        result = server.getbalance(username_wallet, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20203, result=result)
    except Exception as e:
        print(e)
        return return_result(20203, result=dict(wallet_reason=str(e)))

    confirmed = result.get('confirmed', '0')
    unconfirmed = result.get('unconfirmed', '0')
    unmatured = result.get('unmatured', '0')
    total = result.get('total', '0')
    return return_result(result=dict(total=total, confirmed=confirmed, unconfirmed=unconfirmed, unmatured=unmatured))


@bpv1.route('/transactions/publisherinout/<int:page>/<int:num>/', methods=['POST'])
@appkey_check
def publisher_inout(page, num):
    """发布者收支 (分为 资源收入/广告支出)"""
    appkey = g.appkey
    author = request.json.get('author')
    # with_entities的field可以使用lable指定别名
    records = Content.query. \
                with_entities(Content.claim_id, Content.author,Content.title,
                              Consume.txid, Consume.customer,Consume.price, Consume.create_timed). \
                join(Consume, Content.claim_id == Consume.claim_id). \
                filter(Content.author==author).paginate(page,num,error_out=False)
    total = records.total
    pages = records.pages
    records = consumeinouts_schema.dump(records.items).data

    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/customerinout/<int:page>/<int:num>/', methods=['POST'])
@appkey_check
def customer_inout(page, num):
    """消费者收支 (分为 消费支出/广告收入)"""
    appkey = g.appkey
    customer = request.json.get('customer')
    records = Content.query. \
                with_entities(Content.claim_id, Content.author,Content.title,
                              Consume.txid, Consume.customer,Consume.price, Consume.create_timed).\
                join(Consume, Content.claim_id == Consume.claim_id).\
                filter(Consume.customer==customer).paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records = consumeinouts_schema.dump(records.items).data
    return return_result(result=dict(total=total, pages=pages, records=records))


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
    appkey = g.appkey
    return '{}_{}'.format(appkey, username)

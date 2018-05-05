# -*- coding: utf-8 -*-
# @Date    : 2018/3/30
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1,appkey_check, get_jsonrpc_server
from ulord.utils import return_result
from ulord.utils.formatter import add_timestamp
from flask import request, g,current_app
from ulord.models import Content, Tag, ContentHistory, Consume, AppUser,User
from ulord.extensions import db
from ulord.utils.generate import generate_appkey


@bpv1.route('/transactions/createwallet/', methods=['POST'])
@appkey_check
def create_address():
    """Generate wallets for app users."""
    appkey = g.appkey
    username = request.json.get('username', '')
    username_wallet = get_wallet_name(username)
    pay_password = request.json.get('pay_password')

    try:
        server = get_jsonrpc_server()
        print(username_wallet,pay_password)
        result = server.create(username_wallet, pay_password)
        print(result)
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
    """The user account transfer

    Args:
        is_developer:Is it a developer
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

    try:
        server = get_jsonrpc_server()
        result = server.pay(send_user_wallet, pay_password, recv_wallet_username, amount)
        print(result)
        if result.get('success') is not True:
            print(result)
            return return_result(20206, result=result)
    except Exception as e:
        print(e)
        return return_result(20206, result=dict(wallet_reason=str(e)))
    return return_result()


@bpv1.route('/transactions/balance/', methods=['POST'])
@appkey_check
def balance():
    """Check balances"""
    is_developer = request.json.get('is_developer')
    if is_developer:
        username_wallet = g.user.username
        pay_password=g.user.password_hash
    else:
        username_wallet = get_wallet_name(request.json.get('username'))
        pay_password = request.json.get('pay_password')


    try:
        server = get_jsonrpc_server()
        result = server.getbalance(username_wallet, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20203, result=result)
    except Exception as e:
        print(e)
        return return_result(20203, result=dict(wallet_reason=str(e)))
    result=result.get('result')
    confirmed = result.get('confirmed', '0')
    unconfirmed = result.get('unconfirmed', '0')
    unmatured = result.get('unmatured', '0')
    total = result.get('total', '0')
    return return_result(result=dict(total=total, confirmed=confirmed, unconfirmed=unconfirmed, unmatured=unmatured))


@bpv1.route('/transactions/publish/', methods=['POST'])
@appkey_check
def publish():
    """Release resources"""
    appkey = g.appkey
    author = request.json.get('author', '')
    username_wallet = get_wallet_name(author)
    pay_password = request.json.get('pay_password')

    title = request.json.get('title')
    tags = request.json.get('tag')
    bid = current_app.config['PUBLISH_BID']
    udfs_hash = request.json.get('udfs_hash')
    price = request.json.get('price')
    content_type = request.json.get('content_type')
    currency = 'ULD'
    description = request.json.get('description')

    sourcename = generate_appkey()

    metadata = dict(title=title, author=author, tag=['action'],
                    description='', language='en',
                    license='', licenseUrl='', nsfw=False, preview='', thumbnail='', )
    try:
        server = get_jsonrpc_server()
        # print(username_wallet, pay_password, sourcename, bid, metadata, content_type, udfs_hash, currency, price)
        result = server.publish(username_wallet, pay_password, sourcename,metadata,
                                content_type, udfs_hash, currency, price,bid,None,None,True)
        # print(result)
        if result.get('success') is not True:
            print(result)
            return return_result(20201, result=result)
    except Exception as e:
        print(e)
        return return_result(20201, result=dict(wallet_reason=str(e)))
    result=result.get('result')
    claim_id = result.get('claim_id')
    txid = result.get('txid')
    status = 1
    tags = save_tag(tags)
    history = save_content_history(txid=txid, claim_id=claim_id, author=author, appkey=appkey, title=title,
                                   udfs_hash=udfs_hash, price=price, content_type=content_type, currency=currency,
                                   sourcename=sourcename,des=description, status=status)
    content = save_content(claim_id=claim_id, author=author, appkey=appkey, txid=txid, title=title, udfs_hash=udfs_hash,
                           price=price, content_type=content_type, currency=currency, sourcename=sourcename,
                           des=description, status=status, tags=tags)
    db.session.commit()
    return return_result(result=dict(id=content.id, claim_id=claim_id))


@bpv1.route('/transactions/check/', methods=['POST'])
@appkey_check
def check():
    """Check whether users pay for resources

    Args:
        username: consumer
        claim_ids: list of claim_id
    Returns:
        None: No resource record
        False: Unpaid
        udfs_hash: Paid, file hash
    """
    appkey = g.appkey
    customer = request.json.get('username')
    claim_ids = request.json.get('claim_ids')

    result = dict(zip(claim_ids, [None for claim_id in claim_ids]))
    contents = Content.query.filter(Content.claim_id.in_(claim_ids), appkey == appkey).all()
    for content in contents:
        result.update({content.claim_id: False})
        if content.author == customer:  # Publisher and consumer are the same person
            result.update({content.claim_id: content.udfs_hash})
        else:
            consume = Consume.query.filter_by(claim_id=content.claim_id, customer=customer, appkey=appkey).first()
            if consume:
                result.update({content.claim_id: content.udfs_hash})
    return return_result(result=result)


@bpv1.route('/transactions/consume/', methods=['POST'])
@appkey_check
def consume():
    appkey = g.appkey
    customer = request.json.get('username')
    wallet_username = get_wallet_name(customer)
    claim_id = request.json.get('claim_id')
    # Normal resource consumption
    customer_pay_password = request.json.get('customer_pay_password')
    # Ad click
    author_pay_password = request.json.get('author_pay_password')

    content = Content.query.filter_by(claim_id=claim_id, appkey=appkey).first()
    if not content:
        return return_result(20007)
    price = content.price
    if content.author != customer and content.price != 0:  # Non-free resources
        consume = Consume.query.filter_by(claim_id=claim_id, customer=customer, appkey=appkey).first()
        if not consume:
            try:
                server = get_jsonrpc_server()
                if price >= 0:  # Normal
                    result = server.consume(wallet_username,customer_pay_password,claim_id)
                else:  # Ad
                    send_wallet_username = get_wallet_name(content.author)
                    recv_wallet_username = get_wallet_name(customer)
                    abs_price = abs(float(price))  # decimal to float
                    # print(send_wallet_username, author_pay_password, recv_wallet_username, abs_price)
                    result = server.pay(send_wallet_username, author_pay_password, recv_wallet_username, abs_price)
                if result.get('success') is not True:
                    print(result)
                    return return_result(20202, result=result)
            except Exception as e:
                print(e)
                return return_result(20202, result=dict(wallet_reason=str(e)))
            # print(result)
            result=result.get('result')
            txid = result.get('txid')
            c = Consume(txid=txid, claim_id=claim_id, customer=customer, appkey=appkey, price=price)
            db.session.add(c)

    return return_result(result=dict(udfs_hash=content.udfs_hash))


@bpv1.route('/transactions/account/in/<int:page>/<int:num>/', methods=['POST'])
@appkey_check
def account_in(page, num):
    """income statement

    Publisher resource income and consumer advertising revenue
    """
    appkey = g.appkey
    username = request.json.get('username')
    records = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid,
                    Consume.customer, db.func.abs(Consume.price).label('price'), Consume.create_timed). \
                    join(Consume, Content.claim_id == Consume.claim_id). \
                    filter(Content.appkey==appkey). \
                    filter(db.and_(Content.author == username,Consume.price>0)|
                           db.and_(Consume.customer==username,Consume.price<0)). \
                    order_by(Consume.create_timed.desc()). \
                    paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records=add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/account/out/<int:page>/<int:num>/', methods=['POST'])
@appkey_check
def account_out(page, num):
    """expenditure statement

    Consumer Resource Expenditure and Publisher Ad Spending
    """
    appkey = g.appkey
    username = request.json.get('username')
    records = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid,
                    Consume.customer, db.func.abs(Consume.price).label('price'), Consume.create_timed). \
                    join(Consume, Content.claim_id == Consume.claim_id). \
                    filter(Content.appkey==appkey). \
                    filter(db.and_(Content.author == username,Consume.price<0)|
                           db.and_(Consume.customer==username,Consume.price>0)). \
                    order_by(Consume.create_timed.desc()). \
                    paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records = add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/account/inout/<int:page>/<int:num>/', methods=['POST'])
@appkey_check
def account_inout(page, num):
    """Income and expenditure statement

    account_in and account_out in one
    """
    appkey = g.appkey
    username = request.json.get('username')
    records = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid,
                    Consume.customer, Consume.price, Consume.create_timed). \
                    join(Consume, Content.claim_id == Consume.claim_id). \
                    filter(Content.appkey==appkey). \
                    filter((Content.author == username)|(Consume.customer==username)). \
                    order_by(Consume.create_timed.desc()). \
                    paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records = add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/publish/count/', methods=['POST'])
@appkey_check
def publish_count():
    """发布资源数量"""
    appkey=g.appkey
    author = request.json.get('author')
    count = Content.query.filter_by(appkey=appkey,author=author).count()
    return return_result(result=dict(count=count))


@bpv1.route('/transactions/account/', methods=['POST'])
@appkey_check
def account():
    """ 用户收支总额及资源统计"""
    appkey=g.appkey
    username = request.json.get('username')
    # 发布者收入
    publisher_in=Consume.query.with_entities(
                    db.func.sum(Consume.price).label('sum'),db.func.count(Consume.price).label('count')). \
                    join(Content,Content.claim_id == Consume.claim_id). \
                    filter(Content.appkey==appkey,Content.author == username,Consume.price>0).first()
    # 发布者支出
    publisher_out=Consume.query.with_entities(
                    db.func.abs(db.func.sum(Consume.price)).label('sum'),db.func.count(Consume.price).label('count')). \
                    join(Content,Content.claim_id==Consume.claim_id). \
                    filter(Content.appkey==appkey,Content.author==username,Consume.price<0).first()
    # 消费者收入
    customer_in = Consume.query.with_entities(
                    db.func.abs(db.func.sum(Consume.price)).label('sum'),db.func.count(Consume.price).label('count')). \
                    filter(Consume.appkey==appkey, Consume.customer == username,Consume.price<0).first()
    # 消费这支出
    customer_out=Consume.query.with_entities(
                    db.func.sum(Consume.price).label('sum'),db.func.count(Consume.price).label('count')). \
                    filter(Consume.appkey==appkey, Consume.customer==username,Consume.price>0).first()


    return return_result(result=dict(publisher_in=publisher_in, publisher_out=publisher_out,
                                     customer_in=customer_in,customer_out=customer_out))



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

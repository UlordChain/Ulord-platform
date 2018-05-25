# -*- coding: utf-8 -*-
# @Date    : 2018/3/30
# @Author  : Shu
# @Email   : httpservlet@yeah.net
from . import bpv1, appkey_check, get_jsonrpc_server
from ulord.utils import return_result
from ulord.utils.formatter import add_timestamp
from flask import request, g, current_app
from ulord.models import Content, Tag, ContentHistory, Consume, AppUser, User
from ulord.extensions import db
from ulord.utils.generate import generate_appkey
from ulord.forms import (validate_form, CreateWalletForm, PayToUserForm, BalanceForm, PublishForm,
                        CheckForm,ConsumeForm,AccountInForm,AccountOutForm,AccountInOutForm,
                        PublishCountForm,AccountForm)


@bpv1.route('/transactions/createwallet', methods=['POST'])
@appkey_check
@validate_form(form_class=CreateWalletForm)
def create_wallet():
    """Generate wallets for app users."""
    appkey = g.appkey
    username = g.form.username.data
    pay_password = g.form.pay_password.data

    try:
        server = get_jsonrpc_server()
        print(username, pay_password)
        result = server.create(username, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20204, result=result)
        app_user = AppUser(appkey=appkey, app_username=username)
        db.session.add(app_user)
    except Exception as e:
        print(type(e), e)
        return return_result(20204, result=dict(wallet_reason=str(e)))

    return return_result()


@bpv1.route('/transactions/paytouser', methods=['POST'])
@appkey_check
@validate_form(form_class=PayToUserForm)
def pay_to_user():
    """The user account transfer

    Args:
        is_developer:Is it a developer
    """
    is_developer = g.form.is_developer.data
    if is_developer is True:
        send_user_wallet = g.user.username
        pay_password = g.user.password_hash
    else:
        send_user_wallet = g.form.send_user.data
        pay_password = g.form.pay_password.data

    recv_wallet_username = g.form.recv_user.data
    amount = g.form.amount.data

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


@bpv1.route('/transactions/balance', methods=['POST'])
@appkey_check
@validate_form(form_class=BalanceForm)
def balance():
    """Check balances"""
    is_developer = g.form.is_developer.data
    if is_developer:
        username_wallet = g.user.username
        pay_password = g.user.password_hash
    else:
        username_wallet = g.form.username.data
        pay_password = g.form.pay_password.data

    try:
        server = get_jsonrpc_server()
        result = server.getbalance(username_wallet, pay_password)
        if result.get('success') is not True:
            print(result)
            return return_result(20203, result=result)
    except Exception as e:
        print(e)
        return return_result(20203, result=dict(wallet_reason=str(e)))
    result = result.get('result')
    confirmed = result.get('confirmed', '0')
    unconfirmed = result.get('unconfirmed', '0')
    unmatured = result.get('unmatured', '0')
    total = result.get('total', '0')
    return return_result(result=dict(total=total, confirmed=confirmed, unconfirmed=unconfirmed, unmatured=unmatured))


@bpv1.route('/transactions/publish', methods=['POST'])
@appkey_check
@validate_form(form_class=PublishForm)
def publish():
    """Release resources"""
    appkey = g.appkey
    author = g.form.author.data
    username_wallet=get_wallet_name(author)
    pay_password = g.form.pay_password.data
    title = g.form.title.data
    tags = g.form.tags.data
    udfs_hash = g.form.udfs_hash.data
    price = g.form.price.data
    content_type = g.form.content_type.data
    description = g.form.description.data
    bid = current_app.config['PUBLISH_BID']
    currency = current_app.config['PUBLISH_CURRENCY']

    sourcename = generate_appkey()

    metadata = dict(title=title, author=author, tag=tags, description='', language='en', license='', licenseUrl='',
                    nsfw=False, preview='', thumbnail='', )
    try:
        server = get_jsonrpc_server()
        result = server.publish(username_wallet, pay_password, sourcename, metadata, content_type, udfs_hash, currency,
                                price, bid, None, None, True)
        if result.get('success') is not True:
            print(result)
            return return_result(20201, result=result)
    except Exception as e:
        print(e)
        return return_result(20201, result=dict(wallet_reason=str(e)))
    result = result.get('result')
    claim_id = result.get('claim_id')
    txid = result.get('txid')
    if len(txid)!=64:
        return return_result(20201, result=result)
    status = 1
    tags = save_tag(tags)
    history = save_content_history(txid=txid, claim_id=claim_id, author=author, appkey=appkey, title=title,
                                   udfs_hash=udfs_hash, price=price, content_type=content_type, currency=currency,
                                   sourcename=sourcename, des=description, status=status)
    content = save_content(claim_id=claim_id, author=author, appkey=appkey, txid=txid, title=title, udfs_hash=udfs_hash,
                           price=price, content_type=content_type, currency=currency, sourcename=sourcename,
                           des=description, status=status, tags=tags)
    db.session.commit()
    return return_result(result=dict(id=content.id, claim_id=claim_id))


@bpv1.route('/transactions/check', methods=['POST'])
@appkey_check
@validate_form(form_class=CheckForm)
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
    customer = request.json.get('customer')
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


@bpv1.route('/transactions/consume', methods=['POST'])
@appkey_check
@validate_form(form_class=ConsumeForm)
def consume():
    appkey = g.appkey
    customer = g.form.customer.data
    wallet_username = get_wallet_name(customer)
    claim_id = g.form.claim_id.data
    # Normal resource consumption
    customer_pay_password = g.form.customer_pay_password.data
    # Ad click
    author_pay_password = g.form.author_pay_password.data

    content = g.form.content
    price = content.price
    if content.author != customer and content.price != 0:  # Non-free resources
        consume = Consume.query.filter_by(claim_id=claim_id, customer=customer, appkey=appkey).first()
        if not consume:
            try:
                server = get_jsonrpc_server()
                if price >= 0:  # Normal
                    result = server.consume(wallet_username, customer_pay_password, claim_id)
                else:  # Ad
                    send_wallet_username = get_wallet_name(content.author)
                    abs_price = abs(float(price))  # decimal to float
                    # print(send_wallet_username, author_pay_password, wallet_username, abs_price)
                    result = server.pay(send_wallet_username, author_pay_password, wallet_username, abs_price)
                if result.get('success') is not True:
                    print(result)
                    return return_result(20202, result=result)
            except Exception as e:
                print(e)
                return return_result(20202, result=dict(wallet_reason=str(e)))
            print(result)
            result = result.get('result')
            txid = result.get('txid')
            if len(txid) != 64:
                return return_result(20202, result=result)
            c = Consume(txid=txid, claim_id=claim_id, customer=customer, appkey=appkey, price=price)
            db.session.add(c)

    return return_result(result=dict(udfs_hash=content.udfs_hash))


@bpv1.route('/transactions/account/in/<int:page>/<int:num>', methods=['POST'])
@appkey_check
@validate_form(form_class=AccountInForm)
def account_in(page, num):
    """income statement

    Publisher resource income and consumer advertising revenue
    """
    appkey = g.appkey
    username = g.form.username.data
    records = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid,
                  Consume.customer, db.func.abs(Consume.price).label('price'),
                  Consume.create_timed).join(Consume, Content.claim_id == Consume.claim_id). \
                  filter(Content.appkey == appkey). \
                  filter(db.and_(Content.author == username, Consume.price > 0) |
                  db.and_(Consume.customer == username, Consume.price < 0)). \
                  order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records = add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/account/out/<int:page>/<int:num>', methods=['POST'])
@appkey_check
@validate_form(form_class=AccountOutForm)
def account_out(page, num):
    """expenditure statement

    Consumer Resource Expenditure and Publisher Ad Spending
    """
    appkey = g.appkey
    username = request.json.get('username')
    records = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid,
                  Consume.customer, db.func.abs(Consume.price).label('price'),
                  Consume.create_timed).join(Consume, Content.claim_id == Consume.claim_id). \
                  filter(Content.appkey == appkey). \
                  filter(db.and_(Content.author == username, Consume.price < 0) |
                  db.and_(Consume.customer == username, Consume.price > 0)). \
                  order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records = add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/account/inout/<int:page>/<int:num>', methods=['POST'])
@appkey_check
@validate_form(form_class=AccountInOutForm)
def account_inout(page, num):
    """Income and expenditure statement

    account_in and account_out in one
    """
    appkey = g.appkey
    username = request.json.get('username')
    records = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid,
                  Consume.customer, Consume.price, Consume.create_timed). \
                  join(Consume,Content.claim_id == Consume.claim_id). \
                  filter(Content.appkey == appkey). \
                  filter((Content.author == username) | (Consume.customer == username)). \
                  order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records = add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/publish/count', methods=['POST'])
@appkey_check
@validate_form(form_class=PublishCountForm)
def publish_count():
    """Publish of resources"""
    appkey = g.appkey
    author = g.form.author.data
    count = Content.query.filter_by(appkey=appkey, author=author).count()
    return return_result(result=dict(count=count))


@bpv1.route('/transactions/account', methods=['POST'])
@appkey_check
@validate_form(form_class=AccountForm)
def account():
    """ Total user income and resource statistics."""
    appkey = g.appkey
    username = request.json.get('username')
    # Publisher income
    publisher_in = Consume.query.with_entities(db.func.sum(Consume.price).label('sum'),
        db.func.count(Consume.price).label('count')).join(Content, Content.claim_id == Consume.claim_id).filter(
        Content.appkey == appkey, Content.author == username, Consume.price > 0).first()
    # Publisher expenditure
    publisher_out = Consume.query.with_entities(db.func.abs(db.func.sum(Consume.price)).label('sum'),
        db.func.count(Consume.price).label('count')).join(Content, Content.claim_id == Consume.claim_id).filter(
        Content.appkey == appkey, Content.author == username, Consume.price < 0).first()
    # Consumer income
    customer_in = Consume.query.with_entities(db.func.abs(db.func.sum(Consume.price)).label('sum'),
        db.func.count(Consume.price).label('count')).filter(Consume.appkey == appkey, Consume.customer == username,
                                                            Consume.price < 0).first()
    # Consumer expenditure
    customer_out = Consume.query.with_entities(db.func.sum(Consume.price).label('sum'),
        db.func.count(Consume.price).label('count')).filter(Consume.appkey == appkey, Consume.customer == username,
                                                            Consume.price > 0).first()

    return return_result(result=dict(publisher_in=publisher_in, publisher_out=publisher_out, customer_in=customer_in,
                                     customer_out=customer_out))


def save_content(**kwargs):
    content = Content(**kwargs)
    db.session.add(content)
    return content


def save_content_history(**kwargs):
    history = ContentHistory(**kwargs)
    db.session.add(history)
    return history


def save_tag(tag_names):
    if not tag_names:
        return []
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

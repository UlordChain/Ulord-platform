# -*- coding: utf-8 -*-
# @Date    : 2018/3/30
# @Author  : Shu
# @Email   : httpservlet@yeah.net
import inspect
import traceback
import copy
from . import bpv1, appkey_check, get_jsonrpc_server
from ulord.utils import return_result
from ulord.utils.formatter import add_timestamp
from flask import request, g, current_app as app
from ulord.models import Content, Tag, ContentHistory, Consume, StatisticsAppUser
from ulord.extensions import db
from ulord.utils.generate import generate_appkey
from ulord.forms import (
    validate_form, CreateWalletForm, PayToUserForm, BalanceForm, PublishForm, CheckForm, ConsumeForm, AccountInForm,
    AccountOutForm, AccountInOutForm, PublishCountForm, AccountForm, UpdateForm, DeleteForm)
from ulord.schema import contenthistory_schema
from sqlalchemy.exc import IntegrityError
from ulord.utils.log import formatter_error


@bpv1.route('/transactions/createwallet', methods=['POST'])
@appkey_check
@validate_form(form_class=CreateWalletForm)
def create_wallet():
    """Generate wallets for app users."""
    appkey = g.appkey
    username = g.form.username.data
    pay_password = g.form.pay_password.data
    try:
        1 / 0
        server = get_jsonrpc_server()
        result = server.create(username, pay_password)
        if result.get('errcode') != 0:
            return result
        app_user = StatisticsAppUser(appkey=appkey, app_username=username)
        db.session.add(app_user)
        db.session.commit()
    except IntegrityError as ie:
        app.logger.error(formatter_error(ie))
    except:
        app.logger.error(formatter_error(traceback.format_exc()))
        return return_result(20204)

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
        if result.get('errcode') != 0:
            return result
    except:
        app.logger.error(formatter_error(traceback.format_exc()))
        return return_result(20206)
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
        if result.get('errcode') != 0:
            return result
    except:
        app.logger.error(formatter_error(traceback.format_exc()))
        return return_result(20203)
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
    """Release resources

    Args:
        status: 1:add, 2:update, 3:delete(enabled=False)
    """
    data = copy.deepcopy(g.form.data)
    data['appkey'] = g.appkey
    username_wallet = get_wallet_name(data['author'])
    pay_password = data.pop('pay_password')
    data['currency'] = app.config['PUBLISH_CURRENCY']
    data['bid'] = app.config['PUBLISH_BID']
    data['claim_name'] = generate_appkey()

    metadata = dict(title=data['title'], author=data['author'], tag=data['tags'], description=data['des'],
                    language=data['language'] or 'en', license=data['license'], licenseUrl=data['license_url'],
                    nsfw=False, preview=data['preview'], thumbnail=data['thumbnail'])
    try:
        server = get_jsonrpc_server()
        result = server.publish(username_wallet, pay_password, data['claim_name'], metadata, data['content_type'],
                        data['udfs_hash'], data['currency'], data['price'], data['bid'], None, None, True)
        if result.get('errcode') != 0:
            return result
    except:
        app.logger.error(formatter_error(traceback.format_exc()))
        return return_result(20201)
    result = result.get('result')
    data['fee'] = float(result.get('fee', 0))
    data['claim_id'] = result.get('claim_id')
    data['txid'] = result.get('txid')
    data['nout'] = int(result.get('nout', 0))
    if len(data['txid']) != 64:
        return return_result(20201, result=result)
    data['status'] = 1
    data['tags'] = save_tag(data['tags'])
    content = Content(**data)
    db.session.add(content)
    data.pop('tags')
    history = ContentHistory(**data)
    db.session.add(history)
    db.session.commit()
    return return_result(result=dict(id=content.id, claim_id=content.claim_id))


@bpv1.route('/transactions/update', methods=['POST'])
@appkey_check
@validate_form(form_class=UpdateForm)
def update():
    """Update published resources

    Args:
        status: 1:add, 2:update, 3:delete(enabled=False)
    """
    data = copy.deepcopy(g.form.data)
    _id = data.pop('id')
    pay_password = data.pop('pay_password')
    content = Content.query.filter_by(id=_id, appkey=g.appkey, enabled=True).first()
    if not content:
        return return_result(20007)
    content.currency = app.config['PUBLISH_CURRENCY']
    content.bid = app.config['PUBLISH_BID']

    for k, v in data.items():
        if k in ['id', 'pay_password']:
            continue
        if v:
            if k == 'tags':
                v = save_tag(v)
            setattr(content, k, v)

    metadata = dict(title=content.title, author=content.author, tag=[tag.name for tag in content.tags],
                    description=content.des, language=content.language or 'en', license=content.license,
                    licenseUrl=content.license_url, nsfw=False, preview=content.preview, thumbnail=content.thumbnail)
    try:
        server = get_jsonrpc_server()
        result = server.update_claim(get_wallet_name(content.author), pay_password, content.claim_name,
                                     content.claim_id, content.txid, content.nout, metadata, content.content_type,
                                     content.udfs_hash, content.currency, content.price, content.bid,
                                     None, None)
        if result.get('errcode') != 0:
            return result
    except:
        app.logger.error(formatter_error(traceback.format_exc()))
        return return_result(20208)

    result = result.get('result')
    content.txid = result.get('txid')
    content.nout = int(result.get('nout', 0))
    content.fee = float(result.get('fee', 0))
    content.status = 2
    if len(content.txid) != 64:
        return return_result(20208, result=result)

    data = contenthistory_schema.dump(content).data
    data['appkey'] = g.appkey
    history = ContentHistory(**data)
    db.session.add(history)
    db.session.commit()
    return return_result(result=dict(id=content.id, claim_id=content.claim_id))


@bpv1.route('/transactions/delete', methods=['POST'])
@appkey_check
@validate_form(form_class=DeleteForm)
def delete():
    """Update published resources

    Args:
        status: 1:add, 2:update, 3:delete(DB: enabled=False; CHAIN:metadata['nsfw']=True)
    """

    content = Content.query.filter_by(id=g.form.id.data, appkey=g.appkey, enabled=True).first()
    if not content:
        return return_result(20007)

    metadata = dict(title=content.title, author=content.author, tag=[tag.name for tag in content.tags],
                    description=content.des, language=content.language or 'en', license=content.license,
                    licenseUrl=content.license_url, nsfw=True, preview=content.preview, thumbnail=content.preview)
    try:
        server = get_jsonrpc_server()
        result = server.update_claim(get_wallet_name(content.author), g.form.pay_password.data, content.claim_name,
                                     content.claim_id, content.txid, content.nout, metadata, content.content_type,
                                     content.udfs_hash, content.currency, content.price, content.bid,
                                     None, None)
        if result.get('errcode') != 0:
            return result
    except:
        app.logger.error(formatter_error(traceback.format_exc()))
        return return_result(20208)

    result = result.get('result')
    content.txid = result.get('txid')
    content.nout = int(result.get('nout', 0))
    content.fee = float(result.get('fee', 0))
    content.status = 3
    content.enabled = False

    data = contenthistory_schema.dump(content).data
    data['appkey'] = g.appkey
    history = ContentHistory(**data)
    db.session.add(history)
    db.session.commit()
    return return_result(result=dict(num=1))


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
    customer = g.form.customer.data
    claim_ids = g.form.claim_ids.data

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
        cs = Consume.query.filter_by(claim_id=claim_id, customer=customer, appkey=appkey).first()
        if not cs:
            try:
                server = get_jsonrpc_server()
                if price >= 0:  # Normal
                    result = server.consume(wallet_username, customer_pay_password, claim_id)
                else:  # Ad
                    send_wallet_username = get_wallet_name(content.author)
                    abs_price = abs(float(price))  # decimal to float
                    result = server.pay(send_wallet_username, author_pay_password, wallet_username, abs_price)
                if result.get('errcode') != 0:
                    return result
            except:
                app.logger.error(formatter_error(traceback.format_exc()))
                return return_result(20202)
            app.logger.debug(result)
            result = result.get('result')
            txid = result.get('txid')
            if len(txid) != 64:
                return return_result(20202, result=result)
            c = Consume(txid=txid, claim_id=claim_id, customer=customer, appkey=appkey, price=price)
            db.session.add(c)
            db.session.commit()
    return return_result(result=dict(udfs_hash=content.udfs_hash))


@bpv1.route('/transactions/account/in/<int:page>/<int:num>', methods=['POST'])
@appkey_check
@validate_form(form_class=AccountInForm)
def account_in(page, num):
    """income statement

    Publisher resource income and consumer advertising revenue

    Args:
        page: Show the page
        num: How many pages per page
        category: 0 - Resource income, 1 - Advertising revenue
    """
    appkey = g.appkey
    username = g.form.username.data
    category = g.form.category.data
    sdate = g.form.sdate.data
    edate = g.form.edate.data
    query = Content.query.with_entities(Content.id, Content.claim_id, Content.author, Content.title, Content.enabled,
                                        Consume.txid, Consume.customer, db.func.abs(Consume.price).label('price'),
                                        Consume.create_timed).join(Consume,
                                                                   Content.claim_id == Consume.claim_id).filter(
        Content.appkey == appkey).filter(Consume.create_timed >= sdate, Consume.create_timed <= edate)
    if category == 0:
        query = query.filter(Content.author == username, Consume.price > 0)
    elif category == 1:
        query = query.filter(Consume.customer == username, Consume.price < 0)
    else:
        query = query.filter(
            db.and_(Content.author == username, Consume.price > 0) | db.and_(Consume.customer == username,
                                                                             Consume.price < 0))
    records = query.order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
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

    Args:
        page: Show the page
        num: How many pages per page
        category: 0 - Resource spending, 1 - Advertising income
    """
    appkey = g.appkey
    username = g.form.username.data
    category = g.form.category.data
    sdate = g.form.sdate.data
    edate = g.form.edate.data
    query = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid, Consume.customer,
                                        db.func.abs(Consume.price).label('price'), Consume.create_timed).join(Consume,
                                                                          Content.claim_id == Consume.claim_id).filter(
        Content.appkey == appkey).filter(Consume.create_timed >= sdate, Consume.create_timed <= edate)
    if category == 0:
        query = query.filter(Consume.customer == username, Consume.price > 0)
    elif category == 1:
        query = query.filter(Content.author == username, Consume.price < 0)
    else:
        query = query.filter(
            db.and_(Content.author == username, Consume.price < 0) | db.and_(Consume.customer == username,
                                                                             Consume.price > 0))
    records = query.order_by(Consume.create_timed.desc()).paginate(page, num, error_out=False)
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
    username = g.form.username.data
    records = Content.query.with_entities(Content.claim_id, Content.author, Content.title, Consume.txid,
                                          Consume.customer, Consume.price, Consume.create_timed).join(Consume,
                                                                                                      Content.claim_id == Consume.claim_id).filter(
        Content.appkey == appkey).filter((Content.author == username) | (Consume.customer == username)).order_by(
        Consume.create_timed.desc()).paginate(page, num, error_out=False)
    total = records.total
    pages = records.pages
    records = add_timestamp(records.items)
    return return_result(result=dict(total=total, pages=pages, records=records))


@bpv1.route('/transactions/account', methods=['POST'])
@appkey_check
@validate_form(form_class=AccountForm)
def account():
    """ Total user income and resource statistics."""
    appkey = g.appkey
    username = g.form.username.data
    sdate = g.form.sdate.data
    edate = g.form.edate.data

    # Publisher income
    publisher_in = Consume.query.with_entities(db.func.sum(Consume.price).label('sum'),
                                               db.func.count(Consume.price).label('count')).join(Content,
                                                                                                 Content.claim_id == Consume.claim_id).filter(
        Content.appkey == appkey, Content.author == username, Consume.price > 0).filter(Consume.create_timed >= sdate,
                                                                                        Consume.create_timed <= edate).first()

    # Publisher expenditure
    publisher_out = Consume.query.with_entities(db.func.abs(db.func.sum(Consume.price)).label('sum'),
                                                db.func.count(Consume.price).label('count')).join(Content,
                                                                                                  Content.claim_id == Consume.claim_id).filter(
        Content.appkey == appkey, Content.author == username, Consume.price < 0).filter(Consume.create_timed >= sdate,
                                                                                        Consume.create_timed <= edate).first()

    # Consumer income
    customer_in = Consume.query.with_entities(db.func.abs(db.func.sum(Consume.price)).label('sum'),
                                              db.func.count(Consume.price).label('count')).filter(
        Consume.appkey == appkey, Consume.customer == username, Consume.price < 0).filter(Consume.create_timed >= sdate,
                                                                                          Consume.create_timed <= edate).first()

    # Consumer expenditure
    customer_out = Consume.query.with_entities(db.func.sum(Consume.price).label('sum'),
                                               db.func.count(Consume.price).label('count')).filter(
        Consume.appkey == appkey, Consume.customer == username, Consume.price > 0).filter(Consume.create_timed >= sdate,
                                                                                          Consume.create_timed <= edate).first()

    return return_result(result=dict(publisher_in=publisher_in, publisher_out=publisher_out, customer_in=customer_in,
                                     customer_out=customer_out))


@bpv1.route('/transactions/publish/count', methods=['POST'])
@appkey_check
@validate_form(form_class=PublishCountForm)
def publish_count():
    """Publish of resources"""
    appkey = g.appkey
    author = g.form.author.data
    sdate = g.form.sdate.data
    edate = g.form.edate.data
    count = Content.query.filter_by(appkey=appkey, author=author).filter(Content.create_timed >= sdate,
                                                                         Content.create_timed <= edate).count()
    return return_result(result=dict(count=count))


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
    return tags


def get_wallet_name(username):
    appkey = g.appkey
    return '{}_{}'.format(appkey, username)

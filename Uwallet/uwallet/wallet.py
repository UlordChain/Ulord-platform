# -*- coding: utf-8 -*-
# @Date    : 18-5-7 上午12:46
# @Author  : hetao
# @Email   : 18570367466@163.com
import hashlib
import json
import logging
import random
import threading
import time
import traceback
from decimal import Decimal
from functools import partial

from pymongo.errors import DuplicateKeyError

from uwallet.account import BIP32_Account
from uwallet.coinchooser import COIN_CHOOSERS
from uwallet.constants import COINBASE_MATURITY, TYPE_CLAIM, TYPE_UPDATE, TYPE_SUPPORT, TYPE_ADDRESS, TYPE_PUBKEY, \
    RECOMMENDED_FEE, EXPIRATION_BLOCKS
from uwallet.errors import ServerError, ParamsError, InvalidPassword, NotEnoughFunds
from uwallet.mnemonic import Mnemonic
from uwallet.store import Dict_Field, List_Field
from uwallet.synchronizer import Synchronizer
from uwallet.transaction import Transaction
from uwallet.ulord import deserialize_xkey, pw_decode, pw_encode, bip32_root, bip32_private_derivation, \
    public_key_to_bc_address, is_address, hash_160_to_bc_address, bip32_private_key, encode_claim_id_hex, claim_id_hash
from uwallet.settings import DATABASE_HOST, DATABASE_PORT, WALLET_FIELD, NOR_FIELD, LIST_DB_FIELD, LIST_BOTH_FIELD, \
    DICT_DB_FIELD, DICT_BOTH_FIELD
from uwallet.util import profiler, rev_hex
from uwallet.verifier import SPV
from uwallet.version import UWALLET_VERSION, NEW_SEED_VERSION
from uwallet.wallet_ import BIP32_Wallet

log = logging.getLogger(__name__)


class Connection(object):
    """获取单例的连接对象

    >>> obj1 = Connection()
    >>> obj2 = Connection()
    >>> id(obj1)==id(obj2)
    False
    >>> id(obj1.mongo_con()) == id(obj2.mongo_con())
    True

    注意: 就算给定的host和port不能连接, mongodb 和 redis 也不会发生异常
    只有当使用连接对象去做操作时, 才会发生ConnectionError异常
    """

    __mongodb_connection = None

    @classmethod
    def mongo_con(cls):
        """ Get the mongodb connection object """

        if cls.__mongodb_connection is None:
            from pymongo import MongoClient

            cls.__mongodb_connection = MongoClient(host=DATABASE_HOST, port=DATABASE_PORT)

        return cls.__mongodb_connection


class Wallet_Storage(object):
    use_change = False
    root_name = 'x/'
    gap_limit = 1  # min receiving addresses
    gap_limit_for_change = 1  # min change addresses
    root_derivation = "m/"
    wallet_type = 'standard'

    def __init__(self, user, password, is_create=False):
        self.user = user
        self._password = password

        if not password:
            raise ParamsError('51002')
        try:
            str(password)
        except:
            raise ParamsError('51001', "the password can't conversion into str")

        if '_' in user:
            self.app_key, self.id = user.split('_')
        else:
            self.app_key, self.id = 'ulord', user

        db = Connection.mongo_con()['uwallet_user']
        self.__col = db[self.app_key]
        self.__col.create_index('seed', unique=True, background=True)

        # self.wallet = Wallet()
        self.is_exists = self.is_exists()
        if self.is_exists:
            if is_create:
                raise ParamsError('51004', self.user)
            # load wallet
            self.check_password()

            receiving = self.addresses['receiving'] if self.use_change else self.addresses
            self.first_address = receiving and receiving[0]
        else:
            if not is_create:
                raise ParamsError('51003', self.user)

            # create wallet

    @classmethod
    def get_first_address(cls, user):
        if '_' in user:
            app_key, id = user.split('_')
        else:
            app_key, id = 'ulord', user
        col = Connection.mongo_con()['uwallet_user'][app_key]
        # todo: except
        res = col.find_one({'_id': id}, {'_id': 0, 'addresses': 1})
        if res is None:
            raise ParamsError('51003', user)

        receiving = res['addresses']['receiving'] if cls.use_change else res['addresses']
        first_address = receiving and receiving[0]
        return first_address

    def is_exists(self):
        res = self.__col.find({'_id': self.id}).count()
        if res:
            return True
        else:
            return False

    def __getattr__(self, field):
        if field in WALLET_FIELD:
            res = self.__col.find_one({'_id': self.id}, {'_id': 0, field: 1})
            rs = res[field]
            if isinstance(rs, dict):
                return Dict_Field(self, field, rs)
            elif isinstance(rs, list):
                return List_Field(self, field, rs)
            else:
                return rs

    def __setattr__(self, field, value):
        if field in WALLET_FIELD:
            self.update_col(field, value)
        else:
            super(Wallet_Storage, self).__setattr__(field, value)

    def __delattr__(self, field):
        if field in WALLET_FIELD:
            self.update_col(field, 1, '$unset')
        else:
            super(Wallet_Storage, self).__delattr__(field)

    def update_col(self, key, value, operate='$set', update_data=None):
        if key is not None and update_data is None:
            try:
                json.dumps(key)
                json.dumps(value)
            except:
                log.error(traceback.format_exc())
                raise ServerError('52012', key)
            update_data = {key: value}
        res = self.__col.update_one({'_id': self.id}, {operate: update_data}, True)
        return res


    def del_wallet(self):
        res = self.__col.delete_one({'_id': self.id})
        return res

    def check_password(self):
        self.decoded_xprv = pw_decode(self.master_private_keys[self.root_name], self._password)
        self.xpub = self.master_public_keys[self.root_name]
        try:
            if deserialize_xkey(self.decoded_xprv)[3] == deserialize_xkey(self.xpub)[3]:
                return True
        except InvalidPassword:
            pass
        raise ParamsError('51001')


class Abstract_Wallet(Wallet_Storage):
    max_change_outputs = 3

    def __init__(self, user, password, is_create=False):
        super(Abstract_Wallet, self).__init__(user, password, is_create=is_create)
        self.unverified_tx = {}
        self.electrum_version = UWALLET_VERSION
        # saved fields
        self.seed_version = NEW_SEED_VERSION
        self.multiple_change = False
        self.frozen_addresses = set()


        # there is a difference between wallet.up_to_date and interface.is_up_to_date()
        # interface.is_up_to_date() returns true when all requests have been answered and processed
        # wallet.up_to_date is true when the wallet is synchronized (stronger requirement)
        self.up_to_date = False


    def start_threads(self, network):
        self.network = network
        if self.network is not None:
            self.lock = threading.Lock()
            self.transaction_lock = threading.Lock()
            self.send_tx_lock = threading.Lock()
            self.tx_event = threading.Event()
            self.load_accounts()  
            self.load_transactions()  
            self.build_reverse_history()  
            self.check_history()  
            
            self.prepare_for_verifier()
            self.verifier = SPV(self.network, self)
            self.synchronizer = Synchronizer(self, network)
            self.verifier.run()
            self.synchronizer.run()
            network.add_jobs([self.verifier, self.synchronizer])
        else:
            self.verifier = None
            self.synchronizer = None

    def stop_threads(self):
        if self.network is not None:
            self.network.remove_jobs([self.synchronizer, self.verifier])
            if self.synchronizer:
                self.synchronizer.release()
                self.synchronizer = None
            else:
                log.warning("Synchronizer alread released.")
            self.verifier = None
            # Now no references to the syncronizer or verifier
            # remain so they will be GC-ed
            self.stored_height = self.get_local_height()

    def prepare_for_verifier(self):
        # review transactions that are in the history
        for addr, hist in self.addr_history.items():
            for tx_hash, tx_height in hist:
                # add it in case it was previously unconfirmed
                self.add_unverified_tx(tx_hash, tx_height)

        # if we are on a pruning server, remove unverified transactions
        vr = self.verified_tx3.keys() + self.unverified_tx.keys()
        for tx_hash in self.tx_transactions.keys():
            if tx_hash not in vr:
                log.info("removing transaction %s", tx_hash)
                self.tx_transactions.pop(tx_hash)
                self.transactions.pop(tx_hash)

    def add_unverified_tx(self, tx_hash, tx_height):
        # Only add if confirmed and not verified
        if tx_height > 0 and tx_hash not in self.verified_tx3:
            self.unverified_tx[tx_hash] = tx_height

    def get_local_height(self):
        """ return last known height if we are offline """
        return self.network.get_local_height() if self.network is not None else self.stored_height


    @profiler
    def build_reverse_history(self):
        self.tx_addr_hist = {}
        for addr, hist in self.addr_history.items():
            for tx_hash, h in hist:
                s = self.tx_addr_hist.get(tx_hash, set())
                s.add(addr)
                self.tx_addr_hist[tx_hash] = s

    @profiler
    def check_history(self):
        for addr, hist in self.addr_history.items():
            if not self.is_mine(addr):
                self.addr_history.pop(addr)
                continue

            for tx_hash, tx_height in hist:
                if tx_hash in self.pruned_txo.values() or self.txi.get(tx_hash) or self.txo.get(
                        tx_hash):
                    continue
                tx = self.tx_transactions.get(tx_hash)
                if tx is not None:
                    self.add_transaction(tx_hash, tx)


    def is_mine(self, address):
        return address in self.get_addresses(True)


    def update_password(self, new_password):
        if not new_password:
            raise ParamsError('51002')
        try:
            str(new_password)
        except:
            raise ParamsError('51001', "the password can't conversion into str")

        self.get_decode_seed()
        seed = pw_encode(self.decoded_seed, new_password)

        master_private_keys = {}
        for k, v in self.master_private_keys.items():
            b = pw_decode(v, self._password)
            c = pw_encode(b, new_password)
            master_private_keys[k] = c

        # save to mongodb
        self.seed = seed
        self.master_private_keys = master_private_keys

        self._password = new_password

    def get_decode_seed(self):
        self.decoded_seed =  pw_decode(self.seed, self._password)

    def mnemonic_to_seed(self):
        return Mnemonic.mnemonic_to_seed(self.decoded_seed, self._password)

    def make_seed(self, lang="en"):
        return Mnemonic(lang).make_seed()

    def make_and_add_seed(self, lang="en"):
        try:
            self.decoded_seed = ' '.join(self.make_seed(lang).split())
            self.seed = pw_encode(self.decoded_seed, self._password)
            return self.decoded_seed
        except DuplicateKeyError:
            # seed 重复
            self.add_seed(self.make_seed(lang))

    def create_master_keys(self):
        if self.decoded_seed is None:
            self.get_decode_seed()
        self.add_xprv_from_seed()

    def add_xprv_from_seed(self):
        # we don't store the seed, only the master xpriv
        xprv, xpub = bip32_root(self.mnemonic_to_seed())
        self.decoded_xprv, xpub = bip32_private_derivation(xprv, "m/", self.root_derivation)
        self.master_public_keys = {self.root_name: xpub}
        self.master_private_keys = {self.root_name: pw_encode(self.decoded_xprv, self._password)}

    def get_addresses(self, is_change=False):
        res =  self.addresses
        if not self.use_change:
            return res
        if is_change:
            return res.get('change', [])
        else:
            return res.get('receiving', [])

    def add_address(self, address, is_change=False):
        if self.use_change:
            if is_change:
                self.addresses['change'].append(address)
            else:
                self.addresses['receiving'].append(address)
        else:
            self.addresses.append(address)

        if address not in self.addr_history:
            self.addr_history[address] = []
        if self.synchronizer:
            self.synchronizer.add(address)
        self.save_accounts()

    def fill_fields(self):
        res = {}
        for field in DICT_BOTH_FIELD + DICT_DB_FIELD:
            if field not in ('master_private_keys', 'master_public_keys', 'accounts'):
                res.update({field: {}})
        for field in LIST_DB_FIELD + LIST_BOTH_FIELD:
            res.update({field: []})

        for field in NOR_FIELD:
            if field != 'seed':
                res.update({field: ' '})
        address = {} if self.use_change else []
        res.update(addresses=address)
        self.update_col(None, None, update_data=res)

    def get_balance(self, domain=None, exclude_claimtrietx=False):
        if domain is None:
            domain = self.get_addresses()
            if self.use_change:
                domain += self.get_addresses(True)

        cc = uu = xx = 0
        for addr in domain:
            c, u, x = self.get_addr_balance(addr, exclude_claimtrietx)
            cc += c
            uu += u
            xx += x
        return cc, uu, xx

    def get_addr_balance(self, address, exclude_claimtrietx=False):
        received, sent = self.get_addr_io(address)
        c = u = x = 0
        for txo, (tx_height, v, is_cb) in received.items():
            exclude_tx = False
            # check if received transaction is a claimtrie tx to ourself
            if exclude_claimtrietx:
                prevout_hash, prevout_n = txo.split(':')
                tx_type = self.claimtrie_transactions.get(txo)
                if tx_type is not None:
                    exclude_tx = True

            if not exclude_tx:
                if is_cb and tx_height + COINBASE_MATURITY > self.get_local_height():
                    x += v
                elif tx_height > 0:
                    c += v
                else:
                    u += v
                if txo in sent:
                    if sent[txo] > 0:
                        c -= v
                    else:
                        u -= v
        return c, u, x

    def get_addr_io(self, address):
        h = self.addr_history.get(address, [])
        received = {}
        sent = {}
        for tx_hash, height in h:
            l = self.txo.get(tx_hash, {}).get(address, [])
            for n, v, is_cb in l:
                received[tx_hash + ':%d' % n] = (height, v, is_cb)
        for tx_hash, height in h:
            l = self.txi.get(tx_hash, {}).get(address, [])
            for txi, v in l:
                sent[txi] = height
        return received, sent

    # synchronizer callback
    def get_address_history(self, address):
        return self.addr_history.get(address, [])

    # synchronizer callback
    def get_status(self, h):
        if not h:
            return None
        status = ''
        for tx_hash, height in h:
            status += tx_hash + ':%d:' % height
        return hashlib.sha256(status).digest().encode('hex')

    # synchronizer callback
    def receive_history_callback(self, addr, hist):
        with self.lock:
            old_hist = self.addr_history.get(addr, [])
            for tx_hash, height in old_hist:
                if (tx_hash, height) in hist:
                    continue
                # remove tx if it's not referenced in histories
                self.tx_addr_hist[tx_hash].remove(addr)
                if not self.tx_addr_hist[tx_hash]:
                    self.remove_transaction(tx_hash)

            self.addr_history[addr] = hist

            for tx_hash, tx_height in hist:
                # add it in case it was previously unconfirmed
                self.add_unverified_tx(tx_hash, tx_height)
                # add reference in tx_addr_hist
                s = self.tx_addr_hist.get(tx_hash, set())
                s.add(addr)
                self.tx_addr_hist[tx_hash] = s
                # if addr is new, we have to recompute txi and txo
                tx = self.tx_transactions.get(tx_hash)
                if tx is not None and self.txi.get(tx_hash, {}).get(addr) is None and self.txo.get(
                        tx_hash, {}).get(addr) is None:
                    self.add_transaction(tx_hash, tx)

    # synchronizer callback
    def get_history(self, domain=None):
        log.debug('start get history')
        from collections import defaultdict
        # get domain
        if domain is None:
            domain = self.get_addresses()
            if self.use_change:
                domain += self.get_addresses(True)

        # 1. Get the history of each address in the domain, maintain the
        #    delta of a tx as the sum of its deltas on domain addresses
        tx_deltas = defaultdict(int)
        for addr in domain:
            h = self.get_address_history(addr)
            for tx_hash, height in h:
                delta = self.get_tx_delta(tx_hash, addr)
                if delta is None or tx_deltas[tx_hash] is None:
                    tx_deltas[tx_hash] = None
                else:
                    tx_deltas[tx_hash] += delta

        # 2. create sorted history
        history = []
        for tx_hash, delta in tx_deltas.items():
            conf, timestamp = self.get_confirmations(tx_hash)
            history.append((tx_hash, conf, delta, timestamp))
        history.sort(key=lambda x: self.get_txpos(x[0]))
        history.reverse()

        # 3. add balance
        c, u, x = self.get_balance(domain)
        balance = c + u + x
        h2 = []
        for item in history:
            tx_hash, conf, delta, timestamp = item
            h2.append((tx_hash, conf, delta, timestamp, balance))
            if balance is None or delta is None:
                balance = None
            else:
                balance -= delta
        h2.reverse()

        # fixme: this may happen if history is incomplete
        if balance not in [None, 0]:
            log.error("Error: history not synchronized")
            return []

        return h2

    def get_txpos(self, tx_hash):
        "return position, even if the tx is unverified"
        with self.lock:
            x = self.verified_tx3.get(tx_hash)
        y = self.unverified_tx.get(tx_hash)
        if x:
            height, timestamp, pos = x
            return height, pos
        elif y:
            return y, 0
        else:
            return 1e12, 0

    def undo_verifications(self, height):
        """Used by the verifier when a reorg has happened"""
        log.debug('in undo_verifications >>>>>>>>>>>>>>> Unexpected')
        txs = []
        with self.lock:
            for tx_hash, item in self.verified_tx3:
                tx_height, timestamp, pos = item
                if tx_height >= height:
                    self.verified_tx3.pop(tx_hash, None)
                    txs.append(tx_hash)
        return txs

    def get_tx_delta(self, tx_hash, address):
        "effect of tx on address"
        # pruned
        if tx_hash in self.pruned_txo.values():
            return None
        delta = 0
        # substract the value of coins sent from address
        d = self.txi.get(tx_hash, {}).get(address, [])
        for n, v in d:
            delta -= v
        # add the value of the coins received at address
        d = self.txo.get(tx_hash, {}).get(address, [])
        for n, v, cb in d:
            delta += v
        return delta

    def find_pay_to_pubkey_address(self, prevout_hash, prevout_n):
        dd = self.txo.get(prevout_hash, {})
        for addr, l in dd.items():
            for n, v, is_cb in l:
                if n == prevout_n:
                    log.info("found pay-to-pubkey address:", addr)
                    return addr

    def get_spendable_coins(self, domain=None, exclude_frozen=True, abandon_txid=None):
        coins = []
        found_abandon_txid = False
        if domain is None:
            domain = self.get_addresses()
            if self.use_change:
                domain += self.get_addresses(True)

        if exclude_frozen:
            domain = set(domain) - self.frozen_addresses
        for addr in domain:
            c = self.get_addr_utxo(addr)
            for txo, v in c.items():
                tx_height, value, is_cb = v
                if is_cb and tx_height + COINBASE_MATURITY > self.get_local_height():
                    continue
                prevout_hash, prevout_n = txo.split(':')

                tx = self.tx_transactions.get(prevout_hash)
                try:
                    tx.deserialize()
                except AttributeError:
                    log.error("************* tx_transactions has no key %s", prevout_hash)
                    continue
                txout = tx.outputs()[int(prevout_n)]
                if txout[0] & (TYPE_CLAIM | TYPE_SUPPORT | TYPE_UPDATE) == 0 or (
                        abandon_txid is not None and prevout_hash == abandon_txid):
                    output = {
                        'address': addr,
                        'value': value,
                        'prevout_n': int(prevout_n),
                        'prevout_hash': prevout_hash,
                        'height': tx_height,
                        'coinbase': is_cb,
                        'is_claim': bool(txout[0] & TYPE_CLAIM),
                        'is_support': bool(txout[0] & TYPE_SUPPORT),
                        'is_update': bool(txout[0] & TYPE_UPDATE),
                    }
                    if txout[0] & TYPE_CLAIM:
                        output['claim_name'] = txout[1][0][0]
                        output['claim_value'] = txout[1][0][1]
                    elif txout[0] & TYPE_SUPPORT:
                        output['claim_name'] = txout[1][0][0]
                        output['claim_id'] = txout[1][0][1]
                    elif txout[0] & TYPE_UPDATE:
                        output['claim_name'] = txout[1][0][0]
                        output['claim_id'] = txout[1][0][1]
                        output['claim_value'] = txout[1][0][2]
                    coins.append(output)
                if abandon_txid is not None and prevout_hash == abandon_txid:
                    found_abandon_txid = True
                continue
        if abandon_txid is not None and not found_abandon_txid:
            raise ValueError("Can't spend from the given txid")
        return coins

    def get_addr_utxo(self, address):
        coins, spent = self.get_addr_io(address)
        for txi in spent:
            coins.pop(txi)
        return coins

    @profiler
    def add_input_info(self, txin):
        address = txin['address']
        sequence = self.get_address_index(address)
        # todo: 可以优化
        redeemScript = self.account_obj.redeem_script(*sequence)
        pubkeys = self.account_obj.get_pubkeys(*sequence)
        x_pubkeys = self.account_obj.get_xpubkeys(*sequence)
        # sort pubkeys and x_pubkeys, using the order of pubkeys
        pubkeys, x_pubkeys = zip(*sorted(zip(pubkeys, x_pubkeys)))
        txin['pubkeys'] = list(pubkeys)
        txin['x_pubkeys'] = list(x_pubkeys)
        txin['signatures'] = [None] * len(pubkeys)

        if redeemScript:
            txin['redeemScript'] = redeemScript
            txin['num_sig'] = self.account_obj.m
        else:
            txin['redeemPubkey'] = self.account_obj.get_pubkey(*sequence)
            txin['num_sig'] = 1

    def get_address_index(self, address):
        if not self.use_change:
            addresses = self.get_addresses()
            if address in addresses:
                return (0, addresses.index(address))
        else:
            # for acc_id in self.accounts:
            #     for for_change in [0, 1]:
            #         addresses = self.accounts[acc_id].get_addresses(for_change)
            #         if address in addresses:
            #             return (for_change, addresses.index(address))
            raise ServerError('52013', 'get_address_index')
        raise Exception("Address not found", address)

    def fee_per_kb(self, config):
        b = config.get('dynamic_fees')
        f = config.get('fee_factor', 50)
        F = config.get('fee_per_kb', RECOMMENDED_FEE)
        if b and self.network and self.network.fee:
            result = min(RECOMMENDED_FEE, self.network.fee * (50 + f) / 100)
        else:
            result = F
        return result

    def relayfee(self):
        RELAY_FEE = 5000
        MAX_RELAY_FEE = 50000
        f = self.network.relay_fee if self.network and self.network.relay_fee else RELAY_FEE
        return min(f, MAX_RELAY_FEE)

    def make_unsigned_transaction(self, coins, outputs, config, fixed_fee=None, change_addr=None,
                                  abandon_txid=None):
        # check outputs
        for type, data, value in outputs:
            if type & (TYPE_CLAIM | TYPE_UPDATE | TYPE_SUPPORT):
                data = data[1]
            if type & TYPE_ADDRESS:
                assert is_address(data), "Address " + data + " is invalid!"

        # Avoid index-out-of-range with coins[0] below
        if not coins:
            raise NotEnoughFunds()

        for item in coins:
            self.add_input_info(item)

        # change address
        if not self.use_change:
            change_addrs = [self.first_address]
        elif change_addr:
            change_addrs = [change_addr]
        else:
            # 这里可以从数据库里面取
            # send change to one of the accounts involved in the tx
            # address = coins[0].get('address')
            # if self.use_change and self.accounts[account].has_change():
            #     # New change addresses are created only after a few
            #     # confirmations.  Select the unused addresses within the
            #     # gap limit; if none take one at random
            #     addrs = self.self.account_obj.get_addresses(1)[-self.gap_limit_for_change:]
            #     change_addrs = [addr for addr in addrs if
            #                     self.get_num_tx(addr) == 0]
            #     if not change_addrs:
            #         change_addrs = [random.choice(addrs)]
            # else:
            #     change_addrs = [address]
            raise ServerError('52013', 'make_unsigned_transaction')

        # Fee estimator
        if fixed_fee is None:
            fee_estimator = partial(Transaction.fee_for_size,
                                    self.relayfee(),
                                    self.fee_per_kb(config))
        else:
            fee_estimator = lambda size: fixed_fee

        # Change <= dust threshold is added to the tx fee
        dust_threshold = 182 * 3 * self.relayfee() / 1000

        # Let the coin chooser select the coins to spend
        max_change = self.max_change_outputs if self.multiple_change else 1
        coin_chooser = self.coin_chooser(config)
        tx = coin_chooser.make_tx(coins, outputs, change_addrs[:max_change],
                                  fee_estimator, dust_threshold, abandon_txid=abandon_txid)

        # Sort the inputs and outputs deterministically
        tx.BIP_LI01_sort()

        return tx

    @profiler
    def sign_transaction(self, tx):
        if self.is_watching_only():
            return
        # Add derivation for utxo in wallets
        for i, addr in self.utxo_can_sign(tx):
            txin = tx.inputs()[i]
            txin['address'] = addr
            self.add_input_info(txin)
        # Add private keys
        keypairs = {}
        for x in self.xkeys_can_sign(tx):
            sec = self.get_private_key_from_xpubkey(x)
            if sec:
                keypairs[x] = sec
        # Sign
        if keypairs:
            tx.sign(keypairs)

    def coin_chooser_name(self, config):
        kind = config.get('coin_chooser')
        if kind not in COIN_CHOOSERS:
            kind = 'Priority'
        return kind

    def coin_chooser(self, config):
        klass = COIN_CHOOSERS[self.coin_chooser_name(config)]
        return klass()

    def is_watching_only(self):
        return False

    def utxo_can_sign(self, tx):
        out = set()
        coins = self.get_spendable_coins()
        for i in tx.inputs_without_script():
            txin = tx.inputs[i]
            for item in coins:
                if txin.get('prevout_hash') == item.get('prevout_hash') and txin.get(
                        'prevout_n') == item.get('prevout_n'):
                    out.add((i, item.get('address')))
        return out

    def xkeys_can_sign(self, tx):
        out = set()
        for x in tx.inputs_to_sign():
            if self.can_sign_xpubkey(x):
                out.add(x)
        return out

    def can_sign_xpubkey(self, x_pubkey):
        if x_pubkey[0:2] in ['02', '03', '04']:
            addr = public_key_to_bc_address(x_pubkey.decode('hex'))
            return self.is_mine(addr)
        elif x_pubkey[0:2] == 'ff':
            xpub, sequence = BIP32_Account.parse_xpubkey(x_pubkey)
            return xpub in [self.master_public_keys[k] for k in self.master_private_keys.keys()]
        elif x_pubkey[0:2] == 'fd':
            addrtype = ord(x_pubkey[2:4].decode('hex'))
            addr = hash_160_to_bc_address(x_pubkey[4:].decode('hex'), addrtype)
            return self.is_mine(addr)
        else:
            raise BaseException("z")

    def get_private_key_from_xpubkey(self, x_pubkey):
        if x_pubkey[0:2] in ['02', '03', '04']:
            addr = public_key_to_bc_address(x_pubkey.decode('hex'))
            if self.is_mine(addr):
                return self.get_private_key(addr)[0]
        elif x_pubkey[0:2] == 'ff':
            xpub, sequence = BIP32_Account.parse_xpubkey(x_pubkey)
            for k, v in self.master_public_keys.items():
                if v == xpub:
                    xprv = self.decoded_xprv
                    if xprv:
                        _, _, _, c, k = deserialize_xkey(xprv)
                        return bip32_private_key(sequence, k, c)
        elif x_pubkey[0:2] == 'fd':
            addrtype = ord(x_pubkey[2:4].decode('hex'))
            addr = hash_160_to_bc_address(x_pubkey[4:].decode('hex'), addrtype)
            if self.is_mine(addr):
                return self.get_private_key(addr)[0]
        else:
            raise BaseException("z")


    def get_name_claims(self, domain=None, include_abandoned=True, include_supports=True,
                        exclude_expired=True):
        claims = []
        if domain is None:
            domain = self.get_addresses()
            if self.use_change:
                domain += self.get_addresses(True)

        for addr in domain:
            txos, txis = self.get_addr_io(addr)
            for txo, v in txos.items():
                tx_height, value, is_cb = v
                prevout_hash, prevout_n = txo.split(':')

                tx = self.tx_transactions.get(prevout_hash)
                tx.deserialize()
                txout = tx.outputs()[int(prevout_n)]
                if not include_abandoned and txo in txis:
                    continue
                if not include_supports and txout[0] & TYPE_SUPPORT:
                    continue
                if txout[0] & (TYPE_CLAIM | TYPE_UPDATE | TYPE_SUPPORT):
                    local_height = self.get_local_height()
                    expired = tx_height + EXPIRATION_BLOCKS <= local_height
                    if expired and exclude_expired:
                        continue
                    output = {
                        'txid': prevout_hash,
                        'nout': int(prevout_n),
                        'address': addr,
                        'amount': Decimal(value),
                        'height': tx_height,
                        'expiration_height': tx_height + EXPIRATION_BLOCKS,
                        'expired': expired,
                        'confirmations': local_height - tx_height,
                        'is_spent': txo in txis,
                    }
                    if tx_height:
                        output['height'] = tx_height
                        output['expiration_height'] = tx_height + EXPIRATION_BLOCKS
                        output['expired'] = expired
                        output['confirmations'] = local_height - tx_height
                        output['is_pending'] = False
                    else:
                        output['height'] = None
                        output['expiration_height'] = None
                        output['expired'] = expired
                        output['confirmations'] = None
                        output['is_pending'] = True

                    if txout[0] & TYPE_CLAIM:
                        output['category'] = 'claim'
                        claim_name, claim_value = txout[1][0]
                        output['name'] = claim_name
                        output['value'] = claim_value.encode('hex')
                        claim_id = claim_id_hash(rev_hex(output['txid']).decode('hex'),
                                                 output['nout'])
                        claim_id = encode_claim_id_hex(claim_id)
                        output['claim_id'] = claim_id
                    elif txout[0] & TYPE_SUPPORT:
                        output['category'] = 'support'
                        claim_name, claim_id = txout[1][0]
                        output['name'] = claim_name
                        output['claim_id'] = encode_claim_id_hex(claim_id)
                    elif txout[0] & TYPE_UPDATE:
                        output['category'] = 'update'
                        claim_name, claim_id, claim_value = txout[1][0]
                        output['name'] = claim_name
                        output['value'] = claim_value.encode('hex')
                        output['claim_id'] = encode_claim_id_hex(claim_id)
                    if not expired:
                        output[
                            'blocks_to_expiration'] = tx_height + EXPIRATION_BLOCKS - local_height
                    claims.append(output)
        return claims

    @profiler
    def send_tx(self, tx, timeout=300):
        # fixme: this does not handle the case where server does not answer
        if not self.network.interface:
            raise Exception("Not connected.")

        txid = tx.hash()

        with self.send_tx_lock:
            self.network.send([('blockchain.transaction.broadcast', [str(tx)])], self.on_broadcast)
            self.tx_event.wait()
            success, result = self.receive_tx(txid, tx)
            self.tx_event.clear()

            if not success:
                log.error("send tx failed: %s", result)
                return success, result

            log.debug("waiting for %s to be added to the wallet", txid)
            now = time.time()
            while txid not in self.tx_transactions and time.time() < now + timeout:
                time.sleep(0.2)

            if txid not in self.tx_transactions:
                # TODO: detect if the txid is not known because it changed
                log.error("timed out while waiting to receive back a broadcast transaction, "
                          "expected txid: %s", txid)
                return False, "timed out while waiting to receive back a broadcast transaction, " \
                              "expected txid: %s" % txid
            log.info("successfully sent %s", txid)
        return success, result

    def receive_tx(self, tx_hash, tx):
        out = self.tx_result
        if out != tx_hash:
            return False, "error: " + out
        return True, out

    def on_broadcast(self, r):
        self.tx_result = r.get('result')
        self.tx_event.set()

    def get_spendable_claimtrietx_coin(self, txid, nOut):
        tx = self.tx_transactions.get(txid)
        if tx is None:
            raise BaseException('txid was not found in wallet')
        tx.deserialize()
        txouts = tx.outputs()
        if len(txouts) < nOut + 1:
            raise BaseException('nOut is too large')
        txout = txouts[nOut]
        txout_type, txout_dest, txout_value = txout
        if not txout_type & (TYPE_CLAIM | TYPE_UPDATE | TYPE_SUPPORT):
            raise BaseException('txid and nOut does not refer to a claimtrie transaction')

        address = txout_dest[1]
        utxos = self.get_addr_utxo(address)
        if txid + ':' + str(nOut) not in utxos:
            raise BaseException('this claimtrie transaction has already been spent')

        # create inputs
        is_update = txout_type & TYPE_UPDATE
        is_claim = txout_type & TYPE_CLAIM
        is_support = txout_type & TYPE_SUPPORT

        i = {'prevout_hash': txid, 'prevout_n': nOut, 'address': address, 'value': txout_value,
             'is_update': is_update, 'is_claim': is_claim, 'is_support': is_support}
        if is_claim:
            i['claim_name'] = txout_dest[0][0]
            i['claim_value'] = txout_dest[0][1]
        elif is_support:
            i['claim_name'] = txout_dest[0][0]
            i['claim_id'] = txout_dest[0][1]
        elif is_update:
            i['claim_name'] = txout_dest[0][0]
            i['claim_id'] = txout_dest[0][1]
            i['claim_value'] = txout_dest[0][2]
        else:
            # should not reach here
            raise ZeroDivisionError()

        self.add_input_info(i)
        return i


class Wallet(Abstract_Wallet):

    def create_main_account(self):
        xpub = self.master_public_keys.get("x/")
        self.account_obj = BIP32_Account({'xpub': xpub}, self.use_change)
        self.save_accounts()

    def save_accounts(self):
        self.accounts = {self.root_name: self.account_obj.dump()}


    def synchronize(self):
        self.account_obj.synchronize(self)

    @profiler
    def load_transactions(self):
        self.tx_transactions = {}
        for tx_hash, raw in self.transactions.items():
            tx = Transaction(raw)
            self.tx_transactions[tx_hash] = tx
            if not self.txi.get(tx_hash) and not self.txo.get(tx_hash) and \
                    (tx_hash not in self.pruned_txo.values()):
                log.info("removing unreferenced tx: %s", tx_hash)
                self.tx_transactions.pop(tx_hash)
                self.transactions.pop(tx_hash)

            # add to claimtrie transactions if its a claimtrie transaction
            tx.deserialize()
            for n, txout in enumerate(tx.outputs()):
                if txout[0] & (TYPE_CLAIM | TYPE_UPDATE | TYPE_SUPPORT):
                    key = tx_hash + ':' + str(n)
                    self.claimtrie_transactions[key] = txout[0]

    def add_transaction(self, tx_hash, tx):
        log.info("Adding tx: %s", tx_hash)
        is_coinbase = True if tx.inputs()[0].get('is_coinbase') else False
        # warring: 不能去掉dict
        temp_txi = dict(self.txi)
        temp_txo = dict(self.txo)
        with self.transaction_lock:
            # add inputs
            temp_txi[tx_hash] = d = {}
            for txi in tx.inputs():
                addr = txi.get('address')
                if not txi.get('is_coinbase'):
                    prevout_hash = txi['prevout_hash']
                    prevout_n = txi['prevout_n']
                    ser = prevout_hash + ':%d' % prevout_n
                if addr == "(pubkey)":
                    addr = self.find_pay_to_pubkey_address(prevout_hash, prevout_n)
                # find value from prev output
                if addr and self.is_mine(addr):
                    dd = temp_txo.get(prevout_hash, {})
                    for n, v, is_cb in dd.get(addr, []):
                        if n == prevout_n:
                            if d.get(addr) is None:
                                d[addr] = []
                            d[addr].append((ser, v))
                            break
                    else:
                        self.pruned_txo[ser] = tx_hash

            # add outputs
            temp_txo[tx_hash] = d = {}
            for n, txo in enumerate(tx.outputs()):
                ser = tx_hash + ':%d' % n
                _type, x, v = txo
                if _type & (TYPE_CLAIM | TYPE_UPDATE | TYPE_SUPPORT):
                    x = x[1]
                    self.claimtrie_transactions[ser] = _type
                if _type & TYPE_ADDRESS:
                    addr = x
                elif _type & TYPE_PUBKEY:
                    addr = public_key_to_bc_address(x.decode('hex'))
                else:
                    addr = None
                if addr and self.is_mine(addr):
                    if d.get(addr) is None:
                        d[addr] = []
                    d[addr].append((n, v, is_coinbase))
                # give v to txi that spends me
                next_tx = self.pruned_txo.get(ser)
                if next_tx is not None:
                    self.pruned_txo.pop(ser)
                    dd = temp_txi.get(next_tx, {})
                    if dd.get(addr) is None:
                        dd[addr] = []
                    dd[addr].append((ser, v))
            # save
            self.tx_transactions[tx_hash] = tx
            self.transactions[tx_hash] = str(tx)
            # todo: 这里是否可以优化, 一次性更新全部的值数据量大的话是不是有点慢
            self.txi = temp_txi
            self.txo = temp_txo
            log.info("Saved transaction, txi and txo")

    def remove_transaction(self, tx_hash):
        with self.transaction_lock:
            log.info("removing tx from history %s", tx_hash)
            # tx = self.transactions.pop(tx_hash)
            for ser, hh in self.pruned_txo.items():
                if hh == tx_hash:
                    self.pruned_txo.pop(ser)
            # add tx to pruned_txo, and undo the txi addition
            for next_tx, dd in self.txi.items():
                for addr, l in dd.items():
                    ll = l[:]
                    for item in ll:
                        ser, v = item
                        prev_hash, prev_n = ser.split(':')
                        if prev_hash == tx_hash:
                            l.remove(item)
                            self.pruned_txo[ser] = next_tx
                    if not l:
                        dd.pop(addr)
                    else:
                        dd[addr] = l
            try:
                self.txi.pop(tx_hash)
                self.txo.pop(tx_hash)
            except KeyError:
                log.error("tx was not in history", tx_hash)

    @profiler
    def load_accounts(self):
        for k, v in self.accounts.items():
            if not self.use_change:
                account_args = {'xpub': self.xpub, 'receiving': v}
                self.account_obj = BIP32_Account(account_args, self.use_change)
                corrected = self.account_obj.correct_pubkeys()
                if corrected:
                    self.save_accounts()
            else:
                # todo: self.use_change == True , v.get('pending')这种情况需要处理

                # 暂时不支持这种
                #     if v.get('imported'):
                #         self.accounts[k] = ImportedAccount(v)
                #     elif v.get('xpub'):
                #         self.accounts[k] = BIP32_Account(v)
                #         corrected = self.accounts[k].correct_pubkeys()
                #         if corrected:
                #             self.save_accounts()
                #
                #     elif v.get('pending'):
                #         removed = True
                #     else:
                #         self.print_error("cannot load account", v)
                # if removed:
                #     self.save_accounts()
                raise ServerError('52013', 'load_accounts')

    def set_up_to_date(self, up_to_date):
        with self.lock:
            self.up_to_date = up_to_date

    def is_up_to_date(self):
        with self.lock:
            return self.up_to_date

    def receive_tx_callback(self, tx_hash, tx, tx_height):
        self.add_transaction(tx_hash, tx)
        self.add_unverified_tx(tx_hash, tx_height)

    # spv callback
    def get_unverified_txs(self):
        """Returns a map from tx hash to transaction height"""
        return self.unverified_tx

    # spv callback
    def add_verified_tx(self, tx_hash, info):
        # Remove from the unverified map and add to the verified map and
        self.unverified_tx.pop(tx_hash, None)
        self.verified_tx3[tx_hash] = info  # (tx_height, timestamp, pos)

        conf, timestamp = self.get_confirmations(tx_hash)
        self.network.trigger_callback('verified', tx_hash, conf, timestamp)

    def get_confirmations(self, tx):
        """ return the number of confirmations of a monitored transaction. """
        with self.lock:
            if tx in self.verified_tx3:
                height, timestamp, pos = self.verified_tx3[tx]
                conf = (self.get_local_height() - height + 1)
                if conf <= 0:
                    timestamp = None
            elif tx in self.unverified_tx:
                conf = -1
                timestamp = None
            else:
                conf = 0
                timestamp = None

        return conf, timestamp

    def get_private_key(self, address):
        if self.is_watching_only():
            return []
        sequence = self.get_address_index(address)
        return self.account_obj.get_private_key(sequence, self, self._password)


    def wait_until_synchronized(self, callback=None):
        def wait_for_wallet():
            self.set_up_to_date(False)
            while not self.is_up_to_date():
                if callback:
                    msg = "%s\n%s %d" % (
                        "Please wait...",
                        "Addresses generated:",
                        len(self.get_addresses()))
                    callback(msg)
                time.sleep(0.1)

        def wait_for_network():
            while not self.network.is_connected():
                if callback:
                    msg = "%s \n" % ("Connecting...")
                    callback(msg)
                time.sleep(0.1)

        # wait until we are connected, because the user
        # might have selected another server
        if self.network:
            wait_for_network()
            wait_for_wallet()
        else:
            self.synchronize()
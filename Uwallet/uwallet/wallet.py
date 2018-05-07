# -*- coding: utf-8 -*-
# @Date    : 18-5-2 上午5:05
# @Author  : hetao
# @Email   : 18570367466@163.com
import json
import random
import threading
import time
import hashlib
import logging
from decimal import Decimal
from functools import partial

from uwallet import __version__ as UWALLET_VERSION
from uwallet.account import ImportedAccount, Multisig_Account, BIP32_Account
from uwallet.constants import TYPE_ADDRESS, TYPE_CLAIM, TYPE_SUPPORT, TYPE_UPDATE, TYPE_PUBKEY
from uwallet.constants import EXPIRATION_BLOCKS, COINBASE_MATURITY, RECOMMENDED_FEE
from uwallet.coinchooser import COIN_CHOOSERS
from uwallet.dbmodule import ExecuteMongodb
from uwallet.mnemonic import Mnemonic
from uwallet.settings import WALLET_FIELDS
from uwallet.synchronizer import Synchronizer
from uwallet.transaction import Transaction
from uwallet.util import PrintError, profiler, rev_hex
from uwallet.errors import NotEnoughFunds, InvalidPassword, ServerError
from uwallet.verifier import SPV
from uwallet.version import NEW_SEED_VERSION
from uwallet.ulord import regenerate_key, is_address, is_compressed, pw_encode, pw_decode
from uwallet.ulord import is_new_seed, hash_160_to_bc_address, xpub_from_xprv, bip32_private_key
from uwallet.ulord import encode_claim_id_hex, deserialize_xkey, claim_id_hash, is_private_key
from uwallet.ulord import public_key_from_private_key, public_key_to_bc_address
from uwallet.ulord import bip32_public_derivation, bip32_private_derivation, bip32_root

log = logging.getLogger(__name__)

# internal ID for imported account
IMPORTED_ACCOUNT = '/x'

# 预计舍弃这个
class WalletStorage(PrintError):
    def __init__(self, user):
        self.user = user if '_' in user else 'ulord_' + user
        self.app_key, self.user_name = tuple(self.user.split('_'))

        self.executeMongodb = ExecuteMongodb('uwallet_user', self.app_key)

        self.file_exists = False if self.read() is None else True


    def read(self):
        """ Read the all contents of the wallet ."""
        return self.executeMongodb.find_one_doc({'_id': self.user_name})


    def get(self, key, default=None):
        res = self.executeMongodb.find_one_doc({'_id': self.user_name}, {'_id': 0, key: 1})
        if res:
            return res[key]
        else:
            return default

    def put(self, key, value, operate=None):
        try:
            json.dumps(key)
            json.dumps(value)
        except:
            self.print_error("json error: cannot save", key)
            raise ServerError('52012', key)
        if operate is None:
            if value is not None:
                operate = '$set'
            else:
                operate = '$unset'
        res = self.executeMongodb.update_one_doc({'_id': self.user_name}, {operate: {key: value}})
        return res

    def del_wallet(self):
        res = self.executeMongodb.del_doc({'_id': self.user_name})
        return res


class Abstract_Wallet(PrintError):
    """
    Wallet classes are created to handle various address generation methods.
    Completion states (watching-only, single account, no seed, etc) are handled inside classes.
    """

    max_change_outputs = 3

    def __init__(self, storage):
        self.storage = storage
        self.network = None
        self.electrum_version = UWALLET_VERSION
        self.gap_limit_for_change = 1  # constant
        # saved fields
        self.seed_version = storage.get('seed_version', NEW_SEED_VERSION)
        # 不使用找零地址  --hetao 18.5.2
        self.use_change = storage.get('use_change', False)
        self.multiple_change = storage.get('multiple_change', False)

        self.labels = storage.get('labels', {})
        self.frozen_addresses = set(storage.get('frozen_addresses', []))
        self.stored_height = storage.get('stored_height', 0)  # last known height (for offline mode)
        self.history = storage.get('addr_history', {})  # address -> list(txid, height)

        # This attribute is set when wallet.start_threads is called.
        self.synchronizer = None

        # imported_keys is deprecated. The GUI should call convert_imported_keys
        self.imported_keys = self.storage.get('imported_keys', {})

        self.load_accounts() # 创建钱包没有做任何事
        self.load_transactions()   # 创建钱包没有做任何事
        self.build_reverse_history()    # 创建钱包没有做任何事

        # spv
        self.verifier = None
        # Transactions pending verification.  A map from tx hash to transaction
        # height.  Access is not contended so no lock is needed.
        self.unverified_tx = {}
        # Verified transactions.  Each value is a (height, timestamp, block_pos) tuple.
        # Access with self.lock.
        self.verified_tx = storage.get('verified_tx3', {})

        # there is a difference between wallet.up_to_date and interface.is_up_to_date()
        # interface.is_up_to_date() returns true when all requests have been answered and processed
        # wallet.up_to_date is true when the wallet is synchronized (stronger requirement)
        self.up_to_date = False
        self.lock = threading.Lock()
        self.transaction_lock = threading.Lock()
        self.tx_event = threading.Event()

        self.send_tx_lock = threading.Lock()

        self.check_history()  # 创建钱包没有做任何事

        self.claim_certificates = storage.get('claim_certificates', {})
        self.default_certificate_claim = storage.get('default_certificate_claim', None)

        # save wallet type the first time
        if self.storage.get('wallet_type') is None:
            self.storage.put('wallet_type', self.wallet_type)

    def diagnostic_name(self):
        return self.basename()

    def __str__(self):
        return self.basename()

    @profiler
    def load_transactions(self):
        self.txi = self.storage.get('txi', {})
        self.txo = self.storage.get('txo', {})
        self.pruned_txo = self.storage.get('pruned_txo', {})
        tx_list = self.storage.get('transactions', {})
        self.claimtrie_transactions = self.storage.get('claimtrie_transactions', {})
        self.transactions = {}
        for tx_hash, raw in tx_list.items():
            tx = Transaction(raw)
            self.transactions[tx_hash] = tx
            if self.txi.get(tx_hash) is None and self.txo.get(tx_hash) is None and\
                    (tx_hash not in self.pruned_txo.values()):
                log.info("removing unreferenced tx: %s", tx_hash)
                self.transactions.pop(tx_hash)

            # add to claimtrie transactions if its a claimtrie transaction
            tx.deserialize()
            for n, txout in enumerate(tx.outputs()):
                if txout[0] & (TYPE_CLAIM | TYPE_UPDATE | TYPE_SUPPORT):
                    self.claimtrie_transactions[tx_hash + ':' + str(n)] = txout[0]

    @profiler
    def save_transactions(self, write=False):
        with self.transaction_lock:
            tx = {}
            for k, v in self.transactions.items():
                tx[k] = str(v)
            self.storage.put('transactions', tx)
            self.storage.put('txi', self.txi)
            self.storage.put('txo', self.txo)
            self.storage.put('pruned_txo', self.pruned_txo)
            self.storage.put('addr_history', self.history)
            self.storage.put('claimtrie_transactions', self.claimtrie_transactions)

    def save_certificate(self, claim_id, private_key, write=True):
        certificate_keys = self.storage.get('claim_certificates') or {}
        certificate_keys[claim_id] = private_key
        self.storage.put('claim_certificates', certificate_keys)

    def set_default_certificate(self, claim_id, overwrite_existing=True, write=False):
        if self.default_certificate_claim is not None and overwrite_existing or not \
                self.default_certificate_claim:
            self.storage.put('default_certificate_claim', claim_id)
            self.default_certificate_claim = claim_id

    def get_certificate_signing_key(self, claim_id):
        certificates = self.storage.get('claim_certificates', {})
        return certificates.get(claim_id, None)

    def get_certificate_claim_ids_for_signing(self):
        certificates = self.storage.get('claim_certificates', {})
        return certificates.keys()

    def clear_history(self):
        with self.transaction_lock:
            self.txi = {}
            self.txo = {}
            self.pruned_txo = {}
        self.save_transactions()
        with self.lock:
            self.history = {}
            self.tx_addr_hist = {}

    @profiler
    def build_reverse_history(self):
        self.tx_addr_hist = {}
        for addr, hist in self.history.items():
            for tx_hash, h in hist:
                s = self.tx_addr_hist.get(tx_hash, set())
                s.add(addr)
                self.tx_addr_hist[tx_hash] = s

    @profiler
    def check_history(self):
        save = False
        for addr, hist in self.history.items():
            if not self.is_mine(addr):
                self.history.pop(addr)
                save = True
                continue

            for tx_hash, tx_height in hist:
                if tx_hash in self.pruned_txo.values() or self.txi.get(tx_hash) or self.txo.get(
                        tx_hash):
                    continue
                tx = self.transactions.get(tx_hash)
                if tx is not None:
                    self.add_transaction(tx_hash, tx)
                    save = True
        if save:
            self.save_transactions()

    # wizard action
    def get_action(self):
        pass

    def basename(self):
        return self.storage.user

    def convert_imported_keys(self, password):
        for k, v in self.imported_keys.items():
            sec = pw_decode(v, password)
            pubkey = public_key_from_private_key(sec)
            address = public_key_to_bc_address(pubkey.decode('hex'))
            if address != k:
                raise InvalidPassword()
            self.import_key(sec, password)
            self.imported_keys.pop(k)
        self.storage.put('imported_keys', self.imported_keys)

    def load_accounts(self):
        self.accounts = {}
        d = self.storage.get('accounts', {})
        removed = False
        for k, v in d.items():
            if v.get('imported'):
                self.accounts[k] = ImportedAccount(v)
            elif v.get('xpub'):
                self.accounts[k] = BIP32_Account(v)
                corrected = self.accounts[k].correct_pubkeys()
                if corrected:
                    self.save_accounts()

            elif v.get('pending'):
                removed = True
            else:
                self.print_error("cannot load account", v)
        if removed:
            self.save_accounts()

    def create_main_account(self):
        pass

    def synchronize(self):
        pass

    def can_create_accounts(self):
        return False

    def needs_next_account(self):
        return self.can_create_accounts() and self.accounts_all_used()

    def permit_account_naming(self):
        return self.can_create_accounts()

    def set_up_to_date(self, up_to_date):
        print '============in up to date', up_to_date
        with self.lock:
            self.up_to_date = up_to_date
        if up_to_date:
            self.save_transactions(write=True)

    def is_up_to_date(self):
        with self.lock:
            return self.up_to_date

    def is_imported(self, addr):
        account = self.accounts.get(IMPORTED_ACCOUNT)
        if account:
            return addr in account.get_addresses(0)
        else:
            return False

    def has_imported_keys(self):
        account = self.accounts.get(IMPORTED_ACCOUNT)
        return account is not None

    def import_key(self, sec, password):
        assert self.can_import(), 'This wallet cannot import private keys'
        try:
            pubkey = public_key_from_private_key(sec)
            address = public_key_to_bc_address(pubkey.decode('hex'))
        except Exception:
            raise Exception('Invalid private key')

        if self.is_mine(address):
            raise Exception('Address already in wallet')

        if self.accounts.get(IMPORTED_ACCOUNT) is None:
            self.accounts[IMPORTED_ACCOUNT] = ImportedAccount({'imported': {}})
        self.accounts[IMPORTED_ACCOUNT].add(address, pubkey, sec, password)
        self.save_accounts()

        # force resynchronization, because we need to re-run add_transaction
        if address in self.history:
            self.history.pop(address)

        if self.synchronizer:
            self.synchronizer.add(address)
        return address

    def delete_imported_key(self, addr):
        account = self.accounts[IMPORTED_ACCOUNT]
        account.remove(addr)
        if not account.get_addresses(0):
            self.accounts.pop(IMPORTED_ACCOUNT)
        self.save_accounts()

    def set_label(self, name, text=None):
        changed = False
        old_text = self.labels.get(name)
        if text:
            if old_text != text:
                self.labels[name] = text
                changed = True
        else:
            if old_text:
                self.labels.pop(name)
                changed = True

        if changed:
            self.storage.put('labels', self.labels)

        return changed

    def get_addresses(self, for_change=False):

        res =  self.storage.get('addresses', {})
        if for_change:
            return res.get('change', [])
        else:
            return res.get('receiving', [])


    def addresses(self, include_change=True):
        # 去掉这种方式, include_change 暂时别删除

        # return list(addr for acc in self.accounts for addr in
        #             self.get_account_addresses(acc, include_change))
        res = self.get_addresses()
        if include_change:
            res +=  self.get_addresses(True)
        return res


    def is_mine(self, address):
        return address in self.addresses(True)

    def is_change(self, address):
        if not self.is_mine(address):
            return False
        acct, s = self.get_address_index(address)
        if s is None:
            return False
        return s[0] == 1

    def get_address_index(self, address):
        for acc_id in self.accounts:
            for for_change in [0, 1]:
                addresses = self.accounts[acc_id].get_addresses(for_change)
                if address in addresses:
                    return acc_id, (for_change, addresses.index(address))
        raise Exception("Address not found", address)

    def get_private_key(self, address, password):
        if self.is_watching_only():
            return []
        account_id, sequence = self.get_address_index(address)
        return self.accounts[account_id].get_private_key(sequence, self, password)

    def get_public_keys(self, address):
        account_id, sequence = self.get_address_index(address)
        return self.accounts[account_id].get_pubkeys(*sequence)

    def sign_message(self, address, message, password):
        keys = self.get_private_key(address, password)
        assert len(keys) == 1
        sec = keys[0]
        key = regenerate_key(sec)
        compressed = is_compressed(sec)
        return key.sign_message(message, compressed, address)

    def decrypt_message(self, pubkey, message, password):
        address = public_key_to_bc_address(pubkey.decode('hex'))
        keys = self.get_private_key(address, password)
        secret = keys[0]
        ec = regenerate_key(secret)
        decrypted = ec.decrypt_message(message)
        return decrypted

    def add_unverified_tx(self, tx_hash, tx_height):
        # Only add if confirmed and not verified
        if tx_height > 0 and tx_hash not in self.verified_tx:
            self.unverified_tx[tx_hash] = tx_height

    def add_verified_tx(self, tx_hash, info):
        # Remove from the unverified map and add to the verified map and
        self.unverified_tx.pop(tx_hash, None)
        with self.lock:
            self.verified_tx[tx_hash] = info  # (tx_height, timestamp, pos)
        self.storage.put('verified_tx3', self.verified_tx)

        conf, timestamp = self.get_confirmations(tx_hash)
        self.network.trigger_callback('verified', tx_hash, conf, timestamp)

    def get_unverified_txs(self):
        """Returns a map from tx hash to transaction height"""
        return self.unverified_tx

    def undo_verifications(self, height):
        """Used by the verifier when a reorg has happened"""
        txs = []
        with self.lock:
            for tx_hash, item in self.verified_tx:
                tx_height, timestamp, pos = item
                if tx_height >= height:
                    self.verified_tx.pop(tx_hash, None)
                    txs.append(tx_hash)
        return txs

    def get_local_height(self):
        """ return last known height if we are offline """
        return self.network.get_local_height() if self.network else self.stored_height

    def get_confirmations(self, tx):
        """ return the number of confirmations of a monitored transaction. """
        with self.lock:
            if tx in self.verified_tx:
                height, timestamp, pos = self.verified_tx[tx]
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

    def get_txpos(self, tx_hash):
        "return position, even if the tx is unverified"
        with self.lock:
            x = self.verified_tx.get(tx_hash)
        y = self.unverified_tx.get(tx_hash)
        if x:
            height, timestamp, pos = x
            return height, pos
        elif y:
            return y, 0
        else:
            return 1e12, 0

    def is_found(self):
        return self.history.values() != [[]] * len(self.history)

    def get_num_tx(self, address):
        """ return number of transactions where address is involved """
        return len(self.history.get(address, []))

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

    def get_wallet_delta(self, tx):
        """ effect of tx on wallet """
        addresses = self.addresses(True)
        is_relevant = False
        is_send = False
        is_pruned = False
        is_partial = False
        v_in = v_out = v_out_mine = 0
        for item in tx.inputs():
            addr = item.get('address')
            if addr in addresses:
                is_send = True
                is_relevant = True
                d = self.txo.get(item['prevout_hash'], {}).get(addr, [])
                for n, v, cb in d:
                    if n == item['prevout_n']:
                        value = v
                        break
                else:
                    value = None
                if value is None:
                    is_pruned = True
                else:
                    v_in += value
            else:
                is_partial = True
        if not is_send:
            is_partial = False
        for addr, value in tx.get_outputs():
            v_out += value
            if addr in addresses:
                v_out_mine += value
                is_relevant = True
        if is_pruned:
            # some inputs are mine:
            fee = None
            if is_send:
                v = v_out_mine - v_out
            else:
                # no input is mine
                v = v_out_mine
        else:
            v = v_out_mine - v_in
            if is_partial:
                # some inputs are mine, but not all
                fee = None
                is_send = v < 0
            else:
                # all inputs are mine
                fee = v_out - v_in
        return is_relevant, is_send, v, fee

    def get_addr_io(self, address):
        h = self.history.get(address, [])
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

    def get_addr_utxo(self, address):
        coins, spent = self.get_addr_io(address)
        for txi in spent:
            coins.pop(txi)
        return coins

    # return the total amount ever received by an address
    def get_addr_received(self, address):
        received, sent = self.get_addr_io(address)
        return sum([v for height, v, is_cb in received.values()])

    # return the balance of a bitcoin address: confirmed and matured, unconfirmed, unmatured
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

    # get coin object in order to abandon calimtrie transactions
    # equivalent of get_spendable_coins but for claimtrie utxos
    def get_spendable_claimtrietx_coin(self, txid, nOut):
        tx = self.transactions.get(txid)
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

    def get_spendable_coins(self, domain=None, exclude_frozen=True, abandon_txid=None):
        coins = []
        found_abandon_txid = False
        if domain is None:
            domain = self.addresses(True)
        if exclude_frozen:
            domain = set(domain) - self.frozen_addresses
        for addr in domain:
            c = self.get_addr_utxo(addr)
            for txo, v in c.items():
                tx_height, value, is_cb = v
                if is_cb and tx_height + COINBASE_MATURITY > self.get_local_height():
                    continue
                prevout_hash, prevout_n = txo.split(':')
                tx = self.transactions.get(prevout_hash)
                tx.deserialize()
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

    def get_max_amount(self, config, inputs, fee):
        sendable = sum(map(lambda x: x['value'], inputs))
        for i in inputs:
            self.add_input_info(i)
        addr = self.addresses(False)[0]
        output = (TYPE_ADDRESS, addr, sendable)
        dummy_tx = Transaction.from_io(inputs, [output])
        if fee is None:
            fee_per_kb = self.fee_per_kb(config)
            fee = dummy_tx.estimated_fee(self.relayfee(), fee_per_kb)
        amount = max(0, sendable - fee)
        return amount, fee

    def get_account_addresses(self, acc_id, include_change=True):
        '''acc_id of None means all user-visible accounts'''
        addr_list = []
        acc_ids = self.accounts_to_show() if acc_id is None else [acc_id]
        for _acc_id in acc_ids:
            if _acc_id in self.accounts:
                acc = self.accounts[_acc_id]
                addr_list += acc.get_addresses(0)
                if include_change:
                    addr_list += acc.get_addresses(1)
        return addr_list
        # return self.addresses()

    def get_account_from_address(self, addr):
        """Returns the account that contains this address, or None"""
        for acc_id in self.accounts:  # similar to get_address_index but simpler
            if addr in self.get_account_addresses(acc_id):
                return acc_id
        return None

    def get_account_balance(self, account, exclude_claimtrietx=False):
        return self.get_balance(self.get_account_addresses(account, exclude_claimtrietx))

    def get_frozen_balance(self):
        return self.get_balance(self.frozen_addresses)

    def get_balance(self, domain=None, exclude_claimtrietx=False):
        if domain is None:
            domain = self.addresses(True)
        cc = uu = xx = 0
        for addr in domain:
            c, u, x = self.get_addr_balance(addr, exclude_claimtrietx)
            cc += c
            uu += u
            xx += x
        return cc, uu, xx

    def get_address_history(self, address):
        with self.lock:
            return self.history.get(address, [])

    def get_status(self, h):
        if not h:
            return None
        status = ''
        for tx_hash, height in h:
            status += tx_hash + ':%d:' % height
        return hashlib.sha256(status).digest().encode('hex')

    def find_pay_to_pubkey_address(self, prevout_hash, prevout_n):
        dd = self.txo.get(prevout_hash, {})
        for addr, l in dd.items():
            for n, v, is_cb in l:
                if n == prevout_n:
                    self.print_error("found pay-to-pubkey address:", addr)
                    return addr

    def add_transaction(self, tx_hash, tx):
        log.info("Adding tx: %s", tx_hash)
        is_coinbase = True if tx.inputs()[0].get('is_coinbase') else False
        with self.transaction_lock:
            # add inputs
            self.txi[tx_hash] = d = {}
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
                    dd = self.txo.get(prevout_hash, {})
                    for n, v, is_cb in dd.get(addr, []):
                        if n == prevout_n:
                            if d.get(addr) is None:
                                d[addr] = []
                            d[addr].append((ser, v))
                            break
                    else:
                        self.pruned_txo[ser] = tx_hash

            # add outputs
            self.txo[tx_hash] = d = {}
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
                    dd = self.txi.get(next_tx, {})
                    if dd.get(addr) is None:
                        dd[addr] = []
                    dd[addr].append((ser, v))
            # save
            self.transactions[tx_hash] = tx
            # todo: 需要把transactions的交互都改变为和数据库的交互
            # self.storage.put('transactions.%s' % tx_hash, tx)
            log.info("Saved transaction")
            print '$'*30, 'Saved transaction', tx_hash
            print id(self.transactions)

    def remove_transaction(self, tx_hash):
        with self.transaction_lock:
            self.print_error("removing tx from history", tx_hash)
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
                self.print_error("tx was not in history", tx_hash)

    def receive_tx_callback(self, tx_hash, tx, tx_height):
        self.add_transaction(tx_hash, tx)
        self.save_transactions()
        self.add_unverified_tx(tx_hash, tx_height)

    def receive_history_callback(self, addr, hist):
        with self.lock:
            old_hist = self.history.get(addr, [])
            for tx_hash, height in old_hist:
                if (tx_hash, height) not in hist:
                    # remove tx if it's not referenced in histories
                    self.tx_addr_hist[tx_hash].remove(addr)
                    if not self.tx_addr_hist[tx_hash]:
                        self.remove_transaction(tx_hash)

            self.history[addr] = hist

        for tx_hash, tx_height in hist:
            # add it in case it was previously unconfirmed
            self.add_unverified_tx(tx_hash, tx_height)
            # add reference in tx_addr_hist
            s = self.tx_addr_hist.get(tx_hash, set())
            s.add(addr)
            self.tx_addr_hist[tx_hash] = s
            # if addr is new, we have to recompute txi and txo
            tx = self.transactions.get(tx_hash)
            if tx is not None and self.txi.get(tx_hash, {}).get(addr) is None and self.txo.get(
                    tx_hash, {}).get(addr) is None:
                self.add_transaction(tx_hash, tx)

        # Write updated TXI, TXO etc.
        self.save_transactions()

    def get_history(self, domain=None):
        from collections import defaultdict
        # get domain
        if domain is None:
            domain = self.get_account_addresses(None)

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
            self.print_error("Error: history not synchronized")
            return []

        return h2

    def get_name_claims(self, domain=None, include_abandoned=True, include_supports=True,
                        exclude_expired=True):
        claims = []
        if domain is None:
            domain = self.get_account_addresses(None)

        for addr in domain:
            txos, txis = self.get_addr_io(addr)
            for txo, v in txos.items():
                tx_height, value, is_cb = v
                prevout_hash, prevout_n = txo.split(':')

                tx = self.transactions.get(prevout_hash)
                # todo: delete
                if tx is None:
                    print prevout_hash, '***', id(self.transactions)

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

    def get_label(self, tx_hash):
        label = self.labels.get(tx_hash, '')
        if label == '':
            label = self.get_default_label(tx_hash)
        return label

    def get_default_label(self, tx_hash):
        if self.txi.get(tx_hash) == {}:
            d = self.txo.get(tx_hash, {})
            labels = []
            for addr in d.keys():
                label = self.labels.get(addr)
                if label:
                    labels.append(label)
            return ', '.join(labels)
        return ''

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

    def get_tx_fee(self, tx):
        # this method can be overloaded
        return tx.get_fee()

    def coin_chooser_name(self, config):
        kind = config.get('coin_chooser')
        if kind not in COIN_CHOOSERS:
            kind = 'Priority'
        return kind

    def coin_chooser(self, config):
        klass = COIN_CHOOSERS[self.coin_chooser_name(config)]
        return klass()

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
        if change_addr:
            change_addrs = [change_addr]
        else:
            # send change to one of the accounts involved in the tx
            address = coins[0].get('address')
            account, _ = self.get_address_index(address)
            if self.use_change and self.accounts[account].has_change():
                # New change addresses are created only after a few
                # confirmations.  Select the unused addresses within the
                # gap limit; if none take one at random
                addrs = self.accounts[account].get_addresses(1)[-self.gap_limit_for_change:]
                change_addrs = [addr for addr in addrs if
                                self.get_num_tx(addr) == 0]
                if not change_addrs:
                    change_addrs = [random.choice(addrs)]
            else:
                change_addrs = [address]

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

    def mktx(self, outputs, password, config, fee=None, change_addr=None, domain=None):
        coins = self.get_spendable_coins(domain)
        tx = self.make_unsigned_transaction(coins, outputs, config, fee, change_addr)
        self.sign_transaction(tx, password)
        return tx

    def add_input_info(self, txin):
        address = txin['address']
        account_id, sequence = self.get_address_index(address)
        account = self.accounts[account_id]
        redeemScript = account.redeem_script(*sequence)
        pubkeys = account.get_pubkeys(*sequence)
        x_pubkeys = account.get_xpubkeys(*sequence)
        # sort pubkeys and x_pubkeys, using the order of pubkeys
        pubkeys, x_pubkeys = zip(*sorted(zip(pubkeys, x_pubkeys)))
        txin['pubkeys'] = list(pubkeys)
        txin['x_pubkeys'] = list(x_pubkeys)
        txin['signatures'] = [None] * len(pubkeys)

        if redeemScript:
            txin['redeemScript'] = redeemScript
            txin['num_sig'] = account.m
        else:
            txin['redeemPubkey'] = account.get_pubkey(*sequence)
            txin['num_sig'] = 1

    def sign_transaction(self, tx, password):
        if self.is_watching_only():
            return
        # Raise if password is not correct.
        self.check_password(password)
        # Add derivation for utxo in wallets
        for i, addr in self.utxo_can_sign(tx):
            txin = tx.inputs()[i]
            txin['address'] = addr
            self.add_input_info(txin)
        # Add private keys
        keypairs = {}
        for x in self.xkeys_can_sign(tx):
            sec = self.get_private_key_from_xpubkey(x, password)
            if sec:
                keypairs[x] = sec
        # Sign
        if keypairs:
            tx.sign(keypairs)

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
            while txid not in self.transactions and time.time() < now + timeout:
                time.sleep(0.2)

            if txid not in self.transactions:
                #TODO: detect if the txid is not known because it changed
                log.error("timed out while waiting to receive back a broadcast transaction, "
                          "expected txid: %s", txid)
                return False, "timed out while waiting to receive back a broadcast transaction, " \
                              "expected txid: %s" % txid
            log.info("successfully sent %s", txid)
        return success, result

    def on_broadcast(self, r):
        self.tx_result = r.get('result')
        self.tx_event.set()

    def receive_tx(self, tx_hash, tx):
        out = self.tx_result
        if out != tx_hash:
            return False, "error: " + out
        return True, out

    def update_password(self, old_password, new_password):
        if new_password == '':
            new_password = None

        if self.has_seed():
            decoded = self.get_seed(old_password)
            self.seed = pw_encode(decoded, new_password)
            self.storage.put('seed', self.seed)

        imported_account = self.accounts.get(IMPORTED_ACCOUNT)
        if imported_account:
            imported_account.update_password(old_password, new_password)
            self.save_accounts()

        if hasattr(self, 'master_private_keys'):
            for k, v in self.master_private_keys.items():
                b = pw_decode(v, old_password)
                c = pw_encode(b, new_password)
                self.master_private_keys[k] = c
            self.storage.put('master_private_keys', self.master_private_keys)

        self.set_use_encryption(new_password is not None)

    def is_frozen(self, addr):
        return addr in self.frozen_addresses

    def set_frozen_state(self, addrs, freeze):
        '''Set frozen state of the addresses to FREEZE, True or False'''
        if all(self.is_mine(addr) for addr in addrs):
            if freeze:
                self.frozen_addresses |= set(addrs)
            else:
                self.frozen_addresses -= set(addrs)
            self.storage.put('frozen_addresses', list(self.frozen_addresses))
            return True
        return False

    def prepare_for_verifier(self):
        # review transactions that are in the history
        for addr, hist in self.history.items():
            for tx_hash, tx_height in hist:
                # add it in case it was previously unconfirmed
                self.add_unverified_tx(tx_hash, tx_height)

        # if we are on a pruning server, remove unverified transactions
        with self.lock:
            vr = self.verified_tx.keys() + self.unverified_tx.keys()
        for tx_hash in self.transactions.keys():
            if tx_hash not in vr:
                log.info("removing transaction %s", tx_hash)
                self.transactions.pop(tx_hash)

    def start_threads(self, network):
        self.network = network
        if self.network is not None:
            self.prepare_for_verifier()
            self.verifier = SPV(self.network, self)
            self.synchronizer = Synchronizer(self, network)
            network.add_jobs([self.verifier, self.synchronizer])
        else:
            self.verifier = None
            self.synchronizer = None

    def stop_threads(self):
        if self.network:
            self.network.remove_jobs([self.synchronizer, self.verifier])
            if self.synchronizer:
                self.synchronizer.release()
                self.synchronizer = None
            else:
                log.warning("Synchronizer alread released.")
            self.verifier = None
            # Now no references to the syncronizer or verifier
            # remain so they will be GC-ed
            self.storage.put('stored_height', self.get_local_height())

    def wait_until_synchronized(self, callback=None):
        def wait_for_wallet():
            self.set_up_to_date(False)
            while not self.is_up_to_date():
                if callback:
                    msg = "%s\n%s %d" % (
                        "Please wait...",
                        "Addresses generated:",
                        len(self.addresses(True)))
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

    def accounts_to_show(self):
        return self.accounts.keys()

    def get_accounts(self):
        return {a_id: a for a_id, a in self.accounts.items()
                if a_id in self.accounts_to_show()}

    def get_account_name(self, k):
        return self.labels.get(k, self.accounts[k].get_name(k))

    def get_account_names(self):
        ids = self.accounts_to_show()
        return dict(zip(ids, map(self.get_account_name, ids)))

    def add_account(self, account_id, account):
        self.accounts[account_id] = account
        self.save_accounts()

    def save_accounts(self):
        d = {}
        for k, v in self.accounts.items():
            d[k] = v.dump()
        self.storage.put('accounts', d)

    def can_import(self):
        return not self.is_watching_only()

    def can_export(self):
        return not self.is_watching_only()

    def is_used(self, address):
        h = self.history.get(address, [])
        c, u, x = self.get_addr_balance(address)
        return len(h) > 0 and c + u + x == 0

    def is_empty(self, address):
        c, u, x = self.get_addr_balance(address)
        return c + u + x == 0

    def address_is_old(self, address, age_limit=2):
        age = -1
        h = self.history.get(address, [])
        for tx_hash, tx_height in h:
            if tx_height == 0:
                tx_age = 0
            else:
                tx_age = self.get_local_height() - tx_height + 1
            if tx_age > age:
                age = tx_age
        return age > age_limit

    def can_sign(self, tx):
        if self.is_watching_only():
            return False
        if tx.is_complete():
            return False
        if self.xkeys_can_sign(tx):
            return True
        if self.utxo_can_sign(tx):
            return True
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

    def get_private_key_from_xpubkey(self, x_pubkey, password):
        if x_pubkey[0:2] in ['02', '03', '04']:
            addr = public_key_to_bc_address(x_pubkey.decode('hex'))
            if self.is_mine(addr):
                return self.get_private_key(addr, password)[0]
        elif x_pubkey[0:2] == 'ff':
            xpub, sequence = BIP32_Account.parse_xpubkey(x_pubkey)
            for k, v in self.master_public_keys.items():
                if v == xpub:
                    xprv = self.get_master_private_key(k, password)
                    if xprv:
                        _, _, _, c, k = deserialize_xkey(xprv)
                        return bip32_private_key(sequence, k, c)
        elif x_pubkey[0:2] == 'fd':
            addrtype = ord(x_pubkey[2:4].decode('hex'))
            addr = hash_160_to_bc_address(x_pubkey[4:].decode('hex'), addrtype)
            if self.is_mine(addr):
                return self.get_private_key(addr, password)[0]
        else:
            raise BaseException("z")

    def can_sign_xpubkey(self, x_pubkey):
        if x_pubkey[0:2] in ['02', '03', '04']:
            addr = public_key_to_bc_address(x_pubkey.decode('hex'))
            return self.is_mine(addr)
        elif x_pubkey[0:2] == 'ff':
            if not isinstance(self, BIP32_Wallet):
                return False
            xpub, sequence = BIP32_Account.parse_xpubkey(x_pubkey)
            return xpub in [self.master_public_keys[k] for k in self.master_private_keys.keys()]
        elif x_pubkey[0:2] == 'fd':
            addrtype = ord(x_pubkey[2:4].decode('hex'))
            addr = hash_160_to_bc_address(x_pubkey[4:].decode('hex'), addrtype)
            return self.is_mine(addr)
        else:
            raise BaseException("z")

    def is_watching_only(self):
        return False

    def can_change_password(self):
        return not self.is_watching_only()

    def get_unused_addresses(self, account):
        # fixme: use slots from expired requests
        domain = self.get_account_addresses(account, include_change=False)
        return [addr for addr in domain if not self.history.get(addr)]

    def get_unused_address(self, account):
        domain = self.get_account_addresses(account, include_change=False)
        for addr in domain:
            if not self.history.get(addr):
                return addr


class Deterministic_Wallet(Abstract_Wallet):
    def __init__(self, storage):
        Abstract_Wallet.__init__(self, storage)

    def has_seed(self):
        return self.seed != ''

    def is_deterministic(self):
        return True

    def is_watching_only(self):
        return not self.has_seed()

    def add_seed(self, seed, password):

        # self.seed_version, self.seed = self.format_seed(seed)
        seed = ' '.join(seed.split())
        if password:
            self.seed = pw_encode(seed, password)
        self.storage.put('seed', self.seed)
        # todo: 这里的seed需要设置唯一性, 失败之后需要重新生成
        # self.storage.put('seed_version', self.seed_version)

    def get_seed(self, password):
        return pw_decode(self.seed, password)

    def get_mnemonic(self, password):
        return self.get_seed(password)

    # def change_gap_limit(self, value):
    #     '''This method is not called in the code, it is kept for console use'''
    #     if value >= self.gap_limit:
    #         self.gap_limit = value
    #         self.storage.put('gap_limit', self.gap_limit)
    #         return True
    #
    #     elif value >= self.min_acceptable_gap():
    #         for key, account in self.accounts.items():
    #             addresses = account.get_addresses(False)
    #             k = self.num_unused_trailing_addresses(addresses)
    #             n = len(addresses) - k + value
    #             account.receiving_pubkeys = account.receiving_pubkeys[0:n]
    #             account.receiving_addresses = account.receiving_addresses[0:n]
    #         self.gap_limit = value
    #         self.storage.put('gap_limit', self.gap_limit)
    #         self.save_accounts()
    #         return True
    #     else:
    #         return False

    def num_unused_trailing_addresses(self, addresses):
        k = 0
        for a in addresses[::-1]:
            if self.history.get(a):
                break
            k = k + 1
        return k

    def min_acceptable_gap(self):
        # fixme: this assumes wallet is synchronized
        n = 0
        nmax = 0

        for account in self.accounts.values():
            addresses = account.get_addresses(0)
            k = self.num_unused_trailing_addresses(addresses)
            for a in addresses[0:-k]:
                if self.history.get(a):
                    n = 0
                else:
                    n += 1
                    if n > nmax:
                        nmax = n
        return nmax + 1

    def default_account(self):
        return self.accounts['0']

    def create_new_address(self, account=None, for_change=0):
        with self.lock:
            if account is None:
                account = self.default_account()
            address = account.create_new_address(for_change)
            self.add_address(address)
        log.info("created address %s", address)
        return address

    def add_address(self, address, is_change=False):
        if is_change:
            self.storage.put('addresses.change', address, '$push')
        else:
            self.storage.put('addresses.receiving', address, '$push')

        if address not in self.history:
            self.history[address] = []
        if self.synchronizer:
            self.synchronizer.add(address)
        self.save_accounts()

    def get_least_used_address(self, account=None, for_change=False, max_count=100):
        """
        get the least used address, if none exist generate a fresh one

        :param account: account number, default None
        :param for_change: is change address
        :param max_count: maximum history count on an address, default of 100
                          uwallet server used to have a limit of 1000 utxos per address
                          it would be nice to be able to re-enable it, so let's set this
                          to 100 and gradually distribute addresses more optimally
        :return:
        """

        domain = self.get_account_addresses(account, include_change=for_change)
        hist = {}
        for addr in domain:
            if for_change != self.is_change(addr):
                continue
            else:
                h = self.history.get(addr)
                if h and len(h) >= max_count:
                    continue
                elif h:
                    hist[addr] = h
                else:
                    hist[addr] = []
        if hist:
            return sorted(hist.keys(), key=lambda x: len(hist[x]))[0]
        return self.create_new_address(account, for_change=for_change)

    def synchronize(self):
        with self.lock:
            for account in self.accounts.values():
                account.synchronize(self)

    def is_beyond_limit(self, address, account, is_change):
        if type(account) == ImportedAccount:
            return False
        addr_list = account.get_addresses(is_change)
        i = addr_list.index(address)
        prev_addresses = addr_list[:max(0, i)]
        limit = self.gap_limit_for_change if is_change else self.gap_limit
        if len(prev_addresses) < limit:
            return False
        prev_addresses = prev_addresses[max(0, i - limit):]
        for addr in prev_addresses:
            if self.history.get(addr):
                return False
        return True

    def get_action(self):
        if not self.get_master_public_keys():
            return 'create_seed'
        if not self.accounts:
            return 'create_main_account'

    def get_master_public_keys(self):
        out = {}
        for k, account in self.accounts.items():
            if type(account) == ImportedAccount:
                continue
            name = self.get_account_name(k)
            mpk_text = '\n\n'.join(account.get_master_pubkeys())
            out[name] = mpk_text
        return out


class BIP32_Wallet(Deterministic_Wallet):
    # abstract class, bip32 logic
    root_name = 'x/'

    def __init__(self, storage):
        Deterministic_Wallet.__init__(self, storage)
        self.master_public_keys = storage.get('master_public_keys', {})
        self.master_private_keys = storage.get('master_private_keys', {})
        self.gap_limit = storage.get('gap_limit', 1)

    def is_watching_only(self):
        return not bool(self.master_private_keys)

    def can_import(self):
        return False

    def get_master_public_key(self):
        return self.master_public_keys.get(self.root_name)

    def get_master_private_key(self, account, password):
        k = self.master_private_keys.get(account)
        if not k:
            return
        xprv = pw_decode(k, password)
        try:
            deserialize_xkey(xprv)
        except:
            raise InvalidPassword()
        return xprv

    def check_password(self, password):
        xpriv = self.get_master_private_key(self.root_name, password)
        xpub = self.master_public_keys[self.root_name]
        if deserialize_xkey(xpriv)[3] != deserialize_xkey(xpub)[3]:
            raise InvalidPassword()

    def add_master_public_key(self, name, xpub):
        if xpub in self.master_public_keys.values():
            raise BaseException('Duplicate master public key')
        self.master_public_keys[name] = xpub
        self.storage.put('master_public_keys', self.master_public_keys)

    def add_master_private_key(self, name, xpriv, password):
        self.master_private_keys[name] = pw_encode(xpriv, password)
        self.storage.put('master_private_keys', self.master_private_keys)

    def derive_xkeys(self, root, derivation, password):
        x = self.master_private_keys[root]
        root_xprv = pw_decode(x, password)
        xprv, xpub = bip32_private_derivation(root_xprv, root, derivation)
        return xpub, xprv

    def mnemonic_to_seed(self, seed, password):
        return Mnemonic.mnemonic_to_seed(seed, password)

    def make_seed(self, lang="en"):
        return Mnemonic(lang).make_seed()

    def format_seed(self, seed):
        return NEW_SEED_VERSION, ' '.join(seed.split())


class BIP32_RD_Wallet(BIP32_Wallet):
    # Abstract base class for a BIP32 wallet with a self.root_derivation

    @classmethod
    def account_derivation(cls, account_id):
        return cls.root_derivation + account_id

    @classmethod
    def address_derivation(cls, account_id, change, address_index):
        account_derivation = cls.account_derivation(account_id)
        return "%s/%d/%d" % (account_derivation, change, address_index)

    def address_id(self, address):
        acc_id, (change, address_index) = self.get_address_index(address)
        return self.address_derivation(acc_id, change, address_index)

    def add_xprv_from_seed(self, seed, name, password, passphrase=''):
        # we don't store the seed, only the master xpriv
        xprv, xpub = bip32_root(self.mnemonic_to_seed(seed, passphrase))
        xprv, xpub = bip32_private_derivation(xprv, "m/", self.root_derivation)
        self.add_master_public_key(name, xpub)
        self.add_master_private_key(name, xprv, password)

    def add_xpub_from_seed(self, seed, name):
        # store only master xpub
        xprv, xpub = bip32_root(self.mnemonic_to_seed(seed, ''))
        xprv, xpub = bip32_private_derivation(xprv, "m/", self.root_derivation)
        self.add_master_public_key(name, xpub)

    def create_master_keys(self, password):
        seed = self.get_seed(password)
        self.add_xprv_from_seed(seed, self.root_name, password)


# class Wallet(BIP32_RD_Wallet, Mnemonic):
class Wallet(BIP32_RD_Wallet, Mnemonic):
    # Standard wallet
    root_derivation = "m/"
    wallet_type = 'standard'

    def create_main_account(self):
        xpub = self.master_public_keys.get("x/")
        account = BIP32_Account({'xpub': xpub})
        self.add_account('0', account)

    def __getattr__(self, item):
        if item in WALLET_FIELDS:
            res =  self.storage.get(item)
            self.__setattr__(item, res)
            return res
        else:
            raise AttributeError("Wallet has no attribute %s" % item)


# 所有对钱包属性的操作都是对数据库的操作
class Wallet_():
    def __init__(self, storage):
        storage.put('wallet_type', 'standard')





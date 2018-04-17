# -*- coding: UTF-8 -*-
import argparse
import ast
import base64
import copy
import datetime
import json
import logging
import sys
import time
from decimal import Decimal
from functools import wraps

from ecdsa import BadSignatureError

from unetschema.claim import ClaimDict
from unetschema.decode import smart_decode
from unetschema.error import DecodeError
from unetschema.signer import SECP256k1, get_signer
from unetschema.uri import URIParseError, parse_unet_uri
from unetschema.validator import validate_claim_id
from uwallet.wallet import WalletStorage, Wallet

from uwallet import __version__
from uwallet.contacts import Contacts
from uwallet.constants import COIN, TYPE_ADDRESS, TYPE_CLAIM, TYPE_SUPPORT, TYPE_UPDATE
from uwallet.constants import RECOMMENDED_CLAIMTRIE_HASH_CONFIRMS, MAX_BATCH_QUERY_SIZE
from uwallet.hashing import Hash, hash_160
from uwallet.claims import verify_proof
from uwallet.ulord import hash_160_to_bc_address, is_address, decode_claim_id_hex
from uwallet.ulord import encode_claim_id_hex, encrypt_message, public_key_from_private_key
from uwallet.ulord import claim_id_hash, verify_message
from uwallet.base import base_decode
from uwallet.transaction import Transaction
from uwallet.transaction import decode_claim_script, deserialize as deserialize_transaction
from uwallet.transaction import get_address_from_output_script, script_GetOp
from uwallet.errors import InvalidProofError, NotEnoughFunds
from uwallet.util import format_satoshis, rev_hex
from uwallet.mnemonic import Mnemonic

from uwallet import gl
from uwallet.wallet import Wallet, WalletStorage
log = logging.getLogger(__name__)

known_commands = {}
ADDRESS_LENGTH = 25
MAX_PAGE_SIZE = 500


# Format amount to be decimal encoded string
# Format value to be hex encoded string
def format_amount_value(obj):
    if isinstance(obj, dict):
        for k, v in obj.iteritems():
            if k == 'amount' or k == 'effective_amount':
                if not isinstance(obj[k], float):
                    obj[k] = float(obj[k]) / float(COIN)
            elif k == 'supports' and isinstance(v, list):
                obj[k] = [{'txid': txid, 'nout': nout, 'amount': float(amount) / float(COIN)}
                          for (txid, nout, amount) in v]
            elif isinstance(v, (list, dict)):
                obj[k] = format_amount_value(v)
    elif isinstance(obj, list):
        obj = [format_amount_value(o) for o in obj]
    return obj


class Command(object):
    def __init__(self, func, s):
        self.name = func.__name__
        self.requires_network = 'n' in s
        self.requires_wallet = 'w' in s
        self.requires_password = 'p' in s
        self.description = func.__doc__
        self.help = self.description.split('.')[0] if self.description else None
        varnames = func.func_code.co_varnames[1:func.func_code.co_argcount]
        self.defaults = func.func_defaults
        if self.defaults:
            n = len(self.defaults)
            self.params = list(varnames[:-n])
            self.options = list(varnames[-n:])
        else:
            self.params = list(varnames)
            self.options = []
            self.defaults = []


def command(s):
    def decorator(func):
        global known_commands
        name = func.__name__
        known_commands[name] = Command(func, s)

        @wraps(func)
        def func_wrapper(*args, **kwargs):
            print 'func_wrapper'
            return func(*args, **kwargs)

        return func_wrapper

    return decorator


class Commands(object):
    def __init__(self, config, wallets, network, callback=None, password=None, new_password=None):
        self.config = config
        self.wallets = wallets
        self.network = network
        self._callback = callback
        self._password = password
        self.new_password = new_password
        self.contacts = Contacts(self.config)

    def _run(self, method, args, password_getter):
        cmd = known_commands[method]
        if self._password and cmd.requires_password and self.wallet.use_encryption:
            self._password = apply(password_getter, ())
        f = getattr(self, method)
        result = f(*args)
        self._password = None
        if self._callback:
            apply(self._callback, ())
        return result

    @command('')
    def haswallet(self,user):
        """Is there a wallet?"""
        raise BaseException('Not a JSON-RPC command')

    @command('')
    def haspassword(self,user):
        """Is there a wallet?"""
        raise BaseException('Not a JSON-RPC command')

    @command('')
    def daemonisrunning(self):
        """Daemon is running"""
        raise BaseException('Not a JSON-RPC command')

    @command('')
    def commands(self):
        """List of commands"""
        return ' '.join(sorted(known_commands.keys()))

    @command('')
    def create(self, user, password):
        """Create a new wallet"""
        if not password:
            return {
            'success': False,
            'reason': 'invalid password',
        }
        storage = WalletStorage(self.config.get_wallet_path() + '//' + user)
        wallet = Wallet(storage)
        seed = wallet.make_seed()
        wallet.add_seed(seed, password)
        wallet.create_master_keys(password)
        wallet.create_main_account()
        wallet.synchronize()
        wallet.start_threads(self.network)
        self.wallets[user] = wallet
        wallet.storage.write()
        log.info("Wallet saved in '%s'", wallet.storage.path)
        return {
            'success': True,
            'seed': seed,
            # 'message': "Please keep seed in a safe place; if you lose it, you will not be able to restore " \
            #       "your wallet."
        }


    @command('wn')
    def restore(self, text, user):
        """Restore a wallet from text. Text can be a seed phrase, a master
        public key, a master private key, a list of bitcoin addresses
        or bitcoin private keys. If you want to be prompted for your
        seed, type '?' or ':' (concealed) """
        raise BaseException('Not a JSON-RPC command')

    @command('w')
    def deseed(self, user):
        """Remove seed from wallet. This creates a seedless, watching-only
        wallet."""
        raise BaseException('Not a JSON-RPC command')

    @command('wp')
    def password(self, user):
        """Change wallet password. """
        self.wallets[user].update_password(self._password, self.new_password)
        self.wallets[user].storage.write()
        return {'password': self.wallets[user].use_encryption}

    @command('')
    def getconfig(self, key):
        """Return a configuration variable. """
        return self.config.get(key)

    @command('')
    def setconfig(self, key, value):
        """Set a configuration variable. 'value' may be a string or a Python expression."""
        try:
            value = ast.literal_eval(value)
        except:
            pass
        self.config.set_key(key, value)
        return True

    @command('')
    def make_seed(self, nbits=128, entropy=1, language=None):
        """Create a seed"""
        language = language or "en"
        s = Mnemonic(language).make_seed(nbits, custom_entropy=entropy)
        return s.encode('utf8')

    @command('')
    def check_seed(self, seed, entropy=1, language=None):
        """Check that a seed was generated with given entropy"""
        language = language or "en"
        return Mnemonic(language).check_seed(seed, entropy)

    @command('n')
    def getaddresshistory(self, address):
        """Return the transaction history of any address. Note: This is a
        walletless server query, results are not checked by SPV.
        """
        return self.network.synchronous_get(('blockchain.address.get_history', [address]))

    @command('w')
    def listunspent(self, user):
        """List unspent outputs. Returns the list of unspent transaction
        outputs in your wallet."""
        l = copy.deepcopy(self.wallets[user].get_spendable_coins(exclude_frozen=False))
        for i in l:
            v = i["value"]
            i["value"] = float(v) / float(COIN) if v is not None else None
        return l

    @command('n')
    def getaddressunspent(self, address):
        """Returns the UTXO list of any address. Note: This
        is a walletless server query, results are not checked by SPV.
        """
        return self.network.synchronous_get(('blockchain.address.listunspent', [address]))

    @command('n')
    def getutxoaddress(self, txid, pos):
        """Get the address of a UTXO. Note: This is a walletless server query, results are
        not checked by SPV.
        """
        r = self.network.synchronous_get(('blockchain.utxo.get_address', [txid, pos]))
        return {'address': r}

    @command('wp')
    def createrawtx(self, inputs, outputs, user, unsigned=False):
        """Create a transaction from json inputs. The syntax is similar to bitcoind."""
        coins = self.wallets[user].get_spendable_coins(exclude_frozen=False)
        tx_inputs = []
        for i in inputs:
            prevout_hash = i['txid']
            prevout_n = i['vout']
            for c in coins:
                if c['prevout_hash'] == prevout_hash and c['prevout_n'] == prevout_n:
                    self.wallets[user].add_input_info(c)
                    tx_inputs.append(c)
                    break
            else:
                raise BaseException('Transaction output not in wallet',
                                    prevout_hash + ":%d" % prevout_n)
        outputs = map(lambda x: (TYPE_ADDRESS, x[0], int(COIN * x[1])), outputs.items())
        tx = Transaction.from_io(tx_inputs, outputs)
        if not unsigned:
            self.wallets[user].sign_transaction(tx, self._password)
        return tx.as_dict()

    @command('wp')
    def signtransaction(self, tx, user, privkey=None):
        """Sign a transaction. The wallet keys will be used unless a private key is provided."""
        t = Transaction(tx)
        if privkey:
            pubkey = public_key_from_private_key(privkey)
            t.sign({pubkey: privkey})
        else:
            self.wallets[user].sign_transaction(t, self._password)
        return t.as_dict()

    @command('')
    def deserialize(self, tx):
        """Deserialize a serialized transaction"""
        return Transaction(tx).deserialize()

    @command('n')
    def broadcast(self, tx):
        """Broadcast a transaction to the network. """
        t = Transaction(tx)
        return self.network.synchronous_get(('blockchain.transaction.broadcast', [str(t)]))

    @command('')
    def createmultisig(self, num, pubkeys):
        """Create multisig address"""
        assert isinstance(pubkeys, list), (type(num), type(pubkeys))
        redeem_script = Transaction.multisig_script(pubkeys, num)
        address = hash_160_to_bc_address(hash_160(redeem_script.decode('hex')), 5)
        return {'address': address, 'redeemScript': redeem_script}

    @command('w')
    def freeze(self, address, user):
        """Freeze address. Freeze the funds at one of your wallet\'s addresses"""
        return self.wallets[user].set_frozen_state([address], True)

    @command('w')
    def unfreeze(self, address, user):
        """Unfreeze address. Unfreeze the funds at one of your wallet\'s address"""
        return self.wallets[user].set_frozen_state([address], False)

    @command('wp')
    def getprivatekeys(self, address, user):
        """
        Get private keys of addresses. You may pass a single wallet address,
        or a list of wallet addresses.
        """

        is_list = type(address) is list
        domain = address if is_list else [address]
        out = [self.wallets[user].get_private_key(address, self._password) for address in domain]
        return out if is_list else out[0]

    @command('w')
    def ismine(self, address, user):
        """Check if address is in wallet. Return true if and only address is in wallet"""
        return self.wallets[user].is_mine(address)

    @command('')
    def dumpprivkeys(self):
        """Deprecated."""
        return "This command is deprecated. Use a pipe instead: " \
               "'uwallet listaddresses | uwallet getprivatekeys - '"

    @command('')
    def validateaddress(self, address):
        """Check that an address is valid. """
        return is_address(address)

    @command('w')
    def getpubkeys(self, address, user):
        """Return the public keys for a wallet address. """
        return self.wallets[user].get_public_keys(address)

    @command('w')
    def getbalance(self, user, password, account=None, exclude_claimtrietx=False):
        """Return the balance of your wallet. """
        self.set_password_for_rpc(password)
        if account is None:
            c, u, x = self.wallets[user].get_balance(exclude_claimtrietx=exclude_claimtrietx)
        else:
            c, u, x = self.wallets[user].get_account_balance(account, exclude_claimtrietx)
        out = {"confirmed": str(Decimal(c) / COIN)}
        total = (Decimal(c) / COIN)
        if u:
            out["unconfirmed"] = str(Decimal(u) / COIN)
            total = total + (Decimal(u) / COIN)
        if x:
            out["unmatured"] = str(Decimal(x) / COIN)
            total = total + (Decimal(x) / COIN)
        out['success'] = True
        out['total'] = str(total)
        return out

    @command('n')
    def getaddressbalance(self, address):
        """Return the balance of any address. Note: This is a walletless
        server query, results are not checked by SPV.
        """
        out = self.network.synchronous_get(('blockchain.address.get_balance', [address]))
        out["confirmed"] = str(Decimal(out["confirmed"]) / COIN)
        out["unconfirmed"] = str(Decimal(out["unconfirmed"]) / COIN)
        return out

    @command('n')
    def getproof(self, address):
        """Get Merkle branch of an address in the UTXO set"""
        p = self.network.synchronous_get(('blockchain.address.get_proof', [address]))
        out = []
        for i, s in p:
            out.append(i)
        return out

    @command('n')
    def getmerkle(self, txid, height):
        """Get Merkle branch of a transaction included in a block. Electrum
        uses this to verify transactions (Simple Payment Verification)."""
        return self.network.synchronous_get(
            ('blockchain.transaction.get_merkle', [txid, int(height)]))

    @command('n')
    def getservers(self):
        """Return the list of available servers"""
        while not self.network.is_up_to_date():
            time.sleep(0.1)
        return self.network.get_servers()

    @command('')
    def version(self):
        """Return the version of uwallet."""
        return __version__

    @command('w')
    def getmpk(self, user):
        """Get master public key. Return your wallet\'s master public key(s)"""
        return self.wallets[user].get_master_public_keys()

    @command('wp')
    def getmasterprivate(self, user):
        """Get master private key. Return your wallet\'s master private key"""
        return str(self.wallets[user].get_master_private_key(self.wallets[user].root_name, self._password))

    @command('wp')
    def getseed(self, user):
        """Get seed phrase. Print the generation seed of your wallet."""
        s = self.wallets[user].get_mnemonic(self._password)
        return s.encode('utf8')

    @command('wp')
    def importprivkey(self, privkey, user):
        """Import a private key. """
        try:
            addr = self.wallets[user].import_key(privkey, self._password)
            out = "Keypair imported: " + addr
        except Exception as e:
            out = "Error: " + str(e)
        return out

    def _resolver(self, x):
        if x is None:
            return None
        out = self.contacts.resolve(x)
        if out.get('type') == 'openalias' and self.nocheck is False and out.get(
                'validated') is False:
            raise BaseException('cannot verify alias', x)
        return out['address']

    @command('n')
    def sweep(self, privkey, destination, tx_fee=None, nocheck=False):
        """Sweep private keys. Returns a transaction that spends UTXOs from
        privkey to a destination address. The transaction is not
        broadcasted."""
        privkeys = privkey if type(privkey) is list else [privkey]
        self.nocheck = nocheck
        dest = self._resolver(destination)
        if tx_fee is None:
            tx_fee = 0.0001
        fee = int(Decimal(tx_fee) * COIN)
        return Transaction.sweep(privkeys, self.network, dest, fee)

    @command('wp')
    def signmessage(self, address, message, user):
        """Sign a message with a key. Use quotes if your message contains
        whitespaces"""
        sig = self.wallets[user].sign_message(address, message, self._password)
        return base64.b64encode(sig)

    @command('')
    def verifymessage(self, address, signature, message):
        """Verify a signature."""
        sig = base64.b64decode(signature)
        return verify_message(address, sig, message)

    def _mktx(self, outputs, fee, change_addr, domain, nocheck, unsigned, user, claim_name=None,
              claim_val=None,
              abandon_txid=None, claim_id=None):
        self.nocheck = nocheck
        change_addr = self._resolver(change_addr)
        domain = None if domain is None else map(self._resolver, domain)
        fee = None if fee is None else int(COIN * Decimal(fee))
        final_outputs = []
        wallet = self.wallets[user]
        for address, amount in outputs:
            address = self._resolver(address)
            # assert self.wallet.is_mine(address)
            if amount == '!':
                assert len(outputs) == 1
                inputs = wallet.get_spendable_coins(domain)
                amount = sum(map(lambda x: x['value'], inputs))
                if fee is None:
                    for i in inputs:
                        wallet.add_input_info(i)
                    output = (TYPE_ADDRESS, address, amount)
                    dummy_tx = Transaction.from_io(inputs, [output])
                    fee_per_kb = wallet.fee_per_kb(self.config)
                    fee = dummy_tx.estimated_fee(wallet.relayfee(), fee_per_kb)
                amount -= fee
            else:
                amount = int(COIN * Decimal(amount))
            txout_type = TYPE_ADDRESS
            val = address
            if claim_name is not None and claim_val is not None and claim_id is not None \
                    and abandon_txid is not None:
                assert len(outputs) == 1
                txout_type |= TYPE_UPDATE
                val = ((claim_name, claim_id, claim_val), val)
            elif claim_name is not None and claim_id is not None:
                assert len(outputs) == 1
                txout_type |= TYPE_SUPPORT
                val = ((claim_name, claim_id), val)
            elif claim_name is not None and claim_val is not None:
                assert len(outputs) == 1
                txout_type |= TYPE_CLAIM
                val = ((claim_name, claim_val), val)
            final_outputs.append((txout_type, val, amount))

        coins = wallet.get_spendable_coins(domain, abandon_txid=abandon_txid)
        tx = wallet.make_unsigned_transaction(coins, final_outputs, self.config, fee,
                                              change_addr,
                                              abandon_txid=abandon_txid)
        str(tx)  # this serializes
        if not unsigned:
            wallet.sign_transaction(tx, self._password)

        return tx

    @command('wp')
    def getunusedaddress(self, user, account=None):
        addr = self.wallets[user].get_unused_address(account)
        if addr is None:
            addr = self.wallets[user].create_new_address()
        return addr

    @command('wp')
    def getleastusedchangeaddress(self, user, account=None):
        return self.wallets[user].get_least_used_address(account, for_change=True)

    @command('wp')
    def getleastusedaddress(self, user, account=None):
        return self.wallets[user].get_least_used_address(account)

    @command('wp')
    def payto(self, destination, amount, user, tx_fee=None, from_addr=None, change_addr=None,
              nocheck=False, unsigned=False):
        """Create a raw transaction. """
        domain = [from_addr] if from_addr else None
        tx = self._mktx([(destination, amount)], tx_fee, change_addr, domain, nocheck, unsigned, user)
        return tx.as_dict()

    @command('wpn')
    def paytoandsend(self, destination, amount, user, tx_fee=None, from_addr=None, change_addr=None,
                     nocheck=False, unsigned=False):
        """Create and broadcast transaction. """
        domain = [from_addr] if from_addr else None
        tx = self._mktx([(destination, amount)], tx_fee, change_addr, domain, nocheck, unsigned, user)
        return self.network.synchronous_get(('blockchain.transaction.broadcast', [str(tx)]))

    @command('w')
    def waitfortxinwallet(self, txid, user, timeout=30):
        """
        wait for tx with txid to appear in wallet for timeout seconds
        return True, if txid appears within timeout, return False otherwise
        """
        start_time = time.time()
        while start_time - time.time() < timeout:
            if txid in self.wallets[user].transactions:
                return True
            time.sleep(0.2)
        return False

    @command('wpn')
    def sendclaimtoaddress(self, claim_id, destination, amount, user, tx_fee=None, change_addr=None,
                           broadcast=True, skip_validate_schema=None):
        claims = self.getnameclaims(user, raw=True, include_supports=False, claim_id=claim_id,
                                    skip_validate_signatures=True)
        if len(claims) > 1:
            return {"success": False, 'reason': 'more than one claim that matches'}
        elif len(claims) == 0:
            return {"success": False, 'reason': 'claim not found', 'claim_id': claim_id}
        else:
            claim = claims[0]

        txid = claim['txid']
        nout = claim['nout']
        claim_name = claim['name']
        claim_val = claim['value']
        certificate_id = None
        if not skip_validate_schema:
            decoded = smart_decode(claim_val)
            if self.cansignwithcertificate(decoded.certificate_id, user):
                certificate_id = decoded.certificate_id
        return self.update(claim_name, claim_val, amount=amount, certificate_id=certificate_id,
                           claim_id=claim_id, txid=txid, nout=nout, broadcast=broadcast,
                           claim_addr=destination, tx_fee=tx_fee, change_addr=change_addr,
                           raw=False, skip_validate_schema=skip_validate_schema)

    @command('wp')
    def paytomany(self, outputs, user, tx_fee=None, from_addr=None, change_addr=None, nocheck=False,
                  unsigned=False):
        """Create a multi-output transaction. """
        domain = [from_addr] if from_addr else None
        tx = self._mktx(outputs, tx_fee, change_addr, domain, nocheck, unsigned, user)
        return tx.as_dict()

    @command('wp')
    def paytomanyandsend(self, outputs, user, tx_fee=None, from_addr=None, change_addr=None,
                         nocheck=False, unsigned=False):
        """Create and broadcast a multi-output transaction. """
        domain = [from_addr] if from_addr else None
        tx = self._mktx(outputs, tx_fee, change_addr, domain, nocheck, unsigned, user)
        return self.network.synchronous_get(('blockchain.transaction.broadcast', [str(tx)]))

    @command('w')
    def claimhistory(self, user):

        claim_amt = dict()

        def get_info_dict(name, claim_id, nout, txo, value, tx_type):
            amount = float(Decimal(txo[2]) / Decimal(COIN))

            if tx_type == "claim":
                amount = -1 * amount
                claim_amt[claim_id] = amount
            elif tx_type == "support" and value < 0:
                amount = -1 * amount
            elif tx_type == "update":
                abs_amount = abs(amount)
                if claim_id in claim_amt:
                    # the previous update or claim is known already
                    abs_prev_amount = abs(claim_amt[claim_id])
                    claim_amt[claim_id] = amount
                    amount = abs_prev_amount - abs_amount
                else:
                    # this is a claim that was sent to us via an update transaction
                    amount = abs_amount
                    claim_amt[claim_id] = abs_amount
            return {
                'claim_name': name,
                'claim_id': claim_id,
                'nout': nout,
                'balance_delta': amount,
                'amount': float(Decimal(txo[2]) / Decimal(COIN)),
                'address': txo[1][1]
            }

        history = self.history()
        results = []

        for history_result in history:
            txid = history_result['txid']
            tx = self.wallets[user].transactions[txid]
            tx_outs = tx.outputs()

            support_infos = []
            update_infos = []
            claim_infos = []

            for nout, tx_out in enumerate(tx_outs):
                if tx_out[0] & TYPE_SUPPORT:
                    claim_name, claim_id = tx_out[1][0]
                    claim_id = encode_claim_id_hex(claim_id)
                    support_infos.append(get_info_dict(claim_name, claim_id, nout, tx_out,
                                                       history_result['value'], tx_type="support"))
                elif tx_out[0] & TYPE_UPDATE:
                    claim_name, claim_id, claim_value = tx_out[1][0]
                    claim_id = encode_claim_id_hex(claim_id)
                    update_infos.append(get_info_dict(claim_name, claim_id, nout, tx_out,
                                                      history_result['value'], tx_type="update"))
                elif tx_out[0] & TYPE_CLAIM:
                    claim_name, claim_value = tx_out[1][0]
                    claim_id = claim_id_hash(rev_hex(tx.hash()).decode('hex'), nout)
                    claim_id = encode_claim_id_hex(claim_id)
                    claim_infos.append(get_info_dict(claim_name, claim_id, nout, tx_out,
                                                     history_result['value'], tx_type="claim"))

            result = history_result
            result['support_info'] = support_infos
            result['update_info'] = update_infos
            result['claim_info'] = claim_infos
            results.append(result)
        return results

    @command('wn')
    def tiphistory(self, user):
        claim_ids_to_check = []
        results = []

        claim_history = self.claimhistory(user)
        for h in claim_history:
            for supported in h['support_info']:
                claim_ids_to_check.append(supported['claim_id'])

        claims = self.getclaimsbyids(claim_ids_to_check)

        for h in claim_history:
            support_info = []
            for supported in h['support_info']:
                claim_ids_to_check.append(supported['claim_id'])
                claim = claims.get(supported['claim_id'])
                if claim:
                    if supported['address'] == claim['address']:
                        supported['is_tip'] = True
                    else:
                        supported['is_tip'] = False
                    support_info.append(supported)
            h['support_info'] = support_info
            results.append(h)

        return results

    @command('w')
    def transactionfee(self, txid, user):
        """
        Get the fee for a transaction by txid
        """
        wallet = self.wallets[user]
        if txid in wallet.transactions:
            tx = wallet.transactions[txid]
        else:
            return {'error': 'transaction is not in local history'}
        fee = 0

        for tx_in in tx.inputs():
            # add up the amounts for the txos used as inputs
            if tx_in['prevout_hash'] in wallet.transactions:
                spent_tx = wallet.transactions[tx_in['prevout_hash']]
                fee += spent_tx.outputs()[tx_in['prevout_n']][2]
            else:
                # TODO: look up and save the transaction
                return float(Decimal(tx.fee_for_size(wallet.relayfee(),
                                                     wallet.fee_per_kb(self.config),
                                                     tx.estimated_size())) / Decimal(COIN))
        return float(Decimal(fee - tx.output_value()) / Decimal(COIN))

    @command('w')
    def history(self, user):
        """Wallet history. Returns the transaction history of your wallet."""

        out = []
        for tx_hash, confirms, value, timestamp, balance in self.wallets[user].get_history():
            try:
                time_str = datetime.datetime.fromtimestamp(timestamp).isoformat(' ')[:-3]
            except Exception:
                time_str = "----"

            result = {
                'txid': tx_hash,
                'fee': self.transactionfee(tx_hash, user),
                'timestamp': timestamp,
                'date': "%16s" % time_str,
                'value': float(value) / float(COIN) if value is not None else None,
                'confirmations': confirms
            }
            out.append(result)
        return out

    @command('w')
    def setlabel(self, key, label, user):
        """Assign a label to an item. Item may be a bitcoin address or a
        transaction ID"""
        self.wallets[user].set_label(key, label)

    @command('')
    def listcontacts(self):
        """Show your list of contacts"""
        return self.contacts

    @command('')
    def getalias(self, key):
        """Retrieve alias. Lookup in your list of contacts, and for an OpenAlias DNS record."""
        return self.contacts.resolve(key)

    @command('')
    def searchcontacts(self, query):
        """Search through contacts, return matching entries. """
        results = {}
        for key, value in self.contacts.items():
            if query.lower() in key.lower():
                results[key] = value
        return results

    @command('w')
    def listaddresses(self, user, receiving=False, change=False, show_labels=False, frozen=False,
                      unused=False, funded=False, show_balance=False):
        """
        List wallet addresses. Returns the list of all addresses in your wallet.
        Use optional arguments to filter the results.
        """
        wallet = self.wallets[user]
        out = []
        for addr in wallet.addresses(True):
            if frozen and not wallet.is_frozen(addr):
                continue
            if receiving and wallet.is_change(addr):
                continue
            if change and not wallet.is_change(addr):
                continue
            if unused and wallet.is_used(addr):
                continue
            if funded and wallet.is_empty(addr):
                continue
            item = addr
            if show_balance:
                item += ", " + format_satoshis(sum(wallet.get_addr_balance(addr)))
            if show_labels:
                item += ', ' + repr(wallet.labels.get(addr, ''))
            out.append(item)
        return out

    @command('w')
    def gettransaction(self, txid, user):
        """Retrieve a transaction in deserialized json format"""
        tx = self.wallets[user].transactions.get(txid) if self.wallets[user] else None
        if tx is None and self.network:
            raw = self.network.synchronous_get(('blockchain.transaction.get', [txid]))
            if raw:
                tx = Transaction(raw)
            else:
                raise BaseException("Unknown transaction")
        return deserialize_transaction(str(tx))

    @command('')
    def encrypt(self, pubkey, message):
        """Encrypt a message with a public key. Use quotes if the message contains whitespaces."""
        return encrypt_message(message, pubkey)

    @command('wp')
    def decrypt(self, pubkey, encrypted, user):
        """Decrypt a message encrypted with a public key."""
        return self.wallets[user].decrypt_message(pubkey, encrypted, self._password)

    @command('n')
    def notify(self, address, URL):
        """Watch an address. Everytime the address changes, a http POST is sent to the URL."""

        def callback(x):
            import urllib2
            headers = {'content-type': 'application/json'}
            data = {'address': address, 'status': x.get('result')}
            try:
                req = urllib2.Request(URL, json.dumps(data), headers)
                response_stream = urllib2.urlopen(req)
                log.info('Got Response for %s' % address)
            except BaseException as e:
                log.error(str(e))

        self.network.send([('blockchain.address.subscribe', [address])], callback)
        return True

    def validate_claim_signature_and_get_channel_name(self, claim, certificate_claim,
                                                      claim_address, decoded_certificate=None):
        if not certificate_claim:
            return False, None
        certificate = decoded_certificate or smart_decode(certificate_claim['value'])
        if not isinstance(certificate, ClaimDict):
            raise TypeError("Certificate is not a ClaimDict: %s" % str(type(certificate)))
        if Commands._validate_signed_claim(claim, claim_address, certificate):
            return True, certificate_claim['name']
        return False, None

    def parse_and_validate_claim_result(self, claim_result, certificate=None, raw=False):
        if not claim_result or 'value' not in claim_result:
            return claim_result

        claim_result['decoded_claim'] = False
        decoded = None

        if not raw:
            claim_value = claim_result['value']
            try:

                # Because I did a base64 encoding when I wrote the transaction --JustinQP
                # claim_value_decoded = base64.b64decode(claim_value.decode('hex'))
                # decoded = smart_decode(claim_value_decoded.encode('hex'))
                decoded = smart_decode(claim_value)
                claim_result['value'] = decoded.claim_dict
                claim_result['decoded_claim'] = True
            except DecodeError:
                pass

        if decoded:
            claim_result['has_signature'] = False
            if decoded.has_signature:
                if certificate is None:
                    log.info("fetching certificate to check claim signature")
                    certificate = self.getclaimbyid(decoded.certificate_id)
                    if not certificate:
                        log.warning('Certificate %s not found', decoded.certificate_id)
                claim_result['has_signature'] = True
                claim_result['signature_is_valid'] = False
                validated, channel_name = self.validate_claim_signature_and_get_channel_name(
                    decoded, certificate, claim_result['address'])
                claim_result['channel_name'] = channel_name
                if validated:
                    claim_result['signature_is_valid'] = True

        if 'height' in claim_result and claim_result['height'] is None:
            claim_result['height'] = -1

        if 'amount' in claim_result and not isinstance(claim_result['amount'], float):
            claim_result = format_amount_value(claim_result)

        return claim_result

    def offline_parse_and_validate_claim_result(self, claim_result, certificate, raw=False,
                                                decoded_claim=None, decoded_certificate=None,
                                                skip_validate_signatures=False):
        """
        Parse and validate a claim result from uwallet server

        Unlike  parse_and_validate_claim_result, this function does not
        send any queries to uwallet server. In the other function this
        is done to retrieve the certificate claim referenced by the signed
        claim.

        :param claim_result: a claim result from uwallet server
        :param certificate: a certificate result from uwallet server | None
        :param raw: bool
        :param decoded_claim: ClaimDict obtained from claim value
        :param decoded_certificate: ClaimDict
        :param skip_validate_signatures: bool, claim signature validation is not necessary for many
        local operations and adds significant overhead to the call, ie 2200 claims can be parsed
        in under 6 seconds without signature validation and just over a minute with it
        :return: formatted claim result
        """

        # TODO: remove the old parse_and_validate_claim_result function

        if not claim_result or 'value' not in claim_result:
            return claim_result

        claim_result['decoded_claim'] = False
        decoded = None

        if decoded_claim:
            if not isinstance(decoded_claim, ClaimDict):
                raise TypeError("Not given a ClaimDict: %s" % str(type(decoded_claim)))
        if decoded_certificate:
            if not isinstance(decoded_certificate, ClaimDict):
                raise TypeError("Not given a ClaimDict: %s" % str(type(decoded_certificate)))

        if not raw:
            claim_value = claim_result['value']
            if decoded_claim:
                decoded = decoded_claim
                claim_result['value'] = decoded.claim_dict
                claim_result['decoded_claim'] = True
            else:
                try:
                    decoded = smart_decode(claim_value)
                    claim_result['value'] = decoded.claim_dict
                    claim_result['decoded_claim'] = True
                except DecodeError:
                    pass

        if decoded:
            claim_result['has_signature'] = False
            if decoded.has_signature:
                claim_result['has_signature'] = True
                claim_result['signature_is_valid'] = False
                if not skip_validate_signatures:
                    validated, channel_name = self.validate_claim_signature_and_get_channel_name(
                        decoded, certificate, claim_result['address'])
                    claim_result['channel_name'] = channel_name
                    if validated:
                        claim_result['signature_is_valid'] = True
                else:
                    claim_result['signature_is_valid'] = None

        if 'height' in claim_result and claim_result['height'] is None:
            claim_result['height'] = -1

        if 'amount' in claim_result and not isinstance(claim_result['amount'], float):
            claim_result = format_amount_value(claim_result)

        return claim_result

    @staticmethod
    def _validate_signed_claim(claim, claim_address, certificate):
        if not claim.has_signature:
            raise Exception("Claim is not signed")
        if not base_decode(claim_address, ADDRESS_LENGTH, 58):
            raise Exception("Not given a valid claim address")
        try:
            if claim.validate_signature(claim_address, certificate.protobuf):
                return True
        except BadSignatureError:
            # print_msg("Signature for %s is invalid" % claim_id)
            return False
        except Exception as err:
            log.error("Signature for %s is invalid, reason: %s - %s", claim_address,
                      str(type(err)), err)
            return False
        return False

    @staticmethod
    def _verify_proof(name, claim_trie_root, result, height, depth):
        """
        Verify proof for name claim
        """

        def _build_response(name, value, claim_id, txid, n, amount, effective_amount,
                            claim_sequence, claim_address, supports):
            r = {
                'name': name,
                'value': value.encode('hex'),
                'claim_id': claim_id,
                'txid': txid,
                'nout': n,
                'amount': amount,
                'effective_amount': effective_amount,
                'height': height,
                'depth': depth,
                'claim_sequence': claim_sequence,
                'address': claim_address,
                'supports': supports
            }
            return r

        def _parse_proof_result(name, result):
            support_amount = sum([amt for (stxid, snout, amt) in result['supports']])
            supports = result['supports']
            if 'txhash' in result['proof'] and 'nOut' in result['proof']:
                if 'transaction' in result:
                    computed_txhash = Hash(result['transaction'].decode('hex'))[::-1].encode('hex')
                    tx = deserialize_transaction(result['transaction'])
                    nOut = result['proof']['nOut']
                    if result['proof']['txhash'] == computed_txhash:
                        if 0 <= nOut < len(tx['outputs']):
                            scriptPubKey = tx['outputs'][nOut]['scriptPubKey']
                            amount = tx['outputs'][nOut]['value']
                            effective_amount = amount + support_amount
                            decoded_script = [r for r in script_GetOp(scriptPubKey.decode('hex'))]
                            decode_out = decode_claim_script(decoded_script)
                            decode_address = get_address_from_output_script(
                                scriptPubKey.decode('hex'))
                            claim_address = decode_address[1][1]
                            claim_id = result['claim_id']
                            claim_sequence = result['claim_sequence']
                            if decode_out is False:
                                return {'error': 'failed to decode as claim script'}
                            n, script = decode_out
                            decoded_name, decoded_value = n.name, n.value
                            if decoded_name == name:
                                return _build_response(name, decoded_value, claim_id,
                                                       computed_txhash, nOut, amount,
                                                       effective_amount, claim_sequence,
                                                       claim_address, supports)
                            return {'error': 'name in proof did not match requested name'}
                        outputs = len(tx['outputs'])
                        return {'error': 'invalid nOut: %d (let(outputs): %d' % (nOut, outputs)}
                    return {'error': "computed txid did not match given transaction: %s vs %s" %
                                     (computed_txhash, result['proof']['txhash'])
                            }
                return {'error': "didn't receive a transaction with the proof"}
            return {'error': 'name is not claimed'}

        if 'proof' in result:
            try:
                verify_proof(result['proof'], claim_trie_root, name)
            except InvalidProofError:
                return {'error': "Proof was invalid"}
            return _parse_proof_result(name, result)
        else:
            return {'error': "proof not in result"}

    @command('n')
    def requestvalueforname(self, name, blockhash):
        """
        Request and return value of name with proof from uwallet server without verifying proof
        """

        if not name:
            return {'error': 'no name to request'}
        log.info('Requesting value for name: %s, blockhash: %s', name, blockhash)
        return self.network.synchronous_get(('blockchain.claimtrie.getvalue', [name, blockhash]))

    @command('n')
    def getvalueforname(self, name, raw=False):
        """
        Request value of name from uwallet server and verify its proof
        """

        height = self.network.get_local_height() - RECOMMENDED_CLAIMTRIE_HASH_CONFIRMS + 1
        block_header = self.network.blockchain.read_header(height)
        block_hash = self.network.blockchain.hash_header(block_header)
        response = self.requestvalueforname(name, block_hash)
        height, depth = None, None
        if response and 'height' in response:
            height = response['height']
            depth = self.network.get_server_height() - height
        result = Commands._verify_proof(name, block_header['claim_trie_root'], response,
                                        height, depth)
        return self.parse_and_validate_claim_result(result, raw=raw)

    @command('n')
    def getclaimbynameinchannel(self, uri, name, raw=False):
        """
        Get claim by name within a channel by uri
        """

        channel_claims = self.getclaimsinchannel(uri, raw)
        if 'error' in channel_claims:
            return channel_claims
        for claim in channel_claims:
            if claim['name'] == name:
                return claim
        return {'success': False, 'error': 'claim not found', 'name': name}

    @command('wn')
    def getdefaultcertificate(self, user):
        """
        Get the claim id of the default certificate used for claim signing, if there is one
        """

        certificate_id = self.wallets[user].default_certificate_claim
        if not certificate_id:
            return {'error': 'no default certificate configured'}
        return self.getclaimbyid(certificate_id)

    @staticmethod
    def prepare_claim_queries(start_position, query_size, channel_claim_infos):
        queries = [tuple()]
        names = {}
        # a table of index counts for the sorted claim ids, including ignored claims
        absolute_position_index = {}

        block_sorted_infos = sorted(channel_claim_infos.iteritems(), key=lambda x: int(x[1][1]))
        per_block_infos = {}
        for claim_id, (name, height) in block_sorted_infos:
            claims = per_block_infos.get(height, [])
            claims.append((claim_id, name))
            per_block_infos[height] = sorted(claims, key=lambda x: int(x[0], 16))

        abs_position = 0

        for height in sorted(per_block_infos.keys(), reverse=True):
            for claim_id, name in per_block_infos[height]:
                names[claim_id] = name
                absolute_position_index[claim_id] = abs_position
                if abs_position >= start_position:
                    if len(queries[-1]) >= query_size:
                        queries.append(tuple())
                    queries[-1] += (claim_id,)
                abs_position += 1
        return queries, names, absolute_position_index

    def iter_channel_claims_pages(self, queries, claim_positions, claim_names, certificate,
                                  page_size=10):
        # uwallet server returns a dict of {claim_id: (name, claim_height)}
        # first, sort the claims by block height (and by claim id int value within a block).

        # map the sorted claims into getclaimsbyids queries of query_size claim ids each

        # send the batched queries to uwallet server and iteratively validate and parse
        # the results, yield a page of results at a time.

        # these results can include those where `signature_is_valid` is False. if they are skipped,
        # page indexing becomes tricky, as the number of results isn't known until after having
        # processed them.
        # TODO: fix ^ in unetschema

        def iter_validate_channel_claims():
            for claim_ids in queries:
                batch_result = self.network.synchronous_get(
                    ("blockchain.claimtrie.getclaimsbyids", claim_ids))
                for claim_id in claim_ids:
                    claim = batch_result[claim_id]
                    if claim['name'] == claim_names[claim_id]:
                        formatted_claim = self.parse_and_validate_claim_result(claim, certificate)
                        formatted_claim['absolute_channel_position'] = claim_positions[
                            claim['claim_id']]
                        yield formatted_claim
                    else:
                        log.warning("ignoring claim with name mismatch %s %s", claim['name'],
                                    claim['claim_id'])

        yielded_page = False
        results = []
        for claim in iter_validate_channel_claims():
            results.append(claim)

            # if there is a full page of results, yield it
            if len(results) and len(results) % page_size == 0:
                yield results[-page_size:]
                yielded_page = True

        # if we didn't get a full page of results, yield what results we did get
        if not yielded_page:
            yield results

    def get_channel_claims_page(self, channel_claim_infos, certificate, page, page_size=10):
        page = page or 0
        page_size = max(page_size, 1)
        if page_size > MAX_PAGE_SIZE:
            raise Exception("page size above maximum allowed")
        start_position = (page - 1) * page_size
        queries, names, claim_positions = self.prepare_claim_queries(start_position, page_size,
                                                                     channel_claim_infos)
        page_generator = self.iter_channel_claims_pages(queries, claim_positions, names,
                                                        certificate, page_size=page_size)
        upper_bound = len(claim_positions)
        if not page:
            return None, upper_bound
        if start_position > upper_bound:
            raise IndexError("claim %i greater than max %i" % (start_position, upper_bound))
        return next(page_generator), upper_bound

    def _handle_resolve_uri_response(self, parsed_uri, block_header, raw, resolution, page=0,
                                     page_size=10):
        result = {}
        # parse an included certificate
        if 'certificate' in resolution:
            certificate_response = resolution['certificate']['result']
            certificate_resolution_type = resolution['certificate']['resolution_type']
            if certificate_resolution_type == "winning" and certificate_response:
                if 'height' in certificate_response:
                    height = certificate_response['height']
                    depth = self.network.get_server_height() - height
                    certificate_result = Commands._verify_proof(parsed_uri.name,
                                                                block_header['claim_trie_root'],
                                                                certificate_response,
                                                                height, depth)
                    result['certificate'] = self.parse_and_validate_claim_result(certificate_result,
                                                                                 raw=raw)
            elif certificate_resolution_type == "claim_id":
                result['certificate'] = self.parse_and_validate_claim_result(certificate_response,
                                                                             raw=raw)
            elif certificate_resolution_type == "sequence":
                result['certificate'] = self.parse_and_validate_claim_result(certificate_response,
                                                                             raw=raw)
            else:
                log.error("unknown response type: %s", certificate_resolution_type)

            if 'certificate' in result:
                certificate = result['certificate']
                if 'unverified_claims_in_channel' in resolution:
                    max_results = len(resolution['unverified_claims_in_channel'])
                    result['claims_in_channel'] = max_results
                else:
                    result['claims_in_channel'] = 0
            else:
                result['error'] = "claim not found"
                result['success'] = False
                result['uri'] = str(parsed_uri)

        else:
            certificate = None

        # if this was a resolution for a name, parse the result
        if 'claim' in resolution:
            claim_response = resolution['claim']['result']
            claim_resolution_type = resolution['claim']['resolution_type']
            if claim_resolution_type == "winning" and claim_response:
                if 'height' in claim_response:
                    height = claim_response['height']
                    depth = self.network.get_server_height() - height
                    claim_result = Commands._verify_proof(parsed_uri.name,
                                                          block_header['claim_trie_root'],
                                                          claim_response,
                                                          height, depth)
                    result['claim'] = self.parse_and_validate_claim_result(claim_result,
                                                                           certificate,
                                                                           raw)
            elif claim_resolution_type == "claim_id":
                result['claim'] = self.parse_and_validate_claim_result(claim_response,
                                                                       certificate,
                                                                       raw)
            elif claim_resolution_type == "sequence":
                result['claim'] = self.parse_and_validate_claim_result(claim_response,
                                                                       certificate,
                                                                       raw)
            else:
                log.error("unknown response type: %s", claim_resolution_type)

        # if this was a resolution for a name in a channel make sure there is only one valid
        # match
        elif 'unverified_claims_for_name' in resolution and 'certificate' in result:
            unverified_claims_for_name = resolution['unverified_claims_for_name']

            channel_info = self.get_channel_claims_page(unverified_claims_for_name,
                                                        result['certificate'], page=1)
            claims_in_channel, upper_bound = channel_info

            if len(claims_in_channel) > 1:
                log.error("Multiple signed claims for the same name")
            elif not claims_in_channel:
                log.error("No valid claims for this name for this channel")
            else:
                result['claim'] = claims_in_channel[0]

        # parse and validate claims in a channel iteratively into pages of results
        elif 'unverified_claims_in_channel' in resolution and 'certificate' in result:
            ids_to_check = resolution['unverified_claims_in_channel']
            channel_info = self.get_channel_claims_page(ids_to_check, result['certificate'],
                                                        page=page, page_size=page_size)
            claims_in_channel, upper_bound = channel_info

            if claims_in_channel:
                result['claims_in_channel'] = claims_in_channel
        elif 'error' not in result:
            result['error'] = "claim not found"
            result['success'] = False
            result['uri'] = str(parsed_uri)

        return result

    @command('n')
    def getvalueforuri(self, uri, raw=False, page=0, page_size=10):
        """
        Resolve a ULD URI
        """

        result = self.getvaluesforuris(raw, page, page_size, uri)
        if uri in result:
            return result[uri]
        return result

    @command('n')
    def getvaluesforuris(self, raw=False, page=0, page_size=10, *uris):
        """
        Resolve a ULD URI
        """

        page = int(page)
        page_size = int(page_size)
        uris_to_send = ()

        for uri in uris:
            try:
                parse_unet_uri(uri)
                uris_to_send += (str(uri),)
            except URIParseError as err:
                return {'error': err.message}

        height = self.network.get_local_height() - RECOMMENDED_CLAIMTRIE_HASH_CONFIRMS + 1
        block_header = self.network.blockchain.read_header(height)
        block_hash = self.network.blockchain.hash_header(block_header)
        response = self.network.synchronous_get(('blockchain.claimtrie.getvaluesforuris',
                                                 (block_hash,) + uris_to_send))
        result = {}
        for uri in response:
            result[uri] = self._handle_resolve_uri_response(parse_unet_uri(str(uri)), block_header,
                                                            raw, response[uri], page=page,
                                                            page_size=page_size)
        return result

    @command('n')
    def getsignaturebyid(self, claim_id):
        """
        Get signature information for a claim by its claim id
        """

        response = {'has_signature': False}
        raw_claim = self.getclaimbyid(claim_id, raw=True)

        if raw_claim:
            try:
                decoded_claim = smart_decode(raw_claim['value'].decode('hex'))
                response['decoded_claim'] = True
            except DecodeError:
                response['decoded_claim'] = False
                response['error'] = "Could not decode claim value"
            if response['decoded_claim'] and decoded_claim.has_signature:
                response['has_signature'] = True
                response['signature'] = decoded_claim.signature
                response['certificate'] = self.getclaimbyid(decoded_claim.certificate_id)
                validated, channel_name = self.validate_claim_signature_and_get_channel_name(
                    decoded_claim, response['certificate'], raw_claim['address'])
                response['channel_name'] = channel_name
                if validated:
                    response['signature_is_valid'] = True
                else:
                    response['signature_is_valid'] = False
            else:
                response['has_signature'] = False
        else:
            response['error'] = "claim does not exist"
        return response

    @command('n')
    def getclaimsfromtx(self, txid, raw=False):
        """
        Return the claims which are in a transaction
        """

        result = self.network.synchronous_get(('blockchain.claimtrie.getclaimsintx', [txid]))
        return self.parse_and_validate_claim_result(result, raw=raw)

    @command('n')
    def getclaimbyoutpoint(self, txid, nout, raw=False):
        """
        Return claim at outpoint (txid:nout)
        If no claim exists at outpoint, or outpoint not found, return
        dictionary where 'success' is False and 'error' is 'claim not found'
        """

        claims = self.network.synchronous_get(('blockchain.claimtrie.getclaimsintx', [txid]))
        claim_not_found_out = {'success': False, 'error': 'claim not found',
                               'outpoint': '%s:%i' % (txid, nout)}
        if claims is None:
            return claim_not_found_out
        for claim in claims:
            if claim['nout'] == nout:
                return self.parse_and_validate_claim_result(claim, raw=raw)
        return claim_not_found_out

    @command('n')
    def getclaimsforname(self, name, raw=False):
        """
        Return all claims and supports for a name
        """

        result = self.network.synchronous_get(('blockchain.claimtrie.getclaimsforname', [name]))
        claims_for_return = []
        for claim in result['claims']:
            claims_for_return.append(self.parse_and_validate_claim_result(claim, raw=raw))
        result['claims'] = claims_for_return
        return result

    @command('n')
    def getclaimssignedby(self, claim_id, raw=False):
        """
        Request claims signed by a given certificate
        """

        result = self.network.synchronous_get(('blockchain.claimtrie.getclaimssignedbyid',
                                               [claim_id]))
        return [self.parse_and_validate_claim_result(claim, raw=raw) for claim in result]

    @command('n')
    def getclaimsinchannel(self, uri, raw=False):
        """
        Get claims in a channel for a uri
        """

        parsed = parse_unet_uri(uri)
        if not parsed.is_channel:
            return {'error': 'not a channel uri'}
        elif parsed.claim_sequence is not None:
            claims = self.network.synchronous_get(
                ('blockchain.claimtrie.getclaimssignedbynthtoname',
                 [parsed.name, parsed.claim_sequence]))
        elif parsed.claim_id is not None:
            claims = self.network.synchronous_get(('blockchain.claimtrie.getclaimssignedbyid',
                                                   [parsed.claim_id]))
        else:
            claims = self.network.synchronous_get(('blockchain.claimtrie.getclaimssignedby',
                                                   [parsed.name]))
        if claims:
            return [self.parse_and_validate_claim_result(claim, raw=raw) for claim in claims]
        return []

    @command('n')
    def getblock(self, blockhash):
        """
        Return a block matching the given blockhash
        """

        return self.network.synchronous_get(('blockchain.block.get_block', [blockhash]))

    @command('n')
    def getbestblockhash(self):
        height = self.network.get_local_height()
        if height < 0:
            return None
        header = self.network.blockchain.read_header(height)
        return self.network.blockchain.hash_header(header)

    @command('n')
    def getmostrecentblocktime(self):
        height = self.network.get_local_height()
        if height < 0:
            return None
        header = self.network.get_header(self.network.get_local_height())
        return header['timestamp']

    @command('n')
    def getnetworkstatus(self):
        out = {'is_connecting': self.network.is_connecting(),
               'is_connected': self.network.is_connected(),
               'local_height': self.network.get_local_height(),
               'server_height': self.network.get_server_height(),
               'blocks_behind': self.network.get_blocks_behind(),
               'retrieving_headers': self.network.blockchain.retrieving_headers}
        return out

    @command('n')
    def getclaimtrie(self):
        """
        Return the entire claim trie
        """

        return self.network.synchronous_get(('blockchain.claimtrie.get', []))

    @command('n')
    def getclaimbyid(self, claim_id, raw=False):
        """
        Get claim by claim id
        """

        result = self.network.synchronous_get(('blockchain.claimtrie.getclaimbyid', [claim_id]))
        return self.parse_and_validate_claim_result(result, raw=raw)

    @command('n')
    def getclaimsbyids(self, claim_ids, raw=False):
        """
        Get a dictionary of claim results keyed by claim id for a list of claim ids
        """

        def iter_certificate_ids(claim_results):
            for claim_id, claim_result in claim_results.iteritems():
                if claim_result and 'value' in claim_result:
                    try:
                        decoded = smart_decode(claim_result['value'])
                        if decoded.has_signature:
                            yield claim_id, decoded.certificate_id
                    except DecodeError:
                        pass

        def iter_certificate_claims(certificate_results):
            for claim_id, claim_result in certificate_results.iteritems():
                yield claim_id, self.offline_parse_and_validate_claim_result(claim_result, None,
                                                                             raw)

        def iter_resolve_and_parse(to_query):
            claim_results = self.network.synchronous_get(("blockchain.claimtrie.getclaimsbyids",
                                                          to_query))
            certificate_infos = dict(iter_certificate_ids(claim_results))

            cert_results = self.network.synchronous_get(("blockchain.claimtrie.getclaimsbyids",
                                                         certificate_infos.values()))
            certificates = dict(iter_certificate_claims(cert_results))

            for claim_id, claim_result in claim_results.iteritems():
                if claim_id in certificate_infos:
                    certificate_id = certificate_infos[claim_id]
                    certificate = certificates[certificate_id]
                else:
                    certificate = None
                yield claim_id, self.offline_parse_and_validate_claim_result(claim_result,
                                                                             certificate, raw)

        def iter_queries(remaining):
            while remaining:
                query = remaining[:MAX_BATCH_QUERY_SIZE]
                remaining = remaining[MAX_BATCH_QUERY_SIZE:]
                for claim_id, claim in iter_resolve_and_parse(query):
                    yield claim_id, claim

        return dict(iter_queries(claim_ids))

    @command('n')
    def getnthclaimforname(self, name, n, raw=False):
        """
        Get the last update to the nth claim to a name
        """

        result = self.network.synchronous_get(('blockchain.claimtrie.getnthclaimforname',
                                               [name, n]))
        return self.parse_and_validate_claim_result(result, raw=raw)

    @command('wn')
    def getnameclaims(self, user, raw=False, include_abandoned=False, include_supports=True,
                      txid=None, nout=None, claim_id=None, skip_validate_signatures=False):
        """
        Get my name claims from wallet
        """

        # get the name claims from the wallet
        result = self.wallets[user].get_name_claims(include_abandoned=include_abandoned,
                                                    include_supports=include_supports)
        name_claims = []

        # set of claim ids of claims in the wallet
        claim_ids = {c['claim_id'] for c in result}

        # dictionary of claims (not including supports) in the wallet, keyed by claim id
        claims = {}

        # dictionary of decoded ClaimDict objects, keyed by claim id
        claim_dict_objs = {}

        # list of (<claim_id>, <certificate_id>) tuples, where <certificate_id> is None if
        # the claim is not signed
        # note: the certificate claim is not necessarily in the wallet
        claim_tuples = []

        # list of certificate claim ids not known by the wallet but used for signing
        needed_certificates = []

        # list of support transactions
        supports = []

        for claim in result:
            # if we're looking for a specific claim by its id, skip all other claims
            if claim_id and claim_id != claim['claim_id']:
                continue
            # if we're looking for a specific claim by its outpoint, skip all other claims
            if txid is not None:
                if claim['txid'] != txid:
                    continue
            if nout is not None:
                if claim['nout'] != nout:
                    continue
            # if transaction is a claim or update (supports don't have a `value`)
            if 'value' in claim:
                try:
                    decoded = smart_decode(claim['value'])
                    if not isinstance(decoded, ClaimDict):
                        log.warning("Failed to decode %s to a claim dict, instead got %s",
                                    claim['name'], str(type(decoded)))
                    if not skip_validate_signatures:
                        certificate_id = decoded.certificate_id
                        # if the claim is signed but the certificate id is not for a claim in the
                        # wallet, add it to the list of needed claims
                        if certificate_id and certificate_id not in claim_ids:
                            needed_certificates.append(certificate_id)
                    else:
                        certificate_id = None
                    claim_tuples.append((claim['claim_id'], certificate_id))
                    claim['value'] = decoded
                    claims[claim['claim_id']] = claim
                except DecodeError:
                    claim_tuples.append((claim['claim_id'], None))
                    claims[claim['claim_id']] = claim
            else:
                supports.append(claim)

        # if we're not skipping signature validation, claim_tuples now maps all the claims in the
        # wallet to their certificate claims (if applicable) and any certificate claims not also in
        # the wallet are in the needed_certificates list
        if needed_certificates:
            log.warning("Fetching %i certificate claims for claims made with certificates not in "
                        "this wallet", len(needed_certificates))
            needed_cert_results = self.getclaimsbyids(needed_certificates)
            # put the fetched certificate claims into the claims dictionary for use validating
            # the signatures of the claims in the wallet
            for _claim_id in needed_cert_results:
                claims[_claim_id] = needed_cert_results[_claim_id]
                claims[_claim_id]['value'] = smart_decode(needed_cert_results[_claim_id]['value'])

        # use pre-decoded ClaimDicts rather than decoding them each time we call
        # offline_parse_and_validate_claim_result
        for _claim_id in claims:
            claim_value = claims[_claim_id]['value']
            claim_dict_objs[_claim_id] = claim_value
        # format (and validate, unless skip_validate_signatures) the resulting claims for return
        for _claim_id, certificate_id in claim_tuples:
            if certificate_id and certificate_id in claims:
                certificate = claims[certificate_id]
                certificate_obj = claim_dict_objs[certificate_id]
            else:
                certificate = None
                certificate_obj = None

            claim = claims[_claim_id]
            claim_obj = claim_dict_objs[_claim_id]
            if isinstance(claim_obj, ClaimDict):
                decoded_claim = claim_obj
            else:
                decoded_claim = None

            parsed = self.offline_parse_and_validate_claim_result(
                claim, certificate=certificate, raw=raw, decoded_claim=decoded_claim,
                decoded_certificate=certificate_obj,
                skip_validate_signatures=skip_validate_signatures)
            name_claims.append(parsed)

        # format and add supports to claims for return
        for support in supports:
            parsed = format_amount_value(support)
            name_claims.append(parsed)
        return name_claims

    @command('wp')
    def getcertificateclaims(self, user, raw=False, include_abandoned=False):
        """
        Get my claims containing certificates
        """

        certificate_claims = []
        name_claims = self.wallets[user].get_name_claims(include_abandoned=include_abandoned,
                                                         include_supports=False)
        for claim in name_claims:
            try:
                decoded = smart_decode(claim['value'])
                if decoded.is_certificate:
                    cert_result = self.offline_parse_and_validate_claim_result(
                        claim, None, raw=raw, decoded_claim=decoded, skip_validate_signatures=True)
                    if self.cansignwithcertificate(cert_result['claim_id'], user):
                        cert_result['can_sign'] = True
                    else:
                        cert_result['can_sign'] = False
                    certificate_claims.append(cert_result)
            except DecodeError:
                pass
        return certificate_claims

    @command('wpn')
    def getcertificatesforsigning(self, user, raw=False):
        """
        Get certificate claims that are usable for signing, the claims are not necessarily in the
        wallet
        """

        my_certs = self.getcertificateclaims(user, raw=raw)
        certificate_claim_ids = self.wallets[user].get_certificate_claim_ids_for_signing()
        result = []
        for cert_claim in my_certs:
            cert_claim['is_mine'] = True
            result.append(cert_claim)
            certificate_claim_ids.remove(cert_claim['claim_id'])
        if certificate_claim_ids:
            imported_certs = self.getclaimsbyids(certificate_claim_ids, raw=raw)
            for claim_id, cert_claim in imported_certs.iteritems():
                cert_claim['is_mine'] = False
                result.append(cert_claim)
        return result

    def _calculate_fee(self, inputs, outputs, set_tx_fee, user):
        if set_tx_fee is not None:
            return set_tx_fee
        dummy_tx = Transaction.from_io(inputs, outputs)
        # fee per kb will default to RECOMMENDED_FEE, which is 50000
        # relay fee will default to 5000
        # fee is max(relay_fee, size is fee_per_kb * esimated_size)
        # will be roughly 10,000 deweys (0.0001 UT), standard abandon should be about 200 bytes
        # this is assuming config is not set to dynamic, which in case it will get fees from
        # ulords fee estimation algorithm

        size = dummy_tx.estimated_size()
        fee = Transaction.fee_for_size(self.wallets[user].relayfee(), self.wallets[user].fee_per_kb(self.config),
                                       size)
        return fee

    @command('w')
    def verify_claim_schema(self, val):
        """
        Parse an encoded claim value
        """

        try:
            decoded = smart_decode(val)
            results = {'claim_dictionary': decoded.claim_dict,
                       'serialized': decoded.serialized.encode('hex')}
            return results
        except DecodeError as err:
            return {'error': err}

    @command('wpn')
    def claim(self, name, val, amount, user, certificate_id=None, broadcast=True, claim_addr=None,
              tx_fee=None, change_addr=None, raw=False, skip_validate_schema=None,
              skip_update_check=None):
        """
        Claim a name
        """
        wallet = self.wallets[user]
        if skip_validate_schema and certificate_id:
            return {'success': False, 'reason': 'refusing to sign claim without validated schema'}

        parsed_claim = self.verify_request_to_make_claim(name, val, certificate_id)
        if 'error' in parsed_claim:
            return {'success': False, 'reason': parsed_claim['error']}

        parsed_uri = parse_unet_uri(name)
        name = parsed_claim['name']
        val = parsed_claim['val']
        certificate_id = parsed_claim['certificate_id']

        if not skip_update_check:
            my_claims = [claim
                         for claim in self.getnameclaims(user, include_supports=False,
                                                         skip_validate_signatures=True)
                         if claim['name'] == name]
            if len(my_claims) > 1:
                return {'success': False, 'reason': "Dont know which claim to update"}
            if my_claims:
                my_claim = my_claims[0]

                if parsed_uri.claim_id and not my_claim['claim_id'].startswith(parsed_uri.claim_id):
                    return {'success': False,
                            'reason': 'claim id in URI does not match claim to update'}
                log.info("There is an unspent claim in your wallet for this name, updating "
                         "it instead")
                return self.update(name, val, user, amount=amount,
                                   broadcast=broadcast, claim_addr=claim_addr,
                                   tx_fee=tx_fee, change_addr=change_addr,
                                   certificate_id=certificate_id, raw=raw,
                                   skip_validate_schema=skip_validate_schema)

        # decode claim value as hex
        if not raw:
            val = val.decode('hex')

        # validate claim and change address if either where given, get least used if not provided
        if claim_addr is None:
            claim_addr = wallet.get_least_used_address()
        if not base_decode(claim_addr, ADDRESS_LENGTH, 58):
            return {'error': 'invalid claim address'}

        if change_addr is None:
            change_addr = wallet.get_least_used_address(for_change=True)
        if not base_decode(change_addr, ADDRESS_LENGTH, 58):
            return {'error': 'invalid change address'}

        amount = int(COIN * amount)
        if amount <= 0:
            return {'success': False, 'reason': 'Amount must be greater than 0'}
        if tx_fee is not None:
            tx_fee = int(COIN * tx_fee)
            if tx_fee < 0:
                return {'success': False, 'reason': 'tx_fee must be greater than or equal to 0'}

        claim_value = None

        if not skip_validate_schema:
            try:
                claim_value = smart_decode(val)
            except DecodeError as err:
                return {'success': False,
                        'reason': 'Decode error: %s' % err}

            if not parsed_uri.is_channel and claim_value.is_certificate:
                return {'success': False,
                        'reason': 'Certificates must have URIs beginning with /"@/"'}

            if claim_value.has_signature:
                return {'success': False, 'reason': 'Claim value is already signed'}

            if certificate_id is not None:
                if not self.cansignwithcertificate(certificate_id, user):
                    return {'success': False,
                            'reason': 'Cannot sign for certificate %s' % certificate_id}

        if certificate_id and claim_value:
            signing_key = wallet.get_certificate_signing_key(certificate_id)
            signed = claim_value.sign(signing_key, claim_addr, certificate_id, curve=SECP256k1)
            val = signed.serialized


        outputs = [(TYPE_ADDRESS | TYPE_CLAIM, ((name, val), claim_addr), amount)]
        coins = wallet.get_spendable_coins()
        try:
            tx = wallet.make_unsigned_transaction(coins, outputs,
                                                  self.config, tx_fee, change_addr)
        except NotEnoughFunds:
            return {'success': False,
                    'reason': 'Not enough funds'}
        wallet.sign_transaction(tx, self._password)
        if broadcast:
            success, out = wallet.send_tx(tx)
            if not success:
                return {'success': False, 'reason': out}

        nout = None
        for i, output in enumerate(tx._outputs):
            if output[0] & TYPE_CLAIM:
                nout = i
        assert nout is not None

        claimid = encode_claim_id_hex(claim_id_hash(rev_hex(tx.hash()).decode('hex'), nout))
        return {
            "success": True,
            "txid": tx.hash(),
            "nout": nout,
            "tx": str(tx),
            "fee": str(Decimal(tx.get_fee()) / COIN),
            "claim_id": claimid
        }

    @command('wpn')
    def claimcertificate(self, name, amount, user, broadcast=True, claim_addr=None, tx_fee=None,
                         change_addr=None, set_default_certificate=None):
        """
        Generate a new signing key and make a certificate claim
        """

        if not parse_unet_uri(name).is_channel:
            return {'error': 'non compliant uri for certificate'}

        secp256k1_private_key = get_signer(SECP256k1).generate().private_key.to_pem()
        claim = ClaimDict.generate_certificate(secp256k1_private_key, curve=SECP256k1)
        encoded_claim = claim.serialized.encode('hex')
        result = self.claim(name, encoded_claim, amount, user, broadcast=broadcast,
                            claim_addr=claim_addr, tx_fee=tx_fee, change_addr=change_addr)

        if result['success']:
            self.wallets[user].save_certificate(result['claim_id'], secp256k1_private_key)
            self.wallets[user].set_default_certificate(result['claim_id'],
                                                       overwrite_existing=set_default_certificate)
        return result

    @staticmethod
    def _deserialize_certificate_key(serialized_certificate_info):
        """
        :param serialized_certificate_info:
        :return: certificate claim id hex, pem encoded private key
        """

        cert_info = serialized_certificate_info[:40]
        priv_key_info = serialized_certificate_info[40:]
        return cert_info, priv_key_info.decode('hex')

    @staticmethod
    def _serialize_certificate_key(certificate_id, pem_private_key):
        info_str = certificate_id + pem_private_key.encode('hex')
        return info_str

    @command('wp')
    def exportcertificateinfo(self, certificate_id, user):
        """
        Export serialized channel signing information
        """

        if not self.cansignwithcertificate(certificate_id, user):
            return {'error': 'certificate private is not in the wallet: %s' % certificate_id}
        priv_key = self.wallets[user].get_certificate_signing_key(certificate_id)
        if not priv_key:
            return {'error': 'failed to key signing key for %s' % certificate_id}
        return self._serialize_certificate_key(certificate_id, priv_key)

    def _import_certificate_info(self, certificate_id, signing_key, certificate_claim, user):
        if self.cansignwithcertificate(certificate_id, user):
            return {'error': 'refusing to overwrite certificate key already in the wallet',
                    'success': False}
        certificate_claim_obj = ClaimDict.load_dict(certificate_claim['value'])
        if not certificate_claim_obj.is_certificate:
            return {'error': 'claim is not a certificate', 'success': False}
        if not certificate_claim_obj.validate_private_key(signing_key, certificate_id):
            return {'error': 'private key does not match certificate', 'success': False}
        self.wallets[user].save_certificate(certificate_id, signing_key)
        return {'success': True}

    @command('wpn')
    def importcertificateinfo(self, user, *serialized_certificate_info):
        """
        Import serialized channel infos
        """

        infos = {}
        response = {}
        for info in serialized_certificate_info:
            certificate_id, signing_key = self._deserialize_certificate_key(info)
            infos[certificate_id] = signing_key
        certificate_claims = self.getclaimsbyids(infos.keys())
        for cert_id, cert_claim in certificate_claims.iteritems():
            response[cert_id] = self._import_certificate_info(cert_id, infos[cert_id], cert_claim, user)
        return response

    @command('wpn')
    def updateclaimsignature(self, name, user, amount=None, claim_id=None, certificate_id=None):
        """
        Update an unsigned claim with a signature
        """

        claim_value = None
        claim_address = None
        if claim_id is None:
            claims = self.getnameclaims(user, raw=True, include_supports=False,
                                        skip_validate_signatures=True)
            for claim in claims:
                if claim['name'] == name and not claim['is_spent']:
                    claim_id = claim['claim_id']
                    claim_value = claim['value']
                    claim_address = claim['address']
                    break

        if claim_id is None or claim_value is None:
            return {'error': 'no claim to update'}
        claim = smart_decode(claim_value)
        if certificate_id is None:
            certificate_id = claim.certificate_id
            certificate = self.getclaimbyid(certificate_id)
            if not certificate:
                raise Exception('Certificate claim {} not found'.format(claim.certificate_id))
        elif not self.cansignwithcertificate(certificate_id, user):
            return {
                'error': ('can update claim for unet://{}#{}, but the signing key is '
                          'missing for certificate {}').format(name, claim_id, certificate_id)
            }
        else:
            certificate = self.getclaimbyid(str(certificate_id))
            if not certificate:
                raise Exception('Certificate claim {} not found'.format(claim.certificate_id))
        validated, channel_name = self.validate_claim_signature_and_get_channel_name(claim,
                                                                                     certificate,
                                                                                     claim_address)
        if validated:
            return {
                'error': 'unet://{}#{} has a valid signature already'.format(name, claim_id)
            }

        return self.update(name, claim.serialized_no_signature, amount=amount,
                           certificate_id=certificate_id, claim_id=claim_id, raw=True)

    @command('wpn')
    def updatecertificate(self, name, user, amount=None, revoke=False, val=None):
        """
        Update a certificate claim
        """

        if not parse_unet_uri(name).is_channel:
            return {'error': 'non compliant uri for certificate'}
        elif not revoke and not val:
            return {'error': 'nothing to update with'}

        if revoke:
            secp256k1_private_key = get_signer(SECP256k1).generate().private_key.to_pem()
            certificate = ClaimDict.generate_certificate(secp256k1_private_key, curve=SECP256k1)
            result = self.update(name, certificate.serialized, amount=amount, raw=True)
            self.wallets[user].save_certificate(result['claim_id'], secp256k1_private_key)
        else:
            decoded = smart_decode(val)
            if not decoded.is_certificate:
                return {'error': 'value is not a certificate'}
            result = self.update(name, decoded.serialized, amount=amount, raw=True)
        return result

    @command('wp')
    def cansignwithcertificate(self, certificate_id, user):
        """
        Can sign with given claim certificate
        """
        wallet = self.wallets[user]
        if wallet.get_certificate_signing_key(certificate_id) is not None:
            return True
        return False

    @command('wpn')
    def support(self, name, claim_id, amount, user, broadcast=True, claim_addr=None, tx_fee=None,
                change_addr=None):
        """
        Support a name claim
        """
        wallet = self.wallets[user]
        if claim_addr is None:
            claim_addr = wallet.get_least_used_address()
        if change_addr is None:
            change_addr = wallet.get_least_used_address(for_change=True)

        claim_id = decode_claim_id_hex(claim_id)
        amount = int(COIN * amount)
        if amount <= 0:
            return {'success': False, 'reason': 'Amount must be greater than 0'}
        if tx_fee is not None:
            tx_fee = int(COIN * tx_fee)
            if tx_fee < 0:
                return {'success': False, 'reason': 'tx_fee must be greater than or equal to 0'}

        outputs = [(TYPE_ADDRESS | TYPE_SUPPORT, ((name, claim_id), claim_addr), amount)]
        coins = wallet.get_spendable_coins()
        try:
            tx = wallet.make_unsigned_transaction(coins, outputs, self.config, tx_fee,
                                                  change_addr)
        except NotEnoughFunds:
            return {'success': False, 'reason': 'Not enough funds'}
        wallet.sign_transaction(tx, self._password)
        if broadcast:
            success, out = wallet.send_tx(tx)
            if not success:
                return {'success': False, 'reason': out}

        nout = None
        for i, output in enumerate(tx._outputs):
            if output[0] & TYPE_SUPPORT:
                nout = i

        return {"success": True, "txid": tx.hash(), "nout": nout, "tx": str(tx),
                "fee": str(Decimal(tx.get_fee()) / COIN)}

    @command('wpn')
    def sendwithsupport(self, claim_id, amount, user, broadcast=True, tx_fee=None,
                        change_addr=None):
        """
        Send credits to a claim's address via a support transaction
        """

        claim = self.getclaimbyid(claim_id)
        if claim.get('signature_is_valid') is False:
            return {'error: refusing to support a claim with an invalid signature'}
        claim_addr = claim['address']
        name = claim['name']
        return self.support(name, claim_id, amount, user, broadcast, claim_addr, tx_fee, change_addr)

    def verify_request_to_make_claim(self, uri, val, certificate_id):
        try:
            parsed_uri = parse_unet_uri(uri)
        except URIParseError as err:
            return {'error': 'Failed to decode URI: %s' % err}

        # val = base64.b64decode(val.decode('hex'))
        if parsed_uri.is_channel:
            if parsed_uri.path:
                try:
                    if smart_decode(val).is_certificate:
                        return {'error': 'Claim in a channel should not contain a certificate'}
                except DecodeError as err:
                    return {'error': 'Failed to decode claim: %s' % err}
                name = parsed_uri.path
            else:
                try:
                    if not smart_decode(val).is_certificate:
                        return {'error': 'Channel claim does not contain a certificate'}
                except DecodeError as err:
                    return {'error': 'Failed to decode certificate in claim: %s' % err}
                name = parsed_uri.name
        else:
            name = parsed_uri.name

        return {'name': name, 'certificate_id': certificate_id, 'val': val}

    @command('wpn')
    def renewclaimsbeforeexpiration(self, height, user, broadcast=True, skip_validate_schema=False):
        """
        Renew unexpired claims that will expire by the specified height.
        Unexpired claims will be updated to an identical claim, and supports
        will be spent into an identical support

        :param height: (int) update claims expiring before or at this block height
        :param skip_validate_schema: (bool) skip validation of schema
        :param broadcast: (bool) broadcast transactions
        :returns dictionary, {<outpoint string>: formatted claim result}
        """
        claims = self.wallets[user].get_name_claims(include_abandoned=False, include_supports=True,
                                                    exclude_expired=True)
        pending_expiration = [claim for claim in claims if claim['expiration_height'] <= height]
        results = {}
        for claim in pending_expiration:
            outpoint = "%s:%i" % (claim['txid'], claim['nout'])
            results[outpoint] = self._renewclaim(claim, user, broadcast, skip_validate_schema)
        return results

    @command('wpn')
    def renewclaim(self, txid, nout, user, broadcast=True, skip_validate_schema=False):
        """
        Renew claim. Unexpired claims will be udpated to an identical claim
        and supports will be spent into an identical support.

        :param txid: (str) txid of claim
        :param nout: (int) nout of claim
        :param skip_validate_schema: (bool) skip validation of schema
        :param broadcast: (bool) True if broadcasting the claim
        :returns dictionary: formatted claim result
        """
        claims = self.wallets[user].get_name_claims(include_abandoned=False, include_supports=True,
                                                    exclude_expired=True)
        claims = [claim for claim in claims if claim['txid'] == txid and claim['nout'] == nout]
        if not claims:
            return {'success': False, 'reason': 'no matching claim found for %s:%i' % (txid, nout)}
        claim = claims[0]
        return self._renewclaim(claim, user, broadcast, skip_validate_schema)

    def _renewclaim(self, claim, user, broadcast=True, skip_validate_schema=False):
        log.info("Updating unet://%s#%s (%s)", claim['name'], claim['claim_id'],
                 claim['category'])
        if claim['category'] != 'support':
            out = self.update(claim['name'], claim['value'], claim_id=claim['claim_id'],
                              txid=claim['txid'], nout=claim['nout'],
                              skip_validate_schema=skip_validate_schema, broadcast=broadcast)
        else:
            out = self.updatesupport(claim['txid'], claim['nout'], user, broadcast=broadcast)
        return out

    @command('wpn')
    def updatesupport(self, txid, nout, user, amount=None, broadcast=True,
                      claim_addr=None, tx_fee=None, change_addr=None):
        """
        Update a claim support, will spend support into a new support

        :param txid: (str) txid of support transaction to update
        :param nout: (int) nout of support transaction to update
        :param amount: (float) amount to support claim by, defaults to the current amount
        :param broadcast: (bool) broadcast the transaction
        :param claim_addr: (str) address to send support to
        :param tx_fee: (float) tx fee
        :param change_addr: (str) address to send change to
        :returns formatted claim result
        """
        wallet = self.wallets[user]
        if claim_addr is None:
            claim_addr = wallet.get_least_used_address()
        if change_addr is None:
            change_addr = wallet.get_least_used_address(for_change=True)

        supports = wallet.get_name_claims(include_supports=True)
        claim_support = [support for support in supports if
                         support['txid'] == txid and support['nout'] == nout and
                         support['category'] == 'support']
        if not claim_support:
            return {'success': False, 'reason': 'Support not found for txo %s:%i' % (txid,
                                                                                     nout)}
        claim_support = claim_support[0]
        claim_id = claim_support['claim_id']
        name = claim_support['name']
        val = None

        out = self._get_input_output_for_updates(name, val, amount, claim_id, txid, nout, user,
                                                 claim_addr, change_addr, tx_fee,
                                                 is_support_replace=True)
        if not out['success']:
            return out
        else:
            inputs = out['inputs']
            outputs = out['outputs']

        tx = Transaction.from_io(inputs, outputs)
        wallet.sign_transaction(tx, self._password)
        if broadcast:
            success, out = wallet.send_tx(tx)
            if not success:
                return {"success": False, "reason": out}
        nout = None
        amount = 0
        for i, output in enumerate(tx._outputs):
            if output[0] & TYPE_SUPPORT:
                nout = i
                amount = output[2]
        return {
            "success": True,
            "txid": tx.hash(),
            "nout": nout,
            "tx": str(tx),
            "fee": str(Decimal(tx.get_fee()) / COIN),
            "amount": str(Decimal(amount) / COIN),
            "claim_id": claim_id
        }

    @command('wpn')
    def update(self, name, val, user, amount=None, certificate_id=None, claim_id=None, txid=None,
               nout=None, broadcast=True, claim_addr=None, tx_fee=None, change_addr=None, raw=None,
               skip_validate_schema=None):
        """
        Update a name claim

        :param name: (str) name of claim being updated
        :param val: (str) calim value to update to
        :param amount: (float) amount to update, defaults to the amount in the
            claim being updated minus tx fees
        :param certificate_id: claim ID of the certificate associated with the claim
        :param claim_id: claim ID of the claim being updated
        :param txid: (str) txid of support transaction to update
        :param nout: (int) nout of support transaction to update
        :param broadcast: (bool) broadcast the transaction
        :param claim_addr: (str) address to send support to
        :param tx_fee: (float) tx fee
        :param change_addr: (str) address to send change to
        :param raw: (bool) default False. If True, val is byte encoded already
            so do not decode from hex string
        :param skip_validate_schema:default False. If True, skip validation of
            claim schema in val, and skip claim signing. Cannot be True if
            certificate_id is not None

        :returns formatted claim result
        """
        wallet = self.wallets[user]
        gl.flag_claim = True
        if skip_validate_schema and certificate_id:
            return {'success': False, 'reason': 'refusing to sign claim without validated schema'}

        parsed_claim = self.verify_request_to_make_claim(name, val, certificate_id)
        if 'error' in parsed_claim:
            return {'success': False, 'reason': parsed_claim['error']}

        parsed_uri = parse_unet_uri(name)
        name = parsed_claim['name']
        val = parsed_claim['val']
        certificate_id = parsed_claim['certificate_id']

        if not raw:
            val = val.decode('hex')
        if not skip_validate_schema:
            try:
                decoded_claim = smart_decode(val)
            except DecodeError as err:
                return {'success': False, 'reason': 'Decode error: %s' % err}
        else:
            decoded_claim = None
        if claim_addr is None:
            claim_addr = wallet.get_least_used_address()
        if not base_decode(claim_addr, ADDRESS_LENGTH, 58):
            return {'error': 'invalid claim address'}

        if change_addr is None:
            change_addr = wallet.get_least_used_address(for_change=True)
        if not base_decode(change_addr, ADDRESS_LENGTH, 58):
            return {'error': 'invalid change address'}
        if claim_id is None or txid is None or nout is None:
            claims = self.getnameclaims(user, skip_validate_signatures=True)
            for claim in claims:
                if claim['name'] == name and not claim['is_spent']:
                    claim_id = claim['claim_id']
                    txid = claim['txid']
                    nout = claim['nout']
                    break
            if not claim_id:
                return {'success': False, 'reason': 'No claim to update'}

        if not skip_validate_schema:
            try:
                claim_value = smart_decode(val)
            except DecodeError as err:
                return {'success': False,
                        'reason': 'Decode error: %s' % err}

            if not parsed_uri.is_channel and claim_value.is_certificate:
                return {'success': False,
                        'reason': 'Certificates must have URIs beginning with /"@/"'}

            if claim_value.has_signature:
                return {'success': False, 'reason': 'Claim value is already signed'}

            if certificate_id is not None:
                if not self.cansignwithcertificate(certificate_id, user):
                    return {'success': False,
                            'reason': 'Cannot sign for certificate %s' % certificate_id}

            if certificate_id and claim_value:
                signing_key = wallet.get_certificate_signing_key(certificate_id)
                signed = claim_value.sign(signing_key, claim_addr, certificate_id, curve=SECP256k1)
                val = signed.serialized

            if certificate_id and decoded_claim:
                signing_key = wallet.get_certificate_signing_key(certificate_id)
                if signing_key:
                    signed = decoded_claim.sign(signing_key,
                                                claim_addr,
                                                certificate_id,
                                                curve=SECP256k1)
                    val = signed.serialized
                else:
                    return {'success': False,
                            'reason': "Cannot sign with certificate %s" % certificate_id}

            elif not certificate_id and decoded_claim:
                if decoded_claim.has_signature:
                    certificate_id = decoded_claim.certificate_id
                    signing_key = wallet.get_certificate_signing_key(certificate_id)
                    if signing_key:
                        claim = ClaimDict.deserialize(val)
                        signed = claim.sign(signing_key,
                                            claim_addr,
                                            certificate_id,
                                            curve=SECP256k1)
                        val = signed.serialized
                    else:
                        return {'success': False,
                                'reason': "Cannot sign with certificate %s" % certificate_id}

        out = self._get_input_output_for_updates(name, val, amount, claim_id, txid, nout, user,
                                                 claim_addr, change_addr, tx_fee)
        if not out['success']:
            return out
        else:
            inputs = out['inputs']
            outputs = out['outputs']

        tx = Transaction.from_io(inputs, outputs)
        wallet.sign_transaction(tx, self._password)
        if broadcast:
            success, out = wallet.send_tx(tx)
            if not success:
                return {"success": False, "reason": out}

        nout = None
        amount = 0
        for i, output in enumerate(tx._outputs):
            if output[0] & TYPE_UPDATE:
                nout = i
                amount = output[2]

        return {
            "success": True,
            "txid": tx.hash(),
            "nout": nout,
            "tx": str(tx),
            "fee": str(Decimal(tx.get_fee()) / COIN),
            "amount": str(Decimal(amount) / COIN),
            "claim_id": claim_id
        }

    def _get_input_output_for_updates(self, name, val, amount, claim_id, txid, nout, user,
                                      claim_addr=None, change_addr=None, tx_fee=None,
                                      is_support_replace=False):
        """
        obtain inputs and outputs when crafting either an update
        or a support replacement

        :param name: (str) claim name
        :param val: (str) claim value
        :param amount: (float) claim amount, if amount is None, we keep the same amount
            as the original claim minus the tx fee
        :param claim_id: (str) claim id to be updated or if in the case of support replacements,
           claim_id of the claim that's being supported
        :param txid: (str) txid of claim to be updated
        :param nout: (int) nout of claim to be updated
        :param claim_addr: (str) specify address to send the claim to
        :param change_addr: (str) specify change address to send change to
        :param tx_fee: (float) specify amount of tx fee to pay
        :param is_support_replace: (bool) False if we are doing an update of a claim. If True,
            we are replacing a support (abandon previous support and create new
            support in place of it)

        :returns a dictionary where key 'success' is True if succesful in obtaining
            inputs and outputs, and False if not. If 'success' is False, there will
            be a 'reason' field for the failure reaso. If 'success' is True,
            there will be an 'outputs' field and 'inputs' field.
        """
        wallet = self.wallets[user]
        decoded_claim_id = decode_claim_id_hex(claim_id)

        if amount is not None:
            amount = int(COIN * amount)
            if amount <= 0:
                return {'success': False, 'reason': 'Amount must be greater than 0'}
        if tx_fee is not None:
            tx_fee = int(COIN * tx_fee)
            if tx_fee < 0:
                return {'success': False, 'reason': 'tx_fee must be greater than or equal to 0'}

        claim_utxo = wallet.get_spendable_claimtrietx_coin(txid, nout)
        if not is_support_replace and claim_utxo['is_support']:
            return {'success': False,
                    'reason': 'Cannot update a support, is_support_replace must be True'}

        inputs = [claim_utxo]
        txout_value = claim_utxo['value']

        if not is_support_replace:
            claim_tuple = ((name, decoded_claim_id, val), claim_addr)
            claim_type = TYPE_UPDATE
        else:
            # we are spending a support to make a new support
            claim_tuple = ((name, decoded_claim_id), claim_addr)
            claim_type = TYPE_SUPPORT

        # if amount is not specified, keep the same amount minus the tx fee
        if amount is None:
            dummy_outputs = [
                (
                    TYPE_ADDRESS | claim_type,
                    claim_tuple,
                    txout_value
                )
            ]
            fee = self._calculate_fee(inputs, dummy_outputs, tx_fee, user)
            if fee >= txout_value:
                return {
                    'success': False,
                    'reason': 'Fee will exceed amount available in original bid. Increase amount'
                }

            outputs = [
                (
                    TYPE_ADDRESS | claim_type,
                    claim_tuple,
                    txout_value - fee
                )
            ]

        elif amount <= 0:
            return {'success': False, 'reason': 'Amount must be greater than zero'}

        # amount is more than the original bid or equal, we need to get an input
        elif amount >= txout_value:
            additional_input_fee = 0
            if tx_fee is None:
                claim_input_size = Transaction.estimated_input_size(claim_utxo)
                additional_input_fee = Transaction.fee_for_size(wallet.relayfee(),
                                                                wallet.fee_per_kb(self.config),
                                                                claim_input_size)

            get_inputs_for_amount = amount - txout_value + additional_input_fee
            # create a dummy tx for the extra amount in order to get the proper inputs to spend
            dummy_outputs = [
                (
                    TYPE_ADDRESS | claim_type,
                    claim_tuple,
                    get_inputs_for_amount
                )
            ]
            coins = wallet.get_spendable_coins()
            try:
                dummy_tx = wallet.make_unsigned_transaction(coins, dummy_outputs,
                                                            self.config, tx_fee, change_addr)
            except NotEnoughFunds:
                return {'success': False, 'reason': 'Not enough funds'}

            # add the unspents to input
            for i in dummy_tx._inputs:
                inputs.append(i)

            outputs = [
                (
                    TYPE_ADDRESS | claim_type,
                    claim_tuple,
                    amount
                )
            ]
            # add the change utxos to output
            for output in dummy_tx._outputs:
                if not output[0] & claim_type:
                    outputs.append(output)

        # amount is less than the original bid,
        # we need to put remainder minus fees in a change address
        elif amount < txout_value:

            dummy_outputs = [
                (
                    TYPE_ADDRESS | claim_type,
                    claim_tuple,
                    amount
                ),
                (
                    TYPE_ADDRESS,
                    change_addr,
                    txout_value - amount
                )
            ]
            fee = self._calculate_fee(inputs, dummy_outputs, tx_fee, user)
            if fee > txout_value - amount:
                return {
                    'success': False,
                    'reason': 'Fee will be greater than change amount, use amount=None to expend '
                              'change as fee'
                }

            outputs = [
                (
                    TYPE_ADDRESS | claim_type,
                    claim_tuple,
                    amount
                ),
                (
                    TYPE_ADDRESS,
                    change_addr,
                    txout_value - amount - fee
                )
            ]

        return {'success': True, 'outputs': outputs, 'inputs': inputs}

    @command('wpn')
    def abandon(self, user, claim_id=None, txid=None, nout=None, broadcast=True, return_addr=None,
                tx_fee=None):
        """
        Abandon a name claim

        Either specify the claim with a claim_id or with txid and nout
        """
        wallet = self.wallets[user]
        gl.flag_claim = True
        claims = self.getnameclaims(user, raw=True, include_abandoned=False, include_supports=True,
                                    claim_id=claim_id, txid=txid, nout=nout,
                                    skip_validate_signatures=True)
        if len(claims) > 1:
            return {"success": False, 'reason': 'more than one claim that matches'}
        elif len(claims) == 0:
            return {"success": False, 'reason': 'claim not found', 'claim_id': claim_id}
        else:
            claim = claims[0]

        txid, nout = claim['txid'], claim['nout']
        if return_addr is None:
            return_addr = wallet.get_least_used_address()
        if tx_fee is not None:
            tx_fee = int(COIN * tx_fee)
            if tx_fee < 0:
                return {'success': False, 'reason': 'tx_fee must be greater than or equal to 0'}

        i = wallet.get_spendable_claimtrietx_coin(txid, nout)
        inputs = [i]
        txout_value = i['value']
        # create outputs
        outputs = [(TYPE_ADDRESS, return_addr, txout_value)]
        # fee will be roughly 10,000 deweys (0.0001 UT), standard abandon should be about 200 bytes
        # this is assuming config is not set to dynamic, which in case it will get fees from
        # ulord's fee estimation algorithm

        fee = self._calculate_fee(inputs, outputs, tx_fee, user)
        if fee > txout_value:
            return {'success': False, 'reason': 'transaction fee exceeds amount to abandon'}
        return_value = txout_value - fee

        # create transaction
        outputs = [(TYPE_ADDRESS, return_addr, return_value)]
        tx = Transaction.from_io(inputs, outputs)
        wallet.sign_transaction(tx, self._password)
        if broadcast:
            success, out = wallet.send_tx(tx)
            if not success:
                return {'success': False, 'reason': out}
        return {'success': True, 'txid': tx.hash(), 'tx': str(tx),
                'fee': str(Decimal(tx.get_fee()) / COIN)}

    # ========================================================================
    # ####################    my packaging interface   #######################
    # ========================================================================
    @command("")
    def set_password_for_rpc(self, password):
        if password:
            if not isinstance(password, (str, buffer)):
                try:
                    password = str(password)
                except:
                    raise ValueError("password error! the password can't conversion into str")
            self._password = password

    @command('wn')
    def publish(self, user, name, bid, metadata, content_type, source_hash, currency, amount, password=None,
                address=None, tx_fee=None, skip_update_check=None):

        # 和余额的比较claim里面有
        if bid < 0:
            raise ValueError('the bid must > 0')

        if 'version' not in metadata:
            metadata['version'] = '_0_0_1'
        if address is None:
            address = self.wallets[user].addresses(False)[0]
        self.set_password_for_rpc(password)

        metadata['fee'] = {
            "currency": currency,
            "address": address,
            "amount": amount,
            'version': '_0_0_1',
        }

        claim_dict = {
            'version': '_0_0_1',
            'claimType': 'streamType',
            'stream': {
                'metadata': metadata,
                'source': {
                    "source": source_hash,
                    "version": "_0_0_1",
                    "contentType": content_type,
                    "sourceType": "unet_sd_hash"
                },
                'version': '_0_0_1',
            }
        }

        try:
            claim = ClaimDict.load_dict(claim_dict)
        except DecodeError as err:
            raise ValueError("invalid publish claim: %s" % err.message)

        claim_serialized = claim.serialized

        # 这里需要作异步优化
        res = self.claim(name, claim_serialized, bid, user, raw=True, change_addr=address, claim_addr=address,
                         tx_fee=tx_fee, skip_update_check=skip_update_check)

        log.info("Publish: %s", {
            'name': name,
            'bid': bid,
            'claim_address': address,
            'claim_dict': claim_dict,
            'result': res
        })

        return res

    @command('')
    def pay(self, send_user, password, receive_user, amount):
        """ Create and broadcast transaction. """
        self.set_password_for_rpc(password)
        address = self.wallets[receive_user].addresses(False)[0]
        tx = self._mktx([(address, amount)], None, None, None, False, False, send_user)
        res = self.network.synchronous_get(('blockchain.transaction.broadcast', [str(tx)]))
        return {
            'success': True,
            'txid': res
        }

    @command('')
    def consume(self, user, claim_id, password):
        try:
            validate_claim_id(claim_id)
        except Exception as err:
            return {'success': False,
                    'reason': "claim_id error: %s" % err.message}

        claim = self.getclaimbyid(claim_id)
        if not claim:
            return {'success': False,
                    'reason': "can't find anything with the claim_id."}

        try:
            claim_value = smart_decode(claim['value'])
        except DecodeError as err:
            return {'success': False,
                    'reason': 'Decode error: %s' % err}
        if claim_value.has_fee:
            address = claim_value.source_fee.address
            amount = claim_value.source_fee.amount
            self.set_password_for_rpc(password)

            res = self.paytoandsend(address, amount, user)
            return {'success': True,
                    'txid': res}
        else:
            return {'success': False,
                    'reason': "can't find fee in the claim."}


# ----------------------------------------------------------------------------------------------------------------------
param_descriptions = {
    'privkey': 'Private key. Type \'?\' to get a prompt.',
    'destination': 'Bitcoin address, contact or alias',
    'address': 'Bitcoin address',
    'seed': 'Seed phrase',
    'txid': 'Transaction ID',
    'pos': 'Position',
    'height': 'Block height',
    'tx': 'Serialized transaction (hexadecimal)',
    'key': 'Variable name',
    'pubkey': 'Public key',
    'message': 'Clear text message. Use quotes if it contains spaces.',
    'encrypted': 'Encrypted message',
    'amount': 'Amount to be sent (in BTC). Type \'!\' to send the maximum available.',
    'requested_amount': 'Requested amount (in BTC).',
    'outputs': 'list of ["address", amount]',
    'exclude_claimtrietx': 'Exclude claimtrie transactions.',
    'set_default_certificate': 'Set new certificate as default signer even if there is already a '
                               'default certificate',
}

command_options = {
    'password': ("-W", "--password", "Password"),
    'receiving': (None, "--receiving", "Show only receiving addresses"),
    'change': (None, "--change", "Show only change addresses"),
    'frozen': (None, "--frozen", "Show only frozen addresses"),
    'unused': (None, "--unused", "Show only unused addresses"),
    'funded': (None, "--funded", "Show only funded addresses"),
    'show_balance': ("-b", "--balance", "Show the balances of listed addresses"),
    'show_labels': ("-l", "--labels", "Show the labels of listed addresses"),
    'nocheck': (None, "--nocheck", "Do not verify aliases"),
    'tx_fee': ("-f", "--fee", "Transaction fee (in BTC)"),
    'from_addr': ("-F", "--from",
                  "Source address. If it isn't in the wallet, it will ask for the private key "
                  "unless supplied in the format public_key:private_key. It's not saved in the "
                  "wallet."),
    'change_addr': ("-c", "--change",
                    "Change address. Default is a spare address, or the source address if it's "
                    "not in the wallet"),
    'nbits': (None, "--nbits", "Number of bits of entropy"),
    'entropy': (None, "--entropy", "Custom entropy"),
    'language': ("-L", "--lang", "Default language for wordlist"),
    'gap_limit': ("-G", "--gap", "Gap limit"),
    'privkey': (None, "--privkey", "Private key. Set to '?' to get a prompt."),
    'unsigned': ("-u", "--unsigned", "Do not sign transaction"),
    'domain': ("-D", "--domain", "List of addresses"),
    'user': (None, "--user", "uwallet name"),
    'account': (None, "--account", "Account"),
    'memo': ("-m", "--memo", "Description of the request"),
    'expiration': (None, "--expiration", "Time in seconds"),
    'force': (None, "--force", "Create new address beyong gap limit, if no more address is "
                               "available."),
    'pending': (None, "--pending", "Show only pending requests."),
    'expired': (None, "--expired", "Show only expired requests."),
    'paid': (None, "--paid", "Show only paid requests."),
    'exclude_claimtrietx': (None, "--exclude_claimtrietx", "Exclude claimtrie transactions"),
    'return_addr': (None, "--return_addr",
                    "Return address where amounts in abandoned claimtrie transactions are "
                    "returned."),
    'claim_addr': (None, "--claim_addr", "Address where claims are sent."),
    'broadcast': (None, "--broadcast", "if True, broadcast the transaction"),
    'raw': ("-r", "--raw", "if True, don't decode claim values"),
    'page': ("-p", "--page", "page number"),
    'page_size': ("-s", "--page_size", "page size"),
    'claim_id': (None, "--claim_id", "claim id"),
    'txid': ("-t", "--txid", "txid"),
    'nout': ("-n", "--nout", "nout"),
    'certificate_id': (None, "--certificate_id", "claim id of a certificate that can be used "
                                                 "for signing"),
    'skip_validate_schema': (None, "--ignore_schema", "Validate the claim conforms with unet "
                                                      "schema"),
    'set_default_certificate': (None, "--set_default_certificate",
                                "Set the new certificate as the default, even if there already is "
                                "one"),
    'amount': ("-a", "--amount", "amount to use in updated name claim"),
    'include_abandoned': (None, "--include_abandoned", "include abandoned claims"),
    'skip_validate_signatures': (None, "--skip_validate_signatures", "include abandoned claims"),
    'include_supports': (None, "--include_supports", "include supports"),
    'skip_update_check': (None, "--skip_update_check",
                          "do not check for an existing unspent claim before making a new one"),
    'revoke': (None, "--revoke", "if true, create a new signing key and revoke the old one"),
    'val': (None, '--value', 'claim value'),
    'timeout': (None, '--timeout', 'timeout'),
    'include_tip_info': (None, "--include_tip_info", 'Include claim tip information'),
    # TODO: 修改这个描述
    'address': (None, "--address", 'give address, to receive ULD')
}


def json_loads(x):
    """don't use floats because of rounding errors"""
    return json.loads(x, parse_float=lambda x: str(Decimal(x)))


arg_types = {
    'num': int,
    'nbits': int,
    'entropy': long,
    'tx': json_loads,
    'pubkeys': json_loads,
    'inputs': json_loads,
    'outputs': json_loads,
    'tx_fee': lambda x: str(Decimal(x)) if x is not None else None,
    'amount': lambda x: str(Decimal(x)) if x != '!' else '!',
    'nout': int
}

config_variables = {

    'addrequest': {
        'requests_dir': 'directory where a bip70 file will be written.',
        'ssl_privkey': 'Path to your SSL private key, needed to sign the request.',
        'ssl_chain': 'Chain of SSL certificates, needed for signed requests. Put your certificate '
                     'at the top and the root CA at the end',
        'url_rewrite': 'Parameters passed to str.replace(), in order to create the r= part of '
                       'bitcoin: URIs. Example: \"(\'file:///var/www/\',\'https://unet.org/\')\"',
    },
    'listrequests': {
        'url_rewrite': 'Parameters passed to str.replace(), in order to create the r= part of '
                       'bitcoin: URIs. Example: \"(\'file:///var/www/\',\'https://unet.org/\')\"',
    }
}


def set_default_subparser(self, name, args=None):
    """
    see http://stackoverflow.com/questions/5176691/argparse-how-to-specify-a-default-subcommand
    """

    subparser_found = False
    for arg in sys.argv[1:]:
        if arg in ['-h', '--help']:  # global help if no subparser
            break
    else:
        for x in self._subparsers._actions:
            if not isinstance(x, argparse._SubParsersAction):
                continue
            for sp_name in x._name_parser_map.keys():
                if sp_name in sys.argv[1:]:
                    subparser_found = True
        if not subparser_found:
            # insert default in first position, this implies no
            # global options without a sub_parsers specified
            if args is None:
                sys.argv.insert(1, name)
            else:
                args.insert(0, name)


argparse.ArgumentParser.set_default_subparser = set_default_subparser


def add_network_options(parser):
    parser.add_argument("-1", "--oneserver", action="store_true", dest="oneserver", default=False,
                        help="connect to one server only")
    parser.add_argument("-s", "--server", dest="server", default=None,
                        help="set server host:port:protocol, where protocol is either t (tcp) or"
                             " s (ssl)")
    parser.add_argument("-p", "--proxy", dest="proxy", default=None,
                        help="set proxy [type:]host[:port], where type is socks4,socks5 or http")


def get_parser():
    # parent parser, because set_default_subparser removes global options
    # todo： 解释上面的注释
    parent_parser = argparse.ArgumentParser('parent', add_help=False)
    group = parent_parser.add_argument_group('global options')
    group.add_argument("-v", "--verbose", action="store_true", dest="verbose", default=False,
                       help="Show debugging information")
    group.add_argument("--client", action="store_true", dest="client", default=False,
                       help="Is it a Qt Gui client")
    group.add_argument("-P", "--portable", action="store_true", dest="portable", default=False,
                       help="Use local 'electrum_data' directory")
    group.add_argument("-w", "--wallet", dest="wallet_path", help="wallet path")
    group.add_argument("-D", "--dir", dest="uwallet_path", help="electrum directory")
    group.add_argument("--guipassword", dest="guipassword", help="The password of uwallet for Qt gui client")
    group.add_argument("--user", dest="user", help="The name of uwallet")
    group.add_argument("--guinewpassword", dest="guinewpassword", help="The new password of uwallet for Qt gui client")
    # create main parser
    parser = argparse.ArgumentParser(
        parents=[parent_parser],
        epilog="Run 'uwallet help <command>' to see the help for a command")
    subparsers = parser.add_subparsers(dest='cmd', metavar='<command>')
    # daemon
    parser_daemon = subparsers.add_parser('daemon', parents=[parent_parser], help="Run Daemon")
    parser_daemon.add_argument("subcommand", choices=['start', 'status', 'stop'])
    # parser_daemon.set_defaults(func=run_daemon)
    add_network_options(parser_daemon)
    # commands
    for cmdname in sorted(known_commands.keys()):
        cmd = known_commands[cmdname]
        p = subparsers.add_parser(cmdname, parents=[parent_parser], help=cmd.help,
                                  description=cmd.description)
        # p.set_defaults(func=run_cmdline)
        if cmd.requires_password:
            p.add_argument("-W", "--password", dest="password", default=None, help="password")

        for optname, default in zip(cmd.options, cmd.defaults):
            a, b, help = command_options[optname]
            action = "store_true" if type(default) is bool else 'store'
            args = (a, b) if a else (b,)
            if action == 'store':
                _type = arg_types.get(optname, str)
                p.add_argument(*args, dest=optname, action=action, default=default, help=help,
                               type=_type)
            else:
                p.add_argument(*args, dest=optname, action=action, default=default, help=help)

        for param in cmd.params:
            h = param_descriptions.get(param, '')
            _type = arg_types.get(param, str)
            p.add_argument(param, help=h, type=_type)

        cvh = config_variables.get(cmdname)
        if cvh:
            group = p.add_argument_group('configuration variables',
                                         '(set with setconfig/getconfig)')
            for k, v in cvh.items():
                group.add_argument(k, nargs='?', help=v)

    # 'cmd' is the default command
    parser.set_default_subparser('cmd')
    return parser

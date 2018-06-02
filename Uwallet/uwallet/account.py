#-*- coding: UTF-8 -*-
import logging

from uwallet.errors import ParamsError
from uwallet.util import rev_hex, int_to_hex, is_extended_pubkey
from uwallet.hashing import hash_160
from uwallet.base import DecodeBase58Check, EncodeBase58Check
from uwallet.transaction import Transaction
from uwallet.ulord import pw_decode, pw_encode, hash_160_to_bc_address, address_from_private_key
from uwallet.ulord import public_key_to_bc_address, deserialize_xkey, bip32_public_derivation
from uwallet.ulord import CKD_pub, bip32_private_key

log = logging.getLogger(__name__)

class Account(object):
    def __init__(self, v, use_change=False):
        self.use_change = use_change
        self.receiving_pubkeys = v.get('receiving', [])

        # addresses will not be stored on disk
        self.receiving_addresses = map(self.pubkeys_to_address, self.receiving_pubkeys)
        if self.use_change:
            self.change_pubkeys = v.get('change', [])
            self.change_addresses = map(self.pubkeys_to_address, self.change_pubkeys)


    def dump(self):
        if self.use_change:
            return {'receiving': self.receiving_pubkeys, 'change': self.change_pubkeys}
        return self.receiving_pubkeys

    def get_pubkey(self, for_change, n):
        pubkeys_list = self.change_pubkeys if for_change else self.receiving_pubkeys
        return pubkeys_list[n]

    def get_address(self, for_change, n):
        addr_list = self.change_addresses if for_change else self.receiving_addresses
        return addr_list[n]

    def get_pubkeys(self, for_change, n):
        return [self.get_pubkey(for_change, n)]

    def get_addresses(self, for_change):
        addr_list = self.change_addresses if for_change else self.receiving_addresses
        return addr_list[:]

    def derive_pubkeys(self, for_change, n):
        pass

    def create_new_address(self, for_change):
        pubkeys_list = self.change_pubkeys if for_change else self.receiving_pubkeys
        addr_list = self.change_addresses if for_change else self.receiving_addresses
        n = len(pubkeys_list)
        pubkeys = self.derive_pubkeys(for_change, n)
        address = self.pubkeys_to_address(pubkeys)
        pubkeys_list.append(pubkeys)
        addr_list.append(address)
        # print_msg(address)
        return address

    def pubkeys_to_address(self, pubkey):
        return public_key_to_bc_address(pubkey.decode('hex'))

    def has_change(self):
        return True

    def get_name(self, k):
        return 'Main account'

    def redeem_script(self, for_change, n):
        return None

    def is_used(self, wallet):
        addresses = self.get_addresses(False)
        return any(wallet.address_is_old(a, -1) for a in addresses)

    def synchronize_sequence(self, wallet, for_change):
        limit = wallet.gap_limit_for_change if for_change else wallet.gap_limit
        while True:
            # addresses = self.get_addresses(for_change)
            addresses = wallet.get_addresses(for_change)
            if len(addresses) < limit:
                address = self.create_new_address(for_change)
                wallet.add_address(address, for_change)
                continue
            if not wallet.use_change:
                break

            if map(lambda a: wallet.address_is_old(a), addresses[-limit:]) == limit * [False]:
                break
            else:
                address = self.create_new_address(for_change)
                wallet.add_address(address)


    def synchronize(self, wallet):
        self.synchronize_sequence(wallet, False)
        if self.use_change:
            self.synchronize_sequence(wallet, True)


class ImportedAccount(Account):
    def __init__(self, d):
        self.keypairs = d['imported']

    def synchronize(self, wallet):
        return

    def get_addresses(self, for_change):
        return [] if for_change else sorted(self.keypairs.keys())

    def get_pubkey(self, *sequence):
        for_change, i = sequence
        assert for_change == 0
        addr = self.get_addresses(0)[i]
        return self.keypairs[addr][0]

    def get_xpubkeys(self, for_change, n):
        return self.get_pubkeys(for_change, n)

    def get_private_key(self, sequence, wallet, password):
        for_change, i = sequence
        assert for_change == 0
        address = self.get_addresses(0)[i]
        pk = pw_decode(self.keypairs[address][1], password)
        # this checks the password
        if address != address_from_private_key(pk):
            raise ParamsError('51001')
        return [pk]

    def has_change(self):
        return False

    def add(self, address, pubkey, privkey, password):
        self.keypairs[address] = (pubkey, pw_encode(privkey, password))

    def remove(self, address):
        self.keypairs.pop(address)

    def dump(self):
        return {'imported': self.keypairs}

    def get_name(self, k):
        return 'Imported keys'

    def update_password(self, old_password, new_password):
        for k, v in self.keypairs.items():
            pubkey, a = v
            b = pw_decode(a, old_password)
            c = pw_encode(b, new_password)
            self.keypairs[k] = (pubkey, c)


class BIP32_Account(Account):
    def __init__(self, v, use_change=False):
        Account.__init__(self, v, use_change)
        self.xpub = v['xpub']
        self.xpub_receive = None
        if use_change:
            self.xpub_change = None

    def correct_pubkeys(self):
        """
        Wallets could have duplicate pubkeys and skip pubkey generation
        for the nth sequence, due to a race condition bug
        return True if problem was found and corrected,
        retun False if no problem was found
        """
        correction_made = False
        if self._check_pubkeys(self.receiving_pubkeys, False):
            self.receiving_pubkeys = self._correct_pubkeys(self.receiving_pubkeys, False)
            correction_made = True
        if self.use_change and self._check_pubkeys(self.change_pubkeys, True):
            self.change_pubkeys = self._correct_pubkeys(self.change_pubkeys, True)
            correction_made = True
        return correction_made

    def _check_pubkeys(self, pubkeys, for_change):
        pubkeys_set = set(pubkeys)
        duplicate_key_found = len(pubkeys_set) != len(pubkeys)
        if duplicate_key_found:
            log.warn("Duplicate key found, will correct, this may take a minute")

        expected_last_pubkey = self.derive_pubkeys(for_change, len(pubkeys)-1)
        last_pubkey_incorrect = expected_last_pubkey != pubkeys[-1]
        if last_pubkey_incorrect:
            log.warn("Last pubkey was not as expected, will correct, this make take a minute")
        return duplicate_key_found or last_pubkey_incorrect

    def _correct_pubkeys(self, pubkeys, for_change):
        """
        Try to re-derive the nth pubkeys and add them
        in order, while making sure every pubkey gets added
        """
        pubkeys_set = set(pubkeys)
        corrected_pubkeys = []
        index = 0
        while len(pubkeys_set) > 0:
            expected_pubkey = self.derive_pubkeys(for_change, index)
            if expected_pubkey in pubkeys_set:
                pubkeys_set.remove(expected_pubkey)
            corrected_pubkeys.append(expected_pubkey)
            log.debug("Appending pubkey:%s", expected_pubkey)
            if index >= len(pubkeys) or expected_pubkey != pubkeys[index]:
                log.debug("Correction made, from %s to %s", expected_pubkey, pubkeys[index])
            index += 1
            if index > len(pubkeys) + 100:
                raise Exception(
                    "Critical error found when correcting public key, exceeded max key generation")
        return corrected_pubkeys

    # def dump(self):
    #     d = Account.dump(self)
    #     d['xpub'] = self.xpub
    #     return d

    def first_address(self):
        pubkeys = self.derive_pubkeys(0, 0)
        addr = self.pubkeys_to_address(pubkeys)
        return addr, pubkeys

    def get_master_pubkeys(self):
        return [self.xpub]

    @classmethod
    def derive_pubkey_from_xpub(cls, xpub, for_change, n):
        _, _, _, c, cK = deserialize_xkey(xpub)
        for i in [for_change, n]:
            cK, c = CKD_pub(cK, c, i)
        return cK.encode('hex')

    def get_pubkey_from_xpub(self, xpub, for_change, n):
        xpubs = self.get_master_pubkeys()
        i = xpubs.index(xpub)
        pubkeys = self.get_pubkeys(for_change, n)
        return pubkeys[i]

    def derive_pubkeys(self, for_change, n):
        xpub = self.xpub_change if for_change else self.xpub_receive
        if xpub is None:
            xpub = bip32_public_derivation(self.xpub, "", "/%d" % for_change)
            if for_change:
                self.xpub_change = xpub
            else:
                self.xpub_receive = xpub
        _, _, _, c, cK = deserialize_xkey(xpub)
        cK, c = CKD_pub(cK, c, n)
        result = cK.encode('hex')
        return result

    def get_private_key(self, sequence, wallet, password):
        out = []
        xpubs = self.get_master_pubkeys()
        roots = [k for k, v in wallet.master_public_keys.iteritems() if v in xpubs]
        for root in roots:
            xpriv = wallet.get_master_private_key(root, password)
            if not xpriv:
                continue
            _, _, _, c, k = deserialize_xkey(xpriv)
            pk = bip32_private_key(sequence, k, c)
            out.append(pk)
        return out

    def get_type(self):
        return 'Standard 1 of 1'

    def get_xpubkeys(self, for_change, n):
        # unsorted
        s = ''.join(map(lambda x: int_to_hex(x, 2), (for_change, n)))
        xpubs = self.get_master_pubkeys()
        return map(lambda xpub: 'ff' + DecodeBase58Check(xpub).encode('hex') + s, xpubs)

    @classmethod
    def parse_xpubkey(cls, pubkey):
        assert is_extended_pubkey(pubkey)
        pk = pubkey.decode('hex')
        pk = pk[1:]
        xkey = EncodeBase58Check(pk[0:78])
        dd = pk[78:]
        s = []
        while dd:
            n = int(rev_hex(dd[0:2].encode('hex')), 16)
            dd = dd[2:]
            s.append(n)
        assert len(s) == 2
        return xkey, s

    def get_name(self, k):
        return "Main account" if k == '0' else "Account " + k


class Multisig_Account(BIP32_Account):
    def __init__(self, v):
        self.m = v.get('m', 2)
        BIP32_Account.__init__(self, v)
        self.xpub_list = v['xpubs']

    def dump(self):
        d = Account.dump(self)
        d['xpubs'] = self.xpub_list
        d['m'] = self.m
        return d

    def get_pubkeys(self, for_change, n):
        return self.get_pubkey(for_change, n)

    def derive_pubkeys(self, for_change, n):
        return map(lambda x: self.derive_pubkey_from_xpub(x, for_change, n),
                   self.get_master_pubkeys())

    def redeem_script(self, for_change, n):
        pubkeys = self.get_pubkeys(for_change, n)
        return Transaction.multisig_script(sorted(pubkeys), self.m)

    def pubkeys_to_address(self, pubkeys):
        redeem_script = Transaction.multisig_script(sorted(pubkeys), self.m)
        address = hash_160_to_bc_address(hash_160(redeem_script.decode('hex')), 5)
        return address

    def get_address(self, for_change, n):
        return self.pubkeys_to_address(self.get_pubkeys(for_change, n))

    def get_master_pubkeys(self):
        return self.xpub_list

    def get_type(self):
        return 'Multisig %d of %d' % (self.m, len(self.xpub_list))

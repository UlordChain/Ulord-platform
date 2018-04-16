#-*- coding: UTF-8 -*-
import os
import urllib
import socket
import logging
import unetschema
from uwallet import ulord
from uwallet.util import hex_to_int, PrintError, int_to_hex, rev_hex
from uwallet.hashing import hash_encode, Hash, Hash_Header
from uwallet.errors import ChainValidationError
from uwallet.constants import HEADER_SIZE, HEADERS_URL, BLOCKS_PER_CHUNK, NULL_HASH
from uwallet.constants import blockchain_params

log = logging.getLogger(__name__)

 
class Ulord(PrintError):
    """Manages blockchain headers and their verification"""

    BLOCKCHAIN_NAME = "ulord_main"

    def __init__(self, config, network):
        self.config = config
        self.network = network
        self.headers_url = HEADERS_URL
        self.local_height = 0
        self.set_local_height()
        self.retrieving_headers = False

        self._MAX_TARGET = blockchain_params[self.BLOCKCHAIN_NAME]['max_target']
        self._N_TARGET_TIMESPAN = blockchain_params[self.BLOCKCHAIN_NAME]['target_timespan'] #150
        self._GENESIS_BITS = blockchain_params[self.BLOCKCHAIN_NAME]['genesis_bits']
        unetschema.BLOCKCHAIN_NAME = self.BLOCKCHAIN_NAME

    @property
    def MAX_TARGET(self):
        return self._MAX_TARGET

    @property
    def N_TARGET_TIMESPAN(self):
        return self._N_TARGET_TIMESPAN

    @property
    def GENESIS_BITS(self):
        return self._GENESIS_BITS

    def height(self):
        return self.local_height

    def init(self):
        self.init_headers_file()
        self.set_local_height()
        log.debug("%d blocks" % self.local_height)

    def verify_header(self, header, prev_header, bits, target):
        prev_hash = self.hash_header(prev_header)
        assert prev_hash == header.get('prev_block_hash'), "prev hash mismatch: %s vs %s" % (
            prev_hash, header.get('prev_block_hash'))
        assert bits == header.get('bits'), "bits mismatch: %s vs %s (hash: %s)" % (
            bits, header.get('bits'), self.hash_header(header))
        _pow_hash = self.hash_header(header)
        assert int('0x' + _pow_hash,
                   16) <= target, "insufficient proof of work: %s vs target %s" % (
            int('0x' + _pow_hash, 16), target)

    def verify_chain(self, chain):
        first_header = chain[0]
        height = first_header['block_height']
        prev_header = self.read_header(height - 1)
        for header in chain:
            height = header['block_height']
            if self.read_header(height) is not None:
                bits, target = self.get_target(height, prev_header, header)
                self.verify_header(header, prev_header, bits, target)
            prev_header = header

    def verify_chunk(self, index, data):
        prev_header = None
        if index != 0:
            prev_header = self.read_header(index * BLOCKS_PER_CHUNK - 1)
        for i in range(BLOCKS_PER_CHUNK):
            raw_header = data[i * HEADER_SIZE:(i + 1) * HEADER_SIZE]
            header = self.deserialize_header(raw_header)
            bits, target = self.get_target(index * BLOCKS_PER_CHUNK + i, prev_header, header)
            if header is not None:
                self.verify_header(header, prev_header, bits, target)
            prev_header = header

    def get_block_hash(self, header):
        block_hash = header.get('prev_block_hash')
        if block_hash:
            return block_hash
        else:
            assert header.get('block_height') == 0
            return NULL_HASH

    def serialize_header(self, res):
        s = int_to_hex(res.get('version'), 4) \
            + rev_hex(self.get_block_hash(res)) \
            + rev_hex(res.get('merkle_root')) \
            + rev_hex(res.get('claim_trie_root')) \
            + int_to_hex(int(res.get('timestamp')), 4) \
            + int_to_hex(int(res.get('bits')), 4) \
            + rev_hex(res.get('nonce'))
        return s



    def deserialize_header(self, s):
        h = {}
        h['version'] = hex_to_int(s[0:4])
        h['prev_block_hash'] = hash_encode(s[4:36])
        h['merkle_root'] = hash_encode(s[36:68])
        h['claim_trie_root'] = hash_encode(s[68:100])
        h['timestamp'] = hex_to_int(s[100:104])
        h['bits'] = hex_to_int(s[104:108])
        h['nonce'] = hash_encode(s[108:140])
        return h

    def hash_header(self, header):
        if header is None:
            return '0' * 64
        return hash_encode(Hash_Header(self.serialize_header(header).decode('hex')))

    def pow_hash_header(self, header):
        if header is None:
            return '0' * 64
        return hash_encode(Hash(self.serialize_header(header).decode('hex')))

    def path(self):
        return os.path.join(self.config.path, 'blockchain_headers')

    def init_headers_file(self):
        filename = self.path()
        if os.path.exists(filename):
            return
        try:
            socket.setdefaulttimeout(30)
            log.info("downloading headers from %s", self.headers_url)
            self.retrieving_headers = True
            try:
                urllib.urlretrieve(self.headers_url, filename)
            except:
                raise
            finally:
                self.retrieving_headers = False
            log.info("done.")
        except Exception:
            log.warning("download failed. creating empty headers file: %s", filename)
            open(filename, 'wb+').close()

    def save_chunk(self, index, chunk):
        filename = self.path()
        f = open(filename, 'rb+')
        f.seek(index * BLOCKS_PER_CHUNK * HEADER_SIZE)
        h = f.write(chunk)
        f.close()
        self.set_local_height()

    def save_header(self, header):
        data = self.serialize_header(header).decode('hex')
        if not len(data) == HEADER_SIZE:
            raise ChainValidationError("Header is wrong size")
        height = header.get('block_height')
        filename = self.path()
        f = open(filename, 'rb+')
        f.seek(height * HEADER_SIZE)
        h = f.write(data)
        f.close()
        self.set_local_height()

    def set_local_height(self):
        name = self.path()
        if os.path.exists(name):
            h = os.path.getsize(name) / HEADER_SIZE - 1
            if self.local_height != h:
                self.local_height = h

    def read_header(self, block_height):
        name = self.path()
        if os.path.exists(name):
            f = open(name, 'rb')
            f.seek(block_height * HEADER_SIZE)
            h = f.read(HEADER_SIZE)
            f.close()
            if len(h) == HEADER_SIZE:
                h = self.deserialize_header(h)
                return h

    def get_target(self, index, first, last, chain='main'):

        if index == 0:
            return self.GENESIS_BITS, self.MAX_TARGET
        assert last is not None, "Last shouldn't be none"
        # bits to target
        bits = last.get('bits')
        # print_error("Last bits: ", bits)
        self.check_bits(bits)

        # new target
        nActualTimespan = last.get('timestamp') - first.get('timestamp')
        nTargetTimespan = self.N_TARGET_TIMESPAN  #150
        nModulatedTimespan = nTargetTimespan - (nActualTimespan - nTargetTimespan) / 8
        nMinTimespan = nTargetTimespan - (nTargetTimespan / 8)
        nMaxTimespan = nTargetTimespan + (nTargetTimespan / 2)
        if nModulatedTimespan < nMinTimespan:
            nModulatedTimespan = nMinTimespan
        elif nModulatedTimespan > nMaxTimespan:
            nModulatedTimespan = nMaxTimespan

        bnOld = ArithUint256.SetCompact(bits)
        bnNew = bnOld * nModulatedTimespan

        bnNew /= nModulatedTimespan
        if bnNew > self.MAX_TARGET:
            bnNew = ArithUint256(self.MAX_TARGET)
        return bnNew.GetCompact(), bnNew._value

    def connect_header(self, chain, header):
        '''Builds a header chain until it connects.  Returns True if it has
        successfully connected, False if verification failed, otherwise the
        height of the next header needed.'''
        chain.append(header)  # Ordered by decreasing height
        height = header['block_height']
        if height > 0 and self.need_previous(header):
            return height - 1
        # The chain is complete so we can save it
        return self.save_chain(chain, height)

    def save_chain(self, chain, height):
        # Reverse to order by increasing height
        chain.reverse()
        try:
            self.verify_chain(chain)
            log.debug("connected at height: %i", height)
            for header in chain:
                self.save_header(header)
            return True
        except BaseException as e:
            log.exception("error saving chain")
            return False

    def need_previous(self, header):
        """Return True if we're missing the block before the one we just got"""
        previous_height = header['block_height'] - 1
        previous_header = self.read_header(previous_height)
        # Missing header, request it
        if not previous_header:
            return True
        # Does it connect to my chain?
        prev_hash = self.hash_header(previous_header)
        if prev_hash != header.get('prev_block_hash'):
            log.info("reorg")
            return True

    def connect_chunk(self, idx, hexdata):
        try:
            data = hexdata.decode('hex')
            self.verify_chunk(idx, data)
            log.info("validated chunk %i", idx)
            self.save_chunk(idx, data)
            return idx + 1
        except BaseException as e:
            log.error('verify_chunk failed: %s', str(e))
            return idx - 1

    def check_bits(self, bits):
        bitsN = (bits >> 24) & 0xff
        assert 0x03 <= bitsN <= 0x1f, \
            "First part of bits should be in [0x03, 0x1d], but it was {}".format(hex(bitsN))
        bitsBase = bits & 0xffffff
        assert 0x8000 <= bitsBase <= 0x7fffff, \
            "Second part of bits should be in [0x8000, 0x7fffff] but it was {}".format(bitsBase)


class UlordTest(Ulord):
    BLOCKCHAIN_NAME = "ulord_testnet"


class UlordReg(Ulord):
    BLOCKCHAIN_NAME = "ulord_regtest"

    def check_bits(self, bits):
        pass


def get_blockchain(config, network):
    chain = config.get('chain', 'ulord_main')

    # set the script and pubkey address prefixes depending on which chain is being used
    script_address = blockchain_params[chain]['script_address']
    script_address_prefix = blockchain_params[chain]['script_address_prefix']
    pubkey_address = blockchain_params[chain]['pubkey_address']
    pubkey_address_prefix = blockchain_params[chain]['pubkey_address_prefix']

    ulord.SCRIPT_ADDRESS = (script_address, script_address_prefix)
    ulord.PUBKEY_ADDRESS = (pubkey_address, pubkey_address_prefix)

    if chain == 'ulord_main':
        return Ulord(config, network)
    elif chain == 'ulord_test':
        return UlordTest(config, network)
    elif chain == 'ulord_regtest':
        return UlordReg(config, network)
    else:
        raise ValueError('Unknown chain: {}'.format(chain))



class ArithUint256(object):
    def __init__(self, value):
        self._value = value

    def __str__(self):
        return hex(self._value)

    @staticmethod
    def fromCompact(nCompact):
        """Convert a compact representation into its value"""
        nSize = nCompact >> 24
        # the lower 23 bits
        nWord = nCompact & 0x007fffff
        if nSize <= 3:
            return nWord >> 8 * (3 - nSize)
        else:
            return nWord << 8 * (nSize - 3)

    @classmethod
    def SetCompact(cls, nCompact):
        return cls(ArithUint256.fromCompact(nCompact))

    def bits(self):
        """Returns the position of the highest bit set plus one."""
        bn = bin(self._value)[2:]
        for i, d in enumerate(bn):
            if d:
                return (len(bn) - i) + 1
        return 0

    def GetLow64(self):
        return self._value & 0xffffffffffffffff

    def GetCompact(self):
        """Convert a value into its compact representation"""
        nSize = (self.bits() + 7) // 8
        nCompact = 0
        if nSize <= 3:
            nCompact = self.GetLow64() << 8 * (3 - nSize)
        else:
            bn = ArithUint256(self._value >> 8 * (nSize - 3))
            nCompact = bn.GetLow64()
        # The 0x00800000 bit denotes the sign.
        # Thus, if it is already set, divide the mantissa by 256 and increase the exponent.
        if nCompact & 0x00800000:
            nCompact >>= 8
            nSize += 1
        assert (nCompact & ~0x007fffff) == 0
        assert nSize < 256
        nCompact |= nSize << 24
        return nCompact

    def __mul__(self, x):
        # Take the mod because we are limited to an unsigned 256 bit number
        return ArithUint256((self._value * x) % 2 ** 256)

    def __idiv__(self, x):
        self._value = (self._value // x)
        return self

    def __gt__(self, x):
        return self._value > x

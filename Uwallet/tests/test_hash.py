import unittest
from uwallet.hashing import *
from uwallet.util import *
from uwallet.blockchain import ArithUint256

def _serialize_header(block):
    s = int_to_hex(block.get('version'), 4) \
        + rev_hex(block.get("prev_block_hash")) \
        + rev_hex(block.get('merkle_root')) \
        + rev_hex(block.get('claim_trie_root')) \
        + int_to_hex(int(block.get('timestamp')), 4) \
        + int_to_hex(int(block.get('bits')), 4) \
        + rev_hex(block.get('nonce'))
    return s

def _hash_head(header):
    return hash_encode(Hash_Header(_serialize_header(header).decode('hex')))

def _check_bits(bits):
    bitsN = (bits >> 24) & 0xff
    assert 0x03 <= bitsN <= 0x1f, \
        "First part of bits should be in [0x03, 0x1d], but it was {}".format(hex(bitsN))
    bitsBase = bits & 0xffffff
    assert 0x8000 <= bitsBase <= 0x7fffff, \
        "Second part of bits should be in [0x8000, 0x7fffff] but it was {}".format(bitsBase)

def _bits_to_target(rex):
    value = ArithUint256.SetCompact(rex)
    return value.GetCompact(), value._value

class testBlock(unittest.TestCase):
    """Test Data(test net)"""
    def setUp(self):
        self.block_hash = "00008bbb342842864268226c38d4d098fdc8d03876bd1ad8c01dc5cb83d604e2"
        self.block_height = 100
        self.block_data = {}
        self.block_data['version'] = 536870912
        self.block_data['prev_block_hash'] = '0001848025e662b2c30be12f36b5e5eae73533327fae5f48423b3930f3b5b0de'
        self.block_data['merkle_root'] = "90dc8866d7e1299c94a2cd0697b6415b34538717ba88d7ab0afdc1f3c7bb2d77"
        self.block_data['claim_trie_root'] = "0000000000000000000000000000000000000000000000000000000000000001"
        self.block_data['timestamp'] = "1520317574"
        self.block_data['bits'] = 0x1f0317bc
        self.block_data['nonce'] = "0000ed86477da1b1347f503a268eb33f107fd9af559acf47c2396bd7b3159ebe"

class TestHash(testBlock):
    """Test block hash"""
    def test_blockheader_hash(self):
        result = _hash_head(self.block_data)
        expected = self.block_hash
        self.assertEqual(expected, result)

    def test_check_bits(self):
        bits = self.block_data['bits']
        bitsN = (bits >> 24) & 0xff
        # First part of bits should be in [0x03, 0x1d]
        self.assert_ (0x03 <= bitsN <= 0x1f)
        bitsBase = bits & 0xffffff
        # Second part of bits should be in [0x8000, 0x7fffff]
        self.assert_(0x8000 <= bitsBase <= 0x7fffff)

    def test_verify_target(self):
        bits = int(self.block_data['bits'])
        _, target = _bits_to_target(bits)
        int_hash = int('0x' + self.block_hash, 16)
        self.assertTrue(int_hash <= target)

if __name__ == '__main__':
    unittest.main()




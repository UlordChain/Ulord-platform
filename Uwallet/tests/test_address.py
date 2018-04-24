import unittest
from uwallet.ulord import public_key_to_bc_address, address_from_private_key
from uwallet.ulord import public_key_from_private_key

class TestBaseAddr(unittest.TestCase):
    def setUp(self):
        self._pubk = "03d485f9129050d203241ca1227adbfd9c05fe881468642959e52a7f15930eba"
        self._addr = "uge36eCzRfG5MKDsNhPgFk13UbJZgeN3ty"
        self._privk = "L2hzM95TB9fHU3RNCkQXSqsTBSfzZeS5FaGq2ZeModXjzB4UqF9F"


class TestTestNetAddress(TestBaseAddr):
    """Test address"""
    def test_pubkey_to_addr(self):
        """Test pubkey to address """
        pubkey = self._pubk.decode("hex")
        result = public_key_to_bc_address(pubkey)
        expected = self._addr
        self.assertEqual(expected, result)

    def test_prvkey_to_pubkey(self):
        """Test privkey to pubkey"""
        result = public_key_from_private_key(self._privk)
        expected = self._pubk
        self.assertEqual(expected, result)

    def test_privkey_to_addr(self):
        """Test privkey to address"""
        result = address_from_private_key(self._privk)
        expected = self._addr
        self.assertEqual(expected, result)


if __name__ == '__main__':
    unittest.main()




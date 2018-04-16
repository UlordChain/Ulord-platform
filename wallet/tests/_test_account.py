import unittest
import copy
from uwallet import bip32
from uwallet import wallet


class Test_Account(unittest.TestCase):
    def setUp(self):
        self.v = {
            "change": [
                "02c02a34f4810401466ead90470834d9233a72c562e9f3659655066d9b44c7d886", 
                "03b0d76e1d5e371d0a4e864a383c942562d77aff87ce160447973d767555fac486", 
                "0274e1ebc5cfb9162b002b4ae36e2fb36186a05482d3a5ff573091cc0b800c7999", 
                "02ee13b8afd4ff76a1403b904f3c3e4676cb727d3fb6eb7bc6d1f1cfe63a8c7685", 
                "031becdde582967c5b0ccc9cd117962aab9d09ec5ab44a70c9e904f620e62ee827", 
                "022242cf1aeba4bc0de0ddbb7a2b7d743d1a21a5a7ad258f4ef57f2d5bb3dce49d"
            ], 
            "receiving": [
                "02bdd5f5147c30a0d5f04ba459e35d026daee6992fc65d28b3aca32aa3a4a213ec", 
                "02a98017a7ab177cc3748bbe79aba062c654a920ef65a58076cee16c14046bbd25", 
                "02c4003b60d05d77deb45e3a43847657ff22aa48105122795e7a694d93c2260156", 
                "03022b75f1df4296af96c7be527878053e46c65a62e5a881fa26c23ade0429242e", 
                "03ea3b0067ae01eb2aa9fbcfb5950ffa18c81d6dd75cc21debae38b0d9b8e26a8c", 
                "0287450430410b13c03d540c3dcf1fc17a2eb39c31d330878c403f0ea2f222cd61", 
                "02194a3f35c6a6becbf4a4fef5496571017a73472ebc4cb29770c55fee4da08067", 
                "03aafb384f1780fa632a984c519f7c9935936ea44111d00ff9f8b81656c3ba8696", 
                "037eed7b1c7026e167c98e3a0a9e1cdbfbedda4b1bcf9d4d4e8198432d35c00149", 
                "02663ba42033ebeaa83ea2e8720550b90cb6b5662c73b95eb1e9d2890c110a0f99", 
                "028daecbce8333f19aef43675f8981f50a7f12593f267365020cd84a71981f7509", 
                "0285d0d28b34c6ececacfc8b0d8357a53e1b984629c619899c9eeba51bde726bfd", 
                "023a770ba7bd4a65c4a6e400c11c189a4479e123f9cbd16f477a714fb0ca3b4a37", 
                "02e5de516fa8de73fbd669767f8fcca8ac9c05a5c65bf6cd9ff236691023f8c0e9", 
                "02a4b37671f80b30ce16718c8c60709ed7f293e0302689a851e6f85aacb0ba3182", 
                "0213d2e39a9a94c8dc3e3e724cf20af1abedba8d2c508ee31e0707b18c8999d4f8", 
                "03af9f063c294fd1e5a1d40af2c7d161b0eb1195dc08ed60ee29263192dc86fdc2", 
                "031daf61b5002f4451e905d29559cb043b9a0dec8c8c34e52748cb1ce550c8108d", 
                "02b29d615bea1c00fd58d47772bd371a8decd4c4a87764b3d6a876d4f5e75c73d2", 
                "03a9bcf257630c876c02e59203ea9a4773defd6f1d865a90274bd0e74bf09c7126"
            ],
            'xpub': "xpub661MyMwAqRbcFvqKUU56A7Hvvaigqbo93LwgxBEikMjpwzWKd5fswRcrKGb9Ry3kNqbVi1CphTvqPAqLL2VnULAQSrnKAVkFXP3hxHSZAtA"
        }

    def test_bip32_account(self):

        a = bip32.BIP32_Account(self.v)
        self.assertEquals(a.dump(), self.v)
        self.assertEquals(a.get_master_pubkeys(), [self.v['xpub']])
        self.assertEquals(a.first_address(),
                          ('UNPXzrUQV8RvkiRisLcxT66ic9T5nBacU1', self.v['receiving'][0]))

        xprv = 'xprv9s21ZrQH143K3SkrNSY5nyMCNYtCS95Hg8269nq7C2Cr5CBB5YMdPdJNU1eEL4EFoz4ibeDzDp6r37ofe4vsawGnqAUYu3pgqpSjuGq39S8'
        storage = dict(
            master_public_keys={0: a.xpub},
            master_private_keys={0: xprv},
            wallet_type='standard'
        )
        w = wallet.BIP32_Wallet(storage)
        print a.get_private_key(sequence=[0, 0], wallet=w, password=None)
        self.assertEquals(a.get_private_key(sequence=[0, 0], wallet=w, password=None),
                          ['KxuBFG13CPUBwPAUWvZSQ3mjNNjHoDghfxnax6RbwS3Rw8tqSzCk'])


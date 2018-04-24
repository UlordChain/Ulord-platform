#-*- coding: utf-8 -*-
import pylint
import unittest
from unetschema.claim import ClaimDict
from test_data import example_010


"""
1. dict      (python_object_dict)
2. ClaimDict (python_object_class)
3. Claim_pb     (protobuf_claim)
4. Claim_pb_dict (python_object_dict_claim_base64encode)
5. Claim_serialized  (hex_string)

    @classmethod
    ClaimDict.load_dict(claim_dict)               # dict     -> ClaimDict
    ClaimDict.load_protobuf(claim_pb)             # Claim_pb -> ClaimDict
    ClaimDict.load_protobuf_dict(claim_pb_dict)   # Claim_pb_dict -> ClaimDict
    ClaimDict.deserialize(serialized_string)      # Claim_serialized_string -> ClaimDict

    @property
    claim_dict(self)                # ClaimDict -> dict   # 请求字典以字节表示为十六进制和base58
    protobuf(self)                  # ClaimDict -> Claim_pb  
    protobuf_dict(self)             # ClaimDict -> Claim_pb_dict
    serialized()                    # ClaimDict -> Claim_serialized_string

"""

class TestEncoderAndDecoder(unittest.TestCase):
    def setUp(self):
        self.maxDiff = 5000
        # dict(hex|base58 encoded) -> ClaimDict(decoded)
        self.claimDict = ClaimDict.load_dict(example_010)
        # ClaimDict -> Claim_pb( protobuf )
        self.claim_pb = self.claimDict.protobuf
        # ClaimDict -> Claim_serialized_string(The data doesn't use hexadecimal encoding.)
        self.claim_serialized_string = self.claimDict.serialized

    def test_verify_data(self):
        # ClaimDict -> dict
        self.assertDictEqual(self.claimDict.claim_dict, example_010, "ClaimDict->dict")
        # Claim_pb -> dict
        self.assertDictEqual(ClaimDict.load_protobuf(self.claim_pb).claim_dict, example_010)
        # claim_serialized_string -> ClaimDict
        self.assertEqual(ClaimDict.deserialize(self.claim_serialized_string), self.claimDict)

        # dict -> ClaimDict->Claim_pb
        self.assertEqual(ClaimDict.load_dict(example_010).protobuf, self.claim_pb)
        # dict -> ClaimDict -> dict
        self.assertEqual(ClaimDict.load_dict(example_010).claim_dict, example_010)
        # dict -> ClaimDict ->Claim_pb_dict -> ClaimDict
        self.assertEqual(ClaimDict.load_protobuf_dict(ClaimDict.load_dict(example_010).protobuf_dict), self.claimDict)
        # dict -> ClaimDict -> Claim_pb_dict ->ClaimDict ->dict
        self.assertEqual(ClaimDict.load_protobuf_dict(ClaimDict.load_dict(example_010).protobuf_dict).claim_dict, example_010)



    def test_is_certificate(self):
        self.assertFalse(ClaimDict.load_dict(example_010).is_certificate, "Is_certificate")

    def test_verify_length(self):
        self.assertEqual(self.claim_pb.ByteSize(), ClaimDict.load_protobuf(self.claim_pb).protobuf_len)
        self.assertEqual(self.claimDict.json_len, ClaimDict.load_protobuf(self.claim_pb).json_len)


    def test_deserialize(self):
        serialized_claim_hex = self.claim_serialized_string.encode('hex')
        # decode -> deserialize
        self.assertEqual(ClaimDict.deserialize(serialized_claim_hex.decode('hex')), self.claimDict)

    def test_stream_is_not_certificate(self):
        deserialized_claim = ""

if __name__ == '__main__':
    unittest.main()

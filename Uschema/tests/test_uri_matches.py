import unittest
import ecdsa
from copy import deepcopy

from test_data import claim_id
from unetschema.uri import URI, URIParseError


parsed_uri_matches = [
    ("test", URI("test"), False, "string"),                                          #   string          
    ("test#%s" % claim_id, URI("test", claim_id=claim_id), False,"string#claim_id"), ##  string#claim_id 
    ("test:1", URI("test", claim_sequence=1), False, "string:sequence"),             #:  string:number   
    ("test$1", URI("test", bid_position=1), False, "string$bid_position"),           #$  string:number   

    ("unet://test", URI("test"), False, "unet:string"),
    ("unet://test#%s" % claim_id, URI("test", claim_id=claim_id), False, "unet:string#claim_id"),
    ("unet://test:1", URI("test", claim_sequence=1), False, "unet:string:sequence"),
    ("unet://test$1", URI("test", bid_position=1), False, "unet:string:bid_position"),

    ("@test", URI("@test"), True, "channel: @string"),
    ("@test#%s" % claim_id, URI("@test", claim_id=claim_id), True, "channel: @string#claim_id"),
    ("@test:1", URI("@test", claim_sequence=1), True, "channel: @string:sequence"),
    ("@test$1", URI("@test", bid_position=1), True, "channel: @string$bid_position"),

    ("unet://@test1:1/fakepath", URI("@test1", claim_sequence=1, path="fakepath"), True, "unet:@string:sequence/path"),
    ("unet://@test1$1/fakepath", URI("@test1", bid_position=1, path="fakepath"), True, "unet:@string$bid_pos/path"),
    ("unet://@test1#abcdef/fakepath", URI("@test1", claim_id="abcdef", path="fakepath"), True, "unet@string#claim_id/path"),

    ("@aa",   URI("@aa"),   True, "channel namespace: @aa"),
    ("@fff",  URI("@fff"),  True, "channel namespace: @fff"),
    ("@zzzz", URI("@zzzz"), True, "channel namespace: @zzzz")
]



class UnitTest(unittest.TestCase):
    maxDiff = 4000

class TestURIParse(UnitTest):
    def setUp(self):
        self.longMessage = True

    def test_uri_parse(self):
        for test_string, expected_uri_obj, is_channel, debug_log in parsed_uri_matches:
            try:
                # string -> URI
                self.assertEqual(URI.from_uri_string(test_string), expected_uri_obj, debug_log)
                # URI -> dict -> URI
                self.assertEqual(URI.from_dict(expected_uri_obj.to_dict()), expected_uri_obj, debug_log)
                # is_channel
                self.assertEqual(URI.from_uri_string(test_string).is_channel, is_channel, debug_log)
            except URIParseError as err:
                print "Error: " + debug_log
                raise

            if test_string.startswith("unet://"):
                # string -> URI -> string
                self.assertEqual(URI.from_uri_string(test_string).to_uri_string())


if __name__ == '__main__':
    unittest.main()
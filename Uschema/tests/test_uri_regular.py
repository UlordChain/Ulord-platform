import unittest
from test_data import claim_id
from unetschema.uri import URI, URIParseError


# test_string, expected_uri_obj, is_channel, err_info
parsed_uri_matches = [
    ("test", URI("test"), False, "string"),                                          
    ("test#%s" % claim_id, URI("test", claim_id=claim_id), False,"string#claim_id"), 
    ("test:1", URI("test", claim_sequence=1), False, "string:sequence"),             
    ("test$1", URI("test", bid_position=1), False, "string$bid_position"),           

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

# Judging the regular expression, can correctly throw an exception.
parsed_uri_raises = [
    ("unet://", URIParseError),
    ("unet:/", URIParseError),
    ("unet:///", URIParseError),
    ("unet//", URIParseError),
    ("unet:/test", URIParseError),
    ("unet:///test", URIParseError),
    ("unet//test", URIParseError),

    ("unet://test:1#%s" % claim_id, URIParseError),
    ("unet://test:3$1", URIParseError),
    ("unet://test$1:1", URIParseError),
    ("unet://test#x", URIParseError),
    ("unet://test#x/page", URIParseError),

    ("unet://@", URIParseError),
    ("unet://te@st", URIParseError),
    ("unet://test@", URIParseError),
    ("unet://@@test", URIParseError),
    ("unet://@te@st/", URIParseError),
    ("unet://@test@/", URIParseError),
    ("unet://@test:", URIParseError),
    ("unet://@test#", URIParseError),
    ("unet://@test$", URIParseError),
    ("unet://@test/", URIParseError),

    ("unet://test:", URIParseError),
    ("unet://test:0", URIParseError),
    ("unet://test:a", URIParseError),
    #("unet://test:-1", URIParseError),
    ("unet://test:1:1", URIParseError),
    ("unet://test:0#%s" % claim_id, URIParseError),
    ("unet://test:0$1", URIParseError),

    ("unet://test$`", URIParseError),
    ("unet://test$0", URIParseError),
    ("unet://test$a", URIParseError),
    #("unet://test$-1", URIParseError),
    ("unet://test$1#%s" % claim_id, URIParseError),
    ("unet://test$1:1", URIParseError),
    ("unet://test$1$1", URIParseError),

    ("unet://test/path", URIParseError),
    ("unet://@test1#abcdef/fakepath:1", URIParseError),
    ("unet://@test1:1/fakepath:1", URIParseError),
    ("unet://@test1:1ab/fakepath", URIParseError),
    ("unet://test:1:1:1", URIParseError),
    ("whatever/unet://test", URIParseError),
    ("unet://unet://test", URIParseError),
    ("unet://❀", URIParseError),
    ("unet://@/what", URIParseError),
    ("unet://test:0x123", URIParseError),
    ("unet://test:0x123/page", URIParseError),
    ("unet://@test1#ABCDEF/fakepath", URIParseError),
    ("test:0001", URIParseError),
    ("unet://@test1$1/fakepath?arg1&arg2&arg3", URIParseError)
]

class UnitTest(unittest.TestCase):
    # This attribute controls the maximum length of diffs output by assert methods that report diffs on failure.
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
                if test_string.startswith("unet://"):
                    # string -> URI -> string
                    self.assertEqual(URI.from_uri_string(test_string).to_uri_string(), test_string, debug_log)
                    # string -> URI ->dict -> URI - >string
                    uri_dict = URI.from_uri_string(test_string).to_dict()
                    self.assertEqual(URI.from_dict(uri_dict).to_uri_string(), test_string, debug_log)
                    # URI -> Dict > URI -> string
                    self.assertEqual(URI.from_dict(expected_uri_obj.to_dict()).to_uri_string(), test_string, debug_log)
            except URIParseError as err:
                print "Error: " + debug_log
                raise

    def test_uri_errors(self):
        for test_str, err in parsed_uri_raises:
            try:
                URI.from_uri_string(test_str)
            except URIParseError:
                pass
            else:
                print ("\n Successfully parsed invalid url: " + test_str )
            self.assertRaises(err, URI.from_uri_string, test_str)


if __name__ == '__main__':
    unittest.main()
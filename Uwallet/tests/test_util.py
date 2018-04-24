import  unittest
from uwallet.util import format_satoshis

class TestUtil(unittest.TestCase):
    def test_format_satoshis_positive(self):
        result = format_satoshis(1357)
        expected = "0.00001357"
        self.assertEqual(expected, result)

    def test_format_satoshis_negative(self):
        result = format_satoshis(-1234)
        expected = "-0.00001234"
        self.assertEqual(expected, result)

    def test_fromat_satoshis_diff_positive(self):
        result = format_satoshis(2468,is_diff=True)
        expected = "+0.00002468"
        self.assertEqual(expected, result)

    def test_format_satoshis_diff_negative(self):
        result = format_satoshis(-1234, is_diff=True)
        expected = "-0.00001234"
        self.assertEqual(result, expected)

if __name__ == '__main__':
    unittest.main(verbosity=2)
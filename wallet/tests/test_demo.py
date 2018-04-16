import unittest


class TestClassBase(unittest.TestCase):
    """Test Data"""
    def setUp(self):
        pass

class TestClassName(TestClassBase):
    """Test block hash"""
    def test_func_name(self):
        result = ""
        expected = "-0.00001234"
        self.assertEqual(expected, result)


if __name__ == '__main__':
    unittest.main()

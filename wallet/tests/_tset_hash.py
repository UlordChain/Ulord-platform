import hashlib


def sha2561(x):
    return hashlib.sha256(x).digest()

def hex_to_int(s):
    return int('0x' + s[::-1].encode('hex'), 16)

def hash_encode(x):
    #return x[::-1].encode('hex')
    return x.encode('hex')

def Hash(x):
    if type(x) is unicode:
        x = x.encode('utf-8')
    return sha2561(sha2561(x))

def int_to_hex(i, length=1):
    s = hex(i)[2:].rstrip('L')
    s = "0" * (2 * length - len(s)) + s
    return rev_hex(s)

def rev_hex(s):
    return s.decode('hex')[::-1].encode('hex')

def serialize_header(res):
    s = int_to_hex(res.get('version'), 4) \
        + rev_hex(res.get('prev_block_hash')) \
        + rev_hex(res.get('merkle_root')) \
        + rev_hex(res.get('claim_trie_root')) \
        + int_to_hex(int(res.get('timestamp')), 4) \
        + int_to_hex(int(res.get('bits')), 4) \
        + int_to_hex(int(res.get('nonce')), 4)
    return s

def hash_header(header):
    if header is None:
        return '0' * 64
    return hash_encode(Hash(serialize_header(header).decode('hex')))


def test_hash():
    d = hashlib.sha256(b"hello")
    d.hexdigest()
    d2 = hashlib.sha256()
    d.hexdigest()
    d2.update(d.digest())
    return d2.hexdigest()



def main():
    h1 = {}
    h1['version'] = 1
    h1['prev_block_hash'] = '0000000000000000000000000000000000000000000000000000000000000000'
    h1['merkle_root'] = '5618cc716bc1ad1584d19fe1ffe5169761ebf60bd2bcd19b91b337549eab8159'
    h1['claim_trie_root'] = '0000000000000000000000000000000000000000000000000000000000000001'
    h1['timestamp'] = 1511420100
    h1['bits'] = 520617983
    h1['nonce'] = '0000000000000000000000000000000000000000000000000000000000000307'
    h1['solution'] = '0066ab39b51ecdc15a4df15260c4c9086b3955876d13233c3bab647fd3d49d44495cbe77dd1974b4701d1344918e71ccaaed2cac8136f1bab8d118e130cd0c2e923760ad11b441b8a0182c6afa44770a44fbfb7000b84a2e9eced1c55cdee02e9aed8746b4f7fe000f051f698523e04aa5cd126c63a3e4f1e76707fe1f4906042f23e723b79976334824257347863c75ff58242cc2eb9961ab59915f0cd6a712f77742896674724f1243d129c373f0f3d7152b755f6b3b66ff70985f5d3c1ceee182142eb795e6e7e3116c3bee739d9bce7e1cc77edd21aac601c21e561fe2f40e5d9ccc8e4100219c4ac21bde3d1bd296b3d511e7724f4c32fc0f2e187d8d6aaa4f56c0f74f35cfc9ebe681f753bc17e0505e9e1e99da4f3d7313292dde4e711b65cddba22e225fe5c359da2d8eff2a67ab6c59106329251cbcc4293a56288d52dd72fbf542fe5dc0120a69949a20d202e6a9ca5a468ca3fc1150b4776a286a16a371136313e42e143712da3145702494534cb9c5ffebbe14861091a390810bb7b728db81d40518a4d72bb13ff1752449f5b0ad4dcf127c66e255f4ee08a6e9d0fa4d9e039b9bc7268f4895f476610616db9e60dfac4f429d06e80778f390f9cb4100a3f0bce39f4914018b61fa0b6d992bd98da940f00431d811d0a99920c91464cc15071daf899e3fdd853708ea93d7c98253591b119c062e337a689070b9a5bce5ca08458d06a4227b32661e1a52b2b490a292a00d95205e29a295c42e33b1f014a0b201e012d0499e9ec5b4e1b44cefc2ec1f2d056eb31cfe9e2282ed1e03c8a4977551465dc2bc92820e54f8fb3c4bc1ed8fcaa2fd5e24b9c20b8bdd45781fb0f43ac6ca1b44ee6942d52e77d62628b9b2aa642d4a2f3b6c1f34dbed4ec5a81ce72f0ef1f51ced874fec85a6cbe68ec9a65935884adf3182054cf0c9f6018da251c04a7117694c118229ca5810a4958634322a572da9099c6fa352a6f3d4b4d0742d1ff01896ae3c4494b7b5b48685b6bd96a91b5eb0ee39727fdfba3e00aa34fe5b73034f9ec42c955b1b9ab0935c1af001910e63364304fd1bacb312f4674d11da1396eeec023023633ce71ff7b02883289a5b4e190a27ce2f7f0952b6a8890de53db56d861a9146e99e3be9ddc1c968e86e98b7e1201338a736cdc873194a13449e875201c748c4c2874ff4cba6c31321a08f5e96b93db9253ee91f22d29091dd387b15a818eb05e753f1bff5ea09ac5ca7eb82b16bf170234dab5eeb4947d8334be50cedc15663a69fe979428134ca3fcce96ff5ebd10b05bc53fd6ed8a5d5d5ea0d6c3579310b606a1c388a1e6de2b07d57e321e47ff669da51bd3e42f09fdf37262c0633a1d0756399c11611d8714477566abe41082642bdf6be8f40e520a562e602533ecdb2d75c494e0389330767218299604ae2db31cbd3dd4c1a571df747b996fa9de5fc8344ebf534c6cee60ea7a4fa5f0b16e431acbfdb7ddb5680b2ed483100bd756cf5fb301f4e0c28844c5168db9882be6c2d40353b7816d498091997ae5590ad6d228a632177d424b167be188e971261d7393410ce5f82cdc2fb08a5193ad396db63db19116296ec095bd7c324c1d28b2f96a9369b7237dc49d7f609d9180a318cc0f7d77842efda06fe537e3d0d3ef4b125100a02c15d52a631553e718eb5b839bc175b3535c45cca817154f66c1dcaf22e565f7ac8fa18a2b21b73ea7275669fe633166a9ba350289b1df94cb5770b6ed472fbfd15a552ee48e8cad8b816f42d1f1d3df56a2b9fada96be33beb404405bdc63909c23cdbb7d8009135e991caa61b1ee29f71c02b7e611827e2327e628e07e74f93e35fa057e67d0ee089eede29a9db0e5d8b0f1f40144353c6268e8f5bfabb6f04'


    hashed_head = hash_header(h1)
    print ("hashed_head", hashed_head)

    my_hash = hash_encode(Hash(b'hello'))
    print my_hash

    test_hello = test_hash()
    print ("test_hello", test_hello)


    str1 = 'abcdef'
    str2 = str1[::-1]
    print str2

if __name__ == '__main__':
    main()

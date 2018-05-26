# -*- coding: utf-8 -*-
# @Date    : 2018/5/16
# @Author  : Shu
# @Email   : httpservlet@yeah.net
# Use pycryptodome instead of pycrypto.
import os, base64
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5


class RSAHelper(object):
    def __init__(self):
        dirpath = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'datas')
        pubkeypath = os.path.join(dirpath, 'ulord_private_rsa_key.bin')
        prikeypath = os.path.join(dirpath, 'ulord_public_rsa_key.pem')
        if os.path.isfile(pubkeypath) and os.path.isfile(prikeypath):
            # print('if')
            with open(pubkeypath) as f:
                self.pubkeybytes = f.read()
                self.pubkey = RSA.import_key(self.pubkeybytes)
            with open(prikeypath) as f:
                self.prikeybytes = f.read()
                self.prikey = RSA.import_key(self.prikeybytes)
        else:
            # print('else')
            self.prikey = RSA.generate(1024)
            self.prikeybytes = self.prikey.export_key()
            with open(prikeypath, 'wb') as f:
                f.write(self.prikeybytes)
            self.pubkey = self.prikey.publickey()
            self.pubkeybytes = self.pubkey.export_key()
            with open(pubkeypath, 'wb') as f:
                f.write(self.pubkeybytes)

    def _encry(self, msg):
        cipher = PKCS1_v1_5.new(self.pubkey)
        return cipher.encrypt(msg)

    def _decrypt(self, msg):
        cipher = PKCS1_v1_5.new(self.prikey)
        return cipher.decrypt(msg, None)

    def encry(self, msg):
        msg = self._encry(msg)
        return base64.b64encode(msg)

    def decrypt(self, msg):
        msg = base64.b64decode(msg)
        return self._decrypt(msg)


rsahelper = RSAHelper()

if __name__ == '__main__':
    msg = rsahelper.encry(b'123')
    print(msg)
    print(rsahelper.decrypt(b'O0UqkRjcdFyF8+vfVO2YjsMwh72ymESjlgqygXef+LlBJ+KaMlAVXbqLXgk9BuAv+9lDCE/T26gnoTUMAcqRrToyoZZE3Kv+D2QPTK+S5jBxf7s6I0q3fN0P3rtrPvDnU6SKISpiLpL/hptyZNrHo2kA5KoF/Q/p2upLSS8kKAE='))

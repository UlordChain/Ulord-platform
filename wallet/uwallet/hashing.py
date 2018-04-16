#-*- coding: UTF-8 -*-
import os
import sys
sys.path.append(os.path.realpath('.'))

import hashlib
import hmac
from cryptohello_hash import cpu_has_aes_in_supported,cryptohello_hash

HAS_AES_NI = cpu_has_aes_in_supported()

def sha256(x):
    return hashlib.sha256(x).digest()


def sha512(x):
    return hashlib.sha512(x).digest()


def ripemd160(x):
    h = hashlib.new('ripemd160')
    h.update(x)
    return h.digest()


def Hash(x):
    if type(x) is unicode:
        x = x.encode('utf-8')
    return sha256(sha256(x))

#new hash instead of doublehash
def Hash_Header(x):
    if type(x) is unicode:
        x = x.encode('utf-8')
    return cryptohello_hash(x,HAS_AES_NI)

def PowHash(x):
    if type(x) is unicode:
        x = x.encode('utf-8')
    return sha256(sha256(x))




def hash_encode(x):
    return x[::-1].encode('hex')


def hash_decode(x):
    return x.decode('hex')[::-1]


def hmac_sha_512(x, y):
    return hmac.new(x, y, hashlib.sha512).digest()


def hash_160(public_key):
    md = hashlib.new('ripemd160')
    md.update(sha256(public_key))
    return md.digest()

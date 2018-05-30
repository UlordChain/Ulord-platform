# -*- coding: utf-8 -*-
# @Date    : 18-5-24 上午2:03
# @Author  : hetao
# @Email   : 18570367466@163.com
# Copyright (c) 2016-2018 The Ulord Core Developers
import time

from utasks.wallet_tasks import publish, update_claim


def profiler(func):
    def do_profile(*args, **kw_args):
        n = func.func_name
        t0 = time.time()
        o = func(*args, **kw_args)
        t = time.time() - t0
        print "[profiler] %s %f" % (n, t)
        return o

    # return lambda *args, **kw_args: do_profile(func, args, kw_args)
    return do_profile

def print_result(*args):
    print('callback print: ---------')
    print(args)
    print('-------------------------')

def get_publish_params():
    """

    :return: {u'fee': u'0.000359', u'success': True, u'tx': u'01000000026cd6a90cd9e7d486aa9e52261bf74969181ac7a691a398df243bfccb24daee7d000000006a47304402205c07adc5ab1f5ae4830f8c2a71355e10fa647bd598ba55ce95f2cb326c8d3f8e02205f524450cb52d35dd91cac39e2ff8cce66444bd9e21087c367a49ee234b715e0012102521dcd489d7740aac35616320091cad6656b127a86a7a84fbe7622557ad14be6ffffffff6cd6a90cd9e7d486aa9e52261bf74969181ac7a691a398df243bfccb24daee7d010000006b483045022100f1061ea1eef46c97c2e9d3d511523ddb1753687283848bc578d4301a90bc6d37022002dd3868ad8e5931000b7d9c8f6b109385dbc25e2c0c5dfb3b26442929acdf49012102521dcd489d7740aac35616320091cad6656b127a86a7a84fbe7622557ad14be6ffffffff023fc0650300000000fd3a01b70968657431616f31313114e190209f6b4e6d5d3ea17c191d75d7a5e01f97504cfd080110011af601080112bc01080410011a0d57686174206973204c4252593f223057686174206973204c4252593f20416e20696e74726f64756374696f6e207769746820416c6578205461626172726f6b2a0c53616d75656c20427279616e32084c42525920496e6338004224080110011a19824fe527d791ec681899527ab5902de65c247e84792da6ac4c259a99993f4a2f68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f66696c65732e6c6272792e696f2f6c6f676f2e706e6752005a0060021a33080110011a2212206c10c5cece33226e2cc5c56d999fd7874494a1bebf47be0416a09bf53b8954262209766964656f2f6d70346d6d76a9144fe527d791ec681899527ab5902de65c247e847988ac5871263f170000001976a9144fe527d791ec681899527ab5902de65c247e847988ac00000000', u'txid': u'325dbf4ed36ccc50f84118e322b1452aeb0f385d71f5accbd326ecd4df3df121', u'amount': u'0.56999999', u'claim_id': u'50971fe0a5d7751d197ca13e5d6d4e6b9f2090e1', u'nout': 0}
    """
    metadata = {
        "license": "LBRY Inc",
        "description": "update test",
        "language": "en",
        "title": "What is LBRY?",
        "author": "Samuel Bryan",
        "version": "_0_1_0",
        "nsfw": False,
        "licenseUrl": "",
        "preview": "",
        "thumbnail": "https://s3.amazonaws.com/files.lbry.io/logo.png",
        "tag": ["action", "sss"]
    }

    # sourceHash = "d5169241150022f996fa7cd6a9a1c421937276a3275eb912790bd07ba7aec1fac5fd45431d226b8fb402691e79aeb24b"
    sourceHash = "QmVcVaHhMeWNNetSLTZArmqaHMpu5ycqntx7mFZaci63VF"

    contentType = "video/mp4"

    currency = "UT"
    amount = 1.56
    bid = 0.59
    address = None
    tx_fee = None
    args = (metadata, contentType, sourceHash, currency, amount, bid, address, tx_fee)
    return args


if __name__ == '__main__':
    user = 'test_201805281100'
    password = '123'
    # password = 'pbkdf2:sha256:50000$oEw0SZX0$f8d9951addfa90213e63bb4553cacc7e3cc8e78d9d59f5e707da1fc09dd4d675'

    claim_name = 'test_201801588'
    claim_id = 'bcd560324378b3705c1ffe37fadfb11643181c77'
    txid = 'eef94b23bfc2f5c05133450e53028efc6de7533c178d7aa3e82f6fc32f56cd81'

    publish_args = get_publish_params()
    # # 异步调用
    # publish.delay(user, password, claim_name, *publish_args, skip_update_check=True, callback=print_result)  # 2.68
    # 等待结果返回
    # print(res.get())

    # 同步调用
    # publish(user, password, claim_name, *publish_args, skip_update_check=True, callback=print_result)

    update_claim(user, password, claim_name, claim_id, txid, 0, *publish_args, callback=print_result)
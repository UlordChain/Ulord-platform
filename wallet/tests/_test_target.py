from uwallet.blockchain import unet
from uwallet.blockchain import ArithUint256

GENESIS_BITS = 0x1f07ffff
MAX_TARGET = 0x0007FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
N_TARGET_TIMESPAN = 150

def check_bits(bits):
        bitsN = (bits >> 24) & 0xff
        assert 0x03 <= bitsN <= 0x1f, \
            "First part of bits should be in [0x03, 0x1d], but it was {}".format(hex(bitsN))
        bitsBase = bits & 0xffffff
        assert 0x8000 <= bitsBase <= 0x7fffff, \
            "Second part of bits should be in [0x8000, 0x7fffff] but it was {}".format(bitsBase)


def get_target(index, first, last, chain='main'):
    """
    this follows the calculations in lbrycrd/src/lbry.cpp
    Returns: (bits, target)
    """
    if index == 0:
        return GENESIS_BITS, MAX_TARGET
    assert last is not None, "Last shouldn't be none"
    # bits to target
    bits = last.get('bits')
    # print_error("Last bits: ", bits)
    self.check_bits(bits)

    # new target
    nActualTimespan = last.get('timestamp') - first.get('timestamp')
    nTargetTimespan = N_TARGET_TIMESPAN  #150
    nModulatedTimespan = nTargetTimespan - (nActualTimespan - nTargetTimespan) / 8
    nMinTimespan = nTargetTimespan - (nTargetTimespan / 8)
    nMaxTimespan = nTargetTimespan + (nTargetTimespan / 2)
    if nModulatedTimespan < nMinTimespan:
        nModulatedTimespan = nMinTimespan
    elif nModulatedTimespan > nMaxTimespan:
        nModulatedTimespan = nMaxTimespan

    bnOld = ArithUint256.SetCompact(bits)
    bnNew = bnOld * nModulatedTimespan
    # this doesn't work if it is nTargetTimespan even though that
    # is what it looks like it should be based on reading the code
    # in lbry.cpp
    bnNew /= nModulatedTimespan
    if bnNew > MAX_TARGET:
        bnNew = ArithUint256(MAX_TARGET)
    return bnNew.GetCompact(), bnNew._value


def verify_target(block):
    #bits = int('0x' + block.get('bits'), 16)
    bits = int(block.get('bits'),16)
    #print bits
    _, target = bits_to_target(bits)
    int_hash = int('0x' + block.get('hash'), 16)
    print ("int_hash: ", int_hash)
    print ("target  : ", target)

    if (int_hash <= target):
        print ("verify target success")
    else:
        print ("verify target failed")

def bits_to_target(rex):
    value = ArithUint256.SetCompact(rex) #rex: 0x1111
    return value.GetCompact(), value._value


def main():
    # 2 block . data from blockchain-cli.
    block_1 = {}
    block_1['hash'] = '00022b278c567c27569618ba94fcfff38f0e5cffbce90a99cea80bc5cab89724'
    block_1['bits'] = "1f07ffff"
    block_1['timestamp'] = 1511426385

    # 3 block 
    block_2 = {}
    block_2['hash'] = '0006b528e8d76c90056d1685ca2dd9959726c7bfce9871ebd511a5aa44de0905'
    block_2['bits'] = "1f07ffff"
    block_2['timestamp'] = 1511426707

    # 1000 block 
    block_3 = {}
    block_3['hash'] = '000447de95629ec2efa86cf95c2042491278ff49b822f12b701cb4c848051376'
    block_3['bits'] = "1f079a69"
    block_3['timestamp'] = 1511774706

    # 1001 block 
    block_4 = {}
    block_4['hash'] = '0007ac5bea8f2088135eb987462d85a82ae953257f5ee771f3f0631043da1148'
    block_4['bits'] = "1f07c913"
    block_4['timestamp'] = 1511774724

    bits, target = get_target(0, block_1, block_2)

    #to test bits to target.
    #bits_0, target_0 = bits_to_target(GENESIS_BITS)
    #print ("bits_0  :", bits_0)
    #print ("target_0:", target_0)

    verify_target(block_3)
    verify_target(block_4)



if __name__ == '__main__':
    main()

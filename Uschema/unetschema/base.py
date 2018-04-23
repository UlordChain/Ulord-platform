from unetschema.schema import B58_CHARS, ADDRESS_CHECKSUM_LENGTH
from unetschema.hashing import double_sha256
from unetschema.error import InvalidAddress


def b58decode(v):
    long_value = 0L
    for (i, c) in enumerate(v[::-1]):
        long_value += B58_CHARS.find(c) * (58 ** i)
    result = ''
    while long_value >= 256:
        div, mod = divmod(long_value, 256)
        result = chr(mod) + result
        long_value = div
    result = chr(long_value) + result
    nPad = 0
    for c in v:
        if c == B58_CHARS[0]:
            nPad += 1
        else:
            break
    return chr(0) * nPad + result


def b58encode(v):
    long_value = 0L
    for (i, c) in enumerate(v[::-1]):
        long_value += (256 ** i) * ord(c)
    result = ''
    while long_value >= 58:
        div, mod = divmod(long_value, 58)
        result = B58_CHARS[mod] + result
        long_value = div
    result = B58_CHARS[long_value] + result
    # Bitcoin does a little leading-zero-compression:
    # leading 0-bytes in the input become leading-1s
    nPad = 0
    for c in v:
        if c == '\0':
            nPad += 1
        else:
            break
    return (B58_CHARS[0] * nPad) + result


def validate_b58_checksum(addr_bytes):
    addr_without_checksum = addr_bytes[:-ADDRESS_CHECKSUM_LENGTH]
    addr_checksum = addr_bytes[-ADDRESS_CHECKSUM_LENGTH:]
    if double_sha256(addr_without_checksum)[:ADDRESS_CHECKSUM_LENGTH] != addr_checksum:
        raise InvalidAddress("Invalid address checksum")


def b58decode_strip_checksum(v):
    addr_bytes = b58decode(v)
    validate_b58_checksum(addr_bytes)
    return addr_bytes[:-ADDRESS_CHECKSUM_LENGTH]


def b58encode_with_checksum(addr_bytes):
    addr_checksum = double_sha256(addr_bytes)[:ADDRESS_CHECKSUM_LENGTH]
    return b58encode(addr_bytes + addr_checksum)

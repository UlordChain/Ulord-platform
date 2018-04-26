import base64
from copy import deepcopy
from unetschema.address import decode_address, encode_address
from unetschema.base import b58decode, b58encode
from unetschema.schema import CLAIM_TYPES, CLAIM_TYPE, STREAM_TYPE, CERTIFICATE_TYPE
from unetschema.schema import SIGNATURE
from unetschema.error import DecodeError, InvalidAddress


def encode_fields(claim_dictionary):
    """Encode bytes to hex and b58 for return by ClaimDict"""
    claim_dictionary = deepcopy(claim_dictionary)
    claim_type = CLAIM_TYPES[claim_dictionary[CLAIM_TYPE]]
    claim_value = claim_dictionary[claim_type]
    if claim_type == CLAIM_TYPES[STREAM_TYPE]:
        claim_value['source']['source'] = b58encode(claim_value['source']['source'])
        if 'fee' in claim_value['metadata']:
            try:
                address = encode_address(claim_value['metadata']['fee']['address'])
            except InvalidAddress as err:
                raise DecodeError("Invalid fee address: %s" % err.message)
            claim_value['metadata']['fee']['address'] = address
    elif claim_type == CLAIM_TYPES[CERTIFICATE_TYPE]:
        public_key = claim_value["publicKey"]
        claim_value["publicKey"] = public_key.encode('hex')
    if SIGNATURE in claim_dictionary:
        encoded_sig = claim_dictionary[SIGNATURE]['signature'].encode('hex')
        encoded_cert_id = claim_dictionary[SIGNATURE]['certificateId'].encode('hex')
        claim_dictionary[SIGNATURE]['signature'] = encoded_sig
        claim_dictionary[SIGNATURE]['certificateId'] = encoded_cert_id
    claim_dictionary[claim_type] = claim_value
    return claim_dictionary


def decode_fields(claim_dictionary):
    """Decode hex and b58 encoded bytes in dictionaries given to ClaimDict"""
    claim_dictionary = deepcopy(claim_dictionary)
    claim_type = CLAIM_TYPES[claim_dictionary[CLAIM_TYPE]]
    claim_value = claim_dictionary[claim_type]
    if claim_type == CLAIM_TYPES[STREAM_TYPE]:
        claim_value['source']['source'] = b58decode(claim_value['source']['source'])
        if 'fee' in claim_value['metadata']:
            try:
                address = decode_address(claim_value['metadata']['fee']['address'])
            except InvalidAddress as err:
                raise DecodeError("Invalid fee address: %s" % err.message)
            claim_value['metadata']['fee']['address'] = address
    elif claim_type == CLAIM_TYPES[CERTIFICATE_TYPE]:
        public_key = claim_value["publicKey"].decode('hex')
        claim_value["publicKey"] = public_key
    if SIGNATURE in claim_dictionary:
        decoded_sig = claim_dictionary[SIGNATURE]['signature'].decode('hex')
        decoded_cert_id = claim_dictionary[SIGNATURE]['certificateId'].decode('hex')
        claim_dictionary[SIGNATURE]['signature'] = decoded_sig
        claim_dictionary[SIGNATURE]['certificateId'] = decoded_cert_id
    claim_dictionary[claim_type] = claim_value
    return claim_dictionary


def decode_b64_fields(claim_dictionary):
    """Decode b64 encoded bytes in protobuf generated dictionary to be given to ClaimDict"""
    claim_dictionary = deepcopy(claim_dictionary)
    claim_type = CLAIM_TYPES[claim_dictionary[CLAIM_TYPE]]
    claim_value = claim_dictionary[claim_type]
    if claim_type == CLAIM_TYPES[STREAM_TYPE]:
        claim_value['source']['source'] = base64.b64decode(claim_value['source']['source'])
        if 'fee' in claim_value['metadata']:
            address = base64.b64decode(claim_value['metadata']['fee']['address'])
            claim_value['metadata']['fee']['address'] = address
    elif claim_type == CLAIM_TYPES[CERTIFICATE_TYPE]:
        public_key = base64.b64decode(claim_value["publicKey"])
        claim_value["publicKey"] = public_key
    if SIGNATURE in claim_dictionary:
        encoded_sig = base64.b64decode(claim_dictionary[SIGNATURE]['signature'])
        encoded_cert_id = base64.b64decode(claim_dictionary[SIGNATURE]['certificateId'])
        claim_dictionary[SIGNATURE]['signature'] = encoded_sig
        claim_dictionary[SIGNATURE]['certificateId'] = encoded_cert_id
    claim_dictionary[claim_type] = claim_value
    return claim_dictionary
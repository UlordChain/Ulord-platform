import json

from unetschema.error import DecodeError, InvalidAddress
from unetschema.legacy.migrate import migrate as schema_migrator
from unetschema.claim import ClaimDict

from google.protobuf import json_format  # pylint: disable=no-name-in-module


def migrate_json_claim_value(decoded_json):
    try:
        if 'fee' in decoded_json:
            old_fee = decoded_json['fee']
            if not old_fee[old_fee.keys()[0]]['amount']:
                del decoded_json['fee']
                return migrate_json_claim_value(decoded_json)
    except (TypeError, AttributeError, InvalidAddress):
        raise DecodeError("Failed to decode claim")
    try:
        pb_migrated = schema_migrator(decoded_json)
        return pb_migrated
    except json_format.ParseError as parse_error:
        raise DecodeError("Failed to parse protobuf: %s" % parse_error)
    except Exception as err:
        raise DecodeError("Failed to migrate claim: %s" % err)


hex_chars = "0123456789abcdef"


def smart_decode(claim_value):
    """
    Decode a claim value

    Try decoding claim protobuf, if this fails try decoding json and migrating it.
    If unable to decode or migrate, raise DecodeError
    """

    # if already decoded, return
    if isinstance(claim_value, ClaimDict):
        return claim_value
    elif isinstance(claim_value, dict):
        return ClaimDict.load_dict(claim_value)

    # see if we were given a hex string, try decoding it
    skip_hex = sum(1 if char not in hex_chars else 0 for char in claim_value)
    if not skip_hex:
        try:
            decoded = claim_value.decode('hex')
            claim_value = decoded
        except (TypeError, ValueError):
            pass

    if claim_value.startswith("{"):
        # try deserializing protobuf, if that fails try parsing from json
        try:
            decoded_json = json.loads(claim_value)
        except (ValueError, TypeError):
            try:
                decoded_claim = ClaimDict.deserialize(claim_value)
                return decoded_claim
            except (DecodeError, InvalidAddress, KeyError):
                raise DecodeError()
        migrated_claim = migrate_json_claim_value(decoded_json)
        return migrated_claim
    else:
        try:
            decoded_claim = ClaimDict.deserialize(claim_value)
            return decoded_claim
        except (DecodeError, InvalidAddress, KeyError):
            try:
                decoded_json = json.loads(claim_value)
            except (ValueError, TypeError):
                raise DecodeError()
            migrated_claim = migrate_json_claim_value(decoded_json)
            return migrated_claim

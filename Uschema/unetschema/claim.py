import json
from google.protobuf import json_format  # pylint: disable=no-name-in-module
from google.protobuf.message import DecodeError as DecodeError_pb  # pylint: disable=no-name-in-module,import-error

from collections import OrderedDict

from unetschema.schema.claim import Claim
from unetschema.schema import claim_pb2
from unetschema.validator import get_validator
from unetschema.signer import get_signer
from unetschema.schema import NIST256p, CURVE_NAMES, CLAIM_TYPE_NAMES
from unetschema.encoding import decode_fields, decode_b64_fields, encode_fields
from unetschema.error import DecodeError
from unetschema.fee import Fee


"""
1. dict      (python_object_dict)
2. ClaimDict (python_object_class)
3. Claim_pb     (protobuf_claim)
4. Claim_pb_dict (python_object_dict_claim_base64encode)
5. Claim_serialized  (hex_string)

    @classmethod
    ClaimDict.load_dict(claim_dict)               # dict     -> ClaimDict
    ClaimDict.load_protobuf(claim_pb)             # Claim_pb -> ClaimDict
    ClaimDict.load_protobuf_dict(claim_pb_dict)   # Claim_pb_dict -> ClaimDict
    ClaimDict.deserialize(serialized_string)      # Claim_serialized_string -> ClaimDict

    @property
    claim_dict(self)                # ClaimDict -> dict   
    protobuf(self)                  # ClaimDict -> Claim_pb  
    protobuf_dict(self)             # ClaimDict -> Claim_pb_dict
    serialized()                    # ClaimDict -> Claim_serialized_string

"""

class ClaimDict(OrderedDict):
    def __init__(self, claim_dict):
        if isinstance(claim_dict, claim_pb2.Claim):
            raise Exception("To initialize %s with a Claim protobuf use %s.load_protobuf" %
                            (self.__class__.__name__, self.__class__.__name__))
        OrderedDict.__init__(self, claim_dict)

    # ClaimDict -> Claim_pb_dict(Base64encode_dict)
    @property
    def protobuf_dict(self):
        """Claim dictionary using base64 to represent bytes"""

        return json.loads(json_format.MessageToJson(self.protobuf, True))

    # ClaimDict -> Claim_pb
    @property
    def protobuf(self):
        """Claim message object"""

        return Claim.load(self)

    # ClaimDict -> Claim_serialized_string
    @property
    def serialized(self):
        """Serialized Claim protobuf"""

        return self.protobuf.SerializeToString()


    @property
    def serialized_no_signature(self):
        """Serialized Claim protobuf without publisherSignature field"""
        claim = self.protobuf
        claim.ClearField("publisherSignature")
        return ClaimDict.load_protobuf(claim).serialized

    @property
    def has_signature(self):
        claim = self.protobuf
        if claim.HasField("publisherSignature"):
            return True
        return False

    @property
    def is_certificate(self):
        claim = self.protobuf
        return CLAIM_TYPE_NAMES[claim.claimType] == "certificate"

    @property
    def is_stream(self):
        claim = self.protobuf
        return CLAIM_TYPE_NAMES[claim.claimType] == "stream"

    @property
    def source_hash(self):
        claim = self.protobuf
        if not CLAIM_TYPE_NAMES[claim.claimType] == "stream":
            return None
        return claim.stream.source.source.encode('hex')

    @property
    def has_fee(self):
        claim = self.protobuf
        if not CLAIM_TYPE_NAMES[claim.claimType] == "stream":
            return None
        if claim.stream.metadata.HasField("fee"):
            return True
        return False

    @property
    def source_fee(self):
        claim = self.protobuf
        if not CLAIM_TYPE_NAMES[claim.claimType] == "stream":
            return None
        if claim.stream.metadata.HasField("fee"):
            return Fee.load_protobuf(claim.stream.metadata.fee)
        return None

    @property
    def certificate_id(self):
        if not self.has_signature:
            return None
        return self.protobuf.publisherSignature.certificateId.encode('hex')

    @property
    def signature(self):
        if not self.has_signature:
            return None
        return self.protobuf.publisherSignature.signature.encode('hex')

    @property
    def protobuf_len(self):
        """Length of serialized string"""

        return self.protobuf.ByteSize()

    @property
    def json_len(self):
        """Length of json encoded string"""

        return len(json.dumps(self.claim_dict))

    # ClaimDict -> dict
    @property
    def claim_dict(self):
        """Claim dictionary with bytes represented as hex and base58"""

        return dict(encode_fields(self))

    # Claim_pb_dict -> ClaimDict
    @classmethod
    def load_protobuf_dict(cls, protobuf_dict):
        """
        Load a ClaimDict from a dictionary with base64 encoded bytes
        (as returned by the protobuf json formatter)
        """

        return cls(decode_b64_fields(protobuf_dict))

    # Claim_pb -> ClaimDict
    @classmethod
    def load_protobuf(cls, protobuf_claim):
        """Load ClaimDict from a protobuf Claim message"""
        return cls.load_protobuf_dict(json.loads(json_format.MessageToJson(protobuf_claim, True)))

    # dict -> ClaimDict
    @classmethod
    def load_dict(cls, claim_dict):
        """Load ClaimDict from a dictionary with hex and base58 encoded bytes"""
        try:
            return cls.load_protobuf(cls(decode_fields(claim_dict)).protobuf)
        except json_format.ParseError as err:
            raise DecodeError(err.message)

    # Claim_serialized_string -> ClaimDict
    @classmethod
    def deserialize(cls, serialized):
        """Load a ClaimDict from a serialized protobuf string"""

        temp_claim = claim_pb2.Claim()
        try:
            temp_claim.ParseFromString(serialized)
        except DecodeError_pb:
            raise DecodeError(DecodeError_pb)
        return cls.load_protobuf(temp_claim)

    @classmethod
    def generate_certificate(cls, private_key, curve=NIST256p):
        signer = get_signer(curve).load_pem(private_key)
        return cls.load_protobuf(signer.certificate)

    def sign(self, private_key, claim_address, cert_claim_id, curve=NIST256p):
        signer = get_signer(curve).load_pem(private_key)
        signed = signer.sign_stream_claim(self, claim_address, cert_claim_id)
        return ClaimDict.load_protobuf(signed)

    def validate_signature(self, claim_address, certificate):
        if isinstance(certificate, ClaimDict):
            certificate = certificate.protobuf
        curve = CURVE_NAMES[certificate.certificate.keyType]
        validator = get_validator(curve).load_from_certificate(certificate, self.certificate_id)
        return validator.validate_claim_signature(self, claim_address)

    def validate_private_key(self, private_key, certificate_id):
        certificate = self.protobuf
        if CLAIM_TYPE_NAMES[certificate.claimType] != "certificate":
            return
        curve = CURVE_NAMES[certificate.certificate.keyType]
        validator = get_validator(curve).load_from_certificate(certificate, certificate_id)
        signing_key = validator.signing_key_from_pem(private_key)
        return validator.validate_private_key(signing_key)

    def get_validator(self, certificate_id):
        """
        Get a unetschema.validator.Validator object for a certificate claim

        :param certificate_id: claim id of this certificate claim
        :return: None or unetschema.validator.Validator object
        """

        claim = self.protobuf
        if CLAIM_TYPE_NAMES[claim.claimType] != "certificate":
            return
        curve = CURVE_NAMES[claim.certificate.keyType]
        return get_validator(curve).load_from_certificate(claim, certificate_id)

from copy import deepcopy
from unetschema.schema import source_pb2 as source_pb, VERSION_MAP
from unetschema.schema import SOURCE_TYPES, UNET_SD_HASH_LENGTH
from unetschema.schema.schema import Schema
from unetschema.error import InvalidSourceHashLength


class Source(Schema):
    @classmethod
    def load(cls, message):
        _source = deepcopy(message)
        sd_hash = _source.pop('source')
        assert len(sd_hash) == UNET_SD_HASH_LENGTH, InvalidSourceHashLength(len(sd_hash))
        _message_pb = source_pb.Source()
        _message_pb.version = VERSION_MAP[_source.pop("version")]
        _message_pb.sourceType = SOURCE_TYPES[_source.pop('sourceType')]
        _message_pb.source = sd_hash
        _message_pb.contentType = _source.pop('contentType')
        return cls._load(_source, _message_pb)

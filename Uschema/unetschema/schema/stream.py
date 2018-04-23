from copy import deepcopy

from unetschema.schema.source import Source
from unetschema.schema import VERSION_MAP, stream_pb2 as stream_pb
from unetschema.schema.metadata import Metadata
from unetschema.schema.schema import Schema


class Stream(Schema):
    @classmethod
    def load(cls, message):
        _claim = deepcopy(message)
        source = Source.load(_claim.pop('source'))
        metadata = Metadata.load(_claim.pop('metadata'))
        _message_pb = stream_pb.Stream()
        _message_pb.version = VERSION_MAP[_claim.pop("version")]
        _message_pb.source.CopyFrom(source)
        _message_pb.metadata.CopyFrom(metadata)
        return cls._load(_claim, _message_pb)

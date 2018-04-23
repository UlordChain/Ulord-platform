## Protobuf schema for [Ulord](https://ulord.one) claims and spec for unet:// URIs
Uschema is a [protobuf](https://github.com/google/protobuf) schema that defines how claims are structured and validated in the Ulord blockchain.
There is also code to construct, parse, and validate unet:// URIs.

### Use
Add `git+https://github.com/UlordChain/Uschema.git#egg=unetschema` to `requirements.txt`

### Install
To install in development mode, run `pip install -r requirements.txt; pip install -e .` from the repo directory.

### Compile .proto files
There are compiled protobuf files in unetschema/schema (see the files that end in _pb2.py), so compiling fresh is not necessary for most.

### If you want to compile the protobuf files yourself, install protoc:

- macOS: brew install protobuf
- Ubuntu: sudo apt-get install protobuf-compiler python-protobuf

#### Once protobuf is installed, run:
`protoc --proto_path=./unetschema/proto --python_out=./unetschema/schema ./unetschema/proto/*.proto`

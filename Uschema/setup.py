from setuptools import setup, find_packages
import os
from unetschema import __version__ as version

requires = [
    'protobuf',
    'jsonschema',
    'ecdsa',
]

base_dir = os.path.join(os.path.abspath(os.path.dirname(__file__)), "unetschema")
setup(
    name="unetschema",
    description="Protobuf schema for claims on the UlordChain  blockchain",
    version=version,
    author="JustinQP007@gmail.com",
    install_requires=requires,
    packages=find_packages(exclude=['tests'])
)

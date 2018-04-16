import os
import sys
from setuptools import find_packages, setup

from uwallet import __version__

from distutils.sysconfig import get_python_lib
import shutil
import platform
current_place = get_python_lib()

if platform.system().startswith('Win'):
    shutil.copyfile(os.path.join('uwallet', 'cryptohello_hash.pyd'),
                    os.path.join(current_place, 'cryptohello_hash.pyd'))
else:
    shutil.copyfile(os.path.join('uwallet', 'cryptohello_hash.so'),
                    os.path.join(current_place, 'cryptohello_hash.so'))

if sys.version_info[:3] < (2, 7, 0):
    sys.exit("Error: prowallet requires Python version >= 2.7.0...")

data_files = []

requires = [
    'slowaes',
    'ecdsa',
    'pbkdf2',
    'requests',
    'jsonrpclib',
    'six',
    'appdirs',
    'protobuf',
    'jsonschema',
    'unetschema',
]

console_scripts = [
    'muwallet = uwallet.main:main',
]

base_dir = os.path.abspath(os.path.dirname(__file__))

setup(
    name="muwallet",
    version=__version__,
    install_requires=requires,
    packages=find_packages(exclude=['tests']),
    package_data={'uwallet': ['wordlist/*.txt']},
    entry_points={'console_scripts': console_scripts},
    description="Lightweight ulord Wallet",
    author="UT Inc.",
    author_email="JustinQP007@gmail.com",
    license="GNU GPLv3",
    url="http://ulord.one/",
    long_description="""Lightweight ulord Wallet""",
    zip_safe=False
)

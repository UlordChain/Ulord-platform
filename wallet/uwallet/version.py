#-*- coding: UTF-8 -*-
from uwallet import __version__

UWALLET_VERSION = __version__
PROTOCOL_VERSION = '0.10'   # protocol version requested
NEW_SEED_VERSION = 11       # uwallet versions >= 2.0
OLD_SEED_VERSION = 4        # uwallet versions < 2.0

# The hash of the mnemonic seed must begin with this
SEED_PREFIX = '01'       # uwallet standard wallet
SEED_PREFIX_2FA = '101'  # extended seed for two-factor authentication

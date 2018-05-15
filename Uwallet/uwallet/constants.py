#-*- coding: UTF-8 -*-
RECOMMENDED_FEE = 50000  # type: int
COINBASE_MATURITY = 100
COIN = 100000000

# supported types of transaction outputs
TYPE_ADDRESS = 1
TYPE_PUBKEY = 2
TYPE_SCRIPT = 4
TYPE_CLAIM = 8
TYPE_SUPPORT = 16
TYPE_UPDATE = 32

# claim related constants
EXPIRATION_BLOCKS = 262974
RECOMMENDED_CLAIMTRIE_HASH_CONFIRMS = 1

BINDING_FEE = 100000
PLATFORM_ADDRESS = "uR2NcaKGoD5Ktqs8eUdeeCDY3pFX8saoMB"


NO_SIGNATURE = 'ff'

NULL_HASH = '0000000000000000000000000000000000000000000000000000000000000000'
HEADER_SIZE = 140
BLOCKS_PER_CHUNK = 20   #1d / 150s

#TODO add it. --JustinQP
HEADERS_URL = "" 

DEFAULT_PORTS = {'t': '50001', 's': '50002', 'h': '8081', 'g': '8082'}
NODES_RETRY_INTERVAL = 60
SERVER_RETRY_INTERVAL = 10
MAX_BATCH_QUERY_SIZE = 500
proxy_modes = ['socks4', 'socks5', 'http']

# Main network and testnet3 definitions
# these values follow the parameters in ulord/src/chainparams.cpp
blockchain_params = {
    'ulord_main': {
        'pubkey_address': 0,
        'script_address': 5,
        'pubkey_address_prefix': 130,
        'script_address_prefix': 125,
        'genesis_hash': '000e0979b2a26db104fb4d8c2c8d572919a56662cecdcadc3d0583ac8d548e23',
        'max_target': 0x000fffffff000000000000000000000000000000000000000000000000000000,
        'genesis_bits': 0x1f0fffff,
        'target_timespan': 150 
    },
    'ulord_test': {
        'pubkey_address': 0,
        'script_address': 5,
        'pubkey_address_prefix': 130,
        'script_address_prefix': 125,
        'genesis_hash': '000e0979b2a26db104fb4d8c2c8d572919a56662cecdcadc3d0583ac8d548e23',
        'max_target': 0x000fffffff000000000000000000000000000000000000000000000000000000,
        'genesis_bits': 0x1f0fffff,
        'target_timespan': 150
    },
    'ulord_regtest': {
        'pubkey_address': 0,
        'script_address': 5,
        'pubkey_address_prefix': 140,
        'script_address_prefix': 120,
        'genesis_hash': '0158f211e2881c0e725fcc6ec25db2b72ad4a3f8f7830a516e9d6570e9527fd1',
        'max_target': 0x0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F0F,     
        'genesis_bits': 0x200f0f0f,
        'target_timespan': 150
    }
}
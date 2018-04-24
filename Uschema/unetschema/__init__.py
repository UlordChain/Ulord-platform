import os

#I change version, but I dont knonw where is used. 
#This can have an impact if you make some judgments about the version.--lqp
__version__ = "0.0.1rc1"

BLOCKCHAIN_NAME_ENVVAR = "ULORDSCHEMA_BLOCKCHAIN_NAME"
if BLOCKCHAIN_NAME_ENVVAR in os.environ:
    if os.environ[BLOCKCHAIN_NAME_ENVVAR] in ['ulord_main', 'ulord_regtest',
                                              'ulord_testnet']:
        BLOCKCHAIN_NAME = os.environ[BLOCKCHAIN_NAME_ENVVAR]
    else:
        raise OSError("invalid blockchain name: %s" % os.environ[BLOCKCHAIN_NAME_ENVVAR])
else:
    BLOCKCHAIN_NAME = "ulord_main"

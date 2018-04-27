# -*- coding: UTF-8 -*-
class NotEnoughFunds(Exception):
    pass


class InvalidPassword(Exception):
    def __str__(self):
        return "Incorrect password"


class Timeout(Exception):
    pass


class InvalidProofError(Exception):
    pass


class ChainValidationError(Exception):
    pass


class ReturnError(Exception):

    def __init__(self, error_code, desc=None):
        super(ReturnError, self).__init__()
        self.error_code =  error_code
        if desc is not None:
            self.reason = self.error_desc[self.error_code] + ': {}'.format(desc)
        else:
            self.reason = self.error_desc[self.error_code]

class ParamsError(ReturnError):
    error_desc = {
        '51000': 'command not found',
        '51001': 'password error',
        '51002': 'password cannot be empty',
        '51003': 'user not exists',
        '51004': 'user already exists',
        '51005': 'invalid claim_id',
        '51006': "claim not find",
        '51007': "the bid must > 0",
        '51008': "the tx_fee must >= 0",
    }


class ServerError(ReturnError):
    error_desc = {
        '50000': "Unknown Error",
        '52001': 'payment Failed',
        '52002': "can't find fee in the claim.",  #  优化
        '52003': 'permission denied',
        '52004': 'Not enough funds',
        '52005': 'broadcast transaction fail',
        '52006': 'signature transaction fail',
        '52007': 'nout is None',
        '52008': 'operation is too frequent: it is necessary to wait for the transaction confirmation',
        '52009': 'get UTXO fail',
        '52010': 'No extra funds paid fee',
        '52011': 'Dont know which claim to update, because the same name claim > 1',
    }


class DecryptionError(ReturnError):
    error_desc = {
        '53000': 'Decode claim value error',
        '53001': 'invalid claim address',
    }



class EncryptionError(ReturnError):
    error_desc = {

    }

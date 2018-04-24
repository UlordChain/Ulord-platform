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
        '10000': 'command not found',
        '10001': 'password error',
        '10002': 'password cannot be empty',
        '10003': 'user not exists',
        '10004': 'user already exists',
        '10005': 'invalid claim_id',
        '10006': "claim not find",

    }


class ServerError(ReturnError):
    error_desc = {
        '20000': "Unknown Error",  # 优化
        '20001': 'payment Failed',
        '20002': "can't find fee in the claim.",  #  优化
        '20003': 'permission denied',
        '20004': 'Not enough funds',

    }


class EncryptionError(ReturnError):
    error_desc = {

    }

class DecryptionError(ReturnError):
    error_desc = {
        '40000': 'Decode claim value error',
    }


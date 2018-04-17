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

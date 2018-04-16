#-*- coding: UTF-8 -*-
import struct
import logging
from collections import defaultdict, namedtuple
from math import floor, log10

from uwallet.hashing import sha256
from uwallet.constants import COIN, TYPE_ADDRESS
from uwallet.transaction import Transaction
from uwallet.errors import NotEnoughFunds
from uwallet.util import PrintError

log = logging.getLogger()


class PRNG(object):
    """
    A simple deterministic PRNG.  Used to deterministically shuffle a
    set of coins - the same set of coins should produce the same output.
    Although choosing UTXOs "randomly" we want it to be deterministic,
    so if sending twice from the same UTXO set we choose the same UTXOs
    to spend.  This prevents attacks on users by malicious or stale
    servers.
    """

    def __init__(self, seed):
        self.sha = sha256(seed)
        self.pool = bytearray()

    def get_bytes(self, n):
        while len(self.pool) < n:
            self.pool.extend(self.sha)
            self.sha = sha256(self.sha)
        result, self.pool = self.pool[:n], self.pool[n:]
        return result

    def random(self):
        # Returns random double in [0, 1)
        four = self.get_bytes(4)
        return struct.unpack("I", four)[0] / 4294967296.0

    def randint(self, start, end):
        # Returns random integer in [start, end)
        return start + int(self.random() * (end - start))

    def choice(self, seq):
        return seq[int(self.random() * len(seq))]

    def shuffle(self, x):
        for i in reversed(xrange(1, len(x))):
            # pick an element in x[:i+1] with which to exchange x[i]
            j = int(self.random() * (i + 1))
            x[i], x[j] = x[j], x[i]


Bucket = namedtuple('Bucket', ['desc', 'size', 'value', 'coins'])


def strip_unneeded(bkts, sufficient_funds):
    '''Remove buckets that are unnecessary in achieving the spend amount'''
    bkts = sorted(bkts, key=lambda bkt: bkt.value)
    for i in range(len(bkts)):
        if not sufficient_funds(bkts[i + 1:]):
            return bkts[i:]
    # Shouldn't get here
    return bkts


class CoinChooserBase(PrintError):
    def keys(self, coins):
        raise NotImplementedError

    def bucketize_coins(self, coins):
        keys = self.keys(coins)
        buckets = defaultdict(list)
        for key, coin in zip(keys, coins):
            buckets[key].append(coin)

        def make_Bucket(desc, coins):
            size = sum(Transaction.estimated_input_size(coin)
                       for coin in coins)
            value = sum(coin['value'] for coin in coins)
            return Bucket(desc, size, value, coins)

        return map(make_Bucket, buckets.keys(), buckets.values())

    def penalty_func(self, tx):
        def penalty(candidate):
            return 0

        return penalty

    def change_amounts(self, tx, count, fee_estimator, dust_threshold):
        # Break change up if bigger than max_change
        output_amounts = [o[2] for o in tx.outputs()]
        # Don't split change of less than 0.02 BTC
        max_change = max(max(output_amounts) * 1.25, 0.02 * COIN)

        # Use N change outputs
        for n in range(1, count + 1):
            # How much is left if we add this many change outputs?
            change_amount = max(0, tx.get_fee() - fee_estimator(n))
            if change_amount // n <= max_change:
                break

        # Get a handle on the precision of the output amounts; round our
        # change to look similar
        def trailing_zeroes(val):
            s = str(val)
            return len(s) - len(s.rstrip('0'))

        zeroes = map(trailing_zeroes, output_amounts)
        min_zeroes = min(zeroes)
        max_zeroes = max(zeroes)
        zeroes = range(max(0, min_zeroes - 1), (max_zeroes + 1) + 1)

        # Calculate change; randomize it a bit if using more than 1 output
        remaining = change_amount
        amounts = []
        while n > 1:
            average = remaining // n
            amount = self.p.randint(int(average * 0.7), int(average * 1.3))
            precision = min(self.p.choice(zeroes), int(floor(log10(amount))))
            amount = int(round(amount, -precision))
            amounts.append(amount)
            remaining -= amount
            n -= 1

        # Last change output.  Round down to maximum precision but lose
        # no more than 100 satoshis to fees (2dp)
        N = pow(10, min(2, zeroes[0]))
        amount = (remaining // N) * N
        amounts.append(amount)

        assert sum(amounts) <= change_amount

        return amounts

    def change_outputs(self, tx, change_addrs, fee_estimator, dust_threshold):
        amounts = self.change_amounts(tx, len(change_addrs), fee_estimator,
                                      dust_threshold)
        assert min(amounts) >= 0
        assert len(change_addrs) >= len(amounts)
        # If change is above dust threshold after accounting for the
        # size of the change output, add it to the transaction.
        dust = sum(amount for amount in amounts if amount < dust_threshold)
        amounts = [amount for amount in amounts if amount >= dust_threshold]
        change = [(TYPE_ADDRESS, addr, amount)
                  for addr, amount in zip(change_addrs, amounts)]
        log.debug('change: %s', change)
        if dust:
            log.debug('not keeping dust %s', dust)
        return change

    def make_tx(self, coins, outputs, change_addrs, fee_estimator,
                dust_threshold, abandon_txid=None):
        '''Select unspent coins to spend to pay outputs.  If the change is
        greater than dust_threshold (after adding the change output to
        the transaction) it is kept, otherwise none is sent and it is
        added to the transaction fee.'''

        # Deterministic randomness from coins
        utxos = [c['prevout_hash'] + str(c['prevout_n']) for c in coins]
        self.p = PRNG(''.join(sorted(utxos)))

        # Copy the ouputs so when adding change we don't modify "outputs"
        tx = Transaction.from_io([], outputs[:])
        # Size of the transaction with no inputs and no change
        base_size = tx.estimated_size()
        spent_amount = tx.output_value()

        claim_coin = None
        if abandon_txid is not None:
            claim_coins = [coin for coin in coins if coin['is_claim']]
            assert len(claim_coins) >= 1
            claim_coin = claim_coins[0]
            spent_amount -= claim_coin['value']
            coins = [coin for coin in coins if not coin['is_claim']]

        def sufficient_funds(buckets):
            '''Given a list of buckets, return True if it has enough
            value to pay for the transaction'''
            total_input = sum(bucket.value for bucket in buckets)
            total_size = sum(bucket.size for bucket in buckets) + base_size
            return total_input >= spent_amount + fee_estimator(total_size)

        # Collect the coins into buckets, choose a subset of the buckets
        buckets = self.bucketize_coins(coins)
        buckets = self.choose_buckets(buckets, sufficient_funds,
                                      self.penalty_func(tx))

        if claim_coin is not None:
            tx.add_inputs([claim_coin])
        tx.add_inputs([coin for b in buckets for coin in b.coins])
        tx_size = base_size + sum(bucket.size for bucket in buckets)

        # This takes a count of change outputs and returns a tx fee;
        # each pay-to-bitcoin-address output serializes as 34 bytes
        def fee(count):
            return fee_estimator(tx_size + count * 34)

        change = self.change_outputs(tx, change_addrs, fee, dust_threshold)
        tx.add_outputs(change)

        log.debug("using %i inputs", len(tx.inputs()))
        log.info("using buckets: %s", [bucket.desc for bucket in buckets])

        return tx


class CoinChooserOldestFirst(CoinChooserBase):
    '''Maximize transaction priority. Select the oldest unspent
    transaction outputs in your wallet, that are sufficient to cover
    the spent amount. Then, remove any unneeded inputs, starting with
    the smallest in value.
    '''

    def keys(self, coins):
        return [coin['prevout_hash'] + ':' + str(coin['prevout_n'])
                for coin in coins]

    def choose_buckets(self, buckets, sufficient_funds, penalty_func):
        '''Spend the oldest buckets first.'''
        # Unconfirmed coins are young, not old
        def adj_height(height):
            return 99999999 if height == 0 else height

        buckets.sort(key=lambda b: max(adj_height(coin['height'])
                                       for coin in b.coins))
        selected = []
        for bucket in buckets:
            selected.append(bucket)
            if sufficient_funds(selected):
                return strip_unneeded(selected, sufficient_funds)
        raise NotEnoughFunds()


class CoinChooserRandom(CoinChooserBase):
    def keys(self, coins):
        return [coin['prevout_hash'] + ':' + str(coin['prevout_n'])
                for coin in coins]

    def bucket_candidates(self, buckets, sufficient_funds):
        '''Returns a list of bucket sets.'''
        candidates = set()

        # Add all singletons
        for n, bucket in enumerate(buckets):
            if sufficient_funds([bucket]):
                candidates.add((n,))

        # And now some random ones
        attempts = min(100, (len(buckets) - 1) * 10 + 1)
        permutation = range(len(buckets))
        for i in range(attempts):
            # Get a random permutation of the buckets, and
            # incrementally combine buckets until sufficient
            self.p.shuffle(permutation)
            bkts = []
            for count, index in enumerate(permutation):
                bkts.append(buckets[index])
                if sufficient_funds(bkts):
                    candidates.add(tuple(sorted(permutation[:count + 1])))
                    break
            else:
                raise NotEnoughFunds()

        candidates = [[buckets[n] for n in c] for c in candidates]
        return [strip_unneeded(c, sufficient_funds) for c in candidates]

    def choose_buckets(self, buckets, sufficient_funds, penalty_func):
        candidates = self.bucket_candidates(buckets, sufficient_funds)
        penalties = [penalty_func(cand) for cand in candidates]
        winner = candidates[penalties.index(min(penalties))]
        log.debug("Bucket sets: %i", len(buckets))
        log.debug("Winning penalty: %s", min(penalties))
        return winner


class CoinChooserPrivacy(CoinChooserRandom):
    '''Attempts to better preserve user privacy.  First, if any coin is
    spent from a user address, all coins are.  Compared to spending
    from other addresses to make up an amount, this reduces
    information leakage about sender holdings.  It also helps to
    reduce blockchain UTXO bloat, and reduce future privacy loss that
    would come from reusing that address' remaining UTXOs.  Second, it
    penalizes change that is quite different to the sent amount.
    Third, it penalizes change that is too big.'''

    def keys(self, coins):
        return [coin['address'] for coin in coins]

    def penalty_func(self, tx):
        min_change = min(o[2] for o in tx.outputs()) * 0.75
        max_change = max(o[2] for o in tx.outputs()) * 1.33
        spent_amount = sum(o[2] for o in tx.outputs())

        def penalty(buckets):
            badness = len(buckets) - 1
            total_input = sum(bucket.value for bucket in buckets)
            change = float(total_input - spent_amount)
            # Penalize change not roughly in output range
            if change < min_change:
                badness += (min_change - change) / (min_change + 10000)
            elif change > max_change:
                badness += (change - max_change) / (max_change + 10000)
                # Penalize large change; 5 BTC excess ~= using 1 more input
                badness += change / (COIN * 5)
            return badness

        return penalty


COIN_CHOOSERS = {'Priority': CoinChooserOldestFirst,
                 'Privacy': CoinChooserPrivacy}

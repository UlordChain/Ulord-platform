#-*- coding: UTF-8 -*-
import logging
import os
import re
import sys
import threading
import traceback
from collections import defaultdict
from decimal import Decimal
import json
import time
from uwallet.constants import NO_SIGNATURE

log = logging.getLogger("uwallet")

base_units = {'BTC': 8, 'mBTC': 5, 'uBTC': 2}


def normalize_version(v):
    return [int(x) for x in re.sub(r'(\.0+)*$', '', v).split(".")]


class PrintError(object):
    """A handy base class"""

    def diagnostic_name(self):
        return self.__class__.__name__

    def print_error(self, *msg):
        print_error("[%s]" % self.diagnostic_name(), *msg)


class ThreadJob(PrintError):
    """A job that is run periodically from a thread's main loop.  run() is
    called from that thread's context.
    """

    def run(self):
        """Called periodically from the thread"""
        pass


class DebugMem(ThreadJob):
    """A handy class for debugging GC memory leaks"""

    def __init__(self, classes, interval=30):
        self.next_time = 0
        self.classes = classes
        self.interval = interval

    def mem_stats(self):
        import gc
        self.print_error("Start memscan")
        gc.collect()
        objmap = defaultdict(list)
        for obj in gc.get_objects():
            for class_ in self.classes:
                if isinstance(obj, class_):
                    objmap[class_].append(obj)
        for class_, objs in objmap.items():
            self.print_error("%s: %d" % (class_.__name__, len(objs)))
        self.print_error("Finish memscan")

    def run(self):
        if time.time() > self.next_time:
            self.mem_stats()
            self.next_time = time.time() + self.interval


class DaemonThread(threading.Thread, PrintError):
    """ daemon thread that terminates cleanly """

    def __init__(self):
        threading.Thread.__init__(self)
        self.parent_thread = threading.currentThread()
        self.running = False
        self.running_lock = threading.Lock()
        self.job_lock = threading.Lock()
        self.jobs = []

    def add_jobs(self, jobs):
        with self.job_lock:
            self.jobs.extend(jobs)

    def run_jobs(self):
        # Don't let a throwing job disrupt the thread, future runs of
        # itself, or other jobs.  This is useful protection against
        # malformed or malicious server responses
        with self.job_lock:
            # print len(self.jobs), '$'*30, self.job_lock
            for job in self.jobs:
                try:
                    job.run()
                except:
                    traceback.print_exc(file=sys.stderr)

    def remove_jobs(self, jobs):
        with self.job_lock:
            for job in jobs:
                self.jobs.remove(job)

    def start(self):
        with self.running_lock:
            self.running = True
        return threading.Thread.start(self)

    def is_running(self):
        with self.running_lock:
            return self.running and self.parent_thread.is_alive()

    def stop(self):
        with self.running_lock:
            self.running = False


def print_error(*args):
    msg = " ".join([str(item) for item in args]) + "\n"
    log.error(msg)


def json_decode(x):
    try:
        return json.loads(x, parse_float=Decimal)
    except:
        return x


# decorator that prints execution time
def profiler(func):
    def do_profile(*args, **kw_args):
        n = func.func_name
        t0 = time.time()
        o = func(*args, **kw_args)
        t = time.time() - t0
        log.debug("[profiler] %s %f", n, t)
        return o
    # return lambda *args, **kw_args: do_profile(func, args, kw_args)
    return do_profile


def user_dir():
    if "HOME" in os.environ:
        return os.path.join(os.environ["HOME"], ".uwallet")
    elif "APPDATA" in os.environ:
        return os.path.join(os.environ["APPDATA"], "uwallet")
    elif "LOCALAPPDATA" in os.environ:
        return os.path.join(os.environ["LOCALAPPDATA"], "uwallet")
    elif 'ANDROID_DATA' in os.environ:
        try:
            import jnius
            env = jnius.autoclass('android.os.Environment')
            _dir = env.getExternalStorageDirectory().getPath()
            return _dir + '/uwallet/'
        except ImportError:
            pass
        return "/sdcard/uwallet/"
    else:
        # raise Exception("No home directory found in environment variables.")
        return


def format_satoshis(x, is_diff=False, num_zeros=0, decimal_point=8, whitespaces=False):
    from locale import localeconv
    if x is None:
        return 'unknown'
    x = int(x)  # Some callers pass Decimal
    scale_factor = pow(10, decimal_point)
    integer_part = "{:n}".format(int(abs(x) / scale_factor))
    if x < 0:
        integer_part = '-' + integer_part
    elif is_diff:
        integer_part = '+' + integer_part
    dp = localeconv()['decimal_point']
    fract_part = ("{:0" + str(decimal_point) + "}").format(abs(x) % scale_factor)
    fract_part = fract_part.rstrip('0')
    if len(fract_part) < num_zeros:
        fract_part += "0" * (num_zeros - len(fract_part))
    result = integer_part + dp + fract_part
    if whitespaces:
        result += " " * (decimal_point - len(fract_part))
        result = " " * (15 - len(result)) + result
    return result.decode('utf8')


def rev_hex(s):
    return s.decode('hex')[::-1].encode('hex')


def int_to_hex(i, length=1):
    s = hex(i)[2:].rstrip('L')
    s = "0" * (2 * length - len(s)) + s
    return rev_hex(s)


def hex_to_int(s):
    return int('0x' + s[::-1].encode('hex'), 16)


def var_int(i):
    # https://en.bitcoin.it/wiki/Protocol_specification#Variable_length_integer
    if i < 0xfd:
        return int_to_hex(i)
    elif i <= 0xffff:
        return "fd" + int_to_hex(i, 2)
    elif i <= 0xffffffff:
        return "fe" + int_to_hex(i, 4)
    else:
        return "ff" + int_to_hex(i, 8)


# This function comes from bitcointools, bct-LICENSE.txt.
def long_hex(bytes):
    return bytes.encode('hex_codec')


# This function comes from bitcointools, bct-LICENSE.txt.
def short_hex(bytes):
    t = bytes.encode('hex_codec')
    if len(t) < 11:
        return t
    return t[0:4] + "..." + t[-4:]


def parse_sig(x_sig):
    s = []
    for sig in x_sig:
        if sig[-2:] == '01':
            s.append(sig[:-2])
        else:
            assert sig == NO_SIGNATURE
            s.append(None)
    return s


def is_extended_pubkey(x_pubkey):
    return x_pubkey[0:2] in ['fe', 'ff']

# get password routine
def prompt_password(prompt, confirm=True):
    import getpass
    password = getpass.getpass(prompt, stream=None)
    if password and confirm:
        password2 = getpass.getpass("Confirm: ")
        if password != password2:
            sys.exit("Error: Passwords do not match.")
    if not password:
        password = None
    return password

class Timekeeping:

    def __init__(self):
        self.temp = time.time()

    def __run(self):
        t1 = self.temp
        t2 = time.time()
        self.temp = t2
        return t2 - t1

    def get_interval(self):
        return self.__run()

    def print_interval(self):
        print('the interval is:', self.__run())

def join_str(*args):
    str_args = [i.encode('utf-8') if isinstance(i, unicode) else i for i in args]

    return ' '.join(str_args)
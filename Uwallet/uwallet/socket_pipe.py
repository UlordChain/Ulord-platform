#-*- coding: UTF-8 -*-
import errno
import json
import logging
import socket
import ssl
import sys
import time
import traceback

from uwallet.errors import Timeout
from uwallet.util import important_print

log = logging.getLogger(__name__)


def parse_json(message):
    n = message.find('\n')
    if n == -1:
        return None, message
    try:
        j = json.loads(message[0:n])
    except:
        j = None
    return j, message[n + 1:]


class SocketPipe(object):
    def __init__(self, socket):
        self.socket = socket
        self.message = ''
        self.set_timeout(0.1)
        self.recv_time = time.time()

    def set_timeout(self, t):
        self.socket.settimeout(t)

    def idle_time(self):
        return time.time() - self.recv_time

    def get(self):
        while True:
            response, self.message = parse_json(self.message)
            if response is not None:
                return response
            try:
                data = self.socket.recv(1024)
            except socket.timeout:
                raise Timeout
            except ssl.SSLError:
                raise Timeout
            except socket.error, err:
                if err.errno == 60:
                    raise Timeout
                elif err.errno in [11, 35, 10035]:
                    # print_error("socket errno %d (resource temporarily unavailable)"% err.errno)
                    time.sleep(0.05)
                    raise Timeout
                else:
                    log.error("pipe (socket error): %s", err)
                    data = ''
            except:
                traceback.print_exc(file=sys.stderr)
                data = ''

            if not data:  # Connection closed remotely
                return None
            self.message += data
            self.recv_time = time.time()

    def send(self, request):
        out = json.dumps(request) + '\n'
        self._send(out)

    def send_all(self, requests):
        out = ''.join(map(lambda x: json.dumps(x) + '\n', requests))
        self._send(out)

    def _send(self, out):
        while out:
            try:
                sent = self.socket.send(out)
                out = out[sent:]
            except ssl.SSLError as e:
                log.error("SSLError: %s", e)
                time.sleep(0.1)
                continue
            except socket.error as e:
                if e[0] in (errno.EWOULDBLOCK, errno.EAGAIN):
                    log.debug("EAGAIN: retrying")
                    time.sleep(0.1)
                    continue
                elif e[0] in ['timed out', 'The write operation timed out']:
                    log.error("socket timeout, retry")
                    time.sleep(0.1)
                    continue
                else:
                    traceback.print_exc(file=sys.stdout)
                    raise e

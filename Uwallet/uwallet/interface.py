#-*- coding: UTF-8 -*-
import logging
import os
import socket
import sys
import threading
import time
import requests.certs
from uwallet.util import PrintError
from uwallet.errors import Timeout
from uwallet.socket_pipe import SocketPipe

if getattr(sys, 'frozen', False) and os.name == "nt":
    # When frozen for windows distribution, get the include cert
    ca_path = os.path.join(os.path.dirname(sys.executable), 'cacert.pem')
else:
    ca_path = requests.certs.where()

log = logging.getLogger(__name__)


def make_dict(args):
    m, p, i = args
    return {'method': m, 'params': p, 'id': i}


def Connection(server, queue, config_path):
    """Makes asynchronous connections to a remote uwallet server.
    Returns the running thread that is making the connection.

    Once the thread has connected, it finishes, placing a tuple on the
    queue of the form (server, socket), where socket is None if
    connection failed.
    """
    host, port, protocol = server.split(':')
    if protocol not in 'st':
        raise Exception('Unknown protocol: %s' % protocol)
    c = TcpConnection(server, queue, config_path)
    c.start()
    return c


class TcpConnection(threading.Thread, PrintError):
    def __init__(self, server, queue, config_path):
        threading.Thread.__init__(self)
        self.daemon = True
        self.config_path = config_path
        self.queue = queue
        self.server = server
        self.host, self.port, self.protocol = self.server.split(':')
        self.host = str(self.host)
        self.port = int(self.port)

    def diagnostic_name(self):
        return self.host

    def get_simple_socket(self):
        try:
            l = socket.getaddrinfo(self.host, self.port, socket.AF_UNSPEC, socket.SOCK_STREAM)
        except socket.gaierror:
            log.error("cannot resolve hostname")
            return
        for res in l:
            try:
                s = socket.socket(res[0], socket.SOCK_STREAM)
                s.connect(res[4])
                s.settimeout(2)
                s.setsockopt(socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1)
                return s
            except BaseException as e:
                log.exception('Failed to connect to %s', res)
                continue

    def get_socket(self):
        s = self.get_simple_socket()
        if s is None:
            return

        return s

    def run(self):
        socket = self.get_socket()
        if socket:
            log.info("connected to %s", self.server)
        self.queue.put((self.server, socket))


class Interface(PrintError):
    """The Interface class handles a socket connected to a single remote
    uwallet server.  It's exposed API is:

    - Member functions close(), fileno(), get_responses(), has_timed_out(),
      ping_required(), queue_request(), send_requests()
    - Member variable server.
    """

    def __init__(self, server, socket):
        self.server = server
        self.host, _, _ = server.split(':')
        self.socket = socket

        self.pipe = SocketPipe(socket)
        self.pipe.set_timeout(0.0)  # Don't wait for data
        # Dump network messages.  Set at runtime from the console.
        self.debug = False
        self.unsent_requests = []
        self.unanswered_requests = {}
        # Set last ping to zero to ensure immediate ping
        self.last_request = time.time()
        self.last_ping = 0
        self.closed_remotely = False

    def diagnostic_name(self):
        return self.host

    def fileno(self):
        # Needed for select
        return self.socket.fileno()

    def close(self):
        try:
            if not self.closed_remotely:
                self.socket.shutdown(socket.SHUT_RDWR)
        except socket.error as err:
            log.error("Error closing interface: %s (%s)", str(type(err)), err)
        finally:
            self.socket.close()

    def queue_request(self, *args):  # method, params, _id
        '''Queue a request, later to be send with send_requests when the
        socket is available for writing.
        '''
        self.request_time = time.time()
        self.unsent_requests.append(args)

    def send_requests(self):
        '''Sends all queued requests.  Returns False on failure.'''
        wire_requests = map(make_dict, self.unsent_requests)
        try:
            self.pipe.send_all(wire_requests)
        except socket.error:
            log.exception("socket error")
            return False
        for request in self.unsent_requests:
            log.debug("--> %s", request)
            self.unanswered_requests[request[2]] = request
        self.unsent_requests = []
        return True

    def ping_required(self):
        '''Maintains time since last ping.  Returns True if a ping should
        be sent.
        '''
        now = time.time()
        if now - self.last_ping > 60:
            self.last_ping = now
            return True
        return False

    def has_timed_out(self):
        '''Returns True if the interface has timed out.'''
        request_time = time.time() - self.request_time
        if self.unanswered_requests and request_time > 10 and self.pipe.idle_time() > 10:
            log.info("timeout %i", len(self.unanswered_requests))
            return True

        return False

    def get_responses(self):
        '''Call if there is data available on the socket.  Returns a list of
        (request, response) pairs.  Notifications are singleton
        unsolicited responses presumably as a result of prior
        subscriptions, so request is None and there is no 'id' member.
        Otherwise it is a response, which has an 'id' member and a
        corresponding request.  If the connection was closed remotely
        or the remote server is misbehaving, a (None, None) will appear.
        '''
        responses = []
        while True:
            try:
                response = self.pipe.get()
            except Timeout:
                break
            if response is None:
                responses.append((None, None))
                self.closed_remotely = True
                log.warning("connection closed remotely")
                break
            log.debug("<-- %s", response)
            wire_id = response.get('id', None)
            if wire_id is None:  # Notification
                responses.append((None, response))
            else:
                request = self.unanswered_requests.pop(wire_id, None)
                if request:
                    responses.append((request, response))
                else:
                    log.error("unknown wire ID: %s", wire_id)
                    responses.append((None, None))  # Signal
                    break

        return responses

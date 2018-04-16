#-*- coding: UTF-8 -*-
import Queue
import errno
import logging
import os
import random
import re
import select
import socket
import time
from collections import defaultdict, deque
from threading import Lock

from uwallet import __version__ as UWALLET_VERSION
from uwallet.constants import COIN, BLOCKS_PER_CHUNK, DEFAULT_PORTS, proxy_modes
from uwallet.constants import SERVER_RETRY_INTERVAL, NODES_RETRY_INTERVAL
from uwallet.util import DaemonThread, normalize_version
from uwallet.blockchain import get_blockchain
from uwallet.interface import Connection, Interface
from uwallet.simple_config import SimpleConfig
from uwallet.version import PROTOCOL_VERSION

log = logging.getLogger(__name__)


def is_online(host, ports):
    ip = socket.gethostbyname(host)
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(2)
    result = sock.connect_ex((ip, int(ports['t'])))
    sock.close()
    if result == 0:
        log.info("%s:%s is online", host, ports['t'])
        return True
    return False


def parse_servers(result):
    """ parse servers list into dict format"""
    servers = {}
    for item in result:
        host = item[1]
        out = {}
        version = None
        pruning_level = '-'
        if len(item) > 2:
            for v in item[2]:
                if re.match("[stgh]\d*", v):
                    protocol, port = v[0], v[1:]
                    if port == '':
                        port = DEFAULT_PORTS[protocol]
                    out[protocol] = port
                elif re.match("v(.?)+", v):
                    version = v[1:]
                elif re.match("p\d*", v):
                    pruning_level = v[1:]
                if pruning_level == '':
                    pruning_level = '0'
        try:
            is_recent = cmp(normalize_version(version),
                            normalize_version(PROTOCOL_VERSION)) >= 0
        except Exception:
            is_recent = False

        if out and is_recent:
            out['pruning'] = pruning_level
            servers[host] = out

    return servers


def filter_protocol(hostmap, protocol='s'):
    """Filters the hostmap for those implementing protocol.
    The result is a list in serialized form."""
    eligible = []
    for host, portmap in hostmap.items():
        port = portmap.get(protocol)
        if port:
            eligible.append(serialize_server(host, port, protocol))
    return eligible


# noinspection PyPep8
def pick_random_server(hostmap, protocol='t', exclude_set=set()):
    eligible = list(set(filter_protocol(hostmap, protocol)) - exclude_set)
    return random.choice(eligible) if eligible else None


def serialize_proxy(p):
    if type(p) != dict:
        return None
    return ':'.join([p.get('mode'), p.get('host'), p.get('port')])


def deserialize_proxy(s):
    if type(s) not in [str, unicode]:
        return None
    if s.lower() == 'none':
        return None
    proxy = {"mode": "socks5", "host": "localhost"}
    args = s.split(':')
    n = 0
    if proxy_modes.count(args[n]) == 1:
        proxy["mode"] = args[n]
        n += 1
    if len(args) > n:
        proxy["host"] = args[n]
        n += 1
    if len(args) > n:
        proxy["port"] = args[n]
    else:
        proxy["port"] = "8080" if proxy["mode"] == "http" else "1080"
    return proxy


def deserialize_server(server_str):
    host, port, protocol = str(server_str).split(':')
    assert protocol in 'st'
    int(port)  # Throw if cannot be converted to int
    return host, port, protocol


def serialize_server(host, port, protocol):
    return str(':'.join([host, port, protocol]))


class Network(DaemonThread):
    """The Network class manages a set of connections to remote uwallet
    servers, each connected socket is handled by an Interface() object.
    Connections are initiated by a Connection() thread which stops once
    the connection succeeds or fails.

    这个network类 管理一个由连接到远程远程钱包服务器的链接组成的元组，
    每个socket连接由Interface对象操作
    Connections 通过一个一旦成功或失败就结束的Connection线程创建

    Our external API:

    - Member functions get_header(), get_interfaces(), get_local_height(),
          get_parameters(), get_server_height(), get_status_value(),
          is_connected(), set_parameters(), stop()
    """

    def __init__(self, config=None):
        if config is None:
            config = {}  # Do not use mutables as default values!
        DaemonThread.__init__(self)
        self.config = SimpleConfig(config) if isinstance(config, dict) else config
        self.num_server = 8 if not self.config.get('oneserver') else 0
        self.blockchain = get_blockchain(self.config, self)
        # A deque of interface header requests, processed left-to-right
        self.bc_requests = deque()
        # Server for addresses and transactions
        self.default_server = self.config.get('server')
        # Sanitize default server
        try:
            deserialize_server(self.default_server)
        except:
            self.default_server = None
        if not self.default_server:
            default_servers = self.config.get('default_servers')
            if not default_servers:
                raise ValueError('No servers have been specified')
            self.default_server = pick_random_server(default_servers)

        self.lock = Lock()
        self.pending_sends = []
        self.message_id = 0
        self.debug = False
        self.irc_servers = {}  # returned by interface (list from irc)

        self.banner = ''
        self.fee = None
        self.relay_fee = None
        self.heights = {}
        self.merkle_roots = {}
        self.utxo_roots = {}

        # catchup counter, used to track catchup progress before chain is verified and headers saved
        self.catchup_progress = 0

        # callbacks passed with subscriptions
        self.subscriptions = defaultdict(list)
        self.sub_cache = {}
        self.callbacks = defaultdict(list)

        dir_path = os.path.join(self.config.path, 'certs')
        if not os.path.exists(dir_path):
            os.mkdir(dir_path)

        # subscriptions and requests
        self.subscribed_addresses = set()
        # Requests from client we've not seen a response to
        self.unanswered_requests = {}
        # retry times
        self.server_retry_time = time.time()
        self.nodes_retry_time = time.time()
        # kick off the network.  interface is the main server we are currently
        # communicating with.  interfaces is the set of servers we are connecting
        # to or have an ongoing connection with
        self.interface = None
        self.interfaces = {}
        self.auto_connect = self.config.get('auto_connect', False)
        self.connecting = set()
        self.socket_queue = Queue.Queue()
        self.online_servers = {}
        self._set_online_servers()
        self.start_network(deserialize_server(self.default_server)[2],
                           deserialize_proxy(self.config.get('proxy')))

    def register_callback(self, callback, events):
        with self.lock:
            for event in events:
                self.callbacks[event].append(callback)

    def unregister_callback(self, callback):
        with self.lock:
            for callbacks in self.callbacks.values():
                if callback in callbacks:
                    callbacks.remove(callback)

    def trigger_callback(self, event, *args):
        with self.lock:
            callbacks = self.callbacks[event][:]
        for callback in callbacks:
            callback(event, *args)

    def get_server_height(self):
        return self.heights.get(self.default_server, 0)

    def server_is_lagging(self):
        sh = self.get_server_height()
        if not sh:
            log.info('no height for main interface')
            return True
        lh = self.get_local_height()
        result = (lh - sh) > 1
        if result:
            log.info('%s is lagging (%d vs %d)', self.default_server, sh, lh)
        return result

    def set_status(self, status):
        self.connection_status = status
        self.notify('status')

    def is_connected(self):
        return self.interface is not None

    def is_connecting(self):
        return self.connection_status == 'connecting'

    def is_up_to_date(self):
        return self.unanswered_requests == {}

    def queue_request(self, method, params, interface=None):
        # If you want to queue a request on any interface it must go
        # through this function so message ids are properly tracked
        if interface is None:
            interface = self.interface
        message_id = self.message_id
        self.message_id += 1
        if self.debug:
            log.debug('%s --> %s, %s, %s', interface.host, method, params, message_id)
        interface.queue_request(method, params, message_id)
        return message_id

    def send_subscriptions(self):
        log.info(
            'sending subscriptions to %s. Unanswered requests: %s, Subscribed addresses: %s',
            self.interface.server, len(self.unanswered_requests), len(self.subscribed_addresses))
        self.sub_cache.clear()
        # Resend unanswered requests
        requests = self.unanswered_requests.values()
        self.unanswered_requests = {}
        for request in requests:
            message_id = self.queue_request(request[0], request[1])
            self.unanswered_requests[message_id] = request
        for addr in self.subscribed_addresses:
            self.queue_request('blockchain.address.subscribe', [addr])
        self.queue_request('server.banner', [])
        self.queue_request('server.peers.subscribe', [])
        self.queue_request('blockchain.estimatefee', [2])
        self.queue_request('blockchain.relayfee', [])

    def get_status_value(self, key):
        if key == 'status':
            value = self.connection_status
        elif key == 'banner':
            value = self.banner
        elif key == 'fee':
            value = self.fee
        elif key == 'updated':
            value = (self.get_local_height(), self.get_server_height())
        elif key == 'servers':
            value = self.get_servers()
        elif key == 'interfaces':
            value = self.get_interfaces()
        return value

    def notify(self, key):
        if key in ['status', 'updated']:
            self.trigger_callback(key)
        else:
            self.trigger_callback(key, self.get_status_value(key))

    def get_parameters(self):
        host, port, protocol = deserialize_server(self.default_server)
        return host, port, protocol, self.auto_connect

    def get_interfaces(self):
        """The interfaces that are in connected state"""
        return self.interfaces.keys()

    # Do an initial pruning of uwallet servers that don't have the specified port open
    def _set_online_servers(self):
        servers = self.config.get('default_servers', {}).iteritems()
        self.online_servers = {
            host: ports for host, ports in servers
            if is_online(host, ports)
        }

    def get_servers(self):
        if self.irc_servers:
            out = self.irc_servers
        else:
            out = self.online_servers
        return out

    def start_interface(self, server):
        if server not in self.interfaces and server not in self.connecting:
            if server == self.default_server:
                log.info("connecting to %s as new interface", server)
                self.set_status('connecting')
            self.connecting.add(server)
            c = Connection(server, self.socket_queue, self.config.path)

    def start_random_interface(self):
        exclude_set = self.disconnected_servers.union(set(self.interfaces))
        server = pick_random_server(self.get_servers(), self.protocol, exclude_set)
        if server:
            self.start_interface(server)

    def start_interfaces(self):
        self.start_interface(self.default_server)
        for i in range(self.num_server - 1):
            self.start_random_interface()

    def start_network(self, protocol, proxy):
        assert not self.interface and not self.interfaces
        assert not self.connecting and self.socket_queue.empty()
        log.info('starting network')
        self.disconnected_servers = set([])
        self.protocol = protocol
        self.start_interfaces()

    def stop_network(self):
        log.info("stopping network")
        for interface in self.interfaces.values():
            self.close_interface(interface)
        assert self.interface is None
        assert not self.interfaces
        self.connecting = set()
        # Get a new queue - no old pending connections thanks!
        self.socket_queue = Queue.Queue()

    def set_parameters(self, host, port, protocol, proxy, auto_connect):
        proxy_str = serialize_proxy(proxy)
        server = serialize_server(host, port, protocol)
        self.config.set_key('auto_connect', auto_connect, False)
        self.config.set_key("proxy", proxy_str, False)
        self.config.set_key("server", server, True)
        # abort if changes were not allowed by config
        if self.config.get('server') != server or self.config.get('proxy') != proxy_str:
            return

        self.auto_connect = auto_connect
        if self.default_server != server:
            self.switch_to_interface(server)
        else:
            self.switch_lagging_interface()

    def switch_to_random_interface(self):
        '''Switch to a random connected server other than the current one'''
        servers = self.get_interfaces()  # Those in connected state
        if self.default_server in servers:
            servers.remove(self.default_server)
        if servers:
            self.switch_to_interface(random.choice(servers))

    def switch_lagging_interface(self, suggestion=None):
        '''If auto_connect and lagging, switch interface'''
        if self.server_is_lagging() and self.auto_connect:
            if suggestion and self.protocol == deserialize_server(suggestion)[2]:
                self.switch_to_interface(suggestion)
            else:
                self.switch_to_random_interface()

    def switch_to_interface(self, server):
        '''Switch to server as our interface.  If no connection exists nor
        being opened, start a thread to connect.  The actual switch will
        happen on receipt of the connection notification.  Do nothing
        if server already is our interface.'''
        self.default_server = server
        if server not in self.interfaces:
            self.interface = None
            self.start_interface(server)
            return
        i = self.interfaces[server]
        if self.interface != i:
            log.info("switching to %s", server)
            # stop any current interface in order to terminate subscriptions
            self.close_interface(self.interface)
            self.interface = i
            self.send_subscriptions()
            self.set_status('connected')
            self.notify('updated')

    def close_interface(self, interface):
        if interface:
            self.interfaces.pop(interface.server)
            if interface.server == self.default_server:
                self.interface = None
            interface.close()

    def process_response(self, interface, response, callbacks):
        if self.debug:
            log.debug("<-- %s", response)
        error = response.get('error')
        result = response.get('result')
        method = response.get('method')
        params = response.get('params')

        # We handle some responses; return the rest to the client.
        if method == 'server.version':
            interface.server_version = result
        elif method == 'blockchain.headers.subscribe':
            if error is None:
                self.on_header(interface, result)
        elif method == 'server.peers.subscribe':
            if error is None:
                self.irc_servers = parse_servers(result)
                self.notify('servers')
        elif method == 'server.banner':
            if error is None:
                self.banner = result
                self.notify('banner')
        elif method == 'blockchain.estimatefee':
            if error is None:
                self.fee = int(result * COIN)
                log.info("recommended fee %s", self.fee)
                self.notify('fee')
        elif method == 'blockchain.relayfee':
            if error is None:
                self.relay_fee = int(result * COIN)
                log.info("relayfee %s", self.relay_fee)
        elif method == 'blockchain.block.get_chunk':
            self.on_get_chunk(interface, response)
        elif method == 'blockchain.block.get_header':
            self.on_get_header(interface, response)

        for callback in callbacks:
            callback(response)

    def get_index(self, method, params):
        """ hashable index for subscriptions and cache"""
        return str(method) + (':' + str(params[0]) if params else '')

    def process_responses(self, interface):
        responses = interface.get_responses()
        for request, response in responses:
            if request:
                method, params, message_id = request
                k = self.get_index(method, params)
                # client requests go through self.send() with a
                # callback, are only sent to the current interface,
                # and are placed in the unanswered_requests dictionary
                client_req = self.unanswered_requests.pop(message_id, None)
                if client_req:
                    assert interface == self.interface
                    callbacks = [client_req[2]]
                else:
                    callbacks = []
                # Copy the request method and params to the response
                response['method'] = method
                response['params'] = params
                # Only once we've received a response to an addr subscription
                # add it to the list; avoids double-sends on reconnection
                if method == 'blockchain.address.subscribe':
                    self.subscribed_addresses.add(params[0])
            else:
                if not response:  # Closed remotely / misbehaving
                    self.connection_down(interface.server)
                    break
                # Rewrite response shape to match subscription request response
                method = response.get('method')
                params = response.get('params')
                k = self.get_index(method, params)
                if method == 'blockchain.headers.subscribe':
                    response['result'] = params[0]
                    response['params'] = []
                elif method == 'blockchain.address.subscribe':
                    response['params'] = [params[0]]  # addr
                    response['result'] = params[1]
                callbacks = self.subscriptions.get(k, [])

            # update cache if it's a subscription
            if method.endswith('.subscribe'):
                self.sub_cache[k] = response
            # Response is now in canonical form
            self.process_response(interface, response, callbacks)

    def send(self, messages, callback):
        '''Messages is a list of (method, params) tuples'''
        with self.lock:
            self.pending_sends.append((messages, callback))

    def process_pending_sends(self):
        # Requests needs connectivity.  If we don't have an interface,
        # we cannot process them.
        if not self.interface:
            return

        with self.lock:
            sends = self.pending_sends
            self.pending_sends = []

        for messages, callback in sends:
            for method, params in messages:
                r = None
                if method.endswith('.subscribe'):
                    k = self.get_index(method, params)
                    # add callback to list
                    l = self.subscriptions.get(k, [])
                    if callback not in l:
                        l.append(callback)
                    self.subscriptions[k] = l
                    # check cached response for subscriptions
                    r = self.sub_cache.get(k)
                if r is not None:
                    log.warning("cache hit: %s", k)
                    callback(r)
                else:
                    message_id = self.queue_request(method, params)
                    self.unanswered_requests[message_id] = method, params, callback

    def unsubscribe(self, callback):
        '''Unsubscribe a callback to free object references to enable GC.'''
        # Note: we can't unsubscribe from the server, so if we receive
        # subsequent notifications process_response() will emit a harmless
        # "received unexpected notification" warning
        with self.lock:
            for v in self.subscriptions.values():
                if callback in v:
                    v.remove(callback)

    def connection_down(self, server):
        '''A connection to server either went down, or was never made.
        We distinguish by whether it is in self.interfaces.'''
        self.disconnected_servers.add(server)
        if server == self.default_server:
            self.set_status('disconnected')
        if server in self.interfaces:
            self.close_interface(self.interfaces[server])
            self.heights.pop(server, None)
            self.notify('interfaces')

    def new_interface(self, server, socket):
        self.interfaces[server] = interface = Interface(server, socket)
        self.queue_request('blockchain.headers.subscribe', [], interface)
        if server == self.default_server:
            self.switch_to_interface(server)
        self.notify('interfaces')

    def maintain_sockets(self):
        '''Socket maintenance.'''
        # Responses to connection attempts?
        while not self.socket_queue.empty():
            server, socket = self.socket_queue.get()
            self.connecting.remove(server)
            if socket:
                self.new_interface(server, socket)
            else:
                self.connection_down(server)

        # Send pings and shut down stale interfaces
        for interface in self.interfaces.values():
            if interface.has_timed_out():
                self.connection_down(interface.server)
            elif interface.ping_required():
                params = [UWALLET_VERSION, PROTOCOL_VERSION]
                self.queue_request('server.version', params, interface)

        now = time.time()
        # nodes
        if len(self.interfaces) + len(self.connecting) < self.num_server:
            self.start_random_interface()
            if now - self.nodes_retry_time > NODES_RETRY_INTERVAL:
                log.info('network: retrying connections')
                self.disconnected_servers = set([])
                self.nodes_retry_time = now

        # main interface
        if not self.is_connected():
            if self.auto_connect:
                if not self.is_connecting():
                    self.switch_to_random_interface()
            else:
                if self.default_server in self.disconnected_servers:
                    if now - self.server_retry_time > SERVER_RETRY_INTERVAL:
                        self.disconnected_servers.remove(self.default_server)
                        self.server_retry_time = now
                else:
                    self.switch_to_interface(self.default_server)

    def request_chunk(self, interface, data, idx):
        log.debug("requesting chunk %d" % idx)
        self.queue_request('blockchain.block.get_chunk', [idx], interface)
        data['chunk_idx'] = idx
        data['req_time'] = time.time()

    def _caught_up_to_interface(self, data):
        return self.get_local_height() >= data['if_height']

    def _need_chunk_from_interface(self, data):
        return self.get_local_height() + BLOCKS_PER_CHUNK <= data['if_height']

    def on_get_chunk(self, interface, response):
        """Handle receiving a chunk of block headers"""
        if self.bc_requests:
            req_if, data = self.bc_requests[0]
            req_idx = data.get('chunk_idx')
            # Ignore unsolicited chunks
            if req_if == interface and req_idx == response['params'][0]:
                idx = self.blockchain.connect_chunk(req_idx, response['result'])
                if idx < 0 or self._caught_up_to_interface(data):
                    self.bc_requests.popleft()
                    self.notify('updated')
                elif self._need_chunk_from_interface(data):
                    self.request_chunk(interface, data, idx)
                else:
                    self.request_header(interface, data, data['if_height'])

    def request_header(self, interface, data, height):
        log.debug("requesting header %d" % height)
        self.queue_request('blockchain.block.get_header', [height], interface)
        data['header_height'] = height
        data['req_time'] = time.time()
        if 'chain' not in data:
            data['chain'] = []

    def on_get_header(self, interface, response):
        """Handle receiving a single block header"""
        if self.bc_requests:
            req_if, data = self.bc_requests[0]
            req_height = data.get('header_height', -1)
            # Ignore unsolicited headers
            if req_if == interface and req_height == response['params'][0]:
                next_height = self.blockchain.connect_header(data['chain'], response['result'])
                self.catchup_progress += 1
                # If not finished, get the next header
                if next_height is True or next_height is False:
                    self.catchup_progress = 0
                    self.bc_requests.popleft()
                    if next_height:
                        self.switch_lagging_interface(interface.server)
                        self.notify('updated')
                    else:
                        log.warning("header didn't connect, dismissing interface")
                        interface.close()
                else:
                    self.request_header(interface, data, next_height)

    def bc_request_headers(self, interface, data):
        """Send a request for the next header, or a chunk of them,
        if necessary.
        """
        local_height, if_height = self.get_local_height(), data['if_height']
        if if_height < local_height:
            return False
        elif if_height > local_height + BLOCKS_PER_CHUNK:
            self.request_chunk(interface, data, (local_height + 1) / BLOCKS_PER_CHUNK)
        else:
            self.request_header(interface, data, if_height)
        return True

    def handle_bc_requests(self):
        """Work through each interface that has notified us of a new header.
        Send it requests if it is ahead of our blockchain object.
        """
        while self.bc_requests:
            interface, data = self.bc_requests.popleft()
            # If the connection was lost move on
            if interface not in self.interfaces.values():
                continue

            req_time = data.get('req_time')
            if not req_time:
                # No requests sent yet.  This interface has a new height.
                # Request headers if it is ahead of our blockchain
                if not self.bc_request_headers(interface, data):
                    continue
            elif time.time() - req_time > 30:
                log.error("blockchain request timed out")
                self.connection_down(interface.server)
                continue
            # Put updated request state back at head of deque
            self.bc_requests.appendleft((interface, data))
            break

    def wait_on_sockets(self):
        # Python docs say Windows doesn't like empty selects.
        # Sleep to prevent busy looping
        if not self.interfaces:
            time.sleep(0.1)
            return
        rin = [i for i in self.interfaces.values()]
        win = [i for i in self.interfaces.values() if i.unsent_requests]
        try:
            rout, wout, xout = select.select(rin, win, [], 0.2)
        except socket.error as (code, msg):
            if code == errno.EINTR:
                return
            raise
        assert not xout
        for interface in wout:
            interface.send_requests()
        for interface in rout:
            self.process_responses(interface)

    def run(self):
        log.info('Initializing the blockchain')
        self.blockchain.init()
        log.info('Blockchain initialized, starting run loop')
        while self.is_running():
            self.maintain_sockets()
            self.wait_on_sockets()
            self.handle_bc_requests()
            self.run_jobs()  # Synchronizer and Verifier
            self.process_pending_sends()

        log.info('Stopping network')
        self.stop_network()
        log.info("stopped")

    def on_header(self, i, header):
        height = header.get('block_height')
        if not height:
            return
        self.heights[i.server] = height
        self.merkle_roots[i.server] = header.get('merkle_root')
        self.utxo_roots[i.server] = header.get('utxo_root')

        # Queue this interface's height for asynchronous catch-up
        self.bc_requests.append((i, {'if_height': height}))

        if i == self.interface:
            self.switch_lagging_interface()
            self.notify('updated')

    def get_header(self, tx_height):
        return self.blockchain.read_header(tx_height)

    def get_local_height(self):
        return self.blockchain.height()

    def get_blocks_behind(self):
        """An estimate of the number of blocks remaing to download from the server"""
        local_height = self.get_local_height()
        remote_height = self.get_server_height()
        if remote_height < local_height:
            return None
        diff = remote_height - local_height
        if diff > 0:
            return diff - self.catchup_progress
        else:
            return 0

    def synchronous_get(self, request, timeout=30):
        queue = Queue.Queue()
        self.send([request], queue.put)
        try:
            r = queue.get(True, timeout)
        except Queue.Empty:
            msg = 'Failed to get response from server within timeout of {}'.format(timeout)
            raise BaseException(msg)
        if r.get('error'):
            raise BaseException(r.get('error'))
        return r.get('result')

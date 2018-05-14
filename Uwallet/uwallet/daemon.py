#-*- coding: UTF-8 -*-
import sys
from collections import OrderedDict

import jsonrpclib
from jsonrpclib.SimpleJSONRPCServer import SimpleJSONRPCRequestHandler, SimpleJSONRPCServer

from uwallet.commands import Commands, known_commands

def get_daemon(config):
    server = jsonrpclib.Server('http://%s:%d' % ('localhost', config.get('rpc_port')))
    # check if daemon is running
    try:
        server.ping()
        return server
    except:
        pass


class RequestHandler(SimpleJSONRPCRequestHandler):
    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()

    def end_headers(self):
        self.send_header("Access-Control-Allow-Headers",
                         "Origin, X-Requested-With, Content-Type, Accept")
        self.send_header("Access-Control-Allow-Origin", "*")
        SimpleJSONRPCRequestHandler.end_headers(self)


class Daemon():
    def __init__(self, config, network):
        self.config = config
        self.network = network
        self.wallets = OrderedDict()
        self.cmd_runner = Commands(self.config, self.wallets, self.network)

        self.server = SimpleJSONRPCServer(('0.0.0.0', config.get('rpc_port')),
                                          requestHandler=RequestHandler,
                                          logRequests=False)
        self.server.timeout = 0.1
        for cmdname in known_commands:
            self.server.register_function(getattr(self.cmd_runner, cmdname), cmdname)
        self.server.register_function(self.ping, 'ping')
        self.server.register_function(self.run_daemon, 'daemon')

    def ping(self):
        return True

    def run_daemon(self, config):
        sub = config.get('subcommand')
        assert sub in ['start', 'stop', 'status']
        if sub == 'start':
            response = "Daemon already running"
        elif sub == 'status':
            p = self.network.get_parameters()
            response = {
                'path': self.network.config.path,
                'server': p[0],
                'blockchain_height': self.network.get_local_height(),
                'server_height': self.network.get_server_height(),
                'nodes': self.network.get_interfaces(),
                'connected': self.network.is_connected(),
                'auto_connect': p[3],
                'wallets': dict([(k, w.is_up_to_date()) for k, w in self.wallets.items()]),
            }
        elif sub == 'stop':
            self.stop()
            response = "Daemon stopped"
        return response


    def stop(self):
        for k, wallet in self.wallets.items():
            wallet.stop_threads()
        sys.exit()

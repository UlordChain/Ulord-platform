#-*- coding: UTF-8 -*-
import ast
import os

import chardet
import jsonrpclib
import pymongo
from jsonrpclib.SimpleJSONRPCServer import SimpleJSONRPCRequestHandler, SimpleJSONRPCServer

from uwallet.commands import Commands, known_commands
from uwallet.simple_config import SimpleConfig
from uwallet.util import DaemonThread, json_decode
from uwallet.wallet import Wallet, WalletStorage
import thread
import time
import multiprocessing
import select
import traceback

def lockfile(config):
    return os.path.join(config.path, 'daemon')


def get_daemon(config):
    try:
        with open(lockfile(config)) as f:
            host, port = ast.literal_eval(f.read())
    except:
        return
    server = jsonrpclib.Server('http://%s:%d' % (host, port))
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


class Daemon(DaemonThread):
    def __init__(self, config, network):
        DaemonThread.__init__(self)
        self.config = config
        self.network = network
        self.wallets = {}
        self.load_wallet()
        self.cmd_runner = Commands(self.config, self.wallets, self.network)

        host = config.get('rpchost', '0.0.0.0')
        port = config.get('rpcport', 8000)
        self.server = SimpleJSONRPCServer((host, port), requestHandler=RequestHandler,
                                          logRequests=False)
        with open(lockfile(config), 'w') as f:
            f.write(repr(self.server.socket.getsockname()))
        self.server.timeout = 0.1
        for cmdname in known_commands:
            # rpc直接调用命令 --hetao
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

    def load_wallet(self):
        mongo = pymongo.MongoClient('192.168.14.240')
        db = mongo.uwallet_user
        for col_name in db.list_collection_names():
            col = db.get_collection(col_name)

            for user_name in col.find({}, {'_id':1}):

                user = '_'.join([col_name, user_name['_id']])
                storage = WalletStorage(user)

                wallet = Wallet(storage)
                # automatically generate wallet for ulord
                if not storage.file_exists:
                    seed = wallet.make_seed()
                    wallet.add_seed(seed, None)
                    wallet.create_master_keys(None)
                    wallet.create_main_account()
                    wallet.synchronize()

                wallet.start_threads(self.network)
                if wallet:
                    self.wallets[user] = wallet

    def run_cmdline(self, config_options):
        config = SimpleConfig(config_options)
        cmdname = config.get('cmd')
        cmd = known_commands[cmdname]
        # wallet = self.load_wallet(path) if cmd.requires_wallet else None
        # arguments passed to function
        args = map(lambda x: config.get(x), cmd.params)
        # decode json arguments
        args = map(json_decode, args)
        # options
        args += map(lambda x: config.get(x), cmd.options)
        cmd_runner = Commands(config, self.wallets, self.network,
                              password=config_options.get('password'),
                              new_password=config_options.get('new_password'))
        func = getattr(cmd_runner, cmd.name)
        result = func(*args)
        return result


    def run(self):
        i = 0
        while self.is_running():
            # self.server.handle_request()
            try:
                thread.start_new_thread(self.server.handle_request, ())
                time.sleep(0.01)
                i = i+1
            except Exception,ex:
                i = 0
                print ex
                continue
        os.unlink(lockfile(self.config))



    # def run(self):
    #     cpus = multiprocessing.cpu_count()
    #     pros = []
    #     while self.is_running():
    #         fd_sets = _eintr_retry(select.select, [self.server], [], [], 0.1)
    #         if not fd_sets[0]:
    #             self.server.handle_timeout()
    #             ll = pros.__len__()
    #             if(ll>0):
    #                 aliveCount = 0
    #                 for idx in range(0, ll):
    #                     if (pros[idx].is_alive()):
    #                         aliveCount +=1
    #                 print 'aliveThread:',aliveCount
    #
    #             continue
    #         if(pros.__len__() < cpus):
    #             currentP = multiprocessing.Process(target=self.server._handle_request_noblock)
    #             currentP.start()
    #             pros.append(currentP)
    #         else:
    #             for idx in range(0,cpus):
    #                 if(pros[idx].is_alive() == False):
    #
    #                     currentP = multiprocessing.Process(target=self.server._handle_request_noblock)
    #                     currentP.start()
    #                     pros[idx] = currentP
    #                     break
    #
    #     # ps aux | grep "python" | grep - v grep | cut - c 9 - 15 | xargs kill - 9
    #     os.unlink(lockfile(self.config))

    def stop(self):
        for k, wallet in self.wallets.items():
            wallet.stop_threads()
        DaemonThread.stop(self)

def _eintr_retry(func, *args):
    """restart a system call interrupted by EINTR"""
    while True:
        try:
            return func(*args)
        except (OSError, select.error) as e:
                raise
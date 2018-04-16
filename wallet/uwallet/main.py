#-*- coding: UTF-8 -*-
import json
import logging
import os
import sys
from time import time

import requests
from uwallet.commands import Commands, config_variables, get_parser, known_commands
from uwallet.daemon import Daemon, get_daemon
from uwallet.network import Network, SimpleConfig
from uwallet.util import json_decode
from uwallet.errors import InvalidPassword
from uwallet.wallet import Wallet, WalletStorage
log = logging.getLogger("uwallet")

script_dir = os.path.dirname(os.path.realpath(__file__))
is_bundle = getattr(sys, 'frozen', False)


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


def run_non_RPC(config):
    cmdname = config.get('cmd')
    client = config.get('client')
    user = config.get('user')
    storage = WalletStorage(config.get_wallet_path() + '//'+ user)

    if storage.file_exists:
        sys.exit("Error: Remove the existing wallet first!")

    def password_dialog():
        return prompt_password("Password (hit return if you do not wish to encrypt your wallet):")

    if cmdname == 'restore':
        text = config.get('text')
        password = password_dialog() if Wallet.is_seed(text) or Wallet.is_xprv(text) or Wallet.is_private_key(
            text) else None
        try:
            wallet = Wallet.from_text(text, password, storage)
        except BaseException as e:
            sys.exit(str(e))
        if not config.get('offline'):
            network = Network(config)
            network.start()
            wallet.start_threads(network)
            log.info("Recovering wallet...")
            wallet.synchronize()
            wallet.wait_until_synchronized()
            msg = "Recovery successful" if wallet.is_found() else "Found no history for this wallet"
        else:
            msg = "This wallet was restored offline. It may contain more addresses than displayed."
        log.info(msg)
    elif cmdname == 'create':
        if client is True:  # From the GUI start
            password = config.get('guipassword', None)
        else:
            password = password_dialog()
        wallet = Wallet(storage)
        seed = wallet.make_seed()
        wallet.add_seed(seed, password)
        wallet.create_master_keys(password)
        wallet.create_main_account()
        wallet.synchronize()
        if client is True:
            wallet.storage.write()
            return seed
        else:
            print "Your wallet generation seed is:\n\"%s\"" % seed
            print "Please keep it in a safe place; if you lose it, you will not be able to restore " \
                  "your wallet."
    elif cmdname == 'deseed':
        wallet = Wallet(storage)
        if not wallet.seed:
            log.info("Error: This wallet has no seed")
        else:
            ns = wallet.storage.path + '.seedless'
            print "Warning: you are going to create a seedless wallet'\n" \
                  "It will be saved in '%s'" % ns
            if raw_input("Are you sure you want to continue? (y/n) ") in ['y', 'Y', 'yes']:
                wallet.storage.path = ns
                wallet.seed = ''
                wallet.storage.put('seed', '')
                wallet.use_encryption = False
                wallet.storage.put('use_encryption', wallet.use_encryption)
                for k in wallet.imported_keys.keys():
                    wallet.imported_keys[k] = ''
                wallet.storage.put('imported_keys', wallet.imported_keys)
                print "Done."
            else:
                print "Action canceled."
        wallet.storage.write()
    else:
        raise Exception("Unknown command %s" % cmdname)
    wallet.storage.write()
    log.info("Wallet saved in '%s'", wallet.storage.path)


def init_cmdline(config_options):
    config = SimpleConfig(config_options)
    client = config.get('client', None)
    cmdname = config.get('cmd')
    user = config.get('user')
    cmd = known_commands[cmdname]

    if cmdname == 'signtransaction' and config.get('privkey'):
        cmd.requires_wallet = False
        cmd.requires_password = False

    if cmdname in ['payto', 'paytomany'] and config.get('unsigned'):
        cmd.requires_password = False

    if cmdname in ['payto', 'paytomany'] and config.get('broadcast'):
        cmd.requires_network = True

    if cmdname in ['createrawtx'] and config.get('unsigned'):
        cmd.requires_password = False
        cmd.requires_wallet = False

    # instanciate wallet for command-line
    if user != None:
        storage = WalletStorage(config.get_wallet_path() + '//'+ user)

    else:
        storage = WalletStorage(config.get_wallet_path())

    if cmd.requires_wallet and not storage.file_exists:
        log.error("Error: Wallet file not found.")
        print "Type 'uwallet create' to create a new wallet, or provide a path to a wallet with " \
              "the -w option"
        sys.exit(0)

    # important warning
    if cmd.name in ['getprivatekeys']:
        print "WARNING: ALL your private keys are secret."
        print "Exposing a single private key can compromise your entire wallet!"
        print "In particular, DO NOT use 'redeem private key' services proposed by third parties."

    # commands needing password
    if cmd.requires_password and storage.get('use_encryption'):
        if config.get('password'):
            password = config.get('password')
        else:
            password = prompt_password('Password:', False)
            if not password:
                print "Error: Password required"
                sys.exit(1)
    else:
        password = None

    config_options['password'] = password

    if cmd.name == 'password':
        if client is True:
            new_password = config.get('guinewpassword', None)
            if not new_password:
                new_password = None
        else:
            new_password = prompt_password('New password:')
        config_options['new_password'] = new_password
    # print 'config_options', config_options
    return cmd, password


def run_offline_command(config, config_options):
    cmdname = config.get('cmd')
    user = config.get('user')
    print user
    cmd = known_commands[cmdname]
    storage = WalletStorage(config.get_wallet_path() + '//'+ user)
    wallet = Wallet(storage) if cmd.requires_wallet else None
    # check password
    if cmd.requires_password and storage.get('use_encryption'):
        password = config_options.get('password')
        try:
            seed = wallet.check_password(password)
        except InvalidPassword:
            print "Error: This password does not decode this wallet."
            sys.exit(1)
    # 此处没有必要 --hetao
    # if cmd.requires_network:
    #     print "Warning: running command offline"

    # arguments passed to function
    args = map(lambda x: config.get(x), cmd.params)
    # decode json arguments
    args = map(json_decode, args)
    # options
    args += map(lambda x: config.get(x), cmd.options)
    wallets = {}
    wallets[user] = wallet
    cmd_runner = Commands(config, wallets, None, password=config_options.get('password'),
                          new_password=config_options.get('new_password'))
    func = getattr(cmd_runner, cmd.name)
    result = func(*args)
    # save wallet
    if wallet:
        wallet.storage.write()
    return result


def main():
    # make sure that certificates are here
    assert os.path.exists(requests.utils.DEFAULT_CA_BUNDLE_PATH)

    # on osx, delete Process Serial Number arg generated for apps launched in Finder
    sys.argv = filter(lambda x: not x.startswith('-psn'), sys.argv)

    # old 'help' syntax
    if len(sys.argv) > 1 and sys.argv[1] == 'help':
        sys.argv.remove('help')
        sys.argv.append('-h')

    # read arguments from stdin pipe and prompt
    for i, arg in enumerate(sys.argv):
        if arg == '-':
            if not sys.stdin.isatty():
                sys.argv[i] = sys.stdin.read()
                break
            else:
                raise BaseException('Cannot get argument from stdin')
        elif arg == '?':
            sys.argv[i] = raw_input("Enter argument:")
        elif arg == ':':
            sys.argv[i] = prompt_password('Enter argument (will not echo):', False)
    # parse command line
    parser = get_parser()
    args = parser.parse_args()

    if args.verbose:
        logging.getLogger("uwallet").setLevel(logging.INFO)
    else:
        logging.getLogger("uwallet").setLevel(logging.ERROR)
    handler = logging.StreamHandler()
    handler.setFormatter(logging.Formatter("%(asctime)s %(levelname)-8s "
                                           "%(name)s:%(lineno)d: %(message)s"))
    logging.getLogger("uwallet").addHandler(handler)

    # config is an object passed to the various constructors (wallet, interface)
    config_options = args.__dict__
    for k, v in config_options.items():
        if v is None or (k in config_variables.get(args.cmd, {}).keys()):
            config_options.pop(k)
    # todo: 这里有何意义 --hetao
    if config_options.get('server'):
        config_options['auto_connect'] = False

    config = SimpleConfig(config_options)
    cmdname = config.get('cmd')
    client = config.get('client')
    user = config.get('user')
    # Determine whether a wallet exists
    if cmdname == 'haswallet':
        storage = WalletStorage(config.get_wallet_path() + '//'+ user)
        # Run the command with the Gui
        if client is True:
            if storage.file_exists:
                return True
            else:
                return False
        else:
            if storage.file_exists:
                sys.exit("The wallet already exists")
            else:
                sys.exit("The wallet not exists")

    if cmdname == 'haspassword':
        storage = WalletStorage(config.get_wallet_path()+ '//'+ user)
        # Run the command with the Gui
        if client is True:
            if storage.get('use_encryption'):
                return True
            else:
                return False
        else:
            if storage.get('use_encryption'):
                sys.exit("The wallet has the password")
            else:
                sys.exit("The wallet has not password")

    # run non-RPC commands separately
    if cmdname in ['create', 'restore', 'deseed']:
        rs = run_non_RPC(config)
        if client is True:
            return rs
        else:
            sys.exit(0)

    # check if a daemon is running
    server = get_daemon(config)

    if cmdname == 'daemonisrunning':
        if client is True:
            if server is not None:
                return True
            else:
                return False
        else:
            if server is not None:
                sys.exit("Daemon is running")
            else:
                sys.exit("Daemon stopped")

    result = None
    if cmdname == 'daemon':
        if server is not None:
            result = server.daemon(config_options)
        else:
            subcommand = config.get('subcommand')
            if subcommand in ['status', 'stop']:
                print "Daemon not running"
                if client is not True:
                    sys.exit(1)
            elif subcommand == 'start':
                if hasattr(os, "fork"):
                    # p = os.fork()
                    p = 0
                else:
                    log.warning("Cannot start uwallet daemon as a background process")
                    log.warning("To use uwallet commands, run them from a different window")
                    p = 0
                if p == 0:
                    network = Network(config)
                    network.start()
                    daemon = Daemon(config, network)
                    # daemon.runProc()
                    daemon.start()
                    daemon.server.serve_forever()
                    if client is not True:
                        daemon.join()
                        sys.exit(0)
                else:
                    print "starting daemon (PID %d)" % p
                    if client is not True:
                        sys.exit(0)
            else:
                print "syntax: uwallet daemon <start|status|stop>"
                if client is not True:
                    sys.exit(1)

    else:
        # command line
        init_cmdline(config_options)
        if server is not None:
            result = server.run_cmdline(config_options)
        else:
            cmd = known_commands[cmdname]
            if cmd.requires_network:
                print "Network daemon is not running. Try 'uwallet daemon start'"
                if client is not True:
                    sys.exit(1)
            else:
                result = run_offline_command(config, config_options)
    if client is True:
        return result
    else:
        print json.dumps(result, indent=2)
        sys.exit(0)


if __name__ == '__main__':
    import sys

    # -------- create wallet ----------------
    # sys.argv.append('create')
    # sys.argv.append('31f7e6703d5c11e893fcf48e3889c8ab_justin')

    # -------- create wallet ----------------
    # sys.argv.append('getbalance')
    # sys.argv.append('justin')

    # -------- list address ----------------
    # sys.argv.append('listaddresses')
    # sys.argv.append('justin')

    #  -------- send to address ----------------
    # sys.argv.append('paytoandsend')x
    # sys.argv.append('uWNsSvyHPTmwd2eUxhtLPXPSBZP6neJRfz')
    # sys.argv.append('50')
    # sys.argv.append('default_wallet')

    # -------- start daemon ----------------
    sys.argv.append('daemon')
    sys.argv.append('start')
    sys.argv.append('-v')

    print sys.argv
    rs = main()


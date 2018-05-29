import ast
import json
import logging
import os
import threading
import ConfigParser
from copy import deepcopy

from uwallet.util import user_dir

log = logging.getLogger(__name__)

SYSTEM_CONFIG_PATH = "/etc/uwallet.conf"

config = None
NULL = object()


def get_config():
    global config
    return config


def set_config(c):
    global config
    config = c

#TODO: modifi ip 
DEFAULT_CONFIG = {
    'default_servers': {
        # '192.168.14.253': {'t': '50001'},
        '114.67.37.2': {'t': '20531'},
        # '47.75.4.172': {'t': '50001'},
        # '119.27.161.117': {'t': '19888'},
    },
    'chain': 'ulord_main'
}


class SimpleConfig(object):
    """
    The SimpleConfig class is responsible for handling operations involving
    configuration files.

    There are 3 different sources of possible configuration values:
        1. Command line options.
        2. User configuration (in the user's config directory)
        3. System configuration (in /etc/)
    They are taken in order (1. overrides config options set in 2., that
    override config set in 3.)
    """

    def __init__(self, options={}, read_system_config_function=None,
                 read_user_config_function=None, read_user_dir_function=None,
                 default_config=None):
        default_config = default_config if default_config is not None else DEFAULT_CONFIG
        # This lock needs to be acquired for updating and reading the config in
        # a thread-safe way.
        self.lock = threading.RLock()

        # The following two functions are there for dependency injection when
        # testing.
        if read_system_config_function is None:
            read_system_config_function = read_system_config
        if read_user_config_function is None:
            read_user_config_function = read_user_config
        if read_user_dir_function is None:
            self.user_dir = user_dir
        else:
            self.user_dir = read_user_dir_function

        # The command line options
        self.cmdline_options = deepcopy(options)
        self.default_config = deepcopy(default_config)

        # Portable wallets don't use a system config
        if self.cmdline_options.get('portable', False):
            self.system_config = {}
        else:
            self.system_config = read_system_config_function()

        # Set self.path and read the user config
        self.user_config = {}  # for self.get in uwallet_path()
        self.path = self.uwallet_path()
        self.user_config = read_user_config_function(self.path)
        # Upgrade obsolete keys
        self.fixup_keys({'auto_cycle': 'auto_connect'})
        # Make a singleton instance of 'self'
        set_config(self)

    def uwallet_path(self):
        # Read uwallet_path from command line / system configuration
        # Otherwise use the user's default data directory.
        path = self.get('uwallet_path')
        if path is None:
            path = self.user_dir()

        # Make directory if it does not yet exist.
        if not os.path.exists(path):
            os.mkdir(path)

        log.info("uwallet directory: %s", path)
        return path

    def fixup_config_keys(self, config, keypairs):
        updated = False
        for old_key, new_key in keypairs.iteritems():
            if old_key in config:
                if new_key not in config:
                    config[new_key] = config[old_key]
                del config[old_key]
                updated = True
        return updated

    def fixup_keys(self, keypairs):
        """Migrate old key names to new ones"""
        self.fixup_config_keys(self.cmdline_options, keypairs)
        self.fixup_config_keys(self.system_config, keypairs)
        if self.fixup_config_keys(self.user_config, keypairs):
            self.save_user_config()

    def set_key(self, key, value, save=True):
        if not self.is_modifiable(key):
            log.error("Warning: not changing config key '%s' set on the command line", key)
            return

        with self.lock:
            self.user_config[key] = value
            if save:
                self.save_user_config()
        return

    def get(self, key, default=None):
        configs = (
            self.cmdline_options,
            self.user_config,
            self.system_config,
            self.default_config,
        )
        with self.lock:
            for config in configs:
                out = config.get(key, NULL)
                if out is not NULL:
                    break
        return out if out is not NULL else default

    def is_modifiable(self, key):
        return key not in self.cmdline_options

    def save_user_config(self):
        if not self.path:
            return
        path = os.path.join(self.path, "config")
        s = json.dumps(self.user_config, indent=4, sort_keys=True)
        f = open(path, "w")
        f.write(s)
        f.close()

    def get_wallet_path(self):
        """Set the path of the wallet."""

        # command line -w option
        path = self.get('wallet_path')
        if path:
            return path

        # path in config file
        path = self.get('default_wallet_path')
        if path and os.path.exists(path):
            return path

        # default path
        dirpath = os.path.join(self.path, "wallets")
        if not os.path.exists(dirpath):
            os.mkdir(dirpath)

        new_path = os.path.join(self.path, "wallets", "")
        #new_path = os.path.join(self.path, "wallets", "default_wallet")

        # default path in pre 1.9 versions
        old_path = os.path.join(self.path, "uwallet.dat")
        if os.path.exists(old_path) and not os.path.exists(new_path):
            os.rename(old_path, new_path)

        return new_path

    def remove_from_recently_open(self, filename):
        recent = self.get('recently_open', [])
        if filename in recent:
            recent.remove(filename)
            self.set_key('recently_open', recent)


def read_system_config(path=SYSTEM_CONFIG_PATH):
    """Parse and return the system config settings in /etc/uwallet.conf."""
    result = {}
    if os.path.exists(path):
        p = ConfigParser.ConfigParser()
        try:
            p.read(path)
            for k, v in p.items('client'):
                result[k] = v
        except (ConfigParser.NoSectionError, ConfigParser.MissingSectionHeaderError):
            pass

    return result


def read_user_config(path):
    """Parse and store the user config settings in uwallet.conf into user_config[]."""
    if not path:
        return {}
    config_path = os.path.join(path, "config")
    if not os.path.exists(config_path):
        return {}
    with open(config_path, "r") as f:
        data = f.read()
    try:
        result = json.loads(data)
    except Exception:
        try:
            result = ast.literal_eval(data)
        except Exception:
            raise Exception('Failed to parse config file {}'.format(config_path))
    if not isinstance(result, dict):
        raise Exception('User config must be a dictionary')
    return result

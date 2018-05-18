from jsonrpclib import Server
import thread
import time

metadata = {
    "license": "LBRY Inc",
    "description": "What is LBRY? An introduction with Alex Tabarrok",
    "language": "en",
    "title": "What is LBRY?",
    "author": "Samuel Bryan",
    "version": "_0_1_0",
    "nsfw": False,
    "licenseUrl": "",
    "preview": "",
    "thumbnail": "https://s3.amazonaws.com/files.lbry.io/logo.png",
}

sourceHash = "d5169241150022f996fa7cd6a9a1c421937276a3275eb912790bd07ba7aec1fac5fd45431d226b8fb402691e79aeb24b"

contentType = "video/mp4"

currency = "UT"
amount = 1.2
server = Server('http://127.0.0.1:8000')


def main():
    print server.getbalance('wallet0',None)
    # thread.start_new_thread(createw,('a',))
    # thread.start_new_thread(createw,('b',))
    # thread.start_new_thread(createw,('c',))
    # thread.start_new_thread(createw,('d',))
    # thread.start_new_thread(createw,('e',))
    # thread.start_new_thread(createw,('f',))
    # thread.start_new_thread(createw,('g',))
    # thread.start_new_thread(createw,('h',))
    # thread.start_new_thread(createw,('i',))
    # thread.start_new_thread(createw,('j',))
    # thread.start_new_thread(createw,('k',))
    # thread.start_new_thread(createw,('l',))
    # thread.start_new_thread(createw,('m',))
    # thread.start_new_thread(createw,('n',))
    # thread.start_new_thread(createw,('aa',))
    # thread.start_new_thread(createw,('cc',))
    # thread.start_new_thread(createw,('dd',))
    # thread.start_new_thread(createw,('ee',))
    # thread.start_new_thread(createw,('ff',))
    # thread.start_new_thread(createw,('ccc',))
    # thread.start_new_thread(createw,('qq',))
    # thread.start_new_thread(createw,('as',))
    # thread.start_new_thread(createw,('ss2',))
    # thread.start_new_thread(createw,('asd',))
    # thread.start_new_thread(createw,('ds',))
    # thread.start_new_thread(createw,('dsa',))
    # thread.start_new_thread(createw,('dasd',))
    # thread.start_new_thread(createw,('ssas',))
    # thread.start_new_thread(createw,('fss',))
    # thread.start_new_thread(createw,('tf',))
    # thread.start_new_thread(createw,('tfs',))
    # thread.start_new_thread(createw,('sew',))
    # thread.start_new_thread(createw,('sdfe',))
    # thread.start_new_thread(createw,('fde',))
    # thread.start_new_thread(createw,('sdf',))
    # for i in range(70):
    #     createw(i)
    #     i += 1

    # thread.start_new_thread(m1)
    # thread.start_new_thread(m2)
    # thread.start_new_thread(m3)
    # thread.start_new_thread(m4)
    # thread.start_new_thread(m5)
    raw_input()


def createw(name):
    wname = 'wallet' + str(name)
    s = server.rpcCreate(wname,None)
    print s


def m1():
    # res = server.listaddresses('default_wallet')
    res = server.publish('3', 'hetao000bgyy001', 0.15, metadata, contentType, sourceHash, currency, amount)
    print res


def m2():
    # res = server.listaddresses('1')
    res = server.publish('6', 'hetao000bgyy002', 0.15, metadata, contentType, sourceHash, currency, amount)
    print res


def m3():
    # res = server.listaddresses('2')
    res = server.publish('7', 'hetao000bgyy003', 0.15, metadata, contentType, sourceHash, currency, amount)
    print res


def m4():
    # res = server.listaddresses('3')
    res = server.publish('8', 'hetao000bgyy004', 0.15, metadata, contentType, sourceHash, currency, amount)
    print res


def m5():
    # res = server.listaddresses('4')
    res = server.publish('9', 'hetao000bgyy005', 0.15, metadata, contentType, sourceHash, currency, amount)
    print res


if __name__ == '__main__':
    main()
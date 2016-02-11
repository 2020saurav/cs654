#!/usr/bin/python3
from socket   import *
from time     import sleep
from timeout  import timeout
from config   import slaves
from datetime import datetime

import sys

red   = '\033[31m'
green = '\033[32m'
bold  = '\033[1m'
end   = '\033[0m'

@timeout(1, 'TIMEOUT')
def ping(ip):
    try:
        s = socket()
        s.connect((ip, 2020))
        s.send(b'PING')
        resp = s.recv(4)
        s.close()
        return resp
    except Exception as e:
        return str(e)

def printStatus(slave):
    log = 'SERVER: ' + slave['name'] + ' (' + slave['ip'] + ')' + '\t STATUS: '
    if slave['alive']:
        log = log + green + bold + 'ALIVE' + end + end + ' '*10
    else:
        log = log + red   + bold + 'DEAD'  + end + end + ' '*10
    sys.stdout.write('%s\n' % log)
    sys.stdout.flush()

def printStart(slave):
    log = 'SERVER: ' + slave['name'] + ' (' + slave['ip'] + ')' + '\t STATUS: PINGING ...'
    sys.stdout.write('%s\r' % log)
    sys.stdout.flush()

if __name__ == '__main__':
    while True:
        print '\nPING at ' + str(datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
        for slave in slaves:
            printStart(slave)
            resp = ping(slave['ip'])
            if resp == 'PONG':
                slave['alive'] = True
            elif resp == 'TIMEOUT':
                slave['alive'] = False
            else:
                slave['alive'] = False
            printStatus(slave)
        sleep(2)

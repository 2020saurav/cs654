#!/usr/bin/python3
from socket   import *
from config   import *
from time     import *
from datetime import datetime
from time     import time
import thread

PORT      = 2020
sleepTime = 3

red   = '\033[31m'
green = '\033[32m'
bold  = '\033[1m'
end   = '\033[0m'

def logger():
    while True:
        sleep(sleepTime)
        now = time()
        print '\nHEARTBEAT at ' + str(datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
        for slave in slaves:
            log = 'SERVER: ' + slave['name'] + ' (' + slave['ip'] + ')' + '\t STATUS: '
            if (now - slave['lastSeen'] > 2 * sleepTime):
                slave['alive'] = False
                log = log + red   + bold + 'DEAD'  + end + end
            else:
                slave['alive'] = True
                log = log + green + bold + 'ALIVE' + end + end
            log = log + '\t INFO: ' + slave['info']
            print log

def process(msg, ip):
    strings = msg.split(' ')
    for slave in slaves: # using set/dictionary will be little faster
        if (slave['ip'] == ip):
            slave['lastSeen'] = time()
            slave['info']     = strings[1]
            break


if __name__ == '__main__':
    s = socket(AF_INET, SOCK_STREAM)
    s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
    s.bind(('', PORT))
    s.listen(20)
    print ('Listening on ' + gethostbyname(gethostname()) + ':' + str(PORT))
    thread.start_new_thread(logger, ())
    while True:
        try:
            sc, address = s.accept()
            msgLen = int(sc.recv(5))
            msg    = sc.recv(msgLen)
            process(msg, str(address[0]))
            sc.close()
        except Exception as e:
            print (e)
    s.close()

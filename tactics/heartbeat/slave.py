#!/usr/bin/python3
from socket import *
from time   import sleep
from random import randint

masterIP   = '172.24.1.62'
masterPort = 2020

def msgLen(msg):
    return (('%05d')%len(msg))

def sendHeartBeat():
    try:
        s = socket()
        s.connect((masterIP, masterPort))
        info = str(randint(1, 1000))
        msg  = 'HEARTBEAT ' + info
        s.send(str.encode(msgLen(msg)))
        s.send(str.encode(msg))
        s.close()
    except Exception as e:
        print (e)

if __name__ == '__main__':
    while True:
        sendHeartBeat()
        sleep(2)

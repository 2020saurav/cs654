#!/usr/bin/python3
from socket import *

PORT = 2020

if __name__ == '__main__':
    s = socket(AF_INET, SOCK_STREAM)
    s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
    s.bind(('', PORT))
    s.listen(5)
    print ('Listening on ' + gethostbyname(gethostname()) + ':' + str(PORT))
    while True:
        sc, address = s.accept()
        msg = sc.recv(4)
        if msg == b'PING':
            sc.send(b'PONG')
        else:
            sc.send(b'XXXX')
        sc.close()
    s.close()

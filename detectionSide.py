import socket
import time
import pickle
import threading
from PIL import Image
import io
import random


dataToSend = []
dataReceived = []

def sendThread():
    while(1):
        HEADERSIZE = 10
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((socket.gethostname(), 12243))
        s.listen(5)
        d = {}
        if(len(dataToSend)>0):
            #print(len(dataToSend))
            clientsocket, address = s.accept()
            print(f"Connection from {address} has been established.")
            msg = dataToSend[0]
            msg = pickle.dumps(msg)
            msg = bytes(f"{len(msg):<{HEADERSIZE}}", 'utf-8') + msg
            print(msg)
            clientsocket.send(msg)
            del dataToSend[0]
        else:
            time.sleep(1)


def recieveThread():
    while True:
        try:
            HEADERSIZE = 10

            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect((socket.gethostname(), 1243))
            full_msg = b''
            new_msg = True
            while True:
                msg = s.recv(4096)
                if new_msg:
                    print("new msg len:", msg[:HEADERSIZE])
                    msglen = int(msg[:HEADERSIZE])
                    new_msg = False

                print(f"full message length: {msglen}")

                full_msg += msg

                print(len(full_msg))

                if len(full_msg) - HEADERSIZE == msglen:
                    print("full msg recvd")
                    print(full_msg[HEADERSIZE:])
                    d = pickle.loads(full_msg[HEADERSIZE:])
                    dataReceived.append(d)
                    new_msg = True
                    full_msg = b""
                    break
        except:
            time.sleep(1)


def runDetect(dataToTest):
    print("in the detect")
    index = dataToTest.get("index")
    image = dataToTest.get("image")
    image = Image.open(io.BytesIO(image))

    r = random.randint(1, 101)
    image.save("img"+str(r)+".jpg")
    r = {}
    r['index'] = index
    dataToSend.append(r)



def Main():
    thread = threading.Thread(target=sendThread)
    thread.start()
    thread2 = threading.Thread(target=recieveThread)
    thread2.start()
    while(1):
        if len(dataReceived) > 0:
            runDetect(dataReceived[0])
            del dataReceived[0]
        else:
            time.sleep(.5)



if __name__ == '__main__':
    Main()

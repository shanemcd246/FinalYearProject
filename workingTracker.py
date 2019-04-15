import cv2
import imutils
import numpy as np
import time
import pickle
import socket
import threading
import os
from PIL import Image
import io
import sys

global trackCount
trackCount =1;

lock = threading.Lock()



cap = cv2.VideoCapture(0)
time.sleep(2.0)
myDictList = []
sendDictList = []
dataToSend = []
dataRecived = []

def recieveThread():
    global lock
    while True:
        try:
            HEADERSIZE = 10
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect((socket.gethostname(), 12243))
            full_msg = b''
            new_msg = True
            while True:
                msg = s.recv(5)
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
                    print("GOT..................."+str(d.get("index")))
                    dataRecived.append(d)
                    new_msg = True
                    full_msg = b""
                    break

        except:
            time.sleep(1)

def sendThread():
    global lock
    while(1):
        HEADERSIZE = 10
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((socket.gethostname(), 1243))
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
            lock.acquire()
            del dataToSend[0]
            lock.release()
        else:
            time.sleep(1)

def addImage(image, index):
    print("YOOOOOOOOOOOOOOOOOOOOO")
    e = {}
    cv2.imwrite('temp.jpg', image)
    with open('temp.jpg', 'rb') as f:
        Nimage = f.read()
    e['image'] = Nimage
    e['index'] = index
    dataToSend.append(e)
    os.remove("temp.jpg")



def updatetime():
    listsToDelete =[]
    for x in range(len(myDictList)):
        #print(len(myDictList))
        if myDictList[x].get("time") > 60:
            listsToDelete.insert(0, x)
        else:
            myDictList[x].update({"time": (myDictList[x].get("time") + 1)})
            myDictList[x].update({"sendTime": (myDictList[x].get("sendTime") + 1)})
    if len(listsToDelete) > 0:
        for x in range(len(listsToDelete)):
            del myDictList[listsToDelete[x]]


def trackitem(loc_x, loc_y):
    global trackCount
    if len(myDictList) <= 0:
        item = {"index": trackCount, "locX": loc_x, "locY": loc_y, "detected": 0, "time": 0, "sendTime": 0, "sprayed": 0}
        myDictList.append(item)
        trackCount += 1
        return myDictList[0]
    else:
        cIndex = None
        cDiff = None
        for x in range(len(myDictList)):
            xDiff = abs(loc_x - myDictList[x].get("locX"))
            yDIff = abs(loc_y - myDictList[x].get("locY"))

            if (xDiff + yDIff) < 70:
                if cDiff ==None:
                    cDiff = xDiff + yDIff
                    cIndex = x
                elif (xDiff + yDIff) < cDiff:
                    cDiff = xDiff + yDIff
                    cIndex = x

        if cIndex == None:
            item = {"index": trackCount, "locX": loc_x, "locY": loc_y, "detected": 0, "time": 0, "sendTime": 0, "sprayed": 0}
            myDictList.append(item)
            trackCount += 1
            return myDictList[len(myDictList) - 1]
        else:
            myDictList[cIndex].update({"locX": loc_x})
            myDictList[cIndex].update({"locY": loc_y})
            myDictList[cIndex].update({"time": 0})
            return myDictList[cIndex]


def checkForMatches(matches):
    for q in range(len(myDictList)):
        if myDictList[q].get("index") == matches.get("index"):
            myDictList[q].update({"detected": 1})

def spray(x,y):
    print("Sprayed " + str(x) + " " + str(y))


def main():
    thread = threading.Thread(target=sendThread)
    thread.start()
    thread2 = threading.Thread(target=recieveThread)
    thread2.start()
    while(True):
        success, frame = cap.read()
        frame = imutils.resize(frame, width=600)
        dummyFrame = imutils.resize(frame, width=600)
        (H, W) = dummyFrame.shape[:2]
        #cv2.imshow('OG', frame)
        blurred_frame = cv2.GaussianBlur(dummyFrame, (15, 15), 0)

        # Convert BGR to HSV
        hsv = cv2.cvtColor(blurred_frame, cv2.COLOR_BGR2HSV)
        sensitivity = 25

        lower_green = np.array([50 - sensitivity, 50, 60])
        upper_green = np.array([70 + sensitivity, 255, 255])
        mask = cv2.inRange(hsv, lower_green, upper_green)  # I have the Green threshold image.

        contours, _ = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
        rects = []
        for contour in contours:
            area = cv2.contourArea(contour)
            if area > 4000:
                cv2.drawContours(dummyFrame, contour, -1, (0, 255, 0), 3)
                rects.append(cv2.boundingRect(contour))
                x, y, w, h = cv2.boundingRect(contour)
                d = trackitem(round(x+(w*.5)), round(y+(h*.5)))
                cv2.putText(dummyFrame, "Detected:"+str(d.get("detected")), (round(x+(w*.5)), round(y+(h*.5))), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 255))
                if d.get("detected") == 0 and d.get("sendTime") > 30:
                    for q in range(len(myDictList)):
                        if myDictList[q].get("index") == d.get("index"):
                            myDictList[q].update({"sendTime": 0})
                    crop_img = frame[y:y + h, x:x + w]
                    addImage(crop_img, d.get("index"))
                elif d.get("detected") == 1 and d.get("sprayed") == 0:
                    spray(d.get("locX"), d.get("locY"))
                    for q in range(len(myDictList)):
                        if myDictList[q].get("index") == d.get("index"):
                            myDictList[q].update({"sprayed": 1})

        updatetime()
        if len(dataRecived) > 0:
            print("OOOOOOOOOOOOOOOOOOOOOOo there is something")
            checkForMatches(dataRecived[0])
            del dataRecived[0]

        cv2.imshow('frame', frame)
        cv2.imshow('dummyFrame', dummyFrame)
        cv2.imshow('mask', mask)

        # Esc to quit
        if cv2.waitKey(1) & 0xFF == 27:
            break

    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()

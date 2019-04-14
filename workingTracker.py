import cv2
import imutils
import numpy as np
import time
import pickle
import socket
import base64

global trackCount
trackCount =1;

cap = cv2.VideoCapture(0)
time.sleep(2.0)

myDictList = []

sendDictList = []

def sendImage(image, index):
    try:
        HOST = 'localhost'
        PORT = 12345
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((HOST, PORT))
        imageN = cv2.imencode('.jpg', image)[1].tostring()
        sendDict = {'image': imageN, 'index': index}
        data_string = pickle.dumps(sendDict)
        s.send(data_string)
        print("Sending.....")
    except:
        print("Error When Sending")

    #cv2.imshow("img",image)


def updatetime():
    listsToDelete =[]
    for x in range(len(myDictList)):
        print(len(myDictList))
        if myDictList[x].get("time") > 60:
            listsToDelete.insert(0, x)
        else:
            myDictList[x].update({"time": (myDictList[x].get("time") + 1)})
    if len(listsToDelete) > 0:
        for x in range(len(listsToDelete)):
            del myDictList[listsToDelete[x]]


def trackitem(loc_x, loc_y):
    global trackCount
    if len(myDictList) <= 0:
        item = {"index": trackCount, "locX": loc_x, "locY": loc_y, "detected": 0, "time": 0}
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
            item = {"index": trackCount, "locX": loc_x, "locY": loc_y, "detected": 0, "time": 0}
            myDictList.append(item)
            trackCount += 1
            return myDictList[len(myDictList) - 1]
        else:
            myDictList[cIndex].update({"locX": loc_x})
            myDictList[cIndex].update({"locY": loc_y})
            myDictList[cIndex].update({"time": 0})
            return myDictList[cIndex]


def main():
    while(True):
        success, frame = cap.read()
        frame = imutils.resize(frame, width=600)
        (H, W) = frame.shape[:2]
        #cv2.imshow('OG', frame)
        blurred_frame = cv2.GaussianBlur(frame, (15, 15), 0)

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
                cv2.drawContours(frame, contour, -1, (0, 255, 0), 3)
                rects.append(cv2.boundingRect(contour))
                x, y, w, h = cv2.boundingRect(contour)
                d = trackitem(round(x+(w*.5)), round(y+(h*.5)))
                cv2.putText(frame, "Detected:"+str(d.get("detected")), (round(x+(w*.5)), round(y+(h*.5))), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 255))
                if d.get("detected") == 0:
                    crop_img = frame[y:y + h, x:x + w]
                    sendImage(crop_img, d.get("index"))
        updatetime()

        cv2.imshow('frame', frame)
        cv2.imshow('mask', mask)

        # Esc to quit
        if cv2.waitKey(1) & 0xFF == 27:
            break


if __name__ == "__main__":
    main()

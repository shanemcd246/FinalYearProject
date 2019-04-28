import socket
import time
import pickle
import threading
from PIL import Image
import io
import random
import tensorflow as tf
import numpy as np
import mysql.connector
import os
import datetime


TfModel = "c://ForDemo2//output_graph.pb"
TfName = "c://ForDemo2//output_labels.txt"
inputLayer = "Placeholder"
outputLayer = "final_result"
Ugraph = None
doubleCheckEnabled = False

dataToSend = []
dataReceived = []
alreadyDetected = []
doubleCheck = []

lock = threading.Lock()


def addToDatabase(name, score, image, detected, lon, lat):
    mydb = mysql.connector.connect(host="127.0.0.1", user="root", passwd="root")
    mycursor = mydb.cursor()
    mycursor.execute("SHOW DATABASES")
    dbList =[]
    for x in mycursor:
        dbList.append(str(x))
    print("Adding "+name+" to Database....")
    print("@@@@@@@@@@@@@@@@@@@@@")
    if "('finalyearproject',)" not in dbList:
        mycursor.execute("CREATE DATABASE finalYearProject")
    if detected ==1:
        today = datetime.date.today()
        today = str(today.day) + "_" + str(today.month) + "_" + str(today.year)
        mydb = mysql.connector.connect(host="127.0.0.1", user="root", passwd="root", database="finalYearProject")
        mycursor = mydb.cursor()
        sql = "INSERT INTO feildrun_"+today+" (PLANTNAME, SCORE, IMAGE, LON, LAT) VALUES (%s, %s, %s, %s, %s)"
        val = (name, str(score), image, lon, lat)
        try:
            mycursor.execute("CREATE TABLE feildrun_"+today+" (ID INT AUTO_INCREMENT PRIMARY KEY, PLANTNAME VARCHAR(255), SCORE VARCHAR(255), IMAGE VARCHAR(255), LON VARCHAR(255), LAT VARCHAR(255))")
        except:
            pass
        mycursor.execute(sql, val)
    else:
        mydb = mysql.connector.connect(host="127.0.0.1", user="root", passwd="root", database="finalYearProject")
        mycursor = mydb.cursor()
        sql = "INSERT INTO feildrun_notdetected (PLANTNAME, SCORE, IMAGE, LON, LAT) VALUES (%s, %s, %s, %s, %s)"
        val = ("", "", image, lon, lat)
        try:
            mycursor.execute("CREATE TABLE feildrun_notdetected (ID INT AUTO_INCREMENT PRIMARY KEY, PLANTNAME VARCHAR(255), SCORE VARCHAR(255), IMAGE VARCHAR(255), LON VARCHAR(255), LAT VARCHAR(255))")
        except:
            pass
        mycursor.execute(sql, val)
    mydb.commit()


def sendThread():
    global lock
    HOST = '192.168.0.100'
    while(1):
        HEADERSIZE = 10
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.bind((HOST, 12243))
        #s.bind((socket.gethostname(), 12243))
        s.listen(5)
        if(len(dataToSend)>0):
            #print(len(dataToSend))
            clientsocket, address = s.accept()
            #print(f"Connection from {address} has been established.")
            msg = dataToSend[0]
            msg = pickle.dumps(msg)
            msg = bytes(f"{len(msg):<{HEADERSIZE}}", 'utf-8') + msg
            #print(msg)
            clientsocket.send(msg)
            lock.acquire()
            try:
                del dataToSend[0]
            finally:
                lock.release()
        else:
            #time.sleep(.5)
            pass


def recieveThread():
    global lock
    HOST = '192.168.0.102'
    while True:
        try:
            HEADERSIZE = 10

            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect((HOST, 12345))
            #s.connect((socket.gethostname(), 1243))
            full_msg = b''
            new_msg = True
            while True:
                msg = s.recv(4096)
                if new_msg:
                    #print("new msg len:", msg[:HEADERSIZE])
                    msglen = int(msg[:HEADERSIZE])
                    new_msg = False

                #print(f"full message length: {msglen}")

                full_msg += msg

                #print(len(full_msg))

                if len(full_msg) - HEADERSIZE == msglen:
                    print("full msg recvd")
                    #print(full_msg[HEADERSIZE:])
                    d = pickle.loads(full_msg[HEADERSIZE:])
                    dataReceived.append(d)
                    new_msg = True
                    full_msg = b""
                    break
        except:
            #time.sleep(1)
            pass


def runDetect(dataToTest):
    global lock
    index = dataToTest.get("index")
    if index in alreadyDetected:
        pass
    else:
        print("in the detect")
        doneIt = False
        if len(doubleCheck) > 0:
            for x in range(len(doubleCheck)):
                if doubleCheck[x].get("index") == index:
                    doneIt = True
        else:
            item = {"index": index, "mCheck": 0}
            doubleCheck.append(item)
        if not doneIt:
            item = {"index": index, "mCheck": 0}
            doubleCheck.append(item)
            doneIt = True

        image = dataToTest.get("image")
        image = Image.open(io.BytesIO(image))
        timeStamp = getTime();
        image.save("img"+timeStamp+".jpg")

        with open("img"+timeStamp+".jpg", 'rb') as file:
            binaryData = file.read()

        imageToRead = ("img" + "%s.jpg" % (timeStamp))
        tensor = TensorImage(imageToRead)

        #image3 = Image.open(io.BytesIO(tensor))
        #cv2.imshow("fdsf",tensor)

        inputOp = Ugraph.get_operation_by_name("import/" + inputLayer)
        outputOp = Ugraph.get_operation_by_name("import/" + outputLayer)
        with tf.Session(graph=Ugraph) as sess:
            results = sess.run(outputOp.outputs[0], {
                inputOp.outputs[0]: tensor
            })

        results = np.squeeze(results)
        inOrderResults = results.argsort()[-1:][::-1]
        gotDetect = False
        for i in inOrderResults:
            if (results[i] > .94):
                gotDetect = True
                newLoc = "C:/Users/shane/PycharmProjects/untitled/Detected/"+str(listOfNames[i])+"_"+\
                         str(results[i])+"_"+timeStamp+".jpg"
                os.rename("img" + "%s.jpg" % (timeStamp), newLoc)
                print(results[i],listOfNames[i])
                if doubleCheckEnabled:
                    for q in range(len(doubleCheck)):
                        if doubleCheck[q].get("index") == index:
                            if listOfNames[i] == doubleCheck[q].get("mCheck"):
                                #print("DFSHFHSFHGFHFHSHSG")
                                getReadyForSend(newLoc,listOfNames[i],results[i],index,1,dataToTest.get("lon"),
                                                dataToTest.get("lat"))
                            else:
                                doubleCheck[q].update({"mCheck": listOfNames[i]})
                else:
                    #print("DFSHFHSFHGFHFHSHSG")
                    getReadyForSend(newLoc, listOfNames[i], results[i], index, 1, dataToTest.get("lon"),
                                    dataToTest.get("lat"))
        if not gotDetect:
            newLoc = "C:/Users/shane/PycharmProjects/untitled/NotDetected/img" + str(timeStamp)+ ".jpg"
            os.rename("img" + "%s.jpg" % (timeStamp), newLoc)
            getReadyForSend(newLoc, "", "", index, 0,dataToTest.get("lon"),dataToTest.get("lat"))

def TensorImage(inputImg):
    fReader = tf.read_file(inputImg, "file_reader")
    imgReader = tf.image.decode_jpeg(fReader, channels=3, name="jpeg_reader")
    castFloat = tf.cast(imgReader, tf.float32)
    expandDim = tf.expand_dims(castFloat, 0)
    resized = tf.image.resize_bilinear(expandDim, [224, 224])
    normalized = tf.divide(tf.subtract(resized, [0]), [255])
    sess = tf.Session()
    result = sess.run(normalized)
    return result


def getReadyForSend(image, name, score, index,detected,lon,lat):
    print("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    if index not in alreadyDetected:
        thread3 = threading.Thread(target=addToDatabase, args=(name,score,image,detected,lon,lat))
        thread3.start()
    if detected == 1:
        r = {}
        r['index'] = index
        r['name'] = name
        print("Sending "+name+ "Index: "+str(index))
        dataToSend.append(r)
        alreadyDetected.append(index)


def GraphToMemory():
  global Ugraph
  Ugraph = tf.Graph()
  graphDef = tf.GraphDef()
  with open(TfModel, "rb") as m:
    graphDef.ParseFromString(m.read())
  with Ugraph.as_default():
    tf.import_graph_def(graphDef)
  #print("Im Here")


def GetNames(label_file):
  label = []
  proto_as_ascii_lines = tf.gfile.GFile(label_file).readlines()
  for l in proto_as_ascii_lines:
    label.append(l.rstrip())
  return label

def getTime():
    todaysDateTime = str(datetime.datetime.now())
    time = todaysDateTime.split(" ")
    calender = time[0].split("-")
    year = calender[0]
    month = calender[1]
    day = calender[2]
    clock = time[1].split(":")
    hours = clock[0]
    minuets = clock[1]
    second = clock[2].split(".")
    second = second[0]
    dateTimeNow = str(day + "_" + month + "_" + year + "_" + hours + "_" + minuets + "_" + second)
    return dateTimeNow


def Main():
    global lock
    thread = threading.Thread(target=sendThread)
    thread.start()
    thread2 = threading.Thread(target=recieveThread)
    thread2.start()
    global listOfNames
    listOfNames = GetNames(TfName)
    GraphToMemory()
    while(1):
        if len(dataReceived) > 0:
            runDetect(dataReceived[0])
            lock.acquire()
            try:
                del dataReceived[0]
            finally:
                lock.release()
        else:
            time.sleep(.3)


if __name__ == '__main__':
    Main()

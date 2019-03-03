from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import argparse
import cv2 as cv
import numpy as np
import tensorflow as tf
import mysql.connector
import datetime

import os

deviceID =0
hsvSen = 25
TfModel = "c://tmp//output_graph.pb"
TfName = "c://tmp//output_labels.txt"
inputLayer = "Placeholder"
outputLayer = "final_result"
Ugraph = None
imageFolder = "DetectedImages//"
DoubleCheck=[]
listOfNames=[]

def SprayLocation(x,y,h,w,time):
  print("Sprayed "+str((x+(w/2)))+str((y+(h/2)))+ "at: "+str(time))

def GraphToMemory():
  global Ugraph
  Ugraph = tf.Graph()
  graphDef = tf.GraphDef()
  with open(TfModel, "rb") as m:
    graphDef.ParseFromString(m.read())
  with Ugraph.as_default():
    tf.import_graph_def(graphDef)
  #print("Im Here")
  

def addToDatabase(name,score,image):
  mydb = mysql.connector.connect(host="127.0.0.1",user="root",passwd="root")
  mycursor = mydb.cursor()
  mycursor.execute("SHOW DATABASES")
  dbList =[]
  for x in mycursor:
      dbList.append(str(x))
  print("Adding "+name+" to Database....")
  if "('finalyearproject',)" not in dbList:
      mycursor.execute("CREATE DATABASE finalYearProject")
  mydb = mysql.connector.connect(host="127.0.0.1",user="root",passwd="root",database="finalYearProject")
  mycursor = mydb.cursor()
  sql = "INSERT INTO detected (NAME, SCORE, IMAGE) VALUES (%s, %s, %s)"
  val = (name,str(score),image)
  #mycursor.execute("CREATE TABLE detected (ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255), SCORE VARCHAR(255), IMAGE BLOB)")
  mycursor.execute(sql, val)
  mydb.commit()

def TensorImage(inputImg):
  output_name = "normalized"
  fReader = tf.read_file(inputImg, "file_reader")
  imgReader = tf.image.decode_jpeg(fReader, channels=3, name="jpeg_reader")
  castFloat = tf.cast(imgReader, tf.float32)
  expandDim = tf.expand_dims(castFloat, 0)
  resized = tf.image.resize_bilinear(expandDim, [224, 224])
  normalized = tf.divide(tf.subtract(resized, [0]), [255])
  sess = tf.Session()
  result = sess.run(normalized)
  return result

def GetNames(label_file):
  label = []
  proto_as_ascii_lines = tf.gfile.GFile(label_file).readlines()
  for l in proto_as_ascii_lines:
    label.append(l.rstrip())
  #print("loadlables")
  return label

def Recignition(imageToRead,x,y,w,h):
  global Ugraph
  global DoubleCheck
  global listOfNames
  tensor = TensorImage(imageToRead)
  #print(Ugraph)
  inputOp = Ugraph.get_operation_by_name("import/" + inputLayer)
  outputOp = Ugraph.get_operation_by_name("import/" + outputLayer)
  with tf.Session(graph=Ugraph) as sess:
    results = sess.run(outputOp.outputs[0], {
        inputOp.outputs[0]: tensor
    })
  results = np.squeeze(results)
  inOrderResults = results.argsort()[-1:][::-1]
  for i in inOrderResults:
    if(results[i]>.90):
      print(listOfNames[i], results[i])
      DoubleCheck.append(listOfNames[i])
      if(len(DoubleCheck) >=2):
        if(DoubleCheck[0] == DoubleCheck[1]):
          recPhoto = imageFolder+listOfNames[i]+"_"+str(results[i])+".jpg"
          os.rename(imageToRead,recPhoto)
          currentTime = datetime.datetime.now()
          SprayLocation(x,y,h,w,currentTime)
          with open(recPhoto, 'rb') as I:
            weedPicture = I.read()
          addToDatabase(listOfNames[i],str(results[i]),weedPicture)
        DoubleCheck.clear()
      else:
        os.remove(imageToRead)
    else:
      os.remove(imageToRead)
  print("rec")

def detectBox(x,y,w,h,frame):
  cropedImg = frame[y:y+h,x:x+w]
  #print("%d%d%d%d.jpg" %(x,y,h,w))
  cv.imwrite(imageFolder+"%d%d%d%d.jpg" %(x,y,h,w), cropedImg)
  imageToRead = (imageFolder+"%d%d%d%d.jpg" %(x,y,h,w))
  Recignition(imageToRead,x,y,w,h)
  print("detectBox")

def init():
  global listOfNames
  GraphToMemory()
  listOfNames = GetNames(TfName)
  #print(Ugraph)
  print("init")

def main():
  video = cv.VideoCapture(deviceID)
  while True:
    ret, oGFrame = video.read()
    #cv.imshow("OGframe", oGFrame)
    blurredFrame = cv.GaussianBlur(oGFrame,(17,17),0)
    #cv.imshow("blurredFrame", blurredFrame)
    hsvFrame = cv.cvtColor(blurredFrame, cv.COLOR_BGR2HSV)
    #cv.imshow("hsvFrame", hsvFrame)
    #Green color
    lRange = np.array([60 - hsvSen, 60, 30], dtype=np.uint8) 
    uRange = np.array([60 + hsvSen, 255, 255], dtype=np.uint8)
    greenFilterFrame = cv.inRange(hsvFrame, lRange, uRange)
    contours, _ = cv.findContours(greenFilterFrame, cv.RETR_TREE, cv.CHAIN_APPROX_NONE )
    x,y,w,h =0,0,0,0
    for x in contours:
      x = max(contours, key = cv.contourArea)
      x,y,w,h = cv.boundingRect(x)
      #cv.rectangle(oGFrame,(x,y),(x+w,y+h),(0,0,255),1)
    if(w*h > 14000 and y*h > 100):
      detectBox(x,y,w,h,oGFrame)

    if cv.waitKey(1) & 0xFF == ord('q'):
        break 
  cap.release()
  cv.destroyAllWindows()


init()
main()

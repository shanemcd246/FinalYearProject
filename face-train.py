import cv2
import os
import pickle
import numpy as np
from PIL import Image



BASE_DIR = os.path.dirname(os.path.abspath(__file__))
image_dir = os.path.join(BASE_DIR, "images")

face_cascade = cv2.CascadeClassifier('cascades/data/haarcascade_frontalface_alt.xml')
recognizer = cv2.face.LBPHFaceRecognizer_create()


current_id = 0
lable_ids = {}
y_labels = []
x_train = []

for root, dirs, files in os.walk(image_dir):
    for file in files:
        if file.endswith("png") or file.endswith("jpg") or file.endswith("PNG") :
            path = os.path.join(root, file)
            label = os.path.basename(os.path.dirname(path)).replace(" ","-").lower()
            print(label , path)
            if not label in lable_ids:
                lable_ids[label] = current_id
                current_id +=1
            id_ = lable_ids[label]
            #print(lable_ids)
            #y_labels.append(label)
            #x_train.append(path)
            pil_image = Image.open(path).convert("L")#L makes grey
            size = (550,550)
            final_image = pil_image.resize(size, Image.ANTIALIAS)
            image_array = np.array(final_image, "uint8")
            #print(image_array)
            faces = face_cascade.detectMultiScale(image_array, scaleFactor=1.5, minNeighbors=5)

            for(x,y,w,h) in faces:
                roi = image_array[y:y+h, x:x+w]
                x_train.append(roi)
                y_labels.append(id_)

#print(y_labels)
#print(x_train)

with open("labels.pickle", 'wb') as f:
    pickle.dump(lable_ids, f)


recognizer.train(x_train, np.array(y_labels))
recognizer.save("trainner.yml")
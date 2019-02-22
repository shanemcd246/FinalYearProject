import cv2
import numpy as np

cap = cv2.VideoCapture(0)
while True:
    _,frame = cap.read()

    cv2.imshow('frame',frame)
    blurred_frame = cv2.GaussianBlur(frame,(25,25),0)
    #cv2.imshow("blurred_frame", blurred_frame)

    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    #Ajust for finer colout detetcion
    sensitivity = 25;
    #Green color
    lower_range = np.array([60 - sensitivity, 60, 30], dtype=np.uint8) 
    upper_range = np.array([60 + sensitivity, 255, 255], dtype=np.uint8)

    #Apply filter
    mask = cv2.inRange(hsv, lower_range, upper_range)

    contours, _ = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE )
    #print(contours);
    x,y,w,h =0,0,0,0
    for c in contours:
        c = max(contours, key = cv2.contourArea)
        x,y,w,h = cv2.boundingRect(c)
        # draw largest conture
        cv2.rectangle(frame,(x,y),(x+w,y+h),(0,255,0),2)
    cv2.drawContours(frame, contours, -1, (255, 255, 0), 1)
    cv2.imshow("frame", frame)
    
    mask = np.zeros(frame.shape,np.uint8)
    mask[y:y+h,x:x+w] = frame[y:y+h,x:x+w]
    cv2.imshow("new back", mask)
    if cv2.waitKey(1)&0XFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()

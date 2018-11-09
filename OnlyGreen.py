import numpy as np
import cv2



cap = cv2.VideoCapture(0)
sensitivity = 25
while True:
    _, frame = cap.read()
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    #HSV hue sat value
    lower_blue = np.array([90-sensitivity,100,50])
    upper_blue = np.array([90+sensitivity,255,255])

    mask = cv2.inRange(hsv, lower_blue,upper_blue)
    res = cv2.bitwise_and(frame, frame, mask = mask)

    cv2.imshow('frame', frame)
    cv2.imshow('mask', mask)
    cv2.imshow('res', res)

    k = cv2.waitKey(1) & 0xFF
    if k == 27:
        break

cv2.destroyAllWindows()
cap.release()

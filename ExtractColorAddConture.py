import cv2
import numpy as np

img = cv2.imread('4.jpg', 1)
img = cv2.resize(img, (0,0), fx=0.9, fy=0.9)
#Blur image for better conture detection
blurred_frame = cv2.GaussianBlur(img,(25,25),0)
cv2.imshow("new bk", blurred_frame)
# Convert to hsv
hsv = cv2.cvtColor(blurred_frame, cv2.COLOR_BGR2HSV) 
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
    cv2.rectangle(img,(x,y),(x+w,y+h),(0,255,0),2)
 
cv2.drawContours(img, contours, -1, (255, 255, 0), 1)
 
cv2.imshow("contours", img)
mask = np.zeros(img.shape,np.uint8)
mask[y:y+h,x:x+w] = img[y:y+h,x:x+w]
cv2.imshow("new back", mask)

while(1):
  k = cv2.waitKey(0)
  if(k == 27):
    break
cv2.destroyAllWindows()

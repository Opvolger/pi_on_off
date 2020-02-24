# At the command prompt, enter: sudo nano /boot/config.txt, then add this to the bottom of the file:
# dtoverlay=w1–gpio
# reboot
# sudo modprobe w1-therm
# /sys/bus/w1/devices/28******/w1_slave  <- temp

import os
import glob
import time
import paho.mqtt.client as mqtt 
import time
import ssl
 
os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')
 
base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'
 
def read_temp_raw():
    f = open(device_file, 'r')
    lines = f.readlines()
    f.close()
    return lines
 
def read_temp():
    lines = read_temp_raw()
    while lines[0].strip()[-3:] != 'YES':
        time.sleep(0.2)
        lines = read_temp_raw()
    equals_pos = lines[1].find('t=')
    if equals_pos != -1:
        temp_string = lines[1][equals_pos+2:]
        temp_c = float(temp_string) / 1000.0
        return temp_c

broker_address="opvolger.eu"
#broker_address="iot.eclipse.org"
print("creating new instance")
client = mqtt.Client("Python") #create new instance
print("connecting to broker")
client.tls_set()
client.tls_insecure_set (False)
client.username_pw_set('opvolger','425sxDC.')
client.connect(broker_address, 8883) #connect to broker
#client.tls_set()

client.loop_start() #start the loop
while True:
    temp = read_temp()
    print(temp)	
    test = client.publish("/huis/woonkamer/thermostaat/temp",temp, retain=True)
    print(test)
    time.sleep(1)
client.loop_stop() #stop the loop
client.disconnect()


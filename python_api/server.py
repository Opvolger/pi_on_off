#pip install flask
#!flask/bin/python

# pi@retropie:~ $ echo "17" > /sys/class/gpio/export
# pi@retropie:~ $ echo "17" > /sys/class/gpio/unexport

from flask import Flask
import logging
import socket
#import RPi.GPIO as GPIO
import threading
import time
import urllib2
import sys
from urllib2 import HTTPError

app = Flask(__name__)
c = threading.Condition()
pingserver = True

@app.route('/')
def index():
    return "Hello, World!"

@app.route('/Off')
def uit():
    logging.info('Hier zet in de lamp aan!')
    stopPing()
    #GPIO.setmode(GPIO.BOARD)
    #GPIO.setup(11, GPIO.OUT)
    #GPIO.output(11, GPIO.HIGH)    
    return "Off"

@app.route('/On')
def aan():
    logging.info('Hier zet in de lamp uit!')
    stopPing()
    #GPIO.cleanup()
    ##GPIO.output(11, GPIO.LOW)    
    return "On"

class Thread_Ping(threading.Thread):
    def __init__(self, name):
        threading.Thread.__init__(self)
        self.name = name

    def run(self):
        global pingserver
        runit = True
        print("Starten as you wish.")
        while runit:
            c.acquire()
            if pingserver == False:
                runit = False
            c.release()
            time.sleep(10)
            try:
                s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
                s.connect(("8.8.8.8", 80))
                print(s.getsockname()[0])
                h = s.getsockname()[0]
                s.close()
                url = "http://opvolger.net/online.php?ip=" + h
                print("Url "+ url)                
                urllib2.urlopen(url).read()
                print("Ping "+ url +" done...")
            except:
               print "Error: connect"
               print "Unexpected error:", sys.exc_info()[0]
            finally:
                print("Ping opvolger.net")            
        print("Stopping as you wish.")

class Thread_EndPing(threading.Thread):
    def __init__(self, name):
        threading.Thread.__init__(self)
        self.name = name

    def run(self):
        global pingserver
        c.acquire()
        pingserver = False
        c.notify_all()
        c.release()

def stopPing():
    stopping = Thread_EndPing("myThread_Stopping")
    stopping.start()

ping = Thread_Ping("myThread_ping")

def main():
    ping.start()    
    #s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    #s.connect(("8.8.8.8", 80))
    #print(s.getsockname()[0])
    #h = s.getsockname()[0]
    #s.close()
    logging.basicConfig(level=logging.DEBUG) #logging.basicConfig(filename='example.log',level=logging.DEBUG)
    #app.run(host= h, port=5000, debug=False)
    app.run(host= '0.0.0.0', port=5003, debug=False)
    ##GPIO.cleanup()
    
if __name__ == '__main__':
    main()

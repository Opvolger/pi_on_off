# pi_on_off
App / Pi server / php server

sudo apt-get install usb-modeswitch
sudo apt-get install sg3-utils
sudo apt-get install ppp
sudo apt-get install screen

So I may need glasses  :D as the entry in /etc/network/interfaces was for wlan0 NOT wwan0 .........

Adding : 

Allow hotplug wwan0
Iface wwan0 inet dhcp 

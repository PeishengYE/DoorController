# DoorController
This is a personal project to control my garage door. 
## Keyword: 
Embedded LINUX, Android APP, IO control 

## 1) Hardware:
The controller is using an A13 PCB board. To find out what's A13, you can follow this link
https://www.olimex.com/wiki/A13-OLinuXino.  

By controlling the  24V output (The original use is for a screen backlight) from this PCB,
I'm able to control a relay to simulate button pressing for my Garage

## 2) Software:
The software consis of four parts: 

### Linux kernel programming: 
   changed the original default output to off. To make more secrity, 
   introduced new IOCTL command to lock this operation not be triggered by any other process 

### Linux userspace application: 
   This is a server waiting for connection from the Android app. It support two command sent by Android App, 
   the ping command and the real open/close command 
   
### Embedded Linux making 
   Establish an WIFI network connection with an External USB WIFI dongle; 
   filesystem making

### Android App
   A network broadcast receiver to monitor the WIFI network change event
   A Intent service to do the network connection with controller and update UI according the connection result

## 3) Design Tip
   When I'm approaching my home, trying to use this APP, I often have a problem of WIFI connection. 
   In this context, my Phone's Wifi connection is just connected, or trying to connect, as the **WIFI signal is poor**. 
   As soon as the App start, a service is created to connect the controller. 
   **Whenever the connection is established**, the UI is updated to show that the User Operation is available at that moment.
   **Whenever the user is sending a command to the controller**, a check is start before sending this command. 
   A message is showing on the phone to tell the user this status
   
## 4) Next Steps
   Introduce **encryption** between the communication of controller and my phone. 
   Find out a way to initilze the controller's network parameter, like WIFI ssid, passwd, etc. 
   Introduce **a lock screen** when a user trying to send door control command. 
   this is to prevent any wrong operation when the user forget to lock the screen after starting this app. 
   Introduce **Google Cloud Message** to remote control this controller

   

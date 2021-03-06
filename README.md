# DoorController
This is a personal project to control my garage door. 
## Keyword: 
Google Cloud Message, Embedded LINUX, Android APP, IO control 
## Video: 
[![VIDEO](https://github.com/PeishengYE/DoorController/blob/master/docs/Garage_door_testing_0000.png)](https://www.youtube.com/watch?v=-v5WE888Jag)
## 1) Hardware:
The controller is using an A13 PCB board. To find out what's A13, you can follow [this link]
(https://www.olimex.com/wiki/A13-OLinuXino)  

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


###  About Google Cloud Message 
   To establish a WIFI connection may take more than three minutes when the WIFI signal is bad or the Home used router is too busy to reply.    This make the previous WIFI design inaccessible. That's why I introduce the Google Cloud Message. User can use any data connection to connect this door controller. 
   
   This design make the whole system very security and reliable, as GCM is secure by google.  Every GCM client have a unique token in the world.  The disadvantage is that we need a Android Phone/Tablet running Google Cloud Message Service to receive this message. Then when this message arrive, it send a requirement to the door controller. it works like a proxy.

Fortunately we can use any cheap Phone or tablet to do this task, like the Datawind cheap device. You do not need a beautiful screen or  a good touch screen. 

   In the future development, we can also use this device as our home sensor gateway. 

###  ScreenShot of the current UI 
[UI in English](https://github.com/PeishengYE/DoorController/blob/master/docs/Ui_doorController_003.png)| [UI in Chinese](https://github.com/PeishengYE/DoorController/blob/master/docs/Ui_doorController_001.png)
------------ | -------------
![UI in English](https://github.com/PeishengYE/DoorController/blob/master/docs/small/Ui_doorController_003.png)| ![UI in Chinese](https://github.com/PeishengYE/DoorController/blob/master/docs/small/Ui_doorController_001.png)

###  HARDWARE PHOTOES 
[DOOR CONTROLLER_1](https://github.com/PeishengYE/DoorController/blob/master/docs/A13_door_controller_switch_board_000_.JPG)| [DOOR_CONTROLLER_2](https://github.com/PeishengYE/DoorController/blob/master/docs/A13_door_controller_switch_board_001_.JPG)| [MOTION_DETECTION](https://github.com/PeishengYE/DoorController/blob/master/docs/A13_motion_detection_board_000_.JPG)| [GCM GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/Google_Cloud_message_gateway_000_.JPG)
------------ | ------------- | ------------- | -------------
[BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/A13_door_controller_switch_board_000_.JPG)| [BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/A13_door_controller_switch_board_001_.JPG)| [BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/A13_motion_detection_board_000_.JPG)| [BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/Google_Cloud_message_gateway_000_.JPG)
![DOOR CONTROLLER_1](https://github.com/PeishengYE/DoorController/blob/master/docs/small/A13_door_controller_switch_board_000_.JPG)| ![DOOR_CONTROLLER_2](https://github.com/PeishengYE/DoorController/blob/master/docs/small/A13_door_controller_switch_board_001_.JPG)| ![MOTION_DETECTION](https://github.com/PeishengYE/DoorController/blob/master/docs/small/A13_motion_detection_board_000_.JPG)| ![GCM GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/small/Google_Cloud_message_gateway_000_.JPG)

[GCM_GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/Google_Cloud_message_gateway_001_.JPG)| [GCM_GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/Google_Cloud_message_gateway_002.JPG)| [MOTION_DETECTION_EMAIL](https://github.com/PeishengYE/DoorController/blob/master/docs/motion_detected_by_mail_000.png)| [GCM GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/remote_image_recevied.png)
------------ | ------------- | ------------- | -------------
[BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/Google_Cloud_message_gateway_001_.JPG)| [BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/Google_Cloud_message_gateway_002.JPG)| [BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/motion_detected_by_mail_000.png)| [BIG_IMAGE](https://github.com/PeishengYE/DoorController/blob/master/docs/remote_image_recevied.png)
![GCM_GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/small/Google_Cloud_message_gateway_001_.JPG)| ![GCM_GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/small/Google_Cloud_message_gateway_002.JPG)| ![MOTION_DETECTION_EMAIL](https://github.com/PeishengYE/DoorController/blob/master/docs/small/motion_detected_by_mail_000.png)| ![GCM GATEWAY](https://github.com/PeishengYE/DoorController/blob/master/docs/small/remote_image_recevied.png)


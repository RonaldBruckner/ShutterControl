# ShutterControl
Android App with widget to sniff and send UDP broadcasts, used to control window shutters.

![](https://github.com/RonaldBruckner/ShutterControl/blob/master/images/start_screen.png) 
![](https://github.com/RonaldBruckner/ShutterControl/blob/master/images/learn_screen.png?raw=true) 
![](https://github.com/RonaldBruckner/ShutterControl/blob/master/images/widget.png?raw=true)

A lot of quipment in home automation is based on the RF 433 MHz technology, or use a simple IR remote control also used for TVs.
To control such equipment with a samrt phone two things are needed.

1. Hardware<br/>
A device which can receive and send RF or IR commands.
If a command is received it gets encoded as base64 String and is send as UDP broadcast in a local WIFI network.
If the same command is received via WIFI, it gets decoded is will be send over RF or IR. 
For IR a suitable device would be [IRTrans](http://www.irtrans.de/images.prod/lan-640.jpg), for IR a custom hardware was build using an Arduino with WIFI shield and a 433 MHz RX/TX Module.

2. Software<br/>
Several control elements for window shutters can be added and the according commands can be assigned to each button, shutter-up, -stop and -down. To learn the commands for each button this App receives and stores the encoded base64 strings, received via UDP broadcast. If a button in the app or the widget gets pressed, the according command is send via UDP broadcast. 


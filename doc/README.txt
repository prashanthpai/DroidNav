This app has been tested only on Android 2.3.6 and Windows 7

User Guidelines
---------------

1. Copy DroidNav.apk file to your phone and open it to install it.
2. On installing, it'll show up in your App menu/list. Also install nircmd.exe on your PC.
3. Create a WiFi hotspot on your phone and connect your PC to it.
4. Make sure firewall is turned off OR make sure it doesn't block port 15611.
5. Open a cmd prompt and cd to the directory containing Server.jar
6. Enter the following command to start the server
   java -jar Server.jar
7. Open DroidNav app on phone and enter IP and Port specified by the server.
8. Connect and use!! :)


Touchpad
--------
There's large touchpad area on the left and a strip for scrolling on the right.
The Left and Right click buttons are for left and right clicks.
On the touchpad portion : 
	Single tap triggers single click.
	Double tap triggers double click.
	Long press would trigger right click.
The volume buttons on the phone increase and decrease system volume.

Presenter
---------
1. Open the desired PPT/PDF file in computer using Powerpoint/Adobe Reader prespectively.
2. Use buttons on phone to control presentation.
3. The volume buttons on the phone can also be used to move to next and previous slides.
4. Beware that the presentation window must be active and on top.

Send Text
---------
1. Copy/type the text that you want to send to computer into the text area on phone.
2. On the computer, place the cursor wherever you need the text to be pasted.
3. Press the send button on the phone.

Sensor
------
1. Currently, only Proximity sensor has been used.
2. Howering your hand over the proximity sensor on phone will turn off the monitor.
3. This works only if nircmd.exe has been installed.


**********************************************************************************************


Developer Guidelines
--------------------

1. Install JDK version 1.6
2. Unzip the Android Development Tools(ADT) bundle and insgtall nircmd.exe
3. Start eclipse and import the two projects(DroidNav and DroidNav Server) into workspace.
4. Connect the android device, make changes to code(if any) and run android project.
5. Run the server project.
6. Connect, debug, hack, modify, enjoy:)


Github repos : 
https://github.com/prashanthpai/DroidNav
https://github.com/prashanthpai/DroidNavServer
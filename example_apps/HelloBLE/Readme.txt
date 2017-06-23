Installations steps for the example app.
-------------------------------------------------------------------

Auto Preparation Approach
------------------------------
1. Copy  react-native-ble-quick-sdk to mac
2. Change directory to react-native-ble-quick-sdk 
3. Open terminal and Run ./prepareExample script in react-native-ble-quick-sdk folder
4.  Go to HelloBLE\ios\ and open Xcode project and select team or code sign profile.
5. Go to HelloBLE\app\screens\ and open file AppConstants.js. 
6. In AppConstants.js you will find that Object k has deviceid key as empty string. Assign value to deviceid key with the UUID of the device that you want to connect to.
5. Build and run.

Manual Approach
---------------------------
1. Copy  react-native-ble-quick-sdk to mac
2. Go to example_apps\HelloBLE\ folder in react-native-ble-quick-sdk folder and move HelloBLE folder  to some other location out of  react-native-ble-quick-sdk folder.
3. Change directory to HelloBLE project's new location and 
4. do npm install  HelloBLE folder.
5. react-native eject 
6. react-native link
7. Copy react-native-ble-quick-sdk folder to HelloBLE project's node_modules folder.
8. open Xcode project and select team or code sign profile.
9. Drag n drop RNBluetoothLE.xcodeproject from node_modules\react-native-ble-quick-sdk\comm_admins\ble\ios to Xcode Libraries group.
10. Go to Build Phases settings of the application project and add libRNBluetoothLE.a
11. Go to HelloBLE\app\screens\ and open file AppConstants.js. 
12. In AppConstants.js you will find that Object k has deviceid key as empty string. Assign value to deviceid key with the UUID of the device that you want to connect to.
13. Build and run.



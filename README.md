## React Native BLE Quick SDK
React Native BLE Customizable SDK for your device's BLE profile, services and characteristics designed using Bluetooth Developer Studio IDE.

## Requirements
* React Native Framework from Facebook
* Bluetooth Developer Studio IDE from Bluetooth SIG organization
* BLE profile (xml files) of bluetooth device designed using using Bluetooth Developer Studio.
* Windows 7/8.1/10 and Mac OS Yosemite/El Capitan/Sierra .


## Caution: 
This sdk is in development phase so please don't use it for production releases until stable version of this sdk is released. 


## Supported Platforms
- React Native 0.35
- iOS 8+ 
- Android to be supported soon. 



## Install & Prepare
---------------------
1. Assuming that Bluetooth Developer Studio is installed and you have created BLE profile for your device using Bluetooth Developer Studio.

2. Do npm install react-native-ble-quick-sdk as below in your React Native App's root folder. Provide the Bluetooth Developer Studio IDE's root path using argument --bdsPath in npm install command.

```shell
 npm install --save react-native-ble-quick-sdk --bdsPath="{Your Bluetooth Developer Studio Installation Folder i.e. C:\Program Files (x86)\Bluetooth SIG\Bluetooth Developer Studio}" 
```

Note: This step needs admin privilege or it will fail.
 
3. Go to terminal, change to directory "{app_project_root}\node_modules\react-native-ble-quick-sdk\" and execute below two command sequentially as below: 

```shell
	a.) npm run generate-ble-profile --bleProfileType="characteristic" --bleNamespace="com.{yourcompanynamespace}" --bdsPrjRootPath="{Your Bluetooth Developer Studio project path i.e. C:\\mybdsprojectpath\\}"

	b.) npm run generate-ble-profile --bleProfileType="service" --bleNamespace="com.{yourcompanynamespace}" --bdsPrjRootPath="{Your Bluetooth Developer Studio project path i.e. C:\\mybdsprojectpath\\}"
```

* Note 1:

  Argument-1:  bleProfileType is type of BLE profile e.g. service or characteristic. You must run generate-ble-profile command for 					bleProfileType="characteristic" and again for bleProfileType="service".

  Argument-2:  bleNamespace is your BLE device profile's namespace as per Bluetooth Developer Studio.

  Argument-3:  bdsPrjRootPath is root folder of your Bluetooth Developer Studio (i.e. where your *.bds file resides). Please note that 
			   this is not the root path of your react native app project.

* Note 2:
	Please use double slash while specifying path.

	
## Generate BLE device specific API Source
------------------------------------------
	
1. Open your Bluetooth Developer Studio project.

2. Go to Menu Tools -> Generate Code -> Select "Client" for "Generate Code for" option -> Select Plugin "React Native Client Plugin"

3. Set output path in "Save To " field to {app_project_root}\node_modules\react-native-ble-quick-sdk\.

4. Press generate button and watch the Bluetooth Developer Studio log window to check if code is generated successfully or not.

5. If code generation and ble profile generation have completed successfully then React Native BLE Quick SDK is ready to be consumed by BLE Client (Central)react native app to communicate with your BLE device.


## Running on iOS (Android not yet supported, To be supported soon )
* Copy React Native project including \node_modules\react-native-ble-quick-sdk\ from Windows Machine to Mac Machine to prepare and run on iOS. 

## Link the native library
---------------------------
you need to link the native library. You can either:
* Link native library with `react-native link`, or
* Link native library manually

Both approaches are described below.

### Link Native Library with `react-native link`

```shell
react-native link react-native-ble-quick-sdk
```

### Link Native Library Manually

#### iOS
- Open the node_modules/react-native-ble-quick-sdk/comm_admins/ble/ios folder and drag RNBluetoothLE.xcodeproj into your Libraries group.
- Check the "Build Phases"of your project and add "libRNBluetoothLE.a" in the "Link Binary With Libraries" section.


## Your Devcie Specific Example
--------------------------------
	Please refer to folder {app_project_root}\node_modules\react-native-ble-quick-sdk\example\ to find auto generated sample app using this sdk.

	
## Basic Example 
-----------------
** Below example is for for Battery Service as per Bluetooth Specification for type org.bluetooth.service.battery_service(0x180F)
	
```js

import React, {Component} from 'react'; 
 import { 
     Text, 
     View, 
     Alert, 
     StyleSheet, 
     ScrollView 
 } from 'react-native'; 
 import Button from 'apsl-react-native-button' 
 import {getSDKServiceMgrInstance} from 'react-native-ble-quick-sdk';

 var deviceId = ''; // Assign your device's iOS specific CBUUID here. You can find it in the scan api's console log output by your device name.
 
 var objSDKSvcMgr = getSDKServiceMgrInstance(false);
  
 export default class BLEHelloWorldView extends Component { 

     constructor(props) { 
         super(props); 
     } 

     async scanNearbyBleDevices() { 
			let listenerScan = (deviceID, deviceName, rssi) => {
				console.log(' Device Found iOS CBUUID = ' + deviceID + '/' + deviceName + '[' + rssi + ']');
 			};
         try { 
             await objSDKSvcMgr.getDevAdmin().getDeviceScanner().scanAllServices(5, listenerScan); 
             console.log('Initiated scanning task successfully'); 
         } 
         catch (err) { 
             console.log('Failed to start scanning task'); 
         } 
     } 

     async connect2BleDevice() { 
         try { 
             await objSDKSvcMgr.getDevAdmin().getDeviceAccessor().connectToDevice(deviceId); 
             console.log('device connected'); 
         } 
         catch (err) { 
             console.log('device connection failed'); 
         } 
     } 


     async readBatteryLevel() { 

         let listenerBatteryLevel = (BatteryLevel) => { 
             Alert.alert(' BatteryLevel is ' + BatteryLevel); 
         }; 

         try { 
             await objDevSvcMgr.getService('BatteryService').get_battery_level(listenerBatteryLevel); 
             console.log('readBatteryLevel api call success'); 
         } 
         catch (err) { 
             console.log('readBatteryLevel api call failed'); 
         } 

     } 

     render() { 
         return ( 
             <ScrollView style={styles.page}> 
                 <Text style={styles.tabText}>BLE Hello World </Text> 
                 <Button 
                     style={styles.Button} 
                     onPress={ () => { 
                         this.scanNearbyBleDevices(); 
                     } 
                     }> 
                     1-Scan 
                 </Button> 
                 <Button 
                     style={styles.Button} 
                     onPress={ () => { 
                         this.connect2BleDevice(); 
                     } 
                     }> 
                     2-Connect 
                 </Button> 
                 <Button 
                     style={styles.Button} 
                     onPress={ () => { 
                         this.readBatteryLevel(); 
                     } 
                     }> 
                     3-Read BatteryLevel 
                 </Button> 
             </ScrollView> 
         ); 
     } 
 } 
 var styles = StyleSheet.create({ 
	  page: {
		  backgroundColor: '#edf0f5',
		  flex: 1,
	  }, 
     tabText: { 
         color: 'black', 
         margin: 5, 
    }, 
     Button: { 
         backgroundColor: 'green', 
         margin: 30 
     }, 

 }); 
 

```

## Contributing

If you find any problems, please [open an issue](https://github.com/YbrainInc/react-native-ble-quick-sdk/issues/new) or submit a fix as a pull request.

## License

[Apache License 2.0](LICENSE)

## Credits

Native part of the project has been borrowed from [cordova-plugin-ble-central](github.com/don/cordova-plugin-ble-central) and  [react-native-ble-manager](github.com/innoveit/react-native-ble-manager). If your react native app project requires to communicate directly with the BLE device at the low level then you should check it out to see if this project [React Native BLE Quick SDK](https://github.com/YbrainInc/react-native-ble-quick-sdk/)  or [react-native-ble-manager](github.com/innoveit/react-native-ble-manager) is better fit for you.

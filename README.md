## React Native BLE Quick SDK

## Requirements
React Native Framework from Facebook
Bluetooth Developer Studio IDE from Bluetooth SIG organization
BLE profile (xml files) of bluetooth device designed using using Bluetooth Developer Studio.

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
 
3. Go to terminal, change to directory "{app_project_root}\node_modules\react-native-ble-quick-sdk\" and execute below two command as below: 

```shell
	a.) npm run generate-ble-profile --bleProfileType="characteristic" --bleNamespace="com.{yourcompanynamespace}" --bdsPrjRootPath="{Your Bluetooth Developer Studio project path i.e. C:\\mybdsprojectpath\\}"

	b.) npm run generate-ble-profile --bleProfileType="service" --bleNamespace="com.{yourcompanynamespace}" --bdsPrjRootPath="{Your Bluetooth Developer Studio project path i.e. C:\\mybdsprojectpath\\}"
```

Note: 
	Argument 1 : bleProfileType is type of BLE profile e.g. service or characteristic. You must run generate-ble-profile command
				 for bleProfileType="characteristic" and again for bleProfileType="service".
				 
	Argument 2 : bleNamespace is your BLE device profile's namespace as per Bluetooth Developer Studio.

	Argument 3 : bdsPrjRootPath is root folder of your Bluetooth Developer Studio.
	
	Please use double slash while specifying path.

	
## Generate BLE device specific API Source
------------------------------------------
	
1. Open your Bluetooth Developer Studio project.

2. Go to Menu Tools -> Generate Code -> Select "Client" for "Generate Code for" option -> Select Plugin "React Native BLE Quick SDK for Client"

3. Set output path in "Save To " field to {app_project_root}\node_modules\react-native-ble-quick-sdk\.

4. Press generate button and watch the Bluetooth Developer Studio log window to check if code is generated successfully or not.

5. If code generation and ble profile generation have completed successfully then React Native BLE Quick SDK is ready to be consumed by BLE Client (Central)react native app to communicate with your BLE device.

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


## Basic Example
-----------------
	You may refer to folder {app_project_root}\node_modules\react-native-ble-quick-sdk\example\ to find auto generated sample app using this sdk.

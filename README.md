 React Native BLE Quick SDK
 
This sdk is in development phase so please don't use it for production releases until stable version of this sdk is released. 

Installation and preparation steps for react-native-ble-quick-sdk. 
-----------------------------------------------------------------------------------

1. Assuming that Bluetooth Developer Studio is installed and you have created BLE profile for your device using Bluetooth Developer Studio.

2. Do npm install react-native-ble-quick-sdk from github as below in your React Native App's root folder.. Provide the Bluetooth Developer Studio's root path
 using argument --bdsPath in npm install command.

 e.g. npm install  https://github.com/eric2036/react-native-ble-quick-sdk --bdsPath="C:\\Program Files\\something" 

 
3. Go to terminal, change to directory "{app_project_root}\node_modules\react-native-ble-quick-sdk\" and execute below two command as below: 


	a.) npm run generate-ble-profile --bleProfileType="characteristic" --bleNamespace="com.yourcompany" --bdsPrjRootPath="C:\\mybdsproject\\"

	b.) npm run generate-ble-profile --bleProfileType="service" --bleNamespace="com.yourcompany" --bdsPrjRootPath="C:\\mybdsproject\\"

Note: 
	Argument 1 : bleProfileType is type of BLE profile e.g. service or characteristic. You must run generate-ble-profile command
				 for bleProfileType="characteristic" and again for bleProfileType="service".
				 
	Argument 2 : bleNamespace is your BLE profile's namespace as per Bluetooth Developer Studio.

	Argument 3 : bdsPrjRootPath is root folder of your Bluetooth Developer Studio.

4. Open your Bluetooth Developer Studio project.

5. Go to Tools -> Generate Code -> Select "Client" for "Generate Code for" option -> Select Plugin "React Native BLE Quick SDK for Client"

6. Set output path in "Save To " field to {app_project_root}\node_modules\react-native-ble-quick-sdk\.

7. Press generate button and watch the Bluetooth Developer Studio log window to check if code is generated successfully or not.

8. If code generation and ble profile generation have completed successfully then React Native BLE Quick SDK is ready to be consumed by BLE Client (Central)react native app.

9. You may refer to example folder where you can find auto generated sample app using this sdk.

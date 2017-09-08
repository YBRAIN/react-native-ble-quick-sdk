package ble.rni;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;


public class CommAdminBleRNI extends ReactContextBaseJavaModule {

	public static final String LOG_TAG = "CommAdminBleRNI";

	//@private BleAdmin* mBleAdmin;
	private BleAdmin mBleAdmin;


	public CommAdminBleRNI(ReactApplicationContext reactContext, Context baseContext) {
		super(reactContext);

		mBleAdmin = new BleAdmin(reactContext, baseContext);

		Log.d("", "  - CommAdminBleRNI");
	}

//	public CommAdminBleRNI(ReactApplicationContext reactContext, Activity activity) {
//		super(reactContext);
//		mBleAdmin = new BleAdmin(reactContext, activity);
//
//
//	}


	@Override
	public String getName() {
		return "CommAdminBleRNI";
	}

//RCT_EXPORT_METHOD(native_scan:(NSArray *)serviceUUIDStrings timeoutSeconds:(nonnull NSNumber *)timeoutSeconds callback:(nonnull RCTResponseSenderBlock)successCallback)
	@ReactMethod
	public void native_scan(ReadableArray serviceUUIDStrings, final int timeoutSeconds, Callback successCallback) {

		//NSLog(@"native_scan entered");
		Log.d(LOG_TAG,"native_scan entered");

		//[mBleAdmin scan:serviceUUIDStrings timeoutSeconds:timeoutSeconds callback: successCallback];
		mBleAdmin.scan(serviceUUIDStrings, timeoutSeconds, successCallback);
	}

//RCT_EXPORT_METHOD(native_connect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_connect(String peripheralUUID, Callback successCallback, Callback failCallback) {
		//NSLog(@"native_connect entered");
		Log.d(LOG_TAG,"native_connect entered");

		//[mBleAdmin connect:peripheralUUID  successCallback: successCallback failCallback: failCallback];
		mBleAdmin.connect(peripheralUUID, successCallback, failCallback);
	}

	//RCT_EXPORT_METHOD(native_disconnect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_disconnect(String peripheralUUID, Callback successCallback, Callback failCallback) {

		//NSLog(@"native_disconnect entered");
		Log.d(LOG_TAG,"native_disconnect entered");

		//[mBleAdmin disconnect:peripheralUUID  successCallback: successCallback failCallback: failCallback];
		mBleAdmin.disconnect(peripheralUUID , successCallback, failCallback);
	}

	//RCT_EXPORT_METHOD(native_readBleDevRSSI : (NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_readBleDevRSSI (String peripheralUUID, Callback successCallback, Callback failCallback)
	{
	//	NSLog(@"native_readBleDevRSSI entered");
		Log.d(LOG_TAG,"native_readBleDevRSSI entered");

		//[mBleAdmin readRSSIOfConnectedBleDev:peripheralUUID successCallback:successCallback failCallback:failCallback];
		mBleAdmin.readRSSIOfConnectedBleDev(peripheralUUID,successCallback,failCallback);

	}

//RCT_EXPORT_METHOD(native_checkState)
	@ReactMethod
	public void native_checkState(){
		//NSLog(@"native_checkState entered");
		Log.d(LOG_TAG,"native_checkState entered");
	//	[mBleAdmin checkState];
		mBleAdmin.checkState();
	}


//	RCT_EXPORT_METHOD(native_IsDeviceConnnected:(NSString *)deviceUUID resultCallback:(nonnull RCTResponseSenderBlock)resultCallback  failCallback:(nonnull RCTResponseSenderBlock)failCallback)
    @ReactMethod
	public void native_IsDeviceConnnected( String deviceUUID,  Callback resultCallback,  Callback failCallback)

	{
		//NSLog(@"native_IsDeviceConnnected entered");
		Log.d(LOG_TAG,"native_IsDeviceConnnected entered");
		//[mBleAdmin checkPeripheralState:deviceUUID resultCallback:resultCallback failCallback:failCallback ];
		mBleAdmin.checkPeripheralState(deviceUUID, resultCallback,failCallback) ;
	}


//	RCT_EXPORT_METHOD(native_write_string:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_write_string(String deviceUUID, String serviceUUID, String characteristicUUID, String message, Callback successCallback, Callback failCallback) {
		//NSLog(@"native_write entered");
		Log.d(LOG_TAG,"native_write_string entered");
		//[mBleAdmin writeAsString:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID message:message successCallback: successCallback failCallback: failCallback];
		mBleAdmin.writeAsString( deviceUUID, serviceUUID, characteristicUUID , message , successCallback, failCallback);

	}


//RCT_EXPORT_METHOD(native_write_bytes:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID   msgByteBuf:(nonnull NSArray*)msgByteBuf successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_write_bytes(String deviceUUID, String serviceUUID, String characteristicUUID, ReadableArray msgByteBuf, Callback successCallback, Callback failCallback) {

		//NSLog(@"native_write_bytes entered");
		Log.d(LOG_TAG,"native_write_bytes entered");

		//[mBleAdmin writeAsBytes:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID msgByteBuf:msgByteBuf            successCallback: successCallback failCallback: failCallback];
		mBleAdmin.writeAsBytes(deviceUUID, serviceUUID ,characteristicUUID , msgByteBuf, successCallback , failCallback);

	}


	//RCT_EXPORT_METHOD(native_writeWithoutResponse:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_writeWithoutResponse(String deviceUUID, String serviceUUID, String characteristicUUID, String message, Callback successCallback, Callback failCallback)
	{
		//NSLog(@"native_writeWithoutResponse entered");
		Log.d(LOG_TAG,"native_writeWithoutResponse entered");
//		[mBleAdmin writeWithoutResponse:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID message:message successCallback: successCallback failCallback: failCallback];
		mBleAdmin.writeWithoutResponse(deviceUUID ,serviceUUID ,characteristicUUID, message , successCallback , failCallback);

	}

	//RCT_EXPORT_METHOD(native_read:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_read(String deviceUUID, String serviceUUID, String characteristicUUID, Callback successCallback, Callback failCallback) {

		//NSLog(@"native_read entered");
		Log.d(LOG_TAG,"native_read entered");
//		[mBleAdmin read:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID successCallback: successCallback failCallback: failCallback];
		mBleAdmin.read(deviceUUID,serviceUUID, characteristicUUID, successCallback, failCallback);

	}

//RCT_EXPORT_METHOD(native_startNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_startNotification(String deviceUUID, String serviceUUID, String characteristicUUID, Callback successCallback, Callback failCallback) {
		//NSLog(@"native_startNotification entered");
		Log.d(LOG_TAG,"native_startNotification entered");
		//[mBleAdmin startNotification:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID successCallback: successCallback failCallback: failCallback];
		mBleAdmin.startNotification(deviceUUID, serviceUUID ,characteristicUUID , successCallback , failCallback);

	}

//RCT_EXPORT_METHOD(native_stopNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
	@ReactMethod
	public void native_stopNotification(String deviceUUID, String serviceUUID, String characteristicUUID, Callback successCallback, Callback failCallback) {
		//NSLog(@"native_stopNotification entered");
		Log.d(LOG_TAG,"native_stopNotification entered");
//		[mBleAdmin stopNotification:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID successCallback: successCallback failCallback: failCallback];
		mBleAdmin.stopNotification(deviceUUID ,serviceUUID ,characteristicUUID , successCallback , failCallback);


	}

}

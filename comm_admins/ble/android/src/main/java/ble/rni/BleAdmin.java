package ble.rni;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import org.json.JSONException;

import java.util.*;
import android.os.Handler;


import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class BleAdmin { // extends ReactContextBaseJavaModule

	public static final String LOG_TAG = "logs";
	public static final String VENDOR_PREFIX_UUID_2BYTE = "0000";
	public static final String VENDOR_SUFFIX_UUID_12BYTE = "-0000-0000-0000-000000000000";


	private static Activity activity;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothLeScanner bluetoothLeScanner;
	private ScanSettings settings;
	//private Context context;
	private ReactContext reactContext;

	private Context mBaseContext;

	// key is the MAC Address
	Map<String, Peripheral> peripherals = new LinkedHashMap<String, Peripheral>();
	Map<String, Callback> connectCallback = new LinkedHashMap<String, Callback>();


	private Timer timer = new Timer();

	public BleAdmin(ReactApplicationContext reactContext, Context baseContext) {
		//super(reactContext);
		//context = reactContext;
		this.reactContext = reactContext;
		this.mBaseContext = baseContext;
		//this.mReactApplicationContext = reactApplicationContext;
		InitBleAdmin();

		Log.d("", "  - BleAdmin initialized 1");
	}

//	public BleAdmin(ReactApplicationContext reactContext, Activity activity) {
//		super(reactContext);
//		//context = reactContext;
//		this.reactContext = reactContext;
//		BleAdmin.activity = activity;
//
//		InitBleAdmin();
//		Log.d(LOG_TAG, "BleAdmin initialized 2");
//
//	}

	private void InitBleAdmin()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			settings = new ScanSettings.Builder()
					.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
					.build();
		}

		IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		//context.registerReceiver(mReceiver, filter);
		this.reactContext.registerReceiver(mReceiver, filter);



		//didBleAdminLoad();
	}

	private void didBleAdminLoad()
	{


		Handler h = new Handler(Looper.getMainLooper());
		Runnable r = new Runnable() {
			@Override
			public void run() {


				WritableMap map = Arguments.createMap();
				if(reactContext != null)
				{
					map.putString("status", "1");
					//	mBleAdmin.bleAdminRNIEventBridge = self.bridge;
					Log.d(LOG_TAG, "Native lib loaded sucessfully  status:" + "1");
					sendEvent("NativeLibLoad", map);
				}
				else
				{
					map.putString("status", "0");
					Log.d(LOG_TAG, "Native lib loading failed  status:" + "0");
					sendEvent("NativeLibLoad", map);
				}
			}
		};
		h.postDelayed(r,500);
	}

//	@Override
//	public String getName() {
//		return "BleAdminRNI";
//	}

	private BluetoothAdapter getBluetoothAdapter() {
		if (bluetoothAdapter == null) {
			//BluetoothManager manager = (BluetoothManage context.getSystemService(Context.BLUETOOTH_SERVICE);
			BluetoothManager manager = (BluetoothManager) this.reactContext.getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = manager.getAdapter();
		}
		return bluetoothAdapter;
	}

	private void sendEvent(String eventName,
						   @Nullable WritableMap params) {

		//ReactApplicationContext r = getReactApplicationContext();
		//RCTNativeAppEventEmitter jm  = r.getJSModule(RCTNativeAppEventEmitter.class);
		//jm.emit(eventName, params);


		//getReactApplicationContext()
			this.reactContext.getJSModule(RCTNativeAppEventEmitter.class)
				.emit(eventName, params);
	}

	//@ReactMethod
	public void scan(ReadableArray serviceUUIDs, final int scanSeconds, Callback successCallback) {
		Log.d(LOG_TAG, "  android scan");
		if (!getBluetoothAdapter().isEnabled())
			return;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			for (Iterator<Map.Entry<String, Peripheral>> iterator = peripherals.entrySet().iterator(); iterator.hasNext(); ) {
				Map.Entry<String, Peripheral> entry = iterator.next();
				if (!entry.getValue().isConnected()) {
					iterator.remove();
				}
			}

			if (serviceUUIDs.size() > 0) {
				UUID[] services = new UUID[serviceUUIDs.size()];
				for(int i = 0; i < serviceUUIDs.size(); i++){
					services[i] = Utils.convert16bitTo128bitUUID(serviceUUIDs.getString(i));
					Log.d(LOG_TAG, "scan service: " + serviceUUIDs.getString(i));
				}

				//getBluetoothAdapter().startLeScan(services, mLeScanCallback);
				getBluetoothAdapter().startLeScan(mLeScanCallback);
			} else {
				getBluetoothAdapter().startLeScan(mLeScanCallback);
			}

			if (scanSeconds > 0) {
				Thread thread = new Thread() {

					@Override
					public void run() {

						try {
							Thread.sleep(scanSeconds * 1000);
						} catch (InterruptedException e) {
						}

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								getBluetoothAdapter().stopLeScan(mLeScanCallback);
								WritableMap map = Arguments.createMap();
								sendEvent("BleAdminStopScan", map);
							}
						});

					}

				};
				thread.start();
			}
		}
		successCallback.invoke();
	}


	//-(void)readRSSIOfConnectedBleDev:(NSString *) peripheralUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
	public void readRSSIOfConnectedBleDev(String peripheralUUID, Callback successCallback, Callback failCallback) {

//		NSLog(@"readRSSIOfConnectedBleDev");
//		CBPeripheral *peripheral = [self findPeripheralByUUID:peripheralUUID];
//
//		if (peripheral)
//		{
//			[peripheral readRSSI];
//		}
//		else
//		{
//			NSString *error = [NSString stringWithFormat:@"Could not find peripheral %@.", peripheralUUID];
//			NSLog(@"%@", error);
//			failCallback(@[error]);
//		}

	}


	//@ReactMethod
	public void connect(String peripheralUUID, Callback successCallback, Callback failCallback) {
		Log.d(LOG_TAG, "connect: " + peripheralUUID );

		Peripheral peripheral = peripherals.get(peripheralUUID);
		if (peripheral != null){
			//peripheral.connect(successCallback, failCallback, activity);
			peripheral.connect(successCallback, failCallback, mBaseContext);
		} else
			failCallback.invoke();
	}

	//@ReactMethod
	public void disconnect(String peripheralUUID, Callback successCallback, Callback failCallback) {
		Log.d(LOG_TAG, "disconnect: " + peripheralUUID);

		Peripheral peripheral = peripherals.get(peripheralUUID);
		if (peripheral != null){
			peripheral.disconnect();
			successCallback.invoke();
		} else
			failCallback.invoke();
	}

	//@ReactMethod
	public void startNotification(String deviceUUID, String serviceUUID, String characteristicUUID, Callback successCallback, Callback failCallback) {
		Log.d(LOG_TAG, "startNotification");

		serviceUUID = VENDOR_PREFIX_UUID_2BYTE + serviceUUID + VENDOR_SUFFIX_UUID_12BYTE ;
		characteristicUUID = VENDOR_PREFIX_UUID_2BYTE + characteristicUUID + VENDOR_SUFFIX_UUID_12BYTE;


		Peripheral peripheral = peripherals.get(deviceUUID);
		if (peripheral != null){
			peripheral.registerNotify(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), successCallback, failCallback);
		} else
			failCallback.invoke();
	}

	//@ReactMethod
	public void stopNotification(String deviceUUID, String serviceUUID, String characteristicUUID, Callback successCallback, Callback failCallback) {
		Log.d(LOG_TAG, "stopNotification");
		serviceUUID = VENDOR_PREFIX_UUID_2BYTE + serviceUUID + VENDOR_SUFFIX_UUID_12BYTE ;
		characteristicUUID = VENDOR_PREFIX_UUID_2BYTE + characteristicUUID + VENDOR_SUFFIX_UUID_12BYTE;


		Peripheral peripheral = peripherals.get(deviceUUID);
		if (peripheral != null){
			peripheral.removeNotify(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), successCallback, failCallback);
		} else
			failCallback.invoke();
	}


	//@ReactMethod
	public void writeAsString(String deviceUUID, String serviceUUID, String characteristicUUID, String message, Callback successCallback, Callback failCallback) {
		Log.d(LOG_TAG, "writeAsString : " + deviceUUID);
		serviceUUID = VENDOR_PREFIX_UUID_2BYTE + serviceUUID + VENDOR_SUFFIX_UUID_12BYTE ;
		characteristicUUID = VENDOR_PREFIX_UUID_2BYTE + characteristicUUID + VENDOR_SUFFIX_UUID_12BYTE;


		Peripheral peripheral = peripherals.get(deviceUUID);
		if (peripheral != null){
			byte[] decoded = Base64.decode(message.getBytes(), Base64.DEFAULT);
			Log.d(LOG_TAG, "Message(" + decoded.length + "): " + Utils.stringFromHex(decoded) + " for device " + deviceUUID);
			peripheral.write(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), decoded, successCallback, failCallback, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
		} else
			failCallback.invoke();
	}

//	-(void) writeWithoutResponse:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
	public void writeWithoutResponse(String deviceUUID, String serviceUUID, String characteristicUUID, String message, Callback successCallback, Callback failCallback)

	{
//		NSLog(@"writeWithoutResponse");
//
//		BLECommandContext *context = [self getData:deviceUUID serviceUUIDString:serviceUUID characteristicUUIDString:characteristicUUID prop:CBCharacteristicPropertyWriteWithoutResponse failCallback:failCallback];
//
//		NSData* dataMessage = [[NSData alloc] initWithBase64EncodedString:message options:0];
//		if (context) {
//			CBPeripheral *peripheral = [context peripheral];
//			CBCharacteristic *characteristic = [context characteristic];
//
//			NSLog(@"Message to write(%lu): %@ ", (unsigned long)[dataMessage length], [Utils stringFromHex:dataMessage]);
//
//			// TODO need to check the max length
//			[peripheral writeValue:dataMessage forCharacteristic:characteristic type:CBCharacteristicWriteWithoutResponse];
//			successCallback(@[]);
//		}
//		else
//		{
//
//			NSLog(@"writeAsNSData blecontext is null");
//			// failCallback(@[@"Error"]);
//		}
	}

	//@ReactMethod
	public void read(String deviceUUID, String serviceUUID, String characteristicUUID, Callback successCallback, Callback failCallback) {
		Log.d(LOG_TAG, "   read : " + deviceUUID);
		serviceUUID = VENDOR_PREFIX_UUID_2BYTE + serviceUUID + VENDOR_SUFFIX_UUID_12BYTE ;
		characteristicUUID = VENDOR_PREFIX_UUID_2BYTE + characteristicUUID + VENDOR_SUFFIX_UUID_12BYTE;


		Peripheral peripheral = peripherals.get(deviceUUID);
		if (peripheral != null){


			peripheral.readCharacteristic(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), successCallback, failCallback);
			//successCallback.invoke(); //  
		} else
			failCallback.invoke();


	}

	//@ReactMethod
	public void writeAsBytes(String deviceUUID, String serviceUUID, String characteristicUUID, ReadableArray msgByteBuf, Callback successCallback, Callback failCallback) {
		Log.d(LOG_TAG, "write: " + deviceUUID);
		serviceUUID = VENDOR_PREFIX_UUID_2BYTE + serviceUUID + VENDOR_SUFFIX_UUID_12BYTE ;
		characteristicUUID = VENDOR_PREFIX_UUID_2BYTE + characteristicUUID + VENDOR_SUFFIX_UUID_12BYTE;


		Peripheral peripheral = peripherals.get(deviceUUID);
		if (peripheral != null){
			//byte[] decoded = Base64.decode(message.getBytes(), Base64.DEFAULT);
			int msgLen = msgByteBuf.size();
			byte[] byteBuf = new byte[msgLen]; //messageByteArr.toString().getBytes();
			for (int m= 0; m < msgLen; m++) {
				//pByteBuf[m] = byteBufTest[m];
				byteBuf[m] = (byte)msgByteBuf.getInt(m);
				Log.v(LOG_TAG, "" + byteBuf[m]);
			}

			Log.d(LOG_TAG, "Message(" + byteBuf.length + "): " + Utils.stringFromHex(byteBuf) + " for device  " + deviceUUID);


			peripheral.write(UUID.fromString(serviceUUID), UUID.fromString(characteristicUUID), byteBuf, successCallback, failCallback, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
			//successCallback.invoke(); //  
		} else
			failCallback.invoke();
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {


				@Override
				public void onLeScan(final BluetoothDevice device, final int rssi,
									 final byte[] scanRecord) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Log.i(LOG_TAG, "DiscoverPeripheral: " + device.getName());
							String address = device.getAddress();

							if (!peripherals.containsKey(address)) {

								Peripheral peripheral = new Peripheral(device, rssi, scanRecord, reactContext);
								peripherals.put(device.getAddress(), peripheral);

								//BundleJSONConverter bjc = new BundleJSONConverter();
								//try {
									Bundle bundle = peripheral.asBundle(); // bjc.convertToBundle(peripheral.asJSONObject());
									WritableMap map = Arguments.fromBundle(bundle);
									sendEvent("BleAdminDiscoverPeripheral", map);
								//} catch (JSONException e) {

								//}


							} else {
								// this isn't necessary
								Peripheral peripheral = peripherals.get(address);
								peripheral.updateRssi(rssi);
							}
						}
					});
				}


			};

	//@ReactMethod
	public void checkState(){
		Log.d(LOG_TAG, "checkState");

		BluetoothAdapter adapter = getBluetoothAdapter();
		String state = "off";
		switch (adapter.getState()){
			case BluetoothAdapter.STATE_ON:
				state = "on";
				break;
			case BluetoothAdapter.STATE_OFF:
				state = "off";
		}

		WritableMap map = Arguments.createMap();
		map.putString("state", state);
		Log.d(LOG_TAG, "state:" + state);
		sendEvent("BleAdminDidUpdateState", map);
	}

	// BLE connection status.
//	-(void)checkPeripheralState:(NSString *)deviceUUID resultCallback:(nonnull RCTResponseSenderBlock)resultCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
	public void checkPeripheralState (String deviceUUID, Callback resultCallback, Callback failCallback)
	{
//		if (manager != nil){
//			CBPeripheral *peripheral = [self findPeripheralByUUID:deviceUUID];
//			//[self centralManagerDidUpdateState:self.manager];
//
//			if(peripheral != nil)
//			{
//				NSString *peripheralStateName = [self peripheralStateToBoolStr: peripheral.state];
//				resultCallback(@[peripheralStateName]);
//			}
//			else
//			{
//				NSString *error = [NSString stringWithFormat:@"Could not find peripherial with UUID %@", deviceUUID];
//				NSLog(@"%@", error);
//				failCallback(@[error]);
//			}
//		}
//		else
//		{
//			NSString *error = [NSString stringWithFormat:@"Internal error"];
//			NSLog(@"%@", error);
//			failCallback(@[error]);
//		}
	}


	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "onReceive");
			final String action = intent.getAction();

			String stringState = "";
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR);
				switch (state) {
					case BluetoothAdapter.STATE_OFF:
						stringState = "off";
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						stringState = "turning_off";
						break;
					case BluetoothAdapter.STATE_ON:
						stringState = "on";
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						stringState = "turning_on";
						break;
				}
			}

			WritableMap map = Arguments.createMap();
			map.putString("state", stringState);
			Log.d(LOG_TAG, "state: " + stringState);
			sendEvent("BleAdminDidUpdateState", map);
		}
	};




}

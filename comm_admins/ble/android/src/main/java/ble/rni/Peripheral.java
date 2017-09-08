package ble.rni;
import android.app.Activity;
import android.bluetooth.*;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Peripheral wraps the BluetoothDevice and provides methods to convert to JSON.
 */
public class Peripheral extends BluetoothGattCallback {

	private static final String CHARACTERISTIC_NOTIFICATION_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public static final String LOG_TAG = "logs";

	private BluetoothDevice device;
	private byte[] advertisingData;
	private int advertisingRSSI;
	private boolean connected = false;
	private ReactContext reactContext;

	private BluetoothGatt gatt;

	private Callback connectCallback;
	private Callback connectFailCallback;
	private Callback readCallback;
	private Callback writeCallback;

	private List<byte[]> writeQueue = new ArrayList<>();

	public Peripheral(BluetoothDevice device, int advertisingRSSI, byte[] scanRecord, ReactContext reactContext) {

		this.device = device;
		this.advertisingRSSI = advertisingRSSI;
		this.advertisingData = scanRecord;
		this.reactContext = reactContext;

	}

	private void sendEvent(String eventName, @Nullable WritableMap params) {
		reactContext
				.getJSModule(RCTNativeAppEventEmitter.class)
				.emit(eventName, params);
	}


	public void connect(Callback connectCallback, Callback failCallback, Activity activity) {
		if (!connected) {
			BluetoothDevice device = getDevice();
			this.connectCallback = connectCallback;
			this.connectFailCallback = failCallback;
			gatt = device.connectGatt(activity, false, this);
		}else{
			connectCallback.invoke();
		}
	}

	public void connect(Callback connectCallback, Callback failCallback, Context baseContext) {
		if (!connected) {
			BluetoothDevice device = getDevice();
			this.connectCallback = connectCallback;
			this.connectFailCallback = failCallback;
			gatt = device.connectGatt(baseContext, false, this);
		}else{
			connectCallback.invoke();
		}
	}

	public void disconnect() {
		connectCallback = null;
		connected = false;
		if (gatt != null) {
			gatt.close();
			gatt = null;
			Log.d(LOG_TAG, "disconnect");
			WritableMap map = Arguments.createMap();
			map.putString("peripheral", device.getAddress());
			sendEvent("BleAdminDisconnectPeripheral", map);
			Log.d(LOG_TAG, "disconnect done for peripheral :" + device.getAddress());
		}else
			Log.d(LOG_TAG, "disconnect failed, gatt is null");
	}

//	public JSONObject asJSONObject() {
//
//		JSONObject json = new JSONObject();
//
//		try {
//			json.put("name", device.getName());
//			json.put("id", device.getAddress()); // mac address
//			json.put("advertising", byteArrayToJSON(advertisingData));
//			// TODO real RSSI if we have it, else
//			json.put("rssi", advertisingRSSI);
//		} catch (JSONException e) { // this shouldn't happen
//			e.printStackTrace();
//		}
//
//		return json;
//	}

	public Bundle asBundle() {
//		NSString *uuidString = NULL;
//		if (self.identifier.UUIDString) {
//			uuidString = self.identifier.UUIDString;
//		} else {
//			uuidString = @"";
//		}

		//NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
		Bundle bundle = new Bundle();


		//[dictionary setObject: uuidString forKey: @"id"];
		bundle.putString("id", device.getAddress());

//		if ([self name]) {
//			[dictionary setObject: [self name] forKey: @"name"];
//		}

		bundle.putString("name", device.getName());




//		if ([self RSSI]) {
//			[dictionary setObject: [self RSSI] forKey: @"rssi"];
//		} else if ([self advertisementRSSI]) {
//			[dictionary setObject: [self advertisementRSSI] forKey: @"rssi"];
//		}
		bundle.putInt("rssi", advertisingRSSI);

//		if ([self advertising]) {
//			[dictionary setObject: [self advertising] forKey: @"advertising"];
//		}
		//bundle.put("advertising", byteArrayToJSON(advertisingData));

//		if([[self services] count] > 0) {
//			[self serviceAndCharacteristicInfo: dictionary];
//		}

		return bundle;

	}

//	public JSONObject asJSONObject(BluetoothGatt gatt) {
//
//		JSONObject json = asJSONObject();
//
//		try {
//			JSONArray servicesArray = new JSONArray();
//			JSONArray characteristicsArray = new JSONArray();
//			json.put("services", servicesArray);
//			json.put("characteristics", characteristicsArray);
//
//			if (connected && gatt != null) {
//				for (BluetoothGattService service : gatt.getServices()) {
//					servicesArray.put(Utils.uuid2Str(service.getUuid()));
//
//					for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
//						JSONObject characteristicsJSON = new JSONObject();
//						characteristicsArray.put(characteristicsJSON);
//
//						characteristicsJSON.put("service", Utils.uuid2Str(service.getUuid()));
//						characteristicsJSON.put("characteristic", Utils.uuid2Str(characteristic.getUuid()));
//						//characteristicsJSON.put("instanceId", characteristic.getInstanceId());
//
//						characteristicsJSON.put("properties", PeripheralExtension.decodeCharacteristicProperties(characteristic));
//						// characteristicsJSON.put("propertiesValue", characteristic.getProperties());
//
//						if (characteristic.getPermissions() > 0) {
//							characteristicsJSON.put("permissions", PeripheralExtension.decodePermissions(characteristic));
//							// characteristicsJSON.put("permissionsValue", characteristic.getPermissions());
//						}
//
//						JSONArray descriptorsArray = new JSONArray();
//
//						for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
//							JSONObject descriptorJSON = new JSONObject();
//							descriptorJSON.put("uuid", Utils.uuid2Str(descriptor.getUuid()));
//							descriptorJSON.put("value", descriptor.getValue()); // always blank
//
//							if (descriptor.getPermissions() > 0) {
//								descriptorJSON.put("permissions", PeripheralExtension.decodePermissions(descriptor));
//								// descriptorJSON.put("permissionsValue", descriptor.getPermissions());
//							}
//							descriptorsArray.put(descriptorJSON);
//						}
//						if (descriptorsArray.length() > 0) {
//							characteristicsJSON.put("descriptors", descriptorsArray);
//						}
//					}
//				}
//			}
//		} catch (JSONException e) { // TODO better error handling
//			e.printStackTrace();
//		}
//
//		return json;
//	}

	static JSONObject byteArrayToJSON(byte[] bytes) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("CDVType", "ArrayBuffer");
		object.put("data", Base64.encodeToString(bytes, Base64.NO_WRAP));
		return object;
	}

	public boolean isConnected() {
		return connected;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		super.onServicesDiscovered(gatt, status);
		connectCallback.invoke();
		connectCallback = null;
		connectFailCallback = null;
	}

	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

		Log.d(LOG_TAG, "onConnectionStateChange  status = " + status + " newState =  " + newState + " for peripheral:" + device.getAddress());

		this.gatt = gatt;

		if (newState == BluetoothGatt.STATE_CONNECTED) {

			connected = true;
			gatt.discoverServices();

		} else if (newState == BluetoothGatt.STATE_DISCONNECTED){

			if (connected) {
				connected = false;

				if (gatt != null) {
					gatt.close();
					gatt = null;
				}

				WritableMap map = Arguments.createMap();
				map.putString("peripheral", device.getAddress());
				sendEvent("BleAdminDisconnectPeripheral", map);
				Log.d(LOG_TAG, "BleManagerDisconnectPeripheral peripheral:" + device.getAddress());
			}
			if (connectFailCallback != null) {
				connectFailCallback.invoke();
				connectFailCallback = null;
				connectCallback = null;
			}

		}

	}

	public void updateRssi(int rssi) {
		advertisingRSSI = rssi;
	}

	public int unsignedToBytes(byte b) {
		return b & 0xFF;
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		super.onCharacteristicChanged(gatt, characteristic);

		//Log.d(LOG_TAG, "onCharacteristicChanged " + characteristic);
		byte[] dataValue1 = characteristic.getValue();
		//String value1 = BleAdmin.bytesToHex(dataValue1);
		String stringFromData = Utils.stringFromHex(characteristic.getValue());
		String characteristicUUIDStr =  characteristic.getUuid().toString();
		String characteristicUUIDStrShort = characteristicUUIDStr.substring(4,8);

		WritableMap map = Arguments.createMap();
		map.putString("peripheral", device.getAddress());
		map.putString("characteristic", characteristicUUIDStrShort.toUpperCase());

		Log.d(LOG_TAG, "  onCharacteristicChanged " + characteristic + "value = " + stringFromData);


		//	if([characteristic.UUID.UUIDString isEqualToString:@"4286"] ||
		//	[characteristic.UUID.UUIDString isEqualToString:@"93D2"] )
		if(     characteristicUUIDStrShort.equalsIgnoreCase("4286") ||
				characteristicUUIDStrShort.equalsIgnoreCase("93D2") ||
				characteristicUUIDStrShort.equalsIgnoreCase("2A19") ||
				characteristicUUIDStrShort.equalsIgnoreCase("28A5") ||
				characteristicUUIDStrShort.equalsIgnoreCase("28A4") ||
				characteristicUUIDStrShort.equalsIgnoreCase("2A26") ||
				characteristicUUIDStrShort.equalsIgnoreCase("28A2")
				)
		{
			//NSArray *numberArrayData = [Utils numberArrayFromHex:characteristic.value];

			//ArrayList<Integer> numberArrayList = BleAdmin.numberArrayFromHex(characteristic.getValue());
			//WritableArray numberArrayData = Arguments.fromArray(numberArrayList);
			int [] numberArray = Utils.numberArrayFromHex(characteristic.getValue());
			WritableArray numberArrayData = Arguments.fromArray(numberArray); // numberArrayList
			map.putArray("value", numberArrayData);

			//[self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidUpdateValueForCharacteristic" body:@{@"peripheral": peripheral.uuidAsString, @"characteristic":characteristic.UUID.UUIDString, @"value": numberArrayData}];

		}
		else
		{

			//	NSString *stringFromData = [Utils stringFromHex:characteristic.value];
			map.putString("value", stringFromData);
			//			[self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidUpdateValueForCharacteristic" body:@{@"peripheral": peripheral.uuidAsString, @"characteristic":characteristic.UUID.UUIDString, @"value": stringFromData}];



		}

		sendEvent("BleAdminDidUpdateValueForCharacteristic", map);
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		super.onCharacteristicRead(gatt, characteristic, status);
		//Log.d(LOG_TAG, "  onCharacteristicRead " + characteristic);
		byte[] dataValue1 = characteristic.getValue();
		//String value1 = BleAdmin.bytesToHex(dataValue1);
		String stringFromData = Utils.stringFromHex(characteristic.getValue());
		String characteristicUUIDStr =  characteristic.getUuid().toString();
		String characteristicUUIDStrShort = characteristicUUIDStr.substring(4,8);

		WritableMap map = Arguments.createMap();
		map.putString("peripheral", device.getAddress());
		map.putString("characteristic", characteristicUUIDStrShort.toUpperCase());

		Log.d(LOG_TAG, "  onCharacteristicRead " + characteristic + "value = " + stringFromData);


	//	if([characteristic.UUID.UUIDString isEqualToString:@"4286"] ||
	//	[characteristic.UUID.UUIDString isEqualToString:@"93D2"] )
		if(     characteristicUUIDStrShort.equalsIgnoreCase("4286") ||
				characteristicUUIDStrShort.equalsIgnoreCase("93D2") ||
				characteristicUUIDStrShort.equalsIgnoreCase("2A19") ||
				characteristicUUIDStrShort.equalsIgnoreCase("28A5") ||
				characteristicUUIDStrShort.equalsIgnoreCase("28A4") ||
				characteristicUUIDStrShort.equalsIgnoreCase("2A26") ||
				characteristicUUIDStrShort.equalsIgnoreCase("28A2")
				)
		{
			//NSArray *numberArrayData = [Utils numberArrayFromHex:characteristic.value];

			//ArrayList<Integer> numberArrayList = BleAdmin.numberArrayFromHex(characteristic.getValue());
			//WritableArray numberArrayData = Arguments.fromArray(numberArrayList);
			int [] numberArray = Utils.numberArrayFromHex(characteristic.getValue());
			WritableArray numberArrayData = Arguments.fromArray(numberArray);

			map.putArray("value", numberArrayData);

			//[self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidUpdateValueForCharacteristic" body:@{@"peripheral": peripheral.uuidAsString, @"characteristic":characteristic.UUID.UUIDString, @"value": numberArrayData}];

		}
		else
		{

		//	NSString *stringFromData = [Utils stringFromHex:characteristic.value];
			map.putString("value", stringFromData);
		//			[self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidUpdateValueForCharacteristic" body:@{@"peripheral": peripheral.uuidAsString, @"characteristic":characteristic.UUID.UUIDString, @"value": stringFromData}];



		}

		sendEvent("BleAdminDidUpdateValueForCharacteristic", map);

		if (readCallback != null) {

			if (status == BluetoothGatt.GATT_SUCCESS) {
				byte[] dataValue = characteristic.getValue();
				String value = Utils.stringFromHex(dataValue);

				if (readCallback != null) {
					readCallback.invoke(value);
					readCallback = null;
				}
			} else {
				//readCallback.error("Error reading " + characteristic.getUuid() + " status=" + status);
			}

			readCallback = null;

		}

	}

		@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		super.onCharacteristicWrite(gatt, characteristic, status);
		//Log.d(LOG_TAG, "onCharacteristicWrite " + characteristic);

		if (writeCallback != null) {

			if (writeQueue.size() > 0){
				byte[] data = writeQueue.get(0);
				writeQueue.remove(0);
				Log.d(LOG_TAG, "writing data , size =  " + writeQueue.size());
				doWrite(characteristic, data);
			} else {

				if (status == BluetoothGatt.GATT_SUCCESS) {
					writeCallback.invoke();
					Log.e(LOG_TAG, "writeCallback invoked");
				} else {
					//writeCallback.error(status);
					Log.e(LOG_TAG, "error onCharacteristicWrite:" + status);
				}

				writeCallback = null;
			}
		}else
			Log.e(LOG_TAG, "write call back is null");
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		super.onDescriptorWrite(gatt, descriptor, status);
		Log.d(LOG_TAG, "onDescriptorWrite " + descriptor);
	}



	// This seems way too complicated
	public void registerNotify(UUID serviceUUID, UUID characteristicUUID, Callback success, Callback fail) {

		Log.d(LOG_TAG, "registerNotify");

		if (gatt == null) {
			fail.invoke("BluetoothGatt is null");
			return;
		}

		BluetoothGattService service = gatt.getService(serviceUUID);
		BluetoothGattCharacteristic characteristic = findNotifyCharacteristic(service, characteristicUUID);
		//String key = generateHashKey(serviceUUID, characteristic);

		if (characteristic != null) {
			Log.d(LOG_TAG, "characteristic ok");

			if (gatt.setCharacteristicNotification(characteristic, true)) {

				BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_NOTIFICATION_CONFIG));
				if (descriptor != null) {
					Log.d(LOG_TAG, " descriptor");

					// prefer notify over indicate
					if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
						Log.d(LOG_TAG, "Characteristic " + characteristicUUID + " set NOTIFY");
						descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					} else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
						Log.d(LOG_TAG, "Characteristic " + characteristicUUID + " set INDICATE");
						descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
					} else {
						Log.d(LOG_TAG, "Characteristic " + characteristicUUID + " does not have NOTIFY or INDICATE property set");
					}

					if (gatt.writeDescriptor(descriptor)) {
						// Tutto ok
						Log.d(LOG_TAG, "registerNotify done ");
						success.invoke();
					} else {
						fail.invoke("Failed to set client characteristic notification for " + characteristicUUID);
					}

				} else {
					fail.invoke("Set notification failed for " + characteristicUUID);
				}

			} else {
				fail.invoke("Failed to register notification for " + characteristicUUID);
			}

		} else {
			fail.invoke("Characteristic " + characteristicUUID + " not found");
		}

	}

	public void removeNotify(UUID serviceUUID, UUID characteristicUUID, Callback success, Callback fail) {

		Log.d(LOG_TAG, "removeNotify");

		if (gatt == null) {
			fail.invoke("BluetoothGatt is null");
			return;
		}

		BluetoothGattService service = gatt.getService(serviceUUID);
		BluetoothGattCharacteristic characteristic = findNotifyCharacteristic(service, characteristicUUID);
		//String key = generateHashKey(serviceUUID, characteristic);

		if (characteristic != null) {


			if (gatt.setCharacteristicNotification(characteristic, false)) {
				success.invoke();
			} else {
				// TODO we can probably ignore and return success anyway since we removed the notification callback
				fail.invoke("Failed to stop notification for " + characteristicUUID);
			}

		} else {
			fail.invoke("Characteristic " + characteristicUUID + " not found");
		}


	}

	// Some devices reuse UUIDs across characteristics, so we can't use service.getCharacteristic(characteristicUUID)
	// instead check the UUID and properties for each characteristic in the service until we find the best match
	// This function prefers Notify over Indicate
	private BluetoothGattCharacteristic findNotifyCharacteristic(BluetoothGattService service, UUID characteristicUUID) {
		BluetoothGattCharacteristic characteristic = null;

		try {
			// Check for Notify first
			List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
			for (BluetoothGattCharacteristic c : characteristics) {
				if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0 && characteristicUUID.equals(c.getUuid())) {
					characteristic = c;
					break;
				}
			}

			if (characteristic != null) return characteristic;

			// If there wasn't Notify Characteristic, check for Indicate
			for (BluetoothGattCharacteristic c : characteristics) {
				if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0 && characteristicUUID.equals(c.getUuid())) {
					characteristic = c;
					break;
				}
			}

			// As a last resort, try and find ANY characteristic with this UUID, even if it doesn't have the correct properties
			if (characteristic == null) {
				characteristic = service.getCharacteristic(characteristicUUID);
			}

			return characteristic;
		}catch (Exception e) {
			Log.e(LOG_TAG, "Error for characteristic  " + characteristicUUID ,e);
			return null;
		}
	}

	public void readCharacteristic( UUID serviceUUID, UUID characteristicUUID, Callback failCallback, Callback successCallback) {

		boolean success = false;

		if (gatt == null) {
			//callbackContext.error();
			failCallback.invoke("BluetoothGatt is null");
			return;
		}

		BluetoothGattService service = gatt.getService(serviceUUID);
		BluetoothGattCharacteristic characteristic = findReadableCharacteristic(service, characteristicUUID);

		if (characteristic == null) {
			//callbackContext.error("Characteristic " + characteristicUUID + " not found.");
			failCallback.invoke("Characteristic " + characteristicUUID + " not found.");
		} else {
			readCallback = null; // successCallback;   Lets send read value by events - TO DO later
			if (gatt.readCharacteristic(characteristic)) {
				success = true;
			} else {
				readCallback = null;
				//callbackContext.error("Read failed");
				failCallback.invoke("Read failed");
			}
		}

		if (!success) {
			//commandCompleted();
		}

	}
	/* original func
	private void readCharacteristic(CallbackContext callbackContext, UUID serviceUUID, UUID characteristicUUID) {

		boolean success = false;

		if (gatt == null) {
			callbackContext.error("BluetoothGatt is null");
			return;
		}

		BluetoothGattService service = gatt.getService(serviceUUID);
		BluetoothGattCharacteristic characteristic = findReadableCharacteristic(service, characteristicUUID);

		if (characteristic == null) {
			callbackContext.error("Characteristic " + characteristicUUID + " not found.");
		} else {
			readCallback = callbackContext;
			if (gatt.readCharacteristic(characteristic)) {
				success = true;
			} else {
				readCallback = null;
				callbackContext.error("Read failed");
			}
		}

		if (!success) {
			commandCompleted();
		}

	}
*/
	// Some peripherals re-use UUIDs for multiple characteristics so we need to check the properties
	// and UUID of all characteristics instead of using service.getCharacteristic(characteristicUUID)
	private BluetoothGattCharacteristic findReadableCharacteristic(BluetoothGattService service, UUID characteristicUUID) {
		BluetoothGattCharacteristic characteristic = null;

		int read = BluetoothGattCharacteristic.PROPERTY_READ;

		List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
		for (BluetoothGattCharacteristic c : characteristics) {
			if ((c.getProperties() & read) != 0 && characteristicUUID.equals(c.getUuid())) {
				characteristic = c;
				break;
			}
		}

		// As a last resort, try and find ANY characteristic with this UUID, even if it doesn't have the correct properties
		if (characteristic == null) {
			characteristic = service.getCharacteristic(characteristicUUID);
		}

		return characteristic;
	}



	public void doWrite(BluetoothGattCharacteristic characteristic, byte[] data) {

		//Log.d(LOG_TAG, "doWrite");

		characteristic.setValue(data);

		if (gatt.writeCharacteristic(characteristic)) {
			//Log.d(LOG_TAG, "doWrite done");
		} else {
			Log.d(LOG_TAG, "error doWrite");
		}

	}

	public void write(UUID serviceUUID, UUID characteristicUUID, byte[] data, Callback successCallback, Callback failCallback, int writeType) {
		try {
			Thread.sleep(100); // don't allow next write before "CI duration+margin" ms so as to complete finish previous write.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Log.d(LOG_TAG, "write  peripheral");

		if (gatt == null) {
			failCallback.invoke("BluetoothGatt is null");

		}else {

			BluetoothGattService service = gatt.getService(serviceUUID);
			BluetoothGattCharacteristic characteristic = findWritableCharacteristic(service, characteristicUUID, writeType);
			characteristic.setWriteType(writeType);

			if (characteristic == null) {
				failCallback.invoke("Characteristic " + characteristicUUID + " not found.");
			} else {

				if (writeQueue.size() > 0) {
					failCallback.invoke("Scrittura con byte ancora in coda");
				}

				if ( writeCallback != null) {
					failCallback.invoke("Altra scrittura in corso");
				}

				if (writeQueue.size() == 0 && writeCallback == null) {

					if (BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT == writeType)
						writeCallback = successCallback;
					else
						successCallback.invoke();

					if (data.length > 20) {
						int dataLength = data.length;
						int count = 0;
						byte[] firstMessage = null;
						while (count < dataLength && (dataLength - count > 20)) {
							if (count == 0) {
								firstMessage = Arrays.copyOfRange(data, count, count + 20);
							} else {
								byte[] splitMessage = Arrays.copyOfRange(data, count, count + 20);
								writeQueue.add(splitMessage);
							}
							count += 20;
						}
						if (count < dataLength) {
							
							byte[] splitMessage = Arrays.copyOfRange(data, count, data.length);
							Log.d(LOG_TAG, "splitMessage.length =  " + splitMessage.length);
							writeQueue.add(splitMessage);
						}

						Log.d(LOG_TAG, "writeQueue.size = " + writeQueue.size());
						doWrite(characteristic, firstMessage);
					} else {
						characteristic.setValue(data);


						if (gatt.writeCharacteristic(characteristic)) {
							Log.d(LOG_TAG, "write done");
						} else {
							writeCallback = null;
							failCallback.invoke("Write failed");
						}
					}
				}
			}
		}

	}

	// Some peripherals re-use UUIDs for multiple characteristics so we need to check the properties
	// and UUID of all characteristics instead of using service.getCharacteristic(characteristicUUID)
	private BluetoothGattCharacteristic findWritableCharacteristic(BluetoothGattService service, UUID characteristicUUID, int writeType) {
		try {
			BluetoothGattCharacteristic characteristic = null;

			// get write property
			int writeProperty = BluetoothGattCharacteristic.PROPERTY_WRITE;
			if (writeType == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
				writeProperty = BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
			}

			List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
			for (BluetoothGattCharacteristic c : characteristics) {
				if ((c.getProperties() & writeProperty) != 0 && characteristicUUID.equals(c.getUuid())) {
					characteristic = c;
					break;
				}
			}

			// As a last resort, try and find ANY characteristic with this UUID, even if it doesn't have the correct properties
			if (characteristic == null) {
				characteristic = service.getCharacteristic(characteristicUUID);
			}

			return characteristic;
		}catch (Exception e) {
			Log.e(LOG_TAG, "Error in findWritableCharacteristic", e);
			return null;
		}
	}

	private String generateHashKey(BluetoothGattCharacteristic characteristic) {
		return generateHashKey(characteristic.getService().getUuid(), characteristic);
	}

	private String generateHashKey(UUID serviceUUID, BluetoothGattCharacteristic characteristic) {
		return String.valueOf(serviceUUID) + "|" + characteristic.getUuid() + "|" + characteristic.getInstanceId();
	}




}

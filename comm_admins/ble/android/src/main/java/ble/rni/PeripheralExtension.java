package ble.rni;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import org.json.JSONArray;

/**
 *  Created by Eric   on 3/21/17.
 */
public class PeripheralExtension {

    //-(NSArray *) decodeCharacteristicProperties: (CBCharacteristic *) characteristic {
    public static JSONArray decodeCharacteristicProperties(BluetoothGattCharacteristic characteristic) {

        //NSMutableArray *props = [NSMutableArray new];
        JSONArray props = new JSONArray();

        //CBCharacteristicProperties p = [characteristic properties];
        int p = characteristic.getProperties();

        // NOTE: props strings need to be consistent across iOS and Android


//        if ((p & CBCharacteristicPropertyBroadcast) != 0x0) {
//            [props addObject:@"Broadcast"];
//        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_BROADCAST) != 0x0 ) {
            props.put("Broadcast");
        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_READ) != 0x0 ) {
            props.put("Read");
        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0x0 ) {
            props.put("WriteWithoutResponse");
        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0x0 ) {
            props.put("Write");
        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0x0 ) {
            props.put("Notify");
        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0x0 ) {
            props.put("Indicate");
        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0x0 ) {
            // Android calls this "write with signature", using iOS name for now
            props.put("AuthenticateSignedWrites");
        }

        if ((p & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) != 0x0 ) {
            props.put("ExtendedProperties");
        }

//        iOS only
//
//        if ((p & CBCharacteristicPropertyNotifyEncryptionRequired) != 0x0) {
//            [props addObject:@"NotifyEncryptionRequired"];
//        }
//
//        if ((p & CBCharacteristicPropertyIndicateEncryptionRequired) != 0x0) {
//            [props addObject:@"IndicateEncryptionRequired"];
//        }

        return props;
    }

//    public static JSONArray decodePermissions(BluetoothGattCharacteristic characteristic) {
//
//        // NOTE: props strings need to be consistent across iOS and Android
//        JSONArray props = new JSONArray();
//        int permissions = characteristic.getPermissions();
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ) != 0x0 ) {
//            props.put("Read");
//        }
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE) != 0x0 ) {
//            props.put("Write");
//        }
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) != 0x0 ) {
//            props.put("ReadEncrypted");
//        }
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) != 0x0 ) {
//            props.put("WriteEncrypted");
//        }
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) != 0x0 ) {
//            props.put("ReadEncryptedMITM");
//        }
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) != 0x0 ) {
//            props.put("WriteEncryptedMITM");
//        }
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) != 0x0 ) {
//            props.put("WriteSigned");
//        }
//
//        if ((permissions & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) != 0x0 ) {
//            props.put("WriteSignedMITM");
//        }
//
//        return props;
//    }

//    public static JSONArray decodePermissions(BluetoothGattDescriptor descriptor) {
//
//        // NOTE: props strings need to be consistent across iOS and Android
//        JSONArray props = new JSONArray();
//        int permissions = descriptor.getPermissions();
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_READ) != 0x0 ) {
//            props.put("Read");
//        }
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE) != 0x0 ) {
//            props.put("Write");
//        }
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED) != 0x0 ) {
//            props.put("ReadEncrypted");
//        }
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED) != 0x0 ) {
//            props.put("WriteEncrypted");
//        }
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM) != 0x0 ) {
//            props.put("ReadEncryptedMITM");
//        }
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM) != 0x0 ) {
//            props.put("WriteEncryptedMITM");
//        }
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED) != 0x0 ) {
//            props.put("WriteSigned");
//        }
//
//        if ((permissions & BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM) != 0x0 ) {
//            props.put("WriteSignedMITM");
//        }
//
//        return props;
//    }

}

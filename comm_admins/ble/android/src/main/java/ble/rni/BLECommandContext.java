package ble.rni;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

class BLECommandContext {

    //@property CBPeripheral *peripheral;
    public Peripheral peripheral;
   // @property CBService *service;
    public BluetoothGattService service;
    //@property CBCharacteristic *characteristic;
    public BluetoothGattCharacteristic characteristic;



    public BLECommandContext(Peripheral peripheral, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        this.peripheral = peripheral;
        this.service = service;
        this.characteristic = characteristic;
    }


}
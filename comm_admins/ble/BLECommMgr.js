//
//  BLECommMgr.js
//
//   Created by Eric  .
//

'use strict'

import React, { Component } from 'react';
import { getBleAdminInstance} from './BleAdmin';
import DLog from '../../common/DLog'; 
var objBleAdmin  = getBleAdminInstance();

var instanceBLECommMgr = null;
var nBLECommMgrObjCnt = 0;

export function getBLECommMgrInstance()
{
    
    if(nBLECommMgrObjCnt > 0 )
    {
        DLog.printDebugMsg("It's a singleton class, returning existing instance of BLECommMgr class ");

        return instanceBLECommMgr;
    }
    
    nBLECommMgrObjCnt++;
    DLog.printDebugMsg("BLECommMgr Object Count is " + nBLECommMgrObjCnt);
    
    instanceBLECommMgr = new BLECommMgr();
    return instanceBLECommMgr;

}


class BLECommMgr  {
    
   
    
    constructor(props){
        DLog.printDebug(this," Entered this constructor");
     
     
    }
    

    scanForService(serviceUUIDs, seconds) {
 
        DLog.printDebug(this," Entered scanForService");
        return objBleAdmin.scan(serviceUUIDs,seconds);

    }
    
    scanAllServices(scanTimeOut) {
        DLog.printDebug(this," Entered scanAllServices");
        return objBleAdmin.scan([],scanTimeOut);

    }

    
     connectToDevice (peripheralId) {
         DLog.printDebug(this, "Entered connectToDevice ");
         return objBleAdmin.connect(peripheralId);
         
    }

    disconnectDevice(peripheralId) {
        DLog.printDebug(this," Entered disconnectDevice ");
        return objBleAdmin.disconnect(peripheralId);
        
    }
    
    
    getBleDeviceRSSI(peripheralId) {
        DLog.printDebug(this," Entered getBleDeviceRSSI ");
        return objBleAdmin.getBleRSSI(peripheralId);
        
    }
    
    isDeviceConnnected(peripheralId) {
        DLog.printDebug(this," Entered isDeviceConnnected ");
        return objBleAdmin.isDeviceConnnected(peripheralId);
        
    }

    readCharacteristic(peripheralId, serviceUUID, characteristicUUID) {
        DLog.printDebug(this," Entered readCharacteristic")
        return objBleAdmin.read(peripheralId, serviceUUID, characteristicUUID);

    }
    
    readCharacteristicsByteArray(peripheralId, serviceUUID, characteristicUUID, characteristicDataType) {
        DLog.printDebug(this," Entered readCharacteristic")
        return objBleAdmin.read_bytes(peripheralId, serviceUUID, characteristicUUID,characteristicDataType);
        
    }
    
    
    /*writeCharacteristicASCIIValue(peripheralId, serviceUUID, characteristicUUID, data) {
        DLog.printDebug(this," Entered writeCharacteristicASCIIValue");
        return objBleAdmin.write_bytes(peripheralId, serviceUUID, characteristicUUID, data);
    }*/
    
    writeCharacteristicValueAsByteArray(peripheralId, serviceUUID, characteristicUUID, dataByteArr) {
        DLog.printDebug(this," Entered writeCharacteristicValueAsByteArray " + ": " + peripheralId +  ": " + serviceUUID +  ": " +characteristicUUID  + ": " + dataByteArr);
        return objBleAdmin.write_bytes(peripheralId, serviceUUID, characteristicUUID, dataByteArr);
    }
    
    indicateAckCharacteristic(peripheralId, serviceUUID, characteristicUUID) {
        DLog.printDebug(this," Entered indicateAckCharacteristic");
        return objBleAdmin.startNotification(peripheralId, serviceUUID, characteristicUUID);
    }
    
    indicateAckCharacteristicByteArray(peripheralId, serviceUUID, characteristicUUID, characteristicDataType) {
        DLog.printDebug(this," Entered indicateAckCharacteristic");
        return objBleAdmin.startNotification_bytes(peripheralId, serviceUUID, characteristicUUID, characteristicDataType);
    }
    
    notifyCharacteristic(peripheralId, serviceUUID, characteristicUUID) {
        DLog.printDebug(this," Entered notifyCharacteristic")
        return objBleAdmin.startNotification(peripheralId, serviceUUID, characteristicUUID);

    }
    
    stopIndicateAckCharacteristic(peripheralId, serviceUUID, characteristicUUID) {
        DLog.printDebug(this," Entered stopIndicateAckCharacteristic");
        return objBleAdmin.stopNotification(peripheralId, serviceUUID, characteristicUUID);
    }
    
    stopNotifyCharacteristic(peripheralId, serviceUUID, characteristicUUID) {
        DLog.printDebug(this," Entered stopNotifyCharacteristic")
        return objBleAdmin.stopNotification(peripheralId, serviceUUID, characteristicUUID);
        
    }
    

}


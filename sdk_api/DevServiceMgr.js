//
//  DevServiceMgr.js
//
//   Created by Eric  .
//





'use strict'

import React, { Component } from 'react';
import { NativeAppEventEmitter } from 'react-native';

import DLog from '../common/DLog';
import {DLogCfg} from '../common/DLog';

import DevServiceFactory from './DevServiceFactory';


import { getBLECommMgrInstance} from '../comm_admins/ble/BLECommMgr';
import { getSDKServiceMgrInstance} from './SDKServiceMgr';


var self = null;
var instanceDevServiceMgr = null;
var nDevServiceMgrObjCnt = 0;

const NodeEvents = require('events');

import {createService} from './DevServiceFactory';

import DeviceEventTypes from './DeviceEventTypes';
export var DeviceEvents = DeviceEventTypes;


/**
 * @private
 */
export function getDevServiceMgrInstance(bDebugMode)
{
  
    if(bDebugMode != undefined)
    {
        DLogCfg.DEBUG_MODE = bDebugMode;
    }

    
    if(nDevServiceMgrObjCnt > 0 )
    {
        DLog.printDebugMsg("It's a singleton class, returning existing instance of DevServiceMgr class ");

        return instanceDevServiceMgr;
    }
    
    
    nDevServiceMgrObjCnt++;
    DLog.printDebugMsg("DevServiceMgr Object Count is " + nDevServiceMgrObjCnt);

    
    instanceDevServiceMgr = new DevServiceMgr();
    //instanceDevServiceMgr.setDebugMode(bDebugMode);

    
    return instanceDevServiceMgr;
}



/**
 * @private
 * This class provides interface to all the characteristics of a particular service
 * that is defined in the BLE profile of a device through Bluetooth developer studio.
 * @extends NodeEvents
 */
class DevServiceMgr extends NodeEvents {

    
constructor(props){
    super(props);
    self = this;
    DLog.printDebugMsg("entered DevServiceMgr-constructor");
    
    this.objSDKServiceMgr  = getSDKServiceMgrInstance();
    this.objBLECommMgr  = getBLECommMgrInstance();
    
    this.setNativeListeners();
    


}
setDebugMode(bDebugMode)
{
    DLogCfg.DEBUG_MODE = bDebugMode;
}
    
/** 
 * @private
 */
setNativeListeners()
{
    
DLog.printDebug(this,"   setNativeListeners");

NativeAppEventEmitter.addListener('NativeLibLoad',
                                  (args) => {
                                  
                                      if(args.status === '1')
                                      {
                                  
                                            DLog.printDebug(this,' RN   BleAdmin Load Success = ' + args.status );
                                            self.emit(DeviceEvents.kNativeLibLoad,1);
                                      }
                                      else
                                      {
                                            DLog.printDebug(this,'   BleAdmin Load failed = ' + args.status);
                                            self.emit(DeviceEvents.kNativeLibLoad,0);
                                      }
                                  
                                  }
                                  );

NativeAppEventEmitter.addListener('BleAdminDiscoverPeripheral',
                                  (args) => {

                                        DLog.printDebug(this,'    Peripheral found  ' + args.id + '/' + args.name);
                                        //self.emit(DeviceEvents.kDeviceFound, args.id, args.name,args.rssi);
                                  
                                       this.objSDKServiceMgr.getDevAdmin().getDeviceScanner().handleReadEvent(args);
                                  
                                  }
                                  );

NativeAppEventEmitter.addListener('BleAdminDisconnectPeripheral',
                                  (args) => {

                                        DLog.printDebug(this,'    Peripheral disconnected  ' + args.peripheral +
                                                    ' due to reason: ' + args.disconnect_reason);
                                        self.emit(DeviceEvents.kBleConnectionLost, args.peripheral, args.disconnect_reason);

                                  }
                                  );

NativeAppEventEmitter.addListener('BleAdminDidUpdateState',
                                  (args) => {
                                  
                                        DLog.printDebug(this,'     Peripheral connection state changed to :  ' + args.state );
                                        self.emit(DeviceEvents.kBleConnectionStateChanged, args.state);
                                  
                                  }
                                  );


NativeAppEventEmitter.addListener('BleAdminStopScan',
                                  () => {
                                  
                                        DLog.printDebug(this,'    scanning stopped ' );
                                        self.emit(DeviceEvents.kScanFinished);

                                  }
                                  );

NativeAppEventEmitter.addListener('BleAdminDidReadRSSI',
                                  (args) => {
                                  
                                  DLog.printDebug(this,'    read RSSI for peripheral ' + args.peripheral + ' rssi_value = ' + args.value);
                                  //self.emit(DeviceEvents.kRSSIValue, args.peripheral, args.value);
                                  this.objSDKServiceMgr.getDevAdmin().getDeviceAccessor().handleReadEvent(args);
                                  }
                                  );
    
}



getService(nSvcTpye) {
    let connectedPeripheralID = this.objSDKServiceMgr.getDevAdmin().getDeviceAccessor().getConnectedPeripheralID();
    DLog.printDebug(this,"getService: " + nSvcTpye + " for connectedPeripheralID = " + connectedPeripheralID);
    
    return DevServiceFactory.createService(nSvcTpye, this.objBLECommMgr, connectedPeripheralID );

}
 


    
}



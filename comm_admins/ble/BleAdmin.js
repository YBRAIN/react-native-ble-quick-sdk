//
//  BleAdmin.js
//
//   Created by Eric  .
//

'use strict';
var React = require('react-native');

var bleAdminRNI = React.NativeModules.CommAdminBleRNI;

var nBleAdminObjCnt = 0;
var instanceBleAdmin = null;

import DLog from '../../common/DLog';

export function getBleAdminInstance()
{

    
    if(nBleAdminObjCnt > 0 ){
        
         DLog.printDebugMsg("It's a singleton class, returning existing instance of BleAdmin class ");

        return instanceBleAdmin;
        
    }
    
    nBleAdminObjCnt++;
     DLog.printDebugMsg("BleAdmin Object Count is " + nBleAdminObjCnt);

    instanceBleAdmin = new BleAdmin();
    return instanceBleAdmin;
}


class BleAdmin  {
    
    
  read(peripheralId, serviceUUID, characteristicUUID) {
    return new Promise( (fulfill, reject) => {
                       
      bleAdminRNI.native_read(
                      peripheralId,
                      serviceUUID,
                      characteristicUUID,
                      (success) => { fulfill(); },
                      (fail) => { reject(fail); }
                      
                      );
                       
                    }
                    );
  }

read_bytes(peripheralId, serviceUUID, characteristicUUID, characteristicDataType) {
    return new Promise( (fulfill, reject) => {
                       
                       bleAdminRNI.native_read_bytearray(
                                               peripheralId,
                                               serviceUUID,
                                               characteristicUUID,
                                               characteristicDataType,
                                               (success) => { fulfill(); },
                                               (fail) => { reject(fail); }
                                               
                                               );
                       
                       }
                       );
}
    
  write_string(peripheralId, serviceUUID, characteristicUUID, data) {
    return new Promise((fulfill, reject) => {
      bleAdminRNI.native_write_string(peripheralId, serviceUUID, characteristicUUID, data, (success) => {
        fulfill();
      }, (fail) => {
        reject(fail);
      });
    });
  }
    
  write_bytes(peripheralId, serviceUUID, characteristicUUID, dataByteArr) {
        return new Promise((fulfill, reject) => {
                           
                           bleAdminRNI.native_write_bytes(peripheralId, serviceUUID, characteristicUUID, dataByteArr,
                                                 (success) => { fulfill(); },
                                                 (fail) => { reject(fail); }
                                                          );
                           }
                           );
    }
    
    

  connect(peripheralId) {
      var p = null;

      DLog.printDebug(this,'gonna connect now..  ');
      p = new Promise((fulfill, reject) =>
                      {
                        bleAdminRNI.native_connect(peripheralId,
                                                 (success) => {
                                                   
                                                   DLog.printDebug(this,'connection done = ' + success);
                                                 fulfill();
                                                   
                                                 }, (fail) => {
                                                  DLog.printDebug(this,'connection failed = ' + fail);
                                                 reject(fail);
                                                 });
                      

                      });
      

  
      return p;
  }

  disconnect(peripheralId) {
    return new Promise((fulfill, reject) => {
      bleAdminRNI.native_disconnect(peripheralId,(disconnectedPeripheralId) => {
        fulfill(disconnectedPeripheralId);
      }, (fail) => {
        reject(fail);
      });
    });
  }

    getBleRSSI(peripheralId) {
        return new Promise((fulfill, reject) => {
                           bleAdminRNI.native_readBleDevRSSI(peripheralId,
                                                         (success) => {
                                                            fulfill();
                                                         },
                                                         (fail) => {
                                                            reject(fail);
                                                         });
                           });
    }

    
  startNotification(peripheralId, serviceUUID, characteristicUUID) {
    return new Promise((fulfill, reject) => {
      bleAdminRNI.native_startNotification(peripheralId, serviceUUID, characteristicUUID, (success) => {
        fulfill();
      }, (fail) => {
        reject(fail);
      });
    });
  }

   
    startNotification_bytes(peripheralId, serviceUUID, characteristicUUID, characteristicDataType) {
        return new Promise((fulfill, reject) => {
                           bleAdminRNI.native_startNotification_bytearray(peripheralId,
                                                                serviceUUID,
                                                                characteristicUUID,
                                                                characteristicDataType,
                                                                (success) => {
                                                                fulfill();
                                                                }, (fail) => {
                                                                reject(fail);
                                                                });
                           });
    }
    
  stopNotification(peripheralId, serviceUUID, characteristicUUID) {
    return new Promise((fulfill, reject) => {
      bleAdminRNI.native_stopNotification(peripheralId, serviceUUID, characteristicUUID, (success) => {
        fulfill();
      }, (fail) => {
        reject(fail);
      });
    });
  }

  checkState() {
    bleAdminRNI.native_checkState();
  }

  isDeviceConnnected(deviceId) {
      
      return new Promise((fulfill, reject) => {
                         bleAdminRNI.native_IsDeviceConnnected(deviceId,
                                                               (result) => {
                                                               DLog.printDebug(this," native_IsDeviceConnnected result = " + result);
                                                               fulfill(result);
                                                               }, (fail) => {
                                                                DLog.printDebug(this," native_IsDeviceConnnected error = " + fail);
                                                               reject(fail);
                                                                });
                         });
      
    }
    

  scan(serviceUUIDs, seconds) {
    return new Promise((fulfill, reject) => {
      bleAdminRNI.native_scan(serviceUUIDs, seconds,(success) => {
        fulfill();
      });
    });
  }

}


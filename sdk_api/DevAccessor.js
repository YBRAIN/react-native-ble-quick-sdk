//
//  DevAccessor.js
//
//   Created by Eric  .
//
// ---------------------------------
// Common device accessor for all communication channels
// ---------------------------------


'use strict'


import DLog from '../common/DLog';
import { getDevAdminInstance} from './DevAdmin';
import AExecutor from '../common/AsyncExecutorObj';
import { DeviceEvents } from './DevServiceMgr';
import { getUtilsInstance} from '../common/utils/Utils';
import { getBLECommMgrInstance} from '../comm_admins/ble/BLECommMgr';


var instanceDevAccessor = null;
var nDevAccessorObjCnt = 0;

/**
 * @private
 */
export function getDevAccessorInstance()
{
    
    DLog.printDebugMsg("entered getDevAccessorInstance");
    
    if(nDevAccessorObjCnt > 0 )
    {
        DLog.printDebugMsg("It's a singleton class, returning existing instance of DevAccessor class ");
   
        return instanceDevAccessor;
    }
    
    
    nDevAccessorObjCnt++;
    DLog.printDebugMsg("DevAccessor Object Count is " + nDevAccessorObjCnt);
    
    instanceDevAccessor = new DevAccessor();    
    return instanceDevAccessor;
}

/**
 * @private
 * This class provide interface to device access services like connect, disconnect, connection status
 * and other device specific access functions.
 */

class DevAccessor {

    
    constructor(){

        this.deviceScanner = null;
        this.deviceAccessor =  null;
        this.connectedPeripheralID = null;
        

    }

    handleReadEvent(args)
    {

        DLog.printDebug(this,"handleReadEvent");
        
        
        DLog.printDebug(this,'read RSSI for peripheral ' + args.peripheral + ' rssi_value = ' + args.value);
        this.emit(DeviceEvents.kRSSIValue, args.peripheral, args.value);
        
    }
    
    
    getBleDeviceRSSI() {
        
    }

    getConnectedPeripheralID() {
        return this.connectedPeripheralID;
    }
    
    connectToDevice(deviceId, autoReleasableListener) {
        
        DLog.printDebug(this,"   connectToDevice ");
        let objCommMgr = null;
        
        if(autoReleasableListener != undefined)
        {
   
           // this.once(DeviceEvents.kConnected,autoReleasableListener);
            
            let lstnCnt = this.listenerCount(DeviceEvents.kConnected);
            DLog.printDebug(this,"get_volume_level : kConnected listener added, listener count =  " + lstnCnt);
        }
        

        objCommMgr = getBLECommMgrInstance();
        if(objCommMgr == null )
        {
            return AExecutor.throwAsyncException("Exception: BLE channel not available");
        }
        
        var functToExecuteThatReturnsPromise = ()=>{return objCommMgr.connectToDevice(deviceId)};
        
        var onSuccess = ()=>
        {
            DLog.printDebug(this,'Connected to ' + deviceId);
             this.connectedPeripheralID = deviceId;
        }
        
        var onFailure = (error)=>
        {
            DLog.printDebug(this,'connect error = '  + error);
             this.connectedPeripheralID = null;
        }
        
        return AExecutor.runAsyncWithCompletionBlock(functToExecuteThatReturnsPromise,onSuccess,onFailure);
        
        
    }


    
    disconnectDevice(deviceId) {
        let objCommMgr = null;
        DLog.printDebug(this,"   disconnectDevice ");

        objCommMgr = getBLECommMgrInstance();
        if(objCommMgr == null )
        {
            return AExecutor.throwAsyncException("Exception: BLE channel not available");
        }
        var functToExecuteThatReturnsPromise = ()=>{return objCommMgr.disconnectDevice(deviceId)};
        
        var onSuccess = (disconnectedPeripheralId)=>
        {
            DLog.printDebug(this,'disconnected ' + disconnectedPeripheralId);

        }
        
        var onFailure = (error)=>
        {
            DLog.printDebug(this,'disconnect error = '  + error);
 
        }
        
        return AExecutor.runAsyncWithCompletionBlock(functToExecuteThatReturnsPromise,onSuccess,onFailure);
        
    }
    
    
    isDeviceConnnected(deviceId) {
        let objCommMgr = null;
        DLog.printDebug(this,"   isDeviceConnnected ");

        objCommMgr = getBLECommMgrInstance();
        if(objCommMgr == null )
        {
            return AExecutor.throwAsyncException("Exception: BLE channel not available");
        }
        
        var functToExecuteThatReturnsPromise = ()=>{return objCommMgr.isDeviceConnnected(deviceId)};
        
        if(deviceId != null)
        {
            return AExecutor.runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise);
        }
        else
        {
            return AExecutor.throwAsyncException("Exception: There are no connected peripherals");
            
        }
        
    }
    
    getBleDeviceRSSI(deviceId, autoReleasableListener) { //
        let objCommMgr = null;
        DLog.printDebug(this,"   getBleDeviceRSSI ");
        
        if(autoReleasableListener != undefined)
        {
            
             this.once(DeviceEvents.kRSSIValue,autoReleasableListener);
            
            let lstnCnt = this.listenerCount(DeviceEvents.kRSSIValue);
            DLog.printDebug(this,"getBleDeviceRSSI : kRSSIValue listener added, listener count =  " + lstnCnt);
        }
        
        
        objCommMgr = getBLECommMgrInstance();
        if(objCommMgr == null )
        {
            return AExecutor.throwAsyncException("Exception: BLE channel not available");
        }
        
        
        var functToExecuteThatReturnsPromise = ()=>{return objCommMgr.getBleDeviceRSSI(deviceId)};
        
        var onSuccess = (rssi_value)=>
        {
            DLog.printDebug(this,'   getBleDeviceRSSI rssi_value = ' + rssi_value);

        }
        
        var onFailure = (error)=>
        {
            DLog.printDebug(this,'   getBleDeviceRSSI error = '  + error);

        }
        
        return AExecutor.runAsyncWithCompletionBlock(functToExecuteThatReturnsPromise,onSuccess,onFailure);
        
    }
  
    
    
}


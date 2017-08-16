//
//  DevScanner.js
//
//   Created by Eric  .
//
// ---------------------------------
// Common device scanner for all communication channels
// ---------------------------------


'use strict'


import DLog from '../common/DLog';
import { getDevAdminInstance} from './DevAdmin';

import AExecutor from '../common/AsyncExecutorObj';
import { DeviceEvents } from './DevServiceMgr';
import { getUtilsInstance} from '../common/utils/Utils';
import { getBLECommMgrInstance} from '../comm_admins/ble/BLECommMgr';


// consts
const NodeEvents = require('events');



var instanceDevScanner = null;
var nDevScannerObjCnt = 0;
var staticbScanInProgress = false;

/**
 * @private
 */
export function getDevScannerInstance()
{
    
    DLog.printDebugMsg("entered getDevScannerInstance");
    
    if(nDevScannerObjCnt > 0 )
    {
        DLog.printDebugMsg("It's a singleton class, returning existing instance of DevScanner class ");

        return instanceDevScanner;
    }
    
    
    nDevScannerObjCnt++;
    DLog.printDebugMsg("DevScanner Object Count is " + nDevScannerObjCnt);
    
    instanceDevScanner = new DevScanner();
    
    
    
    
    return instanceDevScanner;
}

/**
 * @private
 * This class provide interface to device scanning with mutiple options.
 */
class DevScanner extends NodeEvents {

    
    constructor(){
         super();

    }

    handleReadEvent(args)
    {

        DLog.printDebug(this,"   handleReadEvent");

        DLog.printDebug(this,'    Peripheral found  ' + args.id + '/' + args.name);
        this.emit(DeviceEvents.kDeviceFound, args.id, args.name,args.rssi);
        
    }
    

    scanAllServices(scanTimeOut, listener) {
        
        DLog.printDebug(this,"   startScan")
        staticbScanInProgress = false ;
        let objCommMgr = null;
        
        if(listener != undefined)
        {
            
            let lstnCnt = this.listenerCount(DeviceEvents.kDeviceFound);
            DLog.printDebug(this,"scanAllServices : kDeviceFound listener added, listener count =  " + lstnCnt);
            if(lstnCnt === 0)
            {
                this.on(DeviceEvents.kDeviceFound,listener);
            }
            else
            {
                DLog.printDebug(this,"kDeviceFound multiple listeners for same event not allowed, release the old listener before adding new listener");
                return AExecutor.throwAsyncException("Exception: Multiple listeners not allowed");
            }
        }

        objCommMgr = getBLECommMgrInstance();
        if(objCommMgr == null )
        {
            return AExecutor.throwAsyncException("Exception: BLE channel not available");
        }
        
        var functToExecuteThatReturnsPromise = ()=>{return objCommMgr.scanAllServices(scanTimeOut)};
        
        var onSuccess = ()=>
        {
            DLog.printDebug(this,'   started scanning successfully ');
            staticbScanInProgress = true ;
        }
        
        var onFailure = (error)=>
        {
            DLog.printDebug(this,'   failed to start scan' + error);
            staticbScanInProgress = false ;
        }
        
        
        return AExecutor.runAsyncWithCompletionBlock(functToExecuteThatReturnsPromise,onSuccess,onFailure);
    }
    

    
    
}



//
//  DevAdmin.js
//
//   Created by Eric  .
//
// ---------------------------------
// Common device admin for all communication channels
// ---------------------------------


'use strict'


import DLog from '../common/DLog';
import { getDevAccessorInstance } from './DevAccessor';
import { getDevScannerInstance } from './DevScanner';

var instanceDevAdmin = null;
var nDevAdminObjCnt = 0;

/**
 * @private
 */
export function getDevAdminInstance()
{

    DLog.printDebugMsg("entered getDevAdminInstance");
    
    if(nDevAdminObjCnt > 0 )
    {
        DLog.printDebugMsg("It's a singleton class, returning existing instance of DevAdmin class ");

        return instanceDevAdmin;
    }
    
    
    nDevAdminObjCnt++;
    DLog.printDebugMsg("DevAdmin Object Count is " + nDevAdminObjCnt);
    
    instanceDevAdmin = new DevAdmin();

    
    
    
    return instanceDevAdmin;
}

/**
 * @private
 * This class represents general communication services like scan and access functions
 * depending upon type of the communication channel set during SDK
 * initailization phase.
 * @extends none
 */
class DevAdmin {

    
    constructor(){

        this.nChannelType = null;
        this.deviceScanner = null;
        this.deviceAccessor =  null;
        this.deviceSvcMgr =  null;

    }

    setCommChannel(nChannelType)
    {
        DLog.printDebugMsg("setCommChannel =  " + nChannelType);
        this.nChannelType = nChannelType;
    }

    getCommChannelType()
    {
        return this.nChannelType;
    }
    

    getDeviceScanner()
    {
        return getDevScannerInstance();
    }

    getDeviceAccessor()
    {
        return getDevAccessorInstance();
    }
    

    
    
}


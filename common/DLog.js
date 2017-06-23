//
//  DLog.js
//
//   Created by Eric S .
//

'use strict'

export var DLogCfg =
{
    DEBUG_MODE          : false  // Log will enabled in debug mode
}

export default class DLog  {
    
    constructor(props){

    }
    
    static printDebug(classref, msg)
    {
        if(DLogCfg.DEBUG_MODE == true)
        {

            if(classref !== null)
            {
                console.log('[RNBluetoothLE][' + classref.constructor.name + '.js] ' + msg);
            }
            else
            {
                console.log(' [RNBluetoothLE][Unknown file] ' + msg);
            }
            
        }
        
    }

    static printDebugMsg(msg)
    {

     if(DLogCfg.DEBUG_MODE == true){
                console.log(' [RNBluetoothLE][Unknown file] ' + msg);
     }
        
    }
    
}



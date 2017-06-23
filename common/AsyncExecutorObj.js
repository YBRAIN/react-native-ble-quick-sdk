//
//  AsyncExecutor.js
//
//   Created by Eric S .
//



'use strict'


import DLog from '../common/DLog';

class AsyncExecutor  {

    
    constructor(){


    }

    
    runAsyncWithCompletionBlock(functToExecuteThatReturnsPromise,onSuccessBlock,onFailureBlock)
    {
        return new Promise((fulfill, reject) =>
                           {
                           
                           functToExecuteThatReturnsPromise()
                           .then( () => {
                                 
                                 onSuccessBlock();
                                 fulfill();
                                 }
                                 )
                           .catch ( (error) => {
                                   
                                   onFailureBlock(error);
                                   reject(error);
                                   }
                                   )
                           }
                           );
    }
    
    runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise)
    {
        return new Promise((fulfill, reject) =>
                           {
                           
                           functToExecuteThatReturnsPromise()
                           .then( (result) => {
                                 
                                 fulfill(result);
                                 }
                                 )
                           .catch ( (error) => {
                                   
                                   reject(error);
                                   }
                                   )
                           }
                           );
    }
    
    AsyncRead(objBLECommMgr,connectedPeripheralID, serviceUUID,characteristicUUID)
    {
        var functToExecuteThatReturnsPromise = ()=>
        {
            return objBLECommMgr.readCharacteristic(connectedPeripheralID,serviceUUID, characteristicUUID)
        }
        
        
        if(connectedPeripheralID != null)
        {
            
            return this.runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise);
        }
        else
        {
            return this.throwAsyncException("Exception: There are no connected peripherals");
            
        }
    }
    
    AsyncReadAsByteArray(objBLECommMgr,connectedPeripheralID, serviceUUID,characteristicUUID)
    {
        var functToExecuteThatReturnsPromise = ()=>
        {
            return objBLECommMgr.readCharacteristicsByteArray(connectedPeripheralID,serviceUUID, characteristicUUID, "ByteArray")
        }
        
        
        if(connectedPeripheralID != null)
        {
            
            return this.runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise);
        }
        else
        {
            return this.throwAsyncException("Exception: There are no connected peripherals");
            
        }
    }
    


    AsyncWrite(objBLECommMgr,connectedPeripheralID, serviceUUID,characteristicUUID,valueByteArr)
    {
        var functToExecuteThatReturnsPromise = ()=>
        {
            return objBLECommMgr.writeCharacteristicValueAsByteArray(connectedPeripheralID,serviceUUID, characteristicUUID,valueByteArr);
        }
        
        
        if(connectedPeripheralID != null)
        {
            
            return this.runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise);
        }
        else
        {
            return this.throwAsyncException("Exception: There are no connected peripherals");
            
        }
    }
    
    AsyncIndicate(objBLECommMgr,connectedPeripheralID,serviceUUID,characteristicUUID)
    {
        var functToExecuteThatReturnsPromise = ()=>
        {
            return objBLECommMgr.indicateAckCharacteristic(connectedPeripheralID,serviceUUID, characteristicUUID)
        }
        
        
        if(connectedPeripheralID != null)
        {
            
            return this.runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise);
        }
        else
        {
            return this.throwAsyncException("Exception: There are no connected peripherals");
            
        }
    }
    
    AsyncIndicateAsByteArray(objBLECommMgr,connectedPeripheralID,serviceUUID,characteristicUUID)
    {
        var functToExecuteThatReturnsPromise = ()=>
        {
            return objBLECommMgr.indicateAckCharacteristicByteArray(connectedPeripheralID,serviceUUID, characteristicUUID, "ByteArray")
        }
        
        
        if(connectedPeripheralID != null)
        {
            
            return this.runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise);
        }
        else
        {
            return this.throwAsyncException("Exception: There are no connected peripherals");
            
        }
    }
    
    AsyncIndicateStop(objBLECommMgr,connectedPeripheralID,serviceUUID,characteristicUUID)
    {
        var functToExecuteThatReturnsPromise = ()=>
        {
            return objBLECommMgr.stopIndicateAckCharacteristic(connectedPeripheralID,serviceUUID, characteristicUUID)
        }
        
        
        if(connectedPeripheralID != null)
        {
            
            return this.runAsyncWithoutCompletionBlock(functToExecuteThatReturnsPromise);
        }
        else
        {
            return this.throwAsyncException("Exception: There are no connected peripherals");
            
        }
    }
    
    
    throwAsyncException(exceptionMsg)
    {
        return new Promise((fulfill, reject) =>
                           {
                           DLog.printDebug(this,exceptionMsg);
                           reject(exceptionMsg);
                           }
                           );
    }
    
}

module.exports = new AsyncExecutor();


//
//  Utils.js
//
//   Created by Eric S .
//

'use strict'

import DLog from '../DLog';



var nUtilsObjCnt = 0;
var instanceUtils = null;

export var E_TdcsState = {
    
    INIT                :0, //Initial state
    INACTIVE            :1, //Not activated yet.
    ACTIVE              :2, //Active. Can monitor VC state
    IDLE                :2, //TDCSSTATE_ACTIVE + 0, //Idle. Can monitor VC state (Same with Active)
    READY               :3, //TDCSSTATE_ACTIVE + 1, // Patch is connected. Can startStimulation stimulation.
    
    //The followings are under stimulating state
    
    RAMP_UP             :4, //TDCSSTATE_ACTIVE + 2, //Progressing ramp up sequence.
    STIMULATING         :5, //(TDCSSTATE_ACTIVE + 3), //Progressing stimulation sequence.
    RAMP_DOWN           :6, //(TDCSSTATE_ACTIVE + 4); //Progressing ramp down sequence.
    DEACTIVATING        :7,
    EMERGENCY_RAMP_DOWN :8, //Progressing emergency ramp down sequence.
    NUM_STATES          :9,
    INVALID_STATE       :0x7F
    
}

// YEpoch:Ybrain tdcs dev records rtc values since 00Hrs:00min:00sec Jan 01,2000 in secs
//YEpoch time - unix time i.e  (00Hrs:00min:00sec Jan 01,2000) - (00Hrs:00min:00sec Jan 1970)
export const TDCS_DEV_YEPOCH_STARTTIME_IN_SECS = 946684800;

export function getUtilsInstance()
{
    
    if(nUtilsObjCnt > 0 )
    {
        DLog.printDebugMsg("It's a singleton class, returning existing instance of Utils class ");
  
        return instanceUtils;
    }
    
    nUtilsObjCnt++;
    DLog.printDebugMsg("Utils Object Count is " + nUtilsObjCnt);
    

    instanceUtils = new Utils();
    

    return instanceUtils;
    
}



class Utils  {
    
    constructor(props){



    }
    convertNumbers2ByteArrayWithDataType(dataType, number)
    {
        var dataTypeSize = 4;

        var byteArray = [];
        var dataview = null;
        if(dataType === 'uint_8')
        {
            dataTypeSize = 1; // 1 Byte
            
            
            var objUint8Array   = new Uint8Array(new ArrayBuffer(dataTypeSize)); //
             dataview = new DataView(objUint8Array.buffer);
            
            dataview.setInt8(0, number, true);
        }
        else if(dataType === 'uint_16') // short
        {
            dataTypeSize = 2; // 2 bytes
            
            var objUint8Array   = new Uint8Array(new ArrayBuffer(dataTypeSize)); //
             dataview = new DataView(objUint8Array.buffer);
            
            dataview.setInt16(0, number, true);
        }
        else if(dataType === 'uint_32')
        {
            dataTypeSize = 4;
            
            
            var objUint8Array   = new Uint8Array(new ArrayBuffer(dataTypeSize)); //
             dataview = new DataView(objUint8Array.buffer);
            
            dataview.setInt32(0, number, true);
        }
        else if(dataType === 'uint_64')
        {
            dataTypeSize = 8;
            
            
            var objUint8Array   = new Uint8Array(new ArrayBuffer(dataTypeSize)); //
            dataview = new DataView(objUint8Array.buffer);
            
            dataview.setFloat64(0, number, true);
        }
       
        
        
        for(var m=0;m< dataTypeSize ;m++) // requiredLen
        {

            var byte = dataview.getInt8(m);
            byteArray.push(byte);
            
            console.log('convertNumbers2ByteArrayWithDataType byte value at ' + m + ' pos is = ' + byteArray[m]);
        }
        
        return byteArray;
        
        
    }
    
    calcMinNoOfBytesReq2StoreNumber(number)
    {
        
        var a = Math.log(number)/Math.log(2);
        var b = Math.ceil(a);
        var c = b/8;
        var requiredLen = Math.ceil(c);
        
        console.log('Maths: a = ' + a + ' b = ' + b  + ' c = ' + c + ' requiredLen = ' + requiredLen  );
        
        return requiredLen;
    }

    convert8BitInt2ByteArray(int8Value)
    {
        return this.convertNumbers2ByteArrayWithDataType('uint_8',int8Value);
        
    }
    convertShort2ByteArray(shortValue)
    {
        return this.convertNumbers2ByteArrayWithDataType('uint_16',shortValue);
        
    }
    convert32BitInt2ByteArray(int32Value)
    {
        return this.convertNumbers2ByteArrayWithDataType('uint_32',int32Value);
        
    }
    
    convert64BitInt2ByteArray(int64Value)
    {
        return this.convertNumbers2ByteArrayWithDataType('uint_64',int64Value);
        
    }
    
    convertNumberStr2ByteArray(numberStr)
    {
 
        var byteArray = [];
        var numberStrArr = numberStr.split("");
        
        var objUint8Array   = new Uint8Array(new ArrayBuffer(numberStrArr.length));
        var dataview = new DataView(objUint8Array.buffer);
        

        
        for(var m=0;m< numberStrArr.length;m++)
        {
             console.log('convertInt2ByteArray splited value at ' + m + ' pos is = ' + numberStrArr[m]);
            
            dataview.setInt8(m, numberStrArr[m], true);
            var byte = dataview.getInt8(m);
            byteArray.push(byte);
            
            console.log('convertInt2ByteArray byte value at ' + m + ' pos is = ' + byteArray[m]);
        }
        
        return byteArray;
    }
    
    getAsciiVal(j)
    {
        var k = 0;
        var hex_arr = ['a','b','c','d','e','f'];
        
        if(j >= '0' && j <='9' )
        {
            k = 0 + Math.round(j);
            k = 48 + k;
        }
        else
        {
            for(var m=0;m< hex_arr.length;m++)
            {
                if(j === hex_arr[m])
                {
                      k = 65 + m;
                }
            }
        }
        return k;
    }
    
    convertHexStr2AsciiByteArray(numberStr)
    {
 
        var byteArray = [];
        var numberStrArr = numberStr.split("");
        
        var objUint8Array   = new Uint8Array(new ArrayBuffer(numberStrArr.length));
        var dataview = new DataView(objUint8Array.buffer);

        var len = numberStrArr.length;
        byteArray.push(len);
        console.log('convertHexStr2AsciiByteArray byte value at ' + 0 + ' pos is = ' + byteArray[0]);
        for(var m=0;m< numberStrArr.length;m++)
        {

            var j = numberStrArr[m];
            j = this.getAsciiVal(j);
            
            dataview.setUint8(m, j, true);
            var byte = dataview.getInt8(m);
            byteArray.push(byte);
            
            console.log('convertHexStr2AsciiByteArray byte value at ' + (m+1) + ' pos is = ' + byteArray[m+1]);
        }
        
        return byteArray;
    }
    
    convertTDCSState2String(E_TdcsState, tdcs_state_val)
    {
        
        switch (tdcs_state_val) {
            case E_TdcsState.INIT:
                return 'INIT';
            case E_TdcsState.INACTIVE:
                return 'INACTIVE';
            case E_TdcsState.IDLE:
                return 'IDLE';
            case E_TdcsState.READY :
                return 'READY';
            case E_TdcsState.RAMP_UP :
                return 'RAMP_UP';
            case E_TdcsState.STIMULATING :
                return 'STIMULATING';
            case E_TdcsState.RAMP_DOWN :
                return 'RAMP_DOWN';
            case E_TdcsState.DEACTIVATING :
                return 'DEACTIVATING';
            case E_TdcsState.EMERGENCY_RAMP_DOWN :
                return 'EMERGENCY_RAMP_DOWN';
            default:
                return 'INVALID_STATE';
        }
        
        return 'INVALID_STATE';
    }
    
    
}



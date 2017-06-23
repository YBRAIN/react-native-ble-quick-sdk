//
// CommAdminBleRNI.m
// BLE Central for BDS Plugin
//
// Copyright (c) 2017 Eric S
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#import "CommAdminBleRNI.h"

#import "RCTConvert.h"
#import "RCTEventDispatcher.h"


@interface CommAdminBleRNI ()

@end

@implementation CommAdminBleRNI

RCT_EXPORT_MODULE();
@synthesize bridge = _bridge;


- (instancetype)init
{
    
    if (self = [super init]) {
        NSLog(@"init ");
        mBleAdmin = [[ BleAdmin  alloc] init];

    }
    
    [self performSelector:@selector(didBleAdminLoad) withObject:nil afterDelay:0.1];
    
    return self;
}

-(void) didBleAdminLoad
{
    if(self.bridge != nil)
    {
        mBleAdmin.bleAdminRNIEventBridge = self.bridge;
        NSLog(@" Native lib loaded sucessfully ");
        [self.bridge.eventDispatcher sendAppEventWithName:@"NativeLibLoad" body:@{@"status":@"1"} ];
    }
    else
    {
        NSLog(@" Native lib loading failed  ");
        [self.bridge.eventDispatcher sendAppEventWithName:@"NativeLibLoad" body:@{@"status":@"0"}];
    }
    
}


// Exported functions for React Native JS
RCT_EXPORT_METHOD(native_scan:(NSArray *)serviceUUIDStrings timeoutSeconds:(nonnull NSNumber *)timeoutSeconds callback:(nonnull RCTResponseSenderBlock)successCallback)
{
    NSLog(@"native_scan entered");
 
    [mBleAdmin scan:serviceUUIDStrings timeoutSeconds:timeoutSeconds callback: successCallback];

}


RCT_EXPORT_METHOD(native_connect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_connect entered");
    
    [mBleAdmin connect:peripheralUUID  successCallback: successCallback failCallback: failCallback];
    
}

RCT_EXPORT_METHOD(native_disconnect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_disconnect entered");
    
    [mBleAdmin disconnect:peripheralUUID  successCallback: successCallback failCallback: failCallback];
    
}



RCT_EXPORT_METHOD(native_readBleDevRSSI : (NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_readBleDevRSSI entered");
    
    [mBleAdmin readRSSIOfConnectedBleDev:peripheralUUID successCallback:successCallback failCallback:failCallback];
    
}


RCT_EXPORT_METHOD(native_checkState)
{
    NSLog(@"native_checkState entered");
    
    [mBleAdmin checkState];
    
}

 // BLE connection status.
//RCT_EXPORT_METHOD(native_IsDeviceConnnected:(NSString *)deviceUUID resultCallback:(nonnull RCTResponseSenderBlock)resultCallback )
RCT_EXPORT_METHOD(native_IsDeviceConnnected:(NSString *)deviceUUID resultCallback:(nonnull RCTResponseSenderBlock)resultCallback  failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_IsDeviceConnnected entered");
    
    [mBleAdmin checkPeripheralState:deviceUUID resultCallback:resultCallback failCallback:failCallback ];
    
}

RCT_EXPORT_METHOD(native_write_string:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_write_string entered");
    
    [mBleAdmin writeAsString:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID message:message successCallback: successCallback failCallback: failCallback];
    
}



RCT_EXPORT_METHOD(native_write_bytes:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID   msgByteBuf:(nonnull NSArray*)msgByteBuf successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_write_bytes entered");
  

    [mBleAdmin writeAsBytes:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID msgByteBuf:msgByteBuf            successCallback: successCallback failCallback: failCallback];
    
}

RCT_EXPORT_METHOD(native_writeWithoutResponse:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_writeWithoutResponse entered");
    
    [mBleAdmin writeWithoutResponse:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID message:message successCallback: successCallback failCallback: failCallback];
    
}


RCT_EXPORT_METHOD(native_read:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_read entered");
    
    [mBleAdmin read:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID characteristicDataType:@"UNKNOWN" successCallback: successCallback failCallback: failCallback];
    
}

RCT_EXPORT_METHOD(native_read_bytearray:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_read_bytearray entered");
    
    [mBleAdmin readAsByteArray:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID characteristicDataType:characteristicDataType successCallback: successCallback failCallback: failCallback];
    
}


RCT_EXPORT_METHOD(native_startNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_startNotification entered");
    
    [mBleAdmin startNotification:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID characteristicDataType:@"UNKNOWN" successCallback: successCallback failCallback: failCallback];
    
}

RCT_EXPORT_METHOD(native_startNotification_bytearray:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_startNotification entered");
    
    [mBleAdmin startNotificationAsByteArray:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID characteristicDataType:characteristicDataType successCallback: successCallback failCallback: failCallback];
    
}
    
RCT_EXPORT_METHOD(native_stopNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback)
{
    NSLog(@"native_stopNotification entered");
    
    [mBleAdmin stopNotification:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID successCallback: successCallback failCallback: failCallback];
    
}


@end

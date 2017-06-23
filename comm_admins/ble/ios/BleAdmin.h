//
// BleAdmin.h
// BLE Central BDS Plugin
//
// Original copyright (c) 2104 Don Coleman
// Borrowed from innoveit/react-native-ble-manager
// Modifications copyright (c) 2017 Eric S
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


#import <RCTBridgeModule.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "NSYLog.h"
#import "Utils.h"




@interface BleAdmin : NSObject < CBCentralManagerDelegate, CBPeripheralDelegate>{
    NSString* discoverPeripherialCallbackId;
    NSMutableDictionary* connectCallbacks;
    NSMutableDictionary* disconnectCallbacks;
    NSMutableDictionary *readCallbacks;
    NSMutableDictionary *writeCallbacks;
    NSMutableDictionary *notificationCallbacks;
    NSMutableDictionary *stopNotificationCallbacks;
    NSMutableDictionary *connectCallbackLatches;
    NSMutableDictionary *readRSSICallbacks;
    NSMutableArray *writeQueue;

}

@property (strong, nonatomic) NSMutableSet *peripherals;
@property (strong, nonatomic) CBCentralManager *manager;
@property (strong, nonatomic) RCTBridge* bleAdminRNIEventBridge;



-(void) scan:(NSArray *)serviceUUIDStrings timeoutSeconds:(nonnull NSNumber *)timeoutSeconds callback:(nonnull RCTResponseSenderBlock)successCallback;

-(void) connect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void)disconnect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void)readRSSIOfConnectedBleDev:(NSString *) peripheralUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void)checkState;

-(void)checkPeripheralState:(NSString *)deviceUUID resultCallback:(nonnull RCTResponseSenderBlock)resultCallback failCallback:failCallback; // BLE connection status.

-(void) writeAsString:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void) writeAsBytes:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID
          msgByteBuf:(NSArray*)msgByteBuf successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void) writeWithoutResponse:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void) read:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void) readAsByteArray:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;
    
-(void)startNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void)startNotificationAsByteArray:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

-(void)stopNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback;

@end

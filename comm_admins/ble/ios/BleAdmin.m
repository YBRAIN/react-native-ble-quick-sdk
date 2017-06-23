//
// BleAdmin.m
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

#import "BleAdmin.h"
//#import "RCTBridge.h"
#import "RCTConvert.h"
#import "RCTEventDispatcher.h"
#import "CBPeripheral+Extensions.h"
#import "BLECommandContext.h"
#import "CBCharacteristic+Extensions.h"




@implementation BleAdmin

@synthesize manager;
@synthesize peripherals;
@synthesize bleAdminRNIEventBridge;

- (instancetype)init
{
    
    if (self = [super init]) {
        NSLog(@"BleAdmin init");

        peripherals = [NSMutableSet set];
        manager = [[CBCentralManager alloc] initWithDelegate:self queue:dispatch_get_main_queue()];
        
        connectCallbacks = [NSMutableDictionary new];
        disconnectCallbacks = [NSMutableDictionary new];
        connectCallbackLatches = [NSMutableDictionary new];
        readCallbacks = [NSMutableDictionary new];
        writeCallbacks = [NSMutableDictionary new];
        writeQueue = [NSMutableArray array];
        notificationCallbacks = [NSMutableDictionary new];
        stopNotificationCallbacks = [NSMutableDictionary new];

    }
    
    return self;
}



#pragma mark  : For React Native Interface - scan/connect/disconnect/checkstate/write/read/notify
-(void) scan:(NSArray *)serviceUUIDStrings timeoutSeconds:(nonnull NSNumber *)timeoutSeconds callback:(nonnull RCTResponseSenderBlock)successCallback
{
    NSLog(@"scan with timeout %@", timeoutSeconds);
    NSArray * services = [RCTConvert NSArray:serviceUUIDStrings];
    NSMutableArray *serviceUUIDs = [NSMutableArray new];
    

    for (int i = 0; i < [services count]; i++) {
        CBUUID *serviceUUID =[CBUUID UUIDWithString:[serviceUUIDStrings objectAtIndex: i]];
        [serviceUUIDs addObject:serviceUUID];
    }
    [manager scanForPeripheralsWithServices:serviceUUIDs options:nil];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [NSTimer scheduledTimerWithTimeInterval:[timeoutSeconds floatValue] target:self selector:@selector(stopScanTimer:) userInfo: nil repeats:NO];
    });
    successCallback(@[]);
}


-(void)stopScanTimer:(NSTimer *)timer {
    NSLog(@"Stop scan");
    [manager stopScan];
    [NSThread sleepForTimeInterval:2];
    [self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminStopScan" body:@{}];
}

-(void)readRSSIOfConnectedBleDev:(NSString *) peripheralUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    NSLog(@"readRSSIOfConnectedBleDev");
    CBPeripheral *peripheral = [self findPeripheralByUUID:peripheralUUID];
    
    if (peripheral)
    {
        [peripheral readRSSI];
    }
    else
    {
        NSString *error = [NSString stringWithFormat:@"Could not find peripheral %@.", peripheralUUID];
        NSLog(@"%@", error);
        failCallback(@[error]);
    }
    
}


-(void) connect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    NSLog(@"connect");

    CBPeripheral *peripheral = [self findPeripheralByUUID:peripheralUUID];
    if (peripheral  ) {

        if(peripheral.state != CBPeripheralStateConnected){
        
            [connectCallbacks setObject:successCallback forKey:[peripheral uuidAsString]];
            
            NSLog(@"Connecting to peripheral with UUID : %@, connectCallbacks: %@", peripheralUUID, connectCallbacks);
            
            [manager connectPeripheral:peripheral options:nil];
        }
        else
        {
            NSLog(@"Peripheral already connected,  UUID : %@ ", peripheralUUID);
        }
        
    } else {
        NSString *error = [NSString stringWithFormat:@"Could not find peripheral %@.", peripheralUUID];
        NSLog(@"%@", error);
        failCallback(@[error]);
    }
}

-(void)disconnect:(NSString *)peripheralUUID  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    CBPeripheral *peripheral = [self findPeripheralByUUID:peripheralUUID];
    if (peripheral) {
        NSLog(@"Disconnecting from peripheral with UUID : %@", peripheralUUID);
        
        if (peripheral.services != nil) {
            for (CBService *service in peripheral.services) {
                if (service.characteristics != nil) {
                    for (CBCharacteristic *characteristic in service.characteristics) {
                        if (characteristic.isNotifying) {
                            NSLog(@"Remove notification from: %@", characteristic.UUID);
                            [peripheral setNotifyValue:NO forCharacteristic:characteristic];
                        }
                    }
                }
            }
        }
        [disconnectCallbacks setObject:successCallback forKey:[peripheral uuidAsString]]; //  
        [manager cancelPeripheralConnection:peripheral];
        //successCallback(@[]); //  
        
    } else {
        NSString *error = [NSString stringWithFormat:@"Could not find peripheral %@.", peripheralUUID];
        NSLog(@"%@", error);
        failCallback(@[error]);
    }
}


-(void)checkState
{
    if (manager != nil){
        [self centralManagerDidUpdateState:self.manager];
    }
}


-(void)checkPeripheralState:(NSString *)deviceUUID resultCallback:(nonnull RCTResponseSenderBlock)resultCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    if (manager != nil){
        CBPeripheral *peripheral = [self findPeripheralByUUID:deviceUUID];
       
        if(peripheral != nil)
        {
        NSString *peripheralStateName = [self peripheralStateToBoolStr: peripheral.state];
        resultCallback(@[peripheralStateName]);
        }
        else
        {
            NSString *error = [NSString stringWithFormat:@"Could not find peripherial with UUID %@", deviceUUID];
            NSLog(@"%@", error);
            failCallback(@[error]);
        }
    }
    else
    {
        NSString *error = [NSString stringWithFormat:@"Internal error"];
        NSLog(@"%@", error);
        failCallback(@[error]);
    }
}


-(void) writeAsBytes:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID
      msgByteBuf:(NSArray*)msgByteBuf successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{

    int msgLen = [msgByteBuf count];
    unsigned char *pByteBuf = malloc((int)msgLen);

    for (int m= 0; m < msgLen; m++) {
        pByteBuf[m] = [msgByteBuf[m] charValue];
        //NSLog(@"%d ", pByteBuf[m]);
    }
    
    NSData* dataMessage = [NSData dataWithBytes:pByteBuf length:msgLen]; //  
    
    [self writeAsNSData:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID dataMessage:dataMessage successCallback:successCallback failCallback:failCallback];
    

    if(pByteBuf != NULL)
    {
        free(pByteBuf);
        pByteBuf = NULL;
    }

    
}

-(void) writeAsString:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{


    NSData *messageDataUTF8 = [message dataUsingEncoding:NSUTF8StringEncoding];
    NSString *messageStrb64 = [messageDataUTF8 base64EncodedStringWithOptions:kNilOptions];
 
    NSData* dataMessage = [[NSData alloc] initWithBase64EncodedString:messageStrb64 options:0];
    
    [self writeAsNSData:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID dataMessage:dataMessage successCallback:successCallback failCallback:failCallback];
    
}


-(void) writeAsNSData:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID
          dataMessage:(NSData*)dataMessage successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    
    BLECommandContext *context = [self getData:deviceUUID serviceUUIDString:serviceUUID characteristicUUIDString:characteristicUUID prop:CBCharacteristicPropertyWrite failCallback:failCallback];
    
    if (context) {
        CBPeripheral *peripheral = [context peripheral];
        CBCharacteristic *characteristic = [context characteristic];
        
        NSString *key = [self keyForPeripheral: peripheral andCharacteristic:characteristic];
        [writeCallbacks setObject:successCallback forKey:key];
        
        //NSLog(@"Message originale(%lu): %@ ", (unsigned long)[dataMessage length], [Utils stringFromHex:dataMessage]);
        
        RCTLogInfo(@"Message to write(%lu): %@ ", (unsigned long)[dataMessage length], [Utils stringFromHex:dataMessage]);
        
        if ([dataMessage length] > 20){
            int dataLength = (int)dataMessage.length;
            int count = 0;
            NSData* firstMessage;
            while(count < dataLength && (dataLength - count > 20)){
                if (count == 0){
                    firstMessage = [dataMessage subdataWithRange:NSMakeRange(count, 20)];
                }else{
                    NSData* splitMessage = [dataMessage subdataWithRange:NSMakeRange(count, 20)];
                    [writeQueue addObject:splitMessage];
                }
                count += 20;
            }
            if (count < dataLength) {
                NSData* splitMessage = [dataMessage subdataWithRange:NSMakeRange(count, dataLength - count)];
                [writeQueue addObject:splitMessage];
            }
            NSLog(@"Queued splitted message: %lu", (unsigned long)[writeQueue count]);
            [peripheral writeValue:firstMessage forCharacteristic:characteristic type:CBCharacteristicWriteWithResponse];
        } else {
            
            
            [peripheral writeValue:dataMessage forCharacteristic:characteristic type:CBCharacteristicWriteWithResponse];
        }
    }
    else
    {
        
        NSLog(@"writeAsNSData blecontext is null");
       // failCallback(@[@"Error"]);
    }
}


-(void) writeWithoutResponse:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID message:(NSString*)message  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    NSLog(@"writeWithoutResponse");
    
    BLECommandContext *context = [self getData:deviceUUID serviceUUIDString:serviceUUID characteristicUUIDString:characteristicUUID prop:CBCharacteristicPropertyWriteWithoutResponse failCallback:failCallback];
    
    NSData* dataMessage = [[NSData alloc] initWithBase64EncodedString:message options:0];
    if (context) {
        CBPeripheral *peripheral = [context peripheral];
        CBCharacteristic *characteristic = [context characteristic];
        
        NSLog(@"Message to write(%lu): %@ ", (unsigned long)[dataMessage length], [Utils stringFromHex:dataMessage]);
        
        // TODO need to check the max length
        [peripheral writeValue:dataMessage forCharacteristic:characteristic type:CBCharacteristicWriteWithoutResponse];
        successCallback(@[]);
    }
    else
    {
        
        NSLog(@"writeAsNSData blecontext is null");
        // failCallback(@[@"Error"]);
    }
}



-(void) read:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    NSLog(@"read");
    
    BLECommandContext *context = [self getData:deviceUUID serviceUUIDString:serviceUUID characteristicUUIDString:characteristicUUID prop:CBCharacteristicPropertyRead failCallback:failCallback];
    if (context) {
        
        CBPeripheral *peripheral = [context peripheral];
        CBCharacteristic *characteristic = [context characteristic];
        characteristic.characteristicDataType = characteristicDataType;
        NSString *key = [self keyForPeripheral: peripheral andCharacteristic:characteristic];
        
        [peripheral readValueForCharacteristic:characteristic];
        
        successCallback(@[@""]);
    }

    
}

-(void) readAsByteArray:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    
    NSLog(@"readAsByteArray");
    
    [self read:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID characteristicDataType:characteristicDataType successCallback:successCallback failCallback:failCallback];
    

}
-(void)startNotificationAsByteArray:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    NSLog(@"startNotificationAsByteArray");
    
    [self startNotification:deviceUUID serviceUUID:serviceUUID characteristicUUID:characteristicUUID characteristicDataType:characteristicDataType successCallback:successCallback failCallback:failCallback];
}
    
-(void)startNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID characteristicDataType:(NSString*)characteristicDataType  successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    NSLog(@"startNotification");

    BLECommandContext *context = [self getData:deviceUUID serviceUUIDString:serviceUUID characteristicUUIDString:characteristicUUID prop:CBCharacteristicPropertyNotify failCallback:failCallback];
    
    if (context) {
        CBPeripheral *peripheral = [context peripheral];
        CBCharacteristic *characteristic = [context characteristic];
        characteristic.characteristicDataType = characteristicDataType;
        NSString *key = [self keyForPeripheral: peripheral andCharacteristic:characteristic];
        [notificationCallbacks setObject: successCallback forKey: key];
        
        [peripheral setNotifyValue:YES forCharacteristic:characteristic];

        
    }
    
}


-(void)stopNotification:(NSString *)deviceUUID serviceUUID:(NSString*)serviceUUID  characteristicUUID:(NSString*)characteristicUUID successCallback:(nonnull RCTResponseSenderBlock)successCallback failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    NSLog(@"stopNotification");
    
    BLECommandContext *context = [self getData:deviceUUID serviceUUIDString:serviceUUID characteristicUUIDString:characteristicUUID prop:CBCharacteristicPropertyNotify failCallback:failCallback];
    
    if (context) {
        CBPeripheral *peripheral = [context peripheral];
        CBCharacteristic *characteristic = [context characteristic];
        
        NSString *key = [self keyForPeripheral: peripheral andCharacteristic:characteristic];
        [stopNotificationCallbacks setObject: successCallback forKey: key];
        
        if ([characteristic isNotifying]){
            [peripheral setNotifyValue:NO forCharacteristic:characteristic];
            NSLog(@"Characteristic stopped notifying");
        } else {
            NSLog(@"Characteristic is not notifying");
        }
        
    }
    
}

#pragma mark  : Functions for internal use
- (NSString *) centralManagerStateToString: (int)state
{
    switch (state) {
        case CBCentralManagerStateUnknown:
            return @"unknown";
        case CBCentralManagerStateResetting:
            return @"resetting";
        case CBCentralManagerStateUnsupported:
            return @"unsupported";
        case CBCentralManagerStateUnauthorized:
            return @"unauthorized";
        case CBCentralManagerStatePoweredOff:
            return @"off";
        case CBCentralManagerStatePoweredOn:
            return @"on";
        default:
            return @"unknown";
    }
    
    return @"unknown";
}

- (NSString *) peripheralStateToString: (int)state
{
    switch (state) {
        case CBPeripheralStateDisconnected:
            return @"disconnected";
        case CBPeripheralStateDisconnecting:
            return @"disconnecting";
        case CBPeripheralStateConnected:
            return @"connected";
        case CBPeripheralStateConnecting:
            return @"connecting";
        default:
            return @"unknown";
    }
    
    return @"unknown";
}

- (NSString *) peripheralStateToBoolStr: (int)state
{
    switch (state) {
        case CBPeripheralStateDisconnected:
            return @"0";
        case CBPeripheralStateDisconnecting:
            return @"0";
        case CBPeripheralStateConnected:
            return @"1";
        case CBPeripheralStateConnecting:
            return @"0";
        default:
            return @"-1"; // unknown error
    }
    
    return @"unknown";
}

- (NSString *) periphalManagerStateToString: (int)state
{
    switch (state) {
        case CBPeripheralManagerStateUnknown:
            return @"Unknown";
        case CBPeripheralManagerStatePoweredOn:
            return @"PoweredOn";
        case CBPeripheralManagerStatePoweredOff:
            return @"PoweredOff";
        default:
            return @"unknown";
    }
    
    return @"unknown";
}

- (CBPeripheral*)findPeripheralByUUID:(NSString*)uuid {
    
    CBPeripheral *peripheral = nil;
    
    for (CBPeripheral *p in peripherals) {
        
        NSString* other = p.identifier.UUIDString;
        
        if ([uuid isEqualToString:other]) {
            peripheral = p;
            break;
        }
    }
    return peripheral;
}

-(CBService *) findServiceFromUUID:(CBUUID *)UUID p:(CBPeripheral *)p
{
    for(int i = 0; i < p.services.count; i++)
    {
        CBService *s = [p.services objectAtIndex:i];
        if ([self compareCBUUID:s.UUID UUID2:UUID])
            return s;
    }
    
    return nil; //Service not found on this peripheral
}

-(int) compareCBUUID:(CBUUID *) UUID1 UUID2:(CBUUID *)UUID2
{
    char b1[16];
    char b2[16];
    [UUID1.data getBytes:b1 length:16];
    [UUID2.data getBytes:b2 length:16];
    
    if (memcmp(b1, b2, UUID1.data.length) == 0)
        return 1;
    else
        return 0;
}

// Find a characteristic in service with a specific property
-(CBCharacteristic *) findCharacteristicFromUUID:(CBUUID *)UUID service:(CBService*)service prop:(CBCharacteristicProperties)prop
{
    NSLog(@"Looking for %@ with properties %lu", UUID, (unsigned long)prop);
    for(int i=0; i < service.characteristics.count; i++)
    {
        CBCharacteristic *c = [service.characteristics objectAtIndex:i];
        if ((c.properties & prop) != 0x0 && [c.UUID.UUIDString isEqualToString: UUID.UUIDString]) {
            NSLog(@"Found %@", UUID);
            return c;
        }
    }
    return nil; //Characteristic with prop not found on this service
}

// Find a characteristic in service by UUID
-(CBCharacteristic *) findCharacteristicFromUUID:(CBUUID *)UUID service:(CBService*)service
{
    NSLog(@"Looking for %@", UUID);
    for(int i=0; i < service.characteristics.count; i++)
    {
        CBCharacteristic *c = [service.characteristics objectAtIndex:i];
        if ([c.UUID.UUIDString isEqualToString: UUID.UUIDString]) {
            NSLog(@"Found %@", UUID);
            return c;
        }
    }
    return nil; //Characteristic not found on this service
}


// expecting deviceUUID, serviceUUID, characteristicUUID in command.arguments
-(BLECommandContext*) getData:(NSString*)deviceUUIDString  serviceUUIDString:(NSString*)serviceUUIDString characteristicUUIDString:(NSString*)characteristicUUIDString prop:(CBCharacteristicProperties)prop failCallback:(nonnull RCTResponseSenderBlock)failCallback
{
    CBUUID *serviceUUID = [CBUUID UUIDWithString:serviceUUIDString];
    CBUUID *characteristicUUID = [CBUUID UUIDWithString:characteristicUUIDString];
    
    CBPeripheral *peripheral = [self findPeripheralByUUID:deviceUUIDString];
    
    if (!peripheral) {
        NSString* err = [NSString stringWithFormat:@"Could not find peripherial with UUID %@", deviceUUIDString];
        NSLog(@"Could not find peripherial with UUID %@", deviceUUIDString);
        failCallback(@[err]);
        
        return nil;
    }
    
    CBService *service = [self findServiceFromUUID:serviceUUID p:peripheral];
    
    if (!service)
    {
        NSString* err = [NSString stringWithFormat:@"Could not find service with UUID %@ on peripheral with UUID %@",
                         serviceUUIDString,
                         peripheral.identifier.UUIDString];
        NSLog(@"Could not find service with UUID %@ on peripheral with UUID %@",
              serviceUUIDString,
              peripheral.identifier.UUIDString);
        failCallback(@[err]);
        return nil;
    }
    
    CBCharacteristic *characteristic = [self findCharacteristicFromUUID:characteristicUUID service:service prop:prop];
    
    // Special handling for INDICATE. If charateristic with notify is not found, check for indicate.
    if (prop == CBCharacteristicPropertyNotify && !characteristic) {
        characteristic = [self findCharacteristicFromUUID:characteristicUUID service:service prop:CBCharacteristicPropertyIndicate];
    }
    
    // As a last resort, try and find ANY characteristic with this UUID, even if it doesn't have the correct properties
    if (!characteristic) {
        characteristic = [self findCharacteristicFromUUID:characteristicUUID service:service];
    }
    
    if (!characteristic)
    {
        NSString* err = [NSString stringWithFormat:@"Could not find characteristic with UUID %@ on service with UUID %@ on peripheral with UUID %@", characteristicUUIDString,serviceUUIDString, peripheral.identifier.UUIDString];
        NSLog(@"Could not find characteristic with UUID %@ on service with UUID %@ on peripheral with UUID %@",
              characteristicUUIDString,
              serviceUUIDString,
              peripheral.identifier.UUIDString);
        failCallback(@[err]);
        return nil;
    }
    
    BLECommandContext *context = [[BLECommandContext alloc] init];
    [context setPeripheral:peripheral];
    [context setService:service];
    [context setCharacteristic:characteristic];
    return context;
    
}

-(NSString *) keyForPeripheral: (CBPeripheral *)peripheral andCharacteristic:(CBCharacteristic *)characteristic {
    return [NSString stringWithFormat:@"%@|%@", [peripheral uuidAsString], [characteristic UUID]];
}

#pragma mark   1:  Callbacks for scanning PS and for related discoveries
- (void)centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)peripheral
     advertisementData:(NSDictionary *)advertisementData
                  RSSI:(NSNumber *)RSSI
{
    [peripherals addObject:peripheral];
    [peripheral setAdvertisementData:advertisementData RSSI:RSSI];
    
    NSLog(@"Discover peripheral: %@, peripheral count = %d, rssi= %d, advertisementData = %@ ", [peripheral name], peripherals.count,[RSSI intValue], advertisementData);
    [self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDiscoverPeripheral" body:[peripheral asDictionary]];

}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error {
    if (error) {
        NSLog(@"Error: %@", error);
        return;
    }
    //NSLog(@"  Services Discover");
    
    NSMutableSet *servicesForPeriperal = [NSMutableSet new];
    [servicesForPeriperal addObjectsFromArray:peripheral.services];
    [connectCallbackLatches setObject:servicesForPeriperal forKey:[peripheral uuidAsString]];

    //NSLog(@"peripheral services discovered - %@", peripheral.services);
    
    for (CBService *service in peripheral.services) {
        
        //NSLog(@"  Service UUID is: %@ , and description is : %@", service.UUID, service.description);
        [peripheral discoverCharacteristics:nil forService:service]; // discover all is slow
    }

}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error {
    if (error) {
        NSLog(@"Error: %@", error);
        return;
    }

    
    NSString *peripheralUUIDString = [peripheral uuidAsString];
    RCTResponseSenderBlock connectCallback = [connectCallbacks valueForKey:peripheralUUIDString];
    NSMutableSet *latch = [connectCallbackLatches valueForKey:peripheralUUIDString];
    [latch removeObject:service];
    
    if ([latch count] == 0) {
        // Call success callback for connect
        if (connectCallback)
        {
            NSLog(@" connectCallback called with mem addr = %@  ", connectCallback);
            connectCallback(@[[peripheral asDictionary]]);
            [connectCallbacks removeObjectForKey:peripheralUUIDString];
        }
        [connectCallbackLatches removeObjectForKey:peripheralUUIDString];
    }

}


#pragma mark   2:  Callback for connection related
- (void)centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)peripheral
{
    NSLog(@"Peripheral Connected: %@", [peripheral uuidAsString]);
    peripheral.delegate = self;
    
    // did connect is notified to app after discovery of services and then charateristics are done at didDiscoverCharacteristicsForService
    [peripheral discoverServices:nil];
    
    
}

- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error {
    NSLog(@"Peripheral Disconnected: %@", [peripheral uuidAsString]);
    
    if (error) {
        NSLog(@"Error: %@", error);
    }

    
    if(error != nil) // when PS does disconnect i.e connection lost e.g due to going out of distance range
    {
        
        [self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDisconnectPeripheral" body:@{@"peripheral": [peripheral uuidAsString], @"disconnect_reason":[error localizedDescription]}];
    }
    else // when CC initate disconnect which is due to cancelPeripheralConnection function call by CC
    {
    
        NSString *keyPeripheral = [peripheral uuidAsString];
        RCTResponseSenderBlock disconnectCallback = [disconnectCallbacks valueForKey:keyPeripheral];

        if (disconnectCallback) {
            disconnectCallback(@[[peripheral uuidAsString]]);
            [disconnectCallbacks removeObjectForKey:keyPeripheral];
        }
        else
        {
             NSLog(@"disconnectCallback is null");
        }
        
    }
}

- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)peripheral error:(NSError *)error
{
    NSLog(@"Peripheral connection failure: %@. (%@)", peripheral, [error localizedDescription]);
}

- (void)centralManagerDidUpdateState:(CBCentralManager *)central
{
    NSString *stateName = [self centralManagerStateToString:central.state];
    [self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidUpdateState" body:@{@"state":stateName}];
}


#pragma mark   3: Callback for read/notify/indicate
- (void)peripheral:(CBPeripheral *)peripheral didReadRSSI:(NSNumber *)RSSI error:(nullable NSError *)error{
  
    if (error) {
        NSLog(@"Error %@ :%@", peripheral.name, error);
        return;
    }
    
    [self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidReadRSSI" body:@{@"peripheral": peripheral.uuidAsString,  @"value": RSSI}];
    
}

    
- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    if (error) {
        NSLog(@"Error %@ :%@", characteristic.UUID, error);
        return;
    }
    NSLog(@"didUpdateValue Read value [%@]: %@ - %@", characteristic.UUID, characteristic.value, characteristic.characteristicDataType);

    if([characteristic.characteristicDataType isEqualToString:@"ByteArray"] )
    {
        NSArray *numberArrayData = [Utils numberArrayFromHex:characteristic.value];
        
        [self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidUpdateValueForCharacteristic" body:@{@"peripheral": peripheral.uuidAsString, @"characteristic":characteristic.UUID.UUIDString, @"value": numberArrayData}];
        
    }
    else
    {
        
        NSString *stringFromData = [Utils stringFromHex:characteristic.value];
        
        [self.bleAdminRNIEventBridge.eventDispatcher sendAppEventWithName:@"BleAdminDidUpdateValueForCharacteristic" body:@{@"peripheral": peripheral.uuidAsString, @"characteristic":characteristic.UUID.UUIDString, @"value": stringFromData}];
    }
  
}
    



- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    if (error) {
        NSLog(@"Error in didUpdateNotificationStateForCharacteristic: %@", error);
        return;
    }
    
    NSString *key = [self keyForPeripheral: peripheral andCharacteristic:characteristic];
    
    if (characteristic.isNotifying) {
        NSLog(@"Notification began on %@", characteristic.UUID);
        RCTResponseSenderBlock notificationCallback = [notificationCallbacks objectForKey:key];
        if(notificationCallback != NULL)
        {
             notificationCallback(@[]);
            [notificationCallbacks removeObjectForKey:key];
        }
    } else {
        // Notification has stopped
        NSLog(@"Notification ended on %@", characteristic.UUID);
        RCTResponseSenderBlock stopNotificationCallback = [stopNotificationCallbacks objectForKey:key];
        stopNotificationCallback(@[]);
        [stopNotificationCallbacks removeObjectForKey:key];
     
    }
}

#pragma mark   4: Callback for write
- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    NSLog(@"didWrite");
    
    NSString *key = [self keyForPeripheral: peripheral andCharacteristic:characteristic];
    RCTResponseSenderBlock writeCallback = [writeCallbacks objectForKey:key];
    
    if (writeCallback) {
        if (error) {
            NSLog(@"%@", error);
        } else {
            if ([writeQueue count] == 0) {
                writeCallback(@[@""]);
                [writeCallbacks removeObjectForKey:key];
            }else{
                
                NSData *message = [writeQueue objectAtIndex:0];
                [writeQueue removeObjectAtIndex:0];
                
                [peripheral writeValue:message forCharacteristic:characteristic type:CBCharacteristicWriteWithResponse];
            }
            
        }
    }
    
}

@end

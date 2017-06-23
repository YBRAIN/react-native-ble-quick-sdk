//
// CommAdminBleRNI.h
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

#ifndef CommAdminBleRNI_h
#define CommAdminBleRNI_h
#import <Foundation/Foundation.h>

#import <RCTBridgeModule.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "NSYLog.h"
#import "Utils.h"
#import  "BleAdmin.h"
//#import "RCTBridge.h"

@interface CommAdminBleRNI : NSObject <RCTBridgeModule>
{
    
    @private
    BleAdmin* mBleAdmin;
    
}


@end



#endif

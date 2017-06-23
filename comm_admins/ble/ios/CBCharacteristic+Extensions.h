//
// CBCharacteristic+Extensions.h
// BLE Central for BDS Plugin
//
// Original copyright (c) 2104 Don Coleman
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

#import <objc/runtime.h>
#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "CBCharacteristic+Extensions.h"


@interface CBCharacteristic(com_eric_bds_ble_extension)

@property (nonatomic, retain) NSString *characteristicDataType;

@end

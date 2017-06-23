//
// CBCharacteristic+Extensions.m
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

#import "CBCharacteristic+Extensions.h"

static char CHARACTERISTIC_DATA_TYPE_ID;


@implementation CBCharacteristic(com_eric_bds_ble_extension)


-(void)setCharacteristicDataType:(NSString *)characteristicDataType {
    objc_setAssociatedObject(self, &CHARACTERISTIC_DATA_TYPE_ID, characteristicDataType, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

-(NSString*)characteristicDataType{
    return objc_getAssociatedObject(self, &CHARACTERISTIC_DATA_TYPE_ID);
}
    
@end

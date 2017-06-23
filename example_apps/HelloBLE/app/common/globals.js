//
//  globals.js
//

import {getSDKServiceMgrInstance} from 'react-native-ble-quick-sdk';

var GVar = {

    objSDKSvcMgr: null,
}

if (GVar.objSDKSvcMgr == null) {

    GVar.objSDKSvcMgr = getSDKServiceMgrInstance(false);


}

export default GVar;


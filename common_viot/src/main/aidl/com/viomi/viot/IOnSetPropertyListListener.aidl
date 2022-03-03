// IOnSetPropertyListListener.aidl
package com.viomi.viot;

import com.viomi.viot.property.VIotDeviceProperty;

interface IOnSetPropertyListListener {
    void onSet(inout List<VIotDeviceProperty> list);
}

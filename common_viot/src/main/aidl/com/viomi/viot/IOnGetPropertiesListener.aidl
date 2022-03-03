// IOnGetPropertiesListener.aidl
package com.viomi.viot;

import com.viomi.viot.property.VIotDeviceProperty;

interface IOnGetPropertiesListener {
    void onGetProperty(inout List<VIotDeviceProperty> list, String id);
}

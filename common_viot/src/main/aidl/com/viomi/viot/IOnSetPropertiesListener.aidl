// IOnSetPropertiesListener.aidl
package com.viomi.viot;

import com.viomi.viot.property.VIotDeviceProperty;

interface IOnSetPropertiesListener {
    void onSet(inout VIotDeviceProperty property);
}

// IOnActionListener.aidl
package com.viomi.viot;

import com.viomi.viot.action.VIotDeviceAction;

interface IOnActionListener {
    void onAction(inout VIotDeviceAction action);
}

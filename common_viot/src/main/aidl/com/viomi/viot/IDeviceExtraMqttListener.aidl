// IDeviceExtraMqttListener.aidl
package com.viomi.viot;

interface IDeviceExtraMqttListener {
    void onExtraMessage(String message, String topic);
}

// IMessageArrivedListener.aidl
package com.viomi.viot.push;

import com.viomi.viot.push.PushMessage;

// Declare any non-default types here with import statements

interface IMessageArrivedListener {
    void onMessageArrived(inout PushMessage message);
}

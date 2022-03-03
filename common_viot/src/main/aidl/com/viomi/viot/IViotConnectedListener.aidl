// IViotConnectedListener.aidl
package com.viomi.viot;

// Declare any non-default types here with import statements

interface IViotConnectedListener {
    void onConnected();

    void onDisconnected();
}
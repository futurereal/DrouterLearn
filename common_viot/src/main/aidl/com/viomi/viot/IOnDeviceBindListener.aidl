// IOnDeviceBindListener.aidl
package com.viomi.viot;

interface IOnDeviceBindListener {
    void onSucceed();

    void onFailed(int code, String message);

    void onBind();

    void onUnBind();
}

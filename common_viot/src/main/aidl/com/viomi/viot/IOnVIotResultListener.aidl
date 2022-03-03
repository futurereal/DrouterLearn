// IOnVIotResultListener.aidl
package com.viomi.viot;

interface IOnVIotResultListener {
    void onSucceed();

    void onFailed(int code, String message);
}

// IDataRefreshListener.aidl
package com.viomi.viot;

interface IDataRefreshListener {
    void onDeviceRefresh();

    void onSceneRefresh();

    void onMiTokenRefresh();
}

package com.viomi.waterpurifier.edison.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

/**
 * Copyright (C), 2014-2019, 佛山云米科技有限公司
 *
 * @ProjectName: viomi_face
 * @Package: viomi.com.viomiaiface.config
 * @ClassName: ConnectionStateMonitor
 * @Description:    网络状态更改监听
 * @Author: randysu
 * @CreateDate: 2019-06-06 12:10
 * @UpdateUser:
 * @UpdateDate: 2019-06-06 12:10
 * @UpdateRemark:
 * @Version: 1.0
 */
public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    private static final String TAG = ConnectionStateMonitor.class.getSimpleName();
    
    private final Context context;
    private final NetworkRequest networkRequest;
    private final OnWifiConnectStatusListener listener;

    public ConnectionStateMonitor(Context context, OnWifiConnectStatusListener listener) {
        this.context = context;
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        this.listener = listener;
    }

    public void enable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);

        Log.i(TAG, "onLosing 网络不可用");

        if (listener != null) {
            listener.onLost();
        }
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();

        Log.i(TAG, "onUnavailable 网络不可用");

        if (listener != null) {
            listener.onLost();
        }
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);

        Log.i(TAG, "onAvailable 网络可用");

        if (listener != null) {
            listener.onConnected();
        }
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);

        Log.i(TAG, "onLost 断网了");

        if (listener != null) {
            listener.onLost();
        }
    }

    public interface OnWifiConnectStatusListener {
        void onConnected();
        void onLost();
    }

}

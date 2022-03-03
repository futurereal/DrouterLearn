package com.viomi.viot.device.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.IBinder
import com.viomi.viot.rxjava2.VIotBusEvent
import com.viomi.viot.rxjava2.VIotRxBus
import com.viomi.viot.utils.LogUtil

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/26
 *     desc   : 设备进程 Service
 *     version: 1.0
 * </pre>
 */
class VIotHostService : Service() {
    private var mIVIotHostServiceProxy: IVIotHostServiceProxy? = null
    private var mConnectivityManager: ConnectivityManager? = null

    override fun onCreate() {
        super.onCreate()
        LogUtil.d(TAG, "onCreate.")
        mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        mConnectivityManager?.requestNetwork(NetworkRequest.Builder().build(), networkCallback)
        mIVIotHostServiceProxy = IVIotHostServiceProxy(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d(TAG, "onStartCommand.")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        LogUtil.d(TAG, "onBind.")
        return mIVIotHostServiceProxy
    }

    override fun onRebind(intent: Intent?) {
        LogUtil.d(TAG, "onRebind.")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LogUtil.e(TAG, "onUnbind.")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(TAG, "onDestroy.")
        mIVIotHostServiceProxy?.unBindDevice()
        mConnectivityManager?.unregisterNetworkCallback(networkCallback)
        mConnectivityManager = null
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            LogUtil.d(TAG, "Network is available.")
            VIotRxBus.instance.post(VIotBusEvent.MSG_NETWORK_ENABLE)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            LogUtil.e(TAG, "Network is losing.")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            LogUtil.e(TAG, "Network is lost.")
        }

        override fun onUnavailable() {
            super.onUnavailable()
            LogUtil.e(TAG, "Network is unavailable.")
        }
    }

    companion object {
        private val TAG = VIotHostService::class.java.simpleName
    }
}

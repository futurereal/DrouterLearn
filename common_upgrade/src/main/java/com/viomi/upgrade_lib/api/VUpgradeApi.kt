package com.viomi.upgrade_lib.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.blankj.utilcode.util.NetworkUtils
import com.viomi.upgrade_lib.entity.CheckVersionResult
import com.viomi.upgrade_lib.worker.CheckUpdateHelper
import com.viomi.upgrade_lib.worker.CheckUpdateHelper.Companion.SCREEN_OFF
import com.viomi.upgrade_lib.worker.CheckUpdateHelper.Companion.checkInstall
import com.viomi.upgrade_lib.entity.DeviceInfo
import com.viomi.upgrade_lib.entity.DownloadResult
import com.viomi.upgrade_lib.utils.VLog
import com.viomi.upgrade_lib.worker.CheckUpdateWork
import org.json.JSONObject


/**
 * Created by mai on 2020/12/15.
 */

class VUpgradeApi {

    var mDeviceInfo: DeviceInfo? = null
    var mGetUserId: (() -> String)? = null

    var mIsCheckVersion: (() -> Boolean)? = null

    var mVersionCallBack: ((Boolean) -> Unit)? = null

    private var mContext: Context? = null


    companion object {
        val instance: VUpgradeApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            VUpgradeApi()
        }
    }


    /**
     * 初始化，
     *
     * isCheckVersion 是否检测版本
     * versionCallBack 是否有新版本回调
     *
     */
    fun init(
        context: Context?,
        deviceInfo: DeviceInfo,
        getUserId: () -> String,
        isCheckVersion: (() -> Boolean)? = null,
        versionCallBack: ((Boolean) -> Unit)? = null
    ) {
        mIsCheckVersion = isCheckVersion
        mVersionCallBack = versionCallBack

        mContext = context
        mDeviceInfo = deviceInfo
        mGetUserId = getUserId
        CheckUpdateHelper.deleteApkFile()
        CheckUpdateHelper.checkVersionUpdate(context)
        CheckUpdateHelper.uploadAppVersion(mContext!!)
//        val connectivityManager =
//            mContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//
//        connectivityManager!!.requestNetwork(NetworkRequest.Builder().build(),
//            object : NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    super.onAvailable(network)
//
//                    if (NetworkUtils.isAvailable() && NetworkUtils.isConnected()) {
//                        CheckUpdateHelper.uploadAppVersion(mContext!!)
//                    }
//                }
//            })

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        mContext!!.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                VLog.d("VUpgradeApi", "intent.getAction()-->" + intent.action)
                if (Intent.ACTION_SCREEN_OFF == intent.action) { //检查息屏是否安装apk
                    SCREEN_OFF = true
                    checkInstall(context, true)
                } else if (Intent.ACTION_SCREEN_ON == intent.action) {
                    SCREEN_OFF = false
                }
            }
        }, filter)
    }


    /**
     * 下载apk,只下载全量的
     */
    fun downloadApk2(context: Context, url: String, progressListener: (Int) -> Unit) {
        CheckUpdateHelper.downloadApk(context, url, progressListener)
    }

    /**
     * 检测版本
     */
    fun checkVersion(context: Context, versionListener: (CheckVersionResult) -> Unit) {
        CheckUpdateHelper.checkAppVersionNoDownload(context, versionListener)
    }


    /**
     * 下载增量差分包或者全量包
     */
    fun downloadApk(
        context: Context,
        url: String,
        upgradeType: Int,
        recordId: Long,
        downloadListener: (DownloadResult) -> Unit
    ) {
        CheckUpdateHelper.downloadApk(context, url, upgradeType, recordId, downloadListener)
    }

    fun cancelAllWork(context: Context){
        CheckUpdateHelper.cancelAllWork(context)
    }
}
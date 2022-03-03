package com.viomi.iotdevice.iotmesh

import com.viomi.iotdevice.ViomiIotManager
import com.viomi.iotdevice.common.SerialParams
import com.viomi.iotdevice.common.http.IotApiClient
import com.viomi.iotdevice.common.protocol.IotCmd
import com.viomi.iotdevice.common.util.LogUtil
import com.viomi.iotdevice.common.util.RxSchedulerUtil
import com.viomi.iotdevice.iotmesh.callback.IotMeshCallback
import com.viomi.iotdevice.iottowifi.IotToWiFiSerialManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : Viot Mesh Network manager
 *     version: 1.0
 * </pre>
 */
internal class IotMeshManager {
    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private var mScanDisposable: Disposable? = null

    fun createDeviceToWiFiMeshQR() {
        var mac = ViomiIotManager.instance.config?.mac
        mac = mac?.replace(":".toRegex(), "") ?: ""
        val clientId = mac + System.currentTimeMillis() / 1000
        IotApiClient.instance.pApiService.createMeshQRCode("1", clientId)
            ?.compose(RxSchedulerUtil.schedulerTransformer1())
            ?.onTerminateDetach()
            ?.subscribe({
                if (!it.loginQRCode?.result.isNullOrBlank()) {
                    iotMeshCallback?.onMeshQRRefresh(true, it.loginQRCode?.result)
                    checkMeshResult(clientId)
                } else {
                    iotMeshCallback?.onMeshQRRefresh(false, null)
                }
            }, {
                LogUtil.e(TAG, it.message.toString())
                iotMeshCallback?.onMeshQRRefresh(false, null)
            })?.let { mCompositeDisposable.add(it) }
    }

    private fun checkMeshResult(clientId: String) {
        mScanDisposable?.dispose()
        mScanDisposable = Flowable.interval(0, 3, TimeUnit.SECONDS)
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .onTerminateDetach()
            .flatMap {
                val jsonObject = JSONObject()
                jsonObject.put("clientID", clientId)
                IotApiClient.instance.pApiService.checkMeshStatus(IotApiClient.instance.getRequestBody(jsonObject))
            }
            .takeUntil { !it.loginQRCode?.token.isNullOrBlank() && !it.loginQRCode?.appendAttr.isNullOrBlank() && !it.loginQRCode?.loginResult?.userCode.isNullOrBlank() }
            .filter { !it.loginQRCode?.token.isNullOrBlank() && !it.loginQRCode?.appendAttr.isNullOrBlank() && !it.loginQRCode?.loginResult?.userCode.isNullOrBlank() }
            .observeOn(AndroidSchedulers.mainThread())
            .onTerminateDetach()
            .map {
                it.loginQRCode?.parseAppendAttr()
                if (it.loginQRCode?.code == 100) {
                    iotMeshCallback?.onDeviceToWiFiMeshResult(true, it)
                } else {
                    iotMeshCallback?.onDeviceToWiFiMeshResult(false, null)
                }
                it
            }
            .observeOn(Schedulers.io())
            .onTerminateDetach()
            .subscribe({ sendMeshInfo(it.loginQRCode?.userInfo?.account) }, {
                LogUtil.e(TAG, it.message.toString())
                iotMeshCallback?.onDeviceToWiFiMeshResult(false, null)
            })
        mScanDisposable?.let { mCompositeDisposable.add(it) }
    }

    fun removeCallback() {
        iotMeshCallback = null
        mCompositeDisposable.clear()
    }

    private fun sendMeshInfo(uid: String?) {
        val stringBuilder = IotCmd.SendMeshInfo +
                " " + SerialParams.instance.wiFiSSID +
                " " + SerialParams.instance.wiFiPassword +
                " " + SerialParams.instance.wiFiMac +
                " " + uid
        IotToWiFiSerialManager.instance.command = stringBuilder
    }

    fun wifiToDeviceMesh(dataArray: Array<String>?) = takeUnless { dataArray.isNullOrEmpty() || dataArray.size != 3 }?.run {
        SerialParams.instance.saveMeshStatus(true)

        mCompositeDisposable.add(
            Observable.just(dataArray!!)
                .compose(RxSchedulerUtil.schedulerTransformer1())
                .onTerminateDetach()
                .subscribe({ iotMeshCallback?.onWiFiInfoCallback(dataArray[0], dataArray[1]) }, {
                    LogUtil.e(TAG, it.message.toString())
                })
        )

        IotApiClient.instance.pApiService.getMeshInfo(ViomiIotManager.instance.config?.did?.toLong(), dataArray[2])
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.onTerminateDetach()
            ?.retryWhen { it.flatMap { Observable.timer(5, TimeUnit.SECONDS) } }
            ?.subscribe({
                if (it.mobBaseRes?.code == 100) {
                    iotMeshCallback?.onWiFiToDeviceMeshResult(true, it)
                } else {
                    SerialParams.instance.saveMeshStatus(false)
                    iotMeshCallback?.onWiFiToDeviceMeshResult(false, null)
                }
            }, {
                SerialParams.instance.saveMeshStatus(false)
                iotMeshCallback?.onWiFiToDeviceMeshResult(false, null)
                LogUtil.e(TAG, it.message.toString())
            })?.let { mCompositeDisposable.add(it) }
    }

    fun reset() {
        IotToWiFiSerialManager.instance.command = IotCmd.Viot_Restore
    }

    var iotMeshCallback: IotMeshCallback? = null

    companion object {
        private val TAG = IotMeshManager::class.java.simpleName

        val instance: IotMeshManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            IotMeshManager()
        }
    }
}

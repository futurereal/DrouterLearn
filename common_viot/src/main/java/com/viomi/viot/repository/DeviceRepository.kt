package com.viomi.viot.repository

import android.content.Context
import com.alibaba.fastjson.JSON
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.device.VIotDevice
import com.viomi.viot.https.RequestHandler
import com.viomi.viot.https.ResultCodes
import com.viomi.viot.https.VIotClient
import com.viomi.viot.https.beans.BaseResponseBean
import com.viomi.viot.https.beans.MQTTConfigBean
import com.viomi.viot.listener.OnDeviceVerifyListener
import com.viomi.viot.listener.OnMQTTConfigGetListener
import com.viomi.viot.preference.VIotDevicePreference
import com.viomi.viot.rxjava2.VIotBusEvent
import com.viomi.viot.rxjava2.VIotRxBus
import com.viomi.viot.utils.FileUtil.saveFileObject
import com.viomi.viot.utils.encrypt.AESUtil
import com.viomi.viot.utils.encrypt.RSAUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.util.*

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/1
 *     desc   : Http 请求 Repository
 *     version: 1.0
 * </pre>
 */
object DeviceRepository {

    /**
     * 验证设备合法性
     */
    internal fun checkDeviceLegal(publicKey: String, device: VIotDevice, listener: OnDeviceVerifyListener?) {
        val randomStr = UUID.randomUUID().toString()
        val accessKeyRandom = AESUtil.aesEncode(randomStr, device.deviceAccessKey)
        val handler = RequestHandler()
        handler.addParams("pid", device.productId)
        handler.addParams("did", device.deviceId)
        handler.addParams("mac", device.mac.lowercase(Locale.getDefault()))
        handler.addParams("random", randomStr)
        handler.addParams("deviceAccessKeyrandom", accessKeyRandom)
        handler.addParams("devicePublicKey", publicKey)
        Observable.just(RSAUtil.rsaEncode(handler.paramsJSONObject.toString(), device.cloudPublicKey))
            .subscribeOn(Schedulers.io())
            .onTerminateDetach()
            .flatMap { VIotClient.instance.pService.deviceVerify(handler.getTextRequestBody(it)) }
            .onTerminateDetach()
            .subscribe(object : Observer<BaseResponseBean<String?>> {
                override fun onSubscribe(disposable: Disposable) {
                    device.pCompositeDisposable.add(disposable)
                }

                override fun onNext(responseBean: BaseResponseBean<String?>?) {
                    if (responseBean?.code == ResultCodes.RESULT_CODE_1) {
                        listener?.onLegal()
                    } else {
                        responseBean?.code?.let { listener?.onIllegal(it, responseBean.desc) }
                    }
                }

                override fun onError(throwable: Throwable) {
                    if (throwable is HttpException) {
                        listener?.onIllegal(ResultCodes.RESULT_CODE_2, "设备验证失败.")
                    } else {
                        VIotRxBus.instance.post(VIotBusEvent.MSG_RETRY_INITIALIZE)
                    }
                }

                override fun onComplete() {}
            })
    }

    /**
     * 获取设备 MQtt 配置
     */
    internal fun getDeviceConfig(context: Context, device: VIotDevice, listener: OnMQTTConfigGetListener?) {
        val randomStr = UUID.randomUUID().toString()
        val accessKeyRandom = AESUtil.aesEncode(randomStr, device.deviceAccessKey)
        val mac = device.mac.lowercase(Locale.getDefault())
        val publicKey = RSAUtil.getRSAPublicKey(context)
        val privateKey = RSAUtil.getRSAPrivateKey(context)
        val isBind = VIotDevicePreference.getBindFlag(context)
        val handler = RequestHandler()
        handler.addParams("pid", device.productId)
        handler.addParams("did", device.deviceId)
        handler.addParams("uid", if (device.userId.isNullOrBlank()) -1 else device.userId)
        handler.addParams("meshId", mac)
        handler.addParams("devicePublicKey", publicKey)
        handler.addParams("partnerDid", device.deviceConfig.miDid)
        handler.addParams("forceBound", if (device.userId.isNullOrBlank()) true else !isBind)
        handler.addParams("random", randomStr)
        handler.addParams("deviceAccessKeyRandom", accessKeyRandom)
        handler.addParams("mac", mac)
        Observable.just(RSAUtil.rsaEncode(handler.paramsJSONObject.toString(), device.cloudPublicKey))
            .subscribeOn(Schedulers.io())
            .onTerminateDetach()
            .flatMap { VIotClient.instance.pService.getMQTTConfig(handler.getTextRequestBody(it)) }
            .onTerminateDetach()
            .map {
                val mQttConfigBean: MQTTConfigBean
                if (it.code == ResultCodes.RESULT_CODE_1) {
                    val aesDecryptResult = AESUtil.decrypt(it.result, device.deviceAccessKey)
                    val rsaDecryptResult = RSAUtil.rsaDecode(aesDecryptResult, privateKey)
                    listener?.onDecipherResult(rsaDecryptResult)
                    mQttConfigBean = JSON.parseObject(rsaDecryptResult, MQTTConfigBean::class.java)
                    mQttConfigBean.isParse = true
                } else {
                    mQttConfigBean = MQTTConfigBean()
                }
                mQttConfigBean.code = it.code
                mQttConfigBean.desc = it.desc
                mQttConfigBean
            }
            .onTerminateDetach()
            .map {
                if (it.isParse) {
                    saveFileObject(context, MQTTConfigBean::class.java.simpleName, it)
                }
                it
            }
            .onTerminateDetach()
            .subscribe(object : Observer<MQTTConfigBean> {
                override fun onSubscribe(disposable: Disposable) {
                    device.pCompositeDisposable.add(disposable)
                }

                override fun onNext(mqttConfigBean: MQTTConfigBean) {
                    if (mqttConfigBean.isParse) {
                        listener?.onSucceed(mqttConfigBean)
                    } else {
                        listener?.onFailed(mqttConfigBean.code, mqttConfigBean.desc)
                    }
                }

                override fun onError(throwable: Throwable) {
                    VIotRxBus.instance.post(VIotBusEvent.MSG_RETRY_INITIALIZE)
                }

                override fun onComplete() {}
            })
    }

    /**
     * 小米设备获取云米三元组
     */
    fun getVmCode(config: VIotDeviceConfig, publicKey: String): Observable<BaseResponseBean<String?>> {
        val handler = RequestHandler()
        handler.addParams("model", config.miModel)
        handler.addParams("cid", config.miDid)
        handler.addParams("mac", config.mac)
        handler.addParams("random", UUID.randomUUID().toString())
        handler.addParams("devicePublicKey", publicKey)
        return VIotClient.instance.pService.registerDevice(handler.getTextRequestBody(handler.paramsJSONObject.toString()))
    }
}

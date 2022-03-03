package com.viomi.viot.device

import android.content.Context
import com.viomi.viot.IOnDeviceBindListener
import com.viomi.viot.action.VIotDeviceAction
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.event.VIotDeviceEvent
import com.viomi.viot.https.ResultCodes
import com.viomi.viot.https.beans.MQTTConfigBean
import com.viomi.viot.listener.OnDeviceVerifyListener
import com.viomi.viot.listener.OnMQTTConfigGetListener
import com.viomi.viot.mqtt.MQTTClient
import com.viomi.viot.mqtt.MQTTTopic
import com.viomi.viot.mqtt.beans.MQTTMessage
import com.viomi.viot.preference.VIotDevicePreference
import com.viomi.viot.property.VIotDeviceProperty
import com.viomi.viot.repository.DeviceRepository
import com.viomi.viot.repository.MQTTRepository
import com.viomi.viot.rxjava2.VIotBusEvent
import com.viomi.viot.rxjava2.VIotRxBus
import com.viomi.viot.utils.LogUtil
import com.viomi.viot.utils.encrypt.RSAUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.*

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/13
 *     desc   : VIot 设备封装
 *     version: 1.0
 * </pre>
 */
class VIotDevice(internal val deviceConfig: VIotDeviceConfig, private val context: Context) {
    private var mMQTTClient: MQTTClient? = MQTTClient(this, context)
    internal val pCompositeDisposable = CompositeDisposable()
    internal val pCacheActionMap: MutableMap<String?, List<VIotDeviceAction>> = HashMap()
    internal val pReplyActionMap: MutableMap<String, MutableList<VIotDeviceAction>> = HashMap()
    internal val pSupportProperties: MutableMap<Int, MutableList<Int>> = HashMap()
    private var mJob: Job? = null

    var isInitSuccess = false

    var isSupportViot: Boolean? = true

    val allProperties: MutableMap<String, Any> = HashMap()

    /**
     * 设备初始化
     */
    @Synchronized
    fun initialize(context: Context, iOnDeviceBindListener: IOnDeviceBindListener?) {
        LogUtil.d(TAG, "initialize")
        takeUnless { isInitSuccess }?.run {
            isInitSuccess = true
            mJob = CoroutineScope(Dispatchers.IO).launch { start(context, iOnDeviceBindListener) }
        }
    }

    private suspend fun start(context: Context, iOnDeviceBindListener: IOnDeviceBindListener?) = withContext(Dispatchers.IO) {
        val isBind = VIotDevicePreference.getBindFlag(context)
        // 设备没有绑定
        if (!isBind) {
            RSAUtil.createRSAKeyPair(context)
            if (deviceConfig.needVerify && !deviceConfig.userId.isNullOrBlank()) {
                bind(context, iOnDeviceBindListener)
            } else {
                getMQTTConfig(context, iOnDeviceBindListener)
            }
        }
        // 设备已绑定
        else {
            getMQTTConfig(context, iOnDeviceBindListener)
        }
    }

    private fun bind(context: Context, iOnDeviceBindListener: IOnDeviceBindListener?) {
        val publicKey = RSAUtil.getRSAPublicKey(context)
        DeviceRepository.checkDeviceLegal(publicKey, this, object : OnDeviceVerifyListener {
            override fun onLegal() {
                LogUtil.d(TAG, "VIot Device verify success.")
                getMQTTConfig(context, iOnDeviceBindListener)
            }

            override fun onIllegal(code: Int, reason: String?) {
                LogUtil.e(TAG, "VIot Device verify failed, code = $code, reason = $reason.")
                iOnDeviceBindListener?.onFailed(code, reason)
                isInitSuccess = false
            }
        })
    }

    private fun getMQTTConfig(context: Context, iOnDeviceBindListener: IOnDeviceBindListener?) =
        DeviceRepository.getDeviceConfig(context, this, object : OnMQTTConfigGetListener {
            override fun onDecipherResult(result: String?) {
                LogUtil.d(TAG, "Get cloud MQtt config decipher result: $result.")
            }

            override fun onSucceed(configBean: MQTTConfigBean?) {
                LogUtil.d(TAG, "Get cloud MQtt config success, start device.")
                mMQTTClient?.initMQTTConfig()
            }

            override fun onFailed(code: Int, desc: String?) {
                LogUtil.e(TAG, "Get cloud MQtt config failed, code = $code, reason = $desc.")
                if (code == ResultCodes.RESULT_CODE_19) {
                    VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_UNBIND)
                } else {
                    iOnDeviceBindListener?.onFailed(code, desc)
                    isInitSuccess = false
                }
            }
        })

    internal fun stop() {
        mJob?.cancel()
        pCompositeDisposable.dispose()
        mMQTTClient?.disconnect()
        allProperties.clear()
    }

    internal fun syncProperties(properties: MutableMap<String, Any>): String? {
        return reportProperties(properties)
    }

    private fun reportProperties(map: Map<String, Any>?): String? = takeUnless { mMQTTClient?.isSubscribeSuccess != true }?.run {
        val pair = MQTTRepository.createPropertiesChangedMessage(map, deviceId, deviceAccessKey)
        mMQTTClient?.publishTopic(pair.second)
        pair.first
    }

    internal fun reportAllProperties() = takeUnless { allProperties.isEmpty() || isSupportViot != true }?.run {
        reportProperties(allProperties.filter {
            val value = it.key.split(".")
            (pSupportProperties.keys.contains(value[0].toInt()))
                .and(pSupportProperties[value[0].toInt()]?.contains(value[1].toInt()) == true)
        })
    }

    internal fun pingConnection() = mMQTTClient?.publishTopic(MQTTRepository.createDevicePingMessage(deviceId, deviceAccessKey))

    internal fun sendEvent(events: List<VIotDeviceEvent>): String {
        val pair = MQTTRepository.createEventMessage(events, deviceId, deviceAccessKey)
        mMQTTClient?.publishTopic(pair.second)
        return pair.first
    }

    internal fun sendActionOut(actions: MutableList<VIotDeviceAction>, id: String) {
        mMQTTClient?.publishTopic(MQTTRepository.createActionOutMessage(actions, deviceId, deviceAccessKey, id))
        pReplyActionMap.remove(id)
    }

    internal fun sentPropertiesValue(properties: MutableList<VIotDeviceProperty>?, id: String) =
        mMQTTClient?.publishTopic(MQTTRepository.createGetPropertiesReplyMessage(properties, deviceId, deviceAccessKey, id))

    internal fun resetDevice(): Any? = if (mMQTTClient?.pMQttAndroidClient?.isConnected == true) {
        mMQTTClient?.publishTopic(MQTTRepository.createDeviceResetMessage(deviceId, deviceAccessKey))
    } else {
        VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_UNBIND)
    }

    internal fun checkConnection(): String {
        val pair = MQTTRepository.createCheckConnectMessage(deviceId, deviceAccessKey)
        mMQTTClient?.publishTopic(pair.second)
        return pair.first
    }

    internal fun uploadDeviceInfo(map: MutableMap<String, String>?) =
        mMQTTClient?.publishTopic(MQTTRepository.createDeviceInfoMessage(deviceConfig, context, map))

    internal fun sendMqttMessage(payload: String?, subDid: String?, isReply: Boolean) = payload?.let {
        val topicName = if (isReply) String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, subDid) else String.format(MQTTTopic.PROD_UP_RAW, deviceId, subDid)
        mMQTTClient?.publishTopic(MQTTMessage(topicName, it, ""))
    }

    internal fun pullModelConfig() = mMQTTClient?.publishTopic(MQTTRepository.createModelConfigMessage(deviceId, deviceAccessKey))

    val productId: Int? get() = deviceConfig.productId

    val token: String? get() = deviceConfig.token

    val userId: String? get() = deviceConfig.userId

    val deviceId: String? get() = deviceConfig.deviceId

    val deviceAccessKey: String? get() = deviceConfig.deviceAccessKey

    val cloudPublicKey: String? get() = deviceConfig.cloudPublicKey

    var mac: String = deviceConfig.mac

    val manufacture: String? get() = deviceConfig.manufacture

    companion object {
        private val TAG = VIotDevice::class.java.simpleName
    }
}

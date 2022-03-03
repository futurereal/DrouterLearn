package com.viomi.viot.mqtt

import android.content.Context
import com.viomi.viot.device.DeviceMethod
import com.viomi.viot.device.VIotDevice
import com.viomi.viot.https.beans.MQTTConfigBean
import com.viomi.viot.mqtt.beans.MQTTMessage
import com.viomi.viot.repository.MQTTRepository
import com.viomi.viot.repository.MQTTRepository.parseAndGetReply
import com.viomi.viot.rxjava2.RxScheduler
import com.viomi.viot.rxjava2.VIotBusEvent
import com.viomi.viot.rxjava2.VIotRxBus
import com.viomi.viot.utils.FileUtil
import com.viomi.viot.utils.LogUtil
import com.viomi.viot.utils.VIotUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/02
 *     desc   : MQTT 客户端
 *     version: 1.0
 * </pre>
 */
class MQTTClient(private val device: VIotDevice, private val context: Context) {
    internal var pMQttAndroidClient: MqttAndroidClient? = null
    private var mMQttConnectOptions: MqttConnectOptions? = null
    private var mMethod = ""
    private var mHasInit = false
    private var mDisposable: Disposable? = null

    /**
     * 初始化 MQTT 连接
     */
    internal fun initMQTTConfig() {
        val mQttConfigBean = FileUtil.getFileObject(context, MQTTConfigBean::class.java.simpleName) as? MQTTConfigBean
        val deviceId = device.deviceId
        val serverUrl = "tcp://${mQttConfigBean?.endpoint}:1883"
        val clientIdUrl = "${mQttConfigBean?.groupId}@@@${deviceId}"
        val username = "Token|${mQttConfigBean?.accessKey}|${mQttConfigBean?.instanceId}"
        val password = if (mQttConfigBean?.tokens?.size ?: 0 > 1) {
            "${mQttConfigBean?.tokens?.get(1)?.model}|${mQttConfigBean?.tokens?.get(1)?.value}|" +
                    "${mQttConfigBean?.tokens?.get(0)?.model}|${mQttConfigBean?.tokens?.get(0)?.value}"
        } else {
            "${mQttConfigBean?.tokens?.get(0)?.model}|${mQttConfigBean?.tokens?.get(0)?.value}"
        }
        LogUtil.d(TAG, "MQtt config, server url = $serverUrl, clientIdUrl = $clientIdUrl, username = $username, password = $password.")

        pMQttAndroidClient = MqttAndroidClient(context, serverUrl, clientIdUrl)
        pMQttAndroidClient?.setCallback(mMQttCallback)
        mMQttConnectOptions = MqttConnectOptions()
        // 清除缓存
        mMQttConnectOptions?.isCleanSession = true
        // 超时时间, 单位: 秒
        mMQttConnectOptions?.connectionTimeout = 10
        // 心跳包发送间隔, 单位: 秒
        mMQttConnectOptions?.keepAliveInterval = 90
        // 是否自动重连
        mMQttConnectOptions?.isAutomaticReconnect = false
        // MQtt 版本
        mMQttConnectOptions?.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
        // 用户名
        mMQttConnectOptions?.userName = username
        // 密码
        mMQttConnectOptions?.password = password.toCharArray()

        connect()
    }

    /**
     * MQTT 连接
     */
    private fun connect() {
        kotlin.runCatching {
            pMQttAndroidClient?.connect(mMQttConnectOptions, null, mIMQttActionListener)
        }.onFailure {
            it.printStackTrace()
            LogUtil.e(TAG, it.message.toString())
        }
    }

    /**
     * MQTT 断开连接
     */
    internal fun disconnect() {
        kotlin.runCatching {
            mHasInit = false
            mDisposable?.dispose()
            mDisposable = null
            pMQttAndroidClient?.unregisterResources()
            Thread.sleep(50)
            pMQttAndroidClient?.isConnected?.takeIf { it }?.let {
                val token = pMQttAndroidClient?.disconnect()
                takeIf { token?.isComplete == true }?.run { pMQttAndroidClient?.close() }
                pMQttAndroidClient?.setCallback(null)
            }
        }.onFailure {
            it.printStackTrace()
            LogUtil.e(TAG, it.message.toString())
        }
    }

    internal var isSubscribeSuccess = false

    /**
     * 发送 MQTT 消息
     */
    internal fun publishTopic(message: MQTTMessage) = takeIf { pMQttAndroidClient?.isConnected == true }?.run {
        Observable.just(message)
            .subscribeOn(Schedulers.io())
            .onTerminateDetach()
            .subscribe({
                LogUtil.d(TAG, "send message: $it")
                val mQttMessage = MqttMessage()
                mQttMessage.payload = it.payload.toByteArray()
                mQttMessage.qos = 0
                mMethod = it.method
                pMQttAndroidClient?.publish(it.topicName, mQttMessage)
            }, { LogUtil.e(TAG, "PublishTopic error, reason: ${it.message}.") })
    }

    /**
     * 订阅 Topic
     */
    private fun subscribeTopic() {
        kotlin.runCatching {
            val deviceId = device.deviceId
            val upRawReply = String.format(MQTTTopic.PROD_UP_RAW_REPLY, deviceId)
            val downRaw = String.format(MQTTTopic.PROD_DOWN_RAW, deviceId)

            if (pMQttAndroidClient?.isConnected == true) {
                pMQttAndroidClient?.subscribe(upRawReply, MQTTQoS.QoS0, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        isSubscribeSuccess = true
                        LogUtil.d(TAG, "MQtt subscribe $upRawReply success.")
                        val offline = FileUtil.getFileObject(context, "offlineReason") as? String
                        if (!offline.isNullOrBlank()) {
                            publishTopic(MQTTRepository.createLogReportMessage(offline, device.deviceId, device.deviceAccessKey))
                            FileUtil.saveFileObject(context, "offlineReason", null)
                        }
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        LogUtil.e(TAG, "MQtt subscribe $upRawReply failed, reason: ${exception?.localizedMessage}.")
                    }
                })

                pMQttAndroidClient?.subscribe(downRaw, MQTTQoS.QoS0, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        LogUtil.d(TAG, "MQtt subscribe $downRaw success.")
                        device.uploadDeviceInfo(null)
                        device.pullModelConfig()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        LogUtil.e(TAG, "MQtt subscribe $downRaw failed, reason = ${exception?.message}.")
                    }
                })
            }
        }.onFailure { it.printStackTrace() }
    }

    /**
     * MQTT 重新连接
     */
    private fun reconnect() {
        if (mDisposable != null) return
        LogUtil.d(TAG, "start MQtt reconnect timer.")
        mDisposable = Observable.interval(10, 10, TimeUnit.SECONDS)
            .compose(RxScheduler.schedulerTransformer2())
            .filter { VIotUtil.isNetworkEnable(context) }
            .onTerminateDetach()
            .subscribe({
                LogUtil.d(TAG, "MQtt reconnect start.")
                connect()
            }, { LogUtil.e(TAG, it.message ?: "") })
    }

    /**
     * MQTT 状态回调
     */
    private val mMQttCallback: MqttCallback = object : MqttCallbackExtended {
        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            LogUtil.d(TAG, "MQtt connectComplete.")
            VIotRxBus.instance.post(VIotBusEvent.MSG_VIOT_CONNECT_CHANGE, true)
            mDisposable?.dispose()
            mDisposable = null
            kotlin.runCatching { subscribeTopic() }.onFailure { it.printStackTrace() }
        }

        override fun connectionLost(cause: Throwable?) {
            LogUtil.e(TAG, "MQtt connectionLost, reason: " + cause?.message)
            VIotRxBus.instance.post(VIotBusEvent.MSG_VIOT_CONNECT_CHANGE, false)
            val jsonObject = JSONObject()
            jsonObject.put("reason", cause?.message)
            if (jsonObject.length() > 0) {
                FileUtil.saveFileObject(context, "offlineReason", jsonObject.toString())
            }
            reconnect()
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            val payloadStr = String(message?.payload ?: byteArrayOf())
            LogUtil.d(TAG, "MQtt messageArrived, topic: $topic, qos: ${message?.qos}, retained: ${message?.isRetained}, payload: $payloadStr.")
            if (VIotUtil.isSubDeviceMsg(topic)) {
                VIotRxBus.instance.post(VIotBusEvent.MSG_EXTRA_MQTT_MESSAGE, payloadStr, topic)
            } else {
                val mQttMessage = parseAndGetReply(payloadStr, topic, device)
                mQttMessage?.let { publishTopic(it) }
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            LogUtil.d(TAG, "MQtt deliveryComplete.")
            takeIf { mMethod == DeviceMethod.METHOD_DEVICE_RESET_DOWN }?.run { VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_UNBIND) }
            mMethod = ""
        }
    }

    private val mIMQttActionListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            LogUtil.d(TAG, "MQtt connect success.")
            takeUnless { mHasInit }?.run {
                VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_BIND)
                mHasInit = true
            }
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            LogUtil.e(TAG, "MQtt connect fail, reason: ${exception?.message}: ${asyncActionToken?.exception}")
            if ((asyncActionToken?.exception?.reasonCode?.toShort() == MqttException.REASON_CODE_FAILED_AUTHENTICATION).or(asyncActionToken?.exception?.reasonCode?.toShort() == MqttException.REASON_CODE_NOT_AUTHORIZED)) {
                disconnect()
                VIotRxBus.instance.post(VIotBusEvent.MSG_RETRY_INITIALIZE)
            } else {
                reconnect()
            }
        }
    }

    companion object {
        private val TAG = MQTTClient::class.java.simpleName
    }
}

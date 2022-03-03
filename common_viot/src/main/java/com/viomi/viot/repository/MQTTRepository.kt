package com.viomi.viot.repository

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.alibaba.fastjson.JSON
import com.viomi.viot.BuildConfig
import com.viomi.viot.account.AccountMessage
import com.viomi.viot.action.VIotDeviceAction
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.device.DeviceMethod
import com.viomi.viot.device.VIotDevice
import com.viomi.viot.event.VIotDeviceEvent
import com.viomi.viot.https.ResultCodes
import com.viomi.viot.mqtt.MQTTTopic
import com.viomi.viot.mqtt.beans.ArrivedMessageBody
import com.viomi.viot.mqtt.beans.MQTTMessage
import com.viomi.viot.property.VIotDeviceProperty
import com.viomi.viot.push.PushMessage
import com.viomi.viot.rxjava2.VIotBusEvent
import com.viomi.viot.rxjava2.VIotRxBus
import com.viomi.viot.utils.FileUtil
import com.viomi.viot.utils.LogUtil
import com.viomi.viot.utils.SnowFlakeIdWorker
import com.viomi.viot.utils.VIotUtil
import com.viomi.viot.utils.encrypt.AESUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 * <pre>
 *     @author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/2
 *     desc   : MQTT 负载构造 Repository
 *     version: 1.0
 * </pre>
 */
object MQTTRepository {
    private val TAG = MQTTRepository::class.java.simpleName

    /**
     * 设备端请求消息
     */
    private const val DEVICE_REQUEST = "DM"

    /**
     * 设备端回复消息
     */
    private const val DEVICE_REPLY = "DR"

    internal fun parseAndGetReply(payload: String, topic: String?, device: VIotDevice): MQTTMessage? {
        var mQttMessage: MQTTMessage? = null
        kotlin.runCatching {
            val decipherResult = AESUtil.decrypt(payload, device.deviceAccessKey)
            Log.d(TAG, "Handle MQtt arrived result: $decipherResult.")
            val messageBody = JSON.parseObject(decipherResult, ArrivedMessageBody::class.java)
            when {
                messageBody.method == DeviceMethod.METHOD_SET_PROPERTIES -> {
                    val properties: List<VIotDeviceProperty> = JSON.parseArray(messageBody.params, VIotDeviceProperty::class.java)
                    VIotRxBus.instance.post(VIotBusEvent.MSG_SET_PROPERTY_LIST, properties)
                    mQttMessage = handleSetProperties(properties, device.deviceId, device.deviceAccessKey, messageBody.id)
                }
                messageBody.method == DeviceMethod.METHOD_ACTION -> {
                    val actions: List<VIotDeviceAction> = JSON.parseArray(messageBody.params, VIotDeviceAction::class.java)
                    device.pCacheActionMap[messageBody.id] = actions
                    messageBody.id?.let { mQttMessage = handleAction(actions, it) }
                }
                messageBody.method == DeviceMethod.METHOD_GET_PROPERTIES -> {
                    val properties: List<VIotDeviceProperty> = JSON.parseArray(messageBody.params, VIotDeviceProperty::class.java)
                    messageBody.id?.let { mQttMessage = handleGetProperties(properties, it) }
                }
                messageBody.reply == DeviceMethod.METHOD_PROPERTIES_CHANGED -> VIotRxBus.instance.post(VIotBusEvent.MSG_PROPERTIES_CHANGED, messageBody.id)
                messageBody.reply == DeviceMethod.METHOD_EVENT_OCCUR -> VIotRxBus.instance.post(VIotBusEvent.MSG_EVENT_OCCURRED, messageBody.id)
                messageBody.reply == DeviceMethod.METHOD_DEVICE_RESET_UP -> {
                    val result = JSONArray(messageBody.result)
                    for (i in 0 until result.length()) {
                        val did = result.optJSONObject(i)?.optString("did")
                        takeIf { did == device.deviceId }?.run { VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_UNBIND) }
                    }
                }
                messageBody.reply == DeviceMethod.METHOD_DEVICE_CONNECTION_CHECK -> {
                    val jsonObject = JSONObject(messageBody.result)
                    val code = jsonObject.optInt("code", 0)
                    VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_CONNECTION, code, messageBody.id)
                }
                messageBody.reply == DeviceMethod.METHOD_DEVICE_LOG_REPORT -> VIotRxBus.instance.post(VIotBusEvent.MSG_REPORT_LOG, messageBody.result)
                messageBody.method == DeviceMethod.METHOD_DEVICE_RESET_DOWN -> {
                    val jsonObject = JSONObject(messageBody.params)
                    val did = jsonObject.optString("did", "")
                    if (did == device.deviceId) {
                        mQttMessage = handleDeviceReset(did, device.deviceId, device.deviceAccessKey, messageBody.id)
                    } else {
                        VIotRxBus.instance.post(VIotBusEvent.MSG_EXTRA_MQTT_MESSAGE, decipherResult, topic)
                    }
                }
                messageBody.reply == DeviceMethod.METHOD_OTA_DEVICE_INFO -> LogUtil.d(TAG, "Upload device info success.")
                messageBody.method == DeviceMethod.METHOD_DEVICE_REMOTE_DEBUG -> {
                    VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_REMOTE_DEBUG, messageBody.params)
                    mQttMessage = handleRemoteDebug(device.deviceId, device.deviceAccessKey, messageBody.id)
                }
                messageBody.method == DeviceMethod.METHOD_DEVICE_DATA_REFRESH -> {
                    VIotRxBus.instance.post(VIotBusEvent.MSG_DEVICE_DATA_REFRESH, messageBody.params)
                    mQttMessage = handleDataRefresh(device.deviceId, device.deviceAccessKey, messageBody.id)
                }
                messageBody.method == DeviceMethod.METHOD_DEVICE_PUSH_MESSAGE -> {
                    val message = JSON.parseObject(messageBody.params, PushMessage::class.java)
                    VIotRxBus.instance.post(VIotBusEvent.MSG_PUSH_MESSAGE, message)
                    mQttMessage = handlePushMessage(device.deviceId, device.deviceAccessKey, messageBody.id)
                }
                messageBody.method == DeviceMethod.METHOD_CLOUD_CONNECTION_PING -> mQttMessage =
                    handleCloudPingMessage(device.deviceId, device.deviceAccessKey, messageBody.id)
                messageBody.method == DeviceMethod.METHOD_DEVICE_ACCOUNT_REFRESH -> {
                    val message = JSON.parseObject(messageBody.params, AccountMessage::class.java)
                    VIotRxBus.instance.post(VIotBusEvent.MSG_ACCOUNT_REFRESH, message)
                    mQttMessage = handleUserRefreshMessage(device.deviceId, device.deviceAccessKey, messageBody.id)
                }
                messageBody.reply == DeviceMethod.METHOD_DEVICE_CONNECTION_PING -> LogUtil.d(TAG, "Device connection ping success.")
                messageBody.reply == DeviceMethod.METHOD_PULL_MODEL_CONFIG -> {
                    device.isSupportViot = JSONObject(messageBody.result).optJSONObject("info")?.optBoolean("isSupportViot")
                    device.pSupportProperties.clear()
                    val jsonArray = JSONObject(messageBody.result).optJSONArray("productServices")
                    for (i in 0 until (jsonArray?.length() ?: 0)) {
                        val jsonObject = jsonArray?.optJSONObject(i)
                        val siid = jsonObject?.optInt("siid") ?: 0
                        if (device.pSupportProperties[siid] == null) {
                            device.pSupportProperties[siid] = ArrayList()
                        }
                        device.pSupportProperties[siid]?.add(jsonObject?.optInt("piid") ?: 0)
                    }
                    device.reportAllProperties()
                    VIotRxBus.instance.post(VIotBusEvent.MSG_MODEL_CONFIG_GET, device.isSupportViot)
                }
                else -> {
                    VIotRxBus.instance.post(VIotBusEvent.MSG_EXTRA_MQTT_MESSAGE, decipherResult, topic)
                    mQttMessage = null
                }
            }
        }.onFailure {
            it.printStackTrace()
            LogUtil.e(TAG, "Handle MQtt arrived message parse error, reason: ${it.message}.")
        }
        return mQttMessage
    }

    internal fun createPropertiesChangedMessage(properties: Map<String, Any?>?, deviceId: String?, accessKey: String?): Pair<String, MQTTMessage> {
        val idWorker = SnowFlakeIdWorker(2, 3)
        val id = idWorker.nextId()
        val idStr = "$DEVICE_REQUEST$id"
        val jsonObject = JSONObject()
        jsonObject.put("id", idStr)
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        jsonObject.put("method", DeviceMethod.METHOD_PROPERTIES_CHANGED)
        val jsonArray = JSONArray()
        properties?.forEach {
            val strings = it.key.split(".")
            val paramJsonObject = JSONObject()
            paramJsonObject.put("did", deviceId)
            paramJsonObject.put("siid", strings[0].toInt())
            paramJsonObject.put("piid", strings[1].toInt())
            paramJsonObject.put("value", it.value)
            jsonArray.put(paramJsonObject)
        }
        jsonObject.put("params", jsonArray)
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, deviceId, deviceId)
        Log.d(TAG, "createPropertiesChangedMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return Pair(id.toString(), MQTTMessage(topicName, payload, DeviceMethod.METHOD_PROPERTIES_CHANGED))
    }

    internal fun createEventMessage(events: List<VIotDeviceEvent>, deviceId: String?, accessKey: String?): Pair<String, MQTTMessage> {
        val idWorker = SnowFlakeIdWorker(2, 3)
        val id = idWorker.nextId()
        val idStr = "$DEVICE_REQUEST$id"
        val jsonObject = JSONObject()
        jsonObject.put("id", idStr)
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        jsonObject.put("method", DeviceMethod.METHOD_EVENT_OCCUR)
        val params = JSONArray()
        events.forEach {
            val paramJsonObject = JSONObject()
            paramJsonObject.put("did", it.did)
            paramJsonObject.put("eiid", it.eiid)
            paramJsonObject.put("siid", it.siid)
            val arguments = JSONArray()
            it.properties?.forEach { property ->
                val argument = JSONObject()
                argument.put("piid", property.piid)
                argument.put("value", property.value)
                arguments.put(argument)
            }
            paramJsonObject.put("arguments", arguments)
            params.put(paramJsonObject)
        }
        jsonObject.put("params", params)
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, deviceId, deviceId)
        Log.d(TAG, "createEventMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return Pair(id.toString(), MQTTMessage(topicName, payload, DeviceMethod.METHOD_EVENT_OCCUR))
    }

    internal fun createDeviceResetMessage(deviceId: String?, accessKey: String?): MQTTMessage {
        val idWorker = SnowFlakeIdWorker(2, 3)
        val id = idWorker.nextId()
        val idStr = "$DEVICE_REQUEST$id"
        val jsonObject = JSONObject()
        jsonObject.put("id", idStr)
        jsonObject.put("method", DeviceMethod.METHOD_DEVICE_RESET_UP)
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        jsonObject.put("timestamp", System.currentTimeMillis())
        val paramJsonObject = JSONObject()
        paramJsonObject.put("did", deviceId)
        jsonObject.put("params", paramJsonObject)
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, deviceId, deviceId)
        Log.d(TAG, "createDeviceResetMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_RESET_UP)
    }

    internal fun createLogReportMessage(message: String, deviceId: String?, accessKey: String?): MQTTMessage {
        val idWorker = SnowFlakeIdWorker(2, 3)
        val id = idWorker.nextId()
        val idStr = "$DEVICE_REQUEST$id"
        val jsonObject = JSONObject()
        jsonObject.put("id", idStr)
        jsonObject.put("method", DeviceMethod.METHOD_DEVICE_LOG_REPORT)
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        val jsonArray = JSONArray()
        jsonArray.put(message)
        jsonObject.put("param", jsonArray)
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, deviceId, deviceId)
        Log.d(TAG, "createLogReportMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_LOG_REPORT)
    }

    internal fun createCheckConnectMessage(deviceId: String?, accessKey: String?): Pair<String, MQTTMessage> {
        val idWorker = SnowFlakeIdWorker(2, 3)
        val id = idWorker.nextId()
        val idStr = "$DEVICE_REQUEST$id"
        val jsonObject = JSONObject()
        jsonObject.put("id", idStr)
        jsonObject.put("method", DeviceMethod.METHOD_DEVICE_CONNECTION_CHECK)
        jsonObject.put("gatewayDid", deviceId)
        jsonObject.put("timestamp", System.currentTimeMillis().toString())
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        val jsonArray = JSONArray()
        val jsonParams = JSONObject()
        jsonParams.put("did", deviceId)
        jsonArray.put(jsonParams)
        jsonObject.put("params", jsonArray)
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, deviceId, deviceId)
        Log.d(TAG, "createCheckConnectMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return Pair(id.toString(), MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_CONNECTION_CHECK))
    }

    internal fun createDeviceInfoMessage(config: VIotDeviceConfig, context: Context, map: MutableMap<String, String>?): MQTTMessage {
        val idWorker = SnowFlakeIdWorker(2, 3)
        val id = idWorker.nextId()
        val idStr = "$DEVICE_REQUEST$id"
        val jsonObject = JSONObject()
        jsonObject.put("id", idStr)
        jsonObject.put("method", DeviceMethod.METHOD_OTA_DEVICE_INFO)
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        val jsonArray = JSONArray()
        val jsonParam = JSONObject()
        jsonParam.put("did", config.deviceId)
        jsonParam.put("pid", config.productId)
        jsonParam.put("life", SystemClock.elapsedRealtime() / 1000)
        jsonParam.put("mac", config.mac)
        jsonParam.put("model", config.model)
        jsonParam.put("spec_ver", "1.0")
        jsonParam.put("mmfree", FileUtil.getSDCardFreeSize())
        val jsonAp = JSONObject()
        val wifiManager = context.applicationContext?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val wifiInfo = wifiManager?.connectionInfo
        jsonAp.put("rssi", wifiInfo?.rssi)
        jsonAp.put("ssid", wifiInfo?.ssid?.replace("\"", ""))
        jsonAp.put("bssid", wifiInfo?.bssid)
        jsonParam.put("ap", jsonAp)
        val jsonNetif = JSONObject()
        jsonNetif.put("local_ip", VIotUtil.getIpAddress(context))
        jsonNetif.put("mask", VIotUtil.getMaskAddress("eth0"))
        jsonNetif.put("gw", VIotUtil.getGateway())
        jsonParam.put("netif", jsonNetif)
        val jsonModuleInfo = JSONArray()
        val infoParam = JSONObject()
        infoParam.put("fw_name", VIotUtil.get(context, "ro.board.platform"))
        infoParam.put("fw_ver", context.applicationContext.packageManager.getPackageInfo(context.packageName, 0).versionName)
        val infoParam2 = JSONObject()
        infoParam2.put("fw_name", "Viot Sdk")
        infoParam2.put("fw_ver", BuildConfig.VERSION_NAME)
        val infoParam3 = JSONObject()
        infoParam3.put("fw_name", "os_ver")
        infoParam3.put("fw_ver", Build.DISPLAY)
        jsonModuleInfo.put(infoParam)
        jsonModuleInfo.put(infoParam2)
        jsonModuleInfo.put(infoParam3)
        map?.forEach {
            val info = JSONObject()
            info.put("fw_name", it.key)
            info.put("fw_ver", it.value)
            jsonModuleInfo.put(info)
        }
        jsonParam.put("module_info", jsonModuleInfo)
        jsonArray.put(jsonParam)
        jsonObject.put("params", jsonArray)
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, config.deviceId, config.deviceId)
        Log.d(TAG, "createDeviceInfoMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), config.deviceAccessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_OTA_DEVICE_INFO)
    }

    internal fun createActionOutMessage(actions: List<VIotDeviceAction>, deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject()
        jsonObject.put("id", id?.replace("CM", DEVICE_REPLY))
        jsonObject.put("reply", DeviceMethod.METHOD_ACTION)
        val jsonArray = JSONArray()
        actions.forEach {
            val paramJsonObject = JSONObject()
            paramJsonObject.put("code", ResultCodes.RESULT_CODE_30)
            paramJsonObject.put("did", it.did)
            paramJsonObject.put("aiid", it.aiid)
            paramJsonObject.put("siid", it.siid)
            val outJsonArray = JSONArray()
            it.outList?.forEach { `in` ->
                val outJsonObject = JSONObject()
                outJsonObject.put("piid", `in`.piid)
                outJsonObject.put("value", `in`.value)
                outJsonArray.put(outJsonObject)
            }
            paramJsonObject.put("out", outJsonArray)
            jsonArray.put(paramJsonObject)
        }
        jsonObject.put("result", jsonArray)
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleAction reply: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_ACTION)
    }

    internal fun createGetPropertiesReplyMessage(properties: List<VIotDeviceProperty>?, deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject()
        jsonObject.put("id", id?.replace("CM", DEVICE_REPLY))
        jsonObject.put("reply", DeviceMethod.METHOD_GET_PROPERTIES)
        val jsonArray = JSONArray()
        properties?.forEach {
            val paramJsonObject = JSONObject()
            paramJsonObject.put("code", ResultCodes.RESULT_CODE_30)
            paramJsonObject.put("did", it.did)
            paramJsonObject.put("piid", it.piid)
            paramJsonObject.put("siid", it.siid)
            paramJsonObject.put("value", it.value)
            jsonArray.put(paramJsonObject)
        }
        jsonObject.put("result", jsonArray)
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleGetProperties reply: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_GET_PROPERTIES)
    }

    internal fun createModelConfigMessage(deviceId: String?, accessKey: String?): MQTTMessage {
        val jsonObject = JSONObject().apply {
            val idWorker = SnowFlakeIdWorker(2, 3)
            val id = idWorker.nextId()
            val idStr = "$DEVICE_REQUEST$id"
            put("id", idStr)
            put("version", BuildConfig.VERSION_NAME)
            put("method", DeviceMethod.METHOD_PULL_MODEL_CONFIG)
            put("timestamp", System.currentTimeMillis())
        }
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, deviceId, deviceId)
        Log.d(TAG, "createModelConfigMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_PULL_MODEL_CONFIG)
    }

    internal fun createDevicePingMessage(deviceId: String?, accessKey: String?): MQTTMessage {
        val jsonObject = JSONObject().apply {
            val idWorker = SnowFlakeIdWorker(2, 3)
            val id = idWorker.nextId()
            val idStr = "$DEVICE_REQUEST$id"
            put("id", idStr)
            put("method", DeviceMethod.METHOD_DEVICE_CONNECTION_PING)
            put("timestamp", System.currentTimeMillis())
            put("version", BuildConfig.VERSION_NAME)
            val jsonArray = JSONArray()
            val resultJson = JSONObject()
            resultJson.put("did", deviceId)
            jsonArray.put(resultJson)
            put("params", jsonArray)
        }
        val topicName = String.format(MQTTTopic.PROD_UP_RAW, deviceId, deviceId)
        Log.d(TAG, "createDevicePingMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_CONNECTION_PING)
    }

    private fun handleSetProperties(
        list: List<VIotDeviceProperty>,
        deviceId: String?,
        accessKey: String?,
        id: String?
    ): MQTTMessage {
        val jsonObject = JSONObject()
        jsonObject.put("id", id?.replace("CM", DEVICE_REPLY))
        jsonObject.put("reply", DeviceMethod.METHOD_SET_PROPERTIES)
        val jsonArray = JSONArray()
        list.forEach {
            VIotRxBus.instance.post(VIotBusEvent.MSG_SET_PROPERTIES, it)
            val paramJsonObject = JSONObject()
            paramJsonObject.put("did", it.did)
            paramJsonObject.put("piid", it.piid)
            paramJsonObject.put("siid", it.siid)
            paramJsonObject.put("code", ResultCodes.RESULT_CODE_30)
            jsonArray.put(paramJsonObject)
        }
        jsonObject.put("result", jsonArray)
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleSetProperties result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_SET_PROPERTIES)
    }

    private fun handleAction(actions: List<VIotDeviceAction>, id: String): MQTTMessage? {
        actions.forEach {
            it.id = id
            VIotRxBus.instance.post(VIotBusEvent.MSG_ACTION, it)
        }
        return null
    }

    private fun handleGetProperties(properties: List<VIotDeviceProperty>, id: String?): MQTTMessage? {
        VIotRxBus.instance.post(VIotBusEvent.MSG_GET_PROPERTIES, properties, id)
        return null
    }

    private fun handleInvalidRequest(deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject()
        jsonObject.put("id", id?.replace("CM", DEVICE_REPLY))
        val errorJsonObject = JSONObject()
        errorJsonObject.put("code", ResultCodes.RESULT_CODE_29)
        errorJsonObject.put("message", "Invalid Request")
        jsonObject.put("error", errorJsonObject)
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleInvalidRequest result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, "")
    }

    private fun handleDeviceReset(did: String, deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject()
        jsonObject.put("id", id?.replace("CM", DEVICE_REPLY))
        jsonObject.put("reply", DeviceMethod.METHOD_DEVICE_RESET_DOWN)
        val jsonArray = JSONArray()
        val resultJson = JSONObject()
        resultJson.put("code", ResultCodes.RESULT_CODE_1)
        resultJson.put("did", did)
        jsonArray.put(resultJson)
        jsonObject.put("result", jsonArray)
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleDeviceReset result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_RESET_DOWN)
    }

    private fun handleRemoteDebug(deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject()
        jsonObject.put("id", id?.replace("CM", DEVICE_REPLY))
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        jsonObject.put("reply", DeviceMethod.METHOD_DEVICE_REMOTE_DEBUG)
        val jsonArray = JSONArray()
        val resultJson = JSONObject()
        resultJson.put("code", ResultCodes.RESULT_CODE_1)
        jsonArray.put(resultJson)
        jsonObject.put("result", jsonArray)
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleRemoteDebug result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_REMOTE_DEBUG)
    }

    private fun handleDataRefresh(deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject()
        jsonObject.put("id", id?.replace("CM", DEVICE_REPLY))
        jsonObject.put("version", BuildConfig.VERSION_NAME)
        jsonObject.put("reply", DeviceMethod.METHOD_DEVICE_DATA_REFRESH)
        val jsonArray = JSONArray()
        val resultJson = JSONObject()
        resultJson.put("code", ResultCodes.RESULT_CODE_1)
        jsonArray.put(resultJson)
        jsonObject.put("result", jsonArray)
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleDataRefresh result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_DATA_REFRESH)
    }

    private fun handlePushMessage(deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject().apply {
            put("id", id?.replace("CM", DEVICE_REPLY))
            put("version", BuildConfig.VERSION_NAME)
            put("reply", DeviceMethod.METHOD_DEVICE_PUSH_MESSAGE)
            val jsonArray = JSONArray()
            val resultJson = JSONObject()
            resultJson.put("code", ResultCodes.RESULT_CODE_1)
            jsonArray.put(resultJson)
            put("result", jsonArray)
        }
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handlePushMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_PUSH_MESSAGE)
    }

    private fun handleCloudPingMessage(deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject().apply {
            put("id", id?.replace("CM", DEVICE_REPLY))
            put("version", BuildConfig.VERSION_NAME)
            put("reply", DeviceMethod.METHOD_CLOUD_CONNECTION_PING)
            put("timestamp", System.currentTimeMillis())
            val jsonArray = JSONArray()
            val resultJson = JSONObject()
            resultJson.put("code", ResultCodes.RESULT_CODE_1)
            resultJson.put("did", deviceId)
            jsonArray.put(resultJson)
            put("result", jsonArray)
        }
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleCloudPingMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_CLOUD_CONNECTION_PING)
    }

    private fun handleUserRefreshMessage(deviceId: String?, accessKey: String?, id: String?): MQTTMessage {
        val jsonObject = JSONObject().apply {
            put("id", id?.replace("CM", DEVICE_REPLY))
            put("version", BuildConfig.VERSION_NAME)
            put("reply", DeviceMethod.METHOD_DEVICE_ACCOUNT_REFRESH)
            put("timestamp", System.currentTimeMillis())
            val jsonArray = JSONArray()
            val resultJson = JSONObject()
            resultJson.put("code", ResultCodes.RESULT_CODE_1)
            jsonArray.put(resultJson)
            put("result", jsonArray)
        }
        val topicName = String.format(MQTTTopic.PROP_DOWN_RAW_REPLY, deviceId, deviceId)
        Log.d(TAG, "handleUserRefreshMessage result: $jsonObject.")
        val payload = AESUtil.aesEncode(jsonObject.toString(), accessKey)
        return MQTTMessage(topicName, payload, DeviceMethod.METHOD_DEVICE_ACCOUNT_REFRESH)
    }
}

package com.viomi.viot.device

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/3
 *     desc   : MQTT 设备相关方法
 *     version: 1.0
 * </pre>
 */
object DeviceMethod {
    const val METHOD_PROPERTIES_CHANGED = "properties_changed"
    const val METHOD_SET_PROPERTIES = "set_properties"
    const val METHOD_GET_PROPERTIES = "get_properties"
    const val METHOD_EVENT_OCCUR = "event_occured"
    const val METHOD_ACTION = "action"
    const val METHOD_DEVICE_RESET_DOWN = "dev.reset"
    const val METHOD_DEVICE_RESET_UP = "dev.reset.report"
    const val METHOD_DEVICE_LOG_REPORT = "dev.log.report"
    const val METHOD_DEVICE_CONNECTION_CHECK = "dev.connection.check"
    const val METHOD_OTA_DEVICE_INFO = "ota.dev.info"
    const val METHOD_DEVICE_REMOTE_DEBUG = "dev.remote.debug"
    const val METHOD_DEVICE_DATA_REFRESH = "dev.data.refresh"
    const val METHOD_DEVICE_PUSH_MESSAGE = "dev.push.message"
    const val METHOD_DEVICE_CONNECTION_PING = "dev.connection.ping"
    const val METHOD_CLOUD_CONNECTION_PING = "cloud.connection.ping"
    const val METHOD_DEVICE_ACCOUNT_REFRESH = "dev.account.refresh"
    const val METHOD_PULL_MODEL_CONFIG = "pull.model.config.info"
}

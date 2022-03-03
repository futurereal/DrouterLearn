package com.viomi.viot.rxjava2

import com.viomi.viot.BuildConfig

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/03
 *     desc   : RxBus 事件定义
 *     version: 1.0
 * </pre>
 */
class VIotBusEvent internal constructor(val msgId: String, val msgObject: Any?, val extraObject: Any?) {

    companion object {
        const val MSG_DEVICE_BIND = "${BuildConfig.LIBRARY_PACKAGE_NAME}.BIND"
        const val MSG_DEVICE_UNBIND = "${BuildConfig.LIBRARY_PACKAGE_NAME}.UNBIND"
        const val MSG_SET_PROPERTIES = "${BuildConfig.LIBRARY_PACKAGE_NAME}.SET_PROPERTIES"
        const val MSG_SET_PROPERTY_LIST = "${BuildConfig.LIBRARY_PACKAGE_NAME}.SET_PROPERTY_LIST"
        const val MSG_GET_PROPERTIES = "${BuildConfig.LIBRARY_PACKAGE_NAME}.GET_PROPERTIES"
        const val MSG_ACTION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.ACTION"
        const val MSG_EVENT_OCCURRED = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EVENT_OCCURRED"
        const val MSG_PROPERTIES_CHANGED = "${BuildConfig.LIBRARY_PACKAGE_NAME}.PROPERTIES_CHANGED"
        const val MSG_DEVICE_CONNECTION = "${BuildConfig.LIBRARY_PACKAGE_NAME}.DEVICE_CONNECTION"
        const val MSG_REPORT_LOG = "${BuildConfig.LIBRARY_PACKAGE_NAME}.REPORT_LOG"
        const val MSG_NETWORK_ENABLE = "${BuildConfig.LIBRARY_PACKAGE_NAME}.NETWORK_ENABLE"
        const val MSG_RETRY_INITIALIZE = "${BuildConfig.LIBRARY_PACKAGE_NAME}.RETRY_INITIALIZE"
        const val MSG_DEVICE_REMOTE_DEBUG = "${BuildConfig.LIBRARY_PACKAGE_NAME}.DEVICE_REMOTE_DEBUG"
        const val MSG_DEVICE_DATA_REFRESH = "${BuildConfig.LIBRARY_PACKAGE_NAME}.DATA_REFRESH"
        const val MSG_PUSH_MESSAGE = "${BuildConfig.LIBRARY_PACKAGE_NAME}.PUSH_MESSAGE"
        const val MSG_ACCOUNT_REFRESH = "${BuildConfig.LIBRARY_PACKAGE_NAME}.ACCOUNT_REFRESH"
        const val MSG_EXTRA_MQTT_MESSAGE = "${BuildConfig.LIBRARY_PACKAGE_NAME}.EXTRA_MQTT_MESSAGE"
        const val MSG_VIOT_CONNECT_CHANGE = "${BuildConfig.LIBRARY_PACKAGE_NAME}.VIOT_CONNECT_CHANGE"
        const val MSG_MODEL_CONFIG_GET = "${BuildConfig.LIBRARY_PACKAGE_NAME}.MODEL_CONFIG_GET"
    }
}

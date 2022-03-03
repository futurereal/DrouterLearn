package com.viomi.viot.mqtt

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/27
 *     desc   : MQTT topic
 *     version: 1.0
 * </pre>
 */
object MQTTTopic {
    const val PROD_UP_RAW = "viot_up_raw/%1\$s/sub/%2\$s/thing" // 用于设备上报消息。设备操作权限：发布
    const val PROD_UP_RAW_REPLY = "viot_up_raw_reply/%1\$s/#" // 用于云端回复设备上报消息。设备操作权限：订阅
    const val PROD_DOWN_RAW = "viot_down_raw/%1\$s/#" // 用于云端下发指令给设备。设备操作权限：订阅
    const val PROP_DOWN_RAW_REPLY = "viot_down_raw_reply/%1\$s/sub/%2\$s/thing" // 用于设备端回复云端指令。设备操作权限：发布
    const val PROP_DOWN_RAW_SUB = "viot_down_raw/%1\$s/sub/%2\$s/thing/" // 云端回复设备的 topic
    const val TEST_UP_RAW_REPLY = "test_viot_up_raw_reply/%1\$s/#" // 用于云端回复设备上报消息。设备操作权限：订阅
    const val TEST_DOWN_RAW = "test_viot_down_raw/%1\$s/#" // 用于云端下发指令给设备。设备操作权限：订阅
    const val TEST_UP_RAW = "test_viot_up_raw/%1\$s/sub/%1\$s/thing" // 用于设备上报消息。设备操作权限：发布
    const val TEST_DOWN_RAW_REPLY = "test_viot_down_raw_reply/%1\$s/sub/%1\$s/thing" // 用于设备端回复云端指令。设备操作权限：发布
}

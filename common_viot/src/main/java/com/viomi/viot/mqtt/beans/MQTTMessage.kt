package com.viomi.viot.mqtt.beans

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/02
 *     desc   : MQTT 消息封装
 *     version: 1.0
 * </pre>
 */
data class MQTTMessage(val topicName: String, val payload: String, val method: String)

package com.viomi.viot.mqtt.beans

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/03
 *     desc   : 接收的 MQTT 消息
 *     version: 1.0
 * </pre>
 */
class ArrivedMessageBody : Serializable {
    @JSONField(name = "id")
    var id: String? = null

    @JSONField(name = "method")
    var method: String? = null

    @JSONField(name = "reply")
    var reply: String? = null

    @JSONField(name = "timestamp")
    var timestamp: String? = null

    @JSONField(name = "version")
    var version: String? = null

    @JSONField(name = "params")
    var params: String = ""

    @JSONField(name = "result")
    var result: String = ""
}

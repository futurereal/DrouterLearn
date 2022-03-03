package com.viomi.viot.https.beans

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/25
 *     desc   : MQTT 配置实体
 *     version: 1.0
 * </pre>
 */
class MQTTConfigBean : Serializable {
    @JSONField(name = "accessKey")
    var accessKey: String? = null

    @JSONField(name = "dids")
    var dids: List<String>? = null

    @JSONField(name = "endpoint")
    var endpoint: String? = null

    @JSONField(name = "groupId")
    var groupId: String? = null

    @JSONField(name = "instanceId")
    var instanceId: String? = null

    @JSONField(name = "token")
    var tokens: List<Token>? = null

    var isParse = false
    var code = 0
    var desc: String? = null

    class Token : Serializable {
        @JSONField(name = "model")
        var model: String? = null

        @JSONField(name = "value")
        var value: String? = null
    }
}

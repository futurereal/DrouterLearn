package com.viomi.iotdevice.iotmesh.entity

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable
import java.util.Date

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/12/4
 *     desc   : WiFi to Android Device mesh network entity.
 *     version: 1.0
 * </pre>
 */
class WiFiToDeviceMeshEntity : Serializable {
    @JSONField(name = "mobBaseRes")
    var mobBaseRes: MobBaseRes? = null

    class MobBaseRes : Serializable {
        @JSONField(name = "code")
        var code = 0

        @JSONField(name = "desc")
        var desc: String? = null

        @JSONField(name = "result")
        var result: Result? = null
    }

    class Result : Serializable {
        @JSONField(name = "userViomiInfoBean")
        var userViomiInfoBean: UserViomiInfoBean? = null

        @JSONField(name = "token")
        var token: String? = null
    }

    class UserViomiInfoBean : Serializable {
        @JSONField(name = "id")
        var id = 0

        @JSONField(name = "userId")
        var userId: Long? = null

        @JSONField(name = "creator")
        var creator: String? = null

        @JSONField(name = "status")
        var status = 0

        @JSONField(name = "updateTime")
        var updateTime: Date? = null

        @JSONField(name = "createTime")
        var createTime: Date? = null

        @JSONField(name = "pwd")
        var pwd: String? = null

        @JSONField(name = "nickName")
        var nickName: String? = null

        @JSONField(name = "mobile")
        var mobile: String? = null

        @JSONField(name = "account")
        var account: String? = null

        @JSONField(name = "headImg")
        var headImg: String? = null

        @JSONField(name = "gender")
        var gender = 0

        @JSONField(name = "email")
        var email: String? = null

        @JSONField(name = "openId")
        var openId: String? = null
    }
}

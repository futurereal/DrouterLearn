package com.viomi.iotdevice.iotmesh.entity

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : Android Device To WiFi mesh network entity.
 *     version: 1.0
 * </pre>
 */
class DeviceToWiFiMeshEntity : Serializable {
    @JSONField(name = "mobBaseRes")
    var loginQRCode: QRCodeLogin? = null

    class QRCodeLogin : Serializable {
        @JSONField(name = "code")
        var code = 0

        @JSONField(name = "desc")
        var desc: String? = null

        @JSONField(name = "result")
        var result: String? = null

        @JSONField(name = "token")
        var token: String? = null

        @JSONField(name = "LoginData")
        var loginResult: QRCodeLoginResult? = null

        @JSONField(name = "appendAttr")
        var appendAttr: String? = null

        var userInfo: UserInfo? = null

        fun parseAppendAttr() {
            userInfo = JSON.parseObject(appendAttr, UserInfo::class.java)
        }
    }

    class UserInfo : Serializable {
        @JSONField(name = "nikeName")
        var nickname: String? = null

        @JSONField(name = "headImg")
        var headImg: String? = null

        @JSONField(name = "account")
        var account: String? = null

        @JSONField(name = "mobile")
        var mobile: String? = null

        @JSONField(name = "cid")
        var cid = 0

        @JSONField(name = "gender")
        var gender = -1

        @JSONField(name = "xiaomi")
        var miUserInfo: MiUserInfo? = null

        var token: String? = null

        var userCode: String? = null
    }

    class QRCodeLoginResult : Serializable {
        @JSONField(name = "accountType")
        var accountType = 0

        @JSONField(name = "loginType")
        var loginType = 0

        @JSONField(name = "userId")
        var userId = 0

        @JSONField(name = "userCode")
        var userCode: String? = null

        @JSONField(name = "roles")
        var roles: List<String>? = null

        @JSONField(name = "headImg")
        var headImg: String? = null
    }

    class MiUserInfo : Serializable {
        @JSONField(name = "miId")
        var miId: String? = null

        @JSONField(name = "accessToken")
        var accessToken: String? = null

        @JSONField(name = "userId")
        var userId: String? = null

        @JSONField(name = "type")
        var type: String? = null

        @JSONField(name = "mExpiresIn")
        private var mExpiresIn: Long = 0

        @JSONField(name = "macKey")
        var macKey: String? = null

        @JSONField(name = "macAlgorithm")
        var macAlgorithm: String? = null

        fun getmExpiresIn(): Long {
            return mExpiresIn
        }

        fun setmExpiresIn(mExpiresIn: Long) {
            this.mExpiresIn = mExpiresIn
        }
    }
}
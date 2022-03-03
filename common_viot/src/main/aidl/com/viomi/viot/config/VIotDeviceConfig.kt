package com.viomi.viot.config

import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField
import com.viomi.viot.utils.VIotUtil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Created by William on 2020/5/13.
 */
@Parcelize
data class VIotDeviceConfig(
    @JSONField(name = "pid") var productId: Int?,
    var token: String?,
    var userId: String?,
    @JSONField(name = "did") var deviceId: String?,
    @JSONField(name = "deviceAccessKey") var deviceAccessKey: String?,
    @JSONField(name = "cloudPublicKey") var cloudPublicKey: String?,
    var manufacture: String?,
    var model: String?,
    var miDid: String? = null,
    var needVerify: Boolean = true
) : Parcelable {

    @IgnoredOnParcel
    var hasTriad: Boolean = true

    @IgnoredOnParcel
    var miModel: String? = null

    @IgnoredOnParcel
    internal var mac: String = VIotUtil.getMacAddress()

    constructor(hasTriad: Boolean) : this(null, null, null, null, null, null, null, null, null) {
        this.hasTriad = hasTriad
    }
}

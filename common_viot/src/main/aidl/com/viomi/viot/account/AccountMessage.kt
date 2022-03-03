package com.viomi.viot.account

import android.os.Parcel
import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField
import kotlinx.parcelize.Parcelize

/**
 * 账号刷新消息
 *
 * @author William
 * @date 2021/3/16
 */
@Parcelize
data class AccountMessage(
    @JSONField(name = "viotAccountId") val viotAccountId: String?,
    @JSONField(name = "miAccountId") val miAccountId: String?,
    @JSONField(name = "plateform") val plateform: ArrayList<Int>?,
    @JSONField(name = "handleType") val handleType: Int?
) : Parcelable {

    fun readFromParcel(dest: Parcel?) {

    }
}

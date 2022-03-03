package com.viomi.viot.push

import android.os.Parcel
import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField
import kotlinx.parcelize.Parcelize


/**
 * 推送消息
 *
 * @author William
 * @date 2020/12/22
 */
@Parcelize
data class PushMessage(
    @JSONField(name = "title") val title: String?,
    @JSONField(name = "content") val content: String?,
    @JSONField(name = "payload") val payload: String?
) : Parcelable {

    fun readFromParcel(dest: Parcel?) {

    }
}

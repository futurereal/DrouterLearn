package com.viomi.viot.property

import android.os.Parcel
import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Created by William on 2020/6/2.
 */
@Parcelize
data class VIotDeviceProperty(
    @JSONField(name = "siid") var siid: Int?,
    @JSONField(name = "piid") var piid: Int?,
    @JSONField(name = "value") var value: @RawValue Any?,
    @JSONField(name = "did") var did: String?
) : Parcelable {

    fun readFromParcel(dest: Parcel?) {

    }
}

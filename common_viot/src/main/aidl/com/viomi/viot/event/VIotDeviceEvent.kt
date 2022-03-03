package com.viomi.viot.event

import android.os.Parcel
import android.os.Parcelable
import com.viomi.viot.property.VIotDeviceProperty
import kotlinx.parcelize.Parcelize

/**
 * Created by William on 2020/6/3.
 */
@Parcelize
data class VIotDeviceEvent(
    var siid: Int?,
    var eiid: Int?,
    var properties: ArrayList<VIotDeviceProperty>?,
    var did: String?
) : Parcelable {

    fun readFromParcel(dest: Parcel?) {

    }
}

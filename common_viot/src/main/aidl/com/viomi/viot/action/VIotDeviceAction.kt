package com.viomi.viot.action

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.ObservableArrayList
import com.alibaba.fastjson.annotation.JSONField
import com.viomi.viot.listener.OnActionOutChangeListener
import com.viomi.viot.property.VIotDeviceProperty
import kotlinx.parcelize.Parcelize


/**
 * Created by William on 2020/6/4.
 */
@Parcelize
data class VIotDeviceAction(
    @JSONField(name = "aiid") var aiid: Int?,
    @JSONField(name = "siid") var siid: Int?,
    @JSONField(name = "in") var inList: ArrayList<VIotDeviceProperty>?,
    @JSONField(name = "did") var did: String?,
    var outList: ArrayList<VIotDeviceProperty>? = null,
    internal var id: String? = null
) : Parcelable {

    internal var onActionOutChangeListener: OnActionOutChangeListener? = null

    fun readFromParcel(dest: Parcel?) {

    }

    fun replyOut(list: MutableList<VIotDeviceProperty>?) {
        if (!outList.isNullOrEmpty()) return
        outList = ObservableArrayList()
        list?.let { outList?.addAll(it) }
        onActionOutChangeListener?.onListChange()
    }
}

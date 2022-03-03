package com.viomi.viot.listener

import com.viomi.viot.property.VIotDeviceProperty

/**
 * set_properties 下发回调接口
 * Created by William on 2020/6/3.
 */
interface OnSetPropertiesListener {
    fun onSetProperty(property: VIotDeviceProperty?)
    fun onFailed(code: Int, message: String?)
}

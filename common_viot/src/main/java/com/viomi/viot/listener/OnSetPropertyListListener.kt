package com.viomi.viot.listener

import com.viomi.viot.property.VIotDeviceProperty

/**
 * set_properties 下发回调接口
 *
 * @author William
 * @date 2021/3/17
 */
interface OnSetPropertyListListener {
    fun onSetProperty(property: MutableList<VIotDeviceProperty>?)
    fun onFailed(code: Int, message: String?)
}

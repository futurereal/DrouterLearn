package com.viomi.viot.listener

import com.viomi.viot.property.VIotDeviceProperty

/**
 * get_properties 下发回调接口
 *
 * @author William
 * @date 2021/4/15
 */
interface OnGetPropertiesListener {
    fun onGetProperty(properties: MutableList<VIotDeviceProperty>?): MutableList<VIotDeviceProperty>?
    fun onFailed(code: Int, message: String?)
}

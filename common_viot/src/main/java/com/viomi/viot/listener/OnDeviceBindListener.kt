package com.viomi.viot.listener

import com.viomi.viot.config.VIotDeviceConfig

/**
 * 设备绑定状态回调接口
 * Created by William on 2020/6/15.
 */
interface OnDeviceBindListener {
    fun onSucceed(config: VIotDeviceConfig)
    fun onFailed(code: Int, message: String?)
    fun onBind()
    fun onUnBind()
}

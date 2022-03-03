package com.viomi.viot.listener

/**
 * 设备验证合法性回调接口
 * Created by William on 2020/6/2.
 */
interface OnDeviceVerifyListener {
    fun onLegal()
    fun onIllegal(code: Int, reason: String?)
}

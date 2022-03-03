package com.viomi.viot.listener

import com.viomi.viot.action.VIotDeviceAction

/**
 * Action 下发回调接口
 * Created by William on 2020/6/4.
 */
interface OnActionListener {
    fun onAction(action: VIotDeviceAction?)
    fun onFailed(code: Int, message: String?)
}

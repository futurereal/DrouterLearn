package com.viomi.viot.listener

/**
 * 远程调试监听接口
 *
 * @author William
 * @date 2020/12/22
 */
interface OnRemoteDebugListener {
    fun remoteMessage(message: String?)
}

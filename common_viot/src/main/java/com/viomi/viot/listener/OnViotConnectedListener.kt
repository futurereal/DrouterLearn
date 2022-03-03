package com.viomi.viot.listener

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2021/12/14
 *     desc   : viot 连接结果
 *     version: 1.0
 * </pre>
 */
interface OnViotConnectedListener {
    fun onConnected()

    fun onDisconnected()
}
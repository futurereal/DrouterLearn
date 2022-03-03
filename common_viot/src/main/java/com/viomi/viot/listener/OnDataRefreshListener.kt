package com.viomi.viot.listener

/**
 * 数据刷新监听
 *
 * @author William
 * @date 2020/12/22
 */
interface OnDataRefreshListener {
    fun onSceneRefresh()

    fun onDevicesRefresh()

    fun onMiTokenRefresh()
}

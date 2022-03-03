package com.viomi.iotdevice.iotmesh.callback

import com.viomi.iotdevice.iotmesh.entity.DeviceToWiFiMeshEntity
import com.viomi.iotdevice.iotmesh.entity.WiFiToDeviceMeshEntity

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : Mesh 组网相关回调
 *     version: 1.0
 * </pre>
 */
interface IotMeshCallback {
    /**
     * 配网 WiFi 信息回调.
     *
     * @param ssid:     WiFi SSID
     * @param password: WiFi 密码
     */
    fun onWiFiInfoCallback(ssid: String?, password: String?)

    /**
     * Mesh 组网二维码回调.
     *
     * @param isSuccess: 是否获取成功
     * @param url:       二维码链接
     */
    fun onMeshQRRefresh(isSuccess: Boolean, url: String?)

    /**
     * Android To WiFi Mesh 组网回调.
     *
     * @param isSuccess: 是否成功
     * @param entity:    回调信息
     */
    fun onDeviceToWiFiMeshResult(isSuccess: Boolean, entity: DeviceToWiFiMeshEntity?)

    /**
     * WiFi To Android Mesh 组网回调.
     *
     * @param isSuccess: 是否成功
     * @param entity:    回调信息
     */
    fun onWiFiToDeviceMeshResult(isSuccess: Boolean, entity: WiFiToDeviceMeshEntity?)
}

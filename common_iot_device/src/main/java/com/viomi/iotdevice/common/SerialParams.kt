package com.viomi.iotdevice.common

import android.content.Context
import android.content.SharedPreferences
import com.viomi.iotdevice.ViomiIotManager

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/12/31
 *     desc   : 串口相关缓存参数
 *     version: 1.0
 * </pre>
 */
class SerialParams {
    private val mSharedPreferences: SharedPreferences by lazy {
        ViomiIotManager.instance.pAppContext.getSharedPreferences(SharedPreferencesStr, Context.MODE_PRIVATE)
    }

    /**
     * 串口回显开关
     */
    var isEchoEnable: Boolean
        get() = mSharedPreferences.getBoolean(Key_Echo_Enable, false)
        set(enable) {
            val editor = mSharedPreferences.edit()
            editor.putBoolean(Key_Echo_Enable, enable)
            editor.apply()
        }

    /**
     * 设备 model
     */
    var model: String
        get() = mSharedPreferences.getString(Key_Model, "") ?: ""
        set(model) {
            val editor = mSharedPreferences.edit()
            editor.putString(Key_Model, model)
            editor.apply()
        }

    /**
     * 根据 sid 和 pid 获取属性
     */
    fun getProp(siid: String, piid: String): String? {
        val key = "$siid.$piid"
        return mSharedPreferences.getString(key, "")
    }

    /**
     * 根据 sid 和 pid 缓存属性
     */
    fun saveProp(siid: String, piid: String, value: String?) {
        val key = "$siid.$piid"
        val editor = mSharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun saveProp(siid_piid: String?, value: String?) {
        val editor = mSharedPreferences.edit()
        editor.putString(siid_piid, value)
        editor.apply()
    }

    /**
     * 获取当前连接 WiFi SSID
     */
    val wiFiSSID: String?
        get() = mSharedPreferences.getString(Key_WIFI_SSID, "")

    /**
     * 缓存当前连接 WiFi SSID
     */
    fun saveWiFiSSID(ssid: String) {
        val editor = mSharedPreferences.edit()
        editor.putString(Key_WIFI_SSID, ssid)
        editor.apply()
    }

    /**
     * 获取当前连接 WiFi 密码
     */
    val wiFiPassword: String?
        get() = mSharedPreferences.getString(Key_WIFI_PASSWORD, "")

    /**
     * 缓存当前连接 WiFi 密码
     */
    fun saveWiFiPassword(password: String?) {
        val editor = mSharedPreferences.edit()
        editor.putString(Key_WIFI_PASSWORD, password)
        editor.apply()
    }

    /**
     * 获取当前连接 WiFi Mac
     */
    val wiFiMac: String?
        get() = mSharedPreferences.getString(Key_WiFi_MAC, "")

    /**
     * 缓存当前连接 WiFi Mac
     */
    fun saveWiFiMac(mac: String?) {
        val editor = mSharedPreferences.edit()
        editor.putString(Key_WiFi_MAC, mac)
        editor.apply()
    }

    /**
     * 获取当前 Mesh 组网状态
     */
    val meshStatus: Boolean
        get() = mSharedPreferences.getBoolean(Key_Mesh_Status, false)

    /**
     * 缓存 Mesh 组网状态
     */
    fun saveMeshStatus(status: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(Key_Mesh_Status, status)
        editor.apply()
    }

    companion object {
        private const val SharedPreferencesStr = "SerialParams"

        /**
         * 串口回显开关
         */
        private const val Key_Echo_Enable = "Key_Echo"

        /**
         * 设备 Model
         */
        private const val Key_Model = "Key_Model"

        /**
         * 当前连接 WiFi SSID
         */
        private const val Key_WIFI_SSID = "Key_WiFi_SSID"

        /**
         * 当前连接 WiFi 密码
         */
        private const val Key_WIFI_PASSWORD = "Key_WiFi_Password"

        /**
         * 当前连接 WiFi Mac
         */
        private const val Key_WiFi_MAC = "Key_WiFi_Mac"

        /**
         * Mesh 组网状态
         */
        private const val Key_Mesh_Status = "Key_Mesh_Status"

        val instance: SerialParams by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SerialParams()
        }
    }
}

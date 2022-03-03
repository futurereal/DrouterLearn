package com.viomi.viot.preference

import android.content.Context
import com.viomi.viot.utils.LogUtil

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/02
 *     desc   : SharePreference 管理
 *     version: 1.0
 * </pre>
 */
object VIotDevicePreference {
    private val TAG = VIotDevicePreference::class.java.simpleName
    private const val name = "VIotDevice"
    private const val DEVICE_BIND_FLAG = "device_bind_flag"

    internal fun saveBindFlag(context: Context, isBind: Boolean) {
        val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(DEVICE_BIND_FLAG, isBind)
        editor.apply()
        kotlin.runCatching { Runtime.getRuntime().exec("sync") }.onFailure { LogUtil.e(TAG, it.message.toString()) }
    }

    internal fun getBindFlag(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val bindFlag = sharedPreferences.getBoolean(DEVICE_BIND_FLAG, false)
        LogUtil.d(TAG, "getBindFlag: $bindFlag")
        return bindFlag
    }
}

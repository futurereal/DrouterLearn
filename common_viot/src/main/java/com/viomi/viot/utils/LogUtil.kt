package com.viomi.viot.utils

import android.util.Log

/**
 * 日志工具类
 * Created by William on 2020/5/14.
 */
object LogUtil {
    var isPrintLog: Boolean = false

    fun d(TAG: String, msg: String) = takeIf { isPrintLog }?.run { Log.d(TAG, msg) }

    fun e(TAG: String?, msg: String) = takeIf { isPrintLog }?.run { Log.e(TAG, msg) }

    fun i(TAG: String?, msg: String) = takeIf { isPrintLog }?.run { Log.i(TAG, msg) }

    fun v(TAG: String?, msg: String) = takeIf { isPrintLog }?.run { Log.v(TAG, msg) }

    fun w(TAG: String?, msg: String) = takeIf { isPrintLog }?.run { Log.w(TAG, msg) }
}
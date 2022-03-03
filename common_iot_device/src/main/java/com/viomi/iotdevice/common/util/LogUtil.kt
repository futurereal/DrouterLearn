package com.viomi.iotdevice.common.util

import android.text.TextUtils
import android.util.Log

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/12/31
 *     desc   : 日志打印管理
 *     version: 1.0
 * </pre>
 */
internal object LogUtil {
    internal var logEnable: Boolean = false

    private fun generateTag(caller: StackTraceElement): String {
        var tag = "%s.%s(L:%d)"
        var callerClazzName = caller.className
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        tag = String.format(tag, callerClazzName, caller.methodName, caller.lineNumber)
        tag = if (TextUtils.isEmpty("")) tag else ":$tag"
        return tag
    }

    private val callerStackTraceElement: StackTraceElement
        get() = Thread.currentThread().stackTrace[4]

    internal fun d(TAG: String, msg: String) {
        if (logEnable && msg.isNotBlank()) {
            val caller = callerStackTraceElement
            val tag = generateTag(caller)
            Log.d(tag, "$TAG $msg")
        }
    }

    internal fun e(TAG: String, msg: String) {
        if (logEnable && msg.isNotBlank()) {
            val caller = callerStackTraceElement
            val tag = generateTag(caller)
            Log.e(tag, "$TAG $msg")
        }
    }

    internal fun i(TAG: String, msg: String) {
        if (logEnable && msg.isNotBlank()) {
            val caller = callerStackTraceElement
            val tag = generateTag(caller)
            Log.i(tag, "$TAG $msg")
        }
    }

    internal fun w(TAG: String, msg: String) {
        if (logEnable && msg.isNotBlank()) {
            val caller = callerStackTraceElement
            val tag = generateTag(caller)
            Log.w(tag, "$TAG $msg")
        }
    }
}

package com.viomi.upgrade_lib.utils

import android.text.TextUtils
import android.util.Log
import java.util.*

/**
 * Created by mai on 2020/12/15.
 */

class VLog {
    companion object {
        var DEBUG = true

        fun d(tag: String, msg: String?) {
            if (DEBUG && !TextUtils.isEmpty(msg)) {
                if (msg != null) {
                    Log.d(tag + "_V_UPGRADE", msg)
                }
            }
        }

        fun e(tag: String, msg: String?) {
            if (DEBUG && !TextUtils.isEmpty(msg)) {
                if (msg != null) {
                    Log.e(tag + "_V_UPGRADE", msg)
                }
            }
        }

        fun bigd(tag: String?, msg: String?) {
            var msg = msg
            if (tag != null && tag.length != 0 && msg != null && msg.length != 0) {
                val segmentSize = 3072
                val length = msg.length.toLong()
                if (length <= segmentSize.toLong()) {
                    Log.d(tag, msg)
                } else {
                    while (msg!!.length > segmentSize) {
                        val logContent = msg.substring(0, segmentSize)
                        msg = msg.replace(logContent, "")
                        Log.d(tag, logContent)
                    }
                    Log.d(tag, msg)
                }
            }
        }
    }
}
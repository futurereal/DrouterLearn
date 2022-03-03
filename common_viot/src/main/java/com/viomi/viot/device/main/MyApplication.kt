package com.viomi.viot.device.main

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import com.facebook.drawee.backends.pipeline.Fresco

/**
 *
 * Created by William on 2020/6/12.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (isMainProcess()) {
            Fresco.initialize(this)
        }
    }

    private fun isMainProcess(): Boolean {
        val mainProcessName = packageName
        val processName: String? = getProcess()
        return TextUtils.equals(processName, mainProcessName)
    }

    private fun getProcess(): String? {
        val pid = Process.myPid()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in activityManager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                return processInfo.processName
            }
        }
        return null
    }
}
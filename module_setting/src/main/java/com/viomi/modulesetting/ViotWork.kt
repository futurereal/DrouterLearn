package com.viomi.modulesetting

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.viomi.common.VLogUtil
import com.viomi.modulesetting.manager.ViotDeviceManager
import com.viomi.ovensocommon.serialcontrol.SerialControl

/**
 *@description: 启动Viot 的服务，并且获取所有属性上报
 *@data:2022/2/11
 */
class ViotWork(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        Log.i(TAG, "doWork: ")
        val userinfoDb = ModuleSettingApplicaiton.getUserInfoDb()
        ViotDeviceManager.getInstance().bindViotServcie(userinfoDb)
        VLogUtil.setDeviceId()
        return Result.success()
    }


    companion object {
        private const val TAG = "ViotWork"
    }
}
package com.miotspecv2.defined.viomi

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class MiotInitWork(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        Log.i(TAG, "doWork: ")
        MiotDeviceHelper.getInstance().initDevice()
        MiotDeviceHelper.getInstance().startMiotService(applicationContext)
        return Result.success()
    }

    companion object {
        private const val TAG = "MiotInitWork"
    }
}
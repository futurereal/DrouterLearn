package com.viomi.modulesetting

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.viomi.common.ApplicationUtils
import com.viomi.ovensocommon.db.UserInfoDb
import com.viomi.ovensocommon.db.ViomiRoomDatabase
import com.viomi.ovensocommon.serialcontrol.SerialCommunicateManager
import com.viomi.ovensocommon.serialcontrol.SerialControl
import  io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import com.viomi.modulesetting.service.CommonSetService




/**
 *@description:
 *@data:2022/2/11
 */
class ModuleSettingWork(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        Log.i(TAG, "doWork: ")
        initUserInfoDbAndBindViotService()
        initBeforeSet(ApplicationUtils.getContext())
        SerialCommunicateManager.getInstance().openSerialPort()
        // 需要获取 特定的属性，比如 门是否关好，水箱等的属性
        SerialControl.getMucPropertiesAndReport()
        // 启动服务
        applicationContext.startService(Intent(applicationContext, CommonSetService::class.java))
        return Result.success()
    }

    private fun initUserInfoDbAndBindViotService() {
        Log.i(TAG, "initUserInfoDbAndBindViotService: ")
        Observable.create<UserInfoDb> { emitter ->
            var userInfoDb = ViomiRoomDatabase.getDatabase().userInfoDao().userInfo
            Log.i(TAG, "initUserInfoDbAndBindViotService: $userInfoDb")
            if (userInfoDb == null) {
                userInfoDb = UserInfoDb()
                userInfoDb.nickname = ModuleSettingConstants.DEFAULT_NAME
                userInfoDb.headImgUrl = ModuleSettingConstants.DEFAULT_HEAD
            }
            ModuleSettingApplicaiton.setUserInfoDb(userInfoDb)
            emitter.onNext(userInfoDb)
            emitter.onComplete()
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    /**
     * 恢复之前设置的属性
     */
    private fun initBeforeSet(context: Context) {
        Log.i(TAG, "initBeforeSet: ")
        // 初始化声音的配置
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (mAudioManager != null) {
            val musicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val systemMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
            val musicMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val systemVolume = (musicVolume * 1f / musicMaxVolume * systemMaxVolume).toInt()
            //            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, systemVolume, 0);
        }
        // 初始化Fresco  报错，不能进行初始化
        val okHttpClient = OkHttpClient.Builder()
            .build()
        val imagePipelineConfig = OkHttpImagePipelineConfigFactory
            .newBuilder(context, okHttpClient)
            .build()
        Fresco.initialize(context, imagePipelineConfig)
        Log.i(TAG, "initBeforeSet: initFrecoEnd")
    }

    companion object {
        private const val TAG = "ModuleSettingWork"
    }
}
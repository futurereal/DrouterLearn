package com.viomi.upgrade_lib.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.*
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.viomi.upgrade_lib.http.UpdateClient
import com.viomi.upgrade_lib.UpdateConst
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_FILE_PATH
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_UPDATE_PROGRESS
import com.viomi.upgrade_lib.UpdateConst.CODE_DOWNLOADING
import com.viomi.upgrade_lib.UpdateConst.CODE_PARSE_FAIL
import com.viomi.upgrade_lib.UpdateConst.CODE_SUCCESS
import com.viomi.upgrade_lib.api.VUpgradeApi
import com.viomi.upgrade_lib.entity.CheckVersionResult
import com.viomi.upgrade_lib.entity.DownloadResult
import com.viomi.upgrade_lib.utils.*
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by mai on 2020/9/29.
 */
class CheckUpdateHelper {

    companion object {

        var SCREEN_OFF: Boolean = false

        var apkPath: String? = null

        fun getFirmwareInfo(context: Context): JSONArray {
            var firmwareList = JSONArray()
            val firmware = JSONObject();
            firmware.put("firmwareName", VUpgradeApi.instance.mDeviceInfo?.firmwareName)
            firmware.put("firmwareType", VUpgradeApi.instance.mDeviceInfo?.firmwareType)
            firmware.put("firmwareVersion", PackageUtil.getVersionName(context))
            firmwareList.put(firmware)
            return firmwareList
        }

        fun uploadAppVersion(context: Context) {

            val params = JSONObject()
            params.put("did", VUpgradeApi.instance.mDeviceInfo?.did)
            params.put("channel", VUpgradeApi.instance.mDeviceInfo?.channel)
            params.put("model", VUpgradeApi.instance.mDeviceInfo?.model)
            params.put("mac", VUpgradeApi.instance.mDeviceInfo?.mac)
            params.put("firmwareList", getFirmwareInfo(context))

            val body: RequestBody =
                RequestBody.create("application/json".toMediaTypeOrNull(), params.toString())
            UpdateClient.instance.httpApiService.uploadDeviceInfo(body)
                .compose(SchedulerProvider.applySchedulers())
                .subscribe(Consumer {
                    VLog.d("CheckUpdateHelper", "上报设备信息完成...")
                }, Consumer { it.printStackTrace() });
        }

        fun deleteApkFile() {
            FileUtils.deleteAllInDir(File(UpdateConst.FILE_DOWNLOAD_PATH))
        }

        /**
         * 检查版本更新
         */
        fun checkVersionUpdate(context: Context?) {
            val checkUpdateRandom = Random().nextInt(120) //随机生成0到120

            val checkPeriod =
                if (UpdateConst.CHECK_PERIOD_DEBUG) 10 else 240 + checkUpdateRandom.toLong()
            VLog.d("CheckUpdateHelper", "checkPeriod :$checkPeriod")
            //4小时即240min，加随机时间
            try {
                Observable.interval(3, checkPeriod, TimeUnit.MINUTES)
                    .compose<Long>(SchedulerProvider.applySchedulers())
                    .subscribe({ aLong: Long? ->
                        VLog.d("CheckUpdateHelper", "start check version...")
                        if (VUpgradeApi.instance.mIsCheckVersion == null || VUpgradeApi.instance.mIsCheckVersion!!.invoke()) {
                            VLog.d("CheckUpdateHelper", "start check version real...")
                            checkAppVersion(context!!)
                        }
                    }) { obj: Throwable -> obj.printStackTrace() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun cancelAllWork(context: Context) {
            WorkManager.getInstance(context).cancelAllWork()
        }

        /**
         * 下载apk
         */
        fun downloadApk(context: Context, url: String, progressListener: (Int) -> Unit) {

            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()

            val downloadWork = OneTimeWorkRequest.Builder(DownloadWork::class.java)
                .setConstraints(constraints)
                .setInputData(
                    Data.Builder()
                        .putString(UpdateConst.BUNDLE_KEY_DOWNLOAD_URL, url)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).getWorkInfoByIdLiveData(downloadWork.id)
                .observeForever {
                    if (it != null) {
                        when (it.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                apkPath = it.outputData.getString(BUNDLE_KEY_FILE_PATH)
                                //需要检查是否息屏再安装
                                checkInstall(context, false)
                            }
                            WorkInfo.State.RUNNING -> {
                                progressListener(it.progress.getInt(BUNDLE_KEY_UPDATE_PROGRESS, 0))
                            }
                            WorkInfo.State.FAILED -> {
                                progressListener(-1)
                            }
                        }
                    }
                }

            WorkManager.getInstance(context).enqueueUniqueWork(
                DownloadWork.DOWNLOAD_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                downloadWork!!
            )
        }

        fun downloadApk(
            context: Context,
            url: String,
            upgradeType: Int,
            recordId: Long,
            downloadListener: (DownloadResult) -> Unit
        ) {

            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()

            val downloadWork = OneTimeWorkRequest.Builder(DownloadWork::class.java)
                .setConstraints(constraints)
                .setInputData(
                    Data.Builder()
                        .putString(UpdateConst.BUNDLE_KEY_DOWNLOAD_URL, url)
                        .putLong(UpdateConst.BUNDLE_KEY_RECORD_ID, recordId)
                        .putInt(UpdateConst.BUNDLE_KEY_UPGRADE_TYPE, upgradeType)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).getWorkInfoByIdLiveData(downloadWork.id)
                .observeForever {
                    if (it != null) {
                        when (it.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                apkPath = it.outputData.getString(BUNDLE_KEY_FILE_PATH)
                                downloadListener(DownloadResult(CODE_SUCCESS, 100))
                                //需要检查是否息屏再安装
                                checkInstall(context, false)
                            }
                            WorkInfo.State.RUNNING -> {
                                VLog.d(
                                    "CheckUpdateHelper",
                                    "下载进度${it.progress.getInt(BUNDLE_KEY_UPDATE_PROGRESS, 0)}"
                                )
                                downloadListener(
                                    DownloadResult(
                                        CODE_DOWNLOADING,
                                        it.progress.getInt(BUNDLE_KEY_UPDATE_PROGRESS, 0)
                                    )
                                )
                            }
                            WorkInfo.State.FAILED -> {
                                downloadListener(DownloadResult(CODE_PARSE_FAIL))
                            }
                        }
                    }
                }

            WorkManager.getInstance(context).enqueueUniqueWork(
                DownloadWork.DOWNLOAD_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                downloadWork!!
            )
        }

        fun checkAppVersionNoDownload(
            context: Context,
            versionListener: (CheckVersionResult) -> Unit
        ) {

            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()

            var params = JSONObject()
            params.put("userId", VUpgradeApi.instance.mGetUserId?.invoke())
            params.put("did", VUpgradeApi.instance.mDeviceInfo?.did)
            params.put("channel", VUpgradeApi.instance.mDeviceInfo?.channel)
            params.put("model", VUpgradeApi.instance.mDeviceInfo?.model)
            params.put("firmwareList", getFirmwareInfo(context))

            val checkUpdateWork =
                OneTimeWorkRequest.Builder(CheckUpdateWork::class.java)
                    .setConstraints(constraints)
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UpdateConst.BUNDLE_KEY_VERSION,
                                PackageUtil.getVersionName(context)
                            )
                            .putString(UpdateConst.BUNDLE_KEY_PARAMS, params.toString())
                            .build()
                    )
                    .build()

            WorkManager.getInstance(context)
                .getWorkInfoByIdLiveData(checkUpdateWork.id)
                .observeForever {

                    if (it.state == WorkInfo.State.SUCCEEDED) { //有新版本
                        versionListener(
                            CheckVersionResult(
                                UpdateConst.CODE_SUCCESS,
                                it.outputData.getString(UpdateConst.BUNDLE_KEY_DOWNLOAD_URL),
                                it.outputData.getLong(UpdateConst.BUNDLE_KEY_RECORD_ID, 0L),
                                it.outputData.getString(UpdateConst.BUNDLE_KEY_NEW_VERSION),
                                it.outputData.getInt(UpdateConst.BUNDLE_KEY_UPGRADE_TYPE, 0),
                                it.outputData.getString(UpdateConst.BUNDLE_KEY_DESCRIPTION),
                            )
                        )

                    } else if (it.state == WorkInfo.State.FAILED) {
                        if (it.outputData.getInt(
                                UpdateConst.BUNDLE_KEY_RESULT_CODE,
                                0
                            ) == UpdateConst.CODE_NO_NEED_UPDATE
                        ) {//已是最新版本
                            versionListener(CheckVersionResult(code = UpdateConst.CODE_NO_NEED_UPDATE))

                        } else { //请求出错
                            versionListener(CheckVersionResult(code = UpdateConst.CODE_PARSE_FAIL))
                        }
                    }
                }

            WorkManager.getInstance(context).enqueue(checkUpdateWork)
        }

        private fun checkAppVersion(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()

            var params = JSONObject()
            params.put("userId", VUpgradeApi.instance.mGetUserId?.invoke())
            params.put("did", VUpgradeApi.instance.mDeviceInfo?.did)
            params.put("channel", VUpgradeApi.instance.mDeviceInfo?.channel)
            params.put("model", VUpgradeApi.instance.mDeviceInfo?.model)
            params.put("firmwareList", getFirmwareInfo(context))

            val checkUpdateWork =
                OneTimeWorkRequest.Builder(CheckUpdateWork::class.java)
                    .setConstraints(constraints)
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UpdateConst.BUNDLE_KEY_VERSION,
                                PackageUtil.getVersionName(context)
                            )
                            .putString(UpdateConst.BUNDLE_KEY_PARAMS, params.toString())
                            .build()
                    )
                    .build()


            WorkManager.getInstance(context)
                .getWorkInfoByIdLiveData(checkUpdateWork.id)
                .observeForever {
                    if (it != null) {
                        if (it.state == WorkInfo.State.SUCCEEDED) {
                            VUpgradeApi.instance.mVersionCallBack?.invoke(true)
                        } else if (it.state == WorkInfo.State.FAILED) {
                            VUpgradeApi.instance.mVersionCallBack?.invoke(false)
                        }
                    }
                }

            val downloadWork = OneTimeWorkRequest.Builder(DownloadWork::class.java)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).getWorkInfoByIdLiveData(downloadWork.id)
                .observeForever {
                    if (it != null) {
                        if (it.state == WorkInfo.State.SUCCEEDED) {
                            apkPath = it.outputData.getString(BUNDLE_KEY_FILE_PATH)
                            //需要检查是否息屏再安装
                            checkInstall(context, true)
                        } else if (it.state == WorkInfo.State.RUNNING) {
                            VLog.d(
                                "CheckUpdateHelper",
                                "下载进度-->${it.progress.getInt(BUNDLE_KEY_UPDATE_PROGRESS, 0)}"
                            )
                        }
                    }
                }

            WorkManager.getInstance(context).beginUniqueWork(
                DownloadWork.DOWNLOAD_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                checkUpdateWork
            )
                .then(downloadWork)
                .enqueue()
        }

        fun checkInstall(context: Context, isCheck: Boolean) {
            VLog.d("CheckUpdateHelper", "是否需要检查息屏-->$isCheck")
            if (isCheck) {
                VLog.d("CheckUpdateHelper", "是否息屏-->$SCREEN_OFF")
                if (SCREEN_OFF) {
                    installApk(context)
                }
            } else {
                installApk(context);
            }
        }

        private fun installApk(context: Context) {
            VLog.d("CheckUpdateHelper", "安装apk-->$apkPath")
            if (apkPath != null) {
                val intent = Intent()
                intent.action = "android.intent.action.SILENCE_INSTALL"
                intent.putExtra("apkPath", apkPath)
                context.sendBroadcast(intent)
                apkPath = null

            }
        }
    }
}
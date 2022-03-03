package com.viomi.upgrade_lib.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.viomi.upgrade_lib.http.UpdateClient
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_DOWNLOAD_URL
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_FILE_PATH
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_RECORD_ID
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_RESULT_CODE
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_UPDATE_PROGRESS
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_UPDATE_RESULT
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_UPGRADE_TYPE
import com.viomi.upgrade_lib.UpdateConst.FILE_DOWNLOAD_PATH
import com.viomi.upgrade_lib.preference.UpdatePreference
import com.viomi.upgrade_lib.utils.SchedulerProvider
import com.viomi.upgrade_lib.utils.VLog
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer


/**
 * 下载文件任务
 *
 * @author mai
 * @date 2020/9/5
 */
class DownloadWork(context: Context, workerParameters: WorkerParameters) :
        RxWorker(context, workerParameters) {

    private val TAG = "DownloadWork";

    companion object {
        val DOWNLOAD_WORK_NAME: String = "download_work";
    }

    override fun createWork(): Single<Result> {
        Log.d(TAG, "start download work...")
        val url = inputData.getString(BUNDLE_KEY_DOWNLOAD_URL) ?: ""
        val recordId = inputData.getLong(BUNDLE_KEY_RECORD_ID, 0)
        val upgradeType = inputData.getInt(BUNDLE_KEY_UPGRADE_TYPE, 0)
        val fileName = getFileName(url)
        val file = File(FILE_DOWNLOAD_PATH, fileName)
        val totalLength = if (file.exists()) "-${file.length()}" else "-"
        val range = if (file.exists()) UpdatePreference.getInstance(applicationContext)
                .getFileLength(url) else 0

        return Observable.just(file.exists() && range == file.length())
                .filter {
                    !it
                }
                .flatMap {
                    UpdateClient.instance.downloadService.download("bytes=$range$totalLength", url)
                }
                .toList()
                .map {
                    if (it.isEmpty()) {
                        //已经下载完成
                        //1、如果是差分包，判断是否已经合并成apk

                        var apkPath = file.absolutePath;
                        if (upgradeType == 1) {
                            val newFile = File(FILE_DOWNLOAD_PATH, getPatchNewApkFileName(url))
                            if (!newFile.exists()) {
                                VLog.d(TAG, "已经下载完成，还没合成差分包了，进行差分包合成...")
                                setProgressAsync(
                                        Data.Builder()
                                                .putInt(BUNDLE_KEY_UPDATE_PROGRESS, 95)
                                                .build()
                                )
                                bspatch(
                                        applicationContext.applicationInfo.sourceDir,
                                        apkPath,
                                        newFile.absolutePath
                                )
                            }
                            apkPath = newFile.absolutePath
                        }

                        Result.success(
                                Data.Builder().putInt(BUNDLE_KEY_RESULT_CODE, 0)
                                        .putString(BUNDLE_KEY_FILE_PATH, apkPath)
                                        .build()
                        )
                    } else {
                        downloadFile(
                                recordId,
                                range,
                                url,
                                upgradeType,
                                it[0] as ResponseBody
                        ) // 没有或已经下载部分
                    }
                }
                .doOnError {
                    Result.failure(
                            Data.Builder().putInt(BUNDLE_KEY_RESULT_CODE, 2)
                                    .putString(BUNDLE_KEY_UPDATE_RESULT, it.message).build()
                    )
                }
    }

    private fun getFileName(url: String): String {
        val separatorIndex = url.lastIndexOf("/")
        return if (separatorIndex < 0) url else url.substring(separatorIndex + 1, url.length)
    }

    /**
     * 获取合成apk名称
     */
    private fun getPatchNewApkFileName(url: String): String {
        val separatorIndex = url.lastIndexOf("/")
        val name = if (separatorIndex < 0) url else url.substring(separatorIndex + 1, url.length)
        return name.replace(".apk", "") + "_new.apk"
    }


    private fun downloadFile(
            recordId: Long,
            range: Long,
            url: String,
            upgradeType: Int,
            responseBody: ResponseBody
    ): Result {
        var randomAccessFile: RandomAccessFile? = null
        var inputStream: InputStream? = null
        var total = range
        val responseLength: Long
        try {
            val bufferSize: Int = 1024 * 1024;
            val byteBuffer = ByteBuffer.allocate(bufferSize)
            val buf = ByteArray(1024 * 4)
            var len: Int
            responseLength = responseBody.contentLength()
            inputStream = responseBody.byteStream()
            val filePath: String? = FILE_DOWNLOAD_PATH.also { File(it).mkdirs() }
            val file = File(filePath, getFileName(url))
            randomAccessFile = RandomAccessFile(file, "rwd")
            if (range == 0L) {
                randomAccessFile.setLength(responseLength)
            }
            randomAccessFile.seek(range)

            var progress = 0
            var lastProgress: Int

            val maxProgress: Int = if (upgradeType == 1) 95 else 100;

            fun writeToFile() {
                randomAccessFile.write(byteBuffer.array(), 0, byteBuffer.position())
                total += byteBuffer.position().toLong()
                lastProgress = progress
                progress = (total * maxProgress / randomAccessFile.length()).toInt()
                if (progress > 0 && progress != lastProgress) {
                    VLog.d(TAG, "下载进度--->$progress")
                    setProgressAsync(
                            Data.Builder()
                                    .putInt(BUNDLE_KEY_UPDATE_PROGRESS, progress)
                                    .build()
                    )
                }
                byteBuffer.clear()
            }

            while (inputStream.read(buf).also { len = it } != -1) {

                if (byteBuffer.position() + len > bufferSize) {
                    writeToFile()
                }
                byteBuffer.put(buf, 0, len)
            }

            if (byteBuffer.position() > 0) {
                writeToFile()
            }


            var apkPath = file.absolutePath
            //如果是差分包，进行合并
            if (upgradeType == 1) {
                VLog.d(TAG, "下载完成，进行差分包合成...")
                val newPath = FILE_DOWNLOAD_PATH + File.separator + getPatchNewApkFileName(url)
                bspatch(
                        applicationContext.applicationInfo.sourceDir,
                        apkPath,
                        newPath
                )
                apkPath = newPath;
            }


            //新增上报升级成功
            val params = JSONObject();
            params.put("recordId", recordId)
            params.put("result", 1)
            params.put("progress", 100)
            val body: RequestBody =
                    RequestBody.create("application/json".toMediaTypeOrNull(), params.toString())
            UpdateClient.instance.httpApiService.reportUpgradeProgress(body)
                    .compose(SchedulerProvider.applySchedulers())
                    .subscribe(Consumer {
                        Log.d("DownloadWork", "升级成功上报成功...")
                    }, Consumer { t -> t.printStackTrace() })

            return Result.success(
                    Data.Builder().putInt(BUNDLE_KEY_RESULT_CODE, 0)
                            .putString(BUNDLE_KEY_FILE_PATH, apkPath)
                            .build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(
                    Data.Builder()
                            .putInt(BUNDLE_KEY_RESULT_CODE, 2)
                            .putString(BUNDLE_KEY_UPDATE_RESULT, e.message)
                            .build()
            )
        } finally {
            try {
                UpdatePreference.getInstance(applicationContext)
                        .saveFileLength(url, total)
                randomAccessFile?.close()
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    init {
        System.loadLibrary("native-lib")
    }

    /**
     * @param oldapk 当前运行的apk
     * @param patch  差分包
     * @param output 合成后的新的apk输出到
     */
    external fun bspatch(
            oldapk: String?,
            patch: String?,
            output: String?
    )
}
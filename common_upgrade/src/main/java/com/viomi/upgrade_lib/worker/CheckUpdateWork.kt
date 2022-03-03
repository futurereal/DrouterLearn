package com.viomi.upgrade_lib.worker

import android.content.Context
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_DESCRIPTION
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_DOWNLOAD_URL
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_NEW_VERSION
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_PARAMS
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_RECORD_ID
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_RESULT_CODE
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_UPDATE_RESULT
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_VERSION
import com.viomi.upgrade_lib.UpdateConst.CODE_NO_NEED_UPDATE
import com.viomi.upgrade_lib.UpdateConst.CODE_PARSE_FAIL
import com.viomi.upgrade_lib.UpdateConst.CODE_SUCCESS
import com.viomi.upgrade_lib.entity.UpdateEntity
import com.viomi.upgrade_lib.http.UpdateClient
import com.viomi.upgrade_lib.UpdateConst.BUNDLE_KEY_UPGRADE_TYPE
import com.viomi.upgrade_lib.entity.Response
import com.viomi.upgrade_lib.utils.VLog
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody


/**
 * 检查软件更新任务
 *
 * @author mai
 * @date 2020/8/27
 */
class CheckUpdateWork(context: Context, workerParameters: WorkerParameters) :
    RxWorker(context, workerParameters) {

    override fun createWork(): Single<Result> {
        val params: String = inputData.getString(BUNDLE_KEY_PARAMS) ?: ""
        val version: String = inputData.getString(BUNDLE_KEY_VERSION) ?: ""

        val body: RequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), params)

        return UpdateClient.instance.httpApiService.checkUpdate(body)
            .toList()
            .map {
                parseResult(it[0], version)
            }
            .doOnError {
                VLog.d(CheckUpdateWork::class.java.simpleName, it.message)
                Result.failure(
                    Data.Builder()
                        .putInt(BUNDLE_KEY_RESULT_CODE, CODE_PARSE_FAIL)
                        .putString(BUNDLE_KEY_UPDATE_RESULT, it.message)
                        .build()
                )
            }
    }

    private fun parseResult(
        updateEntity: Response<UpdateEntity>,
        version: String
    ): Result = when {
        updateEntity.code != 100 -> {
            Result.failure(
                Data.Builder()
                    .putInt(BUNDLE_KEY_RESULT_CODE, CODE_PARSE_FAIL)
                    .putString(BUNDLE_KEY_UPDATE_RESULT, "code is not 100.")
                    .build()
            )
        }
        (updateEntity.result!!.firmwareList.isEmpty() ||
                updateEntity.result.firmwareList[0].downloadUrlList.isEmpty()) -> {
            /* Result.success(
                 Data.Builder()
                     .putInt(BUNDLE_KEY_RESULT_CODE, CODE_SUCCESS)
                     .putString(BUNDLE_KEY_DOWNLOAD_URL, "https://cnbj2.fds.api.xiaomi.com/viomi-app/patch")
                     .putString(BUNDLE_KEY_NEW_VERSION, "1.1.9")
                     .putLong(BUNDLE_KEY_RECORD_ID, 131234L)
                     .putInt(BUNDLE_KEY_UPGRADE_TYPE, 1)
                     .build()
             )*/
            Result.failure(
                Data.Builder()
                    .putInt(BUNDLE_KEY_RESULT_CODE, CODE_NO_NEED_UPDATE)
                    .putString(BUNDLE_KEY_UPDATE_RESULT, "Data is null.")
                    .build()
            )
        }
       /* updateEntity.result.firmwareList[0].firmwareLatestVersion <= version -> {
            Result.failure(
                Data.Builder()
                    .putInt(BUNDLE_KEY_RESULT_CODE, CODE_NO_NEED_UPDATE)
                    .putString(BUNDLE_KEY_UPDATE_RESULT, "App no need to update.")
                    .build()
            )
        }*/
        else -> {

            var upgradeType = updateEntity.result.firmwareList[0].downloadUrlList[0].upgradeType
            var downloadUrl = updateEntity.result.firmwareList[0].downloadUrlList[0].url
            var description = updateEntity.result.firmwareList[0].downloadUrlList[0].description;
            if (updateEntity.result.firmwareList[0].downloadUrlList.size > 1) {//存在差分包
                for (downloadUrl_ in updateEntity.result.firmwareList[0].downloadUrlList) {
                    if (downloadUrl_.upgradeType == 1) {
                        upgradeType = downloadUrl_.upgradeType
                        downloadUrl = downloadUrl_.url
                        description = downloadUrl_.description
                    }
                }
            }

            Result.success(
                Data.Builder()
                    .putInt(BUNDLE_KEY_RESULT_CODE, CODE_SUCCESS)
                    .putString(
                        BUNDLE_KEY_DOWNLOAD_URL,
                        downloadUrl
                    )
                    .putString(BUNDLE_KEY_DESCRIPTION, description)
                    .putString(
                        BUNDLE_KEY_NEW_VERSION,
                        updateEntity.result.firmwareList[0].firmwareLatestVersion
                    )
                    .putLong(
                        BUNDLE_KEY_RECORD_ID,
                        updateEntity.result.firmwareList[0].upgradeStrategyConditionResp.recordId
                    )
                    .putInt(BUNDLE_KEY_UPGRADE_TYPE, upgradeType)
                    .build()
            )
        }
    }
}
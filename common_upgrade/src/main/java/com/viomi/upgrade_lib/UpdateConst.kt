package com.viomi.upgrade_lib

import com.blankj.utilcode.util.Utils

/**
 * 升级模块相关常量
 *
 * @author William
 * @date 2020/9/2
 */
object UpdateConst {
    const val CODE_SUCCESS = 0 // 检查更新/下载成功
    const val CODE_DOWNLOADING = 1 //正在下载
    const val CODE_PARSE_FAIL = -99 // 出错
    const val CODE_NO_NEED_UPDATE = -98 // 无需更新

    const val BUNDLE_KEY_PARAMS = "params" // 请求参数
    const val BUNDLE_KEY_VERSION = "version" // 版本号
    const val BUNDLE_KEY_RESULT_CODE = "resultCode" // 结果码
    const val BUNDLE_KEY_NEW_VERSION = "newVersion" // 新版版本号
    const val BUNDLE_KEY_RECORD_ID = "recordId" // 升级记录id
    const val BUNDLE_KEY_UPGRADE_TYPE = "upgradeType" // 升级方式：0-全量，1-差分包
    const val BUNDLE_KEY_UPDATE_RESULT = "updateResult" // 检查更新结果
    const val BUNDLE_KEY_DOWNLOAD_URL = "downloadUrl" // 下载链接
    const val BUNDLE_KEY_DESCRIPTION = "description" // 下载链接
    const val BUNDLE_KEY_FILE_PATH = "filePath" // 下载文件保存位置
    const val BUNDLE_KEY_UPDATE_PROGRESS = "updateProgress" // 更新进度

    const val PREFERENCE_NAME = "module_app_update"

    var HTTP_DEBUG = false
    var CHECK_PERIOD_DEBUG = false

    val FILE_DOWNLOAD_PATH =Utils.getApp().externalCacheDir?.path
//        "${Environment.getExternalStorageDirectory()}${File.separator}Android${File.separator}data${File.separator}viomi_store" // 文件存储位置
}
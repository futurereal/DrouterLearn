package com.viomi.upgrade_lib.entity

/**
 * Created by mai on 2020/12/17.
 */
data class CheckVersionResult(
        val code: Int,   //错误码
        val url: String? = null,   //存在新版本时，下载链接
        val recordId: Long = 0L,  //id
        val newVersion: String? = null,  //最新版本号
        val upgradeType: Int = 0,  //升级类型，0全量包  1差分包
        val description: String? =null,  //升级描述
)

data class DownloadResult(
        val code: Int,
        val progress: Int = -1,
)
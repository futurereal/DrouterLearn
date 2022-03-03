package com.viomi.upgrade_lib.entity


/**
 * 软件升级实体
 * @author mai
 *
 */

data class UpdateEntity(
    val firmwareList: List<Firmware>
)

data class Firmware(
        val downloadUrlList: List<DownloadUrl>,
        val firmwareLatestVersion: String,
        val firmwareName: String,
        val upgradeStrategyConditionResp: UpgradeStrategyConditionResp
)

data class DownloadUrl(
    val upgradeType: Int,
    val description: String,
    val MD5: String,
    val url: String
)

data class UpgradeStrategyConditionResp(
    val battery: Int,
    val forceUpgrade: Int,
    val installEndTime: Int,
    val installStartTime: Int,
    val installStrategy: Int,
    val recordId: Long,
    val retryInterval: Int,
    val retryLimit: Int,
    val timeout: Int
)
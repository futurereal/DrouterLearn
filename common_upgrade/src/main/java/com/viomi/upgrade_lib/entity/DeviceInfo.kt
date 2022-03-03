package com.viomi.upgrade_lib.entity

/**
 * Created by mai on 2020/12/15.
 */
data class DeviceInfo(
    val did: String,
    val channel: Int, //0 viot 设备 1 小米设备
    val model: String,
    val mac: String,
    val firmwareName: String,
    val firmwareType: Int,//3,launcher  4,其他app，此值的model，mac可以为空字符串
)
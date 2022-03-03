package com.viomi.viot.listener

import com.viomi.viot.https.beans.MQTTConfigBean

/**
 * 获取 MQtt 配置回调接口
 * Created by William on 2020/6/2.
 */
interface OnMQTTConfigGetListener {
    fun onDecipherResult(result: String?)
    fun onSucceed(configBean: MQTTConfigBean?)
    fun onFailed(code: Int, desc: String?)
}

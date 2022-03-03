package com.viomi.viot.listener

/**
 * VIot 相关回调接口
 * Created by William on 2020/6/1.
 */
interface OnVIotResultListener {
    fun onSucceed()
    fun onFailed(code: Int, message: String?)
}

package com.viomi.iotdevice.common.callback


/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/21
 *     desc   : 公共回调
 *     version: 1.0
 * </pre>
 */
interface CommonCallback {
    /**
     * 回调返回
     *
     * @param resultCode: 结果代码
     * @param result:     返回结果内容
     * @param desc:       返回结果描述
     */
    fun onReceiveResult(resultCode: Int?, result: Any?, desc: String?)
}

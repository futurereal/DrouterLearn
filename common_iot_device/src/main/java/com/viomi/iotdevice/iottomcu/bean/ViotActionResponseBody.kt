package com.viomi.iotdevice.iottomcu.bean

import java.util.ArrayList

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : viot action 返回结构体
 *     version: 1.0
 * </pre>
 */
data class ViotActionResponseBody(
    /**
     * service id
     */
    var sid: Int = 0,

    /**
     * action id
     */
    var aid: Int = 0,

    /**
     * 结果代码
     */
    var code: Int = 0,

    /**
     * 属性集合
     */
    var propList: MutableList<ViotProperty> = ArrayList(),

    /**
     * 串口返回原数据
     */
    var serialData: String? = null
) {
    companion object {
        /**
         * 数据格式错误
         */
        const val CODE_DATA_FORMAT_ERROR: Int = -99
    }
}
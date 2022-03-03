package com.viomi.iotdevice.iottomcu.bean

import java.util.ArrayList

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : viot set_properties 返回结构体
 *     version: 1.0
 * </pre>
 */
data class ViotSetPropResponseBody(
    /**
     * 结果代码
     */
    var code: Int = 0,

    var propList: MutableList<ViotProperty> = ArrayList(),

    /**
     * 串口返回原数据
     */
    var serialData: String? = null
)

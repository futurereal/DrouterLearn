package com.viomi.iotdevice.iottomcu.bean

import java.util.ArrayList

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : Viot action 请求结构体
 *     version: 1.0
 * </pre>
 */
data class ViotActionRequestBody(
    /**
     * service id
     */
    var sid: Int = 0,

    /**
     * action id
     */
    var aid: Int = 0,

    /**
     * 属性集合
     */
    var propList: MutableList<ViotProperty> = ArrayList()
)

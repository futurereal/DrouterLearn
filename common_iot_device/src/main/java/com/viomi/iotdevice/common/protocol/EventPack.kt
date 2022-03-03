package com.viomi.iotdevice.common.protocol

import java.util.ArrayList
import java.util.HashMap

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : 事件上报封装
 *     version: 1.0
 * </pre>
 */
data class EventPack(
    /**
     * 事件名
     */
    var name: String? = null,

    /**
     * 事件带的属性参数, miot profile 协议用
     */
    var propList: MutableList<Any> = ArrayList<Any>(),

    /**
     * 事件带的属性参数, viot、miot-spec 协议用
     */
    var propMap: MutableMap<Int, Any> = HashMap<Int, Any>()
)

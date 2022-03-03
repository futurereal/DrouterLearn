package com.viomi.iotdevice.iottomcu.bean

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2021/10/27
 *     desc   : 属性
 *     version: 1.0
 * </pre>
 */
data class ViotProperty(
    /**
     * service id
     */
    var sid: Int = 0,

    /**
     * 结果码
     */
    var code: Int = 0,

    /**
     * prop id
     */
    var pid: Int = 0,

    /**
     * prop 的值
     */
    var value: Any? = null
)

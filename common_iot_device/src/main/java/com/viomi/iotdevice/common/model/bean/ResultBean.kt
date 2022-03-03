package com.viomi.iotdevice.common.model.bean

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/21
 *     desc   : 结果结构体
 *     version: 1.0
 * </pre>
 */
data class ResultBean(
    /**
     * 结果代码
     */
    var code: Int = 0,

    /**
     * 结果内容
     */
    var data: Any?,

    /**
     * 结果描述
     */
    var desc: String? = null
)

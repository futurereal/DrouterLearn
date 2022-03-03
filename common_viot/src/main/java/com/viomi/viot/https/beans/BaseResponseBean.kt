package com.viomi.viot.https.beans

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/25
 *     desc   : Http 请求公共 Bean
 *     version: 1.0
 * </pre>
 */
class BaseResponseBean<T> : Serializable {
    @JSONField(name = "code")
    var code = 0

    @JSONField(name = "desc")
    var desc: String? = null

    @JSONField(name = "result")
    var result: T? = null
        private set

    fun setResult(result: T) {
        this.result = result
    }
}

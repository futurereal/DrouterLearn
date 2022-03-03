package com.viomi.iotdevice.iottomcu.bean

import java.util.ArrayList

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2021/10/27
 *     desc   : viot set_properties 请求结构体
 *     version: 1.0
 * </pre>
 */
data class ViotSetPropRequestBody(
    var propList: MutableList<ViotProperty> = ArrayList()
)
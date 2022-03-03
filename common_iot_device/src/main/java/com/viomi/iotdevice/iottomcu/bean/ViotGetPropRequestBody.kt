package com.viomi.iotdevice.iottomcu.bean

import java.util.ArrayList

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : viot get_properties 请求结构体
 *     version: 1.0
 * </pre>
 */
data class ViotGetPropRequestBody(
    var propList: MutableList<ViotProperty> = ArrayList()
)

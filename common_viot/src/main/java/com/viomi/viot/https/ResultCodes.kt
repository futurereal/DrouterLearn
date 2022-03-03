package com.viomi.viot.https

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/26
 *     desc   : 结果码定义
 *     version: 1.0
 * </pre>
 */
object ResultCodes {
    const val RESULT_CODE_0 = -100 // 内部错误
    const val RESULT_CODE_1 = 100 // 成功
    const val RESULT_CODE_2 = -1 // 通用失败
    const val RESULT_CODE_3 = 1 // 接收到请求，但操作还没有完成
    const val RESULT_CODE_4 = -4001 // 属性不可读
    const val RESULT_CODE_5 = -4002 // 属性不可写
    const val RESULT_CODE_6 = -4003 // 属性、方法、事件不存在
    const val RESULT_CODE_7 = -4004 // 其它内部错误
    const val RESULT_CODE_8 = -4005 // 属性value错误
    const val RESULT_CODE_9 = -4006 // 方法in参数错误
    const val RESULT_CODE_10 = -4007 // did错误
    const val RESULT_CODE_11 = -4100 // 签名校验授权失败
    const val RESULT_CODE_12 = -4200 // 请求参数错误
    const val RESULT_CODE_13 = -5001 // 对上行数据进行RSA解密时出错
    const val RESULT_CODE_14 = -6400 // 设备不能把自己添加为自己的子设备
    const val RESULT_CODE_15 = -6100 // 设备不存在
    const val RESULT_CODE_16 = -6210 // 单个设备认证过于频繁被限流
    const val RESULT_CODE_17 = -6211 // 网关下同时在线子设备过多
    const val RESULT_CODE_18 = -6212 // 网关和子设备没有拓扑关系
    const val RESULT_CODE_19 = -6213 // 设备与用户的绑定关系不存在
    const val RESULT_CODE_20 = -6300 // 消息协议中的网关ID与消息通道中的ID不一致
    const val RESULT_CODE_21 = -6301 // 增加子设备拓扑失败
    const val RESULT_CODE_22 = -6302 // 未通过设备合法性校验
    const val RESULT_CODE_23 = -6250 // 上报的子设备不存在
    const val RESULT_CODE_24 = -6251 // 子设备已被删除
    const val RESULT_CODE_25 = -6252 // 子设备已被禁用
    const val RESULT_CODE_26 = -6253 // 子设备会话不存在
    const val RESULT_CODE_27 = -6255 // 子设备密码或者签名错误

    /**
     * 合作方电信的异常码从-7001开始，原则上只使用-7001 到 -7999 范围内的编码。
     * 无法构造请求电信的device字段时返回-7001，可能的情况如下：
     * 1. 根据设备上报的did在云端查不到信息
     * 2. 根据设备的mac无法查到电信设备的sn
     * 3. 使用电信的加密工具生成请求信息中的device时出错
     */
    const val RESULT_CODE_28 = -7001
    const val RESULT_CODE_29 = -32600 // Invalid Request
    const val RESULT_CODE_30 = 0 // 成功返回
    const val RESULT_CODE_31 = -99 // 设备不支持 Viot 协议
}

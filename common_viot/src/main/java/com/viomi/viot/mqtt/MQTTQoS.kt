package com.viomi.viot.mqtt

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/12/18
 *     desc   : 消息传输服务质量
 *     version: 1.0
 * </pre>
 */
object MQTTQoS {
    /**
     * 最多分发一次
     */
    const val QoS0 = 0

    /**
     * 至少达到一次
     */
    const val QoS1 = 1

    /**
     * 仅分发一次
     */
    const val QoS2 = 2
}

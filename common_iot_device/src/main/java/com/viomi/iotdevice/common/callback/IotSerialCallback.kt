package com.viomi.iotdevice.common.callback

import com.viomi.iotdevice.common.protocol.EventPack

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/21
 *     desc   : Iot 相关回调方法
 *     version: 1.0
 * </pre>
 */
interface IotSerialCallback {
    /**
     * 属性上报
     *
     * @param prop:       属性的 map,属性名和属性值
     * @param serialData: 属性上报的串口数据，用于透传时用
     */
    fun props(prop: MutableMap<*, *>?, serialData: String)

    /**
     * 事件上报
     *
     * @param pack:       事件上报带的参数数列
     * @param serialData: 事性上报的串口数据，用于透传时用
     */
    fun event(pack: EventPack?, serialData: String)

    /**
     * 用来以 json 格式发出上行消息 仅 miot 适用
     *
     * @param json: Json 字符串
     */
    fun json_send(json: String)

    /**
     * 上报 model 号
     *
     * @param model: 设备 model
     */
    fun model(model: String)

    /**
     * 上报 mcu 软件版本号
     *
     * @param version: mcu 版本号
     */
    fun mcu_version(version: String)

    /**
     * 进入厂测模式
     */
    fun enterTestMode()
}
package com.viomi.iotdevice.common.model

import com.viomi.iotdevice.common.model.bean.ResultBean

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : 串口结果格式化
 *     version: 1.0
 * </pre>
 */
internal object IotResultFormat {
    const val CMD_OK = "ok"

    const val CMD_ERROR = "error"

    /**
     * 成功
     */
    const val RESULT_OK = 0

    /**
     * 正忙
     */
    private const val CUSTOM_ERROR_BUSY = -97

    /**
     * 自定义错误码，串口异常(串口未打开、无法正常发送数据出去)
     */
    const val CUSTOM_ERROR_SERIAL = -98

    /**
     * 自定义错误码，返回格式异常
     */
    const val CUSTOM_ERROR_FORMAT = -99

    /**
     * 自定义错误码, 超时
     */
    private const val CUSTOM_ERROR_TIMEOUT = -100

    /**
     * 固件升级中
     */
    private const val CUSTOM_ERROR_UPDATING = -101

    /**
     * 自定义错误码, 串口断连(串口连续出现无数据返回，串口断连)
     */
    private const val CUSTOM_ERROR_NO_DATA = -102

    /**
     * 串口正忙.
     */
    val busyResult by lazy { ResultBean(CUSTOM_ERROR_BUSY, null, "serial is busy.") }

    /**
     * 串口异常.
     */
    val serialResult by lazy { ResultBean(CUSTOM_ERROR_SERIAL, null, "serial error.") }

    /**
     * 通信故障
     */
    val serialDisconnectResult by lazy { ResultBean(CUSTOM_ERROR_NO_DATA, null, "serial communication failures.") }

    /**
     * 格式错误异常.
     */
    val faultResult by lazy { ResultBean(CUSTOM_ERROR_FORMAT, null, "format error.") }

    /**
     * 下发命令超时异常.
     */
    val timeOutResult by lazy { ResultBean(CUSTOM_ERROR_TIMEOUT, null, "cmd request time out.") }

    /**
     * 固件升级中.
     */
    val upDatingResult by lazy { ResultBean(CUSTOM_ERROR_UPDATING, null, "mcu is updating.") }
}

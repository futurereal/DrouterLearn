package com.viomi.iotdevice.common.rxbus

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/21
 *     desc   : RxBus 消息定义
 *     version: 1.0
 * </pre>
 */
internal class BusEvent internal constructor(val msgId: Int, val msgObject: Any?) {

    companion object {
        private const val MSG_BASE = 1000

        /**
         * iot down 命令操作成功返回
         */
        const val MSG_DOWN_CMD_RESULT_SUCCESS = MSG_BASE + 1

        /**
         * iot down 命令操作失败返回
         */
        const val MSG_DOWN_CMD_RESULT_FAIL = MSG_BASE + 2
    }
}

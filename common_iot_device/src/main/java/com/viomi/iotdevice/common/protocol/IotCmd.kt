package com.viomi.iotdevice.common.protocol

import com.viomi.iotdevice.common.util.LogUtil

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/21
 *     desc   : Iot 串口相关指令
 *     version: 1.0
 * </pre>
 */
internal object IotCmd {
    private val TAG = IotCmd::class.java.simpleName

    // miot、miot-spec、viot 公共指令

    /**
     * 获取下行指令
     */
    const val GetDown = "get_down"

    /**
     * 结果返回
     */
    const val Result = "result"

    /**
     * 异常返回
     */
    const val Error = "error"

    /**
     * 串口协议版本号设置和获取
     */
    const val Version = "version"

    /**
     * model 设置和获取
     */
    const val Model = "model"

    /**
     * 固件版本设置和获取
     */
    const val McuVersion = "mcu_version"

//    /**
//     * 重启面板
//     */
//    const val Reboot = "reboot"

//    /**
//     * 清除 wifi 配置信息
//     */
//    const val Restore = "restore"

    /**
     * 进入工厂模式
     */
    const val Factory = "factory"

    /**
     * 会对输入进行回显
     */
    const val Echo = "echo"

    /**
     * 使用帮助
     */
    const val Help = "help"

    /**
     * 获取屏端 mac
     */
    const val Mac = "mac"

    /**
     * 获取网络信息，默认回在线
     */
    const val Net = "net"

    /**
     * 获取当前时间, 时间为 UTC+8 时间, 即中国的时间
     */
    const val Time = "time"

    /**
     * 获取 wifi 账号、wifi 密码、设备用户凭证，用于授权系统端获取用户登录信息
     */
    const val UserInfo = "user_info"

    /**
     * 发送 mesh 组网相关信息给 wifi 模组
     */
    const val SendMeshInfo = "send_mesh_info"

    /**
     * 固件主动请求升级
     */
    const val UpdateMe = "update_me"

    // json 格式指令

    /**
     * 获取下行 json 格式指令
     */
    const val JsonGetDown = "json_get_down"

    /**
     * json_ack 与 json_get_down 相对应, 用来回复下行指令执行的结果
     */
    const val JsonAck = "json_ack"

    /**
     * 用来以 json 格式发出上行消息
     */
    const val JsonSend = "json_send"

//    /**
//     * 查询 mcu 版本(已废弃)
//     */
//    const val McuVersionReq = "MIIO_mcu_version_req"

//    /**
//     * 查询 model(已废弃)
//     */
//    const val ModelReq = "MIIO_model_req"

//    /**
//     * 查询网络状态(已废弃)
//     */
//    const val NetChange = "MIIO_net_change"

    /**
     * 升级请求命令
     */
    const val Update_fw = "update_fw"

    // miot 特定指令

    /**
     * 属性上报
     */
    const val Miot_Props = "props"

    /**
     * 事件上报
     */
    const val Miot_Event = "event"

    // miot-spec 特定指令

    /**
     * 属性上报
     */
    const val Miot_Spec_Props = "properties_changed"

    /**
     * 事件上报
     */
    const val Miot_Spec_Event = "event_occured"

    /**
     * 仅双模模组需要
     */
    const val Miot_Spec_Ble_Config = "ble_config"

    /**
     * 用于调用云端的方法(暂不用处理)
     */
    const val Miot_Spec_Call = "call"

    /**
     * 清除 wifi 配置信息
     */
    const val Viot_Restore = "restore"

    /**
     * 获取设备 did
     */
    const val Viot_Did = "did"

    /**
     * 获取设备 mac
     */
    const val Viot_Mac = "mac"

    /**
     * 属性读取
     */
    const val Viot_Get_Prop = "get_properties"

    /**
     * 属性设置
     */
    const val Viot_Set_Prop = "set_properties"

    /**
     * 方法
     */
    const val Viot_Action = "action"

    /**
     * 提取 MCU 发给设备的命令
     * 命令采取空格分隔的方式，第一个连续字符串为命令名，其后可跟若干参数，最后以回车符’\r’结束（即CR或 0x0D）。
     * 命令与参数应由合法字符构成，包括字母、数字、下划线。参数中若包含字符串值，需要用双引号括起。
     *
     * @param receiveData: 接收串口数据
     */
    fun getMcuCmd(receiveData: String?): String? {
        if (receiveData.isNullOrBlank()) {
            LogUtil.e(TAG, "Receive data is null.")
            return null
        }

        /**
         * 结束符不是 \r, 协议不对
         */
        if (receiveData.indexOf("\r") <= 0) {
            LogUtil.e(TAG, "Receive Date has no end bit.")
            return null
        }

        /**
         * 提取命令, 如果有空格, 第一个空格前是命令, 如果没空格, 去了结束字符就是命令
         */
        return if (receiveData.contains(" ")) {
            receiveData.substring(0, receiveData.indexOf(" "))
        } else {
            receiveData.substring(0, receiveData.indexOf("\r"))
        }
    }

    /**
     * 提取 WiFi 模块发给设备的命令
     *
     * @param receiveData：串口接收的数据
     */
    fun getWiFiCmd(receiveData: String?): String? {
        if (receiveData.isNullOrBlank()) {
            return null
        }

        /**
         * 结束符不是 \r, 协议不对
         */
        if (receiveData.indexOf("\r") <= 0) {
            LogUtil.e(TAG, "Parse wifi cmd fail, format is error.")
            return null
        }

        /**
         * 提取方法名, 拆分命令后长度至少为 2
         */
        val commands = receiveData.replace("\r", "").split(" ").toTypedArray()
        if (commands.size < 2) {
            LogUtil.e(TAG, "Parse wifi cmd fail, command is error.")
            return receiveData
        }
        return commands[1]
    }

    /**
     * 自定义通信断连事件
     */
    fun getDisconnectEventData(isProfile: Boolean) = if (isProfile) {
        /**
         * Profile 协议 event 0 0 --->65 76 65 6e 74 20 30 20 30 0d
         */
        charArrayOf(
            0x65.toChar(),
            0x76.toChar(),
            0x65.toChar(),
            0x6e.toChar(),
            0x74.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x0d.toChar()
        )
    } else {
        /**
         * Spec协议 event_occured 0 0 --->65 76 65 6e 74 5f 6f 63 63 75 72 65 64 20 30 20 30 0d
         */
        charArrayOf(
            0x65.toChar(),
            0x76.toChar(),
            0x65.toChar(),
            0x6e.toChar(),
            0x74.toChar(),
            0x5f.toChar(),
            0x6f.toChar(),
            0x63.toChar(),
            0x63.toChar(),
            0x75.toChar(),
            0x72.toChar(),
            0x65.toChar(),
            0x64.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x0d.toChar()
        )
    }

    /**
     * 自定义通讯恢复正常事件
     */
    fun getConnectEventData(isProfile: Boolean) = if (isProfile) {
        /**
         * Profile 协议 event 0 1 --->65 76 65 6e 74 20 30 20 31 0d
         */
        charArrayOf(
            0x65.toChar(),
            0x76.toChar(),
            0x65.toChar(),
            0x6e.toChar(),
            0x74.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x20.toChar(),
            0x31.toChar(),
            0x0d.toChar()
        )
    } else {
        /**
         * Spec协议 event_occured 0 1 --->65 76 65 6e 74 5f 6f 63 63 75 72 65 64 20 30 20 31 0d
         */
        charArrayOf(
            0x65.toChar(),
            0x76.toChar(),
            0x65.toChar(),
            0x6e.toChar(),
            0x74.toChar(),
            0x5f.toChar(),
            0x6f.toChar(),
            0x63.toChar(),
            0x63.toChar(),
            0x75.toChar(),
            0x72.toChar(),
            0x65.toChar(),
            0x64.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x20.toChar(),
            0x31.toChar(),
            0x0d.toChar()
        )
    }

    /**
     * 自定义通讯数据异常事件
     */
    fun getErrorEventData(isProfile: Boolean) = if (isProfile) {
        /**
         * Profile 协议 event 0 2 --->65 76 65 6e 74 20 30 20 31 0d
         */
        charArrayOf(
            0x65.toChar(),
            0x76.toChar(),
            0x65.toChar(),
            0x6e.toChar(),
            0x74.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x20.toChar(),
            0x32.toChar(),
            0x0d.toChar()
        )
    } else {
        /**
         * Spec 协议 event_occured 0 2 --->65 76 65 6e 74 5f 6f 63 63 75 72 65 64 20 30 20 31 0d
         */
        charArrayOf(
            0x65.toChar(),
            0x76.toChar(),
            0x65.toChar(),
            0x6e.toChar(),
            0x74.toChar(),
            0x5f.toChar(),
            0x6f.toChar(),
            0x63.toChar(),
            0x63.toChar(),
            0x75.toChar(),
            0x72.toChar(),
            0x65.toChar(),
            0x64.toChar(),
            0x20.toChar(),
            0x30.toChar(),
            0x20.toChar(),
            0x32.toChar(),
            0x0d.toChar()
        )
    }
}

package com.viomi.iotdevice.iottowifi

import com.viomi.iotdevice.common.SerialParams
import com.viomi.iotdevice.common.protocol.IotCmd
import com.viomi.iotdevice.common.util.LogUtil
import com.viomi.iotdevice.iottomcu.bean.ViotProperty
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody
import java.util.regex.Pattern

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/9/12
 *     desc   : 串口数据转 Viot 协议
 *     version: 1.0
 * </pre>
 */
internal object IotToWiFiFormat {
    private val TAG = IotToWiFiFormat::class.java.simpleName

    /**
     * 解析 get_properties 命令数据
     * 命令格式：get_properties <siid> <piid> ... <siid> <piid>
     * 串口返回数据例子：down get_properties 1 2 1 3
     *
     * @param receiveData: 串口读取数据
     * @return 返回格式：result <siid> <piid> ` [value] ... <siid> <piid> ` [value]
    `</piid></siid>`</piid></siid></piid></siid></piid></siid> */
    fun getPropertiesParse(receiveData: String): String {
        val dataList = receiveData.split(" ").toTypedArray()
        val stringBuilder = StringBuilder()
        stringBuilder.append(IotCmd.Result)
        var i = 2
        while (i < dataList.size) {
            val siid = dataList[i]
            val piid = if (i + 1 < dataList.size) dataList[i + 1] else "-1"
            stringBuilder.append(" ").append(siid)
            stringBuilder.append(" ").append(piid)
            stringBuilder.append(" ").append(0)
            stringBuilder.append(" ").append(SerialParams.instance.getProp(siid, piid))
            if (i == dataList.size - 2) break
            i += 2
        }
        stringBuilder.append("\r")
        LogUtil.d(TAG, "get_properties result: $stringBuilder")
        return stringBuilder.toString()
    }

    /**
     * 解析 set_properties 命令数据
     * 命令格式：set_properties <siid> <piid> <value> ... <siid> <piid> <value>
     * 串口返回数据例子：down set_properties 1 1 10 1 4 "hello"
     *
     * @param receiveData：串口读取数据
    </value></piid></siid></value></piid></siid> */
    fun setPropertiesParse(receiveData: String): ViotSetPropRequestBody {
        val dataList = receiveData.split(" ").toTypedArray()
        val viotSetPropRequestBody = ViotSetPropRequestBody()
        var i = 2
        while (i < dataList.size) {
            val prop = ViotProperty()
            prop.sid = parseInt(dataList[i])
            prop.pid = parseInt(if (i + 1 < dataList.size) dataList[i + 1] else -1)
            prop.value = if (i + 2 < dataList.size) dataList[i + 2] else -1
            viotSetPropRequestBody.propList.add(prop)
            if (i == dataList.size - 3) break
            i += 3
        }
        return viotSetPropRequestBody
    }

    /**
     * 解析 user_info 命令返回的数据
     * 命令格式: user_info
     * 串口返回数据例子: viomi viomi3331 SDGDSGE56HRTRFH5RTJ6TYTYKJYUKUUUYI（当前 ssid + password + 凭证）
     *
     * @param receiveData: 串口读取数据
     */
    fun userInfoParse(receiveData: String): Array<String>? {
        val dataList = receiveData.split(" ").toTypedArray()
        return if (dataList.size != 3) null else dataList
    }

    private fun parseInt(`object`: Any): Int {
        return try {
            `object`.toString().trim { it <= ' ' }.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * 格式化 Mac 地址
     *
     * @param mac :   Mac 地址字符串
     */
    fun formatMac(mac: String): String {
        val regex = "[0-9a-fA-F]{12}"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(mac)
        require(matcher.matches()) { "mac format is error" }
        val stringBuilder = StringBuilder()
        for (i in 0..11) {
            val c = mac[i]
            stringBuilder.append(c)
            if ((i and 1) == 1 && i <= 9) {
                stringBuilder.append(":")
            }
        }
        LogUtil.d(TAG, "Format mac: $stringBuilder")
        return stringBuilder.toString()
    }
}

package com.viomi.iotdevice.iottomcu.protocol

import com.viomi.iotdevice.common.model.IotResultFormat
import com.viomi.iotdevice.common.protocol.EventPack
import com.viomi.iotdevice.common.protocol.IotCmd
import com.viomi.iotdevice.common.util.LogUtil
import com.viomi.iotdevice.iottomcu.bean.ViotActionRequestBody
import com.viomi.iotdevice.iottomcu.bean.ViotActionResponseBody
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropResponseBody
import com.viomi.iotdevice.iottomcu.bean.ViotProperty
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/22
 *     desc   : 串口数据转 Viot/Spec 格式
 *     version: 1.0
 * </pre>
 */
internal object IotToMcuViotFormat {
    private val TAG = IotToMcuViotFormat::class.java.simpleName

    /**
     * 属性上报键值对
     */
    private val mPropMap: MutableMap<String, Any> = HashMap()

    /**
     * 事件上报内容
     */
    private val mEventPack = EventPack()

    /**
     * 属性解析初始化
     */
    private var propStrFormat: String? = null

    /**
     * action 命令, 请求体换成 miio 芯片端格式
     *
     * @param sendData 面板下发的数据
     * @return 返回传输给串口端的数据格式
     *
     * 下发命令 ViotActionRequestBody 结构，转换成字符串格式：action <siid> <aiid> <piid> <value> ... <piid> <value>
     * 例子：action 1 1 1 10\r </value></piid></value></piid></aiid></siid>
     */
    fun actionFormat(sendData: ViotActionRequestBody?): String? {
        if (sendData == null) {
            LogUtil.e(TAG, "sendData null!")
            return null
        }
        var result = "action " + sendData.sid + " " + sendData.aid
        if (!sendData.propList.isNullOrEmpty()) {
            for (i in sendData.propList.indices) {
                result += " " + sendData.propList[i].pid
                result += " "
                var `object` = sendData.propList[i].value
                if (`object` is String) {
                    `object` = "\"" + `object`
                    `object` = `object`.toString() + "\""
                }
                result += `object`
            }
        }
        result += "\r"
        return result
    }

    /**
     * getProperties 命令, 请求体换成 miio 芯片端格式
     *
     * @param sendData 面板下发的数据
     * @return 返回传输给串口端的数据格式
     * 下发命令 ViotActionRequestBody 结构，转换成字符串格式：get_properties <siid> <piid> ... <siid> <piid>
     * 例子：get_properties 1 2 1 3 </piid></siid></piid></siid>
     */
    fun getPropertiesFormat(sendData: ViotGetPropRequestBody?): String? {
        if (sendData == null) {
            LogUtil.e(TAG, "sendData null!")
            return null
        }
        var result = "get_properties"
        var info = "get_properties"
        if (!sendData.propList.isNullOrEmpty()) {
            for (i in sendData.propList.indices) {
                result += " " + sendData.propList[i].sid
                result += " " + sendData.propList[i].pid
                info += " " + sendData.propList[i].sid
                info += "_" + sendData.propList[i].pid
            }
        }
        LogUtil.d(TAG, info)
        result += "\r"
        return result
    }

    /**
     * setProperties 命令, 请求体换成 miio 芯片端格式
     *
     * @param sendData 面板下发的数据
     * @return 返回传输给串口端的数据格式
     * 下发命令 ViotActionRequestBody 结构，转换成字符串格式：set_properties <siid> <piid> <value> ... <siid> <piid> <value>
     * 例子：set_properties 1 1 10 1 4 "hello" </value></piid></siid></value></piid></siid>
     */
    fun setPropertiesFormat(sendData: ViotSetPropRequestBody?): String? {
        if (sendData == null) {
            LogUtil.e(TAG, "sendData null!")
            return null
        }
        var result = "set_properties"
        var info = "format set_properties"
        if (!sendData.propList.isNullOrEmpty()) {
            for (i in sendData.propList.indices) {
                result += " " + sendData.propList[i].sid
                result += " " + sendData.propList[i].pid
                result += " "
                var `object` = sendData.propList[i].value
                if (`object` is String) {
                    `object` = "\"" + `object`
                    `object` = `object`.toString() + "\""
                }
                result += `object`
                info += " " + sendData.propList[i].sid
                info += "_" + sendData.propList[i].pid
                info += "_"
                info += `object`
            }
        }
        LogUtil.d(TAG, info)
        result += "\r"
        return result
    }

    /**
     * action 结果返回, miio 芯片端格式转换成 profile 协议格式
     *
     * @param CMD         IOT 命令
     * @param DOWN_CMD    下行命令
     * @param receiveData 收到 mcu 端的串口数据
     * @return 返回传给面板的数据格式
     */
    fun resultParse(CMD: String?, DOWN_CMD: String?, receiveData: String?): Any? {
        if (receiveData.isNullOrBlank()) {
            return null
        }
        val resultJSONObject = JSONObject()
        when (CMD) {
            IotCmd.Result -> {
                run {
                    // result 命令处理
                    // get_properties：获取设备属性返回 ：result <siid> <piid> <code> [value] ... <siid> <piid> <code> [value] ，例子：result 1 2 0 10 1 3 0 "hello"
                    // set_properties：设置属性返回     ：result <siid> <piid> <code> ... <siid> <piid> <code>，               例子：result 1 1 0 1 4 -4003
                    // action:方法返回                ：result <siid> <aiid> <code> <piid> <value> ... <piid> <value>    ，   例子：result 1 1 0 3 10
                    if (DOWN_CMD == null) {
                        return null
                    }
                    when {
                        DOWN_CMD.startsWith(IotCmd.Viot_Get_Prop) -> {
                            val responseBody = ViotGetPropResponseBody()
                            responseBody.serialData = receiveData
                            if (receiveData.length <= IotCmd.Result.length + 1) {
                                LogUtil.e(TAG, "receiveDateParse result ,message=")
                                responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                                return responseBody
                            }
                            val array = receiveData.split(" ").toTypedArray()
                            var info = "getResponce"
                            try {
                                /**
                                 * result 2 4 0 3 或者 result 2 4 -4003
                                 */
                                if (array.size >= 4) {
                                    var propIdCount = 0 // 属性包含的字段个数，正常是4个，如：1 2 0 10，异常是3个，如：1 3 -4001
                                    var arrayIndex = 1 // 跳过第一个字符串result
                                    do {
                                        val prop = ViotProperty()
                                        prop.sid = array[arrayIndex].toInt()
                                        info += " " + prop.sid
                                        propIdCount++
                                        prop.pid = array[arrayIndex + 1].toInt()
                                        info += "_" + prop.pid
                                        propIdCount++
                                        prop.code = array[arrayIndex + 2].toInt()
                                        info += "_" + prop.code
                                        propIdCount++
                                        if (prop.code == 0) {
                                            prop.value = getPropValue(array[arrayIndex + 3], " ") //Integer.parseInt(array[arrayIndex + 3]);
                                            info += "_" + prop.value
                                            propIdCount++
                                        }
                                        responseBody.propList.add(prop)
                                        arrayIndex += propIdCount
                                        propIdCount = 0
                                    } while (arrayIndex < array.size - 1)
                                    LogUtil.d(TAG, info)
                                } else {
                                    LogUtil.e(TAG, "receiveDateParse,array:" + array.size)
                                    responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                                }
                            } catch (e: Exception) {
                                LogUtil.e(TAG, "ERROR:" + e.message)
                                responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                            }
                            return responseBody
                        }
                        DOWN_CMD.startsWith(IotCmd.Viot_Set_Prop) -> {
                            val responseBody = ViotSetPropResponseBody()
                            responseBody.serialData = receiveData
                            if (receiveData.length <= IotCmd.Result.length + 1) {
                                LogUtil.e(TAG, "receiveDateParse result ,message=")
                                responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                                return responseBody
                            }
                            val array = receiveData.split(" ").toTypedArray()
                            var info = "setResponce"
                            try {
                                if (array.size >= 4) { // result 2 4 0 2 5 0 2 6 0
                                    var i = 1
                                    while (i < array.size) {
                                        if (i % 3 == 1) {
                                            val prop = ViotProperty()
                                            prop.sid = array[i].toInt()
                                            info += " " + prop.sid
                                            prop.pid = array[i + 1].toInt()
                                            info += "_" + prop.pid
                                            prop.code = array[i + 2].toInt()
                                            info += "_" + prop.code
                                            responseBody.propList.add(prop)
                                        }
                                        i++
                                    }
                                    LogUtil.d(TAG, info)
                                } else {
                                    LogUtil.e(TAG, "receiveDateParse,array:" + array.size)
                                    responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                                }
                            } catch (e: Exception) {
                                LogUtil.e(TAG, "ERROR:" + e.message)
                                responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                            }
                            return responseBody
                        }
                        /**
                         * result <siid> <aiid> <code> <piid> <value> ... <piid> <value>
                         */
                        DOWN_CMD.startsWith(IotCmd.Viot_Action) -> {
                            val responseBody = ViotActionResponseBody()
                            responseBody.serialData = receiveData
                            if (receiveData.length <= IotCmd.Result.length + 1) {
                                LogUtil.e(TAG, "receiveDateParse result ,message=")
                                responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                                return responseBody
                            }
                            val array = receiveData.split(" ").toTypedArray()
                            var info = "actionResponce"
                            try {
                                if (array.size >= 4) {
                                    responseBody.sid = array[1].toInt()
                                    info += " " + responseBody.sid
                                    responseBody.aid = array[2].toInt()
                                    info += "_" + responseBody.aid
                                    responseBody.code = array[3].toInt()
                                    info += "_" + responseBody.code
                                    var i = 4
                                    while (i < array.size) {
                                        if (i % 2 == 0) {
                                            val prop = ViotProperty()
                                            prop.pid = array[i].toInt()
                                            info += ":" + prop.pid
                                            prop.value = getPropValue(array[i + 1], " ") //Integer.parseInt(array[i+1]);
                                            info += "_" + prop.value
                                            responseBody.propList.add(prop)
                                        }
                                        i++
                                    }
                                    LogUtil.d(TAG, info)
                                } else {
                                    LogUtil.e(TAG, "receiveDateParse,array:" + array.size)
                                    responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                                }
                            } catch (e: Exception) {
                                responseBody.code = ViotActionResponseBody.CODE_DATA_FORMAT_ERROR
                                LogUtil.e(TAG, "ERROR:" + e.message)
                            }
                            return responseBody
                        }
                        /**
                         * 下发的是升级命令,判断是否收到result "ready"
                         */
                        DOWN_CMD.startsWith(IotCmd.Update_fw) -> {
                            //result "ready"   转成：{"code":0,"message":"ok","result":["ready"]}
                            LogUtil.d(TAG, "DOWN_CMD is:" + IotCmd.Update_fw)
                            if (receiveData.length <= IotCmd.Result.length + 1) {
                                LogUtil.e(TAG, "receiveDateParse result ,message=")
                                return fautResult
                            }
                            val params = receiveData.substring(IotCmd.Result.length + 1, receiveData.length)
                            val array = params.split(",").toTypedArray()
                            return if (!array.isNullOrEmpty()) {
                                val length = array.size
                                val jsonArray = JSONArray()
                                propStrFormat = null
                                var i = 0
                                while (i < length) {
                                    val msg = array[i]
                                    try {
                                        val value = getPropValue(msg, ",")
                                        if (value != null) {
                                            jsonArray.put(value)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        LogUtil.e(TAG, "resultFormat result ,message=" + e.message)
                                        return fautResult
                                    }
                                    i++
                                }
                                try {
                                    resultJSONObject.put("code", 0)
                                    resultJSONObject.put("message", "ok")
                                    resultJSONObject.put("result", jsonArray)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    LogUtil.e(TAG, "receiveDateParse result ,message=" + e.message)
                                    return fautResult
                                }
                                resultJSONObject.toString()
                            } else {
                                LogUtil.e(TAG, "receiveDateParse,result:" + array.size)
                                fautResult
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
            /**
             * error 命令处理, Spec 中只在下行指令异常时会返回 error "undefined command" -9999
             */
            IotCmd.Error -> {
            }
        }
        return null
    }

    /**
     * 属性上报数据解析
     *
     * @param receiveData 属性上报数据  格式：properties_changed <siid> <piid> <value> ... <siid> <piid> <value>，各字段间用空格隔开
     * @return Map结果, key由siid和piid构成，如properties_changed 1 1 17 1 2 "hi"的map为"1.1":17 ,"1.2":"hi"</value></piid></siid></value></piid></siid>
     */
    fun propsDataParse(receiveData: String?): MutableMap<*, *>? {
        if (receiveData.isNullOrBlank()) {
            return null
        }
        val array = receiveData.split(" ").toTypedArray()
        /**
         * 参数最少有四个
         */
        if (array.size < 4) {
            return null
        }
        var key = ""
        var value: Any?
        propStrFormat = null
        var propStr = ""
        var info = "properties_changed"
        mPropMap.clear()
        for (i in 1 until array.size) {
            when {
                i % 3 == 1 -> propStr = array[i]
                i % 3 == 2 -> {
                    propStr += "." + array[i]
                    key = propStr
                    info += " " + key + "_"
                }
                else -> {
                    try {
                        value = getPropValue(array[i], " ")
                        info += value
                        if (value != null) {
                            mPropMap[key] = value
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LogUtil.e(TAG, "propsDataParse result ,message=" + e.message)
                    }
                }
            }
        }
        LogUtil.d(TAG, info)
        return mPropMap
    }

    /**
     * 事件上报数据解析
     *
     * @param receiveData 属性上报数据  格式：event_occured <siid> <eiid> <piid> <value> ... <piid> <value>，各字段间用空格隔开
     * @return name由siid和eiid构成, 如"1.2"</value></piid></value></piid></eiid></siid>
     */
    fun eventDataParse(receiveData: String?): EventPack? {
        if (receiveData.isNullOrBlank()) {
            return null
        }
        val array = receiveData.split(" ").toTypedArray()
        /**
         * 必定有类型字符, siid 和 eiid
         */
        if (array.size < 3) {
            return null
        }
        mEventPack.propMap.clear()
        propStrFormat = null
        var eventStr = array[1]
        eventStr += "." + array[2]
        mEventPack.name = eventStr
        var propIdKey: Int
        var info = "event_occured $eventStr"
        var i = 3
        while (i < array.size) {
            //if (i % 2 == 1) { // 单数是 piid，双数是对应的 value
            try {
                propIdKey = array[i].toInt()
                i++
                var value = getPropValue(array[i], " ")
                while (value == null && (i + 1) < array.size) {
                    i++
                    value = getPropValue(array[i], " ")
                }
                info += propIdKey.toString() + "_" + value
                if (value != null) {
                    mEventPack.propMap[propIdKey] = value
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(TAG, "eventDataParse,error!")
            }
            i++
        }
        LogUtil.d(TAG, info)
        return mEventPack
    }

    /**
     * 命令中字符串转化成对应属性
     *
     * @param msg    要格式化的字符串，如： 110 "mode" "Yes,I'm fine"  26.0
     * @param divide 间隔符
     * @return null 代表不是完整的字属性，非 null 代表完整的属性
     */
    private fun getPropValue(msg: String, divide: String): Any? {
        /**
         * 整数的：前后没双引号, 而且没字符串前置, propStrFormat
         */
        return if (msg.indexOf("\"") != 0 && msg.lastIndexOf("\"") != msg.length - 1 && propStrFormat == null) {
            if (msg.contains(".")) {
                msg.toFloat()
            } else {
                try {
                    msg.toInt()
                } catch (e: NumberFormatException) {
                    msg.toBoolean()
                }
            }
        } else {
            /**
             * 字符有"", 是单一字符串
             */
            if (msg.indexOf("\"") == 0 && msg.lastIndexOf("\"") == msg.length - 1) {
                msg.substring(1).substring(0, msg.length - 1)
            }
            /**
             * 字符串组成部分, 双引号开头, 无双引号, 双引号结尾
             */
            else {
                when {
                    /**
                     * 字符有"开头,是字符串属性的开头部分
                     */
                    msg.indexOf("\"") == 0 -> {
                        propStrFormat = msg.substring(1)
                        null
                    }
                    /**
                     * 字符有"结尾, 是字符串属性的结束部分
                     */
                    msg.lastIndexOf("\"") == msg.length - 1 -> {
                        propStrFormat += " ${msg.substring(0, msg.length - 1)}"
                        val strValue = propStrFormat
                        propStrFormat = null
                        strValue
                    }
                    /**
                     * 无", 是字符串属性的中间部分
                     */
                    else -> {
                        propStrFormat += divide + msg
                        null
                    }
                }
            }
        }
    }

    /**
     * 格式错误异常
     */
    private val fautResult: String
        get() {
            return JSONObject().apply {
                kotlin.runCatching {
                    put("code", IotResultFormat.CUSTOM_ERROR_FORMAT)
                    put("message", "format error")
                    put("result", "")
                }.onFailure { it.printStackTrace() }
            }.toString()
        }
}

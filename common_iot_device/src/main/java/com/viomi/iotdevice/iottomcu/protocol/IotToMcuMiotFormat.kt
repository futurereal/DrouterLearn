package com.viomi.iotdevice.iottomcu.protocol

import android.util.Log
import com.viomi.iotdevice.common.model.IotResultFormat
import com.viomi.iotdevice.common.protocol.EventPack
import com.viomi.iotdevice.common.protocol.IotCmd
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/22
 *     desc   : 串口数据转 miio profile 格式
 *     version: 1.0
 * </pre>
 */
internal object IotToMcuMiotFormat {
    private val TAG = IotToMcuMiotFormat::class.java.simpleName

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
    private var mPropStrFormat: String? = null

    /**
     * action命令, profile 协议格式转换成 miio 芯片端格式
     *
     * @param sendData 面板下发的数据
     * @return 返回传输给串口端的数据格式
     * 下发命令：{"id":1,"method":"set_f4_used","params":[0,0]}，转换成：set_f4_used 0,0\r
     */
    fun actionFormat(sendData: JSONObject?): String? {
        if (sendData == null || sendData.length() == 0) {
            Log.e(TAG, "actionFormat json null!")
            return null
        }
        val method = sendData.optString("method")
        val params = sendData.optJSONArray("params")
        var result = method
        if (params != null && params.length() > 0) {
            result += " "
            for (i in 0 until params.length()) {
                kotlin.runCatching {
                    var `object` = params[i]
                    if (`object` is String) {
                        `object` = "\"" + `object`
                        `object` = `object`.toString() + "\""
                    }
                    result += `object`
                    if (i != params.length() - 1) {
                        result += ","
                    }
                }.onFailure {
                    Log.e(TAG, "actionFormat json format params error!")
                    it.printStackTrace()
                }
            }
        }
        result += "\r"
        return result
    }

    /**
     * 判断是否 OTA 升级准备回复
     */
    fun isOtaResultReady(receiveData: String?): Boolean {
        return receiveData == "result \"ready\""
    }

    /**
     * action 结果返回, miio 芯片端格式转换成 profile 协议格式
     *
     * @param command IOT 命令
     * @param receiveData 收到 mcu 端的串口数据
     */
    fun actionResultParse(command: String?, receiveData: String?): String? {
        if (receiveData.isNullOrBlank()) {
            return null
        }
        val resultJSONObject = JSONObject()
        when (command) {
            IotCmd.Result -> {
                // result 命令处理
                // 芯片端单个属性      ：result "ok"   转成：{"code":0,"message":"ok","result":["ok"]}
                // 芯片端多个属性      ：result 123,"abc"   转成：{"code":0,"message":"ok","result":[123,"abc"]}
                // 芯片端属性包含逗号  ：result 110,"Yes,I'm fine"   转成：{"code":0,"message":"ok","result":[123,"Yes, I'm fine"]}
                if (receiveData.length <= IotCmd.Result.length + 1) {
                    Log.e(TAG, "receiveDateParse result ,message=")
                    return fautResult
                }
                val params = receiveData.substring(IotCmd.Result.length + 1, receiveData.length)
                val array = params.split(",").toTypedArray()
                return if (!array.isNullOrEmpty()) {
                    val length = array.size
                    val jsonArray = JSONArray()
                    mPropStrFormat = null
                    var i = 0
                    while (i < length) {
                        val msg = array[i]
                        kotlin.runCatching { getPropValue(msg, ",")?.let { jsonArray.put(it) } }.onFailure {
                            it.printStackTrace()
                            Log.e(TAG, "resultFormat result ,message=" + it.message)
                            return fautResult
                        }
                        i++
                    }
                    kotlin.runCatching {
                        resultJSONObject.put("code", 0)
                        resultJSONObject.put("message", "ok")
                        resultJSONObject.put("result", jsonArray)
                    }.onFailure {
                        it.printStackTrace()
                        Log.e(TAG, "receiveDateParse result ,message=" + it.message)
                        return fautResult
                    }
                    resultJSONObject.toString()
                } else {
                    Log.e(TAG, "receiveDateParse,result:" + array.size)
                    return fautResult
                }
            }
            IotCmd.Error -> {
                // error 命令处理
                // 芯片端：error "memory error" -5003   转成：{"code":-5003,"message":"memory error","result":""}
                val array = arrayOfNulls<String>(3)
                val firstDivide = receiveData.indexOf(" ")
                val lastDivide = receiveData.lastIndexOf(" ")
                return if (firstDivide > 0 && lastDivide > firstDivide + 1 && lastDivide < receiveData.length) {
                    array[0] = receiveData.substring(0, firstDivide)
                    array[1] = receiveData.substring(firstDivide + 1, lastDivide)
                    array[2] = receiveData.substring(lastDivide + 1)
                    var message = array[1]
                    if ((message?.length ?: 0) < 3 || message?.indexOf("\"") != 0 || message.lastIndexOf("\"") != message.length - 1) {
                        return fautResult
                    }
                    message = message.substring(1, message.length - 1)
                    val code: Int? = try {
                        array[2]?.toInt()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        return fautResult
                    }
                    try {
                        resultJSONObject.put("code", code)
                        resultJSONObject.put("message", message)
                        resultJSONObject.put("result", "")
                        resultJSONObject.toString()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e(TAG, "receiveDateParse error ,message=" + e.message)
                        fautResult
                    }
                } else {
                    Log.e(TAG, "receiveDateParse,error:" + array.size)
                    fautResult
                }
            }
        }
        return null
    }

    /**
     * 属性上报数据解析
     *
     * @param receiveData 属性上报数据  格式：props <prop_name_1> <value_1> <prop_name_2> <value_2> ...，各字段间用空格隔开</value_2></prop_name_2></value_1></prop_name_1>
     */
    fun propsDataParse(receiveData: String?): MutableMap<*, *>? {
        if (receiveData.isNullOrBlank()) {
            return null
        }
        val array = receiveData.split(" ").toTypedArray()
        /**
         * 参数一定是名值对, 并且最少有一对
         */
        if (array.size < 3 || array.size % 2 == 0) {
            return null
        }
        var key = ""
        var value: Any?
        mPropStrFormat = null
        for (i in 1 until array.size) {
            if (i % 2 == 1) {
                key = array[i]
            } else {
                try {
                    value = getPropValue(array[i], " ")
                    value?.let { mPropMap[key] = it }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "porpsDataParse result ,message=" + e.message)
                }
            }
        }
        return mPropMap
    }

    /**
     * 事件上报数据解析
     *
     * @param receiveData 属性上报数据  格式：event <event_name> <value_1> <value_2> ...，各字段间用空格隔开</value_2></value_1></event_name>
     */
    fun eventDataParse(receiveData: String?): EventPack? {
        if (receiveData.isNullOrBlank()) {
            return null
        }
        val array = receiveData.split(" ").toTypedArray()
        if (array.size < 2) {
            return null
        }
        mEventPack.propList.clear()
        mPropStrFormat = null
        for (i in 1 until array.size) {
            if (i == 1) {
                mEventPack.name = array[i]
            } else {
                try {
                    val value = getPropValue(array[i], " ")
                    value?.let { mEventPack.propList.add(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "porpsDataParse result ,message=" + e.message)
                }
            }
        }
        return mEventPack
    }

    /**
     * 命令中字符串装化成对应属性
     *
     * @param msg 要格式化的字符串，如： 110 "mode" "Yes,I'm fine"  26.0
     * @param divide 间隔符
     * @return  null 代表不是完整的字属性，非null代表完整的属性
     */
    private fun getPropValue(msg: String, divide: String): Any? {
        /**
         * 整数的：前后没双引号，而且没字符串前置，propStrFormat 不为 null 时说明前面是字符串的注册部分
         */
        return if (msg.indexOf("\"") != 0 && msg.lastIndexOf("\"") != msg.length - 1 && mPropStrFormat == null) {
            if (msg.contains(".")) {
                msg.toFloat()
            } else {
                msg.toInt()
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
                     * 字符有"开头, 是字符串属性的开头部分
                     */
                    msg.indexOf("\"") == 0 -> {
                        mPropStrFormat = msg.substring(1)
                        null
                    }
                    /**
                     * 字符有"结尾, 是字符串属性的结束部分
                     */
                    msg.lastIndexOf("\"") == msg.length - 1 -> {
                        mPropStrFormat += " ${msg.substring(0, msg.length - 1)}"
                        val strValue = mPropStrFormat
                        mPropStrFormat = null
                        strValue
                    }
                    /**
                     * 无", 是字符串属性的中间部分
                     */
                    else -> {
                        mPropStrFormat += divide + msg
                        null
                    }
                }
            }
        }
    }

    /**
     * 串口异常
     */
    val serialResult: String
        get() {
            return JSONObject().apply {
                kotlin.runCatching {
                    put("code", IotResultFormat.CUSTOM_ERROR_SERIAL)
                    put("message", "serial error")
                    put("result", "")
                }.onFailure { it.printStackTrace() }
            }.toString()
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

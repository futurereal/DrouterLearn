package com.viomi.iotdevice

import android.content.Context
import android.util.Log
import com.viomi.iotdevice.common.callback.CommonCallback
import com.viomi.iotdevice.common.callback.IotSerialCallback
import com.viomi.iotdevice.common.callback.ProgressCallback
import com.viomi.iotdevice.common.http.IotApiClient
import com.viomi.iotdevice.common.util.LogUtil
import com.viomi.iotdevice.iotmesh.IotMeshManager
import com.viomi.iotdevice.iotmesh.callback.IotMeshCallback
import com.viomi.iotdevice.iottomcu.IotToMcuSerialManager
import com.viomi.iotdevice.iottomcu.bean.ViotActionRequestBody
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody
import com.viomi.iotdevice.iottowifi.IotToWiFiSerialManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/10/22
 *     desc   : Iot 串口管理
 *     version: 1.0
 * </pre>
 */
class ViomiIotManager {
    internal lateinit var pAppContext: Context

    var config: IotSerialConfig? = null
        private set

    /**
     * 是否已初始化串口
     */
    private var mIsInitialized = false

    /**
     * 打开/关闭打印本模块打印调试信息
     */
    fun enableLog(enable: Boolean) {
        LogUtil.logEnable = enable
        IotApiClient.instance.logEnable = enable
        IotToMcuSerialManager.instance.enableSerialJniLog = enable
        IotToWiFiSerialManager.instance.serialJniLog = enable
    }

    /**
     * IOT 相关配置初始化
     *
     * @param context: Context
     * @param config:  配置
     */
    fun init(context: Context?, config: IotSerialConfig?) {
        if (context == null) {
            LogUtil.e(TAG, "Serial init fail, context is null.")
            return
        }
        if (config == null) {
            LogUtil.e(TAG, "Serial init fail, config is null.")
            return
        }
        if (mIsInitialized) {
            LogUtil.e(TAG, "Serial has been initialized.")
            return
        }
        this.config = config
        mIsInitialized = true
        pAppContext = context.applicationContext
        IotApiClient.instance.init(config.isDebugEnv)

        // 跟 MCU 通信串口初始化
        if (config.mcuSerialPath.isNotBlank() && config.mcuSerialBaudRate != 0) {
            IotToMcuSerialManager.instance.mac = config.mac ?: ""
            IotToMcuSerialManager.instance.cmdHandler = handler
            IotToMcuSerialManager.instance.readPeriod = config.readPeriod
            IotToMcuSerialManager.instance.errorCount = config.errorCount
            IotToMcuSerialManager.instance.open(config.mcuSerialPath, config.mcuSerialBaudRate, config.iotType)
        }
        // 跟 WiFi 模块通信串口初始化
        if (!config.wifiSerialPath.isNullOrBlank() && config.wifiSerialBaudRate != 0) {
            IotToWiFiSerialManager.instance.open(config.wifiSerialBaudRate, config.wifiSerialPath)
        }
    }

    /**
     * 关闭串口
     */
    fun close() {
        mIsInitialized = false
        handler = null
        IotToMcuSerialManager.instance.close()
        IotToWiFiSerialManager.instance.close()
    }

    /**
     * 串口是否繁忙
     */
    val isSerialBusy: Boolean
        get() = IotToMcuSerialManager.instance.isSerialBusy

    /**
     * 设置数据上报 handler
     */
    var handler: IotSerialCallback? = null

    /**
     * action，显示板向主控板发送数据 (适用于miot profile 协议)
     *
     * @param method:   发送 iot 命令数据的 method 方法，如{"id":1,"method":"set_f4_used","params":[0,0]}
     * @param callback: 回调返回 iot action 结果,如：{"code":0,"message":"ok","result":[123,"abc"]}
     */
    fun action(method: String, array: Array<Any?>?, callback: CommonCallback?) {
        if (!mIsInitialized) {
            LogUtil.e(TAG, "It's not initialized yet.")
            return
        }
        kotlin.runCatching {
            JSONObject().apply {
                put("id", 1)
                put("method", method)
                val jsonArray = JSONArray()
                if (!array.isNullOrEmpty()) {
                    for (`object` in array) {
                        jsonArray.put(`object`)
                    }
                }
                put("params", jsonArray)
                LogUtil.d(TAG, "$method   params:$this")
                IotToMcuSerialManager.instance.iotAction(this, callback)
            }
        }.onFailure { it.printStackTrace() }
    }

    /**
     * action，显示板向主控板发送 json 数据  (适用于 miot profile 协议)
     *
     * @param jsonString: 发送iot json命令数据，如{"method":"set_mode","params":{"mode":5, "temp": 25.5, "wind_level":[2], "wind_percent":[80]}}
     * @param callback:   例子：{"code":0,"message":"ok","result":{"result": ["ok"]}}
     * 回调返回如下：
     * code:結果代碼，成功：0，失敗：对应的错误代码
     * value:对应的json返回结果,如：{"result": ["ok"]}
     * message:附带信息
     */
    fun actionJson(jsonString: String?, callback: CommonCallback?) {
        if (!mIsInitialized) {
            LogUtil.e(TAG, "It's not initialized yet")
            return
        }
        IotToMcuSerialManager.instance.iotActionJson(jsonString, callback)
    }

    /**
     * action，显示板向主控板发送 action 数据 (适用于viot、miot-spec 协议)
     *
     * @param actionBody: action 请求结构体
     */
    fun action(actionBody: ViotActionRequestBody?, callback: CommonCallback?) {
        IotToMcuSerialManager.instance.iotAction(actionBody, callback)
    }

    /**
     * action，显示板向主控板发送 action 数据 透传数据 (适用于viot、miot-spec 协议)
     *
     * @param serialData 串口透传数据
     */
    fun action(serialData: String?, callback: CommonCallback?) {
        IotToMcuSerialManager.instance.iotAction(serialData, callback)
    }

    /**
     * set_properties，显示板向主控板设置属性(适用于viot、miot-spec 协议)
     */
    fun setProperties(requestBody: ViotSetPropRequestBody?, callback: CommonCallback?) {
        IotToMcuSerialManager.instance.iotSetProperties(requestBody, callback)
    }

    /**
     * set_properties   显示板向主控板设置属性，透传数据(适用于viot、miot-spec 协议)
     *
     * @param serialData  串口透传数据
     * @param callback
     */
    fun setProperties(serialData: String?, callback: CommonCallback?) {
        IotToMcuSerialManager.instance.iotSetProperties(serialData, callback)
    }

    /**
     * get_properties，显示板向主控板获取属性(适用于viot、miot-spec 协议)
     */
    fun getProperties(requestBody: ViotGetPropRequestBody?, callback: CommonCallback?) {
        IotToMcuSerialManager.instance.iotGetProperties(requestBody, callback)
    }

    /**
     * 获取 Android Device To WiFi Mesh 组网二维码.
     */
    val deviceToWiFiMeshQR: Unit
        get() {
            IotMeshManager.instance.createDeviceToWiFiMeshQR()
        }

    /**
     * 重置 WiFi 模组.
     */
    fun resetMesh() = IotMeshManager.instance.reset()

    /**
     * 取消 Mesh 组网回调.
     */
    fun removeMesh() = IotMeshManager.instance.removeCallback()


    /**
     * 设置 Mesh 组网回调监听.
     */
    fun setMeshCallback(iotMeshCallback: IotMeshCallback?) {
        IotMeshManager.instance.iotMeshCallback = iotMeshCallback
    }

    /**
     * ota升级
     *
     * @param filePath: mcu ota文件路径
     * @param callback: 升级进度
     */
    fun otaStart(filePath: String, callback: ProgressCallback?) {
        if (!mIsInitialized) {
            Log.e(TAG, "It's not initialized yet")
            throw RuntimeException("serial post not initialized")
        }
        /**
         * 文件校验
         */
        val file = File(filePath)
        if (!file.exists() || !file.isFile || file.length() < 1024) {
            throw RuntimeException("file error")
        }

        /**
         * 发送升级命令
         */
        val commonCallback = object : CommonCallback {
            override fun onReceiveResult(resultCode: Int?, result: Any?, desc: String?) {
                kotlin.runCatching {
                    val jsonObject = JSONObject(result.toString())
                    if (resultCode == 0 && jsonObject.optInt("code") == 0) {
                        val jsonArray = jsonObject.optJSONArray("result")
                        if (jsonArray?.length() == 1 && jsonArray[0] == "ready") {
                            /**
                             * 收到ready，开始升级
                             */
                            IotToMcuSerialManager.instance.otaProgress(filePath, callback)
                            return
                        }
                    }
                    callback?.onResult(false, -1, "result error")
                }.onFailure {
                    it.printStackTrace()
                    callback?.onResult(false, -1, "json error")
                }
            }
        }
        IotToMcuSerialManager.instance.sendUpdateFirmware(commonCallback)
    }

    companion object {
        private val TAG = ViomiIotManager::class.java.simpleName

        val instance: ViomiIotManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ViomiIotManager()
        }
    }
}

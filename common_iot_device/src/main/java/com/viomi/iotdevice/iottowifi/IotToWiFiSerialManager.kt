package com.viomi.iotdevice.iottowifi

import com.viomi.iotdevice.ViomiIotManager
import com.viomi.iotdevice.common.SerialParams
import com.viomi.iotdevice.common.callback.CommonCallback
import com.viomi.iotdevice.common.model.IotResultFormat
import com.viomi.iotdevice.common.protocol.IotCmd
import com.viomi.iotdevice.common.util.IgnoreErrorLambdaSubscriber
import com.viomi.iotdevice.common.util.LogUtil
import com.viomi.iotdevice.common.util.RxSchedulerUtil
import com.viomi.iotdevice.iotmesh.IotMeshManager
import com.viomi.iotdevice.iottomcu.IotToMcuSerialManager
import com.viomi.iotdevice.iottomcu.bean.ViotActionResponseBody
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropResponseBody
import com.viomi.serialport.jniSERIAL
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/9/6
 *     desc   : Android Device To WiFi Serial Communication Manager.
 *     version: 1.0
 * </pre>
 */
internal class IotToWiFiSerialManager {
    private var mWriteSubscription: Subscription? = null
    private var mReadSubscription: Subscription? = null
    private val mJniSERIAL: jniSERIAL by lazy { jniSERIAL() }

    private var mIsStartWrite = true
    private var mMeshCount = 0
    private var mReadCount = 0
    private var mCacheCommand: String? = null

    fun open(baudRate: Int, serialPath: String?) {
        if (jniSERIAL.no_serial_lib) {
            LogUtil.e(TAG, "To wifi serial open fail, no serial lib.")
            return
        }
        LogUtil.d(TAG, "To wifi serial init start, baudRate: $baudRate, serialPath: $serialPath")
        mJniSERIAL.log_enable(serialJniLog)
        val result = mJniSERIAL.init(mJniSERIAL, serialPath, baudRate, 8, 'n', 1)
        if (result == 0) {
            LogUtil.d(TAG, "To wifi serial init success.")
            subscribeWrite()
        } else {
            LogUtil.e(TAG, "To wifi serial init fail, result: $result")
        }
    }

    fun close() {
        mWriteSubscription?.cancel()
        mReadSubscription?.cancel()
        mJniSERIAL.exit()
        LogUtil.d(TAG, "To wifi serial close success.")
    }

    private fun subscribeWrite() {
        mIsStartWrite = true
        Flowable.interval(0, WRITE_PERIOD, TimeUnit.MILLISECONDS)
            .onBackpressureDrop()
            .compose(RxSchedulerUtil.schedulerFlowableTransformer2())
            .onTerminateDetach()
            .retryWhen { it.flatMap { Flowable.timer(WRITE_PERIOD, TimeUnit.MILLISECONDS) } }
            .subscribe(IgnoreErrorLambdaSubscriber<Long>({
                mWriteSubscription?.request(1)
                takeIf { mIsStartWrite && serialWrite() }?.run { serialRead() }
            }, { LogUtil.e(TAG, it.message.toString()) }, { LogUtil.d(TAG, "subscribeWrite onComplete.") },
                {
                    it.request(1)
                    mWriteSubscription = it
                })
            )
    }

    @Synchronized
    private fun serialWrite(): Boolean {
        var params = if (command.isNullOrBlank()) {
            if (mMeshCount >= 4 && !SerialParams.instance.meshStatus) {
                IotCmd.UserInfo
            } else {
                IotCmd.GetDown
            }
        } else {
            command
        }
        mMeshCount = if (mMeshCount >= 4) 0 else mMeshCount + 1
        if (params?.endsWith("\r") != true) params += "\r"
        LogUtil.d(TAG, "To wifi serial write param: $params")
        mCacheCommand = params
        val data = IntArray(params?.length ?: 0)
        val dataChars = params?.toCharArray()
        dataChars?.forEachIndexed { index: Int, result: Char -> data[index] = result.code }
        mJniSERIAL.wr_data = data
        val size: Int = mJniSERIAL.serial_write(mJniSERIAL, mJniSERIAL.wr_data.size)
        if (size != data.size) {
            LogUtil.e(TAG, "To wifi serial write fail, size = $size")
            return false
        }
        command = null
        if (ViomiIotManager.instance.config?.did.isNullOrBlank() && mCacheCommand?.contains(IotCmd.Viot_Did) != true) {
            command = IotCmd.Viot_Did
        } else if (ViomiIotManager.instance.config?.mac.isNullOrBlank() && mCacheCommand?.contains(IotCmd.Viot_Mac) != true) {
            command = IotCmd.Viot_Mac
        }
        return true
    }

    @Synchronized
    private fun serialRead() {
        mIsStartWrite = false
        mReadCount = 0
        Flowable.interval(READ_DELAY, READ_DELAY, TimeUnit.MILLISECONDS)
            .onBackpressureDrop()
            .compose(RxSchedulerUtil.schedulerFlowableTransformer2())
            .onTerminateDetach()
            .takeUntil { mReadCount > 2 || mIsStartWrite }
            .retryWhen { it.flatMap { Flowable.timer(READ_DELAY, TimeUnit.MILLISECONDS) } }
            .subscribe(IgnoreErrorLambdaSubscriber<Long>({
                mReadCount++
                if (mReadCount == 2) mIsStartWrite = true
                val length: Int = mJniSERIAL.serial_read(mJniSERIAL)
                LogUtil.d(TAG, "To wifi serial read length = $length")
                serialReadParse(mJniSERIAL.rd_data, length)
                mReadSubscription?.request(1)
            }, { LogUtil.e(TAG, it.message.toString()) }, { LogUtil.d(TAG, "serialRead onComplete.") },
                {
                    it.request(1)
                    mReadSubscription = it
                })
            )
    }

    private fun serialReadParse(data: IntArray?, length: Int) {
        if (data == null || length == 0) {
            LogUtil.e(TAG, "To wifi serial read parse fail.")
            return
        }
        val dataChar = CharArray(length)
        for (i in dataChar.indices) {
            dataChar[i] = (data[i] and 0xFF).toChar()
        }
        var dataStr = String(dataChar)
        LogUtil.d(TAG, "To wifi serial read format: $dataStr")
        val iotCommand = IotCmd.getWiFiCmd(dataStr)
        if (iotCommand.isNullOrBlank()) {
            LogUtil.e(TAG, "To wifi serial read parse fail, data is error.")
            return
        }
        val crIndex = dataStr.indexOf("\r")
        dataStr = dataStr.substring(0, crIndex)
        LogUtil.d(TAG, "To wifi serial read command: $iotCommand")
        var sendCmd: String? = null
        if (!mCacheCommand.isNullOrBlank()) {
            mCacheCommand = mCacheCommand?.replace("\r", "")
            val array = mCacheCommand?.split(" ")?.toTypedArray()
            sendCmd = array?.get(0)
        }
        when (iotCommand) {
            IotCmd.Viot_Get_Prop ->
                /**
                 * 应答格式：result <siid> <piid> <code> [value] ... <siid> <piid> <code> [value]
                 */
                command = IotToWiFiFormat.getPropertiesParse(dataStr)
            IotCmd.Viot_Set_Prop ->
                /**
                 * 应答格式：result <siid> <piid> <code> ... <siid> <piid> <code>
                 */
                IotToMcuSerialManager.instance.iotSetProperties(
                    IotToWiFiFormat.setPropertiesParse(dataStr), object : CommonCallback {
                        override fun onReceiveResult(resultCode: Int?, result: Any?, desc: String?) {
                            if (result is ViotSetPropResponseBody) {
                                LogUtil.d(TAG, "set_properties result: " + result.serialData)
                                command = if (resultCode == 0) {
                                    val stringBuilder = StringBuilder()
                                    stringBuilder.append(IotCmd.Result)
                                    for (prop in result.propList) {
                                        stringBuilder.append(" ").append(prop.sid)
                                        stringBuilder.append(" ").append(prop.pid)
                                        stringBuilder.append(" ").append(prop.code)
                                    }
                                    stringBuilder.append("\r")
                                    stringBuilder.toString()
                                } else {
                                    result.serialData
                                }
                            }
                        }
                    })
            IotCmd.Viot_Action ->
                /**
                 * 应答格式: result <siid> <aiid> <code> <piid> <value> ... <piid> <value>
                 */
                IotToMcuSerialManager.instance.iotAction(dataStr, object : CommonCallback {
                    override fun onReceiveResult(resultCode: Int?, result: Any?, desc: String?) {
                        if (result is ViotActionResponseBody) {
                            LogUtil.d(TAG, "action result: " + result.serialData)
                            command = if (resultCode == 0) {
                                val stringBuilder = StringBuilder()
                                stringBuilder.append(IotCmd.Result).append(" ").append(result.sid).append(" ").append(result.aid)
                                    .append(" ").append(result.code)
                                for (prop in result.propList) {
                                    stringBuilder.append(" ").append(prop.pid)
                                    stringBuilder.append(" ").append(prop.value)
                                }
                                stringBuilder.append("\r")
                                stringBuilder.toString()
                            } else {
                                result.serialData
                            }
                        }
                    }
                })
            IotResultFormat.CMD_OK -> if (sendCmd != null) {
                when (sendCmd) {
                    IotCmd.SendMeshInfo -> SerialParams.instance.saveMeshStatus(true)
                    IotCmd.Viot_Restore -> {
                        mMeshCount = 0
                        SerialParams.instance.saveMeshStatus(false)
                    }
                }
            }
            else -> if (sendCmd != null) {
                when (sendCmd) {
                    IotCmd.UserInfo -> IotMeshManager.instance.wifiToDeviceMesh(IotToWiFiFormat.userInfoParse(dataStr))
                    IotCmd.Viot_Did -> ViomiIotManager.instance.config?.did = dataStr
                    IotCmd.Viot_Mac -> ViomiIotManager.instance.config?.mac = IotToWiFiFormat.formatMac(dataStr)
                }
            }
        }
        mIsStartWrite = true
    }

    var command: String? = null

    var serialJniLog: Boolean = false

    companion object {
        private val TAG = IotToWiFiSerialManager::class.java.simpleName
        private const val WRITE_PERIOD = 200L
        private const val READ_DELAY = 100L

        val instance: IotToWiFiSerialManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            IotToWiFiSerialManager()
        }
    }
}

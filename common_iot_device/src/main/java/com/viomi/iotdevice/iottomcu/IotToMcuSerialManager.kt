package com.viomi.iotdevice.iottomcu

import com.viomi.iotdevice.BuildConfig
import com.viomi.iotdevice.IotSerialConfig
import com.viomi.iotdevice.common.SerialParams
import com.viomi.iotdevice.common.callback.CommonCallback
import com.viomi.iotdevice.common.callback.IotSerialCallback
import com.viomi.iotdevice.common.callback.ProgressCallback
import com.viomi.iotdevice.common.model.IotResultFormat
import com.viomi.iotdevice.common.model.bean.ResultBean
import com.viomi.iotdevice.common.protocol.IotCmd
import com.viomi.iotdevice.common.protocol.Xmodem
import com.viomi.iotdevice.common.rxbus.BusEvent
import com.viomi.iotdevice.common.rxbus.RxBus
import com.viomi.iotdevice.common.util.FileUtil
import com.viomi.iotdevice.common.util.IgnoreErrorLambdaSubscriber
import com.viomi.iotdevice.common.util.LogUtil
import com.viomi.iotdevice.common.util.RxSchedulerUtil
import com.viomi.iotdevice.iottomcu.bean.ViotActionRequestBody
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody
import com.viomi.iotdevice.iottomcu.protocol.IotToMcuMiotFormat
import com.viomi.iotdevice.iottomcu.protocol.IotToMcuViotFormat
import com.viomi.iotdevice.iottowifi.IotToWiFiSerialManager
import com.viomi.serialport.jniSERIAL
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import org.reactivestreams.Subscription
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/20
 *     desc   : 串口通讯管理
 *     version: 1.0
 * </pre>
 */
internal class IotToMcuSerialManager {
    /**
     * 串口实例
     */
    private val mJniSerial: jniSERIAL by lazy { jniSERIAL() }

    /**
     * 消息订阅处理
     */
    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    /**
     * 发送数据回调
     */
    private val mResultCallbacks: MutableMap<String, CommonCallback?> by lazy { HashMap<String, CommonCallback?>() }

    /**
     * 总共下发的命令
     */
    private val mDownCommands: MutableList<String> by lazy { ArrayList<String>() }

    /**
     * mcu 收到命令后处理超时计时器
     */
    private var mWriteTimerDisposable: Disposable? = null

    /**
     * 串口读观察
     */
    private var mReadSubscription: Subscription? = null

    /**
     * 订阅 action 流程消息
     */
    private var mBusDisposable: Disposable? = null

    /**
     * ota 升级订阅
     */
    private var mXmodemDisposable: Disposable? = null

    /**
     * get_down 的命令，有 get_down 请求时保存，请求结束后清空
     */
    @Volatile
    private var mDownCmd: String? = null
    private var mIsOtaPrePare = false
    private var mIsMcuOta = false

    /**
     * Iot 协议类型
     */
    private var mIotType: IotSerialConfig.IotType? = null
    private val mTimeFormatter by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    private var mRxNoDataCount = 0
    private var mRxErrorDataCount = 0

    /**
     * 缓存读取的串口数据
     */
    private var mCacheDataStr = StringBuffer("")

    /**
     * 缓存命令
     */
    private var mCacheCmd = StringBuffer("")

    /**
     * 设置连续无数据多少次为通信故障
     * 默认连续 20 次 Rx 读数据为空, 视作串口线断开
     */
    var errorCount: Int = 20

    /**
     * 是否打开串口 Jni Log
     */
    var enableSerialJniLog = false

    /**
     * 串口是否正在繁忙
     */
    val isSerialBusy: Boolean
        get() {
            if (!mDownCmd.isNullOrBlank()) {
                LogUtil.e(TAG, "SerialBusy DOWN_CMD != null $mDownCmd")
            }
            return !mDownCmd.isNullOrBlank()
        }

    /**
     * 设备 Mac 地址
     */
    var mac: String = ""

    /**
     * 数据上报 handler
     */
    var cmdHandler: IotSerialCallback? = null

    /**
     * 串口 Read 间隔(单位: ms)
     */
    var readPeriod: Long = 200

    /**
     * 打开串口
     *
     * @param comPath:  串口路径
     * @param baudRate: 波特率
     * @param iotType:  协议类型
     */
    fun open(comPath: String, baudRate: Int, iotType: IotSerialConfig.IotType?) {
        mIotType = iotType
        /**
         * 本地没有 so 库
         */
        if (jniSERIAL.no_serial_lib) {
            LogUtil.e(TAG, "To mcu serial open fail, no serial lib.")
            return
        }
        LogUtil.d(TAG, "To mcu serial init start, baudRate = $baudRate, serialPath = $comPath")
        mJniSerial.log_enable(enableSerialJniLog)
        val result = mJniSerial.init(mJniSerial, comPath, baudRate, 8, 'n', 1)
        if (result == 0) {
            LogUtil.d(TAG, "To mcu serial init success.")
            subscribeRead()
        } else {
            LogUtil.e(TAG, "To mcu serial init fail, result: $result")
            return
        }

        mBusDisposable = RxBus.instance.subscribe {
            when (it.msgId) {
                /**
                 * 发送命令成功
                 */
                BusEvent.MSG_DOWN_CMD_RESULT_SUCCESS -> {
                    LogUtil.d(TAG, "MSG_DOWN_CMD_RESULT_SUCCESS, msg = ${it.msgObject}")
                    mDownCmd = null
                    var i = 0
                    while (i < mDownCommands.size) {
                        val cmd = mDownCommands[i]
                        val commonCallback = mResultCallbacks[cmd]
                        kotlin.runCatching {
                            commonCallback?.onReceiveResult(IotResultFormat.RESULT_OK, it.msgObject, "ok")
                        }.onFailure {
                            val resultBean = IotResultFormat.faultResult
                            commonCallback?.onReceiveResult(resultBean.code, resultBean.data, resultBean.desc)
                        }
                        mResultCallbacks.remove(cmd)
                        mDownCommands.removeAt(i)
                        i--
                        i++
                    }
                    mWriteTimerDisposable?.dispose()
                }
                /**
                 * 发送命令失败
                 */
                BusEvent.MSG_DOWN_CMD_RESULT_FAIL -> {
                    mDownCmd = null
                    var i = 0
                    while (i < mDownCommands.size) {
                        val cmd = mDownCommands[i]
                        val commonCallback = mResultCallbacks[cmd]
                        kotlin.runCatching {
                            val resultBean = it.msgObject as? ResultBean
                            LogUtil.d(TAG, "MSG_DOWN_CMD_RESULT_FAIL, code = ${resultBean?.code}, message = ${resultBean?.desc}")
                            commonCallback?.onReceiveResult(resultBean?.code, resultBean?.data, resultBean?.desc)
                        }.onFailure { LogUtil.e(TAG, "MSG_DOWN_CMD_RESULT_FAIL mResultCallback Error") }
                        mResultCallbacks.remove(cmd)
                        mDownCommands.removeAt(i)
                        i--
                        i++
                    }
                    mWriteTimerDisposable?.dispose()
                }
                else -> LogUtil.w(TAG, "Undefined RxBus Event.")
            }
        }
    }

    /**
     * action, 显示板向主控板发送控制数据(适用于 miot profile 协议)
     *
     * @param sendCmd:  发送命令数据
     * @param callback: 回调
     */
    fun iotAction(sendCmd: JSONObject?, callback: CommonCallback?) {
        LogUtil.d(TAG, "iotAction = $sendCmd")
        val miioSendCmd = IotToMcuMiotFormat.actionFormat(sendCmd)
        cacheCommand(miioSendCmd, callback)
    }

    /**
     * action, 显示板向主控板发送 Json 数据(适用于 miot profile 协议)
     *
     * @param sendCmd:  发送 json 数据
     * @param callback: 回调
     */
    fun iotActionJson(sendCmd: String?, callback: CommonCallback?) {
        LogUtil.d(TAG, "iotActionJson = $sendCmd")
        cacheCommand(sendCmd, callback)
    }

    /**
     * action, 显示板向 mcu 发送控制数据(适用于 viot、miot-spec 协议)
     *
     * @param requestBody: 发送命令数据
     * @param callback:    回调
     */
    fun iotAction(requestBody: ViotActionRequestBody?, callback: CommonCallback?) {
        val specSendCmd = IotToMcuViotFormat.actionFormat(requestBody)
        LogUtil.d(TAG, "iotAction = $specSendCmd")
        cacheCommand(specSendCmd, callback)
    }

    /**
     * action, 显示板向 mcu 发送控制数据, 透传数据(适用于 viot、miot-spec 协议)
     *
     * @param serialData: 发送串口透传数据命令数据
     * @param callback:   回调
     */
    fun iotAction(serialData: String?, callback: CommonCallback?) {
        LogUtil.d(TAG, "iotAction = $serialData")
        cacheCommand(serialData, callback)
    }

    /**
     * get_properties, 显示板向 mcu 发送获取属性命令（适用于viot、miot-spec 协议）
     *
     * @param requestBody: 发送命令数据
     * @param callback:    回调
     */
    fun iotGetProperties(requestBody: ViotGetPropRequestBody?, callback: CommonCallback?) {
        val miioSendCmd = IotToMcuViotFormat.getPropertiesFormat(requestBody)
        LogUtil.d(TAG, "iotGetProperties = $miioSendCmd")
        cacheCommand(miioSendCmd, callback)
    }

    /**
     * set_properties, 显示板向 mcu 发送设置属性命令（适用于viot、miot-spec 协议）
     *
     * @param requestBody: 发送命令数据
     * @param callback:    回调
     */
    fun iotSetProperties(requestBody: ViotSetPropRequestBody?, callback: CommonCallback?) {
        val miioSendCmd = IotToMcuViotFormat.setPropertiesFormat(requestBody)
        LogUtil.d(TAG, "iotSetProperties = $miioSendCmd")
        cacheCommand(miioSendCmd, callback)
    }

    /**
     * setProperties，显示板向 mcu 发送设置属性命令，透传数据(适用于 viot、miot-spec 协议)
     *
     * @param serialData: 发送串口透传数据命令数据
     * @param callback:   回调
     */
    fun iotSetProperties(serialData: String?, callback: CommonCallback?) {
        LogUtil.d(TAG, "iotSetProperties = $serialData")
        cacheCommand(serialData, callback)
    }

    /**
     * 发送升级的下行命令
     *
     * @param callback: 回调
     */
    fun sendUpdateFirmware(callback: CommonCallback?) {
        LogUtil.d(TAG, "sendUpdateFirmware = " + IotCmd.Update_fw)
        cacheCommand(checkWriteDataFormat(IotCmd.Update_fw), callback)
    }

    /**
     * 缓存下发的指令
     */
    private fun cacheCommand(cmd: String?, callback: CommonCallback?) {
        /**
         * 串口异常, 未正常打开(Read 线程未打开, 即串口未初始化)
         */
        if (mReadSubscription == null) {
            val bean = IotResultFormat.serialResult
            callback?.onReceiveResult(bean.code, null, bean.desc)
        }
        /**
         * OTA 升级
         */
        else if (mIsMcuOta && cmd?.startsWith(IotCmd.Update_fw) != true) {
            val bean = IotResultFormat.upDatingResult
            callback?.onReceiveResult(bean.code, null, bean.desc)
        }
        /**
         * 连续读数据为 0(通信故障)
         */
        else if (mRxNoDataCount >= errorCount) {
            val bean = IotResultFormat.serialDisconnectResult
            callback?.onReceiveResult(bean.code, null, bean.desc)
        } else {
            val cmdTemp = if (cmd?.endsWith("\r") == true) cmd else cmd + "\r"
            mResultCallbacks[cmdTemp] = callback
            mCacheCmd.append(cmdTemp)
        }
    }

    /**
     * 开始发送文件升级
     *
     * @param filePath: ota 文件路径
     * @param callback: ota 回调
     */
    fun otaProgress(filePath: String, callback: ProgressCallback?) {
        mIsMcuOta = true
        /**
         * 暂停订阅循环读取串口命令
         */
        unSubscribeRead()
        kotlin.runCatching {
            /**
             * 读取升级文件
             */
            val fileByBytes = FileUtil.readFileByBytes(filePath)

            /**
             * Xmodem 发送
             */
            val xmodem = object : Xmodem() {
                override fun receiveComData(): ByteArray? {
                    return receiveData()
                }

                override fun sendComData(data: ByteArray): Boolean {
                    return sendData(data)
                }
            }
            mXmodemDisposable = xmodem.process(fileByBytes)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtil.d(TAG, "ota progress:$it")
                    callback?.onResult(true, it, "")
                }, {
                    LogUtil.e(TAG, "ota fail, msg = ${it.message}")
                    callback?.onResult(false, -1, it.message)
                    mXmodemDisposable?.dispose()
                    /**
                     * 恢復订阅循环读取串口命令
                     */
                    subscribeRead()
                    mIsMcuOta = false
                }, {
                    LogUtil.e(TAG, "ota finish")
                    callback?.onResult(false, 100, "finish")
                    mXmodemDisposable?.dispose()
                    /**
                     * 恢復订阅循环读取串口命令
                     */
                    subscribeRead()
                    mIsMcuOta = false
                })
        }.onFailure {
            it.printStackTrace()
            LogUtil.e(TAG, "ota fail,msg=IOException")
            callback?.onResult(false, -1, "IOException")
            mXmodemDisposable?.dispose()
            /**
             * 恢復订阅循环读取串口命令
             */
            subscribeRead()
        }
    }

    /**
     * 订阅串口循环读
     */
    private fun subscribeRead() {
        Flowable.interval(0, readPeriod, TimeUnit.MILLISECONDS)
            .onBackpressureDrop()
            .compose(RxSchedulerUtil.schedulerFlowableTransformer5())
            .onTerminateDetach()
            .retryWhen { it.flatMap { Flowable.timer(readPeriod, TimeUnit.MILLISECONDS) } }
            .subscribe(
                IgnoreErrorLambdaSubscriber<Long>({
                    mReadSubscription?.request(1)
                    val length = mJniSerial.serial_read(mJniSerial)
                    LogUtil.d(TAG, "To mcu serial read length = $length")
                    serialReadParse(mJniSerial.rd_data, length)
                }, { LogUtil.e(TAG, it.message.toString()) }, { LogUtil.d(TAG, "subscribeRead onComplete.") }, {
                    it.request(1)
                    mReadSubscription = it
                })
            )
    }

    /**
     * 取消订阅串口循环读
     */
    private fun unSubscribeRead() {
        mReadSubscription?.cancel()
        mReadSubscription = null
    }

    /**
     * 发送 down 命令.
     */
    private fun downCmdSend(sendCmd: String) {
        if (sendCmd.isBlank()) {
            LogUtil.e(TAG, "Serial cmd format is error.")
            RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_FAIL, IotResultFormat.faultResult)
            return
        }
        val sendCmdTemp = checkWriteDataFormat(sendCmd)
        /**
         * 成功发送命令
         */
        if (serialWrite(sendCmdTemp)) {
            mWriteTimerDisposable?.dispose()
            var timeDelay = 1500L
            /**
             * 考虑部分设备 Flash 时间擦除耗时长
             */
            takeIf { sendCmdTemp.startsWith("down update_fw") }?.run { timeDelay = 6000L }
            mWriteTimerDisposable = Observable.timer(timeDelay, TimeUnit.MILLISECONDS)
                .compose(RxSchedulerUtil.schedulerTransformer5())
                .onTerminateDetach()
                .subscribe({
                    mWriteTimerDisposable?.dispose()
                    takeUnless { mDownCmd.isNullOrBlank() }?.run { RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_FAIL, IotResultFormat.timeOutResult) }
                }, {
                    LogUtil.e(TAG, it.message.toString())
                    RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_FAIL, IotResultFormat.faultResult)
                })
        } else {
            LogUtil.e(TAG, "To mcu down cmd send fail, write error.")
            RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_FAIL, IotResultFormat.serialResult)
        }
    }

    private fun sendIotData(data: String) = serialWrite(checkWriteDataFormat(data))

    /**
     * 校验发送数据格式
     */
    private fun checkWriteDataFormat(data: String): String {
        return if (!data.endsWith("\r")) {
            data + "\r"
        } else data
    }

    /**
     * 串口写入数据
     *
     * @param params: 串口写入相关数据
     */
    @Synchronized
    private fun serialWrite(params: String): Boolean = when {
        params.isBlank() -> {
            LogUtil.e(TAG, "To mcu serial write fail, params are null.")
            false
        }
        jniSERIAL.no_serial_lib -> {
            LogUtil.e(TAG, "To mcu serial write fail, no serial lib.")
            false
        }
        else -> {
            LogUtil.d(TAG, "To mcu serialWrite: $params")
            val data = IntArray(params.length)
            val dataChar = params.toCharArray()
            for (i in dataChar.indices) {
                data[i] = dataChar[i].code
            }
            mJniSerial.wr_data = data
            val size = mJniSerial.serial_write(mJniSerial, mJniSerial.wr_data.size)
            var result = true
            takeIf { size != data.size }?.run {
                LogUtil.e(TAG, "To mcu serial write fail, size = $size")
                result = false
            }
            result
        }
    }

    /**
     * 串口读取数据解析
     *
     * @param data:   返回数据
     * @param length: 返回数据长度
     */
    private fun serialReadParse(data: IntArray?, length: Int) {
        var dataChar: CharArray? = null
        /**
         * Rx 数据为空
         */
        if (data?.isEmpty() != false || length == 0) {
            LogUtil.e(TAG, "To mcu serial Read fail, length = $length")
            if (mRxNoDataCount < errorCount) {
                LogUtil.w(TAG, "To mcu serial Read fail, rxNoDataCount = $mRxNoDataCount")
                mRxNoDataCount++
                takeIf { mRxNoDataCount == errorCount }?.run { dataChar = IotCmd.getDisconnectEventData(mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT) }
            }
        }
        /**
         * Rx 数据正常
         */
        else {
            takeIf { mRxNoDataCount == errorCount }?.run {
                upSendEvent(IotCmd.getConnectEventData(mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT).toString().replace("\r", ""))
            }
            mRxNoDataCount = 0
            dataChar = CharArray(length)
            dataChar?.forEachIndexed { index, _ -> dataChar?.set(index, (data[index] and 0xff).toChar()) }
        }
        dataChar?.let {
            val dataStr = String(it)
            LogUtil.d(TAG, "To mcu serialRead：$dataStr")
            takeIf { SerialParams.instance.isEchoEnable }?.run { serialWrite(dataStr) }
            /**
             * 每次读回来缓存
             */
            mCacheDataStr.append(dataStr)
            /**
             * 连续返回数据不对, 判断为通信故障
             */
            if (!mCacheDataStr.contains("\r")) {
                if (mRxErrorDataCount < errorCount) {
                    mRxErrorDataCount++
                    if (mRxErrorDataCount == errorCount) {
                        dataChar = IotCmd.getErrorEventData(mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT)
                        mCacheDataStr.delete(0, mCacheDataStr.length - 1)
                        mCacheDataStr.append(dataChar.toString())
                    }
                }
            } else if (mRxErrorDataCount == errorCount) {
                upSendEvent(IotCmd.getConnectEventData(mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT).toString())
            }
            if (!mCacheDataStr.contains("\r")) {
                return
            }
            takeIf { "event_occured 0 2\r" != mCacheDataStr.toString() }?.run { mRxErrorDataCount = 0 }
            var str = mCacheDataStr.toString()
            str = str.replace("\r".toRegex(), "\r|")
            val readResults = str.split("|").toTypedArray()
            var handleResult = ""
            for (result in readResults) {
                takeIf { result.endsWith("\r") }?.run {
                    handleResult = result
                    handleSerialRead(handleResult)
                    mCacheDataStr.replaceAll(result, "")
                }
            }
//            takeUnless { handleResult.isBlank() }?.run {
//                LogUtil.d(TAG, "Handle result: $handleResult")
//                handleSerialRead(handleResult)
//            }
        }
    }

    private fun handleSerialCmd() {
        /**
         * 防止缓存命令过大
         */
        takeIf { mCacheCmd.length >= 1000 }?.run { mCacheCmd.delete(0, mCacheCmd.length - 1) }
        /**
         * 有缓存命令
         */
        if (mDownCmd.isNullOrBlank() && mCacheCmd.isNotBlank()) {
            var str = mCacheCmd.toString()
            str = str.replace("\r".toRegex(), "\r|")
            val commands = str.split("|").toTypedArray()
            if (commands.size == 1) {
                mDownCmd = commands[0]
                mDownCommands.add(commands[0])
                mCacheCmd.replaceAll(commands[0], "")
            } else {
                mDownCmd = commands[0]
                mDownCommands.add(commands[0])
                mDownCmd = mDownCmd?.replace("\r", "")
                mCacheCmd.replaceAll(commands[0], "")
                for (i in 1 until commands.size) {
                    var cur = commands[i]
                    val cmd = IotCmd.getMcuCmd(cur)
                    if (!cmd.isNullOrBlank() && mDownCmd?.startsWith(cmd) == true) {
                        cur = cur.replace(cmd, "")
                        cur = cur.replace("\r", "")
                        // 防止请求了一样的命令
                        if (mDownCmd?.contains(cur) != true) {
                            mDownCmd += cur
                        }
                        mCacheCmd.replaceAll(commands[i], "")
                        mDownCommands.add(commands[i])
                    }
                }
            }
            mDownCmd = if (mDownCmd?.endsWith("\r") == true) mDownCmd else mDownCmd + "\r"
        }
    }

    private fun handleSerialRead(dataStr: String) {
        handleSerialCmd()
        val iotCmd = IotCmd.getMcuCmd(dataStr)
        // 返回指令正常
        val crIndex = dataStr.indexOf("\r")
        // 去掉 \r, 以及后面的部分
        val subDataStr = dataStr.substring(0, crIndex)
        // 发送 update_fw 后, 进入准备升级状态, 此状态下, 只接收 get down 和 result 命令, 其他返回 error.
        if (mIsOtaPrePare) {
            // 升级阶段收到 get down 继续下发 update_fw
            if (IotCmd.GetDown == iotCmd) {
                sendIotData("down $mDownCmd")
            } else if (IotCmd.Result != iotCmd) {
                sendIotData(IotResultFormat.CMD_ERROR)
            }
        } else {
            when (iotCmd) {
                IotCmd.GetDown, IotCmd.JsonGetDown -> {
                    when {
                        /**
                         * 上一条下发的命令还未收到返回则回复 error
                         */
                        mWriteTimerDisposable?.isDisposed == false -> sendIotData(IotResultFormat.CMD_ERROR)
                        /**
                         * 无 down 命令
                         */
                        mDownCmd.isNullOrBlank() -> sendIotData("down none")
                        else -> {
                            if (mDownCmd?.startsWith(IotCmd.Update_fw) == true) {
                                LogUtil.i(TAG, "IotCmd.Update_fw.equals(DOWN_CMD):$mDownCmd")
                                mIsOtaPrePare = true
                            }
                            downCmdSend("down $mDownCmd")
                        }
                    }
                }
                /**
                 * Miot 协议
                 */
                IotCmd.Result, IotCmd.Error ->
                    if (mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT) {
                        val result = IotToMcuMiotFormat.actionResultParse(iotCmd, subDataStr)
                        LogUtil.d(TAG, "read reult = $result")
                        // DOWN_CMD 为 null, 说明超时时间已过, 返回失效
                        if (!result.isNullOrBlank() && !mDownCmd.isNullOrBlank()) {
                            RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_SUCCESS, result)
                            // 收到回复，取消准备升级状态
                            mIsOtaPrePare = false
                            LogUtil.i(TAG, "receive result！")
                            sendIotData(IotResultFormat.CMD_OK)
                        } else {
                            sendIotData(IotResultFormat.CMD_ERROR)
                        }
                    } else if (mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT_SPEC || mIotType == IotSerialConfig.IotType.PROTOCOL_VIOT) {
                        var resultBody: Any? = null
                        try {
                            resultBody = IotToMcuViotFormat.resultParse(iotCmd, mDownCmd, subDataStr)
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                            LogUtil.e(TAG, "To mcu serial result parse fail, message = " + e.message)
                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                            LogUtil.e(TAG, "To mcu serial result parse fail, message = " + e.message)
                        }
                        // 下发命令后成功返回
                        if (resultBody != null && !mDownCmd.isNullOrBlank()) {
                            RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_SUCCESS, resultBody)
                            // 收到回复，取消准备升级状态
                            mIsOtaPrePare = false
                            sendIotData(IotResultFormat.CMD_OK)
                        } else {
                            RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_FAIL, resultBody)
                            sendIotData(IotResultFormat.CMD_ERROR)
                        }
                    }
                IotCmd.JsonAck -> {
                    if (subDataStr.length > IotCmd.JsonAck.length) {
                        val jsonResult: String = subDataStr.substring(IotCmd.JsonAck.length + 1)
                        val `object` = JSONObject()
                        try {
                            `object`.put("code", 0)
                            `object`.put("message", "ok")
                            `object`.put("result", jsonResult)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_SUCCESS, `object`.toString())
                        LogUtil.i(TAG, "receive json result！")
                    }
                    sendIotData(IotResultFormat.CMD_OK)
                }
                IotCmd.Miot_Props, IotCmd.Miot_Spec_Props -> {
                    sendIotData(IotResultFormat.CMD_OK)
                    if (mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT) {
                        cmdHandler?.props(IotToMcuMiotFormat.propsDataParse(subDataStr), subDataStr)
                    } else if (mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT_SPEC || mIotType == IotSerialConfig.IotType.PROTOCOL_VIOT) {
                        val propData = IotToMcuViotFormat.propsDataParse(subDataStr)
                        propData?.forEach { SerialParams.instance.saveProp(it.key.toString(), it.value.toString()) }
                        cmdHandler?.props(propData, subDataStr)
                        // 上报给 WiFi 模块
                        IotToWiFiSerialManager.instance.command = subDataStr
                    }
                }
                IotCmd.Miot_Event, IotCmd.Miot_Spec_Event -> {
                    sendIotData(IotResultFormat.CMD_OK)
                    upSendEvent(subDataStr)
                }
                IotCmd.JsonSend -> {
                    sendIotData(IotResultFormat.CMD_OK)
                    cmdHandler?.json_send(subDataStr.substring(IotCmd.JsonSend.length + 1))
                }
                IotCmd.Version -> sendIotData("" + BuildConfig.VERSION_NAME)
                IotCmd.Model -> {
                    val array = subDataStr.split(" ").toTypedArray()
                    // 上报格式不对
                    if (array.size > 2) {
                        LogUtil.e(TAG, "To mcu serial Model split error, data = $subDataStr")
                        sendIotData(IotResultFormat.CMD_ERROR)
                        return
                    }
                    // 不带参数，查询直接返回保存的 model
                    if (array.size == 1) {
                        sendIotData(SerialParams.instance.model)
                    } else if (array.size == 2) {
                        val model = array[1]
                        val modelArray = model.split(".").toTypedArray()
                        // Model 格式不正确
                        if (modelArray.size != 3) {
                            sendIotData(IotResultFormat.CMD_ERROR)
                            return
                        }
                        SerialParams.instance.model = model
                        sendIotData(IotResultFormat.CMD_OK)
                        cmdHandler?.model(model)
                    }
                }
                IotCmd.McuVersion -> {
                    val arrayMcu = subDataStr.split(" ").toTypedArray()
                    if (arrayMcu.size != 2) {
                        LogUtil.e(TAG, "To mcu serial McuVersion split error, data = $subDataStr")
                        sendIotData(IotResultFormat.CMD_ERROR)
                        return
                    }
                    sendIotData(IotResultFormat.CMD_OK)
                    cmdHandler?.mcu_version(arrayMcu[1])
                }
                IotCmd.UpdateMe -> {
                    mIsMcuOta = true
                    if (!mDownCmd.isNullOrBlank() && mDownCmd?.startsWith(IotCmd.Update_fw) != true) {
                        RxBus.instance.post(BusEvent.MSG_DOWN_CMD_RESULT_FAIL, IotResultFormat.upDatingResult)
                    }
                }
                IotCmd.Echo -> {
                    val arrayEcho = subDataStr.split(" ").toTypedArray()
                    if (arrayEcho.size != 2) {
                        LogUtil.e(TAG, "To mcu serial Echo split error, data = $subDataStr")
                        sendIotData(IotResultFormat.CMD_ERROR)
                        return
                    }
                    if ("on" == arrayEcho[1]) {
                        SerialParams.instance.isEchoEnable = true
                    } else if ("off" == arrayEcho[1]) {
                        SerialParams.instance.isEchoEnable = false
                    }
                    sendIotData(IotResultFormat.CMD_OK)
                }
                IotCmd.Mac -> sendIotData(mac)
                IotCmd.Net -> sendIotData("cloud")
                IotCmd.Time -> {
                    val arrayTime = subDataStr.split(" ").toTypedArray()
                    if (arrayTime.size == 1) {
                        val retStrFormatNowDate = mTimeFormatter.format(Date(System.currentTimeMillis()))
                        sendIotData(retStrFormatNowDate)
                    } else {
                        sendIotData(System.currentTimeMillis().toString() + "")
                    }
                }
                IotCmd.Help -> {
                    val help = """
                        echo
                        error
                        event
                        get_down
                        help
                        mac
                        net
                        time
                        mcu_version
                        model
                        props
                        result
                        version
                        json_get_down
                        json_ack
                        json_send
                        """.trimIndent()
                    sendIotData(help)
                }
                IotCmd.Miot_Spec_Ble_Config, IotCmd.Factory -> {
                    sendIotData(IotResultFormat.CMD_OK)
                    cmdHandler?.enterTestMode()
                }
                else -> LogUtil.e(TAG, "Unknow serial command")
            }
        }
    }

    private fun upSendEvent(dataStr: String) {
        if (mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT) {
            cmdHandler?.event(IotToMcuMiotFormat.eventDataParse(dataStr), dataStr)
        } else if (mIotType == IotSerialConfig.IotType.PROTOCOL_MIOT_SPEC || mIotType == IotSerialConfig.IotType.PROTOCOL_VIOT) {
            cmdHandler?.event(IotToMcuViotFormat.eventDataParse(dataStr), dataStr)
            // 上报给 WiFi 模块
            IotToWiFiSerialManager.instance.command = dataStr
        }
    }

    /**
     * 读取串口数据
     */
    private fun receiveData(): ByteArray? {
        val size = mJniSerial.serial_read(mJniSerial)
        if (size > 0) {
            LogUtil.e(TAG, "receiveData size:$size")
            val rdData = ByteArray(mJniSerial.rd_data.size)
            for (i in rdData.indices) {
                rdData[i] = mJniSerial.rd_data[i].toByte()
            }
            return rdData
        }
        return null
    }

    /**
     * 写串口数据
     */
    private fun sendData(data: ByteArray): Boolean {
        mJniSerial.wr_data = IntArray(data.size)
        for (i in data.indices) {
            mJniSerial.wr_data[i] = data[i].toInt()
        }
        val length = mJniSerial.serial_write(mJniSerial, mJniSerial.wr_data.size)
        if (length != data.size) {
            LogUtil.e(TAG, "cmdWrite bytes length fail!length = $length")
            return false
        }
        return true
    }

    /**
     * StringBuffer 替换特定字符
     */
    private fun StringBuffer.replaceAll(oldString: String, newString: String) {
        val pattern = Pattern.compile(oldString)
        val matcher = pattern.matcher(this)
        var startIndex = 0
        while (matcher.find(startIndex)) {
            this.replace(matcher.start(), matcher.end(), newString)
            startIndex = matcher.start() + newString.length
        }
    }

    /**
     * 关闭串口
     */
    fun close() {
        mCompositeDisposable.clear()
        mWriteTimerDisposable?.dispose()
        mWriteTimerDisposable = null
        mBusDisposable?.dispose()
        mBusDisposable = null
        mXmodemDisposable?.dispose()
        mXmodemDisposable = null
        mDownCommands.clear()
        unSubscribeRead()
        mResultCallbacks.clear()
        takeIf { !jniSERIAL.no_serial_lib }?.run { mJniSerial.exit() }
    }

    companion object {
        private val TAG = IotToMcuSerialManager::class.java.simpleName

        val instance: IotToMcuSerialManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            IotToMcuSerialManager()
        }
    }
}

package com.viomi.viot.device.service

import android.content.Context
import android.os.IInterface
import android.os.RemoteCallbackList
import com.viomi.viot.*
import com.viomi.viot.account.AccountMessage
import com.viomi.viot.account.IAccountMessageListener
import com.viomi.viot.action.VIotDeviceAction
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.device.VIotDevice
import com.viomi.viot.event.VIotDeviceEvent
import com.viomi.viot.https.ResultCodes
import com.viomi.viot.https.VIotClient
import com.viomi.viot.preference.VIotDevicePreference
import com.viomi.viot.property.VIotDeviceProperty
import com.viomi.viot.push.IMessageArrivedListener
import com.viomi.viot.push.PushMessage
import com.viomi.viot.rxjava2.RxScheduler
import com.viomi.viot.rxjava2.VIotBusEvent
import com.viomi.viot.rxjava2.VIotRxBus
import com.viomi.viot.utils.FileUtil
import com.viomi.viot.utils.LogUtil
import com.viomi.viot.utils.VIotUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     @author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/5
 *     desc   : AIDL 跨进程通信 Binder
 *     version: 1.0
 * </pre>
 */
class IVIotHostServiceProxy(private val context: Context) : IVIotDeviceService.Stub() {
    private var mVIotDevice: VIotDevice? = null
    private var mIsDebug = false
    private var mTime = 10L
    private var mMac: String? = null
    private var mPeriodDisposable: Disposable? = null
    private var mRetryDisposable: Disposable? = null
    private var mPingDisposable: Disposable? = null

    /**
     * 设备绑定状态回调
     */
    private var mIOnDeviceBindListener: IOnDeviceBindListener? = null

    /**
     * 监听 Mqtt 连接状态
     */
    private val mConnectedRemoteCallbackList: RemoteCallbackList<IViotConnectedListener> = RemoteCallbackList()

    /**
     * 监听 set_properties
     */
    private val mSetPropertiesRemoteCallbackList: RemoteCallbackList<IOnSetPropertiesListener> = RemoteCallbackList()
    private val mSetPropertyListRemoteCallbackList: RemoteCallbackList<IOnSetPropertyListListener> = RemoteCallbackList()

    /**
     * 监听 Action
     */
    private val mActionRemoteCallbackList: RemoteCallbackList<IOnActionListener> = RemoteCallbackList()

    /**
     * 监听 get_properties
     */
    private val mGetPropertiesRemoteCallbackList: RemoteCallbackList<IOnGetPropertiesListener> = RemoteCallbackList()

    /**
     * 监听消息推送
     */
    private val mPushMessageRemoteCallbackList: RemoteCallbackList<IMessageArrivedListener> = RemoteCallbackList()

    /**
     * 监听远程调试
     */
    private val mDebugRemoteCallbackList: RemoteCallbackList<IRemoteDebugListener> = RemoteCallbackList()

    /**
     * 监听数据刷新
     */
    private val mDataRefreshRemoteCallbackList: RemoteCallbackList<IDataRefreshListener> = RemoteCallbackList()

    /**
     * 监听账号信息刷新
     */
    private val mAccountRefreshRemoteCallbackList: RemoteCallbackList<IAccountMessageListener> = RemoteCallbackList()

    /**
     * 监听其他 Mqtt 消息
     */
    private val mExtraMqttRemoteCallbackList: RemoteCallbackList<IDeviceExtraMqttListener> = RemoteCallbackList()

    /**
     * 监听获取 model 配置
     */
    private val mModelConfigRemoteCallbackList: RemoteCallbackList<IModelConfigListener> = RemoteCallbackList()

    private val mBinderMap: MutableMap<String, IInterface> = HashMap()

    /**
     * RxBus 监听
     */
    private var mRxBusDisposable = VIotRxBus.instance.subscribe {
        when (it.msgId) {
            /**
             * set_properties 方法 (单属性)
             */
            VIotBusEvent.MSG_SET_PROPERTIES -> kotlin.runCatching {
                val count = mSetPropertiesRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mSetPropertiesRemoteCallbackList.getBroadcastItem(i).onSet(it.msgObject as? VIotDeviceProperty)
                }
            }.also { mSetPropertiesRemoteCallbackList.finishBroadcast() }

            /**
             * set_properties 方法 (多属性)
             */
            VIotBusEvent.MSG_SET_PROPERTY_LIST -> kotlin.runCatching {
                val count = mSetPropertyListRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mSetPropertyListRemoteCallbackList.getBroadcastItem(i).onSet((it.msgObject as? List<*>)?.filterIsInstance<VIotDeviceProperty>())
                }
            }.also { mSetPropertyListRemoteCallbackList.finishBroadcast() }

            /**
             * Action 方法
             */
            VIotBusEvent.MSG_ACTION -> kotlin.runCatching {
                val count = mActionRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mActionRemoteCallbackList.getBroadcastItem(i).onAction(it.msgObject as? VIotDeviceAction)
                }
            }.also { mActionRemoteCallbackList.finishBroadcast() }

            /**
             * get_properties 方法
             */
            VIotBusEvent.MSG_GET_PROPERTIES -> kotlin.runCatching {
                val count = mGetPropertiesRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mGetPropertiesRemoteCallbackList.getBroadcastItem(i)
                        .onGetProperty((it.msgObject as? List<*>)?.filterIsInstance<VIotDeviceProperty>(), it.extraObject as? String)
                }
            }.also { mGetPropertiesRemoteCallbackList.finishBroadcast() }

            /**
             * event 方法
             */
            VIotBusEvent.MSG_EVENT_OCCURRED -> {
                val id = it.msgObject as? String
                (mBinderMap[id?.substring(2, id.length)] as? IOnVIotResultListener)?.onSucceed()
                mBinderMap.remove(id)
            }

            /**
             * properties_changed 方法
             */
            VIotBusEvent.MSG_PROPERTIES_CHANGED -> {
                val id = it.msgObject as? String
                (mBinderMap[id?.substring(2, id.length)] as? IOnVIotResultListener)?.onSucceed()
                mBinderMap.remove(id)
            }

            /**
             * model 配置获取
             */
            VIotBusEvent.MSG_MODEL_CONFIG_GET -> kotlin.runCatching {
                val count = mModelConfigRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mModelConfigRemoteCallbackList.getBroadcastItem(i).onGetConfig(it.msgObject as Boolean)
                }
            }.also { mModelConfigRemoteCallbackList.finishBroadcast() }

            /**
             * 设备绑定成功
             */
            VIotBusEvent.MSG_DEVICE_BIND -> handleDeviceBind()

            /**
             * 设备解绑成功
             */
            VIotBusEvent.MSG_DEVICE_UNBIND -> handleDeviceUnbind()

            /**
             * 设备连接状态
             */
            VIotBusEvent.MSG_DEVICE_CONNECTION -> {
                val code = it.msgObject as? Int
                val id = it.extraObject as? String
                val listener = mBinderMap[id?.substring(2, id.length)] as? IOnVIotResultListener
                if (code == ResultCodes.RESULT_CODE_1) {
                    listener?.onSucceed()
                } else {
                    listener?.onFailed(code ?: -1, "Device is not connected.")
                }
                mBinderMap.remove(id)
            }

            /**
             * 上报日志
             */
            VIotBusEvent.MSG_REPORT_LOG -> handleReportLog(it.msgObject as? String)

            /**
             * 网络可用
             */
            VIotBusEvent.MSG_NETWORK_ENABLE -> handleNetworkEnable()

            /**
             * 重试
             */
            VIotBusEvent.MSG_RETRY_INITIALIZE -> handleRetryInit()

            /**
             * 远程调试
             */
            VIotBusEvent.MSG_DEVICE_REMOTE_DEBUG -> kotlin.runCatching {
                val count = mDebugRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mDebugRemoteCallbackList.getBroadcastItem(i).onRemoteDebug(it.msgObject as? String)
                }
            }.also { mDebugRemoteCallbackList.finishBroadcast() }

            /**
             * 数据刷新
             */
            VIotBusEvent.MSG_DEVICE_DATA_REFRESH -> kotlin.runCatching {
                val jsonObject = JSONObject(it.msgObject as String)
                val isRefreshScene = jsonObject.optBoolean("scene")
                val isRefreshDevices = jsonObject.optBoolean("deviceList")
                val isRefreshMiToken = jsonObject.optBoolean("miToken")
                val count = mDataRefreshRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    takeIf { isRefreshScene }?.run { mDataRefreshRemoteCallbackList.getBroadcastItem(i).onSceneRefresh() }
                    takeIf { isRefreshDevices }?.run { mDataRefreshRemoteCallbackList.getBroadcastItem(i).onDeviceRefresh() }
                    takeIf { isRefreshMiToken }?.run { mDataRefreshRemoteCallbackList.getBroadcastItem(i).onMiTokenRefresh() }
                }
            }.also { mDataRefreshRemoteCallbackList.finishBroadcast() }

            /**
             * 消息推送
             */
            VIotBusEvent.MSG_PUSH_MESSAGE -> kotlin.runCatching {
                val count = mPushMessageRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mPushMessageRemoteCallbackList.getBroadcastItem(i).onMessageArrived(it.msgObject as? PushMessage)
                }
            }.also { mPushMessageRemoteCallbackList.finishBroadcast() }

            /**
             * 账号更新
             */
            VIotBusEvent.MSG_ACCOUNT_REFRESH -> kotlin.runCatching {
                val count = mAccountRefreshRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mAccountRefreshRemoteCallbackList.getBroadcastItem(i).onAccountRefresh(it.msgObject as? AccountMessage)
                }
            }.also { mAccountRefreshRemoteCallbackList.finishBroadcast() }

            /**
             * 其他类型 Mqtt 信息
             */
            VIotBusEvent.MSG_EXTRA_MQTT_MESSAGE -> kotlin.runCatching {
                val count = mExtraMqttRemoteCallbackList.beginBroadcast()
                for (i in 0 until count) {
                    mExtraMqttRemoteCallbackList.getBroadcastItem(i).onExtraMessage(it.msgObject as? String, it.extraObject as? String)
                }
            }.also { mExtraMqttRemoteCallbackList.finishBroadcast() }

            /**
             * mqtt 连接状态监听
             */
            VIotBusEvent.MSG_VIOT_CONNECT_CHANGE -> {
                val result = it.msgObject as Boolean
                val count = mConnectedRemoteCallbackList.beginBroadcast()
                kotlin.runCatching {
                    for (i in 0 until count) {
                        if (result) {
                            mConnectedRemoteCallbackList.getBroadcastItem(i).onConnected()
                        } else {
                            mConnectedRemoteCallbackList.getBroadcastItem(i).onDisconnected()
                        }
                    }
                }.also { mConnectedRemoteCallbackList.finishBroadcast() }
            }
            else -> LogUtil.e(TAG, "The BusEvent is not supported.")
        }
    }

    /**
     * 解绑设备
     */
    internal fun unBindDevice() {
        mTime = 10L
        mRxBusDisposable.dispose()

        unsubscribePeriodSendProps()
        unsubscribeRetryInit()
        unsubscribePing()

        mVIotDevice?.stop()
        mVIotDevice = null

        mIOnDeviceBindListener = null
        mConnectedRemoteCallbackList.kill()
        mSetPropertiesRemoteCallbackList.kill()
        mSetPropertyListRemoteCallbackList.kill()
        mActionRemoteCallbackList.kill()
        mGetPropertiesRemoteCallbackList.kill()
        mPushMessageRemoteCallbackList.kill()
        mDebugRemoteCallbackList.kill()
        mDataRefreshRemoteCallbackList.kill()
        mAccountRefreshRemoteCallbackList.kill()
        mExtraMqttRemoteCallbackList.kill()
        mModelConfigRemoteCallbackList.kill()
        mBinderMap.clear()
    }

    /**
     * 绑定设备
     */
    override fun bindDevice(config: VIotDeviceConfig, iOnDeviceBindListener: IOnDeviceBindListener?) {
        mIOnDeviceBindListener = iOnDeviceBindListener
        takeUnless { mMac.isNullOrBlank() }?.run { config.mac = mMac!! }
        mVIotDevice = VIotDevice(config, context)
        VIotClient.instance.initialize(config, mIsDebug)
        mVIotDevice?.initialize(context, iOnDeviceBindListener)
    }

    /**
     * 上报属性
     */
    override fun syncProperties(list: List<VIotDeviceProperty>, iOnVIotResultListener: IOnVIotResultListener?) {
        val supportKeys = mVIotDevice?.pSupportProperties?.keys
        val supportProperties: MutableMap<String, Any> = HashMap()
        list.forEach {
            val key = "${it.siid}.${it.piid}"
            mVIotDevice?.allProperties?.put(key, it.value ?: "")
            if ((supportKeys?.contains(it.siid))?.and(mVIotDevice?.pSupportProperties?.get(it.siid)?.contains(it.piid) == true) == true) {
                supportProperties[key] = it.value ?: ""
            }
        }
        if (mVIotDevice?.isSupportViot == true) {
            val id = mVIotDevice?.syncProperties(supportProperties)
            iOnVIotResultListener?.let { id?.let { mBinderMap.put(id, iOnVIotResultListener) } }
        } else {
            iOnVIotResultListener?.onFailed(ResultCodes.RESULT_CODE_31, "Pull model config result: isSupportViot is ${mVIotDevice?.isSupportViot}")
        }
    }

    /**
     * 检查连接状态
     */
    override fun checkDeviceConnect(iOnVIotResultListener: IOnVIotResultListener?) {
        val id = mVIotDevice?.checkConnection()
        iOnVIotResultListener?.let { id?.let { mBinderMap.put(id, iOnVIotResultListener) } }
    }

    /**
     * Log 开关
     */
    override fun enableLog(enable: Boolean) {
        LogUtil.isPrintLog = enable
    }

    /**
     * 环境切换
     */
    override fun setDebugEnvironment(enable: Boolean) {
        mIsDebug = enable
    }

    /**
     * Action 回复
     */
    override fun notifyActionOutAdd(action: VIotDeviceAction?) {
        action?.let { deviceAction ->
            takeIf { mVIotDevice?.pReplyActionMap?.get(deviceAction.id).isNullOrEmpty() }?.run {
                deviceAction.id?.let { mVIotDevice?.pReplyActionMap?.put(it, ArrayList()) }
            }
            mVIotDevice?.pReplyActionMap?.get(deviceAction.id)?.add(action)
            takeIf { mVIotDevice?.pReplyActionMap?.get(deviceAction.id)?.size == mVIotDevice?.pCacheActionMap?.get(deviceAction.id)?.size }?.run {
                mVIotDevice?.pCacheActionMap?.remove(deviceAction.id)
                mVIotDevice?.pReplyActionMap?.get(deviceAction.id)?.let { list -> deviceAction.id?.let { mVIotDevice?.sendActionOut(list, it) } }
            }
        }
    }

    /**
     *
     */
    override fun notifyPropertiesValue(list: MutableList<VIotDeviceProperty>?, id: String) {
        mVIotDevice?.sentPropertiesValue(list, id)
    }

    /**
     * 更新 Mac 地址
     */
    override fun updateMacAddress(mac: String?) {
        mMac = mac
        mac?.let {
            mVIotDevice?.mac = it
            mVIotDevice?.deviceConfig?.mac = it
        }
    }

    /**
     * 上报额外设备信息
     */
    override fun uploadExtraDevInfo(map: MutableMap<String, String>?) {
        mVIotDevice?.uploadDeviceInfo(map)
    }

    /**
     * 注册监听 Mqtt 连接情况
     */
    override fun registerViotConnectedListener(iViotConnectedListener: IViotConnectedListener?) {
        iViotConnectedListener?.let { mConnectedRemoteCallbackList.register(it) }
    }

    /**
     * 获取 model 配置
     */
    override fun getModelConfig(iModelConfigListener: IModelConfigListener?) {
        iModelConfigListener?.let { mModelConfigRemoteCallbackList.register(it) }
        mVIotDevice?.pullModelConfig()
    }

    /**
     * 上报事件
     */
    override fun sendEvent(events: List<VIotDeviceEvent>, iOnVIotResultListener: IOnVIotResultListener?) {
        if (mVIotDevice?.isSupportViot == true) {
            val id = mVIotDevice?.sendEvent(events)
            iOnVIotResultListener?.let { id?.let { mBinderMap.put(id, iOnVIotResultListener) } }
        } else {
            iOnVIotResultListener?.onFailed(ResultCodes.RESULT_CODE_31, "Pull model config result: isSupportViot is ${mVIotDevice?.isSupportViot}")
        }
    }

    /**
     * 重置设备
     */
    override fun resetDevice() {
        mVIotDevice?.resetDevice()
    }

    /**
     * 订阅 set_properties
     */
    override fun subscribeSetProperties(iOnSetPropertiesListener: IOnSetPropertiesListener?) {
        if (mVIotDevice?.isSupportViot == true) {
            iOnSetPropertiesListener?.let { mSetPropertiesRemoteCallbackList.register(it) }
        } else {
            LogUtil.e(TAG, "Pull model config result: isSupportViot is ${mVIotDevice?.isSupportViot}")
        }
    }

    /**
     * 订阅 set_properties (List 集合)
     */
    override fun subscribeSetPropertyList(iOnSetPropertyListListener: IOnSetPropertyListListener?) {
        if (mVIotDevice?.isSupportViot == true) {
            iOnSetPropertyListListener?.let { mSetPropertyListRemoteCallbackList.register(iOnSetPropertyListListener) }
        } else {
            LogUtil.e(TAG, "Pull model config result: isSupportViot is ${mVIotDevice?.isSupportViot}")
        }
    }

    /**
     * 订阅 Action
     */
    override fun subscribeAction(iOnActionListener: IOnActionListener?) {
        if (mVIotDevice?.isSupportViot == true) {
            iOnActionListener?.let { mActionRemoteCallbackList.register(it) }
        } else {
            LogUtil.e(TAG, "Pull model config result: isSupportViot is ${mVIotDevice?.isSupportViot}")
        }
    }

    /**
     * 订阅 get_properties
     */
    override fun subscribeGetProperties(iOnGetPropertiesListener: IOnGetPropertiesListener?) {
        if (mVIotDevice?.isSupportViot == true) {
            iOnGetPropertiesListener?.let { mGetPropertiesRemoteCallbackList.register(it) }
        } else {
            LogUtil.e(TAG, "Pull model config result: isSupportViot is ${mVIotDevice?.isSupportViot}")
        }
    }

    /**
     * 订阅消息推送
     */
    override fun subscrbiePushMessage(iMessageArrivedListener: IMessageArrivedListener?) {
        iMessageArrivedListener?.let { mPushMessageRemoteCallbackList.register(it) }
    }

    /**
     * 订阅远程调试
     */
    override fun subscribeRemoteDebug(iRemoteDebugListener: IRemoteDebugListener?) {
        iRemoteDebugListener?.let { mDebugRemoteCallbackList.register(it) }
    }

    /**
     * 订阅设备和场景刷新监听
     */
    override fun subscribeDataRefresh(iDataRefreshListener: IDataRefreshListener?) {
        iDataRefreshListener?.let { mDataRefreshRemoteCallbackList.register(it) }
    }

    /**
     * 订阅账号信息刷新
     */
    override fun subscribeAccountRefresh(iAccountMessageListener: IAccountMessageListener?) {
        iAccountMessageListener?.let { mAccountRefreshRemoteCallbackList.register(it) }
    }

    /**
     * 订阅 MQTT 消息监听
     */
    override fun subscribeExtraMqttMessage(iDeviceExtraMqttListener: IDeviceExtraMqttListener?) {
        iDeviceExtraMqttListener?.let { mExtraMqttRemoteCallbackList.register(iDeviceExtraMqttListener) }
    }

    /**
     * 发送 MQTT 消息
     */
    override fun sendMqttMessage(payload: String?, subDid: String?, isReply: Boolean) {
        mVIotDevice?.sendMqttMessage(payload, subDid, isReply)
    }

    /**
     * 设备绑定
     */
    private fun handleDeviceBind() {
        takeIf { !mVIotDevice?.userId.isNullOrEmpty() }?.run {
            VIotDevicePreference.saveBindFlag(context, true)
            mIOnDeviceBindListener?.onBind()
            subscribePeriodSendProps()
        }
        mVIotDevice?.isInitSuccess = true
        subscribePing()
        mIOnDeviceBindListener?.onSucceed()
    }

    /**
     * 设备解绑
     */
    private fun handleDeviceUnbind() {
        VIotDevicePreference.saveBindFlag(context, false)
        mIOnDeviceBindListener?.onUnBind()
    }

    private fun handleReportLog(result: String?) = takeIf { result == "0" }?.run { FileUtil.saveFileObject(context, "offlineReason", null) }

    private fun handleNetworkEnable() {
        unsubscribeRetryInit()
        mVIotDevice?.initialize(context, mIOnDeviceBindListener)
    }

    private fun handleRetryInit() {
        mVIotDevice?.isInitSuccess = false
        subscribeRetryInit()
    }

    /**
     * 每隔一段时间上报所有属性
     */
    private fun subscribePeriodSendProps() {
        unsubscribePeriodSendProps()
        mPeriodDisposable = Observable.interval(60, 60, TimeUnit.MINUTES)
            .compose(RxScheduler.schedulerTransformer2())
            .onTerminateDetach()
            .subscribe({ mVIotDevice?.reportAllProperties() }, { LogUtil.d(TAG, it.message ?: "") })
    }

    /**
     * 重试初始化
     */
    private fun subscribeRetryInit() {
        unsubscribeRetryInit()
        mRetryDisposable = Observable.interval(mTime, mTime, TimeUnit.SECONDS)
            .compose(RxScheduler.schedulerTransformer2())
            .takeUntil { VIotUtil.isNetworkEnable(context) }
            .filter { VIotUtil.isNetworkEnable(context) }
            .subscribe({
                mVIotDevice?.initialize(context, mIOnDeviceBindListener)
                takeIf { mTime < 600 }?.let { mTime *= 2 }
            }, {
                LogUtil.e(TAG, it.message ?: "")
                VIotRxBus.instance.post(VIotBusEvent.MSG_RETRY_INITIALIZE)
            })
    }

    /**
     * 每隔一段时间 ping 连接
     */
    private fun subscribePing() {
        unsubscribePing()
        mPingDisposable = Observable.interval(20, 20, TimeUnit.MINUTES)
            .compose(RxScheduler.schedulerTransformer2())
            .onTerminateDetach()
            .subscribe({ mVIotDevice?.pingConnection() }, { LogUtil.e(TAG, it.message ?: "") })
    }

    /**
     * 取消重试初始化订阅
     */
    private fun unsubscribeRetryInit() {
        mRetryDisposable?.dispose()
        mRetryDisposable = null
    }

    /**
     * 取消定时上报属性订阅
     */
    private fun unsubscribePeriodSendProps() {
        mPeriodDisposable?.dispose()
        mPeriodDisposable = null
    }

    /**
     * 取消定时 ping 订阅
     */
    private fun unsubscribePing() {
        mPingDisposable?.dispose()
        mPingDisposable = null
    }

    companion object {
        private val TAG = IVIotHostServiceProxy::class.java.simpleName
    }
}
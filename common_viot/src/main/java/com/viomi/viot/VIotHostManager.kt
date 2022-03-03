package com.viomi.viot

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.alibaba.fastjson.JSON
import com.viomi.viot.account.AccountMessage
import com.viomi.viot.account.IAccountMessageListener
import com.viomi.viot.action.VIotDeviceAction
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.device.service.VIotHostService
import com.viomi.viot.event.VIotDeviceEvent
import com.viomi.viot.https.ResultCodes
import com.viomi.viot.https.VIotClient
import com.viomi.viot.listener.*
import com.viomi.viot.preference.VIotDevicePreference
import com.viomi.viot.property.VIotDeviceProperty
import com.viomi.viot.push.IMessageArrivedListener
import com.viomi.viot.push.PushMessage
import com.viomi.viot.repository.DeviceRepository
import com.viomi.viot.utils.LogUtil
import com.viomi.viot.utils.VIotUtil
import com.viomi.viot.utils.encrypt.AESUtil
import com.viomi.viot.utils.encrypt.RSAUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/13
 *     desc   : SDK 管理类
 *     version: 1.0
 * </pre>
 */
class VIotHostManager {
    private var mIvIotDeviceService: IVIotDeviceService? = null
    private var mVIotServiceConnection: VIotServiceConnection? = null
    private var mLogSwitch = false
    private var mIsDebug = false
    private var mDisposable: Disposable? = null
    private var mRegisterDisposable: Disposable? = null

    /**
     * 初始化 Viot 设备
     */
    fun startDevice(context: Context, config: VIotDeviceConfig, onDeviceBindListener: OnDeviceBindListener?) = when {
        // 入参无三元组情况, 动态注册设备
        !config.hasTriad -> {
            mRegisterDisposable?.dispose()
            config.mac = mac
            VIotClient.instance.initialize(config, mIsDebug)
            mRegisterDisposable = Observable.just(context)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .flatMap {
                    RSAUtil.createRSAKeyPair(it)
                    Observable.interval(0, 5, TimeUnit.SECONDS)
                }
                .filter { VIotUtil.isNetworkEnable(context) }
                .onTerminateDetach()
                .flatMap { DeviceRepository.getVmCode(config, RSAUtil.getRSAPublicKey(context)) }
                .takeUntil { it.code == 100 }
                .onTerminateDetach()
                .subscribe({
                    val aesDecryptResult = AESUtil.decrypt(it.result, VIotClient.instance.pAppSecretKey)
                    val keys = JSON.parseObject(aesDecryptResult, VIotDeviceConfig::class.java)
                    config.productId = keys?.productId
                    config.deviceId = keys.deviceId
                    config.deviceAccessKey = RSAUtil.rsaDecode(keys?.deviceAccessKey, RSAUtil.getRSAPrivateKey(context))
                    config.cloudPublicKey = keys?.cloudPublicKey
                    config.needVerify = false
                    config.hasTriad = true
                    registerDevice(context, config, onDeviceBindListener)
                }, {
                    LogUtil.e(TAG, it.message.toString())
                    onDeviceBindListener?.onFailed(ResultCodes.RESULT_CODE_0, it.localizedMessage)
                })
        }
        // 入参有三元组, 直接绑定设备
        else -> {
            config.mac = mac
            registerDevice(context, config, onDeviceBindListener)
        }
    }

    /**
     * 停止 Remote Service
     */
    fun stopDevice(context: Context) {
        mRegisterDisposable?.dispose()
        mRegisterDisposable = null
        stopAlarmCount()
        mVIotServiceConnection?.let {
            kotlin.runCatching { context.unbindService(it) }.onFailure { LogUtil.e(TAG, it.message.toString()) }
            mVIotServiceConnection = null
        }
        mIvIotDeviceService = null
    }

    /**
     * 注册 Remote Service
     */
    private fun registerDevice(context: Context, config: VIotDeviceConfig, onDeviceBindListener: OnDeviceBindListener?) {
        if (config.cloudPublicKey.isNullOrBlank() || config.deviceAccessKey.isNullOrBlank() || config.deviceId.isNullOrBlank() || config.productId == 0) {
            LogUtil.e(TAG, "VIot Device config is not correct.")
            onDeviceBindListener?.onFailed(ResultCodes.RESULT_CODE_0, "VIot Device config is not correct.")
        } else {
            stopDevice(context)
            mVIotServiceConnection = VIotServiceConnection(context, config, onDeviceBindListener)
            mVIotServiceConnection?.takeIf {
                val intent = Intent(context, VIotHostService::class.java)
                !context.bindService(intent, it, Context.BIND_AUTO_CREATE)
            }?.let {
                onDeviceBindListener?.onFailed(ResultCodes.RESULT_CODE_0, "Start VIot device service failed.")
                LogUtil.e(TAG, "Start VIot device service failed.")
                mVIotServiceConnection = null
            }
        }
    }

    /**
     * 绑定设备
     */
    private fun bind(context: Context, config: VIotDeviceConfig, onDeviceBindListener: OnDeviceBindListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.bindDevice(config, object : IOnDeviceBindListener.Stub() {
                override fun onSucceed() {
                    onDeviceBindListener?.onSucceed(config)
                    startAlarmCount(context, config, onDeviceBindListener)
                    LogUtil.d(TAG, "VIot device onSucceed.")
                }

                override fun onFailed(code: Int, message: String?) {
                    onDeviceBindListener?.onFailed(code, message)
                    stopAlarmCount()
                }

                override fun onBind() {
                    onDeviceBindListener?.onBind()
                    LogUtil.d(TAG, "VIot device onBind.")
                }

                override fun onUnBind() {
                    stopDevice(context)
                    stopAlarmCount()
                    LogUtil.d(TAG, "VIot device onUnBind.")
                    onDeviceBindListener?.onUnBind()
                }
            })
        }.onFailure {
            onDeviceBindListener?.onFailed(ResultCodes.RESULT_CODE_0, "Start VIot device failed, reason: ${it.localizedMessage}")
            stopAlarmCount()
            LogUtil.e(TAG, "Start VIot device failed, reason: ${it.localizedMessage}")
            it.printStackTrace()
        }
    }

    /**
     * 属性上报
     */
    fun syncProperties(list: List<VIotDeviceProperty>, onVIotResultListener: OnVIotResultListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.syncProperties(list, object : IOnVIotResultListener.Stub() {
                override fun onSucceed() {
                    onVIotResultListener?.onSucceed()
                }

                override fun onFailed(code: Int, message: String) {
                    onVIotResultListener?.onFailed(code, message)
                }
            })
        }.onFailure {
            it.printStackTrace()
            onVIotResultListener?.onFailed(ResultCodes.RESULT_CODE_0, "Sync properties failed, reason: ${it.message}")
            LogUtil.e(TAG, "Sync properties failed, reason: ${it.message}")
        }
    }

    /**
     * 事件上报
     */
    fun sendEvent(events: List<VIotDeviceEvent>, onVIotResultListener: OnVIotResultListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.sendEvent(events, object : IOnVIotResultListener.Stub() {
                override fun onSucceed() {
                    onVIotResultListener?.onSucceed()
                }

                override fun onFailed(code: Int, message: String) {
                    onVIotResultListener?.onFailed(code, message)
                }
            })
        }.onFailure {
            it.printStackTrace()
            onVIotResultListener?.onFailed(ResultCodes.RESULT_CODE_0, "Send event failed, reason: ${it.message}")
            LogUtil.e(TAG, "Send event failed, reason: ${it.message}")
        }
    }

    /**
     * 注册 set_properties 监听
     */
    fun registerSetPropertiesCallback(onSetPropertiesListener: OnSetPropertiesListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeSetProperties(object : IOnSetPropertiesListener.Stub() {
                override fun onSet(property: VIotDeviceProperty) {
                    onSetPropertiesListener?.onSetProperty(property)
                }
            })
        }.onFailure {
            it.printStackTrace()
            onSetPropertiesListener?.onFailed(ResultCodes.RESULT_CODE_0, "Register set_properties failed, reason: ${it.message}")
            LogUtil.e(TAG, "Register set_properties failed, reason: ${it.message}")
        }
    }

    /**
     * 注册 set_properties 监听
     */
    fun registerSetPropertyListCallback(onSetPropertyListListener: OnSetPropertyListListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeSetPropertyList(object : IOnSetPropertyListListener.Stub() {
                override fun onSet(list: MutableList<VIotDeviceProperty>?) {
                    onSetPropertyListListener?.onSetProperty(list)
                }
            })
        }.onFailure {
            it.printStackTrace()
            onSetPropertyListListener?.onFailed(ResultCodes.RESULT_CODE_0, "Register set_properties failed, reason: ${it.message}")
            LogUtil.e(TAG, "Register set_properties failed, reason: ${it.message}")
        }
    }

    /**
     * 注册 action 监听
     */
    fun registerActionCallback(onActionListener: OnActionListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeAction(object : IOnActionListener.Stub() {
                override fun onAction(action: VIotDeviceAction) {
                    action.onActionOutChangeListener = object : OnActionOutChangeListener {
                        override fun onListChange() {
                            mIvIotDeviceService?.notifyActionOutAdd(action)
                        }
                    }
                    onActionListener?.onAction(action)
                }
            })
        }.onFailure {
            it.printStackTrace()
            onActionListener?.onFailed(ResultCodes.RESULT_CODE_0, "Register action failed, reason: ${it.message}")
            LogUtil.e(TAG, "Register action failed, reason: ${it.message}")
        }
    }

    /**
     * 注册 get_properties 监听
     */
    fun registerGetPropertiesCallback(onGetPropertiesListener: OnGetPropertiesListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeGetProperties(object : IOnGetPropertiesListener.Stub() {
                override fun onGetProperty(list: MutableList<VIotDeviceProperty>?, id: String?) {
                    mIvIotDeviceService?.notifyPropertiesValue(onGetPropertiesListener?.onGetProperty(list), id)
                }
            })
        }.onFailure {
            it.printStackTrace()
            onGetPropertiesListener?.onFailed(ResultCodes.RESULT_CODE_0, "Register Get properties failed, reason: ${it.message}")
            LogUtil.e(TAG, "Register Get properties failed, reason: ${it.message}")
        }
    }

    /**
     * 设备与云端互通检测
     */
    fun checkConnection(onVIotResultListener: OnVIotResultListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.checkDeviceConnect(object : IOnVIotResultListener.Stub() {
                override fun onFailed(code: Int, message: String?) {
                    LogUtil.e(TAG, "VIot device is offline, code: $code, reason: $message.")
                    onVIotResultListener?.onFailed(code, message)
                }

                override fun onSucceed() {
                    LogUtil.d(TAG, "VIot device is connected.")
                    onVIotResultListener?.onSucceed()
                }
            })
        }.onFailure {
            it.printStackTrace()
            onVIotResultListener?.onFailed(ResultCodes.RESULT_CODE_0, "check connection failed, reason: ${it.message}")
            LogUtil.e(TAG, "check connection failed, reason: ${it.message}")
        }
    }

    /**
     * 消息推送监听
     */
    fun registerPushMessageCallback(onMessageArrivedListener: OnMessageArrivedListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscrbiePushMessage(object : IMessageArrivedListener.Stub() {
                override fun onMessageArrived(message: PushMessage?) {
                    onMessageArrivedListener?.onMessageArrived(message)
                }
            })
        }.onFailure {
            LogUtil.e(TAG, "Register PushMessage failed, reason: ${it.message}")
        }
    }

    /**
     * 远程调试设备监听
     */
    fun registerRemoteDebugCallback(onRemoteDebugListener: OnRemoteDebugListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeRemoteDebug(object : IRemoteDebugListener.Stub() {
                override fun onRemoteDebug(content: String?) {
                    onRemoteDebugListener?.remoteMessage(content)
                }
            })
        }.onFailure { LogUtil.e(TAG, "Register remote debug failed, reason: ${it.message}") }
    }

    /**
     * 监听账号信息刷新
     */
    fun registerAccountRefreshCallback(onAccountRefreshListener: OnAccountRefreshListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeAccountRefresh(object : IAccountMessageListener.Stub() {
                override fun onAccountRefresh(accountMessage: AccountMessage?) {
                    onAccountRefreshListener?.onAccountRefresh(accountMessage)
                }
            })
        }.onFailure { LogUtil.e(TAG, "Register account refresh failed, reason: ${it.message}") }
    }

    /**
     * 监听数据刷新
     */
    fun registerDataRefreshCallback(onDataRefreshListener: OnDataRefreshListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeDataRefresh(object : IDataRefreshListener.Stub() {
                override fun onDeviceRefresh() {
                    onDataRefreshListener?.onDevicesRefresh()
                }

                override fun onSceneRefresh() {
                    onDataRefreshListener?.onSceneRefresh()
                }

                override fun onMiTokenRefresh() {
                    onDataRefreshListener?.onMiTokenRefresh()
                }
            })
        }.onFailure {
            LogUtil.e(TAG, "Register data refresh failed, reason: ${it.message}")
        }
    }

    /**
     * 监听 SDK 无法解析的消息回调
     */
    fun registerExtraMqttCallback(onResultCallback: (String?, String?) -> Unit?) {
        kotlin.runCatching {
            mIvIotDeviceService?.subscribeExtraMqttMessage(object : IDeviceExtraMqttListener.Stub() {
                override fun onExtraMessage(message: String?, topic: String?) {
                    onResultCallback(message, topic)
                }
            })
        }.onFailure {
            LogUtil.e(TAG, "Register extra mqtt failed, reason: ${it.message}")
        }
    }

    /**
     * 上报 mqtt 消息
     */
    fun sendMqttMessage(payload: String, subDid: String, isReply: Boolean) {
        kotlin.runCatching {
            mIvIotDeviceService?.sendMqttMessage(payload, subDid, isReply)
        }.onFailure {
            LogUtil.e(TAG, "Send sub device msg failed, reason: ${it.message}")
        }
    }

    /**
     * 重置设备
     */
    fun resetDevice() {
        kotlin.runCatching { mIvIotDeviceService?.resetDevice() }.onFailure { LogUtil.e(TAG, "resetDevice failed, reason: ${it.message}") }
    }

    /**
     * 切换测试和正式环境
     */
    fun setDebugEnvironment(isDebug: Boolean) {
        mIsDebug = isDebug
    }

    /**
     * 日志打印开关
     */
    fun enableLog(enable: Boolean) {
        mLogSwitch = enable
        LogUtil.isPrintLog = mLogSwitch
    }

    /**
     * 外部设置 mac 地址
     */
    var mac: String by Delegates.observable(VIotUtil.getMacAddress()) { _, old, new ->
        LogUtil.d(TAG, "Mac update, old = $old, new = $new")
        kotlin.runCatching { mIvIotDeviceService?.updateMacAddress(new) }.onFailure { LogUtil.e(TAG, "Update mac fail, ${it.printStackTrace()}") }
    }

    /**
     * 配置设置
     */
    private fun setConfig() {
        kotlin.runCatching {
            mIvIotDeviceService?.enableLog(mLogSwitch)
            mIvIotDeviceService?.setDebugEnvironment(mIsDebug)
            mIvIotDeviceService?.updateMacAddress(mac)
        }.onFailure {
            LogUtil.e(TAG, "Set config failed, reason: ${it.message}")
        }
    }

    /**
     * 上报设备额外信息
     */
    fun uploadDeviceInfo(map: MutableMap<String, String>) {
        kotlin.runCatching {
            mIvIotDeviceService?.uploadExtraDevInfo(map)
        }.onFailure { LogUtil.e(TAG, "uploadDeviceInfo failed, reason: ${it.message}") }
    }

    /**
     * 强制设备与用户绑定
     */
    fun setForceBound(context: Context) = VIotDevicePreference.saveBindFlag(context, isBind = false)

    /**
     * 监听 viot 连接
     */
    fun registerViotConnectedListener(onViotConnectedListener: OnViotConnectedListener?) {
        kotlin.runCatching {
            mIvIotDeviceService?.registerViotConnectedListener(object : IViotConnectedListener.Stub() {
                override fun onConnected() {
                    onViotConnectedListener?.onConnected()
                }

                override fun onDisconnected() {
                    onViotConnectedListener?.onDisconnected()
                }
            })
        }
    }

    /**
     * 获取 model 配置
     */
    fun getModelConfig(onResultCallback: (Boolean) -> Unit?) = kotlin.runCatching {
        mIvIotDeviceService?.getModelConfig(object : IModelConfigListener.Stub() {
            override fun onGetConfig(isSupportViot: Boolean) {
                onResultCallback(isSupportViot)
            }
        })
    }

    /**
     * 进程心跳监听
     */
    private fun startAlarmCount(context: Context, config: VIotDeviceConfig, onDeviceBindListener: OnDeviceBindListener?) {
        stopAlarmCount()
        LogUtil.d(TAG, "Start alarm count.")
        mDisposable = Observable.interval(120, 10, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .onTerminateDetach()
            .map { VIotUtil.isServiceWork(context, VIotHostService::class.java.name) }
            .onTerminateDetach()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it) {
                    LogUtil.d(TAG, "VIot service heart beat.")
                } else {
                    startDevice(context, config, onDeviceBindListener)
                }
            }, { LogUtil.e(TAG, it.message.toString()) })
    }

    /**
     * 停止心跳监听
     */
    private fun stopAlarmCount() {
        mDisposable?.dispose()
        mDisposable = null
    }

    private inner class VIotServiceConnection(
        private val context: Context,
        private val config: VIotDeviceConfig,
        private val onDeviceBindListener: OnDeviceBindListener?
    ) : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtil.d(TAG, "VIot device service is connected.")
            mIvIotDeviceService = IVIotDeviceService.Stub.asInterface(service)
            setConfig()
            bind(context, config, onDeviceBindListener)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            LogUtil.e(TAG, "VIot device service is disconnected.")
            mIvIotDeviceService = null
        }

        override fun onBindingDied(name: ComponentName?) {
            LogUtil.e(TAG, "VIot device service is onBindingDied.")
        }

        override fun onNullBinding(name: ComponentName?) {
            LogUtil.e(TAG, "VIot device service is onNullBinding.")
        }
    }

    companion object {
        private val TAG = VIotHostManager::class.java.simpleName

        val instance: VIotHostManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            VIotHostManager()
        }
    }
}

package com.viomi.viot.device.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.viomi.viot.R
import com.viomi.viot.VIotHostManager
import com.viomi.viot.account.AccountMessage
import com.viomi.viot.action.VIotDeviceAction
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.databinding.ActivityMainBinding
import com.viomi.viot.event.VIotDeviceEvent
import com.viomi.viot.listener.*
import com.viomi.viot.property.VIotDeviceProperty
import com.viomi.viot.push.PushMessage
import com.viomi.viot.utils.VIotUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private var mDisposable: Disposable? = null
    private var mDeviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding?.toolbar?.title = "VIot Device"
        mBinding?.isDebug = isDebug()
        mBinding?.text?.movementMethod = ScrollingMovementMethod.getInstance()
//        setSupportActionBar(mBinding?.toolbar)
        ApiClient.instance.isDebug = mBinding?.isDebug ?: false
        ApiClient.instance.init()
        initListener()
        initUI()
        initDevice()
    }

    private fun initDevice() {
        VIotHostManager.instance.stopDevice(this)
        VIotHostManager.instance.enableLog(true)
        VIotHostManager.instance.setDebugEnvironment(mBinding?.isDebug ?: true)
        val triad = getTriad()
        val config: VIotDeviceConfig
        /**
         * 没有包含云米三元组信息
         */
        if (triad.isNullOrBlank()) {
            config = VIotDeviceConfig(false).apply {
                miDid = "152586162"
                miModel = "viomi.fridge.x4"
                token = getToken()
                userId = getUserId()
            }
        }
        /**
         * 已经含有云米三元组信息
         */
        else {
            val jsonObject = JSONObject(triad)
            val pid = jsonObject.optInt("pid")
            val did = jsonObject.optString("did")
            val accessKey = jsonObject.optString("accessKey")
            val cloudPublicKey = jsonObject.optString("cloudPublicKey")
            config = VIotDeviceConfig(pid, getToken(), getUserId(), did, accessKey, cloudPublicKey, null, null)
        }

        VIotHostManager.instance.startDevice(this, config, object : OnDeviceBindListener {
            override fun onSucceed(config: VIotDeviceConfig) {
                runOnUiThread { initUI() }
                with(JSONObject()) {
                    mDeviceId = config.deviceId
                    put("pid", config.productId)
                    put("did", config.deviceId)
                    put("accessKey", config.deviceAccessKey)
                    put("cloudPublicKey", config.cloudPublicKey)
                    saveTriad(toString())
                }
                Log.d(TAG, "onSucceed")
                setText("设备初始化成功")
                register()
            }

            override fun onFailed(code: Int, message: String?) {
                Log.d(TAG, "onFailed")
                VIotHostManager.instance.stopDevice(this@MainActivity)
                saveToken("")
                saveUserId("")
                setText("设备初始化失败, 错误码: $code, 信息: $message")
                runOnUiThread { initUI() }
            }

            override fun onBind() {
                Log.d(TAG, "onBind")
                setText("设备绑定成功")
                reportProperties()
            }

            override fun onUnBind() {
                Log.d(TAG, "onUnBind")
                saveToken("")
                saveUserId("")
                runOnUiThread { initUI() }
                initDevice()
            }
        })
    }

    private fun initUI() {
        mBinding?.reportProperty?.visibility = if (getToken().isNullOrBlank()) View.INVISIBLE else View.VISIBLE
        mBinding?.sendEvent?.visibility = if (getToken().isNullOrBlank()) View.INVISIBLE else View.VISIBLE
        mBinding?.queryMapping?.visibility = if (getToken().isNullOrBlank()) View.INVISIBLE else View.VISIBLE
        mBinding?.resetOrLogin?.text = if (getToken().isNullOrBlank()) "扫码登录" else "重置设备"
        mBinding?.text?.text = ""
    }

    private fun initListener() {
        mBinding?.testCheck?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && mBinding?.group?.visibility == View.VISIBLE) {
                saveDebug(isChecked)
                saveTriad("")
                getQRCode()
            }
        }

        mBinding?.releaseCheck?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && mBinding?.group?.visibility == View.VISIBLE) {
                saveDebug(!isChecked)
                saveTriad("")
                getQRCode()
            }
        }

        mBinding?.reportProperty?.setOnClickListener { reportProperties() }
        mBinding?.sendEvent?.setOnClickListener { sendEvent() }
        mBinding?.resetOrLogin?.setOnClickListener {
            if ("重置设备" == mBinding?.resetOrLogin?.text.toString()) {
                resetDevice()
            } else {
                mBinding?.group?.visibility = View.VISIBLE
            }
        }
        mBinding?.clearLog?.setOnClickListener { mBinding?.text?.text = "" }
        mBinding?.checkConnection?.setOnClickListener { checkConnection() }
        mBinding?.noLogin?.setOnClickListener { mBinding?.group?.visibility = View.GONE }
    }

    private fun register() {
        VIotHostManager.instance.registerActionCallback(object : OnActionListener {
            override fun onAction(action: VIotDeviceAction?) {
                setText("下发 action: aiid:${action?.aiid}, siid:${action?.siid}")
                val list: MutableList<VIotDeviceProperty> = ArrayList()
                list.add(VIotDeviceProperty(3, 1, true, null))
                list.add(VIotDeviceProperty(4, 2, "fgfaa", null))
                list.add(VIotDeviceProperty(5, 1, 110, null))
                action?.replyOut(list)
            }

            override fun onFailed(code: Int, message: String?) {
                setText("注册action监听失败, 错误码: $code, 信息: $message")
            }
        })

        VIotHostManager.instance.registerGetPropertiesCallback(object : OnGetPropertiesListener {
            override fun onGetProperty(properties: MutableList<VIotDeviceProperty>?): MutableList<VIotDeviceProperty>? {
                setText("下发 get_properties: $properties")
                properties?.forEach { it.value = 0 }
                return properties
            }

            override fun onFailed(code: Int, message: String?) {
                setText("注册get_properties监听失败, 错误码: $code, 信息: $message")
            }
        })

        VIotHostManager.instance.registerSetPropertiesCallback(object : OnSetPropertiesListener {
            override fun onSetProperty(property: VIotDeviceProperty?) {
                setText("下发 set_properties: piid:${property?.siid}, siid:${property?.piid}, value: ${property?.value}")
            }

            override fun onFailed(code: Int, message: String?) {
                setText("注册set_properties监听失败, 错误码: $code, 信息: $message")
            }
        })

        VIotHostManager.instance.registerSetPropertyListCallback(object : OnSetPropertyListListener {
            override fun onSetProperty(property: MutableList<VIotDeviceProperty>?) {
                setText("下发 set_properties: $property")
            }

            override fun onFailed(code: Int, message: String?) {
                setText("注册set_properties监听失败, 错误码: $code, 信息: $message")
            }
        })

        VIotHostManager.instance.registerDataRefreshCallback(object : OnDataRefreshListener {
            override fun onSceneRefresh() {
                setText("场景刷新")
            }

            override fun onDevicesRefresh() {
                setText("设备列表刷新")
            }

            override fun onMiTokenRefresh() {
                setText("小米Token刷新")
            }
        })

        VIotHostManager.instance.registerPushMessageCallback(object : OnMessageArrivedListener {
            override fun onMessageArrived(pushMessage: PushMessage?) {
                setText("消息推送: ${pushMessage.toString()}")
            }
        })

        VIotHostManager.instance.registerRemoteDebugCallback(object : OnRemoteDebugListener {
            override fun remoteMessage(message: String?) {
                setText("远程调试: $message")
            }
        })

        VIotHostManager.instance.registerAccountRefreshCallback(object : OnAccountRefreshListener {
            override fun onAccountRefresh(accountMessage: AccountMessage?) {
                setText("账号信息更新: $accountMessage")
            }
        })

        VIotHostManager.instance.registerExtraMqttCallback(onResultCallback = { message, isSelf ->
            setText("是否本设备消息:$isSelf, message = $message")
        })
    }

    private fun reportProperties() {
        val properties = ArrayList<VIotDeviceProperty>()
        val property = VIotDeviceProperty(3, 1, "true", null)
        val property2 = VIotDeviceProperty(3, 2, "0", null)
        properties.add(property)
        properties.add(property2)
        VIotHostManager.instance.syncProperties(properties, object : OnVIotResultListener {
            override fun onSucceed() {
                setText("属性上报成功")
            }

            override fun onFailed(code: Int, message: String?) {
                setText("属性上报失败, 错误码: $code, 信息: $message")
            }
        })
    }

    private fun sendEvent() {
        val list = ArrayList<VIotDeviceProperty>()
        val property = VIotDeviceProperty(4, 1, "true", null)
        val property2 = VIotDeviceProperty(4, 2, "100", null)
        list.add(property)
        list.add(property2)
        val event = VIotDeviceEvent(4, 3, list, mDeviceId)
        val events = ArrayList<VIotDeviceEvent>()
        events.add(event)
        VIotHostManager.instance.sendEvent(events, object : OnVIotResultListener {
            override fun onSucceed() {
                setText("事件上报成功")
            }

            override fun onFailed(code: Int, message: String?) {
                setText("事件上报失败, 错误码: $code, 信息: $message")
            }
        })
    }

    private fun checkConnection() {
        VIotHostManager.instance.checkConnection(object : OnVIotResultListener {
            override fun onSucceed() {
                setText("设备已经连接")
            }

            override fun onFailed(code: Int, message: String?) {
                setText("设备失去连接, 错误码: $code, 信息: $message")
            }
        })
    }

    private fun resetDevice() {
        VIotHostManager.instance.resetDevice()
    }

    override fun onDestroy() {
        super.onDestroy()
        VIotHostManager.instance.stopDevice(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getQRCode() {
        if (!TextUtils.isEmpty(getToken())) return
        stopCheck()
        var mac: String = VIotUtil.getMacAddress().toLowerCase(Locale.getDefault())
        mac = mac.replace(":", "")
        val clientId = mac + System.currentTimeMillis() / 1000
        Observable.just(true)
            .subscribeOn(Schedulers.io())
            .onTerminateDetach()
            .flatMap { ApiClient.instance.service?.createQRCode(clientId, "1") }
            .observeOn(AndroidSchedulers.mainThread())
            .onTerminateDetach()
            .subscribe(object : Observer<String?> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String?) {
                    t?.let {
                        val jsonObject = JSONObject(t)
                        val qrUrl = jsonObject.optJSONObject("mobBaseRes")?.optString("result")
                        val uri = Uri.parse(qrUrl)
                        showQRCode(uri)
                        checkLoginStatus(clientId)
                    }
                }

                override fun onError(e: Throwable) {}
            })
    }

    private fun checkLoginStatus(clientId: String) {
        val jsonObject = JSONObject()
        jsonObject.put("clientID", clientId)
        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())
        mDisposable = Flowable.interval(0, 3, TimeUnit.SECONDS)
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .onTerminateDetach()
            .flatMap { ApiClient.instance.service?.checkLoginStatus(requestBody) }
            .takeUntil {
                val json = JSONObject(it)
                val code = json.optJSONObject("mobBaseRes")?.optInt("code")
                code != 915 || code == 100
            }
            .observeOn(AndroidSchedulers.mainThread())
            .onTerminateDetach()
            .subscribe({
                val json = JSONObject(it)
                val code = json.optJSONObject("mobBaseRes")?.optInt("code")
                if (code == 100) {
                    val token = json.optJSONObject("mobBaseRes")?.optJSONObject("result")?.optString("token")
                    val userId = json.optJSONObject("mobBaseRes")?.optJSONObject("result")?.optJSONObject("loginData")?.optString("userId")
                    saveToken(token)
                    saveUserId(userId)
                    initDevice()
                    mBinding?.group?.visibility = View.GONE
                } else if (code != 915) {
                    getQRCode()
                }
            }, {
                Log.e(TAG, it.message ?: "")
            })
    }

    private fun stopCheck() {
        mDisposable?.dispose()
        mDisposable = null
    }

    private fun showQRCode(uri: Uri) {
        val request = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setResizeOptions(ResizeOptions(200, 200))
            .build()
        val controller = Fresco.newDraweeControllerBuilder()
            .setOldController(mBinding?.qrCode?.controller)
            .setImageRequest(request)
            .build() as? PipelineDraweeController
        mBinding?.qrCode?.controller = controller
    }

    private fun setText(str: String) {
        val content = "${mBinding?.text?.text?.toString()}$str\n"
        runOnUiThread { mBinding?.text?.text = content }
    }

    private fun isDebug(): Boolean {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isDebug", true)
    }

    private fun saveDebug(isDebug: Boolean) {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDebug", isDebug)
        editor.apply()
        mBinding?.isDebug = isDebug
        ApiClient.instance.isDebug = isDebug
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    private fun getUserId(): String? {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", "")
    }

    private fun saveToken(token: String?) {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    private fun saveUserId(userId: String?) {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }

    private fun saveTriad(message: String) {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("triad", message)
        editor.apply()
    }

    private fun getTriad(): String? {
        val sharedPreferences = getSharedPreferences("vm", Context.MODE_PRIVATE)
        return sharedPreferences.getString("triad", "")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}

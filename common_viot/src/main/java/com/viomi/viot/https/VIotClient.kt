package com.viomi.viot.https

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.utils.LogUtil
import com.viomi.viot.utils.encrypt.EncryptUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/13
 *     desc   : Http 请求
 *     version: 1.0
 * </pre>
 */
class VIotClient {
    internal lateinit var pService: VIotService
    internal var pAppKey: String? = null
    internal var pAppSecretKey: String? = null

    fun initialize(vIotDeviceConfig: VIotDeviceConfig, isDebug: Boolean) {
        pAppKey = if (isDebug) APP_KEY_DEBUG else APP_KEY_RELEASE
        pAppSecretKey = if (isDebug) APP_SECRET_KEY_DEBUG else APP_SECRET_KEY_RELEASE
        val timeOut = 8 * 1000.toLong()
        val httpLoggingInterceptor = HttpLoggingInterceptor { message: String? -> LogUtil.d(TAG, message.toString()) }
        httpLoggingInterceptor.level = if (LogUtil.isPrintLog) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        //val captureInfoInterceptor = CaptureInfoInterceptor()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
            .addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                val oldHttpUrl = request.url
                val fullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(oldHttpUrl.scheme)
                    .host(oldHttpUrl.host)
                    .port(oldHttpUrl.port)
                    .build()

                EncryptUtil.headerEncode(request, builder, vIotDeviceConfig)
                chain.proceed(builder.url(fullUrl).build())
            }
            .addInterceptor(httpLoggingInterceptor)
            //.addInterceptor(captureInfoInterceptor)
            .build()
        initRetrofit(okHttpClient, isDebug)
    }

    private fun initRetrofit(okHttpClient: OkHttpClient, isDebug: Boolean) {
        val url = if (isDebug) DEVICE_HOME_DEBUG else DEVICE_HOME_RELEASE
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        pService = retrofit.create(VIotService::class.java)
    }

    companion object {
        private val TAG = VIotClient::class.java.simpleName
        const val DEVICE_HOME_RELEASE = "https://device-home.viomi.com.cn/"
        const val DEVICE_HOME_DEBUG = "https://device-home-test.viomi.com.cn/"
        const val APP_KEY_DEBUG = "hAZC26flIa1VhlY5"
        const val APP_KEY_RELEASE = "wtidsq6j4nYqcBhV"
        const val APP_SECRET_KEY_DEBUG = "scRTAGk3RBVJc9mp"
        const val APP_SECRET_KEY_RELEASE = "g5UNBB8XnufktCsJ"

        val instance: VIotClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            VIotClient()
        }
    }
}

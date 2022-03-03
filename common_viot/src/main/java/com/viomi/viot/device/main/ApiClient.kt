package com.viomi.viot.device.main

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory
import com.viomi.viot.utils.LogUtil
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by William on 2020/6/12.
 */
class ApiClient {
    var service: ApiService? = null
    var isDebug: Boolean = false
    var token: String = ""

    fun init() {
        val timeOut = 30 * 1000.toLong()
        val httpLoggingInterceptor = HttpLoggingInterceptor { message: String? ->
            LogUtil.d(TAG, message ?: "")
        }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
            .addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                val headerValues = request.headers("domain")
                if (headerValues.isEmpty()) {
                    chain.proceed(request)
                }
                require(headerValues.size <= 1) { "Only one domain in the headers" }
                builder.removeHeader("domain")
                val headerValue = headerValues[0]
                val newBaseUrl: HttpUrl?
                val oldHttpUrl = request.url
                if (headerValue == "viot") {
                    newBaseUrl = url.toHttpUrl()
                } else {
                    newBaseUrl = urlViomi.toHttpUrl()
                    builder.header("Authorization_v1", token)
                }

                val newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(newBaseUrl.scheme)
                    .host(newBaseUrl.host)
                    .port(newBaseUrl.port)
                    .build()
                chain.proceed(builder.url(newFullUrl).build())
            }
            .addInterceptor(httpLoggingInterceptor)
            .build()
        initRetrofit(okHttpClient)
    }

    private fun initRetrofit(okHttpClient: OkHttpClient) {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        service = retrofit.create(ApiService::class.java)
    }

    private val url: String get() = if (isDebug) VM_DEBUG else VM_RELEASE
    private val urlViomi: String get() = if (isDebug) VIOMI_DEBUG else VIOMI_RELEASE

    companion object {
        private val TAG = ApiClient::class.java.simpleName
        const val VM_RELEASE = "https://ms.viomi.com.cn"
        const val VM_DEBUG = "https://mstest.viomi.com.cn"
        const val VIOMI_RELEASE = "https://app-home.viomi.com.cn"
        const val VIOMI_DEBUG = "https://app-home-test.viomi.com.cn"

        val instance: ApiClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ApiClient()
        }
    }
}
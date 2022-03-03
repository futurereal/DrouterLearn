package com.viomi.iotdevice.common.http

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory
import com.viomi.iotdevice.common.util.LogUtil
import com.viomi.iotdevice.common.util.SignUtil
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.HashMap
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : Http 请求初始化
 *     version: 1.0
 * </pre>
 */
internal class IotApiClient {
    lateinit var pApiService: IotApiService

    fun init(isDebug: Boolean) {
        val timeOut = (20 * 1000).toLong()

        val httpLoggingInterceptor = HttpLoggingInterceptor { LogUtil.d(TAG, it) }
        httpLoggingInterceptor.level = if (logEnable) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                val headerValues = request.headers(DOMAIN)
                require(headerValues.isEmpty()) { "At least one domain in the headers." }
                LogUtil.d(TAG, request.header(DOMAIN) ?: "")
                builder.removeHeader(DOMAIN)
                val headerValue = headerValues[0]
                val newBaseUrl: HttpUrl?
                val oldHttpUrl: HttpUrl = request.url
                when (headerValue) {
                    DOMAIN_QR_CODE -> newBaseUrl = if (isDebug) QR_DEBUG_HOST.toHttpUrl() else QR_RELEASE_HOST.toHttpUrl()
                    DOMAIN_VIOMI -> {
                        newBaseUrl = if (isDebug) VIOMI_DEBUG_HOST.toHttpUrl() else VIOMI_RELEASE_HOST.toHttpUrl()
                        val map: MutableMap<String, String> = HashMap()
                        val time = System.currentTimeMillis()
                        val uuid = UUID.randomUUID().toString().replace("-", "")
                        map["VIOMI-App-Key"] = "fridge"
                        map["VIOMI-Access-Key-Id"] = "2ieMNkOqrcQEiGmV"
                        map["VIOMI-Timestamp"] = time.toString()
                        map["VIOMI-Noise"] = uuid
                        val sign = SignUtil.sign("post", map, "QIy6zy1MC3qvBUrwSq4xjddwPVqUXHcg")
                        builder.header("VIOMI-App-Key", "fridge")
                            .header("VIOMI-Access-Key-Id", "2ieMNkOqrcQEiGmV")
                            .header("VIOMI-Timestamp", time.toString())
                            .header("VIOMI-Noise", uuid)
                            .header("Authorization", sign)
                    }
                    else -> newBaseUrl = oldHttpUrl
                }
                LogUtil.d(TAG, newBaseUrl.toUrl().toString())
                val newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(newBaseUrl.scheme)
                    .host(newBaseUrl.host)
                    .port(newBaseUrl.port)
                    .build()
                chain.proceed(builder.url(newFullUrl).build())
            })
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(VIOMI_RELEASE_HOST)
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        pApiService = retrofit.create(IotApiService::class.java)
    }

    var logEnable: Boolean = false

    fun getRequestBody(jsonObject: JSONObject): RequestBody {
        return jsonObject.toString().toRequestBody("application/json".toMediaType())
    }

    companion object {
        private val TAG = IotApiClient::class.java.simpleName
        private const val QR_RELEASE_HOST = "https://vmall-auth.viomi.com.cn/"
        private const val QR_DEBUG_HOST = "https://vwater-auth.viomi.com.cn/"
        private const val VIOMI_RELEASE_HOST = "https://ms.viomi.com.cn/"
        private const val VIOMI_DEBUG_HOST = "https://mstest.viomi.com.cn/"
        const val DOMAIN = "domain"
        const val DOMAIN_QR_CODE = "qr_code"
        const val DOMAIN_VIOMI = "viomi"

        val instance: IotApiClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            IotApiClient()
        }
    }
}

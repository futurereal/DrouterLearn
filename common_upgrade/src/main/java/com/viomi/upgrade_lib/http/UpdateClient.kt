package com.viomi.upgrade_lib.http

import com.viomi.upgrade_lib.UpdateConst
import com.viomi.upgrade_lib.utils.VLog
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Http 请求管理
 *
 * @author William
 * @date 2020/8/25
 */
class UpdateClient {
    lateinit var httpApiService: UpdateHttpApi
    lateinit var downloadService: DownloadApi

    init {
        initialize(false)
        initialize(true)
    }

    private fun initialize(isDownload: Boolean) {
        val timeOut = 30 * 1000.toLong()

        val httpLoggingInterceptor = HttpLoggingInterceptor { message: String? ->
            VLog.d(TAG, message ?: "")
        }

        httpLoggingInterceptor.level = if (!isDownload) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
            .readTimeout(timeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
        initRetrofit(okHttpClient, isDownload)
    }

    private fun initRetrofit(okHttpClient: OkHttpClient, isDownload: Boolean) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        if (isDownload) {
            downloadService = retrofit.create(DownloadApi::class.java)
        } else {
            httpApiService = retrofit.create(UpdateHttpApi::class.java)
        }
    }

    companion object {
        private val TAG: String = UpdateClient::class.java.simpleName

        //        private const val baseUrl: String = "https://device-home-test.viomi.com.cn/"
        private var baseUrl: String =
            if (UpdateConst.HTTP_DEBUG) "https://device-home-test.viomi.com.cn/" else "https://device-home.viomi.com.cn/"


        val instance: UpdateClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            UpdateClient()
        }
    }
}
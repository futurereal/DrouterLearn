package com.viomi.upgrade_lib.http


import io.reactivex.Observable
import io.reactivex.SingleSource
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * 下载文件
 *
 * @author William
 * @date 2020/8/25
 */
interface DownloadApi {

    @Streaming
    @GET
    fun download(@Header("Range") range: String, @Url url: String): Observable<ResponseBody>
}
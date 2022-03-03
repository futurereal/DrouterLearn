package com.viomi.viot.https

import com.viomi.viot.https.beans.BaseResponseBean
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/13
 *     desc   : Http API 接口
 *     version: 1.0
 * </pre>
 */
interface VIotService {
    @POST("api/networking/open-api/v1/deviceVerifying")
    fun deviceVerify(@Body requestBody: RequestBody?): Observable<BaseResponseBean<String?>>

    @POST("api/networking/open-api/v1/getMqttConfigAndToken")
    fun getMQTTConfig(@Body requestBody: RequestBody?): Observable<BaseResponseBean<String?>>

    @POST("api/networking/open-api/v1/device/register")
    fun registerDevice(@Body requestBody: RequestBody?): Observable<BaseResponseBean<String?>>
}

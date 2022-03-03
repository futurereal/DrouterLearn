package com.viomi.iotdevice.common.http

import com.viomi.iotdevice.iotmesh.entity.DeviceToWiFiMeshEntity
import com.viomi.iotdevice.iotmesh.entity.WiFiToDeviceMeshEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/11/27
 *     desc   : Http Api
 *     version: 1.0
 * </pre>
 */
internal interface IotApiService {

    @Headers("${IotApiClient.DOMAIN}:${IotApiClient.DOMAIN_QR_CODE}")
    @GET("services/vmall/login/QRCode.json")
    fun createMeshQRCode(@Query("type") type: String?, @Query("clientID") clientId: String?): Observable<DeviceToWiFiMeshEntity>?

    @Headers("${IotApiClient.DOMAIN}:${IotApiClient.DOMAIN_QR_CODE}")
    @POST("services/vmall/login/QRCode.json")
    fun checkMeshStatus(@Body requestBody: RequestBody?): Flowable<DeviceToWiFiMeshEntity>

    @Headers("${IotApiClient.DOMAIN}:${IotApiClient.DOMAIN_VIOMI}")
    @FormUrlEncoded
    @POST("user-web/services/device/login.json")
    fun getMeshInfo(@Field("did") did: Long?, @Query("encryptUserId") encryptUserId: String?): Observable<WiFiToDeviceMeshEntity>?
}

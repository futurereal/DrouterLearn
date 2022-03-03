package com.viomi.viot.device.main

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 *
 * Created by William on 2020/6/12.
 */
interface ApiService {

    @Headers("domain:viot")
    @GET("user-web/services/vmall/login/QRCode.json")
    fun createQRCode(@Query("clientID") clientID: String, @Query("type") type: String): Observable<String>

    @Headers("domain:viot")
    @POST("user-web/services/vmall/login/QRCode.json?")
    fun checkLoginStatus(@Body requestBody: RequestBody?): Flowable<String>

    @Headers("domain:viomi")
    @POST("api/device/open-api/v1/inner/rpc")
    fun openApi(@Body requestBody: RequestBody?): Observable<String>

    @Headers("domain:viomi")
    @GET("api/device/open-api/v1/devConnect")
    fun subDevMeshRequest(@Query("did") did: String): Observable<String>
}

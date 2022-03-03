package com.viomi.upgrade_lib.http

import com.viomi.upgrade_lib.entity.Response
import com.viomi.upgrade_lib.entity.UpdateEntity
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.*

/**
 * Http 接口
 *
 * @author William
 * @date 2020/8/25
 */
interface UpdateHttpApi {

    @POST("api/ota/open-api/v1/getAllUpgradeTypePackageVersions")
    fun checkUpdate(
            @Body body: RequestBody
    ): Observable<Response<UpdateEntity>>

    @POST("api/ota/open-api/v1/uploadDeviceInfo")
    fun uploadDeviceInfo(
            @Body body: RequestBody
    ): Observable<Response<Objects>>

    @POST("api/ota/open-api/v1/reportUpgradeProgress")
    fun reportUpgradeProgress(
            @Body body: RequestBody
    ): Observable<Response<Objects>>

}
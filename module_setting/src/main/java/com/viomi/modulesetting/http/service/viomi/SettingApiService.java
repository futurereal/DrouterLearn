package com.viomi.modulesetting.http.service.viomi;

import com.viomi.modulesetting.entity.AppUpdateResult;
import com.viomi.modulesetting.entity.MobBaseRes;
import com.viomi.modulesetting.entity.QRCodeBase;
import com.viomi.modulesetting.entity.QRCodeLoginCodeBase;
import com.viomi.modulesetting.entity.RequestResult;
import com.viomi.modulesetting.entity.VMAccountEntity;
import com.viomi.modulesetting.http.RetrofitServiceManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.http.service.viomi
 * @ClassName: SettingApiService
 * @Description: Api服务
 * @Author: randysu
 * @CreateDate: 2020/3/18 10:19 AM
 * @UpdateUser:
 * @UpdateDate: 2020/3/18 10:19 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public interface SettingApiService {
    // 检测升级
    @Headers({"domain:" + RetrofitServiceManager.KEY_DOMAIN_UPDATE})
    @GET("/getdata?type=version&&p=1&l=1")
    Observable<AppUpdateResult> checkAppUpdate(@Query("package") String pack, @Query("channel") String channel);

    // 根据ClientID 获取二维码信息 的信息，里面有qrcodeResult ,但是没有用户信息
    // code: 100 desc: 处理成功 result: https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/image/share/86d80608944c4f39a9bc8d4a21fda7cb/T6PSCQfpyBbdiurN0jT.png?GalaxyAccessKeyId=EAKC4WAFZQV4K&Expires=37624511023427&Signature=FQTIXJ9gupdKBXvwUrSr3Fsz/qI=
    @Headers({"domain:" + RetrofitServiceManager.KEY_DOMAIN_VIOMI})
    @GET("/user-web/services/vmall/login/QRCode.json")
    Observable<QRCodeLoginCodeBase> createLoginQRCode(@Query("type") String type, @Query("clientID") String clientId);

    // 扫码登录 根据二维码的clientID 获取用户信息
    @Headers({"domain:" + RetrofitServiceManager.KEY_DOMAIN_VIOMI})
    @POST("/user-web/services/vmall/login/QRCode.json")
    Flowable<QRCodeBase> getLoginUserInfo(@Body RequestBody requestBody);

    // 获取 ssid 黑名单
    @Headers({"domain:" + RetrofitServiceManager.KEY_DOMAIN_DEVICE})
    @GET("/api/base/open-api/v1/network/ssidBlacklist")
    Observable<MobBaseRes<String>> getSSIDBlackList();

    @FormUrlEncoded
    @POST("/information/alias")
    @Headers({"domain:" + RetrofitServiceManager.KEY_DOMAIN_VIOMI})
    Observable<String> reportAlias(@Field("alias") String var);

    // 通过推送回来的 accountId 获取用户信息
    @Headers({"domain:" + RetrofitServiceManager.KEY_DOMAIN_VIOMI})
    @POST("/user-web/services/device/login.json")
    Observable<RequestResult<VMAccountEntity>> getViomiAccountInfo(@Query("did") String did, @Body RequestBody requestBody);
    //上传录像视频
    @POST("https://device-home-test.viomi.com.cn/api/media/open-api/v1/inner/video/upload")
    Observable<MobBaseRes<String>> uploadVideo(@HeaderMap Map<String, String> headerMap, @Body RequestBody body);

    /**
     * 检查电控固件升级
     *
     * @param did did
     * @param pid pid
     * @return observable
     */
    @Headers({"domain:" + RetrofitServiceManager.KEY_DOMAIN_DEVICE})
    @GET("/api/product/open-api/v1/outer/getDeviceMultiFwVersionInfoByDid")
    Observable<MobBaseRes<String>> checkFirmUpdate(@HeaderMap HashMap<String, String> headers, @Query("did") Long did, @Query("pid") Long pid);
}

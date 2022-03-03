package com.viomi.modulesetting.http;

import android.util.Log;

import com.viomi.modulesetting.ModuleSettingConstants;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: ovenSo
 * @Package: com.viomi.settingpagelib.http
 * @ClassName: ChangeUrlIntercept
 * @Description: 改变BaseUrl拦截器
 * @Author: randysu
 * @CreateDate: 2020/4/2 10:31 AM
 * @UpdateUser:
 * @UpdateDate: 2020/4/2 10:31 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class ChangeUrlIntercept implements Interceptor {

    private static final String TAG = ChangeUrlIntercept.class.getName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        List<String> headerValues = request.headers(RetrofitServiceManager.DOMAIN);
        if (headerValues.size() == 0) {
            return chain.proceed(request);
        }
        if (headerValues.size() > 1) {
            throw new IllegalArgumentException("Only one domain in the headers");
        }
        Log.d(TAG, request.header(RetrofitServiceManager.DOMAIN));
        builder.removeHeader(RetrofitServiceManager.DOMAIN);

        // 构造新的Url
        String headerValue = headerValues.get(0);
        HttpUrl newBaseUrl;
        // 旧Url
        HttpUrl oldHttpUrl = request.url();

        if (RetrofitServiceManager.KEY_DOMAIN_VIOMI.equals(headerValue)) {
            if(ModuleSettingConstants.IS_DEBUG_ENV){
                newBaseUrl = HttpUrl.parse(RetrofitServiceManager.VIOMI_DEBUG_HOST);
            } else {
                newBaseUrl = HttpUrl.parse(RetrofitServiceManager.VIOMI_RELEASE_HOST);
            }
        } else if (RetrofitServiceManager.KEY_DOMAIN_UPDATE.equals(headerValue)) {
            newBaseUrl = HttpUrl.parse(RetrofitServiceManager.APP_UPDATE_HOST);
        } else {
            newBaseUrl = oldHttpUrl;
        }
        if (newBaseUrl == null) {
            throw new IllegalArgumentException("New Request Url is null");
        }
        Log.d(TAG, newBaseUrl.url().toString());
        HttpUrl newFullUrl = oldHttpUrl
                .newBuilder()
                .scheme(newBaseUrl.scheme())
                .host(newBaseUrl.host())
                .port(newBaseUrl.port())
                .build();

        return chain.proceed(builder.url(newFullUrl).build());
    }

}

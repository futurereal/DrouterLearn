package com.viomi.modulesetting.http;

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Copyright (C), 2014-2019, 佛山云米科技有限公司
 *
 * @ProjectName: ViomiFaceInWall
 * @Package: com.viomi.viomifaceinwall.http
 * @ClassName: RetrofitServiceManager
 * @Description: Retrofit + okhttp + rxjava
 * @Author: randysu
 * @CreateDate: 2019/3/2 4:57 PM
 * @UpdateUser:
 * @UpdateDate: 2019/3/2 4:57 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class RetrofitServiceManager {
    private static final String TAG = "RetrofitServiceManager";
    private static final int DEFAULT_TIME_OUT = 20;
    private static final int DEFAULT_READ_WRITE_TIME_OUT = 20;

    public final static String VIOMI_RELEASE_HOST = "https://ms.viomi.com.cn";// 云米正式环境
    public final static String VIOMI_DEBUG_HOST = "https://mstest.viomi.com.cn";// 云米测试环境
    public final static String APP_UPDATE_HOST = "https://device-home.viomi.com.cn";// App 检查更新
    // header domain
    public static final String DOMAIN = "domain";
    public static final String KEY_DOMAIN_VIOMI = "viomi";
    public static final String KEY_DOMAIN_UPDATE = "update";
    public static final String KEY_DOMAIN_DEVICE = "device";
    private final Retrofit mRetrofit;

    private RetrofitServiceManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_READ_WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_READ_WRITE_TIME_OUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        ChangeUrlIntercept changeUrlIntercept = new ChangeUrlIntercept();
        builder.addInterceptor(changeUrlIntercept);

        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .baseUrl(APP_UPDATE_HOST)
                .build();
    }

    private static RetrofitServiceManager mInstance;

    public static RetrofitServiceManager getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitServiceManager.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitServiceManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }

    public RequestBody getRequestBody(JSONObject jsonObject) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
    }

}

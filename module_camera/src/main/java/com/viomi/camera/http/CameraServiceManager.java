package com.viomi.camera.http;

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
 * @ClassName: CameraServiceManager
 * @Description: Retrofit + okhttp + rxjava
 * @Author: randysu
 * @CreateDate: 2019/3/2 4:57 PM
 * @UpdateUser:
 * @UpdateDate: 2019/3/2 4:57 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class CameraServiceManager {
    private static final String TAG = "CameraServiceManager";
    private static final int DEFAULT_TIME_OUT = 20;
    private static final int DEFAULT_READ_WRITE_TIME_OUT = 20;
    public final static String VIOMI_VIDEO_HOST_RELEASE = "https://device-home-test.viomi.com.cn";
    public final static String VIOMI_VIDEO_HOST_DEBUG = "https://device-home-test.viomi.com.cn";
    public static final String PATH_VIDEO = "/api/media/open-api/v1/inner/video/";
    private final Retrofit mRetrofit;

    private CameraServiceManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_READ_WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_READ_WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .baseUrl(VIOMI_VIDEO_HOST_DEBUG)
                .build();
    }

    private static CameraServiceManager mInstance;

    public static CameraServiceManager getInstance() {
        if (mInstance == null) {
            synchronized (CameraServiceManager.class) {
                if (mInstance == null) {
                    mInstance = new CameraServiceManager();
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

    public void cancelRequest() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
    }

}

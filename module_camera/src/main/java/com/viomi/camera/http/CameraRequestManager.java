package com.viomi.camera.http;

import android.util.Log;

import com.viomi.camera.CameraRxBusEvent;
import com.viomi.camera.callback.ClearAllVideoCallback;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @description: 相机模块 网络请求的管理类
 */
public class CameraRequestManager {
    private static final String TAG = "CameraRequestManager";
    public static final String BIZ_TYPE_VIDEO = "3";
    public static final String BIZ_TYPE_COVER = "4";
    public static final String PLATFORM_ANDROID = "1";

    public static final String MEDIA_TYPE = "multipart/form-data";

    public static final String KEY_ACCESS_KEY = "accessToken";
    public static final String KEY_BIZ_TYPE = "bizType";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DID = "did";
    public static final String KEY_FILE = "file";
    public static final String KEY_PLATFORM = "platform";
    public static final String KEY_UPLOAD_KEY = "uploadKey";
    // 上传封面 需要  传递 上传视频的VideoId。 上传视频不需要
    public static final String KEY_VIDEO_ID = "videoId";

    public static final String DISCRIPTION_KEY_NAME = "recipeName";
    public static final String DISCRIPTION_KEY_MODEID = "modeId";
    public static final String DISCRIPTION_KEY_RECORD_TIME = "recordTime";

    // 上传成功的
    public static final int REQUEST_SUCCESS_CODE = 100;

    private static volatile CameraRequestManager instance;
    private final CameraApi cameraApi;
    private final String deviceId;
    // 订阅
    private Disposable uploadVideoDisposable;

    private boolean isUpdateProgress = false;

    public static CameraRequestManager getInstance() {
        if (instance == null) {
            synchronized (CameraRequestManager.class) {
                if (instance == null) {
                    instance = new CameraRequestManager();
                }
            }
        }
        return instance;
    }

    private CameraRequestManager() {
        this.cameraApi = CameraServiceManager.getInstance().create(CameraApi.class);
        this.deviceId = ViomiProvideUtil.getDeviceId();
    }

    /**
     * 查询视频列表的url  https://admin-home-test.viomi.com.cn/api/viot-media-server-swagger/media/open-api/v1/inner/video/getAllByUserIdAndDid?accessToken=hAZC26flIa1VhlY5&bizType=3&did=1000106657
     *
     * @param videoInfo
     */
    public void uploadVideo(VideoInfo videoInfo) {
        File videoFile = new File(videoInfo.getVideoFilePath());
        Log.i(TAG, "videoFile path: " + videoFile.getPath());
        String desciption = "";
        try {
            JSONObject descriptionJsonObj = new JSONObject();
            descriptionJsonObj.put(DISCRIPTION_KEY_MODEID, videoInfo.getModeId());
            descriptionJsonObj.put(DISCRIPTION_KEY_RECORD_TIME, String.valueOf(videoInfo.getRecordTime()));
            descriptionJsonObj.put(DISCRIPTION_KEY_NAME, videoInfo.getRecipeName());
            desciption = descriptionJsonObj.toString();
        } catch (JSONException e) {
            Log.e(TAG, "uploadVideo: error headmap  jsonException");
        }
        Log.i(TAG, "uploadVideo: description: " + desciption);
        MultipartBody.Builder videoBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(KEY_BIZ_TYPE, BIZ_TYPE_VIDEO)
                .addFormDataPart(KEY_DID, deviceId)
                .addFormDataPart(KEY_DESCRIPTION, desciption)
                .addFormDataPart(KEY_ACCESS_KEY, ViomiProvideUtil.getCameraAccessKey())
                .addFormDataPart(KEY_PLATFORM, PLATFORM_ANDROID)
                //需要客户端生成一个唯一id上传 客户端随机生成
                .addFormDataPart(KEY_UPLOAD_KEY, deviceId + "_" + System.currentTimeMillis());
        videoBuilder.addFormDataPart(KEY_FILE, videoFile.getName(), RequestBody.create(MediaType.parse(MEDIA_TYPE), videoFile));
        MultipartBody videoRequestBody = videoBuilder.build();
        Map<String, String> headerMap = getReqeustHeaderMap();
        isUpdateProgress = false;
        RequestBody progressReqeustBody = ProgressHelper.withProgress(videoRequestBody, new VideoProgressCallBack());
        // 上报会出现 CallAdapterExcetion ,需要检查 RxJava 和 Retrofit 的兼容情况
        cameraApi.upLoadVideoOrCover(headerMap, progressReqeustBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .subscribe(new Observer<MobBaseRes<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        Log.i(TAG, "onSubscribe: uploadVideo: ");
                        uploadVideoDisposable = disposable;
                        ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_PREPARE);
                    }

                    @Override
                    public void onNext(@NonNull MobBaseRes<String> result) {
                        Log.i(TAG, "onNext: result: " + result.getCode());
                        if ((result.getCode() == REQUEST_SUCCESS_CODE)) {
                            String videoId = result.getResult();
                            Log.i(TAG, "upload video succeed : " + videoId);
                            ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_VIDEO_SUCCESS, videoId);
                        } else {
                            Log.i(TAG, " upload video failure : " + result);
                            ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_VIDEO_FAIL);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        String msg = throwable.getMessage();
                        Log.i(TAG, "onError: " + msg);
                        ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_VIDEO_FAIL);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


    /**
     * 上传视频封面
     */
    public void uploadCover(String videoId, String coverFilePath) {
        Log.i(TAG, " uploadCover： coverFileName = " + coverFilePath);
        File coverFile = new File(coverFilePath);
        MultipartBody.Builder picBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(KEY_BIZ_TYPE, BIZ_TYPE_COVER)
                .addFormDataPart(KEY_DID, deviceId)
                .addFormDataPart(KEY_ACCESS_KEY, ViomiProvideUtil.getCameraAccessKey())
                .addFormDataPart(KEY_PLATFORM, PLATFORM_ANDROID)
                .addFormDataPart(KEY_UPLOAD_KEY, deviceId + "_" + System.currentTimeMillis())
                .addFormDataPart(KEY_VIDEO_ID, videoId);
        picBuilder.addFormDataPart(KEY_FILE, coverFile.getName(), RequestBody.create(MediaType.parse(MEDIA_TYPE), coverFile));
        RequestBody coverRequestBody = picBuilder.build();
        isUpdateProgress = false;
        RequestBody progressReqeustBody = ProgressHelper.withProgress(coverRequestBody, new VideoProgressCallBack());
        Map<String, String> headerMap = getReqeustHeaderMap();
        cameraApi.upLoadVideoOrCover(headerMap, progressReqeustBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .subscribe(new Observer<MobBaseRes<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        Log.i(TAG, "onSubscribe: uploadCover");
                        uploadVideoDisposable = disposable;
                        ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_PREPARE);
                    }

                    @Override
                    public void onNext(@NonNull MobBaseRes<String> result) {
                        Log.i(TAG, "onNext:  uploadCover result: " + result.getCode());
                        if ((result.getCode() == REQUEST_SUCCESS_CODE)) {
                            Log.i(TAG, " uploadCover succeed : " + result.getDesc());
                            ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_COVER_SUCCESS);
                        } else {
                            Log.i(TAG, " uploadCover failure : " + result.getDesc());
                            ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_COVER_FAIL);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        String msg = throwable.getMessage();
                        Log.i(TAG, "uploadCover onError: " + msg);
                        ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_VIDEO_FAIL);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "uploadCover onComplete: ");
                    }
                });

    }

    /**
     * 删除所有视频
     **/
    public void deleteAllVideo(ClearAllVideoCallback callback) {
        Log.i(TAG, "deleteAllVideo:");
        MultipartBody.Builder delBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(KEY_ACCESS_KEY, ViomiProvideUtil.getCameraAccessKey())
                .addFormDataPart(KEY_BIZ_TYPE, BIZ_TYPE_VIDEO)
                .addFormDataPart(KEY_DID, deviceId);
        RequestBody delRequestBody = delBuilder.build();
        cameraApi.deleteAllVideo(getReqeustHeaderMap(), delRequestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .subscribe(result -> {
                    if ((result.getCode() == REQUEST_SUCCESS_CODE)) {
                        Log.i(TAG, "deleteAllVideo succeed : " + result.getDesc());
                        if (callback != null) {
                            callback.onClearResult(true);
                        }
                    } else {
                        Log.i(TAG, "deleteAllVideo failure : " + result.getDesc());
                        if (callback != null) {
                            callback.onClearResult(false);
                        }
                    }
                }, throwable -> {
                    String msg = throwable.getMessage() == null ? "Unknown" : throwable.getMessage();
                    Log.e(TAG, "deleteAllVideo throwable : " + msg);
                    if (callback != null) {
                        callback.onClearResult(false);
                    }
                });
    }


    /**
     * 取消上传
     */
    public void cancelUpload() {
        if (uploadVideoDisposable != null) {
            uploadVideoDisposable.dispose();
        }
    }

    private Map<String, String> getReqeustHeaderMap() {
        Log.i(TAG, "getReqeustHeaderMap: ");
        String headKeyAccess = "VIOMI-Access-Key";
        String headKeyNoise = "VIOMI-Noise";
        String headKeyTimestamp = "VIOMI-Timestamp";
        String headKeySignature = "VIOMI-Signature";
        Map<String, String> headMap = new HashMap<>();
        String accessKeyValue = ViomiProvideUtil.getCameraAccessKey();
        headMap.put(headKeyAccess, accessKeyValue);
        String uuid = UUID.randomUUID().toString();
        headMap.put(headKeyNoise, uuid);
        String time = String.valueOf(System.currentTimeMillis());
        headMap.put(headKeyTimestamp, time);
        //签名
        List<String> order = new ArrayList<>();
        order.add(headKeyAccess);
        order.add(headKeyNoise);
        order.add(headKeyTimestamp);
        String sign = SignUtil.sign(SignUtil.METHOD, headMap, SignUtil.APP_SECRET, order);
        headMap.put(headKeySignature, sign);
        Log.i(TAG, "headerMap add sign:" + headMap);
        return headMap;
    }

    class VideoProgressCallBack implements ProgressCallback {
        @Override
        public void onProgressChanged(long numBytes, long totalBytes, int progress) {
            Log.i(TAG, "onProgressChanged: percent: " + progress + "   : " + isUpdateProgress);
            if (progress == 100 && !isUpdateProgress) {
                isUpdateProgress = true;
                return;
            }
            // 可以不用线程切换
            if (isUpdateProgress) {
                ViomiRxBus.getInstance().post(CameraRxBusEvent.MSG_UPLOAD_PROGRESS, progress);
            }

        }
    }
}

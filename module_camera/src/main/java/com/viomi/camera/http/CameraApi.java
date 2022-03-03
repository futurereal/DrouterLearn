package com.viomi.camera.http;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface CameraApi {
    // 上传录像视频/media/open-api/v1/inner/video/upload
    @POST(CameraServiceManager.PATH_VIDEO + "upload")
    Observable<MobBaseRes<String>> upLoadVideoOrCover(@HeaderMap Map<String, String> headerMap, @Body RequestBody body);
    // 删除所有视频/media/open-api/v1/inner/video/deleleAllByUserIdAndDid
    @POST(CameraServiceManager.PATH_VIDEO + "deleleAllByUserIdAndDid")
    Observable<MobBaseRes<String>> deleteAllVideo(@HeaderMap Map<String, String> headerMap, @Body RequestBody body);
}

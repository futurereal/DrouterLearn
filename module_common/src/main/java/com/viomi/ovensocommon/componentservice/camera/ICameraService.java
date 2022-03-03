package com.viomi.ovensocommon.componentservice.camera;


import com.viomi.ovensocommon.db.VideoInfo;

import java.util.List;

public interface ICameraService {
    boolean isCameraRecording();

    boolean isServiceRunning();


    /**
     * 1 删除本地文件
     * 2 操作sp 里面的videoinfo , 把对应的删掉，把存的视频文件删掉
     * 3 回调结构
     *
     * @param videoIndex VideoInfo的videoIndex
     */
    void deleteLocalVideo(int videoIndex);

    /**
     * 删除 所有的视频
     * 1 、调用服务器的接口删除
     * 2 、遍历sp 里面所有的videoinfo， 把文件删除
     * 3 、 把sp 的userInfo 清空
     */
    void deleteAllVideo();

    /**
     * 获取缓存中的所有video
     **/
    List<VideoInfo> getAllVideoInfo();

    /**
     * 编辑和展示编辑dialog
     *
     * @param videoInfo
     */
    void showEditVideoFragment(VideoInfo videoInfo);

    /**
     * 上传缓存的视频
     * 1 、弹出上传百分比的弹框，调用接口上传
     * 2 、上传成功 之后，操作sp 删除sp 保存的内容 , 存的视频文件删掉
     * 3 、回调结果
     *
     * @param videoInfo videoInfo
     */
    void showUploadVideoFragement(VideoInfo videoInfo);

    void dealRecordAction(int recordActionValue, String recipeName);

    void updateRecordStatus(int recordStateRecording);
}
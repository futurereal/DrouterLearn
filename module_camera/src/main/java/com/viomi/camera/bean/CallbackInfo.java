package com.viomi.camera.bean;

import com.viomi.ovensocommon.db.VideoInfo;

/**
 * @author admin
 * @date 2021/09/16
 */
public class CallbackInfo {
    private String errorMsg;
    private VideoInfo videoInfo;
    private boolean isSuccess;

    public CallbackInfo() {

    }

    public CallbackInfo(VideoInfo videoInfo, boolean isSuccess, String errorMsg) {
        this.errorMsg = errorMsg;
        this.isSuccess = isSuccess;
        this.videoInfo = videoInfo;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @Override
    public String toString() {
        return "CallbackInfo{" +
                "errorMsg='" + errorMsg + '\'' +
                ", videoInfo=" + videoInfo +
                ", isSuccess=" + isSuccess +
                '}';
    }
}

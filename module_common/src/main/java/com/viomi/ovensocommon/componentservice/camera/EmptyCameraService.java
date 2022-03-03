package com.viomi.ovensocommon.componentservice.camera;

import com.viomi.ovensocommon.db.VideoInfo;

import java.util.List;

public class EmptyCameraService implements ICameraService {


    @Override
    public boolean isCameraRecording() {
        return false;
    }

    @Override
    public boolean isServiceRunning() {
        return false;
    }


    @Override
    public void dealRecordAction(int recordActionValue, String recipeName) {

    }

    @Override
    public void updateRecordStatus(int recordStateRecording) {

    }

    @Override
    public void deleteLocalVideo(int videoIndex) {

    }

    @Override
    public void deleteAllVideo() {

    }

    @Override
    public List<VideoInfo> getAllVideoInfo() {
        return null;
    }

    @Override
    public void showEditVideoFragment(VideoInfo videoInfo) {

    }

    @Override
    public void showUploadVideoFragement(VideoInfo videoInfo) {

    }


}

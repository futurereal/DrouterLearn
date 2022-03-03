package com.viomi.camera;

import android.util.Log;

import com.blankj.utilcode.util.ServiceUtils;
import com.viomi.camera.fragment.CameraFragmentManager;
import com.viomi.camera.http.CameraRequestManager;
import com.viomi.camera.utils.CameraUtils;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.camera.ICameraService;
import com.viomi.ovensocommon.db.VideoInfo;

import java.util.List;

/**
 * 相机模块服务
 */
public class CameraModuleService implements ICameraService {
    private static final String TAG = "CameraModuleService";

    @Override
    public boolean isCameraRecording() {
        return false;
    }

    @Override
    public boolean isServiceRunning() {
        return false;
    }

    @Override
    public void deleteLocalVideo(int videoIndex) {
//        int deleteResult = ViomiRoomDatabase.getDatabase().messageInfoDao().deleteVideoInfoByIndex(videoIndex);
//        Log.i(TAG, "deleteLocalVideo: deleteResult: " + deleteResult);
//        boolean dealResult = deleteResult > 0 ? true : false;
    }

    @Override
    public void deleteAllVideo() {
//        ViomiRoomDatabase.getDatabase().messageInfoDao().deleteAllVideoInfo();
        CameraRequestManager.getInstance().deleteAllVideo(result -> Log.d(TAG, "onClearResult: " + result));
    }

    @Override
    public List<VideoInfo> getAllVideoInfo() {
//        List<VideoInfo> videoInfoList = ViomiRoomDatabase.getDatabase().messageInfoDao().loadAllVideoInfo();
//        Log.i(TAG, "getAllVideo: " + videoInfoList.size());
        return null;
    }

    @Override
    public void dealRecordAction(int recordActionValue, String recipeName) {
        Log.i(TAG, "dealRecordAction: " + recordActionValue + " recipeName: " + recipeName);
        //开始    暂停   结束
        // 分清楚什么时候启动Activity， 什么时候只是操作服务
        CameraUtils.dealRecordAction(true, recordActionValue, recipeName);
    }

    @Override
    public void updateRecordStatus(int recordStateRecording) {
        Log.i(TAG, "updateRecordStatus: " + recordStateRecording);
        // 判断服务是否启动
        boolean isServiceRunning = ServiceUtils.isServiceRunning(CameraService.class.getName());
        if (!isServiceRunning) {
            Log.i(TAG, "updateRecordStatus: isService is running false return ");
            return;
        }
        if (recordStateRecording == CommonConstant.RECORD_STATE_RECORDING) {
            CameraUtils.dealRecordAction(CameraService.ACTION_VIDEO_RESTART);
        }
        if (recordStateRecording == CommonConstant.RECORD_STATE_PAUSE) {
            CameraUtils.dealRecordAction(CameraService.ACTION_VIDEO_PAUSE);
        }
        if (recordStateRecording == CommonConstant.RECORD_STATE_FINISH) {
            CameraUtils.dealRecordAction(CameraService.ACTION_VIDEO_STOP);
        }

    }

    @Override
    public void showEditVideoFragment(VideoInfo videoInfo) {
        CameraFragmentManager.getInstance().showVideoEditFragemnt(videoInfo);
    }

    @Override
    public void showUploadVideoFragement(VideoInfo videoInfo) {
        Log.i(TAG, "showUploadVideoFragement: videoInfo: " + videoInfo);
        CameraFragmentManager.getInstance().showVideoUploadFragemnt(videoInfo);
    }
}

package com.viomi.camera.utils;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.blankj.utilcode.util.ServiceUtils;
import com.viomi.camera.CameraService;
import com.viomi.common.ApplicationUtils;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.router.core.ViomiRouter;

import java.io.IOException;

public class CameraUtils {
    private static final String TAG = "CameraUtils";

    public static void dealRecordAction(boolean isLauncherActivity, int recordActionValue, String recipeName) {
        Log.i(TAG, "dealRecordAction: " + recordActionValue + "  recipeName: " + recipeName);
        // 启动界面
        if (isLauncherActivity) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENS0_CAMERA)
                    .withString(ViomiRouterConstant.CAMERA_KEY_RECIPENAME, recipeName).navigation();
        }
    }

    public static void dealRecordAction(String recodeAction) {
        Log.i(TAG, "recodeAction: " + recodeAction);
        Intent cameraServiceIntent = new Intent();
        cameraServiceIntent.setPackage(ApplicationUtils.getContext().getPackageName());
        cameraServiceIntent.setAction(recodeAction);
        ApplicationUtils.getContext().startService(cameraServiceIntent);
    }

    public static void startOrIncreaseService(String recipeName, String modeId, boolean isCooking) {
        boolean isServiceRunning = ServiceUtils.isServiceRunning(CameraService.class.getName());
        Log.i(TAG, "startCameraService: isServiceRunning : " + isServiceRunning);
        Intent cameraServiceIntent = new Intent();
        cameraServiceIntent.setPackage(ApplicationUtils.getContext().getPackageName());
        if (isServiceRunning) {
            cameraServiceIntent.setAction(CameraService.ACTION_PREVIEW_INCREASE);
        } else {
            cameraServiceIntent.putExtra(ViomiRouterConstant.CAMERA_KEY_COOKING, isCooking);
            cameraServiceIntent.putExtra(ViomiRouterConstant.CAMERA_KEY_RECIPENAME, recipeName);
            cameraServiceIntent.putExtra(ViomiRouterConstant.CAMERA_KEY_MODEID, modeId);
            cameraServiceIntent.setAction(CameraService.ACTION_PREVIEW);
        }
        ApplicationUtils.getContext().startService(cameraServiceIntent);
    }

    public static int getVideoTime(String videoFilePath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        int duration = 0;
        try {
            mediaPlayer.setDataSource(videoFilePath);
            mediaPlayer.prepare();
            long durationLong = mediaPlayer.getDuration();
            duration = (int) (durationLong / 1000);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "getVideoTime: " + e.getMessage());
        } finally {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        Log.i(TAG, "getVideoTime: duration: " + duration);
        return duration;
    }


}

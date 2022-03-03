package com.viomi.camera.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.ovensocommon.db.VideoInfo;


/**
 * @author lixinqi
 * @date 2021/09/17
 * dialog管理
 */
public class CameraFragmentManager {
    private final static String TAG = CameraFragmentManager.class.getSimpleName();
    private static CameraFragmentManager instance;

    private CameraFragmentManager() {

    }

    public static CameraFragmentManager getInstance() {
        if (instance == null) {
            synchronized (CameraFragmentManager.class) {
                if (instance == null) {
                    instance = new CameraFragmentManager();
                }
            }
        }
        return instance;
    }

    public void showVideoEditFragemnt(VideoInfo videoInfo) {
        FragmentActivity mActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        Log.i(TAG, "showVideoEditFragemnt: topActivity: " + mActivity.getLocalClassName());
        VideoEditFragment videoEditFragment = new VideoEditFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(VideoEditFragment.KEY_VIDEO_INFO, videoInfo);
        videoEditFragment.setArguments(bundle);
        videoEditFragment.show(mActivity.getSupportFragmentManager(), VideoEditFragment.class.getSimpleName());
    }

    public void showVideoUploadFragemnt(VideoInfo videoInfo) {
        Log.d(TAG, "showUploadDialog: " + videoInfo);
        FragmentActivity mActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        VideoUpLoadFragment videoUpLoadFragment = new VideoUpLoadFragment(videoInfo);
        videoUpLoadFragment.setCancelable(false);
        videoUpLoadFragment.show(mActivity.getSupportFragmentManager(), VideoUpLoadFragment.class.getSimpleName());
    }
}

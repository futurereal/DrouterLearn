package com.viomi.camera.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ServiceUtils;
import com.viomi.camera.CameraService;
import com.viomi.camera.utils.CameraUtils;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.router.annotation.Route;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @description:
 * @data:2021/12/14
 */
@Route(path = ViomiRouterConstant.CAMERA_FRAGMENT)
public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private String recipeName;
    private String modeId;
    private boolean isCooking;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        Bundle bundler = getArguments();
        Log.i(TAG, "onCreate: bundler: " + bundler);
        recipeName = bundler.getString(ViomiRouterConstant.CAMERA_KEY_RECIPENAME);
        modeId = bundler.getString(ViomiRouterConstant.CAMERA_KEY_MODEID);
        isCooking = bundler.getBoolean(ViomiRouterConstant.CAMERA_KEY_COOKING, false);
        Log.i(TAG, "initData: recipeName: " + recipeName + " modeId: " + modeId + " isCooking: " + isCooking);
        initListener();
    }

    private void initListener() {
        Log.i(TAG, "initListener: ");
        mCompositeDisposable = new CompositeDisposable();
        ViomiRxBus.getInstance().subscribe(viomiRxBusEvent -> {
            if (viomiRxBusEvent.getMsgId() == CommonConstant.MSG_STOP_CAMERA) {
                Log.i(TAG, "initListener: stopCamera");
                // 停止录像
                Intent cameraIntent = new Intent();
                cameraIntent.setPackage(getContext().getPackageName());
                cameraIntent.setAction(CameraService.ACTION_VIDEO_STOP);
                getContext().startService(cameraIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: dealCameraService  ");
        CameraUtils.startOrIncreaseService(recipeName, modeId, isCooking);
    }

    @Override
    public void onPause() {
        super.onPause();
        boolean isServiceRunning = ServiceUtils.isServiceRunning(CameraService.class.getName());
        Log.i(TAG, "onPause: isServiceRunning: " + isServiceRunning);
        if (isServiceRunning == false) {
            getActivity().finish();
            return;
        }
        Intent cameraIntent = new Intent();
        cameraIntent.setPackage(getContext().getPackageName());
        Log.i(TAG, "onPause: isCooking:" + isCooking);
        // 如果没有开始烹饪，退出服务，开始烹饪了，设置窗口为最小值
        if (isCooking) {
            cameraIntent.setAction(CameraService.ACTION_PREVIEW_REDUCE);
            getContext().startService(cameraIntent);
        } else {
            cameraIntent.setAction(CameraService.ACTION_VIDEO_STOP);
            getContext().startService(cameraIntent);
        }

    }
}

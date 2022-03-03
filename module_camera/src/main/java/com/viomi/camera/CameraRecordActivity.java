package com.viomi.camera;

import android.content.Intent;
import android.util.Log;

import com.viomi.camera.databinding.ActivityCameraRecordBinding;
import com.viomi.camera.utils.CameraUtils;
import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.FragmentUtils;
import com.viomi.router.annotation.Route;

@Route(path = ViomiRouterConstant.CAMERA_RECORD)
public class CameraRecordActivity extends BaseActivity<ActivityCameraRecordBinding> {
    private static final String TAG = "CameraActivity";
    private String recipeName = "";
    private String modeId = "";
    private boolean isCooking;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_camera_record;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        recipeName = getIntent().getStringExtra(ViomiRouterConstant.CAMERA_KEY_RECIPENAME);
        modeId = getIntent().getStringExtra(ViomiRouterConstant.CAMERA_KEY_MODEID);
        isCooking = getIntent().getBooleanExtra(ViomiRouterConstant.CAMERA_KEY_COOKING, false);
        Log.i(TAG, "initData: recipeName: " + recipeName + " modeId: " + modeId + " isCooking:  " + isCooking);
        FragmentUtils.loadCameraFragment(R.id.camerarecord_container, recipeName, modeId, isCooking);
    }

    @Override
    protected void initListener() {
        addDisposable(ViomiRxBus.getInstance().subscribe(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Log.i(TAG, "initListener : " + msgId);
           /* if (msgId == CommonConstant.MSG_FINISH_CAMERA_ACTIVITY) {
                ViomiRxBus.getInstance().post(CommonConstant.MSG_SHOW_EDITE_FRAGMENT);
                isCooking = false;
                finish();
            }*/
        }));
        viewDataBinding.camerarecordBack.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent: ");
        // 停止录像
        Intent cameraIntent = new Intent();
        cameraIntent.setPackage(getPackageName());
        cameraIntent.setAction(CameraService.ACTION_VIDEO_STOP);
        startService(cameraIntent);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: " + isCooking);
        super.onResume();
        CameraUtils.startOrIncreaseService(recipeName, modeId, isCooking);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent cameraIntent = new Intent();
        cameraIntent.setPackage(getPackageName());
        // 如果没有开始烹饪，退出服务，开始烹饪了，设置窗口为最小值
        Log.i(TAG, "onPause: " + isCooking);
        if (isCooking) {
            cameraIntent.setAction(CameraService.ACTION_PREVIEW_REDUCE);
            startService(cameraIntent);
        }

    }
}

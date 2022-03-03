package com.viomi.ovenso.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityOvensoCameraBinding;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.FragmentUtils;
import com.viomi.router.annotation.Route;

/**
 * @description:
 * @data:2021/12/14
 */
@Route(path = ViomiRouterConstant.OVENS0_CAMERA)
public class OvenSoCameraActivity extends BaseTitleActivity<ActivityOvensoCameraBinding> {
    private static final String TAG = "OvenSoCameraActivity";
    private String recipeName = "";
    private String modeId = "";
    private boolean isCooking = false;

    @Override
    protected int getChildContentViewId() {
        return R.layout.activity_ovenso_camera;
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        Log.i(TAG, "initIntentData: ");
        recipeName = getIntent().getStringExtra(ViomiRouterConstant.CAMERA_KEY_RECIPENAME);
        modeId = getIntent().getStringExtra(ViomiRouterConstant.CAMERA_KEY_MODEID);
        isCooking = getIntent().getBooleanExtra(ViomiRouterConstant.CAMERA_KEY_COOKING, false);
        Log.i(TAG, "initIntentData: recipeName: " + recipeName + " modeId: " + modeId);
        OvenUtil.setRecordingStatusToMcu(CommonConstant.RECORD_STATE_RECORDING);
    }
    
    @Override
    protected void initChildUi() {
        FragmentUtils.loadCameraFragment(R.id.camera_container, recipeName, modeId, isCooking);
    }

    @Override
    protected String getTitleName() {
        String cameraTitle = getString(R.string.camera_title);
        Log.i(TAG, "getTitleName: " + cameraTitle);
        return cameraTitle;
    }

    @Override
    protected void initListener() {
        addDisposable(ViomiRxBus.getInstance().subscribe(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Log.i(TAG, "initListener : " + msgId);
           /* if (msgId == CommonConstant.MSG_FINISH_CAMERA_ACTIVITY) {
                isCooking = false;
                this.finish();
            }*/
        }));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent: ");
        ViomiRxBus.getInstance().post(CommonConstant.MSG_STOP_CAMERA);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (TextUtils.isEmpty(recipeName)) {
            OvenUtil.setRecordingStatusToMcu(CommonConstant.RECORD_STATE_FINISH);
        }
    }

}

package com.viomi.ovenso.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityScreenOffBinding;
import com.viomi.ovenso.view.CircleProgressView;
import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.router.annotation.Route;

/**
 * @author admin
 * @date 2022/01/12
 * @describe 锁屏
 */
@Route(path = ViomiRouterConstant.OVENS0_SCREEN_OFF)
public class ScreenOffActivity extends BaseActivity<ActivityScreenOffBinding> {
    private static final String TAG = "ScreenOffActivity";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_screen_off;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        super.initListener();
        viewDataBinding.screenoffProgress.setListener(new CircleProgressView.OnProgressUpdateListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "onStart: ");
            }

            @Override
            public void onProgressUpdate(int currentProgress) {
                Log.d(TAG, "onProgressUpdate: " + currentProgress);
            }

            @Override
            public void onEnd() {
                Log.d(TAG, "onEnd: ");
                finish();
            }
        });
    }
}
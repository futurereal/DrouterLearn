package com.viomi.ovensocommon;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @description: Activity 的基类
 **/
public abstract class BaseActivity<V extends ViewDataBinding> extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private long lastClickTime;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private int currentViewId;
    protected V viewDataBinding;
    protected boolean isActivityResumed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        performDataBinding();
        initView();
        initData();
        initListener();
    }

    protected void initListener() {
        Log.i(TAG, "initListener: ");
    }


    /**
     * 执行数据绑定
     */
    private void performDataBinding() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewDataBinding = DataBindingUtil.setContentView(this, getContentViewId());
        setContentView(viewDataBinding.getRoot());
    }

    @Override
    protected void onResume() {
        isActivityResumed = true;
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "onWindowFocusChanged: hasFocus: " + hasFocus);
    }

    protected void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onStop() {
        isActivityResumed = false;
        super.onStop();
    }

    protected abstract int getContentViewId();

    protected abstract void initView();

    protected abstract void initData();

    /**
     * 两次点击间隔不能少于 1000 毫秒（防止重复点击）
     */
    protected boolean isRepeatedClick(int viewId) {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        // 重复点击间隔
        int MIN_CLICK_DELAY_TIME = CommonConstant.MIN_CLICK_TIME_MS;
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME || currentViewId != viewId) {
            flag = false;
        }
        lastClickTime = curClickTime;
        currentViewId = viewId;
        Log.i(TAG, "isRepeatedClick: " + flag);
        return flag;
    }

    protected void keepScreenOn() {
        Log.i(TAG, "keepScreenOn: ");
        // 屏幕不息屏 不弹出键盘
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
    }
}


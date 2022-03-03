package com.viomi.modulesetting.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.viomi.modulesetting.entity.LayoutStyleEnum;

/**
 * @description: 设置界面的基类
 * @data:2021/7/13
 */
public abstract class BaseCommonSettingActivity<V extends ViewDataBinding> extends AppCompatActivity {
    private static final String TAG = "BaseCommonSettingActivi";
    protected LayoutStyleEnum mLayoutStyle;
    protected V viewDataBinding;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performDataBinding();
        initView();
        initData();
        initListener();
    }

    /**
     * 执行数据绑定
     */
    private void performDataBinding() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        viewDataBinding = DataBindingUtil.setContentView(this, onBindLayout());
        setContentView(viewDataBinding.getRoot());
    }

    protected int onBindLayout() {
        return 0;
    }

    protected void initData() {
    }

    protected void initListener() {
    }


    protected void initView() {
    }

    protected void registerModuleEvent() {
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected Activity getActivity() {
        return this;
    }


}

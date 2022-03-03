package com.viomi.ovensocommon;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

/**
 * @description: Presenter + Activity 的基类
 * @data:2022/1/12
 */
public abstract class BasePresenterActivity<V extends ViewDataBinding, P extends BasePresenter> extends BaseActivity<V> implements BaseView {
    private static final String TAG = "BasePresenterActivity";
    protected P basePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        basePresenter = initPresenter();
        basePresenter.onAttch(this);
        // 初始化presenter
        basePresenter.onCreate();
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    /**
     * 为了在内存不足的情况下恢复数据
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        basePresenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.onDestroy();
        basePresenter.onDetach();
    }

    protected abstract P initPresenter();
}

package com.viomi.ovenso.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovensocommon.BasePresenter;
import com.viomi.ovensocommon.BaseView;

/**
 * @description:
 * @data:2022/1/12
 */
public abstract class OvenBasePresenterActivity<V extends ViewDataBinding, P extends BasePresenter> extends BaseTitleActivity<V> implements BaseView {
    private static final String TAG = "OvenBasePresenterActivity";
    protected P basePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        // 需要放在super 之前，否则 会有空指针
        basePresenter = initPresenter();
        basePresenter.onAttch(this);
        // 初始化presenter
        basePresenter.onCreate();
        super.onCreate(savedInstanceState);
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

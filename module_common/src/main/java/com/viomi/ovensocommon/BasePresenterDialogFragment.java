package com.viomi.ovensocommon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

/**
 * @description: Presenter + Fragment 处理的基类
 * @data:2022/1/12
 */
public abstract class BasePresenterDialogFragment<V extends ViewDataBinding, P extends BasePresenter> extends BaseDialogFragment<V> implements BaseView {
    public static final String KEY_BUNDLE = "bundle";
    private static final String TAG = "BasePresenterDialogFragment";
    protected P basePresenter;
    protected Context mContext;
    private Bundle mBundle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        // 需要放在 super 之前
        if (savedInstanceState != null) {
            mBundle = savedInstanceState.getBundle("KEY_BUNDLE");
        } else {
            mBundle = getArguments() == null ? new Bundle() : getArguments();
        }
        basePresenter = initPresenter();
        basePresenter.onAttch(this);
        basePresenter.onCreate();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
        basePresenter.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        basePresenter.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        if (mBundle != null) {
            outState.putBundle(KEY_BUNDLE, mBundle);
        }
    }

    protected abstract P initPresenter();
}

package com.viomi.ovensocommon;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.ScreenUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 对话框基类
 */
public abstract class BaseDialogFragment<V extends ViewDataBinding> extends DialogFragment {
    private static final String TAG = "BaseSettingDialogFragme";
    // 弹框尽量使用宽高比
    protected int landHeight = (int) (ScreenUtils.getScreenHeight() * 0.833f);
    protected int landWidth = (int) (ScreenUtils.getScreenWidth() * 0.547f);

    protected int portWidth = 640;
    protected int portHeight = 384;

    protected CompositeDisposable dialogDisposable = new CompositeDisposable();
    DialogInterface.OnDismissListener onDismissListener;
    protected OnButtonsClickListener listener;
    protected V viewDataBinding;
    protected Window window;
    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        contentView = super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, " onCreateView");
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        }
        if (contentView == null) {
            contentView = viewDataBinding.getRoot();
        }
        return contentView;
    }

    protected void addDispose(Disposable disposable) {
        dialogDisposable.add(disposable);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "setDailogSize: ");
        Dialog dialog = getDialog();
        if (dialog == null) {
            Log.i(TAG, "onStart: dialog isf null return ");
            return;
        }
        window = dialog.getWindow();
        if (window == null) {
            Log.i(TAG, "onStart: window is null return  ");
            return;
        }
        WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        // 横屏 和竖屏的界面
        Log.i(TAG, "setDailogSize: " + ScreenUtils.isLandscape());
        if (ScreenUtils.isLandscape()) {
            windowLayoutParams.width = landWidth;
            windowLayoutParams.height = landHeight;
            windowLayoutParams.gravity = Gravity.CENTER;
        } else {
            windowLayoutParams.width = portWidth;
            // width  可能有 负数，因为还没有初始化完毕
            windowLayoutParams.height = portHeight;
            windowLayoutParams.gravity = Gravity.CENTER;
        }
        Log.i(TAG, "setDailogSize:params.width  " + windowLayoutParams.width);
        Log.i(TAG, "setDailogSize:params.height  " + windowLayoutParams.height);
        window.setAttributes(windowLayoutParams);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView();
        initListener();
    }


    protected abstract void initView();

    protected abstract void initListener();

    protected abstract int getLayoutRes();

    public void setOnButtonsClickListener(OnButtonsClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        if (onDismissListener != null) {
            onDismissListener.onDismiss(getDialog());
        }
        dialogDisposable.dispose();
        dialogDisposable.clear();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (onDismissListener != null) {
            onDismissListener.onDismiss(getDialog());
        }
        dialogDisposable.dispose();
        dialogDisposable.clear();
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        onDismissListener = listener;
        Dialog dialog = getDialog();
        if (dialog != null && onDismissListener != null) {
            dialog.setOnDismissListener(onDismissListener);
        }
    }


    public interface OnButtonsClickListener<T> {
        void sureButtonClick(T t);

        void cancelButtonClick();
    }

}
package com.viomi.ovensocommon;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created with Android Studio
 * Author:Ljh
 * Date:2020/1/7
 **/
public abstract class ViomiBaseFragment<V extends ViewDataBinding> extends Fragment {
    private static final String TAG = "BaseActivity";
    //故障类信息提示
    private Disposable mStatusBarDisposable;
    private long lastClickTime;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private int currentViewId;
    protected V viewDataBinding;
    private View contentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, " onCreateView");
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, getContentViewId(), container, false);
        }
        if (contentView == null) {
            contentView = viewDataBinding.getRoot();
        }
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDisposable();
    }

    protected void initListener() {
        Log.i(TAG, "initListener: ");
    }

    protected abstract int getContentViewId();

    protected abstract void initView();

    protected abstract void initData();

    protected void addDisposable(Disposable disposable) {
        Log.i(TAG, "addDisposable: " + mCompositeDisposable);
        Log.i(TAG, "addDisposable: " + disposable);
        if (mCompositeDisposable == null) {
            return;
        }
        mCompositeDisposable.add(disposable);
    }

    protected void clearDisposable() {
        if (mCompositeDisposable == null) {
            return;
        }
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
    }

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
}


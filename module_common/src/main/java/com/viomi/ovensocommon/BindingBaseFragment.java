package com.viomi.ovensocommon;

import android.content.Context;
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
 **/
public abstract class BindingBaseFragment<V extends ViewDataBinding> extends Fragment {
    private static final String TAG = "BindingBaseFragment";
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    protected Context mContext;
    private long lastClickTime;// 上次点击时间
    protected V viewDataBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView");
        if (viewDataBinding == null) {
            viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        }
        return viewDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        initView();
        initListener();
        initData();
    }

    protected abstract int getLayoutRes();

    protected abstract void initView();

    protected abstract void initData();

    protected void initListener() {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart() {
        Log.d(TAG, " onStart");
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.compositeDisposable.clear();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }


    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }


    /**
     * 两次点击间隔不能少于 MIN_CLICK_TIME_MS 毫秒（防止重复点击）
     */
    protected boolean isRepeatedClick(int viewId) {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        int MIN_CLICK_DELAY_TIME = CommonConstant.MIN_CLICK_TIME_MS;
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}



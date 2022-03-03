package com.viomi.ovensocommon;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;


import com.viomi.ovensocommon.toast.ViomiToastUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class ViomiBaseMvvmActivity<V extends ViewDataBinding, VM extends ViomiBaseViewModel> extends AppCompatActivity {
    private static final String TAG = "ViomiBaseMvvmActivity";
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    protected V viewDataBinding;
    protected VM mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performDataBinding();
        initView();
        initData();
        initListener();
    }

    protected void performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getContentViewId());
        setContentView(viewDataBinding.getRoot());
    }

    protected void addDisposable(Disposable waterDisposable) {
        compositeDisposable.add(waterDisposable);
    }


    protected abstract int getContentViewId();

    protected abstract void initData();

    protected abstract void initListener();

    protected void initView() {
        initViewDataBinding();
        initViewObservable();
        ViomiToastUtil.createCenterToast(getApplicationContext());
    }

    private void initViewDataBinding() {
        this.viewDataBinding = DataBindingUtil.setContentView(this, getContentViewId());
        Log.i(TAG, "initViewDataBinding: viewDataBinding:" + viewDataBinding);
        this.mViewModel = createViewModel();
        Log.i(TAG, "initViewDataBinding:mViewModel : " + mViewModel);
        this.getLifecycle().addObserver(mViewModel);
    }

    public VM createViewModel() {
        // 在lifecycle1.1.1(android.arch.lifecycle:extensions:1.1.1)之前 s  否则直接new
        ViewModelProvider viewModelProvider = new ViewModelProvider(this, onBindViewModelFactory());
        Log.i(TAG, "createViewModel: viewModelProvider: " + viewModelProvider);
        ViomiBaseViewModel baseViewModel = viewModelProvider.get(onBindViewModel());
        Log.i(TAG, "createViewModel: baseViewMode: " + baseViewModel);
        return (VM) baseViewModel;
    }

    public abstract Class<VM> onBindViewModel();

    public abstract ViewModelProvider.Factory onBindViewModelFactory();


    public abstract void initViewObservable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }
}

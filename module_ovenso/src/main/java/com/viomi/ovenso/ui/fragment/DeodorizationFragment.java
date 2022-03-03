package com.viomi.ovenso.ui.fragment;

import android.util.Log;

import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.FragmentDeodorizationBinding;
import com.viomi.ovensocommon.BaseDialogFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author admin
 * @date 2022/01/11
 * @describe 除味弹窗
 */
public class DeodorizationFragment extends BaseDialogFragment<FragmentDeodorizationBinding> {
    private final static String TAG = "DeodorizationFragment";
    // 默认60s后自动关闭dialog
    private final static int DEFAULT_DISMISS_TIME = 60;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_deodorization;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        Disposable closeDisposable = Observable.timer(DEFAULT_DISMISS_TIME, TimeUnit.SECONDS)
                .onTerminateDetach()
                .subscribeOn(Schedulers.newThread())
                .subscribe(aLong -> {
                    dismissAllowingStateLoss();
                });
        addDispose(closeDisposable);
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.deodorizationSure.setOnClickListener(v -> {
            dismissAllowingStateLoss();
        });
    }

}

package com.viomi.waterpurifier.edison.ui.fragment;

import android.util.Log;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.WaterPreference;
import com.viomi.waterpurifier.edison.databinding.FragmentWaterFirstoutBinding;
import com.viomi.waterpurifier.edison.util.WaterActionUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FirstOutFragment extends BaseDialogFragment<FragmentWaterFirstoutBinding> {
    private static final String TAG = "FirstOutFragment";
    public static final String KEY_PROTECT_TIME = "keyProtectTime";
    private static final int FIRSTOUT_FLOW = 2000;

    Disposable countDownObservale;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_water_firstout;
    }

    @Override
    protected void initView() {
        int waterTemperature = WaterConstant.DEFAULT_NORMAL_WATER_TEMP;
        WaterActionUtils.startOutWater(waterTemperature, FIRSTOUT_FLOW);
        startCountDown();
    }

    private void startCountDown() {
        Log.i(TAG, "startCountDown: ");
        final int[] leftProtectTime = {getArguments().getInt(KEY_PROTECT_TIME)};
//         .compose(SchedulerProvider.getInstance().applySchedulers())
        countDownObservale = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Log.d(TAG, "addDisposable: totalTime: " + leftProtectTime[0]);
                    if (leftProtectTime[0] > 0) {
                        leftProtectTime[0]--;
                        viewDataBinding.wateroutCountdown.setText(String.valueOf(leftProtectTime[0]));
                        WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_FIRST_OUT_TIME, leftProtectTime[0]);
                        return;
                    }
                    countDownObservale.dispose();
                    WaterActionUtils.stopOutWater();
                    dismissAllowingStateLoss();
                }, Throwable::printStackTrace);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        countDownObservale.dispose();
    }

    @Override
    protected void initListener() {
    }

}

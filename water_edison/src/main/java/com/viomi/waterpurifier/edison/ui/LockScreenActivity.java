package com.viomi.waterpurifier.edison.ui;

import android.text.TextUtils;
import android.util.Log;

import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.utils.TimeFormateUtil;
import com.viomi.router.annotation.Route;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.databinding.ActivityLockScreenBinding;
import com.viomi.waterpurifier.edison.util.TimeUtil;
import com.viomi.waterpurifier.edison.widget.ScrollToUnlockView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

@Route(path = ViomiRouterConstant.WATER_ACTIVITY_LOCK)
public class LockScreenActivity extends BaseActivity<ActivityLockScreenBinding> {
    private static final String TAG = "LockScreenActivity";
    public static final int TIME_UPDATE_PRIOR = 60;
    Disposable timeDisposable;
    final static int TIME_TEXT_SMALL_SIZE = 80;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lock_screen;
    }

    @Override
    protected void initView() {
        updateTimeAndDate();
    }

    @Override
    protected void initData() {
        initListener();
    }

    @Override
    public void initListener() {
        viewDataBinding.lockUnlock.setListener(new ScrollToUnlockView.OnScrollResultListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: finish");
                finish();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: ");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当前时间的判断todo
        String currentTime = "123";
        viewDataBinding.lockDate.setText(currentTime);
        Log.i(TAG, "initData: currentTime: " + currentTime);
        if (TextUtils.equals(currentTime, getResources().getString(R.string.machine_name))) {
            viewDataBinding.lockDate.setTextSize(TIME_TEXT_SMALL_SIZE);
            return;
        }
        updateTimeAndDate();
        if (timeDisposable != null) {
            timeDisposable.dispose();
            timeDisposable = null;
        }
        Calendar curCalendar = Calendar.getInstance(TimeZone.getDefault());
        int currrentSeconds = curCalendar.get(Calendar.SECOND);
        int delaySecond = TIME_UPDATE_PRIOR - currrentSeconds;
        timeDisposable = Observable.interval(delaySecond, TIME_UPDATE_PRIOR, TimeUnit.SECONDS).
                observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            updateTimeAndDate();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeDisposable.dispose();
        timeDisposable = null;
    }

    void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        long timeMills = calendar.getTimeInMillis();
        boolean isTwentyFourHourMode = TimeFormateUtil.isTwentyFourFormate();
        Log.i(TAG, "updateTimeAndDate: isTwentyFourHourMode : " + isTwentyFourHourMode);
        String time = "";
        if (isTwentyFourHourMode) {
            time = TimeUtil.getTimehhmm(timeMills);
        } else {
            time = TimeUtil.getTimeHHMM(timeMills);
        }
        Log.i(TAG, "updateTimeAndDate: time: " + time);
        viewDataBinding.lockDate.setText(time);
        SimpleDateFormat myFormatter = new SimpleDateFormat("MM月dd日 ", Locale.CHINA);
        viewDataBinding.lockDate.setText(myFormatter.format(new Date(timeMills)) + getWorkDay(timeMills));
    }

    public String getWorkDay(long timeMills) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int weekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;
        Log.i(TAG, "getWorkDay: weekIndex: " + weekIndex);
        if (weekIndex < 0) {
            weekIndex = 0;
        }
        return weekDays[weekIndex];
    }
}
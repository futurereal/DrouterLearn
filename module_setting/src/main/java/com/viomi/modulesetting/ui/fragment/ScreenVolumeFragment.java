package com.viomi.modulesetting.ui.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentScreenVolumeBinding;
import com.viomi.modulesetting.ui.fragment.dialog.TimeSelectFragment;
import com.viomi.modulesetting.utils.ModuleSettingUtil;
import com.viomi.modulesetting.utils.SettingPreference;
import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.componentservice.waterpurifier.WaterServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.MediaPlayerUtils;
import com.viomi.ovensocommon.view.CommonSettingSeekBar;
import com.viomi.router.annotation.Route;

import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Description: 屏幕与声音
 */
@Route(path = ViomiRouterConstant.SETTING_FRAGMENT_SCREEN)
public class ScreenVolumeFragment extends BindingBaseFragment<FragmentScreenVolumeBinding> {
    private static final String TAG = "ScreenVolumeFragment";
    private int brightness = 0, systemVolume = 0, musicVolume = 0, systemMaxVolume = 0, musicMaxVolume = 0;
    private AudioManager mAudioManager;
    private NotificationManager mNotificationManager;

    private final ViewTreeObserver.OnGlobalLayoutListener mBrightGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            CommonSettingSeekBar brightnessSeekbar = viewDataBinding.screenvolumeBrightnessSeekbar;
            if (brightnessSeekbar != null && brightnessSeekbar.getStepWidth() != 0) {
                brightnessSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                brightnessSeekbar.setProgressValue(Math.round(brightness / 2.55f));
                viewDataBinding.screenvolumeBrightnessPercentage.setText(Math.round(brightness / 2.55f) + "%");
            }
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener mVolumeGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            CommonSettingSeekBar volumeSeekbar = viewDataBinding.screenvolumeVolumeSeekbar;
            if (volumeSeekbar != null && volumeSeekbar.getStepWidth() != 0) {
                volumeSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int percent = (int) (musicVolume / (musicMaxVolume * 0.01f));
                volumeSeekbar.setProgressValue(percent);
                viewDataBinding.screenvolumeVolumePercentage.setText(percent + "%");
            }
        }
    };


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_screen_volume;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        viewDataBinding.screenvolumeSpeakGroup.setVisibility(ModuleSettingConstants.IS_SHOW_SPEAK ? View.VISIBLE : View.GONE);
        viewDataBinding.screenvolumeSpeakSwitch.setOn(SettingPreference.getInstance().getBoolean(SettingPreference.SPEECH_ENABLE, ModuleSettingConstants.DEFAULT_SPEAK_SWITCH), false);
        initListener();
    }

    @Override
    public void initData() {
        Log.i(TAG, "initData: ");
        initScreenSleep();
        initBrightness();
        initVolume();
    }

    /**
     * 初始化屏幕亮度
     */
    private void initBrightness() {
        try {
            brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "SettingNotFoundException: " + e.getMessage());
            e.printStackTrace();
        }
        Log.i(TAG, "initBrightness:brightness:  " + brightness);
        // 屏幕亮度
        viewDataBinding.screenvolumeBrightnessSeekbar.setOnValueChangeListener(new CommonSettingSeekBar.OnValueChangeListener() {
            @Override
            public void onSelectedPercentage(int selectedPercentage) {
                viewDataBinding.screenvolumeBrightnessPercentage.setText(selectedPercentage + "%");
                brightness = Math.round(selectedPercentage * 2.55f);
                Log.i(TAG, "onSelectedPercentage: brightness: " + brightness);
                Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
                try {
                    Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                    getActivity().getContentResolver().notifyChange(uri, null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean beforeTouch() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getActivity())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouch() {
            }

            @Override
            public void onDragging(int percentage) {
                viewDataBinding.screenvolumeBrightnessPercentage.setText(percentage + "%");
                Log.i(TAG, "onDragging: percentage: " + percentage);
                brightness = Math.round(percentage * 2.55f);
                try {
                    Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
                    Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                    getActivity().getContentResolver().notifyChange(uri, null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initScreenSleep() {
        // 锁屏时间
        int lockTime = 0;
        try {
            lockTime = Settings.System.getInt(getContext().getContentResolver(), ModuleSettingConstants.KEY_CHILD_LOCK_TIME);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "initScreenSleep: lockTime: " + lockTime);
        String lockTimeName = ModuleSettingUtil.getScreenOffTimeName(lockTime);
        viewDataBinding.screenvolumeLockTime.setText(lockTimeName);
        // 息屏时间
        int standByTime = 0;
        // 系统取出来的 时间为毫秒值
        try {
            standByTime = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "initScreenSleep: standByTime： " + standByTime);
        String currentScreenOffName = ModuleSettingUtil.getScreenOffTimeName(standByTime);
        viewDataBinding.screenvolumeStandbyTime.setText(currentScreenOffName);
    }

    /**
     * 声音调节
     */
    private void initVolume() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (mAudioManager != null) {
            musicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            systemVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
            systemMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
            musicMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            viewDataBinding.screenvolumeVolumePercentage.setText((int) (musicVolume * 100f / musicMaxVolume) + "%");
        }
        viewDataBinding.screenvolumeVolumeSeekbar.setOnValueChangeListener(new CommonSettingSeekBar.OnValueChangeListener() {
            @Override
            public void onSelectedPercentage(int selectedPercentage) {
                systemVolume = (int) (selectedPercentage * 0.01f * systemMaxVolume);
                musicVolume = (int) (selectedPercentage * 0.01f * musicMaxVolume);
                viewDataBinding.screenvolumeVolumePercentage.setText((int) (musicVolume * 100f / musicMaxVolume) + "%");
                if (mAudioManager != null) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, systemVolume, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);
                }
            }

            @Override
            public void onTouch() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mNotificationManager != null &&
                        !mNotificationManager.isNotificationPolicyAccessGranted()) {
                    Log.i(TAG, "onTouch: start access Setting  return");
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                    return;
                }
                MediaPlayerUtils.getInstance().stopPlay();
                MediaPlayerUtils.getInstance().startPlayRawResource(R.raw.ok2);
            }

            @Override
            public boolean beforeTouch() {
                return false;
            }

            @Override
            public void onDragging(int percentage) {
                systemVolume = (int) (percentage * 0.01 * systemMaxVolume);
                musicVolume = (int) (percentage * 0.01 * musicMaxVolume);
                viewDataBinding.screenvolumeVolumePercentage.setText((int) (musicVolume * 100f / musicMaxVolume) + "%");
                if (mAudioManager != null) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, systemVolume, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);
                }
            }
        });
    }

    @Override
    public void initListener() {
        viewDataBinding.screenvolumeStandbyTime.setOnClickListener(v -> {
            String standByTimeName = viewDataBinding.screenvolumeStandbyTime.getText().toString();
            showStandByFragment(standByTimeName, TimeSelectFragment.STANDTYPE_SCREEN_OFF);
        });
        viewDataBinding.screenvolumeLockTime.setOnClickListener(v -> {
            String lockTimeName = viewDataBinding.screenvolumeLockTime.getText().toString();
            showStandByFragment(lockTimeName, TimeSelectFragment.STANDTYPE_CHILD_LOCK);
        });
        viewDataBinding.screenvolumeSpeakSwitch.setOnSwitchStateChangeListener(isOn -> SettingPreference.getInstance().setValue(SettingPreference.SPEECH_ENABLE, isOn));

        // 设置时间改变，设置属性，并且更新界面
        // 童锁
        // 插件端设置息屏时间，同步更新界面
        Disposable voloumDisposalbe = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            Log.i(TAG, "accept: " + viomiRxBusEvent);
            int msgId = viomiRxBusEvent.getMsgId();
            Object msgObj = viomiRxBusEvent.getMsgObject();
            switch (msgId) {
                // 设置时间改变，设置属性，并且更新界面
                case ModuleSetingEventConstant.MSG_UPDATE_STANDBYTIME:
                    String standByName = (String) msgObj;
                    viewDataBinding.screenvolumeStandbyTime.setText(standByName);
                    int standByTime = ModuleSettingUtil.getScreenOffTime(standByName);
                    Log.i(TAG, "accept: standByTime: " + standByTime);
                    boolean screenOffResult = Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, standByTime);
                    int screenOffArrayId = ModuleSettingServiceFactory.getInstance().getViotService().getScreenOffArrayId();
                    Log.i(TAG, "accept: screenOffArrayId: " + screenOffArrayId + "  screenOffResult: " + screenOffResult);
                    int[] screenOffArray = getResources().getIntArray(screenOffArrayId);
                    int standByIndex = 0;
                    for (int i = 0; i < screenOffArray.length; i++) {
                        if (standByTime == screenOffArray[i]) {
                            standByIndex = i;
                            break;
                        }
                    }
                    Log.i(TAG, "accept:standByIndex:  " + standByIndex);
                    WaterServiceFactory.getInstance().getWaterService().reportStandByTime(standByIndex);
                    break;
                //  设置锁屏时间
                case ModuleSetingEventConstant.MSG_UPDATE_LOCKTIME:
                    String lockTimeName = (String) msgObj;
                    viewDataBinding.screenvolumeLockTime.setText(lockTimeName);
                    int lockTime = ModuleSettingUtil.getScreenOffTime(lockTimeName);
                    boolean lockSetResult = Settings.System.putInt(getActivity().getContentResolver(), ModuleSettingConstants.KEY_CHILD_LOCK_TIME, lockTime);
                    Log.i(TAG, "accept: lockTime: " + lockTime + " lockSetResult： " + lockSetResult);
                    break;
                // 插件端设置息屏时间，同步更新界面
                case ModuleSetingEventConstant.MSG_SCREENOFF_TIME:
                    String currentStandByName = (String) msgObj;
                    viewDataBinding.screenvolumeStandbyTime.setText(currentStandByName);
                    break;
                default:
                    break;
            }
        });
        addDisposable(voloumDisposalbe);
    }

    private void showStandByFragment(String currentName, int standByType) {
        Log.i(TAG, "showStandByFragment: currentName: " + currentName + " standByTime: " + standByType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getActivity())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        TimeSelectFragment standbyCountdownDialog = new TimeSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TimeSelectFragment.KEY_STANDTIME_NAME, currentName);
        bundle.putInt(TimeSelectFragment.KEY_STANDTYPE, standByType);
        standbyCountdownDialog.setArguments(bundle);
        standbyCountdownDialog.show(getActivity().getSupportFragmentManager(), "" + standByType);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        viewDataBinding.screenvolumeBrightnessSeekbar.getViewTreeObserver().addOnGlobalLayoutListener(mBrightGlobalLayoutListener);
        viewDataBinding.screenvolumeVolumeSeekbar.getViewTreeObserver().addOnGlobalLayoutListener(mVolumeGlobalLayoutListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
        MediaPlayerUtils.getInstance().stopPlay();
        if (viewDataBinding.screenvolumeBrightnessSeekbar != null) {
            viewDataBinding.screenvolumeBrightnessSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(mBrightGlobalLayoutListener);
            viewDataBinding.screenvolumeBrightnessSeekbar.setOnValueChangeListener(null);
        }
        if (viewDataBinding.screenvolumeVolumeSeekbar != null) {
            viewDataBinding.screenvolumeVolumeSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(mVolumeGlobalLayoutListener);
            viewDataBinding.screenvolumeVolumeSeekbar.setOnValueChangeListener(null);
        }
    }
}

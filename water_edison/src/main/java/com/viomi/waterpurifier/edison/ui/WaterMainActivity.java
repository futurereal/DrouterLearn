package com.viomi.waterpurifier.edison.ui;

import static com.viomi.waterpurifier.edison.config.WaterThemeEnum.THEME_ONE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.AppUtils;
import com.viomi.common.CommonUtil;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiBaseMvvmActivity;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.componentservice.waterpurifier.WaterServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.ovensocommon.utils.MediaPlayerUtils;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;
import com.viomi.waterpurifier.edison.BuildConfig;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.WaterPreference;
import com.viomi.waterpurifier.edison.config.TemperatureModeEnum;
import com.viomi.waterpurifier.edison.config.WaterFlowEnum;
import com.viomi.waterpurifier.edison.config.WaterQualityEnum;
import com.viomi.waterpurifier.edison.config.WaterSoundEnum;
import com.viomi.waterpurifier.edison.config.WaterThemeEnum;
import com.viomi.waterpurifier.edison.databinding.ActivityWaterMainBinding;
import com.viomi.waterpurifier.edison.entity.WaterStatusEntity;
import com.viomi.waterpurifier.edison.factory.ProjectViewModuleFactory;
import com.viomi.waterpurifier.edison.manager.MessageDialogManager;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;
import com.viomi.waterpurifier.edison.service.WaterService;
import com.viomi.waterpurifier.edison.ui.fragment.ChildLockFragment;
import com.viomi.waterpurifier.edison.ui.fragment.WaterThemeFragment;
import com.viomi.waterpurifier.edison.util.CustomFrameAnim;
import com.viomi.waterpurifier.edison.util.TimeUtil;
import com.viomi.waterpurifier.edison.util.WaterActionUtils;
import com.viomi.waterpurifier.edison.util.WaterUtils;
import com.viomi.waterpurifier.edison.viewmodule.WaterMainViewModel;
import com.viomi.waterpurifier.edison.widget.MessageTipLayout;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: ERO 净水器 主界面
 */

@Route(path = ViomiRouterConstant.WATER_MAIN)
public class WaterMainActivity extends ViomiBaseMvvmActivity<ActivityWaterMainBinding, WaterMainViewModel> {
    private static final String TAG = "WaterMainActivity";
    public static final int TEMPERATURE_MAX = 100;
    public static final int WATER_OUT_ANIM_INDEX = 4;
    public WaterThemeEnum waterThemeEnum = WaterThemeEnum.THEME_ONE;
    // 2分钟倒计时
    private final static long CLEAN_COUNTDOWN_TIME = 2 * 60 * 1000;
    private static final String INFINITY_TIP = "∞";
    private static final int MODE_INFINITY = 0;
    private final static long ONE_SECOND = 1000;
    public final static String READY_CLEAN_TIP = "s后开启自动清洁";
    private CountDownTimer countDownTimer;
    // 延迟更新时间1分钟
    private final static long DELAY_UPDATE_TIME = 60 * 1000;
    private boolean isChildLockOpen;

    // 童锁温度 50度
    private static final int CHILD_LOCK_TEMPERATURE = 50;
    // 第一次出水的时间 10s
    public static final int DEFAULT_FIRST_OUT_TIME = 10;
    public volatile int defaultNormalTemp;

    public static final int MSG_TEMPERATURE_CHANGED = 101;
    public static final int MSG_TEMPERATURE_MODE_CHANGE = 102;
    public static final int MSG_FLOW_MODE_CHANGE = 103;

    // 杯量大小动画的资源文件数量
    private static final int ANIM_CUP_RESOURCE_COUNT = 41;
    // 出水动画的资源文件数量
    private static final int ANIM_WATER_OUT_COUNT = 17;
    private final static long UI_UPDATE_DELAY = 2000;
    private WaterMainHandler waterMainHandler;
    private volatile CustomFrameAnim waterPoloAnim;
    // 大杯，中杯，小杯 ，连续出水， 正在出水
    private final String[] ANIM_THEME_ONE_PREFIX = new String[]{"theme_default_big_cup_", "theme_default_small_cup_", "theme_default_middle_cup_", "theme_default_big_cup_", "theme_default_water_out_"};
    private final String[] ANIM_THEME_TWO_PREFIX = new String[]{"theme_green_big_cup_", "theme_green_small_cup_", "theme_green_middle_cup_", "theme_green_big_cup_", "theme_green_water_out_"};
    private final String[] ANIM_THEME_THREE_PREFIX = new String[]{"theme_sky_blue_normal_", "theme_sky_blue_normal_", "theme_sky_blue_normal_", "theme_sky_blue_normal_", "theme_sky_blue_water_out_"};
    private PropertyPreferenceManager propertyPreferenceManager;
    private ArrayList<Integer> alludeTemperatureList;
    // 总的出水量
    private int[] flowAmounts;
    private int currentTemperatureMode;

    // 主题自动切换
    private Disposable themeDisposable;
    private int autoThemeArrayIndex = -1;
    private String[] autoThemeArray;
    private int autoThemeTimeIndex = 0;
    private final static String COMMA = ",";


    public class WaterMainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TEMPERATURE_CHANGED:
                    int temperature = (int) msg.obj;
                    Log.i(TAG, "handleMessage: temperature " + temperature);
                    mViewModel.waterTemperatureSingleLiveEvent.setValue(temperature);
                    changeWaterMode(temperature);
                    break;
                case MSG_TEMPERATURE_MODE_CHANGE:
                    int temperatureMode = (int) msg.obj;
                    Log.i(TAG, "handleMessage: temperatureMode: " + temperatureMode);
                    if (temperatureMode == TemperatureModeEnum.CUSTOM.ordinal()) {
                        return;
                    }
                    mViewModel.temperatureModeLiveEvent.setValue(TemperatureModeEnum.values()[temperatureMode]);
                    int currentTemperature = alludeTemperatureList.get(temperatureMode);
                    Log.i(TAG, "handleMessage: currentTemperature: " + currentTemperature);
                    mViewModel.waterTemperatureSingleLiveEvent.setValue(currentTemperature);
                    currentTemperatureMode = temperatureMode;
                    if (temperatureMode != TemperatureModeEnum.CUSTOM.ordinal()) {
                        WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_CUSTOMMODE_INDEX, temperatureMode);
                    }

                    break;
                case MSG_FLOW_MODE_CHANGE:
                    int flowMode = (int) msg.obj;
                    Log.i(TAG, "handleMessage: flowMode " + flowMode);
                    mViewModel.flowModeLiveEvent.setValue(WaterFlowEnum.values()[flowMode]);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_water_main;
    }

    @Override
    public void initData() {
        Log.i(TAG, "initData: ");
        // 由于 和 moduleSetting application 初始化先后 问题，暂时放在main 里面
        waterMainHandler = new WaterMainHandler();
        mViewModel.setMainHandler(waterMainHandler);
        waterPoloAnim = new CustomFrameAnim(viewDataBinding.watermainAmin);
        WaterServiceFactory.getInstance().setWaterService(new WaterService());
        ModuleSettingServiceFactory.getInstance().getViotService().setMenuArrayId(R.array.menu_water_purifier);
        ModuleSettingServiceFactory.getInstance().getViotService().setScreenOffArrayId(R.array.screenofftime_water);
        ModuleSettingServiceFactory.getInstance().getViotService().setAgingRoutPath(ViomiRouterConstant.WATER_AGING);
        ModuleSettingServiceFactory.getInstance().getViotService().setShowMiotBind(false);
        updateTime();
        mViewModel.updateNetStatusAndListener();
        int versionCode = AppUtils.getAppVersionCode();
        String currentModelName = "waterEdison";
        viewDataBinding.watermainApktime.setText(currentModelName + "-v" + versionCode + "-" + BuildConfig.apkBuildTime);
        propertyPreferenceManager = PropertyPreferenceManager.getInstance();
        // 童锁状态的处理
        isChildLockOpen = (boolean) WaterUtils.getLocalWaterProps(WaterPropEnum.CHILD_LOCK, WaterConstant.CHILD_LOCK_DEFAULT);
        Log.i(TAG, "initData:isChildLockOpen: " + isChildLockOpen);
        // 水质 的值  和  枚举的 index 保持一直
        int waterQuality = (int) propertyPreferenceManager.getProperty(WaterPropEnum.WATER_QUALITY.siid, WaterPropEnum.WATER_QUALITY.piid, WaterConstant.DEFAULT_WATER_QUALITY);
        WaterQualityEnum waterQualityEnum = WaterQualityEnum.values()[waterQuality];
        mViewModel.waterQualitySingleLiveEvent.setValue(waterQualityEnum);
        viewDataBinding.setWaterQualityEnum(waterQualityEnum);
        // 杯量模式
        int cupSmall = (int) propertyPreferenceManager.getProperty(WaterPropEnum.SMALL_CUP_FLOW.siid, WaterPropEnum.SMALL_CUP_FLOW.piid, WaterConstant.DEFAULT_SMALL_CUP_FLOW);
        int cupMiddle = (int) propertyPreferenceManager.getProperty(WaterPropEnum.MIDDLE_CUP_FLOW.siid, WaterPropEnum.MIDDLE_CUP_FLOW.piid, WaterConstant.DEFAULT_MID_CUP_FLOW);
        int cupBig = (int) propertyPreferenceManager.getProperty(WaterPropEnum.BIG_CUP_FLOW.siid, WaterPropEnum.BIG_CUP_FLOW.piid, WaterConstant.DEFAULT_BIG_CUP_FLOW);
        flowAmounts = new int[]{Integer.MAX_VALUE, cupSmall, cupMiddle, cupBig};
        int flowMode = (int) propertyPreferenceManager.getProperty(WaterPropEnum.CUP_MODE.siid, WaterPropEnum.CUP_MODE.piid, WaterConstant.DEFAULT_CUP_MODE);
        Log.i(TAG, "initData: flowMode: " + flowMode);
        // 一定要是 setValue 不能是 post
        mViewModel.flowModeLiveEvent.setValue(WaterFlowEnum.values()[flowMode]);
        //常温、温水、热水、开水、当前制水温度也就是 自定温度
        int tempNormal = defaultNormalTemp = (int) propertyPreferenceManager.getProperty(WaterPropEnum.TEMPERATURE_WATER_IN.siid, WaterPropEnum.TEMPERATURE_WATER_IN.piid, WaterConstant.DEFAULT_NORMAL_WATER_TEMP);
        int tempWarm = (int) propertyPreferenceManager.getProperty(WaterPropEnum.TEMP_WARM.siid, WaterPropEnum.TEMP_WARM.piid, WaterConstant.DEFAULT_WARM_WATER_TEMP);
        int tempHot = (int) propertyPreferenceManager.getProperty(WaterPropEnum.TEMP_HOT.siid, WaterPropEnum.TEMP_HOT.piid, WaterConstant.DEFAULT_HOT_WATER_TEMP);
        int tempBoiling = (int) propertyPreferenceManager.getProperty(WaterPropEnum.TEMP_BOILING.siid, WaterPropEnum.TEMP_BOILING.piid, WaterConstant.DEFAULT_BOILING_WATER_TEMP);
        Log.i(TAG, "initData: " + tempNormal + "  " + tempWarm + "  " + tempHot + " ");
        alludeTemperatureList = new ArrayList<>();
        alludeTemperatureList.add(tempNormal);
        alludeTemperatureList.add(tempWarm);
        alludeTemperatureList.add(tempHot);
        alludeTemperatureList.add(tempBoiling);
        currentTemperatureMode = (int) propertyPreferenceManager.getProperty(WaterPropEnum.TEMP_MODE.siid, WaterPropEnum.TEMP_MODE.piid, WaterConstant.DEFAULT_TEMPERATURE_MODE);
        Log.i(TAG, "initData: temperatureMode: " + currentTemperatureMode);
        if (currentTemperatureMode != TemperatureModeEnum.CUSTOM.ordinal()) {
            TemperatureModeEnum temperatureModeEnum = TemperatureModeEnum.values()[currentTemperatureMode];
            // 更新模式
            mViewModel.temperatureModeLiveEvent.setValue(temperatureModeEnum);
            // 更新温度
            mViewModel.waterTemperatureSingleLiveEvent.setValue(alludeTemperatureList.get(currentTemperatureMode));
        }
        viewDataBinding.setWaterMainViewModel(mViewModel);
        // 设置出水状态 更新界面
        mViewModel.waterOutStatusSingleLiveEvent.setValue(false);
        // 更新主题
        setTheme();
        boolean autoChangeTheme = (boolean) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_SWITCH, false);
        if (autoChangeTheme) {
            updateAutoTheme();
            addThemeDisposable();
        }
    }

    @Override
    public void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.watermainSetting.setOnClickListener(v -> {
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_COMMON_SETTING).navigation();
        });
        viewDataBinding.watermainWifistatus.setOnClickListener(v -> {
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_CONTAINER)
                    .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_WLAN)
                    .navigation();
        });
        viewDataBinding.watermainChildlock.setOnClickListener(v -> {
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_CONTAINER)
                    .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.WATER_FRAGMENT_CHILD_LOCK)
                    .navigation();
        });
        viewDataBinding.watermainOut.setOnClickListener(v -> {
            boolean isWaterOut = mViewModel.waterOutStatusSingleLiveEvent.getValue();
            Log.i(TAG, "initListener: isWaterOut: " + isWaterOut);
            if (isWaterOut) {
                WaterActionUtils.stopOutWater();
            } else {
                checkAndStartWaterOut();
            }

        });
        registerModuleEvent();
    }

    /**
     * 刷新主题数组
     */
    private void updateAutoTheme() {
        autoThemeTimeIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CHANGETIME, WaterThemeFragment.DEFAULT_CHANGE_TIME_INDEX);
        String autoSp = (String) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_AUTO_INDEX, "");
        Log.d(TAG, "updateAutoTheme: autoSp: " + autoSp);
        if (!TextUtils.isEmpty(autoSp)) {
            autoThemeArray = autoSp.split(COMMA);
        }
    }

    private void addThemeDisposable() {
        Log.i(TAG, "addThemeDisposable: autoThemeTimeIndex: " + autoThemeTimeIndex);
        if (autoThemeArray == null || autoThemeTimeIndex < 0) {
            return;
        }
        removeThemeDisposable();
        autoThemeArrayIndex = -1;
        themeDisposable = Observable.interval(0, TimeUtil.getChangeDate(autoThemeTimeIndex), TimeUnit.MINUTES)
                .onTerminateDetach()
                .subscribe(aLong -> {
                    autoThemeArrayIndex++;
                    if (autoThemeArrayIndex >= autoThemeArray.length) {
                        autoThemeArrayIndex = 0;
                    }
                    Log.d(TAG, "addThemeDisposable: autoThemeArrayIndex: " + autoThemeArrayIndex);
                    WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_THEME_CURRENT_INDEX, Integer.valueOf(autoThemeArray[autoThemeArrayIndex]));
                    setTheme();
                });
        addDisposable(themeDisposable);
    }

    /**
     * 终止自动切换主题
     */
    private void removeThemeDisposable() {
        Log.d(TAG, "removeThemeDisposable: ");
        if (themeDisposable != null) {
            themeDisposable.dispose();
            compositeDisposable.remove(themeDisposable);
        }
    }

    private void setTheme() {
        int currentThemeIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CURRENT_INDEX, 0);
        Log.i(TAG, "setTheme: currentThemeIndex : " + currentThemeIndex);
        viewDataBinding.setWaterThemeEnum(WaterThemeEnum.values()[currentThemeIndex]);
        int flowMode = mViewModel.flowModeLiveEvent.getValue().ordinal();
        changePoloAnim(flowMode);
    }

    /**
     * 设置时间
     */
    private void updateTime() {
        boolean isBuildTime = CommonUtil.isBuildTime();
        Log.i(TAG, "setTime: isBuildTime : " + isBuildTime);
        viewDataBinding.watermainTimeTip.setVisibility(isBuildTime ? View.VISIBLE : View.GONE);
        viewDataBinding.watermainTimeClock.setVisibility(isBuildTime ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 出水按钮的处理
     */
    private void checkAndStartWaterOut() {
        // 检查错误并且弹出框
        boolean isShowErrorDialog = MessageDialogManager.getInstance().checkErrorAndShowDialog();
        if (isShowErrorDialog) {
            Log.i(TAG, "checkAndStartWaterOut: isShowErrorDialog  return");
            return;
        }
        // 如果第一次出水,出水10s钟
        int protectTime = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_FIRST_OUT_TIME, DEFAULT_FIRST_OUT_TIME);
        Log.i(TAG, "startWaterOut: " + protectTime);
        if (protectTime > 0) {
            Log.i(TAG, "startWaterOut showFirstDialog: ");
            MessageDialogManager.getInstance().showFirstOutAlarmDialog(protectTime);
            return;
        }
        int waterTemperature = viewDataBinding.getWaterTemperature();
        //水温高+童锁
        if (isChildLockOpen && waterTemperature >= CHILD_LOCK_TEMPERATURE) {
            ChildLockFragment childLockFragment = new ChildLockFragment();
            childLockFragment.show(getSupportFragmentManager(), ChildLockFragment.class.getName());
            return;
        }
        // 开始出水
        startWaterOut();
    }

    private void startWaterOut() {
        WaterFlowEnum flowMode = mViewModel.flowModeLiveEvent.getValue();
        int waterAmount = 0;
        if (flowMode.ordinal() == WaterFlowEnum.NO_LIMIT.ordinal()) {
            waterAmount = Integer.MAX_VALUE;
        } else {
            waterAmount = flowAmounts[flowMode.ordinal()];
        }
        int waterTemperature = mViewModel.waterTemperatureSingleLiveEvent.getValue();
        Log.i(TAG, "startWaterOut: waterTemperature " + waterTemperature + "  waterAmount: " + waterAmount);
        // 根据状态更新界面
        MediaPlayerUtils.getInstance().startPlayRawResource(WaterSoundEnum.WATER_OUT.rawId);
        WaterActionUtils.startOutWater(waterTemperature, waterAmount);
    }

    @Override
    public void initViewObservable() {
        Log.i(TAG, "initViewObservable: ");
        // WIFI 状态的更新
        mViewModel.wifiStatusSingleLiveEvent.observe(this, isWifiConnect -> {
            Log.i(TAG, "initViewObservable: isWifiConnect: " + isWifiConnect);
            viewDataBinding.watermainWifistatus.setSelected(isWifiConnect);
            if (!isWifiConnect) {
                return;
            }
            // WiFi连上后，系统时间不一定能立刻切换成网络时间，延迟一分钟准确几率较大
            new Handler(Looper.getMainLooper()).postDelayed(this::updateTime, DELAY_UPDATE_TIME);
        });
        // 出水量的变化 正在出水
        mViewModel.currentWaterOutAmount.observe(this, flow -> viewDataBinding.setCurrentWaterFlow(flow));
        // 出水模式的变化
        mViewModel.flowModeLiveEvent.observe(this, waterfowlEnum -> {
            int selectIndex = 0;
            int ordinalIndex = waterfowlEnum.ordinal();
            if (ordinalIndex == 0) {
                selectIndex = WaterFlowEnum.values().length - 1;
            } else {
                selectIndex = ordinalIndex - 1;
            }
            Log.i(TAG, "initViewObservable: selectIndex: " + selectIndex);
            viewDataBinding.watermainFlowDots.setSelected(selectIndex);
            viewDataBinding.setWaterFlowEnum(waterfowlEnum);
            String waterFlowStr = "";
            if (waterfowlEnum.ordinal() == MODE_INFINITY) {
                waterFlowStr = INFINITY_TIP;
                viewDataBinding.temperatureAmountUnitSmall.setVisibility(View.GONE);
            } else {
                waterFlowStr = String.valueOf(flowAmounts[waterfowlEnum.ordinal()]);
                viewDataBinding.temperatureAmountUnitSmall.setVisibility(View.VISIBLE);
            }
            viewDataBinding.setTargetWaterFLowStr(waterFlowStr);
            changePoloAnim(waterfowlEnum.ordinal());
        });
        // 温度变化
        mViewModel.waterTemperatureSingleLiveEvent.observe(this, currentTemperature -> {
            Log.i(TAG, "initViewObservable: currentTemperature: " + currentTemperature);
            viewDataBinding.setWaterTemperature(currentTemperature);

            boolean isMax = (currentTemperature == TEMPERATURE_MAX);
            viewDataBinding.temperatureAdd.setClickable(!isMax);
            viewDataBinding.temperatureAdd.setSelected(!isMax);
            boolean isMin = currentTemperature.equals(alludeTemperatureList.get(TemperatureModeEnum.NORMAL.ordinal()));
            viewDataBinding.temperatureMinus.setClickable(!isMin);
            viewDataBinding.temperatureMinus.setSelected(!isMin);

            updateLockStatus(currentTemperature);
        });
        // 模式的变化
        mViewModel.temperatureModeLiveEvent.observe(this, temperatureModeEnum -> {
            Log.d(TAG, "initViewObservable: temperatureMode: " + temperatureModeEnum.ordinal());
            viewDataBinding.setTemperatueModeEnum(temperatureModeEnum);
            // 自定义模式不更新点标识
            if (temperatureModeEnum != TemperatureModeEnum.CUSTOM) {
                viewDataBinding.temperatureDots.setSelected(temperatureModeEnum.ordinal());
            }
        });
        // 水质模式的切换
        mViewModel.waterQualitySingleLiveEvent.observe(this, waterQualityEnum -> {
            viewDataBinding.setWaterQualityEnum(waterQualityEnum);
            viewDataBinding.watermainQualityDots.setSelected(waterQualityEnum.ordinal());
        });
        // 出水状态 首次启动也要判断
        mViewModel.waterOutStatusSingleLiveEvent.observe(this, isWaterOut -> {
            Log.i(TAG, "initViewObservable: isWaterOut:" + isWaterOut);
            viewDataBinding.setWaterStatus(new WaterStatusEntity(isWaterOut));
            if (isWaterOut) {
                changePoloAnim(ANIM_THEME_ONE_PREFIX.length - 1);
                ModuleSettingServiceFactory.getInstance().getViotService().setKeepScreenOn(true);
                viewDataBinding.temperatureAmountUnitSmall.setVisibility(View.VISIBLE);
                return;
            }
            WaterFlowEnum currentFlowMode = mViewModel.flowModeLiveEvent.getValue();
            Log.i(TAG, "initViewObservable: currentFlowModeId : " + currentFlowMode.ordinal());
            if (currentFlowMode.ordinal() == WaterFlowEnum.NO_LIMIT.ordinal()) {
                viewDataBinding.temperatureAmountUnitSmall.setVisibility(View.GONE);
            }
            changePoloAnim(currentFlowMode.ordinal());
            ModuleSettingServiceFactory.getInstance().getViotService().setKeepScreenOn(false);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        int flowMode = mViewModel.flowModeLiveEvent.getValue().ordinal();
        changePoloAnim(flowMode);
       /* List<PropertyEntity> localPropertyList = WaterPropGroup.getPropertyFromLocal();
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(localPropertyList);
        List<PropertyEntity> mucPropertyList = WaterPropGroup.getMcuLocalProps();
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(mucPropertyList);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageDialogManager.getInstance().clearInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 如果正在出水发送停水指令
        boolean isWaterOut = viewDataBinding.getWaterStatus().isWaterOut();
        Log.i(TAG, "onStop: isWaterOut: " + isWaterOut);
        if (isWaterOut) {
            WaterActionUtils.stopOutWater();
        }
        waterPoloAnim.stopAnim();

    }

    public void registerModuleEvent() {
        //主题和属性变化的监听
        Disposable waterDisposable = ViomiRxBus.getInstance().subscribeUi(event -> {
            Log.i(TAG, "registerModuleEvent: event: " + event.getMsgId());
            Object object = event.getMsgObject();
            switch (event.getMsgId()) {
                case WaterBusEvent.MSG_SELFCLEAN_COUNTDOWN:
                    showSelfCleanCountDown();
                    return;
                case WaterBusEvent.MSG_FILTER_PROPERTY:
                    PropertyEntity filterPropertyEntity = (PropertyEntity) object;
                    Log.i(TAG, "dealPropertyChange: filterPropertyEntity ： " + filterPropertyEntity);
                    int filterLeftTime = (int) filterPropertyEntity.getContent();
                    if (filterLeftTime >= WaterConstant.FILTER_ERROR_MARGIN) {
                        viewDataBinding.watermainMessagetip.setVisibility(View.GONE);
                        return;
                    }
                    if (filterPropertyEntity.getSid() == WaterPropEnum.FILTER_4IN1_LIFE_TIME.siid) {
                        viewDataBinding.watermainMessagetip.showCommonTip(MessageTipLayout.TYPE_FILTER_INONE);
                    } else if (filterPropertyEntity.getSid() == WaterPropEnum.FILTER_CARBON_LIFE_TIME.siid) {
                        viewDataBinding.watermainMessagetip.showCommonTip(MessageTipLayout.TYPE_FILTER_CARBON);
                    }
                    break;
                case WaterBusEvent.MSG_CISTERN_CHANGE:
                    boolean isLackWater = (boolean) object;
                    Log.i(TAG, "dealPropertyChange: isLackWater: " + isLackWater);
                    if (isLackWater) {
                        viewDataBinding.watermainMessagetip.showCommonTip(MessageTipLayout.TYPE_WATER_LACK);
                    } else {
                        viewDataBinding.watermainMessagetip.setVisibility(View.GONE);
                    }
                    break;
                case WaterBusEvent.MSG_SETTING_THEME_CHANGE:
                    // 切换主题
                    boolean isAutoChange = (boolean) object;
                    Log.i(TAG, "registerModuleEvent: MSG_SETTING_THEME_CHANGE isAutoChange: " + isAutoChange);
                    if (isAutoChange) {
                        updateAutoTheme();
                        addThemeDisposable();
                    } else {
                        removeThemeDisposable();
                        setTheme();
                    }
                    break;
                case WaterBusEvent.MSG_THEME_TIME_CHANGE:
                    // 主题变化
                    autoThemeTimeIndex = (int) object;
                    addThemeDisposable();
                    break;
                case CommonConstant.MSG_COMMUNICATE_CONNECTED:
                    Log.i(TAG, "accept: serial connect ");
                    viewDataBinding.watermainMessagetip.setVisibility(View.GONE);
                    // 发送消掉框的需求
                    break;
                case CommonConstant.MSG_COMMUNICATE_DISCONNECT:
                    Log.i(TAG, "accept: serial disconnect ");
                    MessageDialogManager.getInstance().showSerialDisConnectDialog();
                    viewDataBinding.watermainMessagetip.showCommonTip(MessageTipLayout.TYPE_SERIAL);
                    break;
                case CommonConstant.MSG_CHILDLOCK_SWITCH:
                    isChildLockOpen = (boolean) object;
                    int currentTemperature = mViewModel.waterTemperatureSingleLiveEvent.getValue();
                    Log.i(TAG, "registerModuleEvent: currentTemperature: " + currentTemperature);
                    updateLockStatus(currentTemperature);
                    Log.i(TAG, "registerModuleEvent: deal Child lock  " + isChildLockOpen);
                    break;
                case WaterBusEvent.MSG_PROPERTY_CUPFLOW:
                    PropertyEntity propertyEntity = (PropertyEntity) object;
                    int cupIndex = propertyEntity.getPid() - WaterPropEnum.SMALL_CUP_FLOW.piid + 1;
                    flowAmounts[cupIndex] = (int) propertyEntity.getContent();
                    break;
                case WaterBusEvent.MSG_PROPERTY_TEMPERATURE:
                    PropertyEntity temperatureProperty = (PropertyEntity) object;
                    int tempIndex = temperatureProperty.getPid();
                    // 如果是当前温度 ，更新温度模式
                    if (temperatureProperty.getSid() == WaterPropEnum.SET_TEMP.siid && temperatureProperty.getPid() == WaterPropEnum.SET_TEMP.piid) {
                        tempIndex = alludeTemperatureList.size() - 1;
                    }
                    alludeTemperatureList.set(tempIndex, (int) temperatureProperty.getContent());
                    break;
                case WaterBusEvent.MSG_PROPERTY_TEMPERATURE_CUSTM:
                    int temperature = (int) object;
                    mViewModel.waterTemperatureSingleLiveEvent.setValue(temperature);
                    changeWaterMode(temperature);
                    break;
                case WaterBusEvent.MSG_CHILDLOCK_SUCCESS:
                    startWaterOut();
                    break;
                case CommonConstant.MSG_PROPERTY_CHANGE:
                    //模拟从固件发过来属性变化,优先立马更新界面
                    PropertyEntity propertyEntity1 = (PropertyEntity) object;
                    propertyEntity1.setNotDelay(true);
                    WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyEntity1);
                    break;
                default:
                    break;
            }
        });
        addDisposable(waterDisposable);
        // 属性变化
        Disposable mainFragmentDisposable = ViomiRxBus.getInstance().subscribeUi(event -> {
            int msgId = event.getMsgId();
            Object objectValue = event.getMsgObject();
            // 杯量模式
            if (msgId == WaterBusEvent.MSG_PROPERTY_CUPMODE) {
                updateViewByProperty(MSG_FLOW_MODE_CHANGE, objectValue);
                return;
            }
            // 温度模式
            if (msgId == WaterBusEvent.MSG_PROPERTY_TEMP_MODE) {
                updateViewByProperty(MSG_TEMPERATURE_MODE_CHANGE, objectValue);
                return;
            }
            // 水质模式
            if (msgId == WaterBusEvent.MSG_PROPERTY_MINERAL_TYPE) {
                WaterQualityEnum waterQualityEnum = WaterQualityEnum.values()[(int) objectValue];
                mViewModel.waterQualitySingleLiveEvent.setValue(waterQualityEnum);
                return;
            }
            // 出水量
            if (msgId == WaterBusEvent.MSG_PROPERTY_WATER_FLOW) {
                mViewModel.currentWaterOutAmount.setValue((Integer) objectValue);
                return;
            }

            // 出水状态的改变
            if (msgId == WaterBusEvent.MSG_PROPERTY_WATER_STATUS) {
                boolean isWaterOut = (boolean) objectValue;
                Log.i(TAG, "registerModuleEvent: isWaterOut:" + isWaterOut);
                mViewModel.waterOutStatusSingleLiveEvent.setValue(isWaterOut);
                return;
            }
        });
        addDisposable(mainFragmentDisposable);
    }

    private void updateViewByProperty(int msgWhat, Object msgObject) {
        Log.i(TAG, "updateViewByProperty: " + msgWhat);
        if (waterMainHandler.hasMessages(msgWhat)) {
            waterMainHandler.removeMessages(msgWhat);
        }
        Message message = Message.obtain();
        PropertyEntity propertyEntity = (PropertyEntity) msgObject;
        message.what = msgWhat;
        message.obj = propertyEntity.getContent();
        if (propertyEntity.isNotDelay()) {
            waterMainHandler.sendMessage(message);
        } else {
            waterMainHandler.sendMessageDelayed(message, UI_UPDATE_DELAY);
        }
    }

    private void changeWaterMode(int currentTemperature) {
        Log.i(TAG, "changeWaterMode:");
        // 更新自定义模式
        int targetModeIndex = alludeTemperatureList.indexOf(currentTemperature);
        Log.i(TAG, "changeWaterMode: index: " + targetModeIndex);
        if (targetModeIndex == -1) {
            // 自定义模式
            mViewModel.temperatureModeLiveEvent.postValue(TemperatureModeEnum.CUSTOM);
        } else {
            PropertyEntity propertyEntity = new PropertyEntity(WaterPropEnum.TEMP_MODE.siid, WaterPropEnum.TEMP_MODE.piid, targetModeIndex);
            WaterSerialManager.getInstance().writeProperty(propertyEntity);
        }
    }

    /**
     * 动画
     *
     * @param cupIndex
     */
    private void changePoloAnim(int cupIndex) {
        Log.i(TAG, "changePoloAnim: cupIndex: " + cupIndex);
        String resourcePrefix = "";
        int picturesCount = ANIM_CUP_RESOURCE_COUNT;
        WaterThemeEnum waterThemeEnum = viewDataBinding.getWaterThemeEnum();
        if (THEME_ONE == waterThemeEnum) {
            resourcePrefix = ANIM_THEME_ONE_PREFIX[cupIndex];
        } else if (WaterThemeEnum.THEME_TWO == waterThemeEnum) {
            resourcePrefix = ANIM_THEME_TWO_PREFIX[cupIndex];
        } else if (WaterThemeEnum.THEME_THREE == waterThemeEnum) {
            resourcePrefix = ANIM_THEME_THREE_PREFIX[cupIndex];
        }
        if (waterPoloAnim == null) {
            waterPoloAnim = new CustomFrameAnim(viewDataBinding.watermainAmin);
        }
        if (cupIndex == WATER_OUT_ANIM_INDEX) {
            picturesCount = ANIM_WATER_OUT_COUNT;
        }
        waterPoloAnim.resetResource(resourcePrefix, picturesCount, false);
        waterPoloAnim.startAnim();
    }

    /**
     * 擦亮动画效果
     */
    private void polishesAnim() {
        viewDataBinding.whiteView.setVisibility(View.VISIBLE);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(viewDataBinding.whiteView, "translationX", -308f, 620f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(viewDataBinding.whiteView, "translationY", -240f, 500f);
        ObjectAnimator animatorYB = ObjectAnimator.ofFloat(viewDataBinding.whiteView, "scaleY", 0f, 50f, 50f, 0f);
        ObjectAnimator animatorXB = ObjectAnimator.ofFloat(viewDataBinding.whiteView, "scaleX", 0f, 50f, 50f, 0f);
        animatorX.setRepeatCount(1);
        animatorY.setRepeatCount(1);
        animatorYB.setRepeatCount(1);
        animatorXB.setRepeatCount(1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY, animatorYB, animatorXB);
        animatorSet.setDuration(2500);
        animatorSet.start();
    }

    /**
     * 更新童锁的状态
     *
     * @param currentTemperature
     */
    public void updateLockStatus(Integer currentTemperature) {
        Log.i(TAG, "getChildLock: isChildLockOpen: " + isChildLockOpen);
        if (!isChildLockOpen) {
            viewDataBinding.watermainChildlock.setVisibility(View.GONE);
            return;
        }
        if (viewDataBinding.watermainChildlock.getVisibility() != View.VISIBLE) {
            viewDataBinding.watermainChildlock.setVisibility(View.VISIBLE);
        }
        viewDataBinding.watermainChildlock.setSelected(currentTemperature >= CHILD_LOCK_TEMPERATURE);
    }

    /**
     * 显示自清洁倒计时的处理
     */
    private void showSelfCleanCountDown() {
        Log.d(TAG, "self clean---showSelfCleanCountDown: ");
        // 避免电控频发发指令，频繁执行
        if (countDownTimer != null) {
            return;
        }
        int waterMainMessageTipShow = viewDataBinding.watermainMessagetip.getVisibility();
        if (waterMainMessageTipShow == View.VISIBLE) {
            viewDataBinding.watermainMessagetip.setVisibility(View.GONE);
        }
        viewDataBinding.countdownLayout.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(CLEAN_COUNTDOWN_TIME, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                int finishTime = (int) (millisUntilFinished / ONE_SECOND);
                viewDataBinding.selfcleanCountdown.setText(finishTime + READY_CLEAN_TIP);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "self clean---showSelfCleanCountDown: countDownTimer onFinish");
                viewDataBinding.countdownLayout.setVisibility(View.GONE);
                if (waterMainMessageTipShow == View.VISIBLE) {
                    viewDataBinding.watermainMessagetip.setVisibility(View.VISIBLE);
                }
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }.start();
        Log.d(TAG, "self clean---showSelfCleanCountDown: start countDownTimer: " + countDownTimer.hashCode());
    }

    @Override
    public Class<WaterMainViewModel> onBindViewModel() {
        return WaterMainViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ProjectViewModuleFactory.getInstance(getApplication());
    }
}


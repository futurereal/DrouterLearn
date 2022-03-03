package com.viomi.ovenso.ui.activity;

import static com.viomi.ovenso.OvenConstants.MODE_TYPE_BAKE;
import static com.viomi.ovenso.OvenConstants.MODE_TYPE_MICROWAVE;
import static com.viomi.ovenso.OvenConstants.MODE_TYPE_SINGLE;
import static com.viomi.ovenso.OvenConstants.MODE_TYPE_STREAM;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.viomi.ovenso.OvenApplication;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.ModeDetailTitleEntity;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.bean.TemperatureEntity;
import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovenso.custommode.CustomeModeUtils;
import com.viomi.ovenso.helper.ModesHelper;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityModeDetailBinding;
import com.viomi.ovenso.serial.CustomModeWrite;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.ui.activity.running.CookRunningActivity;
import com.viomi.ovenso.ui.adapter.ModeDetailTitleAdapter;
import com.viomi.ovenso.ui.adapter.TemperatureListAdapter;
import com.viomi.ovenso.ui.fragment.DiscaleFragment;
import com.viomi.ovenso.util.OvenTestUtil;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovenso.util.TimeUtil;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.db.CookParamEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Description:自定义模式 、自定义菜谱、模式 详情的界面
 * 一般调整时间 和温度之后 启动
 * 规则：
 * 1、 只要有 一种模式 有 时间或者温度可调的，就是 显示那个  有选择器的界面 。
 * 2 、一种模式 或者多种模式 ，时间和温度都不可调。 显示 烹饪需时那个 比较大的界面。 如果有于是温度显示预设温度，没有预设温度 就不显示预设温度
 */
@Route(path = ViomiRouterConstant.OVENSO_MODE_DETAIL)
public class ModeDetailActivity extends BaseTitleActivity<ActivityModeDetailBinding> {
    private static final String TAG = "ModeDetailActivity";
    // 由于 普通模式和自定义模式都可能是 modeTypeList size 为1 ，所以要额外定参数
    public static final String KEY_MODE_NAME = "keyModeName";
    public static final String KEY_MODETYPE_LIST = "keyModeTypeList";
    public static final String KEY_CUSTOM_TYPE = "kyeCustomType";
    public static final String UNIT_STREAM = "℃";
    public static final String UNIT_BAKE = "℃";
    public static final String UNIT_MICROWAVE = "档";
    public static final int NONEED_TEMPREATURE = -1;
    private int cookParamIndex = 0;
    private String modeName;

    boolean isShowPicker = false;
    private ArrayList<ModeDetailTitleEntity> modeDetailTitleEntities;
    private float totalCookTime;
    // 预设温度的集合

    private ModeDetailTitleAdapter modeDetailTitleAdapter;
    private ArrayList<TemperatureEntity> temperatureEntityArrayList;
    private ArrayList<ModeTypeEntity> modeTypeEntityList;
    private ModeTypeEntity modeTypeEntity;
    private String customType;
    private int modeId;
    private List<String> microPowerNames;
    private boolean isMicroWave;
    private boolean isStartCook = false;
    private boolean isCustomType;

    @Override
    protected int getChildContentViewId() {
        return R.layout.activity_mode_detail;
    }

    @Override
    protected String getTitleName() {
        return modeName;
    }

    @Override
    protected void initIntentData() {
        modeTypeEntityList = getIntent().getParcelableArrayListExtra(KEY_MODETYPE_LIST);
        // 烹饪或者辅助模式
        modeName = getIntent().getStringExtra(KEY_MODE_NAME);
        customType = getIntent().getStringExtra(KEY_CUSTOM_TYPE);
        isCustomType = !TextUtils.isEmpty(customType);
        modeTypeEntity = modeTypeEntityList.get(0);
        modeId = modeTypeEntity.getModeId();
        Log.i(TAG, "initIntentData: modeTypeEntity " + modeTypeEntity);
        Log.i(TAG, "initIntentData: " + modeTypeEntityList.size());
        initModeData(modeTypeEntityList);
    }

    private void initModeData(ArrayList<ModeTypeEntity> modeTypeEntityList) {
        Log.i(TAG, "initModeData: isCustomMode : " + customType);
        modeDetailTitleEntities = new ArrayList<>();
        temperatureEntityArrayList = new ArrayList<>();
        // 自定义组合模式 只用显示第一个模式的图片，温度列表，烹饪时长
        if (isCustomType) {
            for (ModeTypeEntity modeTypeEntity : modeTypeEntityList) {
                CookParamEntity cookParamEntity = modeTypeEntity.getCookParamEntityList().get(0);
                int definePower = cookParamEntity.getDefineFirepower();
                TemperatureEntity temperatureEntity = new TemperatureEntity(definePower, false);
                temperatureEntityArrayList.add(temperatureEntity);
                float defineTime = cookParamEntity.getDefineTime();
                Log.i(TAG, "initModeData: defineTime: before " + defineTime);
                if (TextUtils.equals(cookParamEntity.getModeType(), MODE_TYPE_MICROWAVE)) {
                    defineTime = defineTime / 60;
                }
                Log.i(TAG, "initModeData: defineTime: after " + defineTime);
                totalCookTime = totalCookTime + defineTime;
            }
            return;
        }
        // 预置模式的处理逻辑   包含 两个模式同时修改， 单个模式修改， 不可修改的三种情况
        ArrayList<CookParamEntity> cookParamEntities = modeTypeEntity.getCookParamEntityList();
        for (int i = 0; i < cookParamEntities.size(); i++) {
            CookParamEntity cookParamEntity = cookParamEntities.get(i);
            ModeDetailTitleEntity modeDetailTitleEntity = new ModeDetailTitleEntity(cookParamEntity.getModeName(), i + 1, false);
            modeDetailTitleEntities.add(modeDetailTitleEntity);
            if (cookParamEntity.getTimeRangeMin() > 0 || cookParamEntity.getFirepowerRangeMin() > 0) {
                isShowPicker = true;
                temperatureEntityArrayList.add(new TemperatureEntity(cookParamEntity.getDefineFirepower(), false));
                continue;
            }
            int defineFirePower = cookParamEntity.getDefineFirepower();
            if (defineFirePower > 0) {
                TemperatureEntity temperatureEntity = new TemperatureEntity(defineFirePower, false);
                temperatureEntityArrayList.add(temperatureEntity);
            }
            int defineFirePowerTwo = cookParamEntity.getDefineFirepowerTwo();
            if (defineFirePowerTwo > 0) {
                TemperatureEntity temperatureEntity = new TemperatureEntity(defineFirePowerTwo, false);
                temperatureEntityArrayList.add(temperatureEntity);
            }
            totalCookTime = totalCookTime + cookParamEntity.getDefineTime();
        }
        modeDetailTitleEntities.get(0).setSelect(true);
        Log.i(TAG, "initModeData: tototalTime  " + totalCookTime);
        Log.i(TAG, "initModeData: listSize: " + temperatureEntityArrayList.size());
    }

    @Override
    protected void initChildUi() {
        // 菜谱图片
        int imgResourceId = OvenApplication.getContext().getResources().getIdentifier(modeTypeEntity.getResIdBg(),
                "drawable", getPackageName());
        childViewBinding.modedetailRecipephoto.setImageResource(imgResourceId);
        //自清洁特殊处理
        if (modeId == OvenConstants.MODE_ID_CLEAN) {
            childViewBinding.modedetailSelfcleanTip.setVisibility(View.VISIBLE);
        }
        // 预约的处理
        // 判断是否是烹饪模式,只有烹饪模式有预约
        boolean isCookMode = ModesHelper.isCookMode(modeId);
        if (isCookMode) {
            childViewBinding.includeAppoint.getRoot().setVisibility(View.VISIBLE);
        }
        if (isShowPicker) {
            updatePickerView(cookParamIndex);
            initStepTitle();
            return;
        }
        childViewBinding.groupCustomeRecipe.setVisibility(View.VISIBLE);
        String timeString = TimeUtil.getTimeHHMM((int) (totalCookTime * 60));
        childViewBinding.modedetailRecipeTime.setText(timeString);
        TemperatureListAdapter temperatureListAdapter = new TemperatureListAdapter(temperatureEntityArrayList);
        childViewBinding.modedetailTargetTemperature.setAdapter(temperatureListAdapter);
    }

    private void initStepTitle() {
        int cookParamSize = modeTypeEntity.getCookParamEntityList().size();
        if (isCustomType || cookParamSize == 1) {
            Log.i(TAG, "initStepTitle: cookParamSize  size  1 return ");
            return;
        }
        childViewBinding.recyclerviewModedetailTitle.setVisibility(View.VISIBLE);
        modeDetailTitleAdapter = new ModeDetailTitleAdapter();
        modeDetailTitleAdapter.setTitleList(modeDetailTitleEntities);
        childViewBinding.recyclerviewModedetailTitle.setAdapter(modeDetailTitleAdapter);
        initAdapterLister();
    }

    private void initAdapterLister() {
        modeDetailTitleAdapter.setOnItemClickListener((parent, view, position, id) -> {
            cookParamIndex = position;
            updatePickerView(cookParamIndex);
        });
        modeDetailTitleAdapter.setOnItemLongClickListener((parent, view, position, id) -> false);
    }

    /**
     * 开始 和预约的点击事件
     * 连续点击的判断
     */
    @Override
    public void initListener() {
        Log.i(TAG, "initListener: ");
        childViewBinding.includeStart.getRoot().setOnClickListener(v -> {
            boolean isClose = (boolean) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.WATERTANK_ISCLOSE.siid, OvenPropEnum.WATERTANK_ISCLOSE.piid, false);
            if (TextUtils.equals(MODE_TYPE_STREAM, modeTypeEntity.getModeType()) && !isClose) {
                ViomiToastUtil.showToastCenter(getString(R.string.error_water_tank_close_content));
                return;
            }
            // 判断是否为除垢,显示除垢对话框
            if (modeId == OvenConstants.MODE_ID_CHUGOU) {
                DiscaleFragment discalingFragment = new DiscaleFragment();
                discalingFragment.show(getSupportFragmentManager(), "discalingFragment");
                return;
            }
            startCook();
        });

        childViewBinding.includeAppoint.getRoot().setOnClickListener(v -> {
            List<PropertyEntity> propertyEntityList = getPropertyList();
            OvenUtil.showAppointFragment(totalCookTime, OvenConstants.DISHID_NO_RECIPE, propertyEntityList);
        });
        childViewBinding.modedetailTimerpicker.setOnSelectListener((view, selected) -> {
            float defineTime = isMicroWave ? Float.parseFloat(selected.first) : Integer.parseInt(selected.first);
            Log.i(TAG, "initListener: defineTime : " + defineTime);
            modeTypeEntity.getCookParamEntityList().get(cookParamIndex).setDefineTime(defineTime);
        });
        childViewBinding.modedetailTemperaturepicker.setOnSelectListener((view, selected) -> {
            int defineFirePower = isMicroWave ? microPowerNames.indexOf(selected.first) + 1 : Integer.parseInt(selected.first);
            Log.i(TAG, "initListener: defineFirePower: " + defineFirePower);
            modeTypeEntity.getCookParamEntityList().get(cookParamIndex).setDefineFirepower(defineFirePower);
        });

        // 监听启动
        Disposable customeModeDisposable = ViomiRxBus.getInstance().subscribeUi(busEvent -> {
            switch (busEvent.getMsgId()) {
                case OvenBusEventConstants.MSG_COOK_STATUSCHANGE:
                    if (!isActivityResumed) {
                        Log.i(TAG, "initListener: isActivityResume false");
                        return;
                    }
                    int statusEnumValue = (int) busEvent.getMsgObject();
                    Log.i(TAG, "initListener: statusEnumValue: " + statusEnumValue);
                    if (statusEnumValue != OvenWorkStatusEnum.WORKING.value && statusEnumValue != OvenWorkStatusEnum.BOOKED.value) {
                        Log.i(TAG, "initListener: not start");
                        return;
                    }
                    Log.i(TAG, "initListener: startCookRunning " + statusEnumValue + "  totalTime: " + totalCookTime);
                    ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_COOK_RUNNING)
                            .withString(CookRunningActivity.KEY_RECIPENAME, modeName)
                            .withInt(CookRunningActivity.KEY_STAUSENUMS_VALUE, statusEnumValue)
                            .withParcelableArrayList(CookRunningActivity.KEY_MODETYPE_LIST, modeTypeEntityList)
                            .navigation();
                    break;
                case CommonConstant.MSG_DOWNWRITE_SUCCESS:
                    // 非烹饪的 写入指令成功 不调用这个逻辑
                    if (!isStartCook) {
                        Log.i(TAG, "initListener: isBook");
                        return;
                    }
                    OvenSerialManager.getInstance().doStandardAction(OvenActionEnum.ACTION_START);
                    isStartCook = false;
                    break;
                case OvenBusEventConstants.MSG_START_COOK:
                    startCook();
                    break;
            }
        });
        addDisposable(customeModeDisposable);
    }

    @Override
    protected void initData() {
    }

    private void startCook() {
        Log.i(TAG, "startCook: startCook");
        isStartCook = true;
        ViomiToastUtil.showToastNormal(getString(R.string.oven_cookparam_launching), Toast.LENGTH_SHORT);
        List<PropertyEntity> propertyList = getPropertyList();
        OvenSerialManager.getInstance().writePropertyList(propertyList);
        OvenTestUtil.testCookingUI(OvenWorkStatusEnum.WORKING);
    }

    /**
     * 更新模式的的界面
     */
    private void updatePickerView(int cookParamIndex) {
        CookParamEntity currentCookParaEntity = modeTypeEntity.getCookParamEntityList().get(cookParamIndex);
        String currentModeType = currentCookParaEntity.getModeType();
        Log.i(TAG, "updateCurrentModeView: modeType: " + currentModeType);
        if (TextUtils.equals(currentModeType, MODE_TYPE_STREAM)) {
            childViewBinding.modedetailTimerpicker.setCenterTextColor(Color.parseColor("#28BECA"));
            childViewBinding.modedetailTemperaturepicker.setCenterTextColor(Color.parseColor("#28BECA"));
            childViewBinding.modedetailTemperaturepicker.setUnitText(UNIT_STREAM);
            isMicroWave = false;
        } else if (TextUtils.equals(currentModeType, MODE_TYPE_BAKE)) {
            childViewBinding.modedetailTimerpicker.setCenterTextColor(Color.parseColor("#FF6926"));
            childViewBinding.modedetailTemperaturepicker.setCenterTextColor(Color.parseColor("#FF6926"));
            childViewBinding.modedetailTemperaturepicker.setUnitText(UNIT_BAKE);
            isMicroWave = false;
        } else if (TextUtils.equals(currentModeType, MODE_TYPE_MICROWAVE)) {
            isMicroWave = true;
            childViewBinding.modedetailTimerpicker.setCenterTextColor(Color.parseColor("#FF6926"));
            childViewBinding.modedetailTemperaturepicker.setCenterTextColor(Color.parseColor("#FF6926"));
        }

        float defineTime = currentCookParaEntity.getDefineTime();
        float timeRangeMax = currentCookParaEntity.getTimeRangeMax();
        float timeRangeMin = currentCookParaEntity.getTimeRangeMin();
        Log.i(TAG, "updateCurrentModeView: " + defineTime + "  " + timeRangeMax + "   " + timeRangeMin);
        if (timeRangeMax <= timeRangeMin) {
            childViewBinding.modedetailTimerpicker.setVisibility(View.GONE);
        } else {
            childViewBinding.modedetailTimerpicker.setVisibility(View.VISIBLE);
            if (isMicroWave) {
                childViewBinding.modedetailTimerpicker.setFloatData(defineTime, timeRangeMin, timeRangeMax, OvenConstants.MICRO_TIME_STEP);
            } else {
                childViewBinding.modedetailTimerpicker.setIntData((int) defineTime, (int) timeRangeMin, (int) timeRangeMax);
            }
        }
        int defineFirePower = currentCookParaEntity.getDefineFirepower();
        int powerRangeMax = currentCookParaEntity.getFirepowerRangeMax();
        int powerRangeMin = currentCookParaEntity.getFirepowerRangeMin();
        Log.i(TAG, "updateCurrentModeView: " + defineFirePower + "  " + powerRangeMax + "   " + powerRangeMin);
        if (powerRangeMax > powerRangeMin) {
            if (TextUtils.equals(currentModeType, MODE_TYPE_MICROWAVE)) {
                microPowerNames = OvenUtil.getMicroPowerNames();
                childViewBinding.modedetailTemperaturepicker.setStringData(microPowerNames, defineFirePower);
            } else {
                childViewBinding.modedetailTemperaturepicker.setIntData(defineFirePower, powerRangeMin, powerRangeMax);
            }
            childViewBinding.modedetailTemperaturepicker.setVisibility(View.VISIBLE);
        } else {
            childViewBinding.modedetailTemperaturepicker.setVisibility(View.GONE);
        }
    }

    private List<PropertyEntity> getPropertyList() {
        String modeType = modeTypeEntity.getModeType();
        ArrayList<PropertyEntity> propertyEntities = new ArrayList<>();
        // 自定义模式 传 模式id ，菜谱 id 为 0  和字符串
        if (TextUtils.equals(modeType, CustomeModeUtils.CUSTOM_NAME_MODE)) {
            PropertyEntity modeIdProperty = new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, modeTypeEntity.getModeId());
            propertyEntities.add(modeIdProperty);
            PropertyEntity dishProperty = new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, OvenConstants.DISHID_NO_RECIPE);
            propertyEntities.add(dishProperty);
            String mcuProprety = CustomeModeUtils.getMucProperty(modeType, modeTypeEntity.getModeId());
            PropertyEntity propertyEntity = new PropertyEntity(CustomModeWrite.CUTOMMODE_MUC_SIID, CustomModeWrite.CUTOMMODE_MUC_PID, mcuProprety);
            propertyEntities.add(propertyEntity);
            Log.i(TAG, "getPropertyList: customMode:  mcuProperty: " + propertyEntity);
            return propertyEntities;
        }
        // 自定义菜谱  需要传菜谱id  和字符串
        if (TextUtils.equals(modeType, CustomeModeUtils.CUSTOM_NAME_RECIPE)) {
            PropertyEntity dishIdProperty = new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, modeTypeEntity.getModeId());
            propertyEntities.add(dishIdProperty);
            String mcuProprety = CustomeModeUtils.getMucProperty(modeType, modeTypeEntity.getModeId());
            PropertyEntity propertyEntity = new PropertyEntity(CustomModeWrite.CUTOMMODE_MUC_SIID, CustomModeWrite.CUTOMMODE_MUC_PID, mcuProprety);
            Log.i(TAG, "getPropertyList: customeRecipe:  mcuProperty: " + propertyEntity);
            propertyEntities.add(propertyEntity);
            return propertyEntities;
        }
        // 非组合模式
        PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, modeTypeEntity.getModeId());
        propertyEntities.add(propertyEntity);
        PropertyEntity dishProperty = new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, OvenConstants.DISHID_NO_RECIPE);
        propertyEntities.add(dishProperty);
        totalCookTime = 0;
        for (CookParamEntity cookParamEntity : modeTypeEntity.getCookParamEntityList()) {
            List<PropertyEntity> timeAndPowerList = getTimeAndPowerProperty(cookParamEntity);
            if (timeAndPowerList != null) {
                Log.i(TAG, "startMode: timeAndPowerList: " + timeAndPowerList.size());
                propertyEntities.addAll(propertyEntities.size(), timeAndPowerList);
            }
        }
        return propertyEntities;
    }

    private List<PropertyEntity> getTimeAndPowerProperty(CookParamEntity cookParamEntity) {
        Log.i(TAG, "getTimeAndPowerProperty: " + cookParamEntity);
        String childModeType = cookParamEntity.getModeType();
        if (TextUtils.equals(childModeType, MODE_TYPE_SINGLE)) {
            Log.i(TAG, "getTimeAndPowerProperty: no nead parameter return");
            return null;
        }
        List<PropertyEntity> timeAndPowerList = new ArrayList<>();
        int siidTime = 0;
        int piidTime = 0;
        int siidFirePower = 0;
        int piidFirePower = 0;
        // 子模式id 也要传递 分钟
        float defineTimeMinute = cookParamEntity.getDefineTime();
        float definteTime = defineTimeMinute;
        if (TextUtils.equals(childModeType, MODE_TYPE_STREAM)) {
            siidTime = OvenPropEnum.TIMEZ.siid;
            piidTime = OvenPropEnum.TIMEZ.piid;
            siidFirePower = OvenPropEnum.TEMPZ.siid;
            piidFirePower = OvenPropEnum.TEMPZ.piid;
        } else if (TextUtils.equals(childModeType, MODE_TYPE_BAKE)) {
            siidTime = OvenPropEnum.TIMEK.siid;
            piidTime = OvenPropEnum.TIMEK.piid;
            siidFirePower = OvenPropEnum.TEMPK.siid;
            piidFirePower = OvenPropEnum.TEMPK.piid;
        } else if (TextUtils.equals(childModeType, MODE_TYPE_MICROWAVE)) {
            siidTime = OvenPropEnum.MICRO_TIME.siid;
            piidTime = OvenPropEnum.MICRO_TIME.piid;
            siidFirePower = OvenPropEnum.MICRO_LEVEL.siid;
            piidFirePower = OvenPropEnum.MICRO_LEVEL.piid;
            // 微波的指令必须传 秒，其他传分钟
            definteTime = defineTimeMinute * 60;
        }
        totalCookTime = totalCookTime + defineTimeMinute;
        // 由于属性值必须是 int
        int intDefineTime = (int) definteTime;
        PropertyEntity propertyEntity = new PropertyEntity(siidTime, piidTime, intDefineTime);
        Log.i(TAG, "getTimeAndPowerProperty:defineTime " + propertyEntity);
        timeAndPowerList.add(propertyEntity);
        int defineFirePowerMax = cookParamEntity.getFirepowerRangeMax();
        if (defineFirePowerMax != NONEED_TEMPREATURE) {
            int definePower = cookParamEntity.getDefineFirepower();
            if (cookParamEntity.getModeId() == OvenConstants.MODE_ID_MICRWAVE_STREAM) {
                definePower = cookParamEntity.getDefineFirepowerTwo();
            }
            propertyEntity = new PropertyEntity(siidFirePower, piidFirePower, definePower);
            Log.i(TAG, "getTimeAndPowerProperty: definePower " + propertyEntity);
            timeAndPowerList.add(propertyEntity);
        }
        return timeAndPowerList;
    }

}


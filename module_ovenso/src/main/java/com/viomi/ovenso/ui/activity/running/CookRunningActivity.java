package com.viomi.ovenso.ui.activity.running;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.ovenso.OvenApplication;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.PropertyUtil;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.bean.OvenRecipeStepEnum;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.bean.TemperatureEntity;
import com.viomi.ovenso.helper.ModesHelper;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityCookRunningBinding;
import com.viomi.ovenso.ui.activity.OvenBasePresenterActivity;
import com.viomi.ovenso.ui.adapter.TemperatureListAdapter;
import com.viomi.ovenso.ui.fragment.DeodorizationFragment;
import com.viomi.ovenso.ui.fragment.DiscaleFragment;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovenso.util.TimeUtil;
import com.viomi.ovensocommon.CommonAffirmFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.camera.CameraServiceFactory;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.db.CookParamEntity;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.ovensocommon.utils.MediaPlayerUtils;
import com.viomi.ovensocommon.utils.VideoFileUtils;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/11/12.
 * Description:???????????????????????????
 */
@Route(path = ViomiRouterConstant.OVENSO_COOK_RUNNING)
public class CookRunningActivity extends OvenBasePresenterActivity<ActivityCookRunningBinding, CookRunningPresenter> implements RunContract.View {
    private static final String TAG = "CookRunningActivity";
    public static final String KEY_RECIPENAME = "keyRecipeName";
    public static final String KEY_MODETYPE_LIST = "keyModeTypeList";
    public static final String KEY_STAUSENUMS_VALUE = "keyCookTypeValue";
    public static final String MODE_TYPE_STREAM = "???";
    private static final int MODESTEP_CHUGOU_ONE = 1;
    private static final int MODESTEP_CHUGOU_TWO = 2;
    private static final int MODESTEP_CHUWEI_ONE = 3;
    private static final int MODESTEP_WEIBOZHENG_ONE = 4;
    private static final int MODESTEP_WEIZHENGKAO_ONE = 5;
    private static final int MODESTEP_WEIZHENGKAO_TWO = 6;
    private static final int MODESTEP_GAOWENXIAODU_ONE = 7;
    private PropertyPreferenceManager propertPreference;
    private String recipeName;
    private int modeId;
    private int recipePhoteRes;
    private ArrayList<TemperatureEntity> temperatureEntityArrayList;
    private ArrayList<ModeTypeEntity> modeTypeList;
    private int statusEnumValue;
    // ???????????????
    private float cookTotalTime;
    private boolean isBook;
    private int currentRecordStatus = CommonConstant.RECORD_STATE_FINISH;
    private CommonAffirmFragment stopConfrimFragment;
    // ???????????????1??? index ???0
    private TemperatureListAdapter temperatureListAdapter;
    private DiscaleFragment discalingFragment;
    private boolean isStreamOrMicro = false;
    private AlphaAnimation alphaAnimation;
    // ??????????????????
    private boolean isCompletion = false;
    private int currentSelectIndex;

    @Override
    protected int getChildContentViewId() {
        keepScreenOn();
        return R.layout.activity_cook_running;
    }

    @Override
    protected void initIntentData() {
        recipeName = getIntent().getStringExtra(KEY_RECIPENAME);
        // ?????????????????????????????????
        statusEnumValue = getIntent().getIntExtra(KEY_STAUSENUMS_VALUE, 0);
        // ????????????
        isBook = statusEnumValue == OvenWorkStatusEnum.BOOKED.value;
        Log.i(TAG, "initIntentData: isBook " + isBook);
        modeTypeList = getIntent().getParcelableArrayListExtra(KEY_MODETYPE_LIST);
        Log.i(TAG, "initIntentData: mdoeTypeListSize: " + modeTypeList.size());
        initAllData(modeTypeList);
        Log.i(TAG, "initIntentData: recipeName: " + recipeName + "  modeId: " + modeId + " statusEnumValue " + statusEnumValue + " cookTotalTime: " + cookTotalTime);
        Log.i(TAG, "initIntentData: tempearatureList: " + (temperatureEntityArrayList != null ? temperatureEntityArrayList.size() : 0));
    }

    @Override
    protected void initChildUi() {
        basePresenter.setStatusEnumValue(statusEnumValue);
        propertPreference = PropertyPreferenceManager.getInstance();
        // ??????????????????
        setImgBackVisibility(View.GONE);
        updateWorkStatus(statusEnumValue);
        // ????????????
        int currentTemperature = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.TEMPER.siid, OvenPropEnum.TEMPER.piid, 0);
        PropertyUtil.currentTemperature = currentTemperature;
        updateCurrentTemperature(currentTemperature);
        // ????????????
        temperatureEntityArrayList.get(0).setSlected(true);
        temperatureListAdapter = new TemperatureListAdapter(temperatureEntityArrayList);
        childViewBinding.cookrunTargetTemperature.setAdapter(temperatureListAdapter);
        dealTemperatureVisilbe(0);
        // ??????
        childViewBinding.cookrunRecipephoto.setImageResource(recipePhoteRes);
        // ????????????   ???????????? ???????????????
        int recipeStep = (int) propertPreference.getProperty(OvenPropEnum.RECIPE_STEP.siid, OvenPropEnum.RECIPE_STEP.piid, 0);
        Log.i(TAG, "initChildUi: recipeStep: " + recipeStep);
        boolean isPreheat = recipeStep == OvenRecipeStepEnum.STEP_PREHEARTING.stepValue;
        Log.i(TAG, "initChildUi: isPreheat: " + isPreheat);
        // ??????   ????????? ???????????????????????? ??????????????? ?????? ??????????????????????????????
        if (statusEnumValue == OvenWorkStatusEnum.WORKING.value) {
            updateLeftTime((int) cookTotalTime);
        }
        // ??????
        if (isBook) {
            childViewBinding.includePauseContinue.getRoot().setVisibility(View.GONE);
        }
        if (CommonConstant.SHOW_DEBUG_INFO) {
            childViewBinding.cookrunTestView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String getTitleName() {
        return recipeName;
    }

    @Override
    public void initListener() {
        Log.i(TAG, "initListener: ");
        propertPreference = PropertyPreferenceManager.getInstance();
        childViewBinding.includePauseContinue.getRoot().setOnClickListener(v -> {
            boolean isDoorClose = (boolean) propertPreference.getProperty(OvenPropEnum.DOOR_OPEN.siid, OvenPropEnum.DOOR_OPEN.piid, false);
            if (!isDoorClose) {
                ViomiToastUtil.showToastNormal(getString(R.string.over_recipedetail_dooropen), Toast.LENGTH_SHORT);
                return;
            }
            Log.i(TAG, "onClick: currentWorkStatusType : " + statusEnumValue);
            if (statusEnumValue == OvenWorkStatusEnum.WORKING.value) {
                basePresenter.cmdRunOpt(OvenActionEnum.ACTION_PAUSE);
            } else {
                basePresenter.cmdRunOpt(OvenActionEnum.ACTION_START);
            }
        });
        childViewBinding.includeOverFinish.getRoot().setOnClickListener(v -> {
            // ????????????????????????  ????????????
            if (OvenConstants.IS_TEST_UI || statusEnumValue == OvenWorkStatusEnum.COMPLETED.value
                    || statusEnumValue == OvenWorkStatusEnum.IDLE.value) {
                finish();
                return;
            }
            showStopConfimFragment();
        });
        childViewBinding.includeRecord.getRoot().setOnClickListener(v -> {
            // ???????????? ????????? ???????????????
            if (isStreamOrMicro) {
                ViomiToastUtil.showToastCenter(getString(R.string.cookrunning_not_record));
                return;
            }
            boolean isBind = ModuleSettingServiceFactory.getInstance().getViotService().isDeviceBind();
            if (!isBind) {
                ViomiToastUtil.showToastCenter(getString(R.string.cookrunning_not_bind));
                return;
            }
            // ?????????????????? + ??????????????????
            if (statusEnumValue != OvenWorkStatusEnum.WORKING.value) {
                ViomiToastUtil.showToastCenter(getString(R.string.cookrunning_notcook));
                return;
            }
            // ????????????????????????????????????
            startCameraActivity();
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent: ");
        showEidtFragment();
    }

    private void initAllData(ArrayList<ModeTypeEntity> modeTypeList) {
        temperatureEntityArrayList = new ArrayList<>();
        modeId = modeTypeList.get(0).getCookParamEntityList().get(0).getModeId();
        recipePhoteRes = OvenApplication.getContext().getResources().getIdentifier(modeTypeList.get(0).getResIdBg(),
                "drawable", getPackageName());
        // ?????????
        if (modeTypeList.size() == 1) {
            ModeTypeEntity modeTypeEntity = modeTypeList.get(0);
            List<CookParamEntity> cookParamEntityList = modeTypeEntity.getCookParamEntityList();
            for (CookParamEntity cookParamEntity : cookParamEntityList) {
                Log.i(TAG, "initAllData: singleMode: " + cookParamEntity);
                initCookType(cookParamEntity);
                int definePowerOne = cookParamEntity.getDefineFirepower();
                int definePowerTwo = cookParamEntity.getDefineFirepowerTwo();
                TemperatureEntity temperatureEntity = new TemperatureEntity(definePowerOne, false);
                temperatureEntityArrayList.add(temperatureEntity);
                if (definePowerTwo > 0) {
                    temperatureEntity = new TemperatureEntity(definePowerTwo, false);
                    temperatureEntityArrayList.add(temperatureEntity);
                }
                float defineTime = cookParamEntity.getDefineTime() * CommonConstant.ONE_MINITE_SECOND;
                cookTotalTime = cookTotalTime + defineTime;
            }
            return;
        }
        // ????????????
        for (ModeTypeEntity modeTypeEntity : modeTypeList) {
            CookParamEntity cookParamEntity = modeTypeEntity.getCookParamEntityList().get(0);
            Log.i(TAG, "initAllData: combineMode: " + cookParamEntity);
            initCookType(cookParamEntity);
            int definePowerOne = cookParamEntity.getDefineFirepower();
            TemperatureEntity temperatureEntity = new TemperatureEntity(definePowerOne, false);
            temperatureEntityArrayList.add(temperatureEntity);
            float defineTime = cookParamEntity.getDefineTime() * CommonConstant.ONE_MINITE_SECOND;
            cookTotalTime = cookTotalTime + defineTime;
        }
        Log.i(TAG, "initAllData: cookTotalTime: " + cookTotalTime);
    }

    private void initCookType(CookParamEntity cookParamEntity) {
        String modeType = cookParamEntity.getModeType();
        if (TextUtils.isEmpty(modeType) == false && (modeType.contains(OvenConstants.MODE_TYPE_MICROWAVE) || modeType.contains(OvenConstants.MODE_TYPE_STREAM))) {
            isStreamOrMicro = true;
        }
    }

    private void showStopConfimFragment() {
        Log.i(TAG, "showConfirmDialog: " + isBook);
        String dialogTip;
        String dialogContent;
        if (isBook) {
            dialogTip = getString(R.string.oven_end_order);
            dialogContent = getString(R.string.oven_dialog_content_order);
        } else {
            dialogTip = getString(R.string.oven_dialog_title_cooking);
            dialogContent = getString(R.string.oven_dialog_content_cooking);
        }
        stopConfrimFragment = new CommonAffirmFragment();
        Bundle bundle = CommonAffirmFragment.getBundle(dialogTip, dialogContent,
                getString(R.string.oven_dialog_cancel), getString(R.string.oven_dialog_sure),
                false, 0);
        stopConfrimFragment.setArguments(bundle);
        stopConfrimFragment.setPositiveClickListener(dialog1 -> {
            OvenActionEnum ovenActionEnum = isBook ? OvenActionEnum.ACTION_CANCLE_PREPARE : OvenActionEnum.ACTION_OVER;
            basePresenter.cmdRunOpt(ovenActionEnum);
            Log.i(TAG, "showStopConfimFragment: " + ovenActionEnum);
            dialog1.dismiss();
        });
        stopConfrimFragment.show(getSupportFragmentManager(), CommonAffirmFragment.class.getSimpleName());
    }

    /**
     * ??????????????????
     */
    @Override
    public void updateWorkStatus(int workStatusValue) {
        this.statusEnumValue = workStatusValue;
        Log.i(TAG, "updateWorkStatus workStatus: " + workStatusValue);
        OvenWorkStatusEnum workStatus = OvenWorkStatusEnum.values()[workStatusValue];
        childViewBinding.cookrunWorkstatus.setText(workStatus.name);
        updateAnimi(workStatus);
        // ?????????????????? ????????? ?????????
        if (stopConfrimFragment != null && stopConfrimFragment.getDialog() != null && stopConfrimFragment.getDialog().isShowing()) {
            stopConfrimFragment.dismiss();
        }
        // ?????????????????????????????????????????????????????????
        int recipeStep = (int) propertPreference.getProperty(OvenPropEnum.RECIPE_STEP.siid, OvenPropEnum.RECIPE_STEP.piid, 0);
        Log.i(TAG, "updateWorkStatus: recipeStep: " + recipeStep);
        boolean isPreheat = recipeStep == OvenRecipeStepEnum.STEP_PREHEARTING.stepValue;
        // ?????????????????????
        if (workStatus.value == OvenWorkStatusEnum.BOOKED.value) {
            childViewBinding.includePauseContinue.pausecontinueTip.setText(R.string.ovenso_pause);
            childViewBinding.includePauseContinue.pausecontinueIcon.setImageResource(R.drawable.icon_pause);
            return;
        }
        if (workStatus.value == OvenWorkStatusEnum.WORKING.value) {
            // ????????????
            if (isPreheat) {
                childViewBinding.cookrunWorkstatus.setText(OvenRecipeStepEnum.STEP_PREHEARTING.stepResourceId);
            }
            childViewBinding.includePauseContinue.pausecontinueTip.setText(R.string.ovenso_pause);
            childViewBinding.includePauseContinue.pausecontinueIcon.setImageResource(R.drawable.icon_pause);
            CameraServiceFactory.getInstance().getCameraService().updateRecordStatus(CommonConstant.RECORD_STATE_RECORDING);
            return;
        }
        if (workStatus.value == OvenWorkStatusEnum.PAUSE.value) {
            // ????????????
            if (isPreheat) {
                childViewBinding.cookrunWorkstatus.setText(R.string.cookrun_preheat_pause);
            }
            childViewBinding.includePauseContinue.pausecontinueTip.setText(R.string.oven_continue);
            childViewBinding.includePauseContinue.pausecontinueIcon.setImageResource(R.drawable.icon_start);
            CameraServiceFactory.getInstance().getCameraService().updateRecordStatus(CommonConstant.RECORD_STATE_PAUSE);
            return;
        }
        if (workStatusValue == OvenWorkStatusEnum.COMPLETED.value) {
            childViewBinding.includePauseContinue.pausecontinueIcon.setImageResource(R.drawable.icon_start);
            childViewBinding.includePauseContinue.pausecontinueTip.setText(R.string.ovenso_start);
            isCompletion = true;
            int voidResourceId;
            if (ModesHelper.isAssistMode(modeId)) {
                voidResourceId = R.raw.assist_completion;
            } else {
                voidResourceId = R.raw.cook_completion;
            }
            MediaPlayerUtils.getInstance().startPlayRawResource(voidResourceId);
            String topActivity = ActivityUtils.getTopActivity().getClass().getName();
            Log.i(TAG, "dealRecordStatus:topActivity:  " + topActivity);
            if (TextUtils.equals(topActivity, CookRunningActivity.class.getName())) {
                showCompeleteFragment();
            }
            CameraServiceFactory.getInstance().getCameraService().updateRecordStatus(CommonConstant.RECORD_STATE_FINISH);
            return;
        }
        // ???????????????????????????
        if (workStatusValue == OvenWorkStatusEnum.IDLE.value) {
            // ????????? ???????????????idle ???????????????
            if (isCompletion) {
                Log.i(TAG, "updateWorkStatus  idle  cookCompletion return");
                return;
            }
            Log.i(TAG, "updateWorkStatus  currentRecordStatus:" + currentRecordStatus);
            childViewBinding.includePauseContinue.pausecontinueIcon.setImageResource(R.drawable.icon_start);
            childViewBinding.includePauseContinue.pausecontinueTip.setText(R.string.ovenso_start);
            if (currentRecordStatus == CommonConstant.RECORD_STATE_FINISH) {
                finish();
            }
            CameraServiceFactory.getInstance().getCameraService().updateRecordStatus(CommonConstant.RECORD_STATE_FINISH);
            return;
        }
    }

    private void updateAnimi(OvenWorkStatusEnum workStatus) {
        childViewBinding.cookrunAnimIn.setVisibility(workStatus.vRotate1);
        childViewBinding.cookrunAnimOut.setVisibility(workStatus.vRotate2);
        childViewBinding.cookrunAnimIn.rotate(workStatus == OvenWorkStatusEnum.WORKING || workStatus == OvenWorkStatusEnum.BOOKED);
        childViewBinding.cookrunAnimOut.rotate(workStatus == OvenWorkStatusEnum.WORKING);
    }

    @Override
    public void updateCurrentTemperature(int currrentTemperature) {
        Log.i(TAG, "updateCurrentTemperature: " + currrentTemperature);
        String temperatureStr = "/" + currrentTemperature;
        childViewBinding.cookrunTemperatureNow.setText(temperatureStr);
    }

    /**
     * ???????????????????????? ????????????  ????????? ????????????
     * ???????????????
     *
     * @param time ??????
     */
    public void updateLeftTime(int time) {
        Log.i(TAG, "updateTime: time: " + time);
        if (time >= CommonConstant.ONE_MINITE_SECOND) {
            updateTimeSize((130));
            childViewBinding.cookrunLefttimeMinutesplite.setVisibility(View.GONE);
            childViewBinding.cookrunLefttimeSecond.setVisibility(View.GONE);
            childViewBinding.cookrunLefttimeHour.setText(TimeUtil.getHour((time)));
            childViewBinding.cookrunLefttimeMinute.setText(TimeUtil.getMin((time)));
            startAlphaAnim();
            return;
        }
        // ?????????????????? 1 ???????????? ???????????????
        if (time >= 0) {
            updateTimeSize((118));
            childViewBinding.cookrunLefttimeHour.setText(TimeUtil.getHour((time)));
            childViewBinding.cookrunLefttimeMinute.setText(TimeUtil.getMin((time)));
            childViewBinding.cookrunLefttimeSecond.setText(TimeUtil.getSecond((time)));
            childViewBinding.cookrunLefttimeMinutesplite.setVisibility(View.VISIBLE);
            childViewBinding.cookrunLefttimeSecond.setVisibility(View.VISIBLE);
        }
    }

    private void updateTimeSize(int textSize) {
        childViewBinding.cookrunLefttimeHour.setTextSize((textSize));
        childViewBinding.cookrunLefttimeHoursplit.setTextSize((textSize));
        childViewBinding.cookrunLefttimeMinute.setTextSize((textSize));
        childViewBinding.cookrunLefttimeMinutesplite.setTextSize((textSize));
        childViewBinding.cookrunLefttimeSecond.setTextSize((textSize));
    }

    private void startAlphaAnim() {
        alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setRepeatCount(2);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        childViewBinding.cookrunLefttimeHoursplit.startAnimation(alphaAnimation);
    }

    @Override
    public void startCameraActivity() {
        // ???????????????
        Log.i(TAG, "dealRecordStatus: startCameraActivity");
        ViomiRouter.getInstance().build(ViomiRouterConstant.OVENS0_CAMERA)
                .withString(ViomiRouterConstant.CAMERA_KEY_RECIPENAME, recipeName)
                .withBoolean(ViomiRouterConstant.CAMERA_KEY_COOKING, true)
                .withString(ViomiRouterConstant.CAMERA_KEY_MODEID, String.valueOf(modeId))
                .navigation(CookRunningActivity.this);
    }

    @Override
    public void updateRecordStatus(int recordStatus) {
        Log.i(TAG, "dealRecordStatus: record: " + recordStatus);
        currentRecordStatus = recordStatus;
        int recordStatusTipId = 0;
        int recordStatusDrawableId = 0;
        if (recordStatus == CommonConstant.RECORD_STATE_RECORDING) {
            recordStatusTipId = R.string.cookrunning_recording;
            recordStatusDrawableId = R.drawable.cookrun_recording;
        } else if (recordStatus == CommonConstant.RECORD_STATE_PAUSE) {
            recordStatusTipId = R.string.cookrunning_recording_pause;
            recordStatusDrawableId = R.drawable.cookrun_record_pause;
        } else if (recordStatus == CommonConstant.RECORD_STATE_FINISH) {
            recordStatusTipId = R.string.cookrunning_recording_start;
            recordStatusDrawableId = R.drawable.cookrun_record_stop;
        }
        childViewBinding.includeRecord.includeRecordTip.setText(recordStatusTipId);
        childViewBinding.includeRecord.includeRecordIcon.setImageResource(recordStatusDrawableId);
    }

    @Override
    public void finishSelft() {
        Log.i(TAG, "finishSelft: finishSelft : " + statusEnumValue);
        if (statusEnumValue == OvenWorkStatusEnum.IDLE.value || statusEnumValue == OvenWorkStatusEnum.COMPLETED.value) {
            Log.i(TAG, "finishSelft: finshiSelft");
            finish();
        }
    }

    @Override
    public void updateCookStep(int customStep) {
        Log.i(TAG, "updateCookStep: customStep: " + customStep);
        currentSelectIndex = customStep;
        // ????????????????????? ????????? ????????????????????????
        temperatureEntityArrayList.get(customStep - 1).setSlected(false);
        temperatureEntityArrayList.get(customStep).setSlected(true);
        temperatureListAdapter.notifyDataSetChanged();
        dealTemperatureVisilbe(customStep);
        if (customStep - 1 >= modeTypeList.size()) {
            Log.i(TAG, "updateCookStep: single return");
            return;
        }
        // ???????????????????????????
        ModeTypeEntity currentModeTypeEntity = modeTypeList.get(customStep - 1);
        recipePhoteRes = OvenApplication.getContext().getResources().getIdentifier(currentModeTypeEntity.getResIdBg(),
                "drawable", getPackageName());
        childViewBinding.cookrunRecipephoto.setImageResource(recipePhoteRes);
    }

    /**
     * ??????????????????????????????
     *
     * @param customStep
     */
    private void dealTemperatureVisilbe(int customStep) {
        // ???????????????????????? ?????????????????????10??? ???????????????????????? ??????10 ???
        int definePower = temperatureEntityArrayList.get(customStep).getTemperature();
        Log.i(TAG, "dealTemperatureVisilbe: definePower: " + definePower);
        if (definePower > 10) {
            childViewBinding.cookrunTemperatureNow.setVisibility(View.VISIBLE);
            childViewBinding.cookrunTemperatureUnit.setVisibility(View.VISIBLE);
        } else {
            childViewBinding.cookrunTemperatureNow.setVisibility(View.GONE);
            childViewBinding.cookrunTemperatureUnit.setVisibility(View.GONE);
        }
    }

    /**
     * ??????  ??? ?????? ?????????????????????
     *
     * @param modeStep ??????
     */
    @Override
    public void updateModeStep(int modeStep) {
        Log.i(TAG, "updateModeStep: modeStep: " + modeStep);
        if (modeStep == MODESTEP_CHUGOU_ONE) {
            discalingFragment = new DiscaleFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(DiscaleFragment.KEY_CHANGE_WATER, true);
            discalingFragment.setArguments(bundle);
            discalingFragment.show(getSupportFragmentManager(), "discalingFragment");
        } else if (modeStep == MODESTEP_CHUGOU_TWO) {
            discalingFragment.dismissAllowingStateLoss();
        } else if (modeStep == MODESTEP_CHUWEI_ONE) {
            DeodorizationFragment fragment = new DeodorizationFragment();
            fragment.show(getSupportFragmentManager(), DeodorizationFragment.class.getSimpleName());
            updateCookStep(OvenConstants.COOKSTEP_TWO);
        } else if (modeStep == MODESTEP_WEIBOZHENG_ONE || modeStep == MODESTEP_WEIZHENGKAO_ONE || modeStep == MODESTEP_GAOWENXIAODU_ONE) {
            updateCookStep(OvenConstants.COOKSTEP_TWO);
        } else if (modeStep == MODESTEP_WEIZHENGKAO_TWO) {
            updateCookStep(OvenConstants.COOKSTEP_THREE);
        }
    }

    /**
     * ??????????????????
     *
     * @param state ????????????true???????????????false
     */
    @Override
    public void waterTankState(boolean state) {
        Log.i(TAG, "waterTankState: " + state);
        String curModeType = modeTypeList.get(currentSelectIndex - 1).getModeType();
        Log.d(TAG, "waterTankState: curModeType: " + curModeType);
        if (!state && TextUtils.equals(MODE_TYPE_STREAM, curModeType)) {
            ViomiToastUtil.showToastCenter(getString(R.string.error_water_tank_close_content));
        }
    }

    @Override
    public void showEidtFragment() {
        // ????????? onResume ?????? ???????????? FragmentDialog
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String orgFilePath = VideoFileUtils.INSTANCE.getVideoPath(recipeName, VideoFileUtils.INSTANCE.getVideoDirOrg());
            File orgFile = new File(orgFilePath);
            if (!orgFile.exists() || orgFile.length() <= 0) {
                Log.i(TAG, "showEidtFragment: file is wrong return");
                return;
            }
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setRecipeName(recipeName);
            videoInfo.setModeId(String.valueOf(modeId));
            videoInfo.setRecordTime(30);
            Log.i(TAG, "showEidtFragment: videoName: " + recipeName);
            CameraServiceFactory.getInstance().getCameraService().showEditVideoFragment(videoInfo);
            OvenUtil.setRecordingStatusToMcu(CommonConstant.RECORD_STATE_FINISH);
        }, 300);
    }

    /**
     * ???????????????????????????
     */
    private void showCompeleteFragment() {
        String contentTip = "";
        if (modeId == OvenConstants.MODE_ID_CHUGOU) {
            contentTip = getString(R.string.oven_discalling_tip_four);
        }
        if (modeId == OvenConstants.MODE_ID_CHUWEI) {
            contentTip = getString(R.string.oven_deodorization_finish);
        }
        String positiveName = getString(R.string.ovenso_known);
        CommonAffirmFragment dialog = new CommonAffirmFragment();
        dialog.setCancelable(false);
        String title = recipeName + getString(R.string.ovenso_finish);
        Bundle bundle = CommonAffirmFragment.getBundle(title, contentTip, null, positiveName, false, 0);
        dialog.setArguments(bundle);
        dialog.setPositiveClickListener(dialog1 -> {
            dialog1.dismiss();
            finish();
        });
        dialog.show(getSupportFragmentManager(), "running");
    }

    /**
     * ???????????? ?????????????????????
     */
    @Override
    public void updatePropertyView() {
        if (!CommonConstant.SHOW_DEBUG_INFO) {
            return;
        }
        childViewBinding.cookrunTestView.updateProperty();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected CookRunningPresenter initPresenter() {
        return new CookRunningPresenter();
    }

}


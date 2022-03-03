package com.viomi.ovenso;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.bean.OvenDeviceFaultEnum;
import com.viomi.ovenso.bean.OvenRecipeStepEnum;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.custommode.CustomeModeUtils;
import com.viomi.ovenso.custommode.RecipeUtil;
import com.viomi.ovenso.helper.ModesHelper;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.serial.CustomModeWrite;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.ui.activity.running.CookRunningActivity;
import com.viomi.ovenso.ui.fragment.RecipeStepFragment;
import com.viomi.ovenso.util.MessageUtils;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.camera.CameraServiceFactory;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.componentservice.ovenso.IOvensoService;
import com.viomi.ovensocommon.db.CookParamEntity;
import com.viomi.ovensocommon.db.MessageEntity;
import com.viomi.ovensocommon.db.RecommendRecipe;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.utils.MediaPlayerUtils;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @data:2021/11/9
 */
public class OvenSoService implements IOvensoService {
    private static final String TAG = "OvenSoService";
    public static final int CUSTOM_RECIPE_MARGIN = 100;
    private int beforeWorkStatus = -1;
    private boolean isStartCookByPlug = false;
    private static final int COOKTYPE_MODE = 1;
    private static final int COOKTYPE_RECIPE = 2;
    private int currentRecipeType = 0;

    @Override
    public void setCombinedModeInfo(OvenPropEnum ovenPropEnum, String modeInfo) {
        String customName = "";
        if (ovenPropEnum.siid == OvenPropEnum.RECIPE1.siid) {
            customName = CustomeModeUtils.CUSTOM_NAME_RECIPE;
        } else if (ovenPropEnum.siid == OvenPropEnum.COMBINED_MODE1.siid) {
            customName = CustomeModeUtils.CUSTOM_NAME_MODE;
        }
        CustomeModeUtils.setCustomeRecipe(customName, ovenPropEnum.piid, modeInfo);
    }

    @Override
    public String getCombinedModeInfo(int modePiid) {
        String customModeInfo = CustomeModeUtils.getCustomeRecipe(CustomeModeUtils.CUSTOM_NAME_MODE, modePiid);
        Log.i(TAG, "getCombinedModeInfo: modePid: " + modePiid + "   customModeInfo:" + customModeInfo);
        return customModeInfo;
    }

    @Override
    public String getCombineRecipeInfo(int recipePid) {
        String customRecipeInfo = CustomeModeUtils.getCustomeRecipe(CustomeModeUtils.CUSTOM_NAME_RECIPE, recipePid);
        Log.i(TAG, "getCombineRecipeInfo: recipePid: " + recipePid + "   customRecipeInfo:" + customRecipeInfo);
        return customRecipeInfo;
    }

    @Override
    public String getRecipeName() {
        String recipeName = RecipeUtil.getRecipeName();
        return recipeName;
    }

    @Override
    public void updateRecordStatus(int recordStatus) {
        Log.i(TAG, "updateRecordStatus: from service " + recordStatus);
        ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_UPDATE_RECORD_STATUS, recordStatus);
    }

    @Override
    public boolean needFobideScreenOff() {
        String topActivityName = ActivityUtils.getTopActivity().getClass().getName();
        Log.i(TAG, "needFobideScreenOff: topActivityName: " + topActivityName);
        return TextUtils.equals(topActivityName, CookRunningActivity.class.getName());
    }

    @Override
    public long insertVideoInfoMessage(VideoInfo videoInfo) {
        long result = MessageUtils.addMessage(videoInfo);
        Log.i(TAG, "insertVideoInfoMessage: result: " + result);
        return result;
    }

    @Override
    public void deleteVideoMessage(VideoInfo videoInfo) {
        long result = MessageUtils.deleteMessage(videoInfo);
        Log.i(TAG, "deleteVideoMessage: result: " + result);
    }

    @Override
    public void dealPropertyFromPlug(PropertyEntity propertyEntity) {
        Log.i(TAG, "dealPropertyFromPlug: " + propertyEntity);
        // 属性上报插件， 并且下发给固件
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntity);
        Log.i(TAG, "dealPropertyFromPlug: to firm");
        OvenSerialManager.getInstance().setOtherProperty(propertyEntity);
        // 如果是自定义菜谱或者自定义模式，需要 传递 增加 属性 3，31. 属性的传递
        int sid = propertyEntity.getSid();
        int pid = propertyEntity.getPid();
        Object content = propertyEntity.getContent();
        if ((sid == OvenPropEnum.DISHID.siid && pid == OvenPropEnum.DISHID.piid)
                || (sid == OvenPropEnum.MODE.siid && pid == OvenPropEnum.MODE.piid)) {
            Log.i(TAG, "dealPropertyFromPlug: content  " + content);
            if ((int) content > 100) {
                int customModeOrRecipePid = (int) content - 100;
                setCustomModeRecipeProperty(pid, customModeOrRecipePid);
            }
        }
        if (sid == OvenPropEnum.VIDEO_RECORD.siid && propertyEntity.getPid() == OvenPropEnum.VIDEO_RECORD.piid) {
            Log.i(TAG, "dealPropertyFromPlug: post MSG_DEAL_RECORD ");
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_DEAL_RECORD_STATUS, content);
            return;
        }
    }

    /**
     * @param pid
     * @param customModeOrRecipePid
     * @description:1 、 通过sid  和  自定义菜谱或者模式的pid  设置 自定义菜谱的属性到固件
     */
    private void setCustomModeRecipeProperty(int pid, int customModeOrRecipePid) {
        String customName = "";
        if (pid == OvenPropEnum.DISHID.piid) {
            customName = CustomeModeUtils.CUSTOM_NAME_RECIPE;
        } else if (pid == OvenPropEnum.MODE.piid) {
            customName = CustomeModeUtils.CUSTOM_NAME_MODE;
        }
        if (TextUtils.isEmpty(customName)) {
            Log.i(TAG, "setCustomModeRecipeProperty: customName is null return ");
            return;
        }
        String mucStr = CustomeModeUtils.getMucProperty(customName, customModeOrRecipePid);
        Log.i(TAG, "setPropertyFromPlug: mcuStr: " + mucStr);
        new CustomModeWrite().executeWrite(mucStr);
    }


    /**
     * 包含 启动暂停 结束  预约 取消预约等 指令的处理
     *
     * @param siid
     * @param aiid
     * @param paramsProp
     */
    @Override
    public void doActionFromPlug(int siid, int aiid, List<PropertyEntity> paramsProp) {
        Log.i(TAG, "doActionFromPlug: siid: " + siid + " aiid : " + aiid);
        // 先根据插件下发的指令 上报 对应执行结果的工作状态
        preReportResult(siid, aiid);
        isStartCookByPlug = true;
        // 下发指令给串口通信
        SerialControl.setAction("", siid, aiid, paramsProp);
    }

    @Override
    public void dealPropertyChangeFromFirm(PropertyEntity propertyEntity) {
        //把所有的属性 存在静态变量里面方便测试
        PropertyUtil.updateGlobalPropAndGetStatusEntity(propertyEntity);
        int sid = propertyEntity.getSid();
        int pid = propertyEntity.getPid();
        Object content = propertyEntity.getContent();
        Log.i(TAG, "dealPropertyChangeFromFirm: " + propertyEntity);
        // 灯 开关
        if (sid == OvenPropEnum.LIGHT.siid && pid == OvenPropEnum.LIGHT.piid) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_UPDATE_LIGHT, content);
            return;
        }
        // 工作状态
        if (sid == OvenPropEnum.WORK_STATUS.siid && pid == OvenPropEnum.WORK_STATUS.piid) {
            int workStatus = (int) content;
            if (workStatus == beforeWorkStatus) {
                Log.d(TAG, "startCookRunningActivity PropertyUtil.status is idle or upgrading return");
                return;
            }
            beforeWorkStatus = workStatus;
        }
        //  剩余的时间
        if (sid == OvenPropEnum.LEFT_TIME.siid && pid == OvenPropEnum.LEFT_TIME.piid) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_UPDATE_LEFTTIME, content);
            return;
        }
        // 已用的工作时间
        if (sid == OvenPropEnum.WORKING_TIME.siid && pid == OvenPropEnum.WORKING_TIME.piid) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_UPDATE_WORKTIME, content);
            return;
        }
        // 实时的温度
        if (sid == OvenPropEnum.TEMPER.siid && pid == OvenPropEnum.TEMPER.piid) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_UPDATE_TEMPERATURE, content);
            return;
        }
        // 组合模式步骤的展示处理
        if (sid == OvenPropEnum.CUSTOM_MODE_STEP.siid && pid == OvenPropEnum.CUSTOM_MODE_STEP.piid) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_CUSTOM_STEP, content);
            return;
        }
        // 除垢和除味的步骤提示
        if (sid == OvenPropEnum.MODE_STEP.siid && pid == OvenPropEnum.MODE_STEP.piid) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_MODE_STEP, content);
            return;
        }
        // 显示菜谱步骤的弹框，烹饪是暂停状态的
        if (sid == OvenPropEnum.RECIPE_STEP.siid && pid == OvenPropEnum.RECIPE_STEP.piid) {
            int recipeStepValue = (int) content;
            if (recipeStepValue == OvenRecipeStepEnum.STEP_NO.stepValue || recipeStepValue == OvenRecipeStepEnum.STEP_PREHEARTING.stepValue) {
                Log.i(TAG, "dealPropertyChangeFromFirm: recipeStep  no need dialog");
                return;
            }
            // 更新模式变化的
            if (recipeStepValue == OvenRecipeStepEnum.STEP_RECIPE_TWO.stepValue) {
                ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_CUSTOM_STEP, OvenConstants.COOKSTEP_TWO);
                return;
            }
            Log.i(TAG, "dealPropertyChangeFromFirm: showRecipeStep dialog ");
            FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
            RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
            Bundle bundle = new Bundle();
            recipeStepFragment.setArguments(bundle);
            bundle.putInt(RecipeStepFragment.KEY_RECIPE_STEP_VALUE, recipeStepValue);
            recipeStepFragment.show(fragmentActivity.getSupportFragmentManager(), RecipeStepFragment.class.getSimpleName());
        }

        // 水箱放置不到位
        if (sid == OvenPropEnum.WATERTANK_ISCLOSE.siid && propertyEntity.getPid() == OvenPropEnum.WATERTANK_ISCLOSE.piid) {
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_WATER_TANK_CLOSE, content);
            return;
        }
        // 缺水的属性变化的处理
        if (sid == OvenPropEnum.LACK_WATER.siid && propertyEntity.getPid() == OvenPropEnum.LACK_WATER.piid) {
            boolean isLockWater = (boolean) content;
            if (isLockWater) {
                MediaPlayerUtils.getInstance().startPlayRawResource(R.raw.add_water);
                OvenUtil.showDeviceFaultDialog(R.string.ovenso_lockwater, R.string.ovenso_lockwater_tip);
                // 加入消息提醒
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setMessageType(MessageEntity.TYPE_ERROR);
                messageEntity.setMessageTime(System.currentTimeMillis());
                String messageTitle = ApplicationUtils.getContext().getString(R.string.ovenso_lockwater);
                String messageContent = ApplicationUtils.getContext().getString(R.string.ovenso_lockwater_tip);
                messageEntity.setMessageTitle(messageTitle);
                messageEntity.setMessageContent(messageContent);
                messageEntity.setIconResId(R.drawable.messagelist_normal);
                MessageUtils.addMessage(messageEntity);
            }
        }
        // 设备故障的处理逻辑
        if (sid == OvenPropEnum.DEVICE_FAULT.siid && propertyEntity.getPid() == OvenPropEnum.DEVICE_FAULT.piid) {
            int deviceFalutValue = (int) content;
            if (deviceFalutValue == 0) {
                OvenUtil.dismissDeviceFalutDialog();
                return;
            }
            OvenDeviceFaultEnum ovenDeviceFaultEnum = OvenUtil.showDeviceFaultDialog(deviceFalutValue);
            if (ovenDeviceFaultEnum == null) {
                Log.i(TAG, "dealPropertyChangeFromFirm: otherError");
                return;
            }
            // 加入消息提醒
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessageType(MessageEntity.TYPE_ERROR);
            messageEntity.setMessageTime(System.currentTimeMillis());
            String messageTitle = ApplicationUtils.getContext().getString(ovenDeviceFaultEnum.titleId);
            String messageContent = ApplicationUtils.getContext().getString(ovenDeviceFaultEnum.msgId);
            messageEntity.setMessageTitle(messageTitle);
            messageEntity.setMessageContent(messageContent);
            messageEntity.setIconResId(R.drawable.messagelist_normal);
            MessageUtils.addMessage(messageEntity);
        }
        //工作状态的改变  空闲中0  工作中1   已暂停2  预约中3  已结束4   (烹饪模式 推荐菜谱 有预约，  我的菜谱和辅助模式没有预约）
        if (sid == OvenPropEnum.WORK_STATUS.siid && pid == OvenPropEnum.WORK_STATUS.piid) {
            int statusEnumValue = (int) content;
            Log.i(TAG, "dealPropertyChangeFromFirm: sateEmunValue: " + statusEnumValue);
            Log.i(TAG, "dealPropertyChangeFromFirm: isStartCookByPlug: " + isStartCookByPlug);
            // 判断topActivity
            String topClassName = ActivityUtils.getTopActivity().getClass().getName();
            Log.i(TAG, "dealPropertyChangeFromFirm: topClassName: " + topClassName);
            // 固件或者屏端 点 开始按钮
            if (!TextUtils.equals(topClassName, CookRunningActivity.class.getName()) &&
                    (statusEnumValue == OvenWorkStatusEnum.WORKING.value || statusEnumValue == OvenWorkStatusEnum.BOOKED.value)
                    && isStartCookByPlug) {
                Log.i(TAG, "dealPropertyChangeFromFirm: isStartCookByPlug: " + isStartCookByPlug);
                // 插件点击开始按钮
                //普通菜谱  普通模式
                // 自定义菜谱   自定义模式
                ArrayList<ModeTypeEntity> modeTypeEntityArrayList = new ArrayList<ModeTypeEntity>();
                String modeOrRecipeName = "";
                int modeId = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, 0);
                int dishId = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0);
                Log.i(TAG, "dealPropertyChangeFromFirm: modeId: " + modeId + " dishId: " + dishId);
                if (dishId != 0) {
                    currentRecipeType = COOKTYPE_RECIPE;
                } else {
                    currentRecipeType = COOKTYPE_MODE;
                }
                //  菜谱启动
                if (currentRecipeType == COOKTYPE_RECIPE) {
                    // 自定义菜谱
                    if (dishId > CUSTOM_RECIPE_MARGIN) {
                        Log.i(TAG, "dealPropertyChangeFromFirm: custom recipe");
                        String customRcipeStr = CustomeModeUtils.getCustomeRecipe(CustomeModeUtils.CUSTOM_NAME_RECIPE, dishId - CUSTOM_RECIPE_MARGIN);
                        ModeTypeEntity modeTypeEntity = CustomeModeUtils.getModeTypeEntity(CustomeModeUtils.CUSTOM_NAME_RECIPE, customRcipeStr);
                        modeTypeEntityArrayList.add(modeTypeEntity);
                    } else {
                        // 普通菜谱
                        Log.i(TAG, "dealPropertyChangeFromFirm: recommond recipe");
                        RecommendRecipe currentRecommendRecipe = RecipeUtil.getRecommendRecipeById(dishId);
                        ModeTypeEntity modeTypeEntity = new ModeTypeEntity();
                        modeTypeEntity.setModeId(currentRecommendRecipe.getRecipeId());
                        modeTypeEntity.setResIdBg("dish_id_" + currentRecommendRecipe.getRecipeId());
                        CookParamEntity cookParamEntity = new CookParamEntity();
                        cookParamEntity.setDefineTime(currentRecommendRecipe.getCookTime());
                        cookParamEntity.setDefineFirepower(currentRecommendRecipe.getCookFirepower());
                        ArrayList<CookParamEntity> cookParamEntityArrayList = new ArrayList<>();
                        cookParamEntityArrayList.add(cookParamEntity);
                        modeTypeEntity.setCookParamEntityList(cookParamEntityArrayList);
                        modeTypeEntityArrayList.add(modeTypeEntity);
                    }
                    modeOrRecipeName = RecipeUtil.getRecipeName();
                } else if (currentRecipeType == COOKTYPE_MODE) {
                    // 自定义模式
                    if (modeId > CUSTOM_RECIPE_MARGIN) {
                        String customModeContent = CustomeModeUtils.getCustomeRecipe(CustomeModeUtils.CUSTOM_NAME_MODE, modeId - CUSTOM_RECIPE_MARGIN);
                        ModeTypeEntity modeTypeEntity = CustomeModeUtils.getModeTypeEntity(CustomeModeUtils.CUSTOM_NAME_MODE, customModeContent);
                        modeTypeEntityArrayList.add(modeTypeEntity);
                    } else {
                        // 普通模式
                        // 模式的值会经常变的，所以需要 重新更具属性来进行设置值
                        ModeTypeEntity modeTypeEntity = ModesHelper.getModeEntityById(modeId);
                        // 如果是微蒸烤
                        if (modeId == OvenConstants.MODE_ID_MICRWAVE_STREAM_BAKE) {
                            CookParamEntity microPropery = modeTypeEntity.getCookParamEntityList().get(0);

                        }

                        CookParamEntity cookParamEntity = modeTypeEntity.getCookParamEntityList().get(0);
                        String childModeType = cookParamEntity.getModeType();
                        int definePowerNew = 0;
                        float defineTimeNew = 0;
                        if (TextUtils.equals(childModeType, OvenConstants.MODE_TYPE_STREAM)) {
                            definePowerNew = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.TEMPZ.siid, OvenPropEnum.TEMPZ.piid, 0);
                            defineTimeNew = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.TIMEZ.siid, OvenPropEnum.TIMEZ.piid, 0);
                        } else if (TextUtils.equals(childModeType, OvenConstants.MODE_TYPE_BAKE)) {
                            definePowerNew = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.TEMPK.siid, OvenPropEnum.TEMPK.piid, 0);
                            defineTimeNew = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.TEMPK.siid, OvenPropEnum.TEMPK.piid, 0);
                        } else if (TextUtils.equals(childModeType, OvenConstants.MODE_TYPE_MICROWAVE)) {
                            // 微波的指令必须传 秒，其他传分钟
                            definePowerNew = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.MICRO_LEVEL.siid, OvenPropEnum.MICRO_LEVEL.piid, 0);
                            defineTimeNew = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.MICRO_TIME.siid, OvenPropEnum.MICRO_TIME.piid, 0);
                            defineTimeNew = defineTimeNew / 60f;
                        }
                        Log.i(TAG, "dealPropertyChangeFromFirm: definePowerNew： " + definePowerNew + "  defineTimeNew: " + defineTimeNew);
                        // 单模式，不用重新设置时间或者温度
                        if (definePowerNew > 0) {
                            cookParamEntity.setDefineFirepower(definePowerNew);
                        }
                        if (defineTimeNew > 0) {
                            cookParamEntity.setDefineTime(defineTimeNew);
                        }
                        modeOrRecipeName = modeTypeEntity.getName();
                        modeTypeEntityArrayList.add(modeTypeEntity);
                    }
                }
                Log.i(TAG, "dealPropertyChangeFromFirm: startCookFromPlug ");

                if (modeTypeEntityArrayList.size() == 0) {
                    Log.i(TAG, "dealPropertyChangeFromFirm: modeTypeList is null");
                    return;
                }
                ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_COOK_RUNNING)
                        .withString(CookRunningActivity.KEY_RECIPENAME, modeOrRecipeName)
                        .withInt(CookRunningActivity.KEY_STAUSENUMS_VALUE, statusEnumValue)
                        .withParcelableArrayList(CookRunningActivity.KEY_MODETYPE_LIST, modeTypeEntityArrayList)
                        .navigation();
                isStartCookByPlug = false;
                return;
            }
            Log.i(TAG, "dealPropertyChangeFromFirm: otherStatus");
            // 开始 和预约  发送到 DetailActivity 里面
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_COOK_STATUSCHANGE, statusEnumValue);
        }
    }

    /**
     * //烹饪完成事件  预约开始事件  缺水事件  只是为了 上报 ，推送消息 给app ，屏端不需要有其他逻辑，其他逻辑通过属性变化来处理，跟插件保持一致
     *
     * @param propertyEntity
     */
    @Override
    public void dealEventChangeFromFirm(PropertyEntity propertyEntity) {
        Log.i(TAG, "dealEventChangeFromFirm: " + propertyEntity);
        ModuleSettingServiceFactory.getInstance().getViotService().reportEvent(propertyEntity.getSid(), propertyEntity.getPid());
        MiotServiceFactory.getInstance().getMiotService().reportEvent(propertyEntity.getSid(), propertyEntity.getPid());
    }

    @Override
    public void isPowerOffLauncher() {

    }

    @Override
    public void MiotLoginStatusChange(boolean isBind) {
        Log.i(TAG, "MiotLoginStatusChange: isBind: " + isBind);
        int msgId = isBind ? OvenBusEventConstants.MSG_MIOT_LOGIN : OvenBusEventConstants.MSG_MIOT_LOGOUT;
        ViomiRxBus.getInstance().post(msgId);
        if (!isBind) {
            // 解除绑定 需要连接服务器，清空所有的视频
            CameraServiceFactory.getInstance().getCameraService().deleteAllVideo();
        }
    }

    @Override
    public void ViotLoginStatusChange(boolean isBind) {
        Log.i(TAG, "ViotLoginStatusChange: isBind: " + isBind);
        int msgId = isBind ? OvenBusEventConstants.MSG_VIOMI_LOGIN : OvenBusEventConstants.MSG_VIOMI_LOGOUT;
        ViomiRxBus.getInstance().post(msgId);
        if (!isBind) {
            // 自定义菜谱的处理
            ViomiRoomDatabase.getDatabase().customModeDao().deleteAll();
            //收藏的处理
            ViomiRoomDatabase.getDatabase().recommendRecipeDao().updateAllCollected(false);
            RecipeUtil.setRecipeListNull();
        }
    }

    /**
     * 根据 action 预先上报工作状态
     */
    private void preReportResult(int sid, int aid) {
        Log.i(TAG, "preReportResult: sid: " + sid + " aid: " + aid);
        OvenWorkStatusEnum targetWorkStatus = OvenUtil.getTargetWorkStaus(sid, aid);
        PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.WORK_STATUS.siid, OvenPropEnum.WORK_STATUS.piid, targetWorkStatus.value);
        Log.i(TAG, "preReportResult: propertyEntity: " + propertyEntity);
        // 上报工作状态
        MiotServiceFactory.getInstance().getMiotService().reportData(propertyEntity);
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntity);
    }

}

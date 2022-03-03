package com.viomi.ovenso.custommode;

import android.text.TextUtils;
import android.util.Log;

import com.viomi.common.ViomiProvideUtil;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.helper.ModesHelper;
import com.viomi.ovenso.helper.ReportUtils;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.db.CookParamEntity;
import com.viomi.ovensocommon.db.CustomModeEntity;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenPropEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/11/19.
 * Description:菜谱工具类
 */
public class CustomeModeUtils {
    private static final String TAG = "CustomeModeUtils";
    // 自定义菜谱的key
    public static final String CUSTOM_NAME_RECIPE = "菜谱组合";
    public static final String CUSTOM_NAME_MODE = "模式组合";
    //自定义菜谱
    public static final int CUSTOME_RECIPE_COUNT = 8;
    public static final int CUSTOME_MODE_RECIPE_BEGAIN = 100;
    // 表示空的自定义菜谱
    public static final String RECIPE_INFO_EMPTY = "null";
    public static final String PLUG_SPLITER = ",";

    public static ArrayList<ModeTypeEntity> testCustomeMode() {
        String customeRecipeOne = "101,5b6u5rOiMg==,34,60,3";     // 微波2
        String customeRecipeTwo = "102,5b6u5rOi6auY5rip6JK4,34,60,3,1,10,110";  // 微波高温蒸
        String customeRecipeThree = "103,5b6u5rOi6auY5rip6JK454Ot6aOO54Ok,34,60,3,1,10,110,17,10,180"; // 微波高温蒸热风烤
        String customeRecipeFour = "104,6auY5rip6JK4MueDremjjueDpA==,1,10,110,1,12,105,17,10,180";   // 高温蒸2热风烤
        setCustomeRecipe(CUSTOM_NAME_MODE, 1, customeRecipeOne);
//        setCustomeRecipe(CUSTOM_NAME_MODE, 2, customeRecipeTwo);
//        setCustomeRecipe(CUSTOM_NAME_MODE, 3, customeRecipeThree);
//        setCustomeRecipe(CUSTOM_NAME_MODE, 4, customeRecipeFour);
        ArrayList<ModeTypeEntity> customRecipeList = CustomeModeUtils.getEntitiesByType(CUSTOM_NAME_MODE, CUSTOME_RECIPE_COUNT);
        return customRecipeList;
    }

    public static ArrayList<ModeTypeEntity> testCustomeRecipe() {
        String customeRecipeOne = "101,6I+c6LCxMeeDremjjueDpA==,17,10,180";   //菜谱1热风烤
        String customeRecipeTwo = "102,6I+c6LCxMueDremjjuW+ruazog==,17,10,180,34,60,4";  // 菜谱2热风微波
        String customeRecipeThree = "103,6I+c6LCxM+eDremjjuW+ruazog==,17,10,180,34,60,5";  // 菜谱3热风微波
        setCustomeRecipe(CUSTOM_NAME_RECIPE, 1, customeRecipeOne);
//        setCustomeRecipe(CUSTOM_NAME_RECIPE, 2, customeRecipeTwo);
//        setCustomeRecipe(CUSTOM_NAME_RECIPE, 3, customeRecipeThree);
        ArrayList<ModeTypeEntity> customRecipeList = CustomeModeUtils.getEntitiesByType(CUSTOM_NAME_RECIPE, CUSTOME_RECIPE_COUNT);
        return customRecipeList;
    }

    /**
     * 更新自定义菜谱
     *
     * @param recipePiid   范围1~8
     *                     "组合模式id,组合模式名字,子模式1id, 时间,温度 ,字模式2id,时间,温度"
     * @param recipeString "组合模式id,组合模式名字,子模式1id, 时间,温度 ,字模式2id,时间,温度"
     */
    public static void setCustomeRecipe(String customType, int recipePiid, String recipeString) {
        Log.i(TAG, "setCustomeRecipe:recipePiid:  " + recipePiid);
        Log.i(TAG, "setCustomeRecipe: recipeString " + recipeString);
        CustomModeEntity customModeEntity = new CustomModeEntity();
        if (TextUtils.equals(recipeString, RECIPE_INFO_EMPTY)) {
            customModeEntity.setModeName(RECIPE_INFO_EMPTY);
        } else {
            String[] recipeProps = recipeString.split(PLUG_SPLITER);
            String modeName = recipeProps[1];
            customModeEntity.setModeName(modeName);
        }
        String indexName = customType + (CUSTOME_MODE_RECIPE_BEGAIN + recipePiid);
        customModeEntity.setIndexName(indexName);
        customModeEntity.setModeId(CUSTOME_MODE_RECIPE_BEGAIN + recipePiid);
        customModeEntity.setModeFullContent(recipeString);
        long insertResult = ViomiRoomDatabase.getDatabase().customModeDao().insert(customModeEntity);
        Log.i(TAG, "setCustomeRecipe: insertResult: " + insertResult);
        // 所有自定义菜谱的名字都是一样的
        ReportUtils.reportDoubleIot(OvenPropEnum.RECIPE1.siid, recipePiid, recipeString);
        // 更新界面
        ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_UPDATE_RECIPE);
    }

    public static String getMucProperty(String customType, int recipePiid) {
        String recipeFullContent = getCustomeRecipe(customType, recipePiid);
        int indexId = recipeFullContent.indexOf(PLUG_SPLITER);
        String deleteIdStr = recipeFullContent.substring(indexId + 1);
        int indexName = deleteIdStr.indexOf(PLUG_SPLITER);
        String mucProrpty = deleteIdStr.substring(indexName + 1);
        Log.i(TAG, "getMucProperty: mucProrpty: " + mucProrpty);
        return mucProrpty;
    }

    /**
     * 更新自定义菜谱
     *
     * @param propertyEntity 范围1~8
     *                       "组合模式id,组合模式名字,子模式1id, 时间,温度 ,字模式2id,时间,温度"
     */
    public static void setCustomeRecipe(PropertyEntity propertyEntity) {
        Log.i(TAG, "setCustomeRecipe: " + propertyEntity.getSid());
        String indexName = "";
        // 自定义菜谱
        if (propertyEntity.getSid() == OvenPropEnum.RECIPE1.siid) {
            indexName = CUSTOM_NAME_RECIPE + propertyEntity.getPid();
        }
        // 自定义模式
        if (propertyEntity.getSid() == OvenPropEnum.COMBINED_MODE1.siid) {
            indexName = CUSTOM_NAME_MODE + propertyEntity.getPid();
        }
        String recipeString = (String) propertyEntity.getContent();
        Log.i(TAG, "setCustomeRecipe: recipeString " + recipeString);
        CustomModeEntity customModeEntity = new CustomModeEntity();
        String[] recipeProps = recipeString.split(PLUG_SPLITER);
        int modeId = Integer.parseInt(recipeProps[0]);
        customModeEntity.setModeId(modeId);
        String modeName = recipeProps[1];
        customModeEntity.setModeName(modeName);
        customModeEntity.setIndexName(indexName);
        customModeEntity.setModeFullContent(recipeString);
        ViomiRoomDatabase.getDatabase().customModeDao().insert(customModeEntity);
        // 所有自定义菜谱的名字都是一样的
        ReportUtils.reportDoubleIot(propertyEntity.getSid(), propertyEntity.getPid(), recipeString);
    }

    public static String getCustomeRecipeName(int recipeId) {
        String recipeStr = getCustomeRecipe(CUSTOM_NAME_RECIPE, recipeId);
        String recipeName = recipeStr.split(PLUG_SPLITER)[1];
        Log.i(TAG, "getCustomeRecipeName: recipeName: " + recipeName);
        return recipeStr;
    }

    /**
     * 读取菜谱信息
     *
     * @param recipePiid 范围1~8
     * @return
     */
    public static String getCustomeRecipe(String customeType, int recipePiid) {
        String indexName = customeType + (CUSTOME_MODE_RECIPE_BEGAIN + recipePiid);
        CustomModeEntity customModeEntity = ViomiRoomDatabase.getDatabase().customModeDao().getModesByIndexName(indexName);
        String modeFullContent = customModeEntity == null ? RECIPE_INFO_EMPTY : customModeEntity.getModeFullContent();
        Log.i(TAG, "getCustomeRecipeOrMode: indexName: " + indexName + "   modeFullContent: " + modeFullContent);
        return modeFullContent;
    }

    public static ArrayList<ModeTypeEntity> getEntitiesByType(String customType, int entityCount) {
        ArrayList<ModeTypeEntity> modeTypeEntityArrayList = new ArrayList<>();
        for (int customModeIndex = 1; customModeIndex < entityCount + 1; customModeIndex++) {
            String recipeStr = getCustomeRecipe(customType, customModeIndex);
            ModeTypeEntity modeTypeEntity = getModeTypeEntity(customType, recipeStr);
            if (modeTypeEntity != null) {
                modeTypeEntityArrayList.add(modeTypeEntity);
            }
        }
        Log.i(TAG, "getAllCustomMode: modeTyeepeListSize:" + modeTypeEntityArrayList.size());
        return modeTypeEntityArrayList;
    }

    /**
     * "组合模式id,组合模式名字,子模式1id, 时间,温度 ,字模式2id,时间,温度"
     *
     * @param customType
     * @param customModeStr
     * @return
     */
    public static ModeTypeEntity getModeTypeEntity(String customType, String customModeStr) {
        Log.i(TAG, "getModeTypeEntity: customModeStr: " + customModeStr);
        if (TextUtils.isEmpty(customModeStr) || TextUtils.equals(RECIPE_INFO_EMPTY, customModeStr)) {
            Log.i(TAG, "getModeTypeEntity: custome is empty  return ");
            return null;
        }
        String[] recipeProps = customModeStr.split(",");
        ModeTypeEntity modeTypeEntity = new ModeTypeEntity();
        int recipeIndex = Integer.parseInt(recipeProps[0]);
        modeTypeEntity.setModeId(recipeIndex);
        modeTypeEntity.setName(recipeProps[1]);
        modeTypeEntity.setModeType(customType);
        modeTypeEntity.setResIdBg("ic_mic_combine");
        modeTypeEntity.setResIdTip("mode_combined_icon");
        ArrayList<CookParamEntity> cookParamEntityArrayList = new ArrayList<>();
        Log.i(TAG, "getModeTypeEntity: recipeProps " + recipeProps.length);
        for (int childModeBegain = 2; childModeBegain < recipeProps.length; childModeBegain += 3) {
            CookParamEntity customeCookParaEntity = new CookParamEntity();
            int childModeId = Integer.parseInt(recipeProps[childModeBegain]);
            int defineTime = Integer.parseInt(recipeProps[childModeBegain + 1]);
            int definePower = Integer.parseInt(recipeProps[childModeBegain + 2]);
            customeCookParaEntity.setModeId(childModeId);
            ModeTypeEntity singleModeEntity = ModesHelper.getModeEntityById(childModeId);
            customeCookParaEntity.setModeType(singleModeEntity.getModeType());
            customeCookParaEntity.setDefineTime(defineTime);
            customeCookParaEntity.setDefineFirepower(definePower);
            cookParamEntityArrayList.add(customeCookParaEntity);
           /* CookParamEntity cookParamEntity = ModesHelper.getModeEntityById(modeId).getCookParamEntityList().get(0);
            customeCookParaEntity.setTimeRangeMin(cookParamEntity.getTimeRangeMin());
            customeCookParaEntity.setTimeRangeMax(cookParamEntity.getTimeRangeMax());
            customeCookParaEntity.setFirepowerRangeMin(cookParamEntity.getFirepowerRangeMin());
            customeCookParaEntity.setFirepowerRangeMax(cookParamEntity.getFirepowerRangeMax());*/
        }
        modeTypeEntity.setCookParamEntityList(cookParamEntityArrayList);
        return modeTypeEntity;
    }


    /**
     * 上报菜单名字给VIOT 服务器，以便插件端更新菜单名字
     */
    public static void uploadRecipeName() {
        int dishNameId = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0);
        Log.i(TAG, "uploadRecipeName: dishName: " + dishNameId);
        ReportUtils.reportDoubleIot(OvenPropEnum.DISHNAME.siid, OvenPropEnum.DISHNAME.piid, dishNameId);
    }

    /**
     * 上传菜谱给插件
     */
    public static void upLoadRecipeToPlug() {
        Log.i(TAG, "upLoadRecipeToPlug: ");
        //上报部分特殊参数 dishName和8个菜谱
        String deviceId = ViomiProvideUtil.getDeviceId();
        List<PropertyEntity> properties = new ArrayList<>();
        //上报菜谱的指令
        // OvenPropEnum.CUSTOM_DISHS.siid, OvenPropEnum.CUSTOM_DISHS.piid, "" 先上报当前的 属性
        PropertyEntity deviceProperty = new PropertyEntity();
        properties.add(deviceProperty);
        //上报8个自定义菜谱
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE1.siid, OvenPropEnum.RECIPE1.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE1.piid));
        properties.add(deviceProperty);
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE2.siid, OvenPropEnum.RECIPE2.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE2.piid));
        properties.add(deviceProperty);
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE3.siid, OvenPropEnum.RECIPE3.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE3.piid));
        properties.add(deviceProperty);
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE4.siid, OvenPropEnum.RECIPE4.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE4.piid));
        properties.add(deviceProperty);
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE5.siid, OvenPropEnum.RECIPE5.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE5.piid));
        properties.add(deviceProperty);
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE6.siid, OvenPropEnum.RECIPE6.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE6.piid));
        properties.add(deviceProperty);
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE7.siid, OvenPropEnum.RECIPE7.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE7.piid));
        properties.add(deviceProperty);
        deviceProperty = new PropertyEntity(OvenPropEnum.RECIPE8.siid, OvenPropEnum.RECIPE8.piid,
                CustomeModeUtils.getCustomeRecipe(CUSTOM_NAME_RECIPE, OvenPropEnum.RECIPE8.piid));
        properties.add(deviceProperty);
        ReportUtils.reportDoubleIotProperties(properties);
    }

    public static void clearCustomeRecipe() {

    }

}


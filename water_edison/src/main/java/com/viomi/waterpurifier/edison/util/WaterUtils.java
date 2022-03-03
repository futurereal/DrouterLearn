package com.viomi.waterpurifier.edison.util;

import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterActionEnum;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterPreference;
import com.viomi.waterpurifier.edison.entity.FilterEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @data:2021/12/16
 */
public class WaterUtils {
    private static final String TAG = "WaterUtils";

    private static final String LOCAL_PROPERTY_PRE = "waterLocal_";
    private static final String LOCAL_SPLITER = "_";

    public static void setLocalWaterProps(WaterPropEnum waterPropEnum, Object waterValue) {
        String waterLocalKey = LOCAL_PROPERTY_PRE + waterPropEnum.siid + LOCAL_SPLITER + waterPropEnum.piid;
        Log.i(TAG, "setLocalWaterProps: key: " + waterLocalKey + " value: " + waterValue);
        WaterPreference.getInstance().setWaterProperty(waterLocalKey, waterValue);
    }

    public static Object getLocalWaterProps(WaterPropEnum waterPropEnum, Object defaultValue) {
        String waterLocalKey = LOCAL_PROPERTY_PRE + waterPropEnum.siid + LOCAL_SPLITER + waterPropEnum.piid;
        Log.i(TAG, "getLocalWaterProps: key: " + waterLocalKey + " defaultValue: " + defaultValue);
        Object objectValue = WaterPreference.getInstance().getWaterProperty(waterLocalKey, defaultValue);
        return objectValue;
    }

    public static List<FilterEntity> getFilterEntityList() {
        Log.i(TAG, "getFilterEntityList: ");
        List<FilterEntity> filterEntityList = new ArrayList<>();
        int[] filterNames = new int[]{R.string.filter_name_carbon, R.string.filter_name_41};
        WaterPropEnum[] filterLifeLevel = new WaterPropEnum[]{WaterPropEnum.FILTER_CARBON_LIFE_LEVEL, WaterPropEnum.FILTER_4IN1_LIFE_LEVEL};
        WaterPropEnum[] filterUseTimes = new WaterPropEnum[]{WaterPropEnum.FILTER_CARBON_USED_TIME, WaterPropEnum.FILTER_4IN1_USED_TIME};
        WaterPropEnum[] filterUseFlows = new WaterPropEnum[]{WaterPropEnum.FILTER_CARBON__USED_FLOW, WaterPropEnum.FILTER_4IN1__USED_FLOW};
        WaterActionEnum[] filterResetActions = new WaterActionEnum[]{WaterActionEnum.RESET_FILTER_CARBON, WaterActionEnum.RESET_FILTER_41};
        int[] qrcodeImgResourceIds = new int[]{R.drawable.filter_qrcode_carbon, R.drawable.filter_qrcode_4in1};
        for (int i = 0; i < filterNames.length; i++) {
            String filterName = ApplicationUtils.getContext().getString(filterNames[i]);
            FilterEntity filterEntity = new FilterEntity();
            filterEntity.setFilterName(filterName);
            WaterPropEnum lifeLeveEnum = filterLifeLevel[i];
            filterEntity.setLifeLevelSiid(lifeLeveEnum.siid);
            filterEntity.setLifeLevelPiid(lifeLeveEnum.piid);
            int lefeLevelValue = (int) PropertyPreferenceManager.getInstance().getProperty(lifeLeveEnum.siid, lifeLeveEnum.piid, 0);
            filterEntity.setLefePercent(lefeLevelValue);
            WaterPropEnum userTimeEnum = filterUseTimes[i];
            filterEntity.setTimeSiid(userTimeEnum.siid);
            filterEntity.setTimePiid(userTimeEnum.piid);
            WaterPropEnum useFlowEnmu = filterUseFlows[i];
            filterEntity.setUseFlowSiid(useFlowEnmu.siid);
            filterEntity.setUseFlowPiid(useFlowEnmu.piid);
            WaterActionEnum filterAction = filterResetActions[i];
            filterEntity.setAiid(filterAction.aiid);
            filterEntity.setSiid(filterAction.siid);
            filterEntity.setQrcodeImgResourceId(qrcodeImgResourceIds[i]);
            filterEntityList.add(filterEntity);
        }
        Log.i(TAG, "getFilterEntityList: " + filterEntityList.size());
        return filterEntityList;
    }
}

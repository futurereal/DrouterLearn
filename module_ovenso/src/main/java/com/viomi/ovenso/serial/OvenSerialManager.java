package com.viomi.ovenso.serial;

import android.util.Log;
import android.widget.Toast;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.serialcontrol.PropertyWrite;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @data:2021/12/13
 */
public class OvenSerialManager {
    private static final String TAG = "OvenSerialManager";

    private static volatile OvenSerialManager mInstance;

    private OvenSerialManager() {
    }

    public static OvenSerialManager getInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        synchronized (OvenSerialManager.class) {
            if (mInstance == null) {
                mInstance = new OvenSerialManager();
            }
        }
        return mInstance;
    }

    public void setLightProperty(boolean isLightOn) {
        boolean isCookReady = isCookReady();
        if (isCookReady == false) {
            return;
        }
        Log.i(TAG, "setLightProperty: isLightOn :" + isLightOn);
        PropertyWrite lightWrite = new LightWrite();
        lightWrite.initPropertyEntity();
        lightWrite.executeWrite(isLightOn);
    }

    public void setPannelProperty(boolean isPannelOn) {
        boolean isCookReady = isCookReady();
        if (isCookReady == false) {
            return;
        }
        Log.i(TAG, "setPannelProperty: isPannelOn: " + isPannelOn);
        PropertyWrite pannelWrite = new PannerWrite();
        pannelWrite.initPropertyEntity();
        pannelWrite.executeWrite(isPannelOn);
    }

    public void setOtherProperty(PropertyEntity propertyEntity) {
        Log.i(TAG, "setOtherProperty: ");
        boolean isCookReady = isCookReady();
        if (isCookReady == false) {
            return;
        }
        List<PropertyEntity> propertyEntities = new ArrayList<>();
        propertyEntities.add(propertyEntity);
        writePropertyList(propertyEntities);
    }

    public void writePropertyList(List<PropertyEntity> propertyEntities) {
        Log.i(TAG, "setPropertyList: ");
        boolean isCookReady = isCookReady();
        if (isCookReady == false) {
            Log.i(TAG, "writePropertyList: isCookRead : " + isCookReady);
            return;
        }
        OvenPropertyListWrite ovenPropertyListWrite = new OvenPropertyListWrite(propertyEntities);
        ovenPropertyListWrite.executeWrite();
    }

    /**
     * 启动烹饪
     */
    private boolean isCookReady() {
        PropertyPreferenceManager propertPreference = PropertyPreferenceManager.getInstance();
        boolean isDoorClose = (boolean) propertPreference.getProperty(OvenPropEnum.DOOR_OPEN.siid, OvenPropEnum.DOOR_OPEN.piid, false);
        if (!isDoorClose) {
            ViomiToastUtil.showToastNormal(ApplicationUtils.getContext().getString(R.string.oven_cookparam_dooropened), Toast.LENGTH_SHORT);
            return false;
        }
        if (OvenUtil.isShowFaultDialog()) {
            Log.i(TAG, "judgeAndstartCook:  showFulatDialog  return");
            return false;
        }
        return true;
    }

    /**
     * 米家标准协议的定义， 主要是  siid = 2 里面的，包含 启动 ，暂停， 结束烹饪
     * 需要传递属性status 与 status 相关
     *
     * @param ovenActionEnum
     */
    public void doStandardAction(OvenActionEnum ovenActionEnum) {
        OvenWorkStatusEnum ovenWorkStatusEnum = OvenUtil.getTargetWorkStaus(ovenActionEnum.siid, ovenActionEnum.aiid);
        Log.i(TAG, "doStandardAction: " + ovenWorkStatusEnum.value);
        List<PropertyEntity> paramsProp = new ArrayList<>();
        PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.WORK_STATUS.siid,
                OvenPropEnum.WORK_STATUS.piid, ovenWorkStatusEnum.value);
        paramsProp.add(propertyEntity);
        SerialControl.setAction(ovenActionEnum.name, ovenActionEnum.siid, ovenActionEnum.aiid, paramsProp);
    }

    /**
     * @param ovenActionEnum 预约之外的其他 米家的非标准协议  包括 取消预约 清除故障位 不需要带属性
     * @param isCheckReady   是否校验 状态是否正常
     */
    public void doOtherCustomAction(OvenActionEnum ovenActionEnum, boolean isCheckReady, List<PropertyEntity> propertyEntityList) {
        Log.i(TAG, "doOtherCustomAction:isCheckReady: " + isCheckReady);
        // 如果是清除故障位的action，不进行判断
        if (ovenActionEnum.siid == ovenActionEnum.ACTION_CANCLE_PREPARE.siid && ovenActionEnum.aiid == OvenActionEnum.ACTION_CLEAR_FAULT.aiid) {
            isCheckReady = false;
        }
        if (isCheckReady && !isCookReady()) {
            Log.i(TAG, "doOtherCustomAction:  iscookReady false return");
            return;
        }
        if (propertyEntityList == null) {
            propertyEntityList = new ArrayList<>();
        }
        if (propertyEntityList.size() == 0) {
            PropertyEntity propertyEntity = new PropertyEntity(CommonConstant.ACTION_SID_NOVALUE,
                    CommonConstant.ACTION_PID_NOVALUE, CommonConstant.ACTION_VALUE_NOVALUE);
            propertyEntityList.add(propertyEntity);
        }
        SerialControl.setAction(ovenActionEnum.name, ovenActionEnum.siid, ovenActionEnum.aiid, propertyEntityList);
    }

    /**
     * 预约模式的传值
     *
     * @param dishId
     * @param appointTime
     */
    public void doAppointAction(int dishId, int appointTime) {
        Log.i(TAG, "doAppointAction: ");
        boolean isCookReady = isCookReady();
        if (isCookReady == false) {
            return;
        }
        List<PropertyEntity> propertyEntities = new ArrayList<>();
        PropertyEntity propertyDish = new PropertyEntity(OvenConstants.ACITON_PARAMETER_SID, OvenPropEnum.DISHID.piid, dishId);
        propertyEntities.add(propertyDish);
        PropertyEntity propertyTime = new PropertyEntity(OvenConstants.ACITON_PARAMETER_SID, OvenPropEnum.APPOINT_TOTAL_TIME.piid, appointTime);
        propertyEntities.add(propertyTime);
        SerialControl.setAction(OvenActionEnum.ACTION_APPOINT.name, OvenActionEnum.ACTION_APPOINT.siid, OvenActionEnum.ACTION_APPOINT.aiid, propertyEntities);
    }


}

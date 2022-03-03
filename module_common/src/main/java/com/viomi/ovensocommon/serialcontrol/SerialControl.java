package com.viomi.ovensocommon.serialcontrol;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.viomi.common.R;
import com.viomi.iotdevice.common.callback.ProgressCallback;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.CommonPreference;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Ljh on 2020/9/21.
 * Description:Android屏向MCU 主板下发命令方法
 */
public class SerialControl {
    private static final String TAG = "SerialControl";
    public static final int DOWN_WRITE_SCCESS = 0;
    public static Map<Integer, int[]> allPropertyMap;

    /**
     * 设置方法
     *
     * @param actionName
     * @param sid
     * @param aid
     * @param paramsProp
     */
    public static void setAction(String actionName, int sid, int aid, List<PropertyEntity> paramsProp) {
        Log.i(TAG, "setAction: actionName: " + actionName);
        if (!checkMcuAviable()) {
            return;
        }
        ActionSendManager.getInstance().sendActionByName(actionName, sid, aid, paramsProp, (propertyName, code) -> {
            Log.d(TAG, "setAction: " + propertyName + " code: " + code); // propertyName 为空
            if (code != DOWN_WRITE_SCCESS) {
                String serialDisconnect = Utils.getApp().getString(R.string.muc_set_fail);
                ViomiToastUtil.showToastCenter(serialDisconnect);
                return;
            }
            ViomiRxBus.getInstance().post(CommonConstant.MSG_ACTION_SUCCESS);
        });
    }

    /**
     * 获取设备的单个属性存在sp里面
     *
     * @param prop
     */
    public static void getProp(OvenPropEnum prop) {
        PropertyGetManager.getInstance().getPropertyByName(prop.name, prop.siid, prop.piid, null);
    }

    public static void getProp(WaterPropEnum prop) {
        PropertyGetManager.getInstance().getPropertyByName(prop.name, prop.siid, prop.piid,
                (propertyName, result) -> Log.d(TAG, "getProp: propertyName: " + propertyName + " result: " + result.toString()));
    }

    public static void getPropertyList(List<PropertyEntity> propertyEntities) {
        PropertyGetManager.getInstance().getPropertyList(propertyEntities, (propertyName, result) ->
                Log.d(TAG, "getProp: propertyName: " + propertyName + " result: " + result.toString()));

    }


    public static void setAllPropertyMap(Map<Integer, int[]> propertyMap) {
        allPropertyMap = propertyMap;
    }

    /**
     * 获取设备的所有属性存在sp 里面并且上报
     * 这个不涉及到更新界面所以不需要处理
     * 1 、MCU升级完成。
     * 2 、绑定设备完成。
     * 3 、网络状态变化，从不联网到联网
     */
    public static void getMucPropertiesAndReport() {
        if (allPropertyMap == null || allPropertyMap.size() == 0) {
            Log.d(TAG, "upLoadAllEroProperty: please set property group！");
            return;
        }
        Log.d(TAG, "getAllProperty: " + allPropertyMap.size());
        List<ViotGetPropRequestBody.Prop> propList = new ArrayList<>();
        for (Integer sid : allPropertyMap.keySet()) {
            int[] pids = allPropertyMap.get(sid);
            for (int pid : pids) {
                ViotGetPropRequestBody.Prop prop = new ViotGetPropRequestBody.Prop();
                prop.sid = sid;
                prop.pid = pid;
                propList.add(prop);
            }
        }
        PropertyGetManager.getInstance().getProperty(propList, null);
    }

    /**
     * 这个后续处理
     *
     * @param path
     * @param firmUpdateListener
     */
    public static void writeFileToMcu(String path, FirmUpdateListener firmUpdateListener) {
        Log.i(TAG, "writeFileToMcu: path: " + path);
        PropertyWriteManager.getInstance().getSerialManager().mcuOta(path, new ProgressCallback() {
            @Override
            public void onResult(boolean isProcessing, int progress, String desc) {
                Log.i(TAG, "writeFileToMcu: progress: " + progress + " desc: " + desc);
                Log.i(TAG, "writeFileToMcu: firmUpdateListener: " + firmUpdateListener);
                if (firmUpdateListener != null) {
                    firmUpdateListener.onResult(isProcessing, progress, desc);
                }
            }
        });
    }


    /**
     * 检查固件是否可用
     *
     * @return
     */
    public static boolean checkMcuAviable() {
        if (CommonPreference.getInstance().getSerialDisconnect()) {
            // 检测串口是否断开
//            OvenRxBus.getInstance().post(OvenRxBusEvent.MSG_COMMUNICATE_DISCONNECT);
            Log.i(TAG, "isMcuAvaible: serial is disconnect return false");
            String serialDisconnect = Utils.getApp().getString(R.string.serial_disconnect);
            Log.i(TAG, "checkMcuAviable: thread " + Thread.currentThread().getName());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ViomiToastUtil.showToastCenter(serialDisconnect);
                }
            });
            return false;
        }
        // 检测固件是否在升级
        if (CommonPreference.getInstance().getMcuUpgrade()) {
            ViomiRxBus.getInstance().post(CommonConstant.MSG_TO_MCU_UPGRADE);
            Log.i(TAG, "isMcuAvaible: mcu upgrading return false");
            String mucUpgradingStr = Utils.getApp().getString(R.string.muc_isupgrading);
            ViomiToastUtil.showToastCenter(mucUpgradingStr);
            return false;
        }
        Log.i(TAG, "isMcuAvaible: true");
        return true;
    }

    public static void openSerialPortAndUploadProperty() {
        Log.i(TAG, "openSerialPortAndUploadProperty: ");
        getMucPropertiesAndReport();
    }

    public interface FirmUpdateListener {
        void onResult(boolean isProcessing, int progress, String desc);
    }
}


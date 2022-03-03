package com.viomi.waterpurifier.edison.util;


import android.util.Log;

import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.entity.WaterErrorEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hailang
 * 2020/9/14
 */
public class DeviceFaultErrorParser {
    private static final String TAG = "DeviceFaultErrorParser";
    private static final Map<Integer, String> errorMap = new HashMap<>();

    static {
        errorMap.put(0, "进水温度传感器异常");
        errorMap.put(1, "出水温度传感器异常");
        errorMap.put(2, "干烧温度传感器异常");
        errorMap.put(3, "加热器异常");
        errorMap.put(4, "超温故障");
        errorMap.put(5, "E模堆异常");
        errorMap.put(6, "出水流量计异常");
        errorMap.put(7, "TDS超高提醒");
        errorMap.put(8, "系统压力异常");
    }

    /**
     * 获取一个新的故障位
     */
    public static WaterErrorEntity getNewError(int newError) {
        List<Integer> newErrorPositionList = getErrorPositions(newError);
        int oldError = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.EQUIPMENT_FAULT.siid, WaterPropEnum.EQUIPMENT_FAULT.piid, 0);
        Log.i(TAG, "getNewError: oldError ;" + oldError + "  newError: " + newError);
        WaterErrorEntity waterErrorEntity = null;
        List<Integer> oldErrorPositionList = getErrorPositions(oldError);
        for (int newPosition : newErrorPositionList) {
            if (!oldErrorPositionList.contains(newPosition)) {
                waterErrorEntity = new WaterErrorEntity();
                waterErrorEntity.setIndex(newPosition);
                waterErrorEntity.setTitle(getErrorTitle(newPosition));
                waterErrorEntity.setDiscription(getErrorDescription());
                break;
            }
        }
        Log.i(TAG, "getNewError: waterErrorEntity : " + waterErrorEntity);
        return waterErrorEntity;
    }

    /**
     * 获取所有的设备故障
     *
     * @param error 错误标识为的值
     * @return 错误的对象集合
     */
    public static List<WaterErrorEntity> getErrorList(int error) {
        List<WaterErrorEntity> waterErrorEntityList = new ArrayList<>();
        List<Integer> errorPositions = getErrorPositions(error);
        for (int errpositon : errorPositions) {
            WaterErrorEntity waterErrorEntity = new WaterErrorEntity();
            waterErrorEntity.setIndex(errpositon);
            waterErrorEntity.setTitle(getErrorTitle(errpositon));
            waterErrorEntity.setDiscription(getErrorDescription());
            waterErrorEntityList.add(waterErrorEntity);
        }
        Log.i(TAG, "getErrorList: " + waterErrorEntityList.size());
        return waterErrorEntityList;
    }

    private static String getErrorTitle(int index) {
        return errorMap.get(index);
    }

    private static String getErrorDescription() {
        return "请联系客服400-6453-2837";
    }

    /**
     * @param error 故障数字
     * @return 所有故障为的集合
     */
    private static List<Integer> getErrorPositions(int error) {
        List<Integer> errorPositionList = new ArrayList<>();
        byte[] errorByteArray = longToByteArray(error);
        for (int i = 0; i < errorByteArray.length; i++) {
            if (errorByteArray[i] == 1) {
                errorPositionList.add(i);
            }
        }
        return errorPositionList;
    }

    private static byte[] longToByteArray(long number) {
        byte[] statusByte = new byte[32];
        long tempNumber = number;
        for (int i = 0; i < statusByte.length; i++) {
            statusByte[i] = (byte) (tempNumber & 1);
            tempNumber = tempNumber >> 1;
        }
        return statusByte;
    }
}

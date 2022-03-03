package com.viomi.ovenso.bean;

import android.view.View;

public enum OvenWorkStatusEnum {
    /**
     * 可以从  工作状态  直接到 空闲状态
     * 可以从  工作状态 ---》 完成状态 ---》 空闲状态
     */
    IDLE(0, "空闲中"),
    WORKING(1, "工作中"),
    PAUSE(2, "已暂停"),
    BOOKED(3, "预约中"),
    /**
     * 工作完成，屏端弹框，语音播报， 插件不需要处理
     */
    COMPLETED(4, "烹饪完成");
    public int value;
    public String name, flagName;
    public int vContinue = View.GONE, vPause = View.GONE, vOVer = View.GONE, vFinish = View.GONE, vNowTemp = View.VISIBLE, vRotate1 = View.VISIBLE, vRotate2 = View.VISIBLE;

    OvenWorkStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
        flagName = "工作中";
        if (value == 1) {
            vPause = vOVer = View.VISIBLE;
        } else if (value == 2) {
            vContinue = vOVer = View.VISIBLE;
        } else if (value == 3) {
            flagName = "预计结束时间";
            vOVer = View.VISIBLE;
            vNowTemp = View.INVISIBLE;
            vRotate2 = View.GONE;
        } else if (value == 4) {
            vFinish = View.VISIBLE;
            vRotate1 = vRotate2 = View.GONE;
        }
    }
}

package com.miotspecv2.defined.viomi;

import android.view.View;

public enum WorkStatusType {
    IDLE(0, "空闲中"),
    WORKING(1, "工作中"),
    PAUSE(2, "已暂停"),
    BOOKED(3, "预约中"),
    COMPLETED(4, "已结束"),
    UPGRADING(10, "升级中");

    public int value;
    public String name, flagName;
    public int vContinue = View.GONE, vPause = View.GONE, vOVer = View.GONE, vFinish = View.GONE,vNowTemp = View.VISIBLE,vRotate1 = View.VISIBLE,vRotate2 = View.VISIBLE;

    WorkStatusType(int value, String name) {
        this.value = value;
        this.name = name;
        flagName = "剩余时间";
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

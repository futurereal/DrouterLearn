package com.viomi.ovenso.bean;

/**
 * Created by Ljh on 2020/9/21.
 * Description:事件上报，用于推送消息给 米家或者云米商城 手机app
 */
public enum OvenEventEnum {
    // 缺水
    EVENT_LOW_WATER(2, 1, "EVENT_LOW_WATER"),
    // 烹饪完成
    EVENT_COOK_COMPLETION(3, 1, "EVENT_WORK_RECORD"),
    // 预约相关的
    EVENT_BOOK(3, 2, "EVENT_WORK_RECORD");
    public int siid;
    public int eiid;
    public String name;

    OvenEventEnum(int siid, int eiid, String name) {
        this.siid = siid;
        this.eiid = eiid;
        this.name = name;
    }
}


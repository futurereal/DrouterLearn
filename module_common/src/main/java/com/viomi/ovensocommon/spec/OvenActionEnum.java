package com.viomi.ovensocommon.spec;

/**
 * Created by Ljh on 2020/9/21.
 * Description:方法
 */
public enum OvenActionEnum {
    //烹饪 米家 标准协议， 下发action 需要带 status 属性
    ACTION_START(2, 1, "start-cook"),
    ACTION_OVER(2, 2, "cancel-cooking"),
    ACTION_PAUSE(2, 3, "pause"),
    /**
     * 其他非标准协议，action 只用带 需要的参数
     */
    // 主要是云米平台 用来设置 定义菜谱，或者启动烹饪。删除自定义菜谱
    ACTION_SET_DISH(3, 1, "ACTION_SET_DISH"),//创建菜谱，可创建菜谱数量最多8个，id为101-108。 （携带参数有意义）
    ACTION_DEL_DISH(3, 2, "deletedish"),//删除菜谱（携带参数有意义）
    // 清除故障位 公用
    ACTION_CLEAR_FAULT(3, 9, "clearfault"),
    // 启动和取消预约  公用
    ACTION_CANCLE_PREPARE(3, 8, "cancelprepare"),
    ACTION_APPOINT(3, 10, "ACTION_APPOINT");   // 预约

    public int siid;
    public int aiid;
    public String name;

    OvenActionEnum(int siid, int aiid, String name) {
        this.siid = siid;
        this.aiid = aiid;
        this.name = name;
    }
}


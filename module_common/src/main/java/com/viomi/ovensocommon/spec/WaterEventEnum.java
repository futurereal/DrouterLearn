package com.viomi.ovensocommon.spec;

/**
 * @author lixinqi on
 * @date 2021/08/31
 * @describe 净水器event屬性
 */
public enum WaterEventEnum {
    // 滤芯寿命到期
    EVENT_FILTER_LIFE_END(7, 1, "EVENT_FILTER_LIFE_END"),
    // 滤芯寿命即将到期
    EVENT_FILTER_LIFE_LOW(7, 2, "EVENT_FILTER_LIFE_LOW"),
    // 滤芯寿命重置
    EVENT_FILTER_LIFE_RESET(7, 3, "EVENT_FILTER_LIFE_RESET"),
    // 本次制水事件
    EVENT_PURE_WATER_RECORD(9, 1, "EVENT_PURE_WATER_RECORD");

    public int siid;
    public int piid;
    public String name;

    WaterEventEnum(int siid, int piid, String name) {
        this.siid = siid;
        this.piid = piid;
        this.name = name;
    }
}

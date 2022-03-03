package com.viomi.ovensocommon.spec;

/**
 * @author lixinqi on
 * @date 2021/08/31
 * @describe 净水器action屬性
 */
public enum WaterActionEnum {
    // 4in1 重置冲洗 滤芯
    RESET_FILTER_41(3, 1, "ACTION_RESET_FILTER_LIFE_41"),
    // RO 重置冲洗滤芯
    RESET_FILTER_CARBON(6, 1, "ACTION_RESET_FILTER_LIFE_RO"),

    // 开启冲洗滤芯功能
    ACTION_ENABLE_RINSE(9, 1, "ACTION_ENABLE_RINSE"),
    // 关闭冲洗滤芯功能
    ACTION_DISABLE_RINSE(9, 2, "ACTION_DISABLE_RINSE"),

    // 系统启动制水
    ACTION_SYS_START(16, 1, "ACTION_SYS_START"),
    // 系统停止制水
    ACTION_SYS_STOP(16, 2, "ACTION_SYS_STOP");

    public int siid;
    public int aiid;
    public String name;

    WaterActionEnum(int siid, int aiid, String name) {
        this.siid = siid;
        this.aiid = aiid;
        this.name = name;
    }
}

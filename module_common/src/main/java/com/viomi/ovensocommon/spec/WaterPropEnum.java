package com.viomi.ovensocommon.spec;

/**
 * @author by xinqi on 2021-07-14.
 * @describe 屬性
 */
public enum WaterPropEnum {
    // 固件产品信息
    FIRM_MANUFACTURE(1, 1, "Manufacture"),
    FIRM_MODEL(1, 2, "firmModel"),
    FIRM_SERIAL_NO(1, 3, "firmSerialNo"),
    FIRM_VERSION(1, 4, "firmVersion"),
    // 设备故障
    EQUIPMENT_FAULT(2, 1, "EQUIPMENT_FAULT"),
    // 工作状态 有好多
    EQUIPMENT_STATE(2, 2, "EQUIPMENT_STATE"),

    // 滤芯剩余寿命:4in1滤芯
    FILTER_4IN1_LIFE_LEVEL(3, 1, "FILTER_LIFE_LEVEL_41"),
    FILTER_4IN1_LIFE_TIME(3, 2, "FILTER_LIFE_TIME_41"),
    // 41滤芯已使用时间
    FILTER_4IN1_USED_TIME(3, 3, "filter-used-time"),
    // 41滤芯已使用流量
    FILTER_4IN1__USED_FLOW(3, 4, "filter-used-flow"),
    // 41滤芯剩余流量
    FILTER_4IN1_LEFT_FLOW(3, 5, "FILTER_LEFT_FLOW_41"),

    // 滤芯剩余寿命:后置滤芯 CARBON
    FILTER_CARBON_LIFE_LEVEL(6, 1, "FILTER_LIFE_LEVEL_RO"),
    // 滤芯剩余时间
    FILTER_CARBON_LIFE_TIME(6, 2, "FILTER_LIFE_TIME_RO"),
    // ro滤芯已使用时间
    FILTER_CARBON_USED_TIME(6, 3, "filter-used-time"),
    // RO滤芯已使用流量
    FILTER_CARBON__USED_FLOW(6, 4, "filter-used-flow"),
    // RO滤芯剩余流量
    FILTER_CARBON__LEFT_FLOW(6, 5, "FILTER_LEFT_FLOW_RO"),

    // 滤芯编号？？？
    FILTER_ID(7, 1, "FILTER_ID"),
    // siid9   统计详情 用于插件显示用户的情况
    // 本次制水进水水水质（平均值）
    TDS_IN(9, 5, "TDS_IN"),
    // 本次制水出水水质（平均值）
    TDS_OUT(9, 6, "TDS_OUT"),
    // 是否正在冲洗滤芯  false or true
    RINSE(9, 7, "isFLushFileter"),
    // 本次制水时长 s 为单位
    USE_TIME(9, 10, "USE_TIME"),
    // 本次制水自来水进水量 ml
    USE_FLOW_IN(9, 11, "USE_FLOW_IN"),
    // 本次制水纯水出水量ml
    USE_FLOW_OUT(9, 12, "USE_FLOW_OUT"),
    // 累计使用时间
    CUMU_TIME(9, 13, "CUMU_TIME"),
    // 累计进水总量ml
    CUMU_FLOW_IN(9, 14, "CUMU_FLOW_IN"),
    // 累计出水总量ml
    CUMU_FLOW_OUT(9, 15, "CUMU_FLOWOUT"),

    // 出水状态&ID   Idle112  Busy112
    WATER_OUT_STATUS(9, 16, "WATER_OUT_STATE_ID"),
    // 进水温度
    TEMPERATURE_WATER_IN(9, 17, "TEMPERATURE_WATER_IN"),
    // 滤芯冲洗的百分比
    FILTER_RESET_PROGRESS(9, 18, "RINSE_PERCENT"),
    // 童锁
    CHILD_LOCK(10, 1, "CHILD_LOCK"),
    // 屏幕待机
    STANDBY(11, 1, "STANDBY"),

    CUP_STOP(12, 1, "CUP_STOP"),
    UV_STATUE(18, 1, "UV_STATUE"),

    // 设备故障
    FAULT(15, 1, "FAULT"),

    // 温水温度
    TEMP_WARM(16, 1, "TEMP_WARM"),
    // 热水温度
    TEMP_HOT(16, 2, "TEMP_HOT"),
    // 开水温度
    TEMP_BOILING(16, 3, "TEMP_BOILING"),
    // 小杯的设置流量
    SMALL_CUP_FLOW(16, 4, "SMALL_CUP_FLOW"),
    // 中杯的设定流量
    MIDDLE_CUP_FLOW(16, 5, "MIDDLE_CUP_FLOW"),
    // 大杯的设定流量
    BIG_CUP_FLOW(16, 6, "BIG_CUP_FLOW"),
    // 温度模式
    TEMP_MODE(16, 7, "TEMP_MODE"),
    // 杯量模式
    CUP_MODE(16, 8, "CUP_MODE"),
    // 本次制水设置温度
    SET_TEMP(16, 9, "set-temp"),
    // 本次制水设置水量
    SET_FLOW(16, 10, "set-flow"),

    // 矿物质含量等级  1 ，2 ，3   三档
    MINERAL_TYPE(17, 1, "MINERAL_TYPE"),
    // 当前水质档  0 纯净水    1 矿物质
    WATER_QUALITY(17, 2, "WATER_QUALITY"),

    // 自清洁属性   0自清洁完成   1即将进入自清洁    2自清洁中
    SELF_CLEAN_MODE(19, 1, "SELF_CLEAN_MODE"),
    // 老化模式
    //工厂模式
    FACTORY(20, 1, "FACTORY"),
    // 杀菌模式 0代表关闭，1代表开启， 只有在工厂模式下才可以开
    FACTORY_DISINFECT(20, 2, "FACTORY_DISINFECT"),

    // 1代表缺水，0代表正常
    CISTERN(21, 1, "CISTERN"),
    // 1 代表 需要换水，  0 代表 换水完成
    CHANGE_WATER(21, 2, "CHANGE_WATER");

    public int siid;
    public int piid;
    public String name;

    WaterPropEnum(int siid, int piid, String name) {
        this.siid = siid;
        this.piid = piid;
        this.name = name;
    }

    public static WaterPropEnum findPropEnum(int sid, int pid) {
        WaterPropEnum foundProp = null;
        for (WaterPropEnum prop : WaterPropEnum.values()) {
            if (prop.siid == sid && prop.piid == pid) {
                foundProp = prop;
                break;
            }
        }
        return foundProp;
    }

}

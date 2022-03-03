package com.viomi.waterpurifier.edison;

/**
 * WaterBusEvent 消息定义
 */
public class WaterBusEvent {
    public static final int MSG_SETTING_THEME_CHANGE = 301;
    public static final int MSG_FLUSH_FINISH = 302;
    /**
     * 主题切换时间的控制
     */
    public static final int MSG_THEME_TIME_CHANGE = 306;
    public static final int MSG_FILTER_PROPERTY = 311;
    // 水箱缺水
    public static final int MSG_CISTERN_CHANGE = 312;
    public static final int MSG_OTHER_PROPERTY = 314;
    public static final int MSG_FILTER_FLUSH = 315;
    public static final int MSG_PROPERTY_CUPMODE = 316;
    public static final int MSG_PROPERTY_CUPFLOW = 317;
    public static final int MSG_PROPERTY_TEMPERATURE = 318;
    public static final int MSG_PROPERTY_TEMPERATURE_CUSTM = 325;
    public static final int MSG_PROPERTY_MINERAL = 319;
    public static final int MSG_PROPERTY_MINERAL_TYPE = 320;
    public static final int MSG_PROPERTY_WATER_FLOW = 321;
    public static final int MSG_PROPERTY_TEMP_MODE = 322;
    public static final int MSG_SELFCLEAN_COUNTDOWN = 323;
    public static final int MSG_FILTER_WASH_PROGRESS = 324;
    public static final int MSG_PROPERTY_WATER_STATUS = 326;
    public static final int MSG_CHILDLOCK_SUCCESS = 327;

}

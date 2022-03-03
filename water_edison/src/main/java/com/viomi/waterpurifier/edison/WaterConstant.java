package com.viomi.waterpurifier.edison;

/**
 * @description:
 * @data:2021/10/25
 */
public class WaterConstant {
    // 童锁默认打开
    public static final boolean CHILD_LOCK_DEFAULT = true;
    public static final boolean IS_TEST_UI = true;
    public static int DEFAULT_SMALL_CUP_FLOW = 250;
    public static int DEFAULT_MID_CUP_FLOW = 500;
    public static int DEFAULT_BIG_CUP_FLOW = 750;

    public static int DEFAULT_NORMAL_WATER_TEMP = 25;
    public static int DEFAULT_WARM_WATER_TEMP = 40;
    public static int DEFAULT_HOT_WATER_TEMP = 75;
    public static int DEFAULT_BOILING_WATER_TEMP = 100;

    // 纯净水
    public static final int DEFAULT_WATER_QUALITY = 0;
    public static final int DEFAULT_CUP_MODE = 1;
    public static final int DEFAULT_TEMPERATURE_MODE = 0;
    // 默认的矿物质含量
    public static final int DEFAULT_MINERAL_TYPE = 1;

    // 缺水状态
    public static final int STATE_LACK_WATER = 1;
    // 滤芯寿命报错边界值
    public static final int FILTER_ERROR_MARGIN = 10;

    public static final int SELFCLEAN_IDLE = 0;
    public static final int SELFCLEAN_BEGAIN = 1;
    public static final int SELFCLEAN_DOING = 2;

    public static final String WATEROUT_PURING = "purifing";
    public static final String WATEROUT_IDLE = "idle";

    public static final String KEY_FILTERENTIY = "keyFilterEntity";
}

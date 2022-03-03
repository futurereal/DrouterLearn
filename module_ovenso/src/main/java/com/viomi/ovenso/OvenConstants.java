package com.viomi.ovenso;

import com.viomi.ovenso.bean.OvenThemeType;

public class OvenConstants {
    /**
     * 在没有接电控的情况下进行UI测试
     */
    public static boolean IS_TEST_UI = false;
    public static final boolean IS_OTA_UPDATE = true;
    public static final String KEY_THEME_OVENSO = "keyThemeOvenso";
    public static final String THEME_NAME_DEFAULT = OvenThemeType.THEME1.themeDrawable;
    public static final int MODE_ID_CHUGOU = 40;
    public static final int MODE_ID_CHUWEI = 42;
    public static final int MODE_ID_MICRWAVE = 34;
    public static final int MODE_ID_MICRWAVE_STREAM = 37;
    public static final int MODE_ID_MICRWAVE_STREAM_BAKE = 38;
    public static final int MODE_ID_CLEAN = 33;
    // 微波边界温度， 小于这个温度代表是微波
    public static final int MICRO_LEVEL_MARGIN = 10;
    public static final float MICRO_TIME_STEP = 0.5f;
    public static final String MODE_TYPE_MICROWAVE = "微波";
    public static final String MODE_TYPE_STREAM = "蒸";
    public static final String MODE_TYPE_BAKE = "烤";
    public static final String MODE_TYPE_SINGLE = "单模式";
    public static final String COOKTIME_UNIT_NAME = "分钟";
    public static Integer CMD_SET_SUCCESS = 0;
    public static final int MODE_ID_ADD = 99;

    // action 参数的sid
    public static final int ACITON_PARAMETER_SID = 1;
    // action 定义的 无意义的参数的sid pid 和 content，只是为了平台，没有任何意义和用途
    public static final String SPLITER = " | ";
    public static final int CLICK_MIN_TIME = 1000;
    // 当前 没有菜谱定义  和协议上的 3 ， 1 ，0  DangQianMeiYouCaiPu 对应
    public static final int DISHID_NO_RECIPE = 0;
    /**
     * 屏端自定义菜谱
     */
    public static final int MODEID_SCREEN_CUSTOMMODE = 109;

    // 根据属性 CUSTOM_MODE_STEP  来定义的
    public static final int COOKSTEP_TWO = 1;
    public static final int COOKSTEP_THREE = 2;
}

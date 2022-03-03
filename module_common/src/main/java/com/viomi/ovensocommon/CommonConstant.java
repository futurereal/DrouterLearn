package com.viomi.ovensocommon;

public class CommonConstant {
    // 显示测试信息
    public static boolean SHOW_DEBUG_INFO = true;
    public static boolean TEST_UI = false;
    public static final int MIN_CLICK_TIME_MS = 300;
    public static final String VIOMI_NAME = "viomi";
    public static final String VIOMI_NAME_FRIEDLY = "Viomi";
    // 串口相关的日志 开启或者关闭  和Viot 相关日志IDE开关
    public static final boolean VIOT_LOG_OPEN = true;
    public static final boolean SERIAL_LOG_OPEN = true;

    public static final int MSG_COMMUNICATE_DISCONNECT = 1;
    public static final int MSG_COMMUNICATE_CONNECTED = 2;
    public static final int MSG_COMMUNICATE_DATA_ERR = 3;
    public static final int MSG_TO_MCU_UPGRADE = 4;
    public static final int MSG_EVENT_CHANGE = 5;
    // 童锁开关
    public static final int MSG_CHILDLOCK_SWITCH = 6;
    public static final int MSG_CMD_FINISH = 7;
    public static final int MSG_CMD_PROGRESS = 8;
    public static final int MSG_STOP_CAMERA = 9;
    public static final int MSG_FINISH_ACTIVITY = 10;
    public static final int MSG_WIFI_STATUS_CHANGE = 11;
    public static final int MSG_DOWNWRITE_SUCCESS = 12;
    public static final int MSG_ACTION_SUCCESS = 13;
    public static final int MSG_PROPERTY_CHANGE = 14;
    //    public static final int MSG_FINISH_CAMERA_ACTIVITY = 15;
    public static final int MSG_SHOW_EDITE_FRAGMENT = 16;

    public static final int MENU_INDEX_WIFI = 1;
    // 相机模块 和 烤箱模块公用
    public static final int RECORD_STATE_FINISH = 0;
    public static final int RECORD_STATE_PAUSE = 1;
    public static final int RECORD_STATE_RECORDING = 2;
    public static final int SERIAL_RESULTCODE_SUCCESS = 0;


    public static final int SERIAL_MODE_SIID = 1;
    public static final int SERIAL_MODE_PIID = 2;
    public static final int SERIAL_VERSION_SIID = 1;
    public static final int SERIAL_VERSION_PIID = 4;

    // 通讯故障的 故障位
    public static final int OVENSO_DEVICE_FALUT_DISCONNECT = 0x01 << 13;
    //协议定义的aciton 没有参数，屏端需要 增加 没有意思的 pid 和value ，配合固件开发。 miot 要用 ，ovenso 要用
    public static final int ACTION_SID_NOVALUE = 1;
    public static final int ACTION_PID_NOVALUE = 1;
    public static final int ACTION_VALUE_NOVALUE = 1;

    public static final int ONE_MINITE_SECOND = 60;

}
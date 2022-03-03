package com.viomi.modulesetting;

/**
 * RxBus 消息定义
 * Created by nanquan on 2018/1/25.
 */
public class ModuleSetingEventConstant {

    /**
     * 登录成功
     */
    public static final int MSG_VIOMI_LOGIN = 102;

    /**
     * 注销成功
     */
    public static final int MSG_VIOMI_LOGOUT = 103;

    public static final int MSG_MIOT_LOGIN = 104;
    public static final int MSG_MIOT_LOGOUT = 105;

    /**
     * 搜索热点信号
     */
    public static final int MSG_SCAN_WIFI_RESULT = 106;

    /**
     * 固件安装完成
     */
    public static final int MSG_FIRMUPDATE_SUCCESS = 108;

    /**
     * SOftAp变化
     */
    public static final int MSG_SOFT_AP_CHANGED = 110;


    /**
     * VIOT 通道绑定失败
     */
    public static final int MSG_BIND_VIOMI_DEVICE_FAIL = 111;
    /**
     *
     */
    public static final int MSG_BIND_DISMISS_QRCODE = 112;
    /**
     * 忽略网络
     */
    public static final int MSG_IGNORE_WIFI = 113;
    /**
     * 输入密码
     */
    public static final int MSG_INPUT_WIFI_PW = 114;

    public static final int MSG_UPDATE_STANDBYTIME = 115;
    public static final int MSG_UPDATE_LOCKTIME = 116;
    public static final int MSG_SCREENOFF_TIME = 117;

    public static final int MSG_WIFI_SWITCH_CHANGE = 118;
}

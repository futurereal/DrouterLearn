package com.viomi.modulesetting;

import com.blankj.utilcode.util.Utils;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib
 * @ClassName: ModuleSettingConstants
 * @Description: 常量
 * @Author: randysu
 * @CreateDate: 2020/3/18 11:28 AM
 * @UpdateUser:
 * @UpdateDate: 2020/3/18 11:28 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class ModuleSettingConstants {

    public static final boolean IS_SHOW_SPEAK = true;
    public static final boolean DEFAULT_SPEAK_SWITCH = true;
    public static final String VERSION_TITLE = "V";
    // 文件保存相关
    public static final String PATH = "viomiFirm";// 所有文件保存主目录
    // 广播
    public static final String BROADCAST_REBOOT = "com.viomi.device.action.dameon.reboot";// 重启系统
    public static final String BROADCAST_RECOVERY = "com.viomi.device.action.dameon.recover";// 恢复出厂设置

    // 重要配置 Key
    public static final String VIOT_APP_KEY = "aEleE5630QBbXu0z";
    public static final long OAUTH_ANDROID_APP_ID = 2882303761517454408L;// 云米 android
    public static final String OAUTH_ANDROID_APP_KEY = "5891745422408";
    public static final long OAUTH_IOS_APP_ID = 2882303761517484785L;// 云米 ios
    public static final String OAUTH_IOS_APP_KEY = "5701748476785";
    //默认设备
    public final static String DefaultDeviceId = "390757311";//"378103600";
    public final static String DefaultMac = "5C:6B:D7:20:62:59";//"5C:6B:D7:11:B6:B4";
    public final static String DefaultMiotToken = "zvaoqFYeT2J1FP1q";//"lHKzRZwcACdfV61x";

    //设备Key相关

    public static final String DEVICE_ID = "1000034774";//
    public static final String ACCESS_KEY = "ruyz21OyQcylUtd3";
    public static final String CLOUD_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHI0SOYQzFLa/EAs1VD6fAhlSqXt4tr2mJy7/I1yKGinSy6ml36SfiDT4u+BXIEagLGf3uqJqqoVoKnrKS3wnf9Hzxj4oGcPG92RtEkQ4PnLwaTKXUrp5UOk9Mc39TZ5koZI8OvUqUUWYblzg/2aNQPtWi6aghUMbI99qcQLhzXQIDAQAB";
    //升级渠道是否为测试环境
    public static final boolean IS_UPGRADE_TEST = false;
    //测试环境切换  测试环境三元组,测试后台的时候需要用
    public static final boolean IS_DEBUG_ENV = false;
    public static final String DEVICE_ID_TEST = "1111308694";
    public static final String ACCESS_KEY_TEST = "fvGzcTttZvGndOQy";
    public static final String CLOUD_PUBLIC_KEY_TEST = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCI8vjX24vj0tZ07LGsC87a2XmcnfNLsKelXovirr7TOlm/C4G6VdcfnjP4rHb0ABuY5rbH116CdXIVIdtkd1qi4+vMSaJMcR3mTfyWAeRsHff5PkyVBhjnEhVqK2IBKvQWcOuWVzekz4U5R2Gut8v8s98wXTBMScmqqZuw8tOdjwIDAQAB";
    /*
    public static final String DEVICE_ID_TEST = "1111308695";
    public static final String ACCESS_KEY_TEST = "71qfDMGcoT9NR5MV";
    public static final String CLOUD_PUBLIC_KEY_TEST = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCI8vjX24vj0tZ07LGsC87a2XmcnfNLsKelXovirr7TOlm/C4G6VdcfnjP4rHb0ABuY5rbH116CdXIVIdtkd1qi4+vMSaJMcR3mTfyWAeRsHff5PkyVBhjnEhVqK2IBKvQWcOuWVzekz4U5R2Gut8v8s98wXTBMScmqqZuw8tOdjwIDAQAB";
    */
    //SP
    public static final String SP_PROP = "Property_";
    public static int SCREENOFF_CLOSE_VALUE = 2147483647;//系统值，熄屏关闭
    //ROM升级apk
    public static final String ROM_UPDATE_PACKAGE = "com.abupdate.fota_demo_iot";
    public static final String ROM_UPDATE_ACTIVITY = "com.abupdate.fota_demo_iot.view.activity.MainActivity";
    //屏自检apk
    public static final String SCREEN_TEST_PACKAGE = "com.cndlcd.factory";
    public static final String SCREEN_TEST_ACTIVITY = "com.cndlcd.factory.DeviceTest";
    //系统工厂测试
    public static final String SYSTEM_FACTORY_PACKAGE = "com.example.table";
    public static final String SYSTEM_FACTORY_ACTIVITY = "com.example.table.MainActivity";
    //运维平台需要，用户登录退出之后把userId 设置为 -1
    public static final String VIOT_UNBIND = "-1";
    public static final String VIOT_NO_BIND = "0";
    public static final String DEFAULT_NAME = "未登录";
    public static final String DEFAULT_HEAD = "android.resource://" + Utils.getApp().getPackageName() + "/" + R.drawable.menuheader_default;
    public static final String KEY_CHILD_LOCK_TIME = "keyguard_timeout";
    public static final String VERSION_PREFIX = "V";

}

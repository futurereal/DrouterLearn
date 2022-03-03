package com.viomi.ovenso.util.preference;

/**
 * Created by Ljh on 2020/9/11.
 * Description:信息存储
 * 汇总SettingPageLib中的SettingPreference和LoginPreference
 */
public class OvenPreference extends BasePreference {
    private static OvenPreference mInstance;

    public static OvenPreference getInstance() {
        if (mInstance == null) {
            synchronized (OvenPreference.class) {
                if (mInstance == null) {
                    mInstance = new OvenPreference();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getPreName() {
        return "com.viomi.device.ovenPre";
    }
}

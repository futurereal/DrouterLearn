package com.viomi.ovensocommon.componentservice.modulesetting;

import com.viomi.ovensocommon.componentservice.IPlugCommonService;

public interface IModuleSettingService extends IPlugCommonService {

    boolean isDeviceBind();

    boolean isViomiLogin();

    boolean isShowMiotBind();

    void setShowMiotBind(boolean showMiotBind);

    void setMenuArrayId(int menuArrayId);

    int getMenuArrayId();

    void setAgingRoutPath(String agingRoutPath);

    String getAgingRoutPath();

    String getMiUserId();

    void miotLoginSuccess();

    void miotLogoutSuccess();

    void setKeepScreenOn(boolean isKeepScreenOn);

    int getScreenOffArrayId();

    void setScreenOffArrayId(int screenOffArrayId);

    void setScreenOffTime(int keepScreenTime);
}

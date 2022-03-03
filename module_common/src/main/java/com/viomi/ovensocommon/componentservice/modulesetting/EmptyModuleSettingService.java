package com.viomi.ovensocommon.componentservice.modulesetting;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;

import java.util.List;
import java.util.Map;

public class EmptyModuleSettingService implements IModuleSettingService {

    @Override
    public boolean isDeviceBind() {
        return false;
    }

    @Override
    public boolean isViomiLogin() {
        return false;
    }

    @Override
    public boolean isShowMiotBind() {
        return false;
    }

    @Override
    public void setShowMiotBind(boolean showMiotBind) {

    }

    @Override
    public void setMenuArrayId(int menuArrayId) {

    }

    @Override
    public int getMenuArrayId() {
        return 0;
    }

    @Override
    public void setAgingRoutPath(String agingRoutPath) {

    }

    @Override
    public String getAgingRoutPath() {
        return null;
    }

    @Override
    public String getMiUserId() {
        return "";
    }

    @Override
    public void miotLoginSuccess() {
    }

    @Override
    public void miotLogoutSuccess() {

    }

    @Override
    public void reportData(Map proMap) {
        
    }

    @Override
    public void reportData(PropertyEntity propertyEntity) {

    }

    @Override
    public void reportData(List<PropertyEntity> reportDataEntities) {

    }


    @Override
    public void reportEvent(int sid, int eid) {

    }

    @Override
    public void reportAction(String eventName) {

    }


    @Override
    public void setKeepScreenOn(boolean isKeepScreenOn) {

    }

    @Override
    public int getScreenOffArrayId() {
        return 0;
    }
    
    @Override
    public void setScreenOffArrayId(int screenOffArrayId) {

    }

    @Override
    public void setScreenOffTime(int keepScreenTime) {

    }
}

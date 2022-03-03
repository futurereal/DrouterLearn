package com.viomi.ovenso.helper;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;

import java.util.List;

public class ReportUtils {

    public static void reportDoubleIot(int sid, int pid, Object value) {
        //属性上报
        PropertyEntity propertyEntity = new PropertyEntity(sid, pid, value);
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntity);
        MiotServiceFactory.getInstance().getMiotService().reportData(propertyEntity);
    }

    public static void reportDoubleIotProperties(List<PropertyEntity> propertyEntityList) {
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntityList);
        MiotServiceFactory.getInstance().getMiotService().reportData(propertyEntityList);
    }

    public static boolean isDeviceBind() {
        boolean isMiotBind = MiotServiceFactory.getInstance().getMiotService().isMiotBind();
        boolean isViotBind = ModuleSettingServiceFactory.getInstance().getViotService().isDeviceBind();
        return isMiotBind | isViotBind;
    }
}

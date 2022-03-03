package com.viomi.modulesetting.service;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.DeviceUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.ModuleSettingApplicaiton;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.utils.ModuleSettingUtil;
import com.viomi.ovensocommon.CommonPreference;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.modulesetting.IModuleSettingService;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.viot.VIotHostManager;
import com.viomi.viot.event.VIotDeviceEvent;
import com.viomi.viot.listener.OnVIotResultListener;
import com.viomi.viot.property.VIotDeviceProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModuleSettingService implements IModuleSettingService {
    private static final String TAG = "ModuleSettingService";
    private int menuArrayId;
    private int screenOffArrayId;
    private String agingRoutPath;
    private boolean showMiotBind;

    public ModuleSettingService() {
    }

    @Override
    public boolean isDeviceBind() {
        UserInfoDb userInfoDb = ModuleSettingApplicaiton.getUserInfoDb();
        String viotUserId = userInfoDb.getUserId();
        Log.i(TAG, "isDeviceBind: viotUserId: " + viotUserId);
        boolean isViotUnbind = TextUtils.isEmpty(viotUserId)
                || TextUtils.equals(viotUserId, ModuleSettingConstants.VIOT_UNBIND)
                || TextUtils.equals(viotUserId, ModuleSettingConstants.VIOT_UNBIND);
        boolean isBindMiot = userInfoDb.isBindMiot();
        Log.i(TAG, "isDeviceBind: isBindMiot: " + isBindMiot);
        return !isViotUnbind || isBindMiot;
    }

    @Override
    public boolean isViomiLogin() {
        return false;
    }

    @Override
    public boolean isShowMiotBind() {
        return showMiotBind;
    }

    @Override
    public void setShowMiotBind(boolean showMiotBind) {
        this.showMiotBind = showMiotBind;
    }

    @Override
    public void setMenuArrayId(int menuArrayId) {
        this.menuArrayId = menuArrayId;
    }

    @Override
    public int getMenuArrayId() {
        return menuArrayId;
    }

    @Override
    public void setAgingRoutPath(String agingRoutPath) {
        this.agingRoutPath = agingRoutPath;
    }

    @Override
    public String getAgingRoutPath() {
        return agingRoutPath;
    }


    @Override
    public String getMiUserId() {
        if (ModuleSettingApplicaiton.getUserInfoDb() == null) {
            return "-1";
        }
        String miotUserId = ModuleSettingApplicaiton.getUserInfoDb().getMiUserId();
        Log.i(TAG, "getMiUserId: miotUserId: " + miotUserId);
        return miotUserId;
    }

    @Override
    public void miotLoginSuccess() {
        Log.i(TAG, "miotLogin: ");
        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_MIOT_LOGIN);
        OvensoServiceFactory.getInstance().getOvenService().MiotLoginStatusChange(true);

    }

    @Override
    public void miotLogoutSuccess() {
        Log.i(TAG, "miotLogout: ");
        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_MIOT_LOGOUT);
        OvensoServiceFactory.getInstance().getOvenService().MiotLoginStatusChange(false);
    }

    @Override
    public void reportData(Map proMap) {
        Iterator<Map.Entry<String, Object>> iterator = proMap.entrySet().iterator();
        String deviceId = ViomiProvideUtil.getDeviceId();
        List<VIotDeviceProperty> deviceProperties = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            // sid  和 pid 集合
            String[] sAndPId = key.split("\\.");
            if (sAndPId == null || sAndPId.length < 2) {
                Log.i(TAG, "props: sAndPId is null or length<2 ");
                break;
            }
            VIotDeviceProperty property = new VIotDeviceProperty(Integer.parseInt(sAndPId[0]), Integer.parseInt(sAndPId[1]),
                    entry.getValue(), deviceId);
            deviceProperties.add(property);
        }
        Log.i(TAG, "reportData: deviceProperties " + deviceProperties.size());
        VIotHostManager.Companion.getInstance().syncProperties(deviceProperties, null);
    }

    @Override
    public void reportData(PropertyEntity propertyEntity) {
        Log.i(TAG, "reportData: single  property ");
        ArrayList<PropertyEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.add(propertyEntity);
        reportData(reportDataEntities);
    }

    @Override
    public void reportData(List<PropertyEntity> reportDataEntities) {
        // 判断是否登录
        if (!isDeviceBind()) {
            Log.i(TAG, "reportData: viot is not bind return ");
            return;
        }
        ArrayList<VIotDeviceProperty> deviceProperties = new ArrayList<>();
        for (PropertyEntity propertyEntity : reportDataEntities) {
            Log.i(TAG, "reportData: " + propertyEntity.getSid() + "   " + propertyEntity.getPid() + "  " + propertyEntity.getContent());
            VIotDeviceProperty vIotDeviceProperty = new VIotDeviceProperty(propertyEntity.getSid(),
                    propertyEntity.getPid(), propertyEntity.getContent(), propertyEntity.getDeviceId());
            deviceProperties.add(vIotDeviceProperty);
        }
        Log.i(TAG, "reportData: deviceProperties size " + deviceProperties.size());
        VIotHostManager.Companion.getInstance().syncProperties(deviceProperties, new OnVIotResultListener() {
            @Override
            public void onSucceed() {
                Log.i(TAG, "onSucceed: ");
            }

            @Override
            public void onFailed(int i, @Nullable String s) {
                Log.i(TAG, "onFailed: s: " + s);
            }
        });
    }

    @Override
    public void reportEvent(int sid, int eid) {
        Log.i(TAG, "reportEvent: ");
        VIotDeviceEvent vIotDeviceEvent = new VIotDeviceEvent(sid, eid, null, DeviceUtils.getUniqueDeviceId());
        // 事件上报
        VIotHostManager.Companion.getInstance().sendEvent(Arrays.asList(vIotDeviceEvent), new OnVIotResultListener() {
            @Override
            public void onSucceed() {
                Log.i(TAG, "onSucceed: ");
            }

            @Override
            public void onFailed(int code, @Nullable String errorTip) {
                Log.i(TAG, "onFailed: code : " + code + " errorTip : " + errorTip);
            }
        });
    }

    @Override
    public void reportAction(String eventName) {

    }


    @Override
    public void setKeepScreenOn(boolean isKeepScreenOn) {
        Log.i(TAG, "setKeepScreenOn: isKeepScreenOn: " + isKeepScreenOn);
        Context context = ApplicationUtils.getContext();
        int beforeScreenOffTime = 0;
        if (isKeepScreenOn) {
            try {
                beforeScreenOffTime = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
                CommonPreference.getInstance().setScreenTime(beforeScreenOffTime);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "setKeepScreenOn: beforeScreenOffTime : on: " + beforeScreenOffTime);
            if (isKeepScreenOn && beforeScreenOffTime != ModuleSettingConstants.SCREENOFF_CLOSE_VALUE) {
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, ModuleSettingConstants.SCREENOFF_CLOSE_VALUE);
            }
        } else {
            beforeScreenOffTime = CommonPreference.getInstance().getKeepScreenTime();
            Log.i(TAG, "setKeepScreenOn: beforeScreenOffTime : off: " + beforeScreenOffTime);
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, beforeScreenOffTime);
        }
    }

    @Override
    public int getScreenOffArrayId() {
        return screenOffArrayId;
    }

    @Override
    public void setScreenOffArrayId(int screenOffArrayId) {
        this.screenOffArrayId = screenOffArrayId;
    }

    @Override
    public void setScreenOffTime(int keepScreenTime) {
        Log.i(TAG, "setScreenOffTime: keepScreenTime: " + keepScreenTime);
        Settings.System.putInt(ApplicationUtils.getContext().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                keepScreenTime);
        String screenOffName = ModuleSettingUtil.getScreenOffTimeName(keepScreenTime);
        Log.i(TAG, "setScreenOffTime: screenOffName: " + screenOffName);
        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_SCREENOFF_TIME, screenOffName);
    }
}

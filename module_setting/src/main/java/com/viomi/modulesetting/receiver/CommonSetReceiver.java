package com.viomi.modulesetting.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.SerialCommunicateManager;
import com.viomi.router.core.ViomiRouter;

public class CommonSetReceiver extends BroadcastReceiver {
    private static final String TAG = "CommonSetReceiver";
    // 串口打开和关闭
    public static final String ACTION_SERIAL_OPEN = "com.viomi.device.ACTION_OPEN_SERIAL";
    public static final String ACTION_SERIAL_CLOSE = "com.viomi.device.ACTION_CLOSE_SERIAL";
    // 锁屏广播，显示童锁界面
    public static final String ACTION_CHILD_LOCK = "viomi.os.action.KEYGUARD_TIMEOUT";
    // WIFI 状态改变的广播
    public static final String ACTION_WIFI_STATUS_CHANGE = WifiManager.WIFI_STATE_CHANGED_ACTION;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: action: " + action);
        if (TextUtils.equals(action, ACTION_SERIAL_OPEN)) {
            SerialCommunicateManager.getInstance().openSerialPort();
        } else if (TextUtils.equals(action, ACTION_SERIAL_CLOSE)) {
            SerialCommunicateManager.getInstance().closeSerialPort();
        } else if (TextUtils.equals(action, ACTION_CHILD_LOCK)) {
            boolean isNeedFobideScreenOff = OvensoServiceFactory.getInstance().getOvenService().needFobideScreenOff();
            Log.i(TAG, "onReceive: isNeedFobideScreenOff: " + isNeedFobideScreenOff);
            if (isNeedFobideScreenOff) {
                return;
            }
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENS0_SCREEN_OFF).navigation();
        } else if (TextUtils.equals(action, ACTION_WIFI_STATUS_CHANGE)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i(TAG, "onReceive: sendWifiSwitchChange:  " + wifiState);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_WIFI_SWITCH_CHANGE, false);
            } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_WIFI_SWITCH_CHANGE, true);
            }

        }

    }
}

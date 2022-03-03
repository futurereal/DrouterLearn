package com.viomi.modulesetting.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.receiver.CommonSetReceiver;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.SerialControl;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * 公共设置的服务
 */
public class CommonSetService extends Service {
    private static final String TAG = "CommonSetService";
    private int mHeartBeatCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        initNetWorkListener();
        registerCommonSetReceiver();
        sendHeartbeatIntervale();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 检测网络状态的变化，比较快，和有首次回调
     */
    private void initNetWorkListener() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCallback networkCallback = new NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.i(TAG, "onAvailable: ");
                ViomiRxBus.getInstance().post(CommonConstant.MSG_WIFI_STATUS_CHANGE, true);
                //网络链接成功上报一次viot 的属性
                SerialControl.getMucPropertiesAndReport();
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.i(TAG, "onLost: ");
                ViomiRxBus.getInstance().post(CommonConstant.MSG_WIFI_STATUS_CHANGE, false);

            }
        };
        connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), networkCallback);
    }

    /**
     * 接受系统的广播 开关串口 以及 铜锁的逻辑
     */
    private void registerCommonSetReceiver() {
        Log.i(TAG, "registerCommonSetReceiver: ");
        CommonSetReceiver commonSetReceiver = new CommonSetReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonSetReceiver.ACTION_CHILD_LOCK);
        filter.addAction(CommonSetReceiver.ACTION_SERIAL_OPEN);
        filter.addAction(CommonSetReceiver.ACTION_SERIAL_CLOSE);
        filter.addAction(CommonSetReceiver.ACTION_WIFI_STATUS_CHANGE);
        registerReceiver(commonSetReceiver, filter);
    }


    /**
     * 每隔10s 发送一次心跳给系统，否则系统会重启，保证桌面持续闪退的情况能够恢复出厂设置
     * 1、系统开机启动心跳检测；launcher启动10s给系统发送一次心跳广播tick。
     * 2、当系统3分钟没收到launcher的心跳tick，则重启系统。记录重启次数。
     * 3、系统当1个小时内，因为未收到launcher心跳tick而重启超过达到10次，则进行系统恢复出厂设置操作。1小时内没有进行过恢复出厂设置，则重新计时。
     */
    private void sendHeartbeatIntervale() {
        Log.i(TAG, "sendHeartbeatIntervale: ");
        Observable.interval(20, 10, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    Log.i(TAG, "sendHeartbeatIntervale: " + mHeartBeatCount);
                    Intent intent = new Intent();
                    intent.setAction("com.viomi.device.action.dameon.heartbeat");
                    intent.putExtra("package", getPackageName());
                    intent.putExtra("beats", mHeartBeatCount);
                    ApplicationUtils.getContext().sendBroadcast(intent);
                    mHeartBeatCount++;
                });
    }


}

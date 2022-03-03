package com.viomi.modulesetting.manager;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.Utils;

/**
 * @Description: 小米和云米IOT服务的管理类
 */
public class DoubleIotManager {
    private static final String TAG = "DoubleIotManager";
    public static final int TIME_BINDSERVICE_DELAY = 10;
    private static DoubleIotManager mInstance;
    public static final boolean IS_BIND_MIOT = true;
    public static final boolean IS_BIND_VIOT = false;

    private DoubleIotManager() {
        Log.i(TAG, "DoubleIotManager: create");
    }

    public static DoubleIotManager getInstance() {
        if (mInstance == null) {
            synchronized (DoubleIotManager.class) {
                if (mInstance == null) {
                    mInstance = new DoubleIotManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 延迟10s 绑定服务
     */
    public void startIotServiceDelay() {
        Log.i(TAG, "startIotServiceDelay: ");
        Context context = Utils.getApp();
       /* Observable.timer(TIME_BINDSERVICE_DELAY, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .flatMap(aLong -> ViomiRoomDatabase.getDatabase().userInfoDao().getUserInfo())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .subscribe(userInfoDb -> {
                    //VIOT帐号初始化（VIOT初始化时若帐号已登录会同时进行MIOT初始化）
                    if (IS_BIND_VIOT) {
                        ViotDeviceManager.getInstance().bindViotServcie(userInfoDb);
                    }
                    // 如果qrCode != null 的时候，说明用户登录过云米，只要登录过云米，云米登录成功之后，就会 自动绑定米家
                          *//*  if (qrCodeBase == null && IS_BIND_MIOT) {
                                MiotDeviceManager.getInstance().bindMiotService(null);
                            }*//*
                }, throwable -> {
                    Log.e(TAG, "initDevice fail:" + throwable.getMessage());
                });*/
    }

}

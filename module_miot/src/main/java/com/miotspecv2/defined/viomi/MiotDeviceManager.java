package com.miotspecv2.defined.viomi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.miotspecv2.defined.device.OvenDevice;
import com.viomi.ovensocommon.componentservice.miot.BindKeyCallBack;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.xiaomi.miot.host.lan.impl.codec.MiotSupportRpcType;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.BindKeyListener;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.listener.MiotConnectedListener;
import com.xiaomi.miot.typedef.listener.OnBindListener;

import java.util.Map;

/**
 * Created by Ljh on 2021/3/12.
 * Description : 米家账号绑定解绑管理类
 */
public class MiotDeviceManager {
    private static final String TAG = "DeviceManagerMiot";
    private static MiotDeviceManager mInstance;
    private final OvenDevice ovenDevice;
    private final StartServiceListener startServiceLister;
    private Context mContext;
    public boolean hasStartService;
    /**
     * 属性上报
     */
    private final MiotServiceDataCallBack mMiotDataReprot;

    private MiotDeviceManager(OvenDevice ovenDevice) {
        this.ovenDevice = ovenDevice;
        startServiceLister = new StartServiceListener();
        mMiotDataReprot = new MiotServiceDataCallBack(ovenDevice);
    }

    public static MiotDeviceManager getInstance(OvenDevice ovenDevice) {
        if (mInstance == null) {
            synchronized (MiotDeviceManager.class) {
                if (mInstance == null) {
                    mInstance = new MiotDeviceManager(ovenDevice);
                }
            }
        }
        return mInstance;
    }

    /**
     * 绑定 Miot 服务,连接Miot后台
     */
    public void startMiotService(Context context) {
        Log.i(TAG, " startMiotService : " + context + " hasBindService: " + hasStartService);
        mContext = context;
        if (hasStartService) {
            Log.i(TAG, "startMiotService:  has bind return ");
            return;
        }
        try {
            // 启动Miot 的服务
            MiotHostManager.getInstance().bind(context, startServiceLister);
        } catch (MiotException miotException) {
            Log.i(TAG, "bindMiotService: MiotException");
        }
    }

    /**
     * 断开小米iot后台连接，解除与Miot服务绑定关系
     */
    public void unbindMiotService() {
        Log.i(TAG, "start unbindMiotService");
        try {
            MiotHostManager.getInstance().stop();
            MiotHostManager.getInstance().unbind(mContext);
        } catch (MiotException e) {
            Log.i(TAG, "unbindMiotService: error return : " + e.getMessage());
        }

    }

    /**
     * 获取米家绑定二维码
     */
    public void getMiotBindKey(BindKeyCallBack bindKeyCallBack) {
        Log.i(TAG, "getMiotBindKey: ");
        try {
            MiotHostManager.getInstance().getBindKey(new BindKeyListener() {
                @Override
                public void onSucceed(String key, int expire) {
                    Log.i(TAG, "getBindKey success：" + key);
                    bindKeyCallBack.keyReuslt(key, "success");
                }

                @Override
                public void onFailed(MiotError error) {
                    Log.i(TAG, "getBindKey failed：" + error.toString());
                    bindKeyCallBack.keyReuslt("", error.getMessage());
                }
            });
        } catch (MiotException e) {
            bindKeyCallBack.keyReuslt("", e.getMessage());
            Log.d(TAG, "getBindKey ：" + e.getMessage());
        }
    }

    /**
     * 重置设备只有再绑定设备
     */
    public void resetAndRebindMiot() {
        Log.i(TAG, "resetAndRebindMiot: ");
        try {
            MiotHostManager.getInstance().reset(new CompletedListener() {
                @Override
                public void onSucceed(String s) {
                    Log.e(TAG, "resetAndRebindMiot   success");
                    unbindMiotService();
                    hasStartService = false;
                    ModuleSettingServiceFactory.getInstance().getViotService().miotLogoutSuccess();
                    startMiotService(mContext);
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "resetAndRebindMiot  onFailed:" + miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            unbindMiotService();
            startMiotService(mContext);
            Log.i(TAG, "resetAndRebindMiot: error: " + e.getMessage());
        }
    }

    public void reportProperties(Map propMap) {
        mMiotDataReprot.reportProperties(propMap);
    }

    public void reportEvent(String eventName) {
        mMiotDataReprot.reportEvent(eventName);
    }

    public void setMiotInfo(String miotInfo) {
        //ovenDevice  内存问题
        Log.i(TAG, "setMiotInfo: miotInfo: " + miotInfo);
        ovenDevice.setMiotInfo(miotInfo);
    }

    /**
     * 启动miot 服务的监听
     */
    class StartServiceListener implements CompletedListener {
        @Override
        public void onSucceed(String result) {
            Log.i(TAG, "StartServiceListener onSucceed: " + result);
            // 增加米家绑定的监听 不需要处理东西，只是看结果
            hasStartService = true;
            addMiotBindLister();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bindMiot();
        }

        @Override
        public void onFailed(MiotError miotError) {
            Log.e(TAG, "StartMiotServiceListener onFailed:" + miotError.getMessage());
        }
    }

    /**
     * 监听设备绑定，连线
     */
    private void addMiotBindLister() {
        Log.i(TAG, "addMiotListener: ");
        try {
            MiotHostManager.getInstance().registerBindListener(new MiotBindListener(), new CompletedListener() {
                @Override
                public void onSucceed(String s) {
                    Log.i(TAG, "onSucceed: bind " + s);
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.i(TAG, "onFailed: bind " + miotError.getMessage());
                }
            });
            MiotHostManager.getInstance().registerMiotConnectedListener(new MiotConnectedListener() {
                @Override
                public void onConnected() {
                    Log.e(TAG, "Miot onConnected");
                }

                @Override
                public void onDisconnected() {
                    Log.e(TAG, "Miot onDisconnected");
                }

                @Override
                public void onTokenException() {
                    Log.e(TAG, "Miot onTokenException");
                }
            });
            // 启动监听 属性变化的逻辑 注册设备支持的属性, 小米spec平台设备调试
            ovenDevice.start(new CompletedListener() {
                @Override
                public void onSucceed(String successMsg) {
                    Log.d(TAG, "bindMiot checkBind  successMsg: " + successMsg);
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.i(TAG, "bindMiot onFailed: registe:" + miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            Log.e(TAG, e.getMessage());
        }
        //绑定成功，插件的写入和上报，不管是否走onBind
        mMiotDataReprot.setAllPropertyHandler();
    }

    /**
     * 设备绑定miot
     */
    private void bindMiot() {
        Log.i(TAG, "bindMiot: ");
        // 配置Miot的 绑定参数 MiotDeviceConfig
        // 设备要烧录正确sn码
        if (TextUtils.isEmpty(ovenDevice.getMiotToken())) {
            Log.i(TAG, "bindMiot: token is null please check sn");
            return;
        }
        try {
            // 开始绑定米家
            Log.i(TAG, "bindMiot: begain Bind miot ");
            // 进程间通信，来把设备绑定米家sdk
            // 通过uid 绑定设备，uid 正确才会有回调 onBind。否则没有
            Log.i(TAG, "bindMiot: uid: " + ovenDevice.getMiotInfo());
            MiotHostManager.getInstance().start(ovenDevice, MiotSupportRpcType.YUNMI);
        } catch (MiotException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    class MiotBindListener implements OnBindListener {
        /**
         * 1 米家扫码绑定  2 通过uid 绑定
         */
        @Override
        public void onBind() {
            //米家成功绑定
            Log.i(TAG, "MiBindListener  onBind");
            ModuleSettingServiceFactory.getInstance().getViotService().miotLoginSuccess();
        }

        /**
         * onUnbind 只有通过米家app 删除设备的时候才有调用
         */
        @Override
        public void onUnBind() {
            //米家插件 删除设备  插件端删除
            Log.e(TAG, "MiBindListener onUnBind");
            ModuleSettingServiceFactory.getInstance().getViotService().miotLogoutSuccess();
        }
    }
}

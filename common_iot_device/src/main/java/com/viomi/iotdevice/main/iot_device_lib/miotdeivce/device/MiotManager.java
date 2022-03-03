package com.viomi.iotdevice.main.iot_device_lib.miotdeivce.device;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.protocol.EventPack;
import com.viomi.iotdevice.main.defined.device.ViomiAircondition;
import com.viomi.iotdevice.main.defined.service.AirconditionService;
import com.viomi.iotdevice.main.iot_device_lib.util.ToolUtil;
import com.xiaomi.miot.host.lan.impl.codec.MiotSupportRpcType;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.DiscoveryType;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.listener.MiotConnectedListener;
import com.xiaomi.miot.typedef.listener.OnBindListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/****
 * miot操作管理器
 */
public class MiotManager {
    private static String TAG = MiotManager.class.getSimpleName();
    private static MiotManager mInstance;// 实例
    private ViomiAircondition mDevice;
    private AirconditionService.ActionHandler mActionHandler;
    private AirconditionService.PropertySetter mPropertySetter;
    private AirconditionService.PropertyGetter mPropertyGetter;
    private String mModel = "viomi.aircondition.x1";//设备model

    private CompletedListener mCompletedListener;
    private Context mContext;

    public static MiotManager getInstance() {
        if (mInstance == null) {
            synchronized (MiotManager.class) {
                if (mInstance == null) {
                    mInstance = new MiotManager();
                }
            }
        }
        return mInstance;
    }

    /***
     * 设置设备model
     * @param model
     */
    public void setModel(String model) {
        mModel = model;
    }

    /**
     * 绑定 Miot 服务
     *
     * @param mid 小米id，已绑定账号传入对应id，否则为null
     */
    public void bindMiotService(Context context, String mid) {
        mContext = context;
        try {
            MiotHostManager.getInstance().bind(context, new CompletedListener() {

                @Override
                public void onSucceed(String s) {
                    Log.d(TAG, "bind service success,s=" + s);
                    addMiotListener();// 添加监听器，监听设备绑定/解绑，连接/断线
                    initConfig(context, mid);

                    try {
                        // 与 iot 后台建立连接
                        MiotHostManager.getInstance().start(mDevice, MiotSupportRpcType.YUNMI);
                    } catch (MiotException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }

                    // 注册设备属性参数，这样后台就可以 get/set 指定的设备属性
                    registerProperty();
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * 初始化设备配置
     *
     * @param mid 小米id
     */
    private void initConfig(Context context, String mid) {

        // 配置
        MiotDeviceConfig config = new MiotDeviceConfig();
        config.addDiscoveryType(DiscoveryType.MIOT);
        config.friendlyName("Viomi");
        config.deviceId("152586174");
        config.macAddress("7C:49:EB:FB:7F:1B");
        config.manufacturer("viomi");
        config.modelName(mModel);
        config.miotToken("w1bx5c5jA4MAZAax");
        config.miotInfo(getMiIotInfo(context, mid));

        mDevice = new ViomiAircondition(config);
    }

    /**
     * 传输给 iot 后台的设备基本信息，可以修改传输的内容，但是不要修改 JSON 数据结构
     */
    private String getMiIotInfo(Context context, String userId) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", "_internal.info");
                jsonObject.put("partner_id", "");

                JSONObject paramObject = new JSONObject();
                paramObject.put("hw_ver", "Android");
                paramObject.put("fw_ver", ToolUtil.getMiVersion());

                JSONObject apObject = new JSONObject();
                apObject.put("ssid", wifiInfo.getSSID());
                apObject.put("bssid", wifiInfo.getBSSID());
                paramObject.put("ap", apObject);

                JSONObject netObject = new JSONObject();
                netObject.put("localIp", ToolUtil.intToIp(dhcpInfo.ipAddress));
                netObject.put("mask", ToolUtil.intToIp(dhcpInfo.netmask));
                netObject.put("gw", ToolUtil.intToIp(dhcpInfo.gateway));
                paramObject.put("netif", netObject);

                if (userId != null) {
                    paramObject.put("uid", userId);
                }
                jsonObject.put("params", paramObject);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 监听设备绑定/解绑，连接/断线
     */
    private void addMiotListener() {
        try {
            MiotHostManager.getInstance().registerBindListener(new OnBindListener() {
                @Override
                public void onBind() {
                    Log.i(TAG, "device onBind");
                    //  LoginPreference.getInstance().saveBindStatus(true);
                    //  RxBus.getInstance().post(BusEvent.MSG_MI_BIND);
                }

                @Override
                public void onUnBind() {
                    Log.e(TAG, "device unbind");
                    // LoginPreference.getInstance().saveBindStatus(false);
                    //RxBus.getInstance().post(BusEvent.MSG_MI_UNBIND);
                }
            }, new CompletedListener() {
                @Override
                public void onSucceed(String s) {
                    Log.i(TAG, "device bind success，s=" + s);
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "device bind fail，msg：" + miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        try {
            MiotHostManager.getInstance().registerMiotConnectedListener(new MiotConnectedListener() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "Miot onConnected");
                }

                @Override
                public void onDisconnected() {
                    Log.e(TAG, "Miot onDisconnected");
                }

                @Override
                public void onTokenException() {

                }
            });
        } catch (MiotException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 注册设备支持的属性
     */
    private void registerProperty() {
        if (mActionHandler == null) {
            mActionHandler = new AirconditionService.ActionHandler() {
                @Override
                public void onSet_wind_level(int wind_level) {
                    Log.e(TAG, "action:set_wind_level,param=" + wind_level);


                    //设置风速
                    Integer[] props = new Integer[]{wind_level};
                    ViomiIotManager.Companion.getInstance().action("set_wind_level", props, new CommonCallback() {
                        @Override
                        public void onReceiveResult(@Nullable Integer resultCode, @Nullable Object result, @Nullable String desc) {
                            if (resultCode == 0) {
                                try {
                                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                    int code = jsonObject.getInt("code");
                                    if (code == 0) {
                                        Log.i(TAG, "set_wind_level success ");
                                    } else {
                                        Log.i(TAG, "set_wind_level fail,msg=" + jsonObject.getString("message"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.i(TAG, "set_wind_level fail,msg=" + desc);
                            }
                        }
                    });
                }

                @Override
                public void onSet_power(int power) {
                    Log.e(TAG, "action:set_power,param=" + power);

                    //开关机
                    Integer[] props = new Integer[]{power};
                    ViomiIotManager.Companion.getInstance().action("set_power", props, new CommonCallback() {
                        @Override
                        public void onReceiveResult(@Nullable Integer resultCode, @Nullable Object result, @Nullable String desc) {
                            if (resultCode == 0) {
                                try {
                                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                    int code = jsonObject.getInt("code");
                                    if (code == 0) {
                                        Log.i(TAG, "set_power success ");
                                    } else {
                                        Log.e(TAG, "set_power fail,msg=" + jsonObject.getString("message"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.e(TAG, "set_power fail,msg=" + desc);
                            }
                        }
                    });
                }
            };
        }

        if (mPropertySetter == null) {
            mPropertySetter = new AirconditionService.PropertySetter() {

                @Override
                public void setPower(int value) {

                }

                @Override
                public void setMode(int value) {

                }

                @Override
                public void setWind_level(int value) {

                }
            };
        }

        if (mPropertyGetter == null) {
            mPropertyGetter = new AirconditionService.PropertyGetter() {
                @Override
                public int getPower() {
                    return DeviceRepository.getInstance().getDeviceProp().power;//取缓存的属性
                }

                @Override
                public int getMode() {
                    return DeviceRepository.getInstance().getDeviceProp().mode;
                }

                @Override
                public int getWind_level() {
                    return DeviceRepository.getInstance().getDeviceProp().wind_level;
                }

            };
        }
        mDevice.AirconditionService().setHandler(mActionHandler, mPropertyGetter, mPropertySetter);

        // 开始注册，这样才能接收服务端发送的 get/set/action 回调
        try {
            mCompletedListener = new CompletedListener() {

                @Override
                public void onSucceed(String s) {
                    Log.d(TAG, "start device success，s=" + s);

                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "start device fail! msg=" + miotError.getMessage());
                }
            };
            mDevice.start(mCompletedListener);
        } catch (MiotException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 重置设备（解除设备与账号的绑定关系）或者切换绑定的小米账号
     *
     * @param mid 小米id
     */
    public void resetDevice(Context context, String mid) {
        try {
            MiotHostManager.getInstance().reset(new CompletedListener() {

                @Override
                public void onSucceed(String s) {
                    Log.d(TAG, "reset device success,s=" + s);
                    if (mContext != null) {
                        unbindMiotService(mContext);
                    }
                    mContext = context;
                    bindMiotService(context, mid);
                }

                @Override
                public void onFailed(MiotError miotError) {
                    Log.e(TAG, "reset failed：" + miotError.getMessage());
                }
            });
        } catch (MiotException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 断开米小米 iot 后台连接，以及解除与 Miot 服务的绑定
     */
    private void unbindMiotService(Context context) {
        try {
            MiotHostManager.getInstance().stop();
            MiotHostManager.getInstance().unbind(context);
            Log.i(TAG, "unbind Miot Service ");
        } catch (MiotException e) {
            Log.e(TAG, "unbind Miot Service failed：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 通知滤网脏堵/解除 事件
     *
     * @param isBlock 1:滤网脏堵需要清理, 0:滤网没有脏堵不需要清理
     */
    public void sendEventFilter(int isBlock) {
        String method = "event.filtertest";
        String params = "[" + isBlock + "]";
        Log.i(TAG, "method=" + method + ",params=" + params);
        if (mDevice != null) {
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 事件上报
     * @param eventPack 事件
     */
    public void eventReport(EventPack eventPack) {
        if (eventPack == null) {
            return;
        }

        if (mDevice != null) {
            String method = "event." + eventPack.getName();
            String params = "[";
            List list = eventPack.getPropList();
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    params += list.get(i);
                } else {
                    params += "," + list.get(i);
                }
            }
            params += "]";
            Log.i(TAG, "eventReport=" + eventPack.getName() + ",params=" + params);
            try {
                mDevice.send(method, params);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }


    /***
     * 属性上报
     * @param prop
     * @param value
     */
    public void propertyReport(String prop, Object value) {
        Log.i(TAG, "propertyReport:" + prop + ",params = " + value);
        if (mDevice != null) {
            try {
                switch (prop) {
                    case "power":
                        mDevice.AirconditionService().power().setValue(value);
                        break;
                    case "mode":
                        mDevice.AirconditionService().mode().setValue(value);
                        break;
                    case "wind_level":

                        mDevice.AirconditionService().wind_level().setValue(value);
                        break;
                }
                mDevice.sendEvents();
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 关闭服务
     */
    public void close() {
        if (mDevice != null && mCompletedListener != null) {
            try {
                MiotHostManager.getInstance().unregister(mDevice, mCompletedListener);
            } catch (MiotException e) {
                e.printStackTrace();
            }
        }
        if (mContext != null) {
            unbindMiotService(mContext);
        }
        mContext = null;
        mDevice = null;
        mInstance = null;
    }
}

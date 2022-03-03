package com.viomi.iotdevice.main.newmiot;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.protocol.EventPack;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotProperty;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody;
import com.viomi.iotdevice.main.iot_device_lib.util.ToolUtil;
import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.viomi.iotdevice.main.newdefined.device.FridgeDevice;
import com.viomi.iotdevice.main.newdefined.service.Deviceinformation;
import com.viomi.iotdevice.main.newdefined.service.Fridge;
import com.viomi.iotdevice.main.newdefined.service.Fridgeanotherchamber;
import com.viomi.iotdevice.main.newdefined.service.Fridgechamber;
import com.viomi.iotdevice.main.newdefined.service.Fridgefactory;
import com.viomi.iotdevice.main.newdefined.service.Fridgepanel;
import com.viomi.iotdevice.main.newmiot.repository.DevRepository;
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

public class Miotv2Manager {

    private static String TAG = Miotv2Manager.class.getSimpleName();
    private static Miotv2Manager mInstance;
    private FridgeDevice mDevice;
    private Deviceinformation.ActionHandler mActionHandler1;
    private Deviceinformation.PropertyGetter mPropertyGetter1;
    private Deviceinformation.PropertySetter mPropertySetter1;
    private Fridge.ActionHandler mActionHandler2;
    private Fridge.PropertyGetter mPropertyGetter2;
    private Fridge.PropertySetter mPropertySetter2;
    private Fridgechamber.ActionHandler mActionHandler3;
    private Fridgechamber.PropertyGetter mPropertyGetter3;
    private Fridgechamber.PropertySetter mPropertySetter3;
    private Fridgeanotherchamber.ActionHandler mActionHandler4;
    private Fridgeanotherchamber.PropertyGetter mPropertyGetter4;
    private Fridgeanotherchamber.PropertySetter mPropertySetter4;
    private Fridgepanel.ActionHandler mActionHandler5;
    private Fridgepanel.PropertyGetter mPropertyGetter5;
    private Fridgepanel.PropertySetter mPropertySetter5;
    private Fridgefactory.ActionHandler mActionHandler6;
    private Fridgefactory.PropertyGetter mPropertyGetter6;
    private Fridgefactory.PropertySetter mPropertySetter6;

    private String mModel = "viomi.fridge.b1";
    private Context mContext;
    private CompletedListener mCompletedListener;

    public static Miotv2Manager getInstance() {
        if (mInstance == null) {
            synchronized (Miotv2Manager.class) {
                if (mInstance == null) {
                    mInstance = new Miotv2Manager();
                }
            }
        }
        return mInstance;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public FridgeDevice getDevice() {
        return mDevice;
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
        config.deviceId("152586328");
        config.macAddress("7C:49:EB:FB:7F:B5");
        config.manufacturer("viomi");
        config.modelName(mModel);
        config.miotToken("Zi1sEX5vpZO4LYBA");
        config.miotInfo(getMiIotInfo(context, mid));

        mDevice = new FridgeDevice(config);
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
        if (mActionHandler1 == null) {
            mActionHandler1 = new Deviceinformation.ActionHandler() {
            };
        }

        if (mPropertySetter1 == null) {
            mPropertySetter1 = new Deviceinformation.PropertySetter() {
            };
        }

        if (mPropertyGetter1 == null) {
            mPropertyGetter1 = new Deviceinformation.PropertyGetter() {
                @Override
                public String getManufacturer() {
                    return mDevice.deviceinformation().manufacturer().getValue();
                }

                @Override
                public String getModel() {
                    return mDevice.deviceinformation().model().getValue();
                }

                @Override
                public String getSerialnumber() {
                    return mDevice.deviceinformation().serialnumber().getValue();
                }

                @Override
                public String getFirmwarerevision() {
                    return mDevice.deviceinformation().firmwarerevision().getValue();
                }
            };
        }

        if (mActionHandler2 == null) {
            mActionHandler2 = new Fridge.ActionHandler() {
            };
        }

        if (mPropertySetter2 == null) {
            mPropertySetter2 = new Fridge.PropertySetter() {
                @Override
                public void setMode(int value) {
                    Log.d(TAG, "device_property mode ，mode=" + value);
                    ViotSetPropRequestBody body = new ViotSetPropRequestBody();
                    ViotProperty p = new ViotProperty();
                    p.setPid(mDevice.fridge().mode().getInstanceID());
                    p.setSid(mDevice.fridge().mode().getServiceInstanceID());
                    p.setValue(value);
                    body.getPropList().add(p);
                    ViomiIotManager.Companion.getInstance().setProperties(body, new CommonCallback() {
                        @Override
                        public void onReceiveResult(@Nullable Integer resultCode, @Nullable Object result, @Nullable String desc) {
                            if (resultCode == 0) {

                            } else {

                            }
                        }
                    });
                    mDevice.fridge().mode().setValue(value);
                    DevRepository.getInstance().getDevProp().mode_2_4 = value;
                    DevRepository.getInstance().saveJsonString();
                }
            };
        }

        if (mPropertyGetter2 == null) {
            mPropertyGetter2 = new Fridge.PropertyGetter() {
                @Override
                public int getFault() {
                    return DevRepository.getInstance().getDevProp().fault_2_1;
                }

                @Override
                public int getMode() {
                    ViotGetPropRequestBody body = new ViotGetPropRequestBody();
                    ViotProperty p = new ViotProperty();
                    p.setPid(mDevice.fridge().mode().getInstanceID());
                    p.setSid(mDevice.fridge().mode().getServiceInstanceID());
                    body.getPropList().add(p);
                    ViomiIotManager.Companion.getInstance().getProperties(body, (resultCode, result, desc) -> {

                    });
                    return DevRepository.getInstance().getDevProp().mode_2_4;
                }

                @Override
                public int getTemperature() {
                    return DevRepository.getInstance().getDevProp().temperature_2_5;
                }
            };
        }

        if (mActionHandler3 == null) {
            mActionHandler3 = new Fridgechamber.ActionHandler() {
            };
        }

        if (mPropertySetter3 == null) {
            mPropertySetter3 = new Fridgechamber.PropertySetter() {
                @Override
                public void setTargettemperature(int value) {
                    Log.d(TAG, "device_property targettemperature ，targettemperature=" + value);
                    mDevice.fridgechamber().targettemperature().setValue(value);
                    DevRepository.getInstance().getDevProp().targettemperature_3_2 = value;
                    DevRepository.getInstance().saveJsonString();
                }

                @Override
                public void setOn(boolean value) {
                    Log.d(TAG, "device_property on ，on=" + value);
                    mDevice.fridgechamber().on().setValue(value);
                    DevRepository.getInstance().getDevProp().on_3_3 = value;
                    DevRepository.getInstance().saveJsonString();
                }
            };
        }

        if (mPropertyGetter3 == null) {
            mPropertyGetter3 = new Fridgechamber.PropertyGetter() {
                @Override
                public int getTemperature() {
                    return DevRepository.getInstance().getDevProp().fridgetemperature_3_1;
                }

                @Override
                public int getTargettemperature() {
                    return DevRepository.getInstance().getDevProp().targettemperature_3_2;
                }

                @Override
                public boolean getOn() {
                    return DevRepository.getInstance().getDevProp().on_3_3;
                }
            };
        }

        if (mActionHandler4 == null) {
            mActionHandler4 = new Fridgeanotherchamber.ActionHandler() {
            };
        }

        if (mPropertySetter4 == null) {
            mPropertySetter4 = new Fridgeanotherchamber.PropertySetter() {
                @Override
                public void setTargettemperature(int value) {
                    Log.d(TAG, "device_property targettemperature ，targettemperature=" + value);
                    mDevice.fridgeanotherchamber().targettemperature().setValue(value);
                    DevRepository.getInstance().getDevProp().targettemperatureanother_4_2 = value;
                    DevRepository.getInstance().saveJsonString();
                }

                @Override
                public void setOn(boolean value) {
                    Log.d(TAG, "device_property on ，on=" + value);
                    mDevice.fridgeanotherchamber().on().setValue(value);
                    DevRepository.getInstance().getDevProp().onanother_4_3 = value;
                    DevRepository.getInstance().saveJsonString();
                }
            };
        }

        if (mPropertyGetter4 == null) {
            mPropertyGetter4 = new Fridgeanotherchamber.PropertyGetter() {
                @Override
                public int getTemperature() {
                    return DevRepository.getInstance().getDevProp().temperatureanother_4_1;
                }

                @Override
                public int getTargettemperature() {
                    return DevRepository.getInstance().getDevProp().targettemperatureanother_4_2;
                }

                @Override
                public boolean getOn() {
                    return DevRepository.getInstance().getDevProp().onanother_4_3;
                }
            };
        }

        if (mActionHandler5 == null) {
            mActionHandler5 = new Fridgepanel.ActionHandler() {
            };
        }

        if (mPropertySetter5 == null) {
            mPropertySetter5 = new Fridgepanel.PropertySetter() {
                @Override
                public void setQuickcooling(boolean value) {
                    Log.d(TAG, "device_property quickcooling ，quickcooling=" + value);
                    mDevice.fridgepanel().quickcooling().setValue(value);
                    DevRepository.getInstance().getDevProp().quickcooling_5_1 = value;
                    DevRepository.getInstance().saveJsonString();
                }

                @Override
                public void setQuickfreeze(boolean value) {
                    Log.d(TAG, "device_property quickfreeze ，quickfreeze=" + value);
                    mDevice.fridgepanel().quickfreeze().setValue(value);
                    DevRepository.getInstance().getDevProp().quickfreeze5_2 = value;
                    DevRepository.getInstance().saveJsonString();
                }
            };
        }

        if (mPropertyGetter5 == null) {
            mPropertyGetter5 = new Fridgepanel.PropertyGetter() {
                @Override
                public boolean getQuickcooling() {
                    return DevRepository.getInstance().getDevProp().quickcooling_5_1;
                }

                @Override
                public boolean getQuickfreeze() {
                    return DevRepository.getInstance().getDevProp().quickfreeze5_2;
                }
            };
        }


        if (mActionHandler6 == null) {
            mActionHandler6 = new Fridgefactory.ActionHandler() {
                @Override
                public void onTestfrost() {

                }
            };
        }

        if (mPropertyGetter6 == null) {
            mPropertyGetter6 = new Fridgefactory.PropertyGetter() {
                @Override
                public int getFrosttemp() {
                    return DevRepository.getInstance().getDevProp().frosttemp_6_1;
                }

                @Override
                public boolean getForcedfrost() {
                    return DevRepository.getInstance().getDevProp().forcedfrost_6_2;
                }

                @Override
                public boolean getForcenonstop() {
                    return DevRepository.getInstance().getDevProp().forcenonstop_6_3;
                }

                @Override
                public boolean getHeatingwire() {
                    return DevRepository.getInstance().getDevProp().heatingwire_6_4;
                }
            };
        }

        if (mPropertySetter6 == null) {
            mPropertySetter6 = new Fridgefactory.PropertySetter() {
                @Override
                public void setForcedfrost(boolean value) {
                    mDevice.fridgefactory().forcedfrost().setValue(value);
                    DevRepository.getInstance().getDevProp().forcedfrost_6_2 = value;
                    DevRepository.getInstance().saveJsonString();
                }

                @Override
                public void setForcenonstop(boolean value) {
                    mDevice.fridgefactory().forcenonstop().setValue(value);
                    DevRepository.getInstance().getDevProp().forcenonstop_6_3 = value;
                    DevRepository.getInstance().saveJsonString();
                }
            };
        }

        mDevice.deviceinformation().setHandler(mActionHandler1, mPropertyGetter1, mPropertySetter1);
        mDevice.fridge().setHandler(mActionHandler2, mPropertyGetter2, mPropertySetter2);
        mDevice.fridgechamber().setHandler(mActionHandler3, mPropertyGetter3, mPropertySetter3);
        mDevice.fridgeanotherchamber().setHandler(mActionHandler4, mPropertyGetter4, mPropertySetter4);
        mDevice.fridgepanel().setHandler(mActionHandler5, mPropertyGetter5, mPropertySetter5);
        mDevice.fridgefactory().setHandler(mActionHandler6, mPropertyGetter6, mPropertySetter6);

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
                    case Miotspecv2Defined.FRIDGE_SERVICE_IID + "." + Miotspecv2Defined.FAULT_PROPERTY_IID:
                        mDevice.fridge().fault().setValue(value);
                        break;
                    case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.FIRMWAREREVISION_PROPERTY_IID:
                        mDevice.deviceinformation().firmwarerevision().setValue(value);
                        break;
                    case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.MANUFACTURER_PROPERTY_IID:
                        mDevice.deviceinformation().manufacturer().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGE_SERVICE_IID + "." + Miotspecv2Defined.MODE_PROPERTY_IID:
                        mDevice.fridge().mode().setValue(value);
                        break;
                    case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.MODEL_PROPERTY_IID:
                        mDevice.deviceinformation().model().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGECHAMBER_SERVICE_IID + "." + Miotspecv2Defined.ON_PROPERTY_IID:
                        mDevice.fridgechamber().on().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGECHAMBER_ANOTHER_SERVICE_IID + "." + Miotspecv2Defined.CHAMBER_ON_PROPERTY_IID:
                        mDevice.fridgeanotherchamber().on().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGECHAMBER_ANOTHER_SERVICE_IID + "." + Miotspecv2Defined.CHAMBER_TARGETTEMPERATURE_PROPERTY_IID:
                        mDevice.fridgeanotherchamber().targettemperature().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGECHAMBER_ANOTHER_SERVICE_IID + "." + Miotspecv2Defined.CHAMBER_TEMPERATURE_PROPERTY_IID:
                        mDevice.fridgeanotherchamber().temperature().setValue(value);
                        break;
                    case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.SERIALNUMBER_PROPERTY_IID:
                        mDevice.deviceinformation().serialnumber().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGECHAMBER_SERVICE_IID + "." + Miotspecv2Defined.TARGETTEMPERATURE_PROPERTY_IID:
                        mDevice.fridgechamber().targettemperature().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGECHAMBER_SERVICE_IID + "." + Miotspecv2Defined.TEMPERATURE_PROPERTY_IID:
                        mDevice.fridgechamber().temperature().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGEPANEL_SERVICE_IID + "." + Miotspecv2Defined.QUICKCOOLING_PROPERTY_IID:
                        mDevice.fridgepanel().quickcooling().setValue(value);
                        break;
                    case Miotspecv2Defined.FRIDGEPANEL_SERVICE_IID + "." + Miotspecv2Defined.QUICKFREEZE_PROPERTY_IID:
                        mDevice.fridgepanel().quickfreeze().setValue(value);
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

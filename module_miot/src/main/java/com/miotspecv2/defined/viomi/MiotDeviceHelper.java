package com.miotspecv2.defined.viomi;

import static com.viomi.ovensocommon.CommonConstant.VIOMI_NAME;
import static com.viomi.ovensocommon.CommonConstant.VIOMI_NAME_FRIEDLY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.miotspecv2.defined.device.OvenDevice;
import com.viomi.common.ApplicationUtils;
import com.viomi.ovensocommon.ModelUtil;
import com.viomi.ovensocommon.componentservice.miot.BindKeyCallBack;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.DiscoveryType;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.CheckBindListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.util.Map;

public class MiotDeviceHelper {
    private static final String TAG = "MiotDeviceHelper";
    private static MiotDeviceHelper mInstance;
    /**
     * miot 管理类
     */
    private MiotDeviceManager miotDeviceManger;
    private OvenDevice mDevice;

    /**
     *
     */
    private MiotDeviceHelper() {

    }

    /**
     * 初始化miot 绑定所需要的参数
     */
    public void initDevice() {
        MiotDeviceConfig miotDeviceConfig = new MiotDeviceConfig();
        miotDeviceConfig.addDiscoveryType(DiscoveryType.MIOT);
        miotDeviceConfig.friendlyName(VIOMI_NAME_FRIEDLY);
        miotDeviceConfig.manufacturer(VIOMI_NAME);
        String modeName = ModelUtil.getModelName();
        // modelName 决定了 米家app 加载的插件的类型
        Log.i(TAG, "initDevice: miotDeviceConfig " + " modeName: " + modeName);
        miotDeviceConfig.modelName(modeName);
        MiIdentification miIdentification = getMiIdentification(ApplicationUtils.getContext());
        miotDeviceConfig.deviceId(miIdentification.getDeviceId());
        miotDeviceConfig.macAddress(miIdentification.getMac());
        miotDeviceConfig.miotToken(miIdentification.getMiToken());
        mDevice = new OvenDevice(miotDeviceConfig);
        String miotInfo = getMiotInfo();
        mDevice.setMiotInfo(miotInfo);
        miotDeviceManger = MiotDeviceManager.getInstance(mDevice);

    }

    public static MiotDeviceHelper getInstance() {
        if (mInstance == null) {
            synchronized (MiotDeviceHelper.class) {
                if (mInstance == null) {
                    mInstance = new MiotDeviceHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取小米标识
     */
    private MiIdentification getMiIdentification(Context context) {
        MiIdentification miIdentification = new MiIdentification();
        String sn = getSystemProperties(context, "gsm.serial");
        if (sn == null) {
            Log.w(TAG, "sn code is null");
            return miIdentification;
        } else {
            Log.d(TAG, "sn = " + sn + ",length = " + sn.length());
        }
        String[] list;
        if (sn.length() >= 24 && (!sn.contains("|"))) {
            list = new String[2];
            list[0] = sn.substring(0, 8);
            list[1] = sn.substring(8, 24);
        } else {
            list = sn.split("\\|");
            if (list.length < 2 || list[1].length() < 16) {
                Log.w("getMiIdentification", "error,sn=" + sn);
                return miIdentification;
            }
        }
        miIdentification.setMac(getMac());
        miIdentification.setDeviceId(list[0]);
        miIdentification.setMiToken(list[1].substring(0, 16));
        Log.d(TAG,
                "miIndentify.mac:" + miIdentification.getMac() + " miIndentify.did:" + miIdentification.getDeviceId() + " miIndentify.token:" + miIdentification.getMiToken());
        return miIdentification;
    }

    /**
     * 获取 mac 地址
     */
    private String getMac() {
        String mac = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return mac;
    }

    /**
     * 根据 Key 获取系统底层属性
     *
     * @return 如果不存在该 key 则返回空字符串
     */
    private String getSystemProperties(Context context, String key) throws IllegalArgumentException {
        String ret;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressLint("PrivateApi") @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            // 参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);
            // 参数
            Object[] params = new Object[1];
            params[0] = key;
            ret = (String) get.invoke(SystemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    /**
     * 传输给ot后台的设备基本信息，可以修改传输的内容，但是不要修改JSON数据结构
     *
     * @return
     */
    private String getMiotInfo() {
        String userId = ModuleSettingServiceFactory.getInstance().getViotService().getMiUserId();
        Log.i(TAG, "getMiotInfo: miotUserId : " + userId);
        WifiManager wifiManager = (WifiManager) ApplicationUtils.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("method", "_internal.info");
                jsonObject.put("partner_id", "");

                JSONObject paramObject = new JSONObject();
                paramObject.put("hw_ver", "Android");
                paramObject.put("fw_ver", getMiVersion());

                JSONObject apObject = new JSONObject();
                apObject.put("ssid", wifiInfo.getSSID());
                apObject.put("bssid", wifiInfo.getBSSID());
                paramObject.put("ap", apObject);

                JSONObject netObject = new JSONObject();
                netObject.put("localIp", intToIp(dhcpInfo.ipAddress));
                netObject.put("mask", intToIp(dhcpInfo.netmask));
                netObject.put("gw", intToIp(dhcpInfo.gateway));
                paramObject.put("netif", netObject);

                if (userId != null) {
                    paramObject.put("uid", userId);
                }
                jsonObject.put("params", paramObject);
                Log.i(TAG, "getMiotInfo: " + jsonObject);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getFirmwareVersion() {
        int versionCode = 0;
        PackageManager packageManager = ApplicationUtils.getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(ApplicationUtils.getContext().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // sdk版本 + app version code
        return MiotHostManager.getInstance().getSdkVersion() + "_" + versionCode;
    }

    /**
     * 检查设备是否已经绑定到了默认米家账号
     * <p>
     * 注意：必须把设备model让后台添加到白名单后，这个请求才会生效
     */
    public void checkBind() {
        try {
            boolean isMiotConnected = MiotHostManager.getInstance().isMiotConnected();
            Log.d(TAG, "checkBind  isMiotConnected = " + isMiotConnected);
            boolean isMiotServiceConnected = MiotHostManager.getInstance().isMiotServiceConnected();
            Log.d(TAG, "checkBind  isMiotServiceConnected = " + isMiotServiceConnected);
            MiotHostManager.getInstance().checkBind(new CheckBindListener() {
                @Override
                public void onSucceed(boolean isBind) {
                    Log.d(TAG, "checkBind onResponse, isBind = " + isBind);
                }

                @Override
                public void onFailed(MiotError error) {
                    Log.e(TAG, "checkBind failed: " + error.toString());
                }
            });
        } catch (MiotException e) {
            e.printStackTrace();
        }
    }

    /**
     * int ip 地址格式化
     */
    public String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 上传米家版本号
     */
    public String getMiVersion() {
        try {
            PackageManager manager = ApplicationUtils.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(ApplicationUtils.getContext().getPackageName(), 0);
            return "3.0.3_0" + info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void reportProperties(Map propMap) {
        if (miotDeviceManger == null) {
            return;
        }
        miotDeviceManger.reportProperties(propMap);
    }

    public void reportEvent(String eventName) {
        miotDeviceManger.reportEvent(eventName);
    }

    public void getMiotBindKey(BindKeyCallBack bindKeyCallBack) {
        miotDeviceManger.getMiotBindKey(bindKeyCallBack);
    }

    public void resetAndRebindMiot() {
        Log.i(TAG, "resetAndRebindMiot: ");
        // 由于userId  是经常变的，所以要重新火气MiotInfo 是否，决定是否能绑定成功
        // -1 绑定不成功， 正确的uerId  可以绑定成功
        miotDeviceManger.setMiotInfo(getMiotInfo());
        miotDeviceManger.resetAndRebindMiot();
    }

    public void startMiotService(Context base) {
        Log.i(TAG, "startMiotService: ");
        miotDeviceManger.startMiotService(base);
    }

    public String getDeviceId() {

        return "123456";
    }
}
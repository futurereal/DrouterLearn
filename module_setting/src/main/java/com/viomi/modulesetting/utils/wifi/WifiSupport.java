package com.viomi.modulesetting.utils.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2014-2021, 佛山云米科技有限公司
 *
 * @ProjectName: ovenSo
 * @Package: com.viomi.modulesetting.utils
 * @ClassName: WifiSupport
 * @Description:
 * @Author: randysu
 * @CreateDate: 4/19/21 9:38 AM
 * @UpdateUser:
 * @UpdateDate: 4/19/21 9:38 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class WifiSupport {
    //    private static final String TAG = "WifiSupport";
    private static final String TAG = "WlanSettingFragment";

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA2, WIFICIPHER_WPA3, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public WifiSupport() {
    }

    public static List<ScanResult> getWifiScanResult(Context context) {
        boolean b = context == null;
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getScanResults();
    }

    public static boolean isWifiEnable(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    public static WifiInfo getConnectedWifiInfo(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
    }

    public static List getConfigurations(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConfiguredNetworks();
    }


    public static WifiConfiguration createWifiConfig(String SSID, String password, WifiCipherType type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WifiCipherType.WIFICIPHER_NOPASS) {
//            config.wepKeys[0] = "";  //注意这里
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
        }
        if (type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (type == WifiCipherType.WIFICIPHER_WPA2 || type == WifiCipherType.WIFICIPHER_WPA3) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;

        }

        return config;

    }

    /**
     * 创建一个WifiConfiguration：在附件的网络中的时候创建
     * @param scanResult
     * @param password
     * @return
     */
    public static WifiConfiguration createWifiConfiguration(ScanResult scanResult,String password){
        WifiConfiguration configuration =new  WifiConfiguration();
        configuration.SSID = Wifi.convertToQuotedString(scanResult.SSID);
        configuration.BSSID = scanResult.BSSID;
        String security = Wifi.ConfigSec.getScanResultSecurity(scanResult);
        Wifi.ConfigSec.setupSecurity(configuration, security, password);
        return configuration;
    }

    /**
     * 接入某个wifi热点
     */
    public static boolean addNetWork(WifiConfiguration config, Context context) {
        Log.i(TAG, "addNetWork: ");
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifimanager.getConnectionInfo();
        Log.i(TAG, "addNetWork: wifiInfo: " + wifiinfo.getBSSID());
        if (null != wifiinfo) {
            wifimanager.disableNetwork(wifiinfo.getNetworkId());
        }
        boolean result = false;
        if (config == null) {
            wifimanager.disconnect();
            return result;
        }
        if (config.networkId >= 0) {
            Log.i(TAG, "addNetWork: disconnect before and connect new");
            wifimanager.disconnect();
            result = wifimanager.enableNetwork(config.networkId, true);
//            wifimanager.updateNetwork(config);
        } else {
            Log.i(TAG, "addNetWork: add new net");
            int addNetworkResult = wifimanager.addNetwork(config);
            Log.i(TAG, "addNetWork: addNetworkResult: " + addNetworkResult);
            result = false;
            if (addNetworkResult > 0) {
                wifimanager.disconnect();
                wifimanager.saveConfiguration();
                return wifimanager.enableNetwork(addNetworkResult, true);
            }else{
                Log.i(TAG, "addNetWork: addNetWork false");
            }
        }
        Log.i(TAG, "addNetWork: result:" + result);
        return result;
    }

    /**
     * 判断wifi热点支持的加密方式
     */
    public static WifiCipherType getWifiCipher(String s) {

        if (s.isEmpty()) {
            return WifiCipherType.WIFICIPHER_INVALID;
        } else if (s.contains("WEP")) {
            return WifiCipherType.WIFICIPHER_WEP;
        } else if (s.contains("WPA") || s.contains("WPA2") || s.contains("WPS")) {
            return WifiCipherType.WIFICIPHER_WPA2;
        } else if (s.contains("WPA3")) {
            return WifiCipherType.WIFICIPHER_WPA3;
        } else {
            return WifiCipherType.WIFICIPHER_NOPASS;
        }
    }

    //查看以前是否也配置过这个网络
    public static WifiConfiguration isExsits(String SSID, Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wifimanager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    // 打开WIFI
    public static void openWifi(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public static void closeWifi(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(false);
        }
    }

    public static boolean isOpenWifi(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean b = wifimanager.isWifiEnabled();
        return b;
    }

    /**
     * 将idAddress转化成string类型的Id字符串
     *
     * @param idString
     * @return
     */
    public static String getStringId(int idString) {
        StringBuffer sb = new StringBuffer();
        int b = (idString >> 0) & 0xff;
        sb.append(b + ".");
        b = (idString >> 8) & 0xff;
        sb.append(b + ".");
        b = (idString >> 16) & 0xff;
        sb.append(b + ".");
        b = (idString >> 24) & 0xff;
        sb.append(b);
        return sb.toString();
    }

    /**
     * 设置安全性
     *
     * @param capabilities
     * @return
     */
    public static String getCapabilitiesString(String capabilities) {
        if (capabilities.contains("WEP")) {
            return "WEP";
        } else if (capabilities.contains("WPA") || capabilities.contains("WPA2") || capabilities.contains("WPS")) {
            return "WPA/WPA2";
        } else {
            return "OPEN";
        }
    }

    public static boolean getIsWifiEnabled(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifimanager.isWifiEnabled();
    }

//    public static void getReplace(Context context, List<ScanResult> list) {
//        WifiInfo wifi = WifiSupport.getConnectedWifiInfo(context);
//        List<ScanResult> listCopy = new ArrayList<>();
//        listCopy.addAll(list);
//        for (int i = 0; i < list.size(); i++) {
//            if (("\"" + list.get(i).getWifiName() + "\"").equals(wifi.getSSID())) {
//                listCopy.add(0, list.get(i));
//                listCopy.remove(i + 1);
//                listCopy.get(0).setState("已连接");
//            }
//        }
//        list.clear();
//        list.addAll(listCopy);
//    }

    /**
     * 去除同名WIFI
     *
     * @param oldSr 需要去除同名的列表
     * @return 返回不包含同命的列表
     */
    public static List<ScanResult> noSameName(List<ScanResult> oldSr) {
        List<ScanResult> newSr = new ArrayList<ScanResult>();
        for (ScanResult result : oldSr) {
            if (!TextUtils.isEmpty(result.SSID) && !containName(newSr, result.SSID))
                newSr.add(result);
        }
        return newSr;
    }

    /**
     * 判断一个扫描结果中，是否包含了某个名称的WIFI
     *
     * @param sr   扫描结果
     * @param name 要查询的名称
     * @return 返回true表示包含了该名称的WIFI，返回false表示不包含
     */
    public static boolean containName(List<ScanResult> sr, String name) {
        for (ScanResult result : sr) {
            if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
                return true;
        }
        return false;
    }

    /**
     * 返回level 等级
     */
    public static int getLevel(int level) {
        if (Math.abs(level) < 50) {
            return 1;
        } else if (Math.abs(level) < 75) {
            return 2;
        } else if (Math.abs(level) < 90) {
            return 3;
        } else {
            return 4;
        }
    }

//    // api 29 需要指定的网络连接回调
//    static ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
//        @Override
//        public void onAvailable(@NonNull Network network) {
//            super.onAvailable(network);
//
//            Log.i(TAG, "ConnectNetwork  api 29  Network  onAvailable");
//        }
//
//        @Override
//        public void onUnavailable() {
//            super.onUnavailable();
//
//            Log.e(TAG, "ConnectNetwork  api 29  Network  onUnavailable");
//        }
//    };

//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    public static void connectToWifiWithApi29(Context context, String wifiSSID, String wifiPassword, WifiCipherType cipherType) {
//        WifiManager wifimanager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//
//        WifiNetworkSuggestion.Builder wifiSuggestionBuilder = new WifiNetworkSuggestion.Builder();
//        wifiSuggestionBuilder.setSsid(wifiSSID);
//        if (cipherType == WifiCipherType.WIFICIPHER_WPA2) {
//            wifiSuggestionBuilder.setWpa2Passphrase(wifiPassword);
//        } else if (cipherType == WifiCipherType.WIFICIPHER_WPA3) {
//            wifiSuggestionBuilder.setWpa3Passphrase(wifiPassword);
//        }
//        wifiSuggestionBuilder.setIsAppInteractionRequired(false);
//
//        WifiNetworkSuggestion wifiNetworkSuggestion = wifiSuggestionBuilder.build();
//
//        List<WifiNetworkSuggestion> suggestionsList = new ArrayList<>();
//        suggestionsList.add(wifiNetworkSuggestion);
//
//        int status = wifimanager.addNetworkSuggestions(suggestionsList);
//        LogUtils.i(TAG, "connectToWifiWithApi29  satus: " + status);
//    }

}

package com.viomi.modulesetting.utils;

import android.content.Context;

import com.viomi.common.ViomiProvideUtil;
import com.viomi.viot.utils.encrypt.AESUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author bailing
 * date 2021/5/13
 * description：
 */
public class ViomiHeaderSign {

    /**
     * 获取请求头
     *
     * @param map     请求参数
     * @param context context
     * @return headers
     */
    public static HashMap<String, String> getHeaders(HashMap<String, String> map, Context context) {
        HashMap<String, String> headers = new HashMap<>(6);
        String uuid = UUID.randomUUID().toString();
        String time = String.valueOf(System.currentTimeMillis());
        String did = ViomiProvideUtil.getDeviceId();
        HashMap<String, String> param = new HashMap<>(7);
        param.put("VIOMI-Access-Key", did);
        param.put("VIOMI-Noise", uuid);
        param.put("VIOMI-Timestamp", time);
        param.putAll(map);
        List<String> soredList = new ArrayList<>();
        soredList.add("VIOMI-Access-Key");
        soredList.add("VIOMI-Noise");
        soredList.add("VIOMI-Timestamp");
        soredList.add("did");
        soredList.add("pid");
        String sign = SignUtil.sign("GET", param, ViomiProvideUtil.getAccessKey(), soredList);
        headers.put("VIOMI-Access-Key", did);
        headers.put("VIOMI-Noise", uuid);
        headers.put("VIOMI-Timestamp", time);
        headers.put("VIOMI-Signature", sign);
        return headers;
    }
    
    public static String decrypt(String content, Context context) {
        return AESUtil.INSTANCE.decrypt(content, ViomiProvideUtil.getAccessKey());
    }
}

package com.viomi.modulesetting.utils;


import android.util.Base64;

import com.viomi.vlog.Vlog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: cent
 * @email: chenzhao@viomi.com.cn
 * @date: 2018/12/26.
 * @description:
 */
public class SignUtil {
    private static final String TAG = SignUtil.class.getSimpleName();

    /**
     * 编码格式
     */
    private static final String ENCODING = "UTF-8";
    /**
     * 参数分隔符
     */
    private static final String SEPARATOR = "&";
    /**
     * 签名算法
     */
    private static final String ALGORITHM_SHA256 = "HmacSHA256";


    /**
     * 生成数据签名
     *
     * @param method：请求方式
     * @param params：参数
     * @param accessKeySecret：密钥
     */
    public static String sign(String method, Map<String, String> params, String accessKeySecret) {
        // 生成待签名串
        String signString = generateSignString(method, params);
        Vlog.d(TAG, signString);
        // 签名
        return digitalSignature(signString, accessKeySecret);
    }

    /**
     * 按照固定顺序来签名
     */
    public static String sign(String method, Map<String, String> params, String accessKeySecret, List<String> keyList) {
        // 生成待签名串
        String signString = generateSignString(method, params, keyList);
        Vlog.d(TAG, signString);
        // 签名
        return digitalSignature(signString, accessKeySecret);
    }


    /**
     * 生成待签名字符串，待签名字符串由请求方法和请求参数（Header参数+Querystring参数）按既定格式拼接而成。格式如下：
     * HttpMethod+UrlEncode("?")+UrlEncode(parameterName1)+"="+UrlEncode(parameterValue1)+"&"+UrlEncode(parameterName2)+"="+UrlEncode(parameterValue2)+...
     * <p>
     * 注意：请求参数需按字典顺序排序
     *
     * @param method：请求方式
     * @param params：参数
     */
    private static String generateSignString(String method, Map<String, String> params) {
        // 参数排序
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys, Collator.getInstance(Locale.CHINA));
//        List<String> sortedKeys = params.keySet().stream().sorted().collect(Collectors.toList());
        // 拼接请求方法
        StringBuilder signStringBuilder = new StringBuilder();
        try {
            signStringBuilder.append(method.toUpperCase())
                    .append(encode("?"));
        } catch (UnsupportedEncodingException e) {
            Vlog.e(TAG, "拼接签名串异常：" + e);
        }

        // 拼接请求参数
        StringBuilder paramStringBuilder = new StringBuilder();
        for (String key : sortedKeys) {
            try {
                paramStringBuilder.append(SEPARATOR)
                        .append(encode(key))
                        .append("=")
                        .append(encode(params.get(key)));
            } catch (UnsupportedEncodingException e) {
                Vlog.e(TAG, "拼接签名串异常，key 为" + key + "，message + " + e);
            }
        }
        signStringBuilder.append(paramStringBuilder.substring(1));
        return signStringBuilder.toString();
    }

    /**
     * 不进行自动排序，按固定顺序
     */
    public static String generateSignString(String method, Map<String, String> params, List<String> keyList) {
        // 拼接请求方法
        StringBuilder signStringBuilder = new StringBuilder();
        try {
            signStringBuilder.append(method.toUpperCase())
                    .append(encode("?"));
        } catch (UnsupportedEncodingException e) {
        }

        // 拼接请求参数
        StringBuilder paramStringBuilder = new StringBuilder();
        for (String key : keyList) {
            try {
                paramStringBuilder.append(SEPARATOR)
                        .append(encode(key))
                        .append("=")
                        .append(encode(params.get(key)));
            } catch (UnsupportedEncodingException e) {
//                Vlog.e(TAG, "拼接签名串异常，key 为" + key + "，message + " + e);
            }
        }
        signStringBuilder.append(paramStringBuilder.substring(1));
        return signStringBuilder.toString();
    }

    /**
     * 对待签名字符串进行数据签名
     *
     * @param data：待签名字符串
     * @param secret：密钥
     */
    private static String digitalSignature(String data, String secret) {
        byte[] sign = null;
        try {
            Mac mac = Mac.getInstance(ALGORITHM_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(ENCODING), ALGORITHM_SHA256));
            sign = mac.doFinal(data.getBytes(ENCODING));
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            Vlog.e(TAG, e.getMessage());
        }
        return Base64.encodeToString(sign, Base64.NO_WRAP);
    }

    /**
     * 根据 RFC3986 规范进行 UrlEncode
     */
    private static String encode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, ENCODING)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~") : null;
    }

}

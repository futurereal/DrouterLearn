package com.viomi.camera.http;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lixinqi
 * 签名工具
 * */
public class SignUtil {

    /**
     * 密钥
     */
    public static final String APP_SECRET = "scRTAGk3RBVJc9mp";
    public static final String METHOD = "POST";

    /**
     * 编码格式
     */
    private static final String ENCODING = "UTF-8";
    /**
     * 参数分隔符
     */
    private static final String SEPARATOR = "&";
    private static final String EQUALS_SIGN = "=";
    /**
     * 签名算法
     */
    private static final String ALGORITHM_SHA256 = "HmacSHA256";

    // 按固定顺序排序签名
    public static String sign(String method, Map<String, String> param, String secret, List<String> orderList) {
        String signStr = generateSignString(method, param, orderList);
        Log.d("SignUtil", "signStr: " + signStr);
        return digitalSignature(signStr, secret);
    }

    // 生成待签名字符串
    public static String generateSignString(String method, Map<String, String> param, List<String> orderList) {
        StringBuilder methodBuilder = new StringBuilder();
        try {
            methodBuilder.append(method.toUpperCase()).append(encode("?"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder paramBuilder = new StringBuilder();
        for (String key : orderList) {
            try {
                paramBuilder.append(SEPARATOR)
                        .append(encode(key))
                        .append(EQUALS_SIGN)
                        .append(encode(param.get(key)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        methodBuilder.append(paramBuilder.substring(1));
        return methodBuilder.toString();
    }

    /**
     * 使用UTF-8字符集对参数名和参数值按RFC3986规则进行编码，Java可使用java.net.URLEncoder，
     * 还需将编码后的字符串中加号（+）替换为 %20、星号（*）替换为 %2A、%7E 替换为波浪号（~）。
     */
    private static String encode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, "UTF-8")
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~") : null;
    }

    // 进行数字签名,签名算法为：HmacSHA256
    public static String digitalSignature(String data, String secret) {
        byte[] sign = null;
        try {
            Mac mac = Mac.getInstance(ALGORITHM_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(ENCODING), ALGORITHM_SHA256));
            sign = mac.doFinal(data.getBytes(ENCODING));
        } catch (Exception e) {
            Log.e("SignUtil", "digitalSignature: exception: " + e.getMessage());
        }
        return Base64.encodeToString(sign, Base64.NO_WRAP);
    }
}

package com.viomi.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by Ljh on 2021/2/3.
 * Description：读取Viomi的设备did、key、softAp信息
 */
public class ViomiProvideUtil {
    private static final String TAG = "ProvidedUtil";
    public static JSONObject data = null;

    public static final String DEFAULT_DEVICE_ID = "1000034774";//
    public static final String DEFAULT_ACCESS_KEY = "ruyz21OyQcylUtd3";
    public static final String DEFAULT_CLOUD_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHI0SOYQzFLa/EAs1VD6fAhlSqXt4tr2mJy7/I1yKGinSy6ml36SfiDT4u+BXIEagLGf3uqJqqoVoKnrKS3wnf9Hzxj4oGcPG92RtEkQ4PnLwaTKXUrp5UOk9Mc39TZ5koZI8OvUqUUWYblzg/2aNQPtWi6aghUMbI99qcQLhzXQIDAQAB";
    //测试环境切换  测试环境三元组,测试后台的时候需要用
    public static final boolean IS_DEBUG_ENV = false;
    public static final boolean IS_DEBUG_CAMERA_ENV = true;
    public static final String DEVICE_ID_TEST = "1111308694";
    public static final String ACCESS_KEY_TEST = "fvGzcTttZvGndOQy";
    // 录像测试环境的token
    public static final String ACCESS_KEY_TEST_CAMERA = "hAZC26flIa1VhlY5";
    public static final String CLOUD_PUBLIC_KEY_TEST = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCI8vjX24vj0tZ07LGsC87a2XmcnfNLsKelXovirr7TOlm/C4G6VdcfnjP4rHb0ABuY5rbH116CdXIVIdtkd1qi4+vMSaJMcR3mTfyWAeRsHff5PkyVBhjnEhVqK2IBKvQWcOuWVzekz4U5R2Gut8v8s98wXTBMScmqqZuw8tOdjwIDAQAB";

    public static final int CLOUD_PUBLIC_KEY_LENGTH = 216;
    public static final String CLOUD_PUBLIC_KEY_START = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC";

    public static String getDeviceId() {
        String did = "";
        if (data == null) {
            data = getDidKeyObject(ApplicationUtils.getContext());
            if (data != null) {
                did = data.optString("did");
            }
        } else {
            did = data.optString("did");
        }
        if (TextUtils.isEmpty(did)) {
            did = DEFAULT_DEVICE_ID;
        }
        if (IS_DEBUG_ENV) {
            did = DEVICE_ID_TEST;
            Log.d(TAG, "getDeviceId: debug " + did);
        }
        return did;
    }

    public static String getAccessKey() {
        String accessKey = "";
        if (data == null) {
            data = getDidKeyObject(ApplicationUtils.getContext());
            if (data != null) {
                accessKey = data.optString("deviceAccessKey");
            }
        } else {
            accessKey = data.optString("deviceAccessKey");
        }

        if (TextUtils.isEmpty(accessKey)) {
            accessKey = DEFAULT_ACCESS_KEY;
        }
        if (IS_DEBUG_ENV) {
            accessKey = ACCESS_KEY_TEST;
        }
        Log.d(TAG, "getAccessKey: accessKey " + accessKey);
        return accessKey;
    }

    public static String getCameraAccessKey() {
        String accessKey = "";
        if (IS_DEBUG_CAMERA_ENV) {
            accessKey = ACCESS_KEY_TEST_CAMERA;
        } else {
            accessKey = getAccessKey();
        }
        Log.i(TAG, "getCameraTestAccessKey: accessKey");
        return accessKey;
    }

    public static String getCloudPublicKey() {
        String key = "";
        if (data == null) {
            data = getDidKeyObject(ApplicationUtils.getContext());
            if (data != null) {
                key = data.optString("cloudPublicKey");
            }
        } else {
            key = data.optString("cloudPublicKey");
        }

        if (TextUtils.isEmpty(key)) {
            key = DEFAULT_CLOUD_PUBLIC_KEY;
        }
        if (IS_DEBUG_ENV) {
            key = CLOUD_PUBLIC_KEY_TEST;
            Log.d(TAG, "getCloudPublicKey:debug  " + key);
        }
        return key;
    }

    public static String getMac() {
        String key = "";
        if (data == null) {
            data = getDidKeyObject(ApplicationUtils.getContext());
            if (data != null) {
                key = data.optString("mac");
            }
        } else {
            key = data.optString("mac");
        }
        Log.d(TAG, "getMac:" + key);
        return key;
    }

    public static JSONObject getDidKeyObject(Context context) {
        String moudleInfo = getViomiModuleInfo(context);
        if (TextUtils.isEmpty(moudleInfo)) {
            Log.i(TAG, "getDidKeyObject: modeInfo is null  ");
            return null;
        }
        JSONObject data = null;
        try {
            data = new JSONObject(moudleInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @SuppressLint("Range")
    public static String getViomiModuleInfo(Context context) {
        final String VIOMI_PATCH = "content://com.viomi.device.provider/viomiModuleInfo";
        String data2 = "";
        Cursor cursor = context.getContentResolver().query(Uri.parse(VIOMI_PATCH), null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    data2 = cursor.getString(cursor.getColumnIndex("data"));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        Log.d(TAG, "getViomiModuleInfo: " + data2);
        return data2;
    }

    /**
     * 判断 sn 码是否对的
     */
    public static boolean isSerialNoRight() {
        String serialNo = getSystemProperties(ApplicationUtils.getContext(), "gsm.serial");
        if (TextUtils.isEmpty(serialNo)) {
            Log.w(TAG, "isThreeGroupRight sn is null return  false ");
            return false;
        }
        Log.d(TAG, "sn = " + serialNo + ",length = " + serialNo.length());
        String[] threeList = serialNo.split("\\|");
        if (threeList.length < 2 || threeList[1].length() < 16) {
            Log.w(TAG, "isThreeGroupRight sn error return false");
            return false;
        }
        if (serialNo.length() >= 24 && (serialNo.contains("|"))) {
            Log.i(TAG, "isThreeGroupRight: return true");
            return true;
        }
        Log.i(TAG, "isThreeGroupRight: return false  ");
        return false;
    }

    /**
     * 判断秘钥是否符合逻辑
     *
     * @return
     */
    public static boolean isPublicKeyRight() {
        String publicKey = getCloudPublicKey();
        if (TextUtils.isEmpty(publicKey) || publicKey.length() != CLOUD_PUBLIC_KEY_LENGTH || !publicKey.startsWith(CLOUD_PUBLIC_KEY_START)) {
            Log.i(TAG, "isPublicKeyRight: publickey is not availeable");
            return false;
        }
        return true;
    }


    /**
     * 根据 Key 获取系统底层属性
     *
     * @return 如果不存在该 key 则返回空字符串
     */
    private static String getSystemProperties(Context context, String key) throws IllegalArgumentException {
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

    public static String getMiotDeviceId() {
        String serialNo = getSystemProperties(ApplicationUtils.getContext(), "gsm.serial");
        String miotDeviceId = "";
        if (serialNo == null) {
            Log.w(TAG, "sn code is null return");
            return miotDeviceId;
        }
        if (serialNo.length() >= 24 && (!serialNo.contains("|"))) {
            miotDeviceId = serialNo.substring(0, 8);
        } else {
            String[] list = serialNo.split("\\|");
            if (list.length < 2 || list[1].length() < 16) {
                Log.w("getMiIdentification", "error,sn=" + serialNo);
                miotDeviceId = list[0];
            }
        }
        Log.i(TAG, "getMiotDeviceId: miotDeviceId: " + miotDeviceId);
        return miotDeviceId;
    }
}

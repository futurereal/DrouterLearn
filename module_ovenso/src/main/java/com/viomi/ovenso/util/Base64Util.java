package com.viomi.ovenso.util;

import android.util.Base64;
import android.util.Log;

/**
 * Created by Ljh on 2020/12/9.
 * Description:
 */
public class Base64Util {
    private static final String TAG = "Base64Util";
    public static String encode(String text){
        Log.d(TAG,"encode:"+text);
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
    }

    public static String decode(String text){
        Log.d(TAG,"decode:"+text);
        return new String(Base64.decode(text.getBytes(), Base64.DEFAULT));
    }
}


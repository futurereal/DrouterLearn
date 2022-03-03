package com.viomi.common;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ApplicationUtils {
    private static Application mInstance;
    private static final String TAG = "ApplicationUtils";

    public static Context getContext() {
        if (mInstance != null) {
            return mInstance;
        }
        Application context = null;
        try {
            context = (Application) Class.forName("android.app.AppGlobals")
                    .getMethod("getInitialApplication").invoke(null);
        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                context = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            mInstance = context;
        }
        Log.i(TAG, "getContext: " + context);
        return context;
    }

}

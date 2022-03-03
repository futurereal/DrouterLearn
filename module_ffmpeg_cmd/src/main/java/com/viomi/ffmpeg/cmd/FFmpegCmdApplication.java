package com.viomi.ffmpeg.cmd;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.viomi.common.VLogUtil;

/**
 * @description:
 * @data:2021/9/15
 */
public class FFmpegCmdApplication extends Application {
    private static final String TAG = "FFmpegCmdApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        VLogUtil.init(this);
    }
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG, "attachBaseContext: ");
    }
}

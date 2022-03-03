package com.viomi.ffmpeg.codec;

import android.app.Application;
import android.content.Context;

import com.viomi.common.VLogUtil;
import com.viomi.router.core.ViomiRouter;

/**
 * @description:
 * @data:2021/9/15
 */
public class FFmpegCodecApplicaiton extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VLogUtil.init(this);
        ViomiRouter.init(this, true);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}

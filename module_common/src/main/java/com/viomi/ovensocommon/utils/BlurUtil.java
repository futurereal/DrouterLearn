package com.viomi.ovensocommon.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: ViomiHomePad
 * @Package: com.viomi.homepad_settingpage.utils
 * @ClassName: BlurUtils
 * @Description:
 * @Author: randysu
 * @CreateDate: 12/31/20 1:37 PM
 * @UpdateUser:
 * @UpdateDate: 12/31/20 1:37 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class BlurUtil {

    public static Bitmap getActivityBackground(Activity activity) {
        if (activity == null)
            return null;
        View decorView = activity.getWindow().getDecorView();
        try {
            decorView.buildDrawingCache();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        // 获取屏幕宽和高
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();

        // 允许当前窗口保存缓存信息
        decorView.setDrawingCacheEnabled(false);
        decorView.setDrawingCacheEnabled(true);

        Bitmap bmp = null;
        if (decorView.getDrawingCache() != null) {
            bmp = Bitmap.createBitmap(decorView.getDrawingCache(), 0, 0, screenWidth, screenHeight);
        } else {
//            bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.wallpaper_blue);
        }

        decorView.getContentDescription();

        return bmp;
    }

    public static Bitmap blurBitmap(Context context, Bitmap sourceBitmap) {
        if (sourceBitmap == null)
            return null;
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_4444);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context);

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, sourceBitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur: 0 < radius <= 25
        blurScript.setRadius(25.0f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        sourceBitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }

}

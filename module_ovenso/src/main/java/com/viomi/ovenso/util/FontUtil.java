package com.viomi.ovenso.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

/**
 * Created by Ljh on 2020/9/1.
 * Description:
 */
public class FontUtil {
    public static void setFont(Context context, TextView view, int resFont){
        Typeface type = ResourcesCompat.getFont(context, resFont);
        view.setTypeface(type);
    }
}


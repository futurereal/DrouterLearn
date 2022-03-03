package com.viomi.ovenso.util;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;

import androidx.recyclerview.widget.DividerItemDecoration;

/**
 * Created by Ljh on 2020/11/12.
 * Description:
 */
public class ItemDecorationUtil {
    public static DividerItemDecoration linearHorDecor(Context mContext, int widthPx){
        DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setAlpha(0);
        shapeDrawable.setIntrinsicWidth(widthPx);
        itemDecoration.setDrawable(shapeDrawable);
        return itemDecoration;
    }

}


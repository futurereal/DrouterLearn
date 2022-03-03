package com.viomi.ovensocommon;

import android.graphics.drawable.ShapeDrawable;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.viomi.common.ApplicationUtils;

/**
 * Created by Ljh on 2020/11/12.
 * Description:
 */
public class ItemDecorationUtil {
    public static DividerItemDecoration getItemDecoration(int widthPx) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(ApplicationUtils.getContext(), DividerItemDecoration.HORIZONTAL);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setAlpha(0);
        shapeDrawable.setIntrinsicWidth(widthPx);
        itemDecoration.setDrawable(shapeDrawable);
        return itemDecoration;
    }

}


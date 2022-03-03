package com.viomi.ovenso.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Created by Ljh on 2020/11/18.
 * Description:
 */
public class LinearLayoutSel extends LinearLayout {
    public LinearLayoutSel(Context context) {
        super(context);
    }

    public LinearLayoutSel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutSel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutSel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        for (int i = 0; i < this.getChildCount(); i++) {
            this.getChildAt(i).setSelected(selected);
        }
    }
}


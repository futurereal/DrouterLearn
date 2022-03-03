package com.viomi.waterpurifier.edison.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.viomi.waterpurifier.edison.R;

public class DotsLayout extends LinearLayout {
    private static final String TAG = "DotsLayout";
    int selectorDotDrawable = R.drawable.selector_dot_theme1;
    private int dotsCount;
    private int selectIndex;

    public DotsLayout(Context context) {
        this(context, null);
    }

    public DotsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLyaout(context, attrs);
    }

    void initLyaout(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.dotslayout);
        dotsCount = typeArray.getInt(R.styleable.dotslayout_dot_count, 0);
        selectIndex = typeArray.getInt(R.styleable.dotslayout_dot_index, 0);
        Log.i(TAG, "initLyaout: " + dotsCount + "  selectIndex: " + selectIndex);
        refresh();
        typeArray.recycle();
    }

    public void setSelectorDrawable(int selectedDrawable) {
        Log.i(TAG, "setSelectorDrawable: ");
        this.selectorDotDrawable = selectedDrawable;
        refresh();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i(TAG, "onSizeChanged: ");
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.i(TAG, "onWindowFocusChanged: ");
        if (hasWindowFocus) {
//            refresh(selectIndex, dotCount);
        }
    }

    public void setSelected(int index) {
        Log.i(TAG, "setSelected: ");
        selectIndex = index;
        int totalCount = getChildCount();
        for (int i = 0; i < totalCount; i++) {
            boolean isSelect = i == index;
            getChildAt(i).setSelected(isSelect);
        }
    }

    private void refresh() {
        Log.i(TAG, "refresh: ");
        removeAllViews();
        int margin = -1;
        if (dotsCount > 2) {
            margin = (getWidth() - 8 * dotsCount) / (dotsCount - 1);
        } else {
            margin = 13;
        }
        margin = 13;
        Log.i(TAG, "refresh: margin: " + margin);
        for (int i = 0; i < dotsCount; i++) {
            View view = new View(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(8
                    , 8);
            if (i != 0) {
                layoutParams.leftMargin = margin;
            }
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(selectorDotDrawable);
            view.setSelected(i == selectIndex);
            addView(view);
        }
    }

}

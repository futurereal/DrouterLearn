package com.viomi.ovensocommon.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ClickUtils;
import com.viomi.common.R;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 * <p>
 * 标题栏
 */
public class TitleBar extends ConstraintLayout {
    ImageView back;
    TextView title;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.setting_title, this);
        back = findViewById(R.id.iv_back);
        back.setOnClickListener(new ClickUtils.OnDebouncingClickListener() {
            @Override
            public void onDebouncingClick(View v) {
                Context ctx = getContext();
                if (ctx instanceof Activity) {
                    ((Activity) ctx).finish();
                }
            }
        });
        title = findViewById(R.id.filterselect_title);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        CharSequence text = a.getText(R.styleable.TitleBar_set_title_text);
        int textSize = a.getDimensionPixelSize(R.styleable.TitleBar_set_title_text_size, 44);
        int textColor = a.getColor(R.styleable.TitleBar_set_title_text_color, 0xFFFFFFFF);
        if (!TextUtils.isEmpty(text)) {
            title.setText(text);
        }
        title.setTextSize(textSize);
        title.setTextColor(textColor);
        a.recycle();
    }

    public void setOnbackClickListener(OnClickListener onbackClickListener) {
        back.setOnClickListener(onbackClickListener);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setTitle(int titleId) {
        this.title.setText(getContext().getString(titleId));
    }
}

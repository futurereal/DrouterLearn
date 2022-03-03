package com.viomi.ovenso.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ItemHomeMenuBinding;

/**
 * Created by Ljh on 2020/9/9.
 * Description:首页卡片容器
 */
public class CardHomeMenuView extends ConstraintLayout {
    private static final String TAG = "CardHomeMenuView";
    private final ItemHomeMenuBinding homeMenuBinding;

    public CardHomeMenuView(Context context) {
        this(context, null);
    }

    public CardHomeMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardHomeMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        homeMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_home_menu, this, true);
        //初始化相关自定义属性
        updateView(context, attrs);
    }

    private void updateView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardHomeMenuView);
        final String name = typedArray.getString(R.styleable.CardHomeMenuView_menuName);
        Log.i(TAG, "updateView: name: " + name);
        final int resourceId = typedArray.getResourceId(R.styleable.CardHomeMenuView_imgSrc, NO_ID);
        typedArray.recycle();
        homeMenuBinding.menuTitle.setText(name);
        homeMenuBinding.getRoot().setBackgroundResource(resourceId);
    }


}


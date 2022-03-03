package com.viomi.waterpurifier.edison.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.config.TemperatureModeEnum;
import com.viomi.waterpurifier.edison.config.WaterQualityEnum;
import com.viomi.waterpurifier.edison.config.WaterThemeEnum;
import com.viomi.waterpurifier.edison.widget.DotsLayout;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @author admin
 * @Description:
 */
public class WaterBindingAdapter {
    private final static String TAG = "WaterBindingAdapter";
    private final static int[] themeBgIds = new int[]{R.drawable.bg_theme1, R.drawable.bg_theme2, R.drawable.bg_theme3};
    private final static int[] wifiStatusIds = new int[]{R.drawable.selector_wifestatus_theme1, R.drawable.selector_wifestatus_theme2, R.drawable.selector_wifestatus_theme3};
    private final static int[] childLockIds = new int[]{R.drawable.selector_childlock_theme1, R.drawable.selector_childlock_theme2, R.drawable.selector_childlock_theme3};
    private final static int[] mainSetIds = new int[]{R.drawable.watermain_theme1_set, R.drawable.watermain_theme2_set, R.drawable.watermain_theme3_set};
    private final static int[] temperatureIncreaseIds = new int[]{R.drawable.selector_add_theme1, R.drawable.selector_add_theme2, R.drawable.selector_add_theme3};
    private final static int[] temperatureDecreaseIds = new int[]{R.drawable.selector_minu_theme1, R.drawable.selector_minu_theme2, R.drawable.selector_minu_theme3};
    private final static int[] temperatureLayoutIds = new int[]{R.drawable.watermain_theme1_temperaturebg, R.drawable.watermain_theme2_temperaturebg, R.drawable.watermain_theme3_temperaturebg};
    private final static int[] dotSelectorIds = new int[]{R.drawable.selector_dot_theme1, R.drawable.selector_dot_theme2, R.drawable.selector_dot_theme3};
    private final static int[] buttonSelectorIds = new int[]{R.drawable.selector_watermain_theme1_button, R.drawable.selector_watermain_theme2_button, R.drawable.selector_watermain_theme2_button};
    private final static int[] waterOutTextColors = new int[]{Color.WHITE, Color.BLACK, R.color.text_color_blue};
    private final static int[] timeTextColors = new int[]{Color.WHITE, Color.WHITE, R.color.text_color_blue};

    /**
     * 为了在xml  自定义 图片里面实现 图片加载，否则加载不出来
     *
     * @param view
     * @param resourceId
     */
    @BindingAdapter(value = "android:background")
    public static void setBackground(View view, int resourceId) {
        Log.i(TAG, "setBackground: resourceId: " + resourceId + " view : " + view.getClass().getCanonicalName());
        view.setBackgroundResource(resourceId);
    }

    /**
     * drawable 加载不出来的问题
     *
     * @param view
     * @param resourceId
     */
    @BindingAdapter(value = "android:drawableLeft")
    public static void setDrawableLeft(TextView view, int resourceId) {
        Log.i(TAG, "setDrawableLeft: resourceId: " + resourceId + " view : " + view.getClass().getCanonicalName());
        Drawable leftDrawable = view.getContext().getDrawable(resourceId);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth() - 4, leftDrawable.getMinimumHeight());
        Log.i(TAG, "setDrawableLeft: leftDrawable: " + leftDrawable);
        view.setCompoundDrawables(leftDrawable, null, null, null);
    }

    @BindingAdapter(value = {"tempEnum"})
    public static void setTempEnum(View view, TemperatureModeEnum tempEnum) {
        Log.i(TAG, "setTempEnum: ");
        if (tempEnum == null) {
            return;
        }
        int viewId = view.getId();
        if (viewId == R.id.temperature_name) {
            ((TextView) view).setText(tempEnum.nameTextId);
        }
        if (viewId == R.id.temperature_dots) {
            if (tempEnum == TemperatureModeEnum.CUSTOM) {
                ((DotsLayout) view).setSelected(-1);
            } else {
                ((DotsLayout) view).setSelected(tempEnum.ordinal());
            }
        }
    }

    @BindingAdapter(value = {"waterThemeEnum"})
    public static void setThemeEnum(View view, WaterThemeEnum waterThemeEnum) {
        Log.i(TAG, "setThemeEnum: " + waterThemeEnum + " view : " + view.getClass().getCanonicalName());
        if (waterThemeEnum == null) {
            return;
        }
        setWaterTheme(view, waterThemeEnum.ordinal());
    }

    @BindingAdapter(value = "waterTypeEnum")
    public static void setWaterTypeEnum(View view, WaterQualityEnum waterQualityEnum) {
        Log.i(TAG, "setWaterTypeEnum: " + waterQualityEnum.nameStrId);
        int id = view.getId();
        if (id == R.id.watermain_quality_name || id == R.id.watermain_current_waterquality) {
            ((TextView) view).setText(waterQualityEnum.nameStrId);
        } else if (id == R.id.watermain_quality_icon) {
            ((ImageView) view).setImageResource(waterQualityEnum.iconDrawableId);
        } else if (id == R.id.watermain_quality_group) {
            ((DotsLayout) view).setSelected(waterQualityEnum.ordinal());
        }
    }

    /**
     * 设置主题
     *
     * @param view
     * @param ordinal
     */
    private static void setWaterTheme(View view, int ordinal) {
        int viewId = view.getId();
        Log.i(TAG, "setWaterTheme: " + ordinal);
        // 背景图
        if (viewId == R.id.watermain_layout) {
            view.setBackgroundResource(themeBgIds[ordinal]);
            return;
        }
        // WIFi
        if (viewId == R.id.watermain_wifistatus) {
            ((ImageButton) view).setImageResource(wifiStatusIds[ordinal]);
            return;
        }
        // 童锁
        if (viewId == R.id.watermain_childlock) {
            ((ImageButton) view).setImageResource(childLockIds[ordinal]);
            return;
        }

        if (viewId == R.id.watermain_setting) {
            ((ImageButton) view).setImageResource(mainSetIds[ordinal]);
            return;
        }

        if (viewId == R.id.temperature_add) {
            ((ImageView) view).setImageResource(temperatureIncreaseIds[ordinal]);
        }
        if (viewId == R.id.temperature_minus) {
            ((ImageView) view).setImageResource(temperatureDecreaseIds[ordinal]);
            return;
        }

        if (viewId == R.id.temperature_control_layout | viewId == R.id.watermain_quality_group || viewId == R.id.watermain_flow_layout) {
            Log.i(TAG, "setWaterTheme: setBackground");
            view.setBackgroundResource(temperatureLayoutIds[ordinal]);
            return;
        }

        if (viewId == R.id.watermain_quality_dots || viewId == R.id.temperature_dots || viewId == R.id.watermain_flow_dots) {
            Log.i(TAG, "setWaterTheme: setDotLayout");
            ((DotsLayout) view).setSelectorDrawable(dotSelectorIds[ordinal]);
        }
        if (viewId == R.id.watermain_out) {
            view.setBackgroundResource(buttonSelectorIds[ordinal]);
        }
        if (viewId == R.id.temperature_name || viewId == R.id.watermain_quality_name
                || viewId == R.id.watermain_flow_name || viewId == R.id.watermain_out) {
            ((TextView) view).setTextColor(waterOutTextColors[ordinal]);
        }
        if (viewId == R.id.watermain_time_clock || viewId == R.id.watermain_time_tip) {
            ((TextView) view).setTextColor(timeTextColors[ordinal]);
        }
    }

}

package com.viomi.waterpurifier.edison.entity;

import com.viomi.common.ApplicationUtils;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterPreference;

/**
 * @description:
 * @data:2021/12/6
 */
public class WaterStatusEntity {
    int[] waterStopTip = new int[]{R.drawable.watermain_theme1_stop, R.drawable.watermain_theme2_stop, R.drawable.watermain_theme3_stop};
    boolean isWaterOut;

    public WaterStatusEntity(Boolean isWaterOut) {
        this.isWaterOut = isWaterOut;
    }

    public String getBig() {
        String unit = "";
        if (isWaterOut) {
            unit = ApplicationUtils.getContext().getString(R.string.unit_flow);
        } else {
            unit = ApplicationUtils.getContext().getString(R.string.unit_temperature);
        }
        return unit;
    }

    public String getSmall() {
        String unit = "";
        if (isWaterOut) {
            unit = ApplicationUtils.getContext().getString(R.string.unit_temperature);
        } else {
            unit = ApplicationUtils.getContext().getString(R.string.unit_flow);

        }
        return unit;
    }

    public int waterTipDrawable() {
        int waterDrawableTip = 0;
        if (isWaterOut) {
            // 获取当前主题
            int currentThemeIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CURRENT_INDEX, 0);
            waterDrawableTip = waterStopTip[currentThemeIndex];
        } else {
            waterDrawableTip = R.drawable.watermain_theme_out;
        }
        return waterDrawableTip;
    }

    public String getWaterTip() {
        String waterOuttip = "";
        if (isWaterOut) {
            waterOuttip = ApplicationUtils.getContext().getString(R.string.water_out_stop);
        } else {
            waterOuttip = ApplicationUtils.getContext().getString(R.string.water_out);
        }
        return waterOuttip;
    }

    public boolean isWaterOut() {
        return isWaterOut;
    }

    public void setWaterOut(boolean waterOut) {
        isWaterOut = waterOut;
    }
}

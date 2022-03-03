package com.viomi.waterpurifier.edison.config;

import com.viomi.waterpurifier.edison.R;

/**
 * @author xinqi
 * @date 2021/02/02
 */
public enum WaterQualityEnum {
    // 纯净水
    PURIFIED_WATER(R.string.purified_water, R.drawable.purified_water),
    // 矿物质水
    MINERAL_WATER(R.string.mineral_water, R.drawable.mineral_water);

    public int nameStrId;
    public int iconDrawableId;

    WaterQualityEnum(int nameStrId, int iconDrawableId) {
        this.nameStrId = nameStrId;
        this.iconDrawableId = iconDrawableId;
    }
}

package com.viomi.waterpurifier.edison.config;

import com.viomi.waterpurifier.edison.R;

/**
 * @author hailang
 * @date 2020/8/31 00319:47
 */
public enum WaterFlowEnum {
    // 连续出水
    NO_LIMIT(0, R.string.cup_keep_out, R.drawable.cup_infinity),
    // 小杯
    SMALL(1, R.string.cup_small, R.drawable.cup_small),
    // 中杯
    MIDDLE(2, R.string.cup_middle, R.drawable.cup_mid),
    // 大杯
    BIG(3, R.string.cup_big, R.drawable.cup_big);

    public int index;
    public int nameId;
    public int iconId;

    WaterFlowEnum(int index, int nameId, int iconId) {
        this.index = index;
        this.nameId = nameId;
        this.iconId = iconId;
    }
}

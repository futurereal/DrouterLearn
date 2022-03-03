package com.viomi.waterpurifier.edison.config;

import com.viomi.waterpurifier.edison.R;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 温度模式
 */
public enum TemperatureModeEnum {

    NORMAL(R.string.normal_temp),
    WARM(R.string.water_warm),
    HOT(R.string.water_hot),
    BOILING(R.string.water_boiling),
    CUSTOM(R.string.custom_temp);
    public int nameTextId;

    TemperatureModeEnum(int nameTextId) {
        this.nameTextId = nameTextId;
    }
}

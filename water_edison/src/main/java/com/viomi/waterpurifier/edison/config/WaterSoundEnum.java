package com.viomi.waterpurifier.edison.config;


import com.viomi.waterpurifier.edison.R;

public enum WaterSoundEnum {
    WATER_OUT(R.raw.water_out),
    WATER_STOP(R.raw.water_stop);
    public int rawId;

    WaterSoundEnum(int rawId) {
        this.rawId = rawId;
    }
}
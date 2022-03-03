package com.viomi.ovensocommon.spec;

public enum MicroLevelEnum {
    MicroLevel1(1, "100W", "低火"),
    MicroLevel2(2, "300W", "中低火"),
    MicroLevel3(3, "500W", "中火"),
    MicroLevel4(4, "700W", "中高火"),
    MicroLevel5(5, "1000W", "高火");


    public int index;
    public String power;
    public String name;

    MicroLevelEnum(int index, String power, String name) {
        this.index = index;
        this.power = power;
        this.name = name;
    }
}

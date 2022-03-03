package com.viomi.iotdevice.main.newmiot.repository;

public class DevProp {
    public String manufacturer_1_1 = "";
    public String model_1_2 = "";
    public String serialnumber_1_3 = "";
    public String firmwarerevision_1_4 = "";
    //
    public int fault_2_1 = 0;
    public int mode_2_4 = 0;//0:无模式 1:智能模式 2:假日模式
    public int temperature_2_5 = 23;//室内温度
    //
    public int fridgetemperature_3_1 = 2;//冰箱冷藏调温范围：2~8℃
    public int targettemperature_3_2 = 2;
    public boolean on_3_3 = true;//冷藏室开关
    //
    public int temperatureanother_4_1 = -15;//冰箱冷冻调温范围：-15~-23℃
    public int targettemperatureanother_4_2 = -15;
    public boolean onanother_4_3 = true;//冷冻室开关
    //
    public boolean quickcooling_5_1 = false;
    public boolean quickfreeze5_2 = false;
    //
    public int frosttemp_6_1 = 0;
    public boolean forcedfrost_6_2 = false;
    public boolean forcenonstop_6_3 = false;
    public boolean heatingwire_6_4 = false;
}

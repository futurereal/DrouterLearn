package com.viomi.iotdevice.main.fridge_demo;

import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotProperty;

public class DevProp {
    public static String manufacturer_1_1 = "";
    public static String model_1_2 = "";
    public static String serialnumber_1_3 = "";
    public static String firmwarerevision_1_4 = "";
    //
    //public int fault_2_1 = 0;//
    public static int mode_2_4 = 0;//0:无模式 1:智能模式 2:假日模式
    public static int outdoor_temperature_2_5 = 23;//室内温度
    public static int working_time_2_6 = 0;//已工作时间
    public static int indoor_temperature_2_7 = 23;//室内温度

    //
    public static int fridgetemperature_3_1 = 2;//冰箱冷藏调温范围：2~8℃
    public static int targettemperature_3_2 = 2;
    public static boolean on_3_3 = true;//冷藏室开关
    public static boolean mode_3_4 = false;//速冷模式
    //
    public static int temperatureanother_4_1 = -15;//冰箱冷冻调温范围：-15~-23℃
    public static int targettemperatureanother_4_2 = -15;
    public static boolean mode_4_4 = true;//速冻模式
    //
    public static int fault_5_1 = 0;//异常码（bit位方式，具体查看标准文档）
    //
    public static int frosttemp_6_1 = 0;//化霜传感器温度
    public static boolean forcedfrost_6_2 = false;//强制化霜
    public static boolean forcenonstop_6_3 = false;//强制不停机、强制制冷
    public static boolean timecut_6_5 = false;//缩时
    public static int fan_speed_6_7 = 0;//风机转速
    public static boolean fan_status_6_8 = false;//风机状态
    public static boolean self_check_6_9 = false;//自检
    public static boolean commodity_check_6_10 = false;//商检

    public static ViotGetPropRequestBody getPropBody() {
        ViotGetPropRequestBody body = new ViotGetPropRequestBody();
        //基本信息
        body.getPropList().add(new ViotProperty(1, 0, 2, null));
        body.getPropList().add(new ViotProperty(1, 0, 4, null));
        //冰箱
        body.getPropList().add(new ViotProperty(2, 0, 4, null));//
        body.getPropList().add(new ViotProperty(2, 0, 7, null));//
        //冷藏室
        body.getPropList().add(new ViotProperty(3, 0, 1, null));//
        body.getPropList().add(new ViotProperty(3, 0, 2, null));//
        body.getPropList().add(new ViotProperty(3, 0, 3, null));//
        body.getPropList().add(new ViotProperty(3, 0, 4, null));//
        //冷冻室
        body.getPropList().add(new ViotProperty(4, 0, 1, null));//
        body.getPropList().add(new ViotProperty(4, 0, 2, null));//
        body.getPropList().add(new ViotProperty(4, 0, 4, null));//
        //冰箱面板
        body.getPropList().add(new ViotProperty(5, 0, 1, null));//故障码
        //工厂模式
        body.getPropList().add(new ViotProperty(6, 0, 1, null));//化霜传感器温度
        body.getPropList().add(new ViotProperty(6, 0, 2, null));//强制化霜
        body.getPropList().add(new ViotProperty(6, 0, 3, null));//强制不停机/强制制冷
        body.getPropList().add(new ViotProperty(6, 0, 5, null));//缩时
        body.getPropList().add(new ViotProperty(6, 0, 7, null));//风机转速
        body.getPropList().add(new ViotProperty(6, 0, 8, null));//风机状态
        body.getPropList().add(new ViotProperty(6, 0, 9, null));//自检
        body.getPropList().add(new ViotProperty(6, 0, 10, null));//商检
        return body;
    }

    public static void savePropValue(String key, Object value) {
        //logUtil.d(TAG, "savePropValue key = " + key + " value = " + value);
        switch (key) {
            case "1.1":
                manufacturer_1_1 = (String) value;
                break;
            case "1.2":
                model_1_2 = (String) value;
                break;
            case "1.3":
                serialnumber_1_3 = (String) value;
                break;
            case "1.4":
                firmwarerevision_1_4 = (String) value;
                break;
            case "2.4":
                mode_2_4 = (int) value;
                break;
            case "2.7":
                indoor_temperature_2_7 = (int) value;
                break;
            case "3.1":
                fridgetemperature_3_1 = (int) value;
                break;
            case "3.2":
                targettemperature_3_2 = (int) value;
                break;
            case "3.3":
                on_3_3 = ((int) value == 1);
                break;
            case "3.4":
                mode_3_4 = ((int) value == 1);
                break;
            case "4.1":
                temperatureanother_4_1 = (int) value;
                break;
            case "4.2":
                targettemperatureanother_4_2 = (int) value;
                break;
            case "4.4":
                mode_4_4 = ((int) value == 1);
                break;
            case "5.1":
                fault_5_1 = (int) value;
                break;
            case "6.1":
                frosttemp_6_1 = (int) value;
                break;
            case "6.2":
                forcedfrost_6_2 = ((int) value == 1);
                break;
            case "6.3":
                forcenonstop_6_3 = ((int) value == 1);
                break;
            case "6.5":
                timecut_6_5 = ((int) value == 1);
                break;
            case "6.7":
                fan_speed_6_7 = (int) value;
                break;
            case "6.8":
                fan_status_6_8 = ((int) value == 1);
                break;
            case "6.9":
                self_check_6_9 = ((int) value == 1);
                break;
            case "6.10":
                commodity_check_6_10 = ((int) value == 1);
                break;

        }
    }
}

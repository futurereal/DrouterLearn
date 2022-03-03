package com.viomi.ovensocommon.spec;

/**
 * Created by Ljh on 2020/9/21.
 * Description:属性
 */
public enum OvenPropEnum {
    //依赖电控板的上电  上报，不能主动从电控板读取
    FIRM_MANUFACTURER(1, 1, "MANUFACTURER"),// 生产厂商
    FIRM_MODEL(1, 2, "MODEL"),// 设备Model
    FIRM_SERIAL_NUMBER(1, 3, "SERIAL_NUMBER"),// 设备序列号
    FIRM_VERSION(1, 4, "FIRM_VER"),// 固件版本

    // 设备状态
    // 0: 空闲 1: 工作中  2: 暂停中 3: 已预约4: 已完成
    WORK_STATUS(2, 1, "workStatus"),
    // 故障位参考电控使用说明书
    DEVICE_FAULT(2, 2, "deviceFalut"),
    // 剩余时间(含预约下的空闲时间)
    LEFT_TIME(2, 7, "LEFT_TIME"),
    //已工作时间(含预约下的空闲时间)
    WORKING_TIME(2, 8, "WORKING_TIME"),
    // 当前的温度会经常上报
    TEMPER(2, 10, "TEMPER"),
    // 设置菜谱 自定义菜谱或者预置菜谱 的dishId
    DISHID(3, 1, "DISHID"),
    // 当前菜谱的名字
    DISHNAME(3, 2, "DISHNAME"),
    // 烹饪模式包含 自定义模式8 个
    MODE(3, 3, "MODE"),
    // 蒸烤的属性
    TEMPZ(3, 4, "TEMPZ"),
    TIMEZ(3, 5, "TIMEZ"),
    TEMPK(3, 6, "TEMPK"),
    TIMEK(3, 7, "TIMEK"),
    // 烤箱门状态
    DOOR_OPEN(3, 8, "DOOR_OPEN"),
    //    CUSTOM_DISHS(3, 10, "CUSTOM_DISHS"),  // 自定义菜谱只用于上报
    // 工作的总时长，蒸和烤加起来
    WORK_TOTAL_TIME(3, 11, "WORK_TOTAL_TIME"),
    // 工作完成的时刻
    FINISH_TIME(3, 13, "FINISH_TIME"),
    // false正常，true缺水
    LACK_WATER(3, 14, "LACK_WATER"),
    // 预约总时间
    APPOINT_TOTAL_TIME(3, 15, "PREPARE_TIME"),
    // 蒸烤箱设备硬件版本
    HARD_VERSION(3, 17, "HARD_VER"),
    // 灯
    LIGHT(3, 18, "LIGHT"),
    // 菜谱步骤  0: WuBuZhou  1: YuRe  预热   2: QuChuDaDanHuang  三色蒸蛋步骤
    RECIPE_STEP(3, 20, "RecipeStep"),
    //视频录制 0  0: JieShu 1: ZanTing  2: LuZhiZhong
    VIDEO_RECORD(3, 22, "VIDEO_RECORD"),
    // 微波档位 1 2 3 4 5
    MICRO_LEVEL(3, 23, "micro_level"),
    // 自定义模式的名字 ????废弃
    CUSTOM_MODE_NAME(3, 24, "MODE_NAME"),
    // 辅助模式的步骤，主要是除垢和除味
    ASSIT_MODE_STEP(3, 25, "assistModeStep"),
    MODE_STEP(3, 26, "mode-step"), // 除垢、除味步驟 接收属性变化，用户弹框 界面展示
    MICRO_TIME(3, 27, "micro-time"),   // 微波时间
    // 叠加模式烹饪的步骤
    CUSTOM_MODE_STEP(3, 29, "customModeStep"),
    // 水箱是否到位   true 表示 到位  false 代表不到位
    WATERTANK_ISCLOSE(3, 30, "waterTankIsClose"),
    // 自定义模式 菜谱的  烹饪属性字符串
    CUSTOM_MODE_CONTENT(3, 31, "customModeContent"),
    // 烤箱自定义菜谱的属性
    RECIPE1(4, 1, "recipeOne"), // 菜谱1
    RECIPE2(4, 2, "recipeTwo"), // 菜谱2
    RECIPE3(4, 3, "recipeThree"), // 菜谱3
    RECIPE4(4, 4, "recipeFour"), // 菜谱4
    RECIPE5(4, 5, "recipeFive"), // 菜谱5
    RECIPE6(4, 6, "recipeSix"), // 菜谱6
    RECIPE7(4, 7, "recipeEnghit"), // 菜谱7
    RECIPE8(4, 8, "recipeSeven"), // 菜谱8
    // 烤箱自定义组合模式的属性
    COMBINED_MODE1(5, 1, "modeOne"), // 组合模式1
    COMBINED_MODE2(5, 2, "modeTwo"), // 组合模式2
    COMBINED_MODE3(5, 3, "modeThree"), // 组合模式3
    COMBINED_MODE4(5, 4, "modeFour"), // 组合模式4
    COMBINED_MODE5(5, 5, "modeFive"), // 组合模式5
    COMBINED_MODE6(5, 6, "modeSix"), // 组合模式6
    COMBINED_MODE7(5, 7, "modeSeven"), // 组合模式7
    COMBINED_MODE8(5, 8, "modeEghit"),// 组合模式8
    CHECK_CODE(6, 1, "checkCode");   //安全校验码，设备三元组的key
    public int siid;
    public int piid;
    public String name;

    OvenPropEnum(int siid, int piid, String name) {
        this.siid = siid;
        this.piid = piid;
        this.name = name;
    }
}


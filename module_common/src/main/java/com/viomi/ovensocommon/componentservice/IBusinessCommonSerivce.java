package com.viomi.ovensocommon.componentservice;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;

import java.util.List;

/**
 * 业务模块公共的接口
 */
public interface IBusinessCommonSerivce {
    /**
     * 插件 设置属性
     *
     * @param propertyEntity
     */
    void dealPropertyFromPlug(PropertyEntity propertyEntity);

    /**
     * 插件 下发 action 指令
     *
     * @param siid
     * @param aiid
     * @param paramsProp
     */
    void doActionFromPlug(int siid, int aiid, List<PropertyEntity> paramsProp);

    /**
     * 处理固件的属性变化
     *
     * @param propertyEntity
     */
    void dealPropertyChangeFromFirm(PropertyEntity propertyEntity);

    /**
     * 处理固件的时间变化
     *
     * @param propertyEntity
     */
    void dealEventChangeFromFirm(PropertyEntity propertyEntity);


    /**
     * 首次上电启动
     */
    void isPowerOffLauncher();

    /**
     * 米家 登录状态的改变
     * @param isBind
     */
    void MiotLoginStatusChange(boolean isBind);

    /**
     * 云米登录状态的改变
     * @param isBind
     */
    void ViotLoginStatusChange (boolean isBind);

}

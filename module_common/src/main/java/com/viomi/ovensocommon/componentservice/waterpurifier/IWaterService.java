package com.viomi.ovensocommon.componentservice.waterpurifier;

import com.viomi.ovensocommon.componentservice.IBusinessCommonSerivce;

/**
 * @author qixin
 * @date 2021/08/17
 */
public interface IWaterService extends IBusinessCommonSerivce {

    boolean getChildLock();

    String getMineralLevel();
    
    void reportStandByTime(int standByIndex);
}

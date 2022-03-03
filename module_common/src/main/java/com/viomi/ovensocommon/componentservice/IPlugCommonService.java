package com.viomi.ovensocommon.componentservice;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @data:2022/1/7
 */
public interface IPlugCommonService {
    void reportData(Map proMap);

    void reportData(PropertyEntity propertyEntity);

    void reportData(List<PropertyEntity> reportDataEntities);

    void reportEvent(int sid, int eid);

    void reportAction(String eventName);

}

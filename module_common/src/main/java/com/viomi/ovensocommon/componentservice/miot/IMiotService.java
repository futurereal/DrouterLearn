package com.viomi.ovensocommon.componentservice.miot;

import com.viomi.ovensocommon.componentservice.IPlugCommonService;

public interface IMiotService extends IPlugCommonService {
    boolean isMiotBind();

    void getMiotBindKey(BindKeyCallBack bindKeyCallBack);

    void resetAndRebindMiot();
}

package com.viomi.ovensocommon.componentservice.ovenso;

import com.viomi.ovensocommon.componentservice.IBusinessCommonSerivce;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.spec.OvenPropEnum;

public interface IOvensoService extends IBusinessCommonSerivce {
    void setCombinedModeInfo(OvenPropEnum ovenPropEnum, String modeInfo);

    String getCombinedModeInfo(int modePiid);

    String getCombineRecipeInfo(int recipeId);

    String getRecipeName();
    
    void updateRecordStatus(int recordStateStart);

    boolean needFobideScreenOff();

    long insertVideoInfoMessage(VideoInfo videoInfo);

    void deleteVideoMessage(VideoInfo videoInfo);
}

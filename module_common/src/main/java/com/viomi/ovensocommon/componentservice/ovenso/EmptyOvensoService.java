package com.viomi.ovensocommon.componentservice.ovenso;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.db.VideoInfo;
import com.viomi.ovensocommon.spec.OvenPropEnum;

import java.util.List;

public class EmptyOvensoService implements IOvensoService {


    @Override
    public void setCombinedModeInfo(OvenPropEnum ovenPropEnum, String modeInfo) {

    }

    @Override
    public String getCombinedModeInfo(int modePiid) {
        return null;
    }

    @Override
    public String getCombineRecipeInfo(int recipeId) {
        return null;
    }

    @Override
    public String getRecipeName() {
        return null;
    }

    @Override
    public void updateRecordStatus(int recordStatus) {

    }

    @Override
    public boolean needFobideScreenOff() {
        return false;
    }

    @Override
    public long insertVideoInfoMessage(VideoInfo videoInfo) {
        return 0;
    }

    @Override
    public void deleteVideoMessage(VideoInfo videoInfo) {

    }

    @Override
    public void dealPropertyFromPlug(PropertyEntity propertyEntity) {

    }

    @Override
    public void doActionFromPlug(int siid, int aiid, List<PropertyEntity> paramsProp) {

    }

    @Override
    public void dealPropertyChangeFromFirm(PropertyEntity propertyEntity) {

    }

    @Override
    public void dealEventChangeFromFirm(PropertyEntity propertyEntity) {

    }

    @Override
    public void isPowerOffLauncher() {

    }

    @Override
    public void MiotLoginStatusChange(boolean isBind) {

    }

    @Override
    public void ViotLoginStatusChange(boolean isBind) {

    }
}

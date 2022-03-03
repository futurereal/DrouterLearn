package com.viomi.ovenso.ui.activity.running;

import com.viomi.ovensocommon.BasePresenter;
import com.viomi.ovensocommon.BaseView;
import com.viomi.ovensocommon.spec.OvenActionEnum;

/**
 * Created by Ljh on 2020/11/24.
 * Description:
 */
public interface RunContract {

    interface View extends BaseView {
        void updateWorkStatus(int workStatus);

        void updateLeftTime(int time);

        void updateCurrentTemperature(int currentTemperaturel);

        void updatePropertyView();

        void updateRecordStatus(int recordStatus);

        void finishSelft();

        void updateCookStep(int customStep);

        void updateModeStep(int modeStep);

        void waterTankState(boolean state);

        void showEidtFragment();

        void startCameraActivity();
    }

    abstract class Presenter<V extends BaseView> extends BasePresenter<View> {
        abstract void cmdRunOpt(OvenActionEnum actionEnum);
    }
}


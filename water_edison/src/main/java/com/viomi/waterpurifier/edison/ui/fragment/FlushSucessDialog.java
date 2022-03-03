package com.viomi.waterpurifier.edison.ui.fragment;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;

import io.reactivex.disposables.Disposable;

public class FlushSucessDialog extends BaseDialogFragment {
    private static final String TAG = "FlushSucessDialog";
    Disposable disposable;

    boolean checkOut;
    boolean checkBack;

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        disposable = ViomiRxBus.getInstance().subscribeUi(event -> {
            if (event.getMsgId() != WaterBusEvent.MSG_FILTER_FLUSH) {
                return;
            }
            PropertyEntity propertyEntity = (PropertyEntity) event.getMsgObject();
            if (WaterPropEnum.WATER_OUT_STATUS.piid == propertyEntity.getPid()) {
                if (checkBack) {//已放回水箱

                }
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_flush_success;
    }


    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        if (disposable != null) {
            disposable.dispose();
        }
    }

}

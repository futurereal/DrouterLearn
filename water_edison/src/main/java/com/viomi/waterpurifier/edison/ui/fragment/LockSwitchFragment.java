package com.viomi.waterpurifier.edison.ui.fragment;

import android.util.Log;

import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.router.annotation.Route;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.FragmentLockSwitchBinding;
import com.viomi.waterpurifier.edison.util.WaterUtils;

@Route(path = ViomiRouterConstant.WATER_FRAGMENT_CHILD_LOCK)
public class LockSwitchFragment extends BindingBaseFragment<FragmentLockSwitchBinding> {
    private static final String TAG = "LockSwitchFragment";

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_lock_switch;
    }

    @Override
    protected void initView() {

    }


    @Override
    public void initData() {
        boolean isOpen = (boolean) WaterUtils.getLocalWaterProps(WaterPropEnum.CHILD_LOCK, WaterConstant.CHILD_LOCK_DEFAULT);
        Log.i(TAG, "initData:  isOpen: " + isOpen);
        viewDataBinding.lockswitchSwitch.setOn(isOpen);
        updateLockStatue(isOpen);
    }

    @Override
    public void initListener() {
        viewDataBinding.lockswitchSwitch.setOnSwitchStateChangeListener(isOn -> {
            Log.i(TAG, "initListener: isOne: " + isOn);
            WaterUtils.setLocalWaterProps(WaterPropEnum.CHILD_LOCK, isOn);
            updateLockStatue(isOn);
            PropertyEntity switchEntity = new PropertyEntity();
            switchEntity.setSid(WaterPropEnum.CHILD_LOCK.siid);
            switchEntity.setPid(WaterPropEnum.CHILD_LOCK.piid);
            switchEntity.setContent(isOn);
            ModuleSettingServiceFactory.getInstance().getViotService().reportData(switchEntity);
            ViomiRxBus.getInstance().post(CommonConstant.MSG_CHILDLOCK_SWITCH, isOn);
        });
    }

    private void updateLockStatue(boolean isOpen) {
        int lockContentId = isOpen ? R.string.setting_lock_on : R.string.setting_lock_off;
        viewDataBinding.lockswitchTipText.setText(lockContentId);
        viewDataBinding.lockswitchTipImg.setSelected(isOpen);
    }

}

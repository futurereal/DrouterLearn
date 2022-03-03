package com.viomi.modulesetting.ui.fragment.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentRebootBinding;
import com.viomi.ovensocommon.BaseDialogFragment;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.ui.fragment.dialog
 * @ClassName: ResetSystemFragment
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/3/17 2:01 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/17 2:01 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class RebootFragment extends BaseDialogFragment<FragmentRebootBinding> {


    @Override
    protected void initView() {
        viewDataBinding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        viewDataBinding.sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(ModuleSettingConstants.BROADCAST_REBOOT);
                dismissAllowingStateLoss();
                v.getContext().sendBroadcast(intent);
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_reboot;
    }




}

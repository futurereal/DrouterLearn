package com.viomi.waterpurifier.edison.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.databinding.FragmentSelfCleanBinding;

/**
 * @author lxq
 * @date 2021-07-26
 * @Description 自清洁dialog
 */
public class SelfCleanFragment extends BaseDialogFragment<FragmentSelfCleanBinding> {
    private final static String TAG = SelfCleanFragment.class.getSimpleName();
    private final static String SECOND_UNIT = "''";
    private final static long TOTAL_TIME = 50 * 1000;
    private final static long ONE_SECOND = 1000;
    private final static String SELF_CLEAN_ANIM_FILE = "clean.json";
    private CountDownTimer countDownTimer;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_self_clean;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initChildView: ");
        viewDataBinding.selfcleanAnim.setAnimation(SELF_CLEAN_ANIM_FILE);
        viewDataBinding.selfcleanAnim.playAnimation();
        ModuleSettingServiceFactory.getInstance().getViotService().setKeepScreenOn(true);
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        countDownTimer = new CountDownTimer(TOTAL_TIME, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "onTick:millisUntilFinished:  " + millisUntilFinished);
                long countDownSecond = millisUntilFinished / ONE_SECOND;
                String countDownSecondStr = countDownSecond + SECOND_UNIT;
                viewDataBinding.selfcleanCountdown.setText(countDownSecondStr);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "self clean---timer onFinish: ");
                dismissAllowingStateLoss();
            }
        }.start();
        Log.d(TAG, "self clean---initView: timer start: " + countDownTimer.hashCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
            window.setBackgroundDrawableResource(R.color.transparent);
        }
    }

    /**
     * 1 、倒计时结束执行 dismiss
     * 2 、固件发送idle 的属性变化，强制结束
     *
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ViomiToastUtil.showToastCenter(getString(R.string.self_clean_succeed_tip));
        viewDataBinding.selfcleanAnim.cancelAnimation();
        countDownTimer.cancel();
        ModuleSettingServiceFactory.getInstance().getViotService().setKeepScreenOn(false);
        Log.d(TAG, "self clean---onDismiss: ");
    }
}

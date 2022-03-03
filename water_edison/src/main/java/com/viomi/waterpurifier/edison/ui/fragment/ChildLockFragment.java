package com.viomi.waterpurifier.edison.ui.fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.databinding.FragmentChildlockBinding;
import com.viomi.waterpurifier.edison.widget.CircleProgressView;

/**
 * Created by Ljh on 2020/11/30.
 * Description:
 */
public class ChildLockFragment extends BaseDialogFragment<FragmentChildlockBinding> {
    private static final String TAG = "ChildLockFragment";

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_childlock;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        viewDataBinding.childlockProgress.setListener(new CircleProgressView.OnProgressUpdateListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: ");
            }

            @Override
            public void onProgressUpdate(int currentProgress) {
                Log.i(TAG, "onProgressUpdate: ");
            }

            @Override
            public void onEnd() {
                Log.i(TAG, "onEnd: ");
                ViomiRxBus.getInstance().post(WaterBusEvent.MSG_CHILDLOCK_SUCCESS);
                dismissAllowingStateLoss();
            }
        });
        viewDataBinding.childlockClose.setOnClickListener(v -> dismissAllowingStateLoss());
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isLandScreen = ScreenUtils.isLandscape();
        WindowManager.LayoutParams layoutParmater = window.getAttributes();
        if (isLandScreen) {
            layoutParmater.width = layoutParmater.height;
        } else {
            layoutParmater.width = 720;
            layoutParmater.height = 700;
            layoutParmater.gravity = Gravity.BOTTOM;
        }
        Log.i(TAG, "onStart: width: " + layoutParmater.width + "  height : " + layoutParmater.height);
        window.setAttributes(layoutParmater);
    }

}


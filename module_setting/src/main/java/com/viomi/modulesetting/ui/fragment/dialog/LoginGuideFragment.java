package com.viomi.modulesetting.ui.fragment.dialog;

import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentLoginGuideBinding;
import com.viomi.modulesetting.ui.adapter.LoginGuidePageFragmentAdapter;
import com.viomi.modulesetting.ui.fragment.LoginGuideOneFragment;
import com.viomi.modulesetting.ui.fragment.LoginGuildeTwoFragment;
import com.viomi.modulesetting.view.CircleIndicator;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.utils.ViomiScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 云米登录操作指引流程
 */
public class LoginGuideFragment extends BaseDialogFragment<FragmentLoginGuideBinding> {
    private static final String TAG = "LoginGuideFragment";

    private final int[] guildeImageResources = new int[]{R.drawable.loginguide_index_one, R.drawable.loginguide_index_two,
            R.drawable.loginguide_index_three, R.drawable.loginguide_index_four};

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_login_guide;
    }


    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        viewDataBinding.loginguildeIndicator.setIndicatorRadius(ViomiScreenUtil.dp2px(requireContext(), 6));
        viewDataBinding.loginguildeIndicator.setIndicatorMargin(ViomiScreenUtil.dp2px(requireContext(), 18));
        viewDataBinding.loginguildeIndicator.setIndicatorMode(CircleIndicator.Mode.OUTSIDE);
        viewDataBinding.loginguildeIndicator.setIndicatorFillColor(ContextCompat.getColor(getContext(), R.color.color_9b_20));
        viewDataBinding.loginguildeIndicator.setIndicatorSelectedBackground(ContextCompat.getColor(getContext(), R.color.color_green_light));
        // 第一个fragment
        List<Fragment> guideFragmentList = new ArrayList<>();
        guideFragmentList.add(new LoginGuideOneFragment());
        // 第二个 到 第五个fragment
        for (int resoureId : guildeImageResources) {
            guideFragmentList.add(LoginGuildeTwoFragment.instance(resoureId));
        }
        Log.i(TAG, "initChildView: size: " + guideFragmentList.size());
        LoginGuidePageFragmentAdapter adapter = new LoginGuidePageFragmentAdapter(getChildFragmentManager(), guideFragmentList);
        viewDataBinding.loginguildeViewpager.setAdapter(adapter);
        viewDataBinding.loginguildeIndicator.setViewPager(viewDataBinding.loginguildeViewpager);
    }


    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.loginguildeClose.setOnClickListener(v -> dismissAllowingStateLoss());
    }


    @Override
    public void onStart() {
        boolean isLandScreen = ScreenUtils.isLandscape();
        if (isLandScreen) {
            landWidth = landHeight;
        } else {
            landHeight = landWidth;
        }
        super.onStart();
    }

}

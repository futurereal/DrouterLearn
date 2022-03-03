package com.viomi.ovenso.ui.fragment;

import android.os.Bundle;
import android.util.Log;

import com.viomi.ovenso.bean.OvenRecipeStepEnum;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.FragmentRecipeStepBinding;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.utils.MediaPlayerUtils;

/**
 * 菜谱步骤的弹框
 */
public class RecipeStepFragment extends BaseDialogFragment<FragmentRecipeStepBinding> {
    private static final String TAG = "RecipeStepFragment";
    private static final int NO_VOICE_RESOURCE = 0;
    public static final String KEY_RECIPE_STEP_VALUE = "keyRecipeStepValue";

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_recipe_step;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        int recipStepValue = bundle.getInt(KEY_RECIPE_STEP_VALUE);
        Log.i(TAG, "initView: recipeStepValue: " + recipStepValue);
        OvenRecipeStepEnum ovenRecipeStepEnum = OvenRecipeStepEnum.values()[recipStepValue];
        Log.i(TAG, "initView: " + getString(ovenRecipeStepEnum.stepResourceId));
        viewDataBinding.recipestepTip.setText(ovenRecipeStepEnum.stepResourceId);
        int voiceResource = ovenRecipeStepEnum.voiceResource;
        if (voiceResource != NO_VOICE_RESOURCE) {
            MediaPlayerUtils.getInstance().startPlayRawResource(voiceResource);
        }
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.recipestepKnown.setOnClickListener(view -> {
            dismissAllowingStateLoss();
        });
    }

}

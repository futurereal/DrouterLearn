package com.viomi.ovenso.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.PropertyUtil;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.CookrunTestviewBinding;
import com.viomi.ovenso.util.OvenTestUtil;


/**
 * @author
 * @date 2022/01/11
 * @describe 烹饪界面弹框测试
 */
public class CookRunningTestView extends FrameLayout {
    private static final String TAG = "CookRunningTestView";
    private CookrunTestviewBinding itemCustomModeBinding;

    public CookRunningTestView(@NonNull Context context) {
        super(context);
    }

    public CookRunningTestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "CookRunningTestView: ");
        LayoutInflater.from(context).inflate(R.layout.cookrun_testview, this);
        itemCustomModeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.cookrun_testview, this, true
        );
        if (OvenConstants.IS_TEST_UI) {
            itemCustomModeBinding.cookrunTestGroup.setVisibility(View.VISIBLE);
        }
        initListener(itemCustomModeBinding);
    }

    private void initListener(CookrunTestviewBinding itemCustomModeBinding) {
        Log.i(TAG, "initListener: ");
        itemCustomModeBinding.cookrunTestStep.setOnClickListener(view -> {
            OvenTestUtil.testModeStep();
        });
        itemCustomModeBinding.cookrunTestChugou.setOnClickListener(view -> {
            OvenTestUtil.testChugou();
        });
        itemCustomModeBinding.cookrunTestRecipestep.setOnClickListener(view -> {
            OvenTestUtil.testRecipeStep();
        });
        itemCustomModeBinding.cookrunTestWaterTank.setOnClickListener(view -> {
            OvenTestUtil.testWaterTankError();
        });
    }

    public void updateProperty() {
        String propertytip = PropertyUtil.getTestProperty();
        itemCustomModeBinding.cookrunTestProperty.setText(propertytip);
    }


}

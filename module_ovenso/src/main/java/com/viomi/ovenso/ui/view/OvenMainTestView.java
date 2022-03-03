package com.viomi.ovenso.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.AppUtils;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.PropertyUtil;
import com.viomi.ovenso.microwave.BuildConfig;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.OvenmainTestviewBinding;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.spec.OvenPropEnum;

/**
 * @description:
 * @data:2022/2/10
 */
public class OvenMainTestView extends ConstraintLayout {
    private static final String TAG = "OvenMainTestView";
    private final OvenmainTestviewBinding mainTestViewBinding;

    public OvenMainTestView(@NonNull Context context) {
        this(context, null);
    }

    public OvenMainTestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OvenMainTestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mainTestViewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.ovenmain_testview, this, true
        );
        initListener();
    }

    private void initListener() {
        Log.i(TAG, "initListener: ");
        mainTestViewBinding.ovenmaintestCustomemode.setOnClickListener(view -> {
            String customeModeFive = "105,6auY5rip6JK4MueDremjjueDpA==,1,13,110,1,12,105,17,11,180";
            OvensoServiceFactory.getInstance().getOvenService().setCombinedModeInfo(OvenPropEnum.COMBINED_MODE5, customeModeFive);
        });

        mainTestViewBinding.ovenmaintestRecipe.setOnClickListener(view -> {
            String customeRecipeFive = "105,6I+c6LCxM+eDremjjuW+ruazog==,17,12,180,34,58,5";
            OvensoServiceFactory.getInstance().getOvenService().setCombinedModeInfo(OvenPropEnum.RECIPE5, customeRecipeFive);
        });
    }

    public void updateProperty() {
        String propertytip = PropertyUtil.getTestProperty();
        mainTestViewBinding.ovenmaintestProperty.setText(propertytip);
    }

    public void showBuildInfo(boolean isSoSeven) {
        // 更新构建时间
        String chanelName = OvenConstants.IS_OTA_UPDATE ? getResources().getString(R.string.oven_channel_test) :
                getResources().getString(R.string.oven_channel_product);
        int versionCode = AppUtils.getAppVersionCode();
        String currentModelName = isSoSeven ? "So7" : "So6";
        String buildInfo = chanelName + currentModelName + "-v" + versionCode + "-" + BuildConfig.apkBuildTime;
        Log.i(TAG, "showBuildInfo: buildInfo: " + buildInfo);
        mainTestViewBinding.ovenmaintestBuildtime.setText(buildInfo);
    }
}

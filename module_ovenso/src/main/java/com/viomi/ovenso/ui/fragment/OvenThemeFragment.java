package com.viomi.ovenso.ui.fragment;

import android.text.TextUtils;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;

import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.OvenThemeType;
import com.viomi.ovenso.bean.ThemeItemEntity;
import com.viomi.ovenso.util.preference.OvenPreference;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.FragmentOvenThemeBinding;
import com.viomi.ovenso.ui.adapter.OvenThemeAdapter;
import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.router.annotation.Route;

import java.util.ArrayList;

/**
 * 主题设置界面
 *
 * @author hailang
 * @date 2020/9/3
 */
@Route(path = ViomiRouterConstant.OVENSO_THEME)
public class OvenThemeFragment extends BindingBaseFragment<FragmentOvenThemeBinding> {
    private static final String TAG = "OvenThemeFragment";
    public static final int SPAN_COUNT = 2;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_oven_theme;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void initData() {
        // 从sp里面取出主题
        String currentThemeName = OvenPreference.getInstance().getString(OvenConstants.KEY_THEME_OVENSO, OvenConstants.THEME_NAME_DEFAULT);
        // 主题的全部资源，并且当前主题选中
        ArrayList<ThemeItemEntity> themeItemEntityList = new ArrayList<>();
        for (OvenThemeType type : OvenThemeType.values()) {
            ThemeItemEntity themeItemEntity = new ThemeItemEntity(type.name, type.themeDrawablethumbnail, type.themeDrawable);
            themeItemEntityList.add(themeItemEntity);
            if (TextUtils.equals(currentThemeName, themeItemEntity.getThemeDrawable())) {
                themeItemEntity.setSelect(true);
            }
        }
        Log.i(TAG, "initData: themeItemEntityList size :  " + themeItemEntityList.size());
        // 如果没有设置主题，默认主题显示
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        viewDataBinding.themeList.setLayoutManager(gridLayoutManager);

        OvenThemeAdapter ovenThemeAdapter = new OvenThemeAdapter(themeItemEntityList);
        viewDataBinding.themeList.setAdapter(ovenThemeAdapter);

        ovenThemeAdapter.setOnItemClickListener((parent, view, position, id) -> {
            ThemeItemEntity entity = themeItemEntityList.get(position);
            for (ThemeItemEntity themeItemEntity : themeItemEntityList) {
                themeItemEntity.setSelect(false);
            }
            entity.setSelect(true);
            ovenThemeAdapter.notifyDataSetChanged();
            OvenPreference.getInstance().setValue(OvenConstants.KEY_THEME_OVENSO, entity.getThemeDrawable());
            ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_THEME_CHANGED);
        });
    }
}

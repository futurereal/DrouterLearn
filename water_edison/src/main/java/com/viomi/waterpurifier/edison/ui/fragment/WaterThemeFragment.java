package com.viomi.waterpurifier.edison.ui.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.ItemDecorationUtil;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.WaterPreference;
import com.viomi.waterpurifier.edison.databinding.FragmentWaterThemeBinding;
import com.viomi.waterpurifier.edison.entity.WaterThemeEntity;
import com.viomi.waterpurifier.edison.ui.adapter.WaterThemeAdapter;
import com.viomi.waterpurifier.edison.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;


/**
 * @author admin
 * @description: 主题切换的
 * @date:2021/10/21
 */
@Route(path = ViomiRouterConstant.WATER_FRAGMENT_THEME)
public class WaterThemeFragment extends BindingBaseFragment<FragmentWaterThemeBinding> {
    private static final String TAG = "WaterThemeFragment";
    // 默认三天
    public static final int DEFAULT_CHANGE_TIME_INDEX = 2;
    private static final int MIN_SIZE = 4;
    private static final String COMMA = ",";
    private List<WaterThemeEntity> waterThemeList;
    private WaterThemeAdapter waterThemeAdapter;
    private int currentTimeIndex;
    private boolean isThemeAutoSwitch;
    private String themeAllIndex = "";
    private int beforeThemeIndex;
    private int currentThemeIndex;

    private Disposable timeDisposable;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_water_theme;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void initData() {
        Log.i(TAG, "initData: ");
        waterThemeList = getWaterThemeList();
        waterThemeAdapter = new WaterThemeAdapter(waterThemeList);
        viewDataBinding.waterthemeAlltheme.setAdapter(waterThemeAdapter);
        DividerItemDecoration itemDecoration = ItemDecorationUtil.getItemDecoration(30);
        viewDataBinding.waterthemeAlltheme.addItemDecoration(itemDecoration);
        isThemeAutoSwitch = (boolean) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_SWITCH, false);
        // 默认是关闭的，如果是关闭的则 没有回调，打开的话就有回调
        if (isThemeAutoSwitch) {
            themeAllIndex = (String) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_AUTO_INDEX, "");
            viewDataBinding.waterthemeSwitch.setOn(true);
            currentTimeIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CHANGETIME, DEFAULT_CHANGE_TIME_INDEX);
            viewDataBinding.waterthemeFrequency.setText(TimeUtil.getChangeDateName(currentTimeIndex));
        } else {
            beforeThemeIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CURRENT_INDEX, 0);
            waterThemeList.get(beforeThemeIndex).setSelected(true);
        }
        waterThemeAdapter.notifyDataSetChanged();

        waterThemeAdapter.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "onItemClick: position: " + position);
            dealItemClick(position);
        });
    }

    private void dealItemClick(int position) {
        WaterThemeEntity waterThemeEntity = waterThemeList.get(position);
        boolean isItemSelect = waterThemeEntity.isSelected();
        Log.i(TAG, "dealItemClick: isThemeSwitch " + isThemeAutoSwitch + " isItemSelect:  " + isItemSelect);
        // 单选 ，当前是选中状态不更新
        if (!isThemeAutoSwitch && isItemSelect) {
            Log.i(TAG, "dealItemClick:  single  return");
            return;
        }
        // 单选当前是非选中状态,更新所有状态
        if (!isThemeAutoSwitch) {
            for (int i = 0; i < waterThemeList.size(); i++) {
                if (i == position) {
                    WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_THEME_CURRENT_INDEX, position);
                    currentThemeIndex = i;
                }
                waterThemeList.get(i).setSelected(i == position);
            }
            return;
        }
        // 多选，如果当前是选中， 总共选中的 是 2个，提示
        if (isItemSelect && themeAllIndex.length() <= MIN_SIZE) {
            String tip = "至少需要选择2个主题";
            ViomiToastUtil.showToastCenter(tip);
            return;
        }
        waterThemeList.get(position).setSelected(!isItemSelect);
        dealThemeAll();
    }

    private void dealThemeAll() {
        themeAllIndex = "";
        for (int i = 0; i < waterThemeList.size(); i++) {
            WaterThemeEntity waterThemeEntity = waterThemeList.get(i);
            if (waterThemeEntity.isSelected()) {
                themeAllIndex += i + COMMA;
            }
            Log.d(TAG, "dealThemeAll: i: " + i + "   themeAllIndex:  " + themeAllIndex);
        }
        WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_THEME_AUTO_INDEX, themeAllIndex);
        Log.i(TAG, "dealThemeAll: themeAllIndex: " + themeAllIndex);
    }

    private List<WaterThemeEntity> getWaterThemeList() {
        int[] nameIds = new int[]{R.string.water_theme_first, R.string.water_theme_second, R.string.water_theme_third};
        int[] typeResourceIds = new int[]{R.drawable.watertheme_sample1, R.drawable.watertheme_sample2, R.drawable.watertheme_sample3};
        List<WaterThemeEntity> waterThemeEntityList = new ArrayList<>(nameIds.length);
        for (int i = 0; i < nameIds.length; i++) {
            WaterThemeEntity waterThemeEntity = new WaterThemeEntity(nameIds[i], typeResourceIds[i], false);
            waterThemeEntityList.add(waterThemeEntity);
        }
        Log.i(TAG, "getWaterThemeList: " + waterThemeEntityList.size());
        return waterThemeEntityList;
    }


    @Override
    public void initListener() {
        viewDataBinding.waterthemeSwitch.setOnSwitchStateChangeListener(isOn -> {
            Log.i(TAG, "initListener:waterThemeSwitch " + isOn);
            dealSwitchChange(isOn);
        });

        viewDataBinding.waterthemeFrequency.setOnClickListener(v -> showThemeTimeFragment());

        timeDisposable = ViomiRxBus.getInstance().subscribe(event -> {
            if (event.getMsgId() == WaterBusEvent.MSG_THEME_TIME_CHANGE) {
                currentTimeIndex = (int) event.getMsgObject();
                Log.d(TAG, "initListener: currentTimeIndex: " + currentTimeIndex);
                viewDataBinding.waterthemeFrequency.setText(TimeUtil.getChangeDateName(currentTimeIndex));
            }
        });

        addDisposable(timeDisposable);
    }

    private void showThemeTimeFragment() {
        Log.i(TAG, "showThemeTimeFragment: ");
        WaterThemeTimeFragment timeFragment = new WaterThemeTimeFragment(currentTimeIndex);
        timeFragment.setArguments(timeFragment.makeBundle(currentTimeIndex));
        timeFragment.show(getParentFragmentManager(), "WaterThemeTimeFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        currentTimeIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CHANGETIME, DEFAULT_CHANGE_TIME_INDEX);
        Log.i(TAG, "onResume: " + currentTimeIndex + "  isThemeSwitch: " + isThemeAutoSwitch);
        if (isThemeAutoSwitch) {
            viewDataBinding.waterthemeFrequency.setText(TimeUtil.getChangeDateName(currentTimeIndex));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: isThemeAutoSwitch:" + isThemeAutoSwitch + "  themeAllIndex: " + themeAllIndex);
        Log.i(TAG, "onDestroy:beforeThemeIndex: " + beforeThemeIndex + " currentThemeIndex: " + currentThemeIndex);
        // 非自动切换，之前 和当前 的 不相同
        if (!isThemeAutoSwitch && (beforeThemeIndex == currentThemeIndex)) {
            return;
        }
        WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_THEME_AUTO_INDEX, themeAllIndex);
        ViomiRxBus.getInstance().post(WaterBusEvent.MSG_SETTING_THEME_CHANGE, isThemeAutoSwitch);
    }

    /**
     * 处理自动更换开关的逻辑
     *
     * @param isOn
     */
    private void dealSwitchChange(boolean isOn) {
        Log.i(TAG, "dealSwitchChange: isOne: " + isOn);
        isThemeAutoSwitch = isOn;
        WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_THEME_SWITCH, isOn);
        // 更新频率的切换
        viewDataBinding.themeGroupFrequency.setVisibility(isOn ? View.VISIBLE : View.GONE);

        int changeTimeIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CHANGETIME, DEFAULT_CHANGE_TIME_INDEX);
        String changeTimeName = TimeUtil.getChangeDateName(changeTimeIndex);
        viewDataBinding.waterthemeFrequency.setText(changeTimeName);
        int currentThemeAllIndex = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_THEME_CURRENT_INDEX, 0);
        Log.i(TAG, "dealSwitchChange: currentThemeIndex: " + currentThemeAllIndex + " allIndexStr:  " + themeAllIndex);
        StringBuilder curThemeAllIndex = new StringBuilder(themeAllIndex);
        for (int i = 0; i < waterThemeList.size(); i++) {
            WaterThemeEntity waterThemeEntity = waterThemeList.get(i);
            if (!isOn) {
                waterThemeEntity.setSelected(currentThemeAllIndex == i);
                continue;
            }
            if (TextUtils.isEmpty(themeAllIndex) || themeAllIndex.length() < MIN_SIZE) {
                waterThemeEntity.setSelected(true);
                curThemeAllIndex.append(i).append(COMMA);
                continue;
            }
            boolean isContainIndex = curThemeAllIndex.toString().contains(String.valueOf(i));
            Log.i(TAG, "dealSwitchChange: isContainIndex: " + isContainIndex);
            waterThemeEntity.setSelected(isContainIndex);
        }
        if (isOn) {
            themeAllIndex = curThemeAllIndex.toString();
            Log.d(TAG, "dealSwitchChange: themeAllIndex: " + themeAllIndex);
        }
        waterThemeAdapter.notifyDataSetChanged();
    }

}

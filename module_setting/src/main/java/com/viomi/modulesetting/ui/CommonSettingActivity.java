package com.viomi.modulesetting.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.common.VLogUtil;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.ModuleSettingApplicaiton;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.ActivityCommonsettingBinding;
import com.viomi.modulesetting.entity.SettingMenuEntity;
import com.viomi.modulesetting.ui.adapter.SettingMenuAdapter;
import com.viomi.modulesetting.utils.DeviceUtil;
import com.viomi.modulesetting.utils.MenuInfoUtil;
import com.viomi.modulesetting.utils.softap.SoftApData;
import com.viomi.modulesetting.utils.softap.SoftApManagerUtils;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.componentservice.waterpurifier.WaterServiceFactory;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;
import com.viomi.router.core.utils.RefInvoke;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author admin
 * @describe: 公共设置界面
 * @data:2021/7/13
 */
@Route(path = ViomiRouterConstant.SETTING_COMMON_SETTING)
public class CommonSettingActivity extends BaseCommonSettingActivity<ActivityCommonsettingBinding> {
    private static final String TAG = "CommonSettingActivity";
    private static final String PACKAGE_MODULE_SETTING = "com.viomi.modulesetting";
    private static final String DEFAULT_FRAGMENT_ROUTER = ViomiRouterConstant.SETTING_FRAGMENT_ACCOUNT;
    public static final int HEAD_INDEX = 0;
    private SettingMenuAdapter settingMenuAdapter;
    // 是否显示 内容
    private boolean isShowSettingContent;
    private List<SettingMenuEntity> settingMenuEntities;
    private final static String WATER_PURIFIER_PACKAGE_NAME = "com.viomi.waterpurifier.edison";
    private CompositeDisposable mCompositeDisposable;
    private SettingMenuEntity lockEntity;
    private int beforeIndex;
    private SettingMenuEntity wifiEntity;
    private SettingMenuEntity softEntity;

    @Override
    public int onBindLayout() {
        return R.layout.activity_commonsetting;
    }

    @Override
    public void initView() {
        Log.i(TAG, "initView start: ");
        String packageName = DeviceUtil.getPackageName(this);
        Log.d(TAG, "packageName : " + packageName);
        int colorBg = getResources().getColor(R.color.settingfragment_bg);
        viewDataBinding.settingContent.setBackgroundColor(colorBg);
        viewDataBinding.settingMenu.setBackgroundColor(colorBg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        viewDataBinding.settingMenu.setLayoutManager(linearLayoutManager);
        settingMenuAdapter = new SettingMenuAdapter();
        viewDataBinding.settingMenu.setAdapter(settingMenuAdapter);
        if (CommonConstant.SHOW_DEBUG_INFO) {
            viewDataBinding.settingCleanlog.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initData() {
        Log.e(TAG, "initData start: ");
        Bundle extrasBundle = getIntent().getExtras();
        String fragmentRouterName = "";
        if (extrasBundle != null) {
            beforeIndex = extrasBundle.getInt(ViomiRouterConstant.SETTING_KEY_MENUINDEX);
            fragmentRouterName = extrasBundle.getString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER);
        } else {
            fragmentRouterName = DEFAULT_FRAGMENT_ROUTER;
        }
        int menuArrayId;
        // 如果是 isLibary = false ,单独调试 moduleSetting
        if (TextUtils.equals(getPackageName(), PACKAGE_MODULE_SETTING)) {
            menuArrayId = R.array.menu_module_setting;
        } else {
            menuArrayId = ModuleSettingServiceFactory.getInstance().getViotService().getMenuArrayId();
        }
        Log.i(TAG, "initData: menuArrayId: " + menuArrayId);
        settingMenuEntities = MenuInfoUtil.getMenuList(menuArrayId);
        settingMenuEntities.get(beforeIndex).setSelected(true);
        settingMenuAdapter.updateDate(settingMenuEntities);
        initStatusBackground();
        if (ScreenUtils.isLandscape()) {
            isShowSettingContent = true;
            viewDataBinding.titleBar.setBackgroundResource(R.color.menufragment_select);
            initSettingFragment(fragmentRouterName);
        }
        refreshUserInfo(ModuleSettingApplicaiton.getUserInfoDb());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.i(TAG, "onNewIntent: ");
        if (!isShowSettingContent) {
            Log.i(TAG, "onNewIntent: showSettingcontent is false return ");
            return;
        }
        Bundle extrasBundle = getIntent().getExtras();
        String fragmentRouterName = "";
        if (extrasBundle != null) {
            Log.i(TAG, "onNewIntent: beforeIndex: " + beforeIndex);
            settingMenuEntities.get(beforeIndex).setSelected(false);
            beforeIndex = extrasBundle.getInt(ViomiRouterConstant.SETTING_KEY_MENUINDEX);
            settingMenuEntities.get(beforeIndex).setSelected(true);
            fragmentRouterName = extrasBundle.getString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER);
        } else {
            fragmentRouterName = DEFAULT_FRAGMENT_ROUTER;
        }
        initSettingFragment(fragmentRouterName);
        settingMenuAdapter.notifyDataSetChanged();
    }

    private void initStatusBackground() {
        Log.i(TAG, "initStatusBackground: ");
        //wifi 是否是打开状态
        boolean isWifiEnable = NetworkUtils.getWifiEnabled();
        String wifiStatus = isWifiEnable ? getResources().getString(R.string.menuitem_status_open) : getResources().getString(R.string.menuitem_status_close);
        wifiEntity = new SettingMenuEntity();
        wifiEntity.setRoutePath(ViomiRouterConstant.SETTING_FRAGMENT_WLAN);
        Log.i(TAG, "initStatusBackground:settingMenuEntities:  " + settingMenuEntities);
        if (settingMenuEntities != null && settingMenuEntities.contains(wifiEntity)) {
            Log.i(TAG, "initStatusBackground: updateWifiStatus");
            settingMenuEntities.get(settingMenuEntities.indexOf(wifiEntity)).setStatus(wifiStatus);
        }
        // 设备热点是否打开
        SoftApData softInfo = SoftApManagerUtils.getSoftApData(this);
        if (softInfo != null) {
            String softStatus = softInfo.getWifiOnOff() == 1 ? getResources().getString(R.string.menuitem_status_open) : getResources().getString(R.string.menuitem_status_close);
            softEntity = new SettingMenuEntity();
            softEntity.setRoutePath(ViomiRouterConstant.SETTING_FRAGMENT_HOTSPOT);
            settingMenuEntities.get(settingMenuEntities.indexOf(softEntity)).setStatus(softStatus);
        }
        Log.d(TAG, "initStatusBackground: packageName: " + this.getPackageName());
        if (WATER_PURIFIER_PACKAGE_NAME.equals(this.getPackageName())) {
            waterPurifierMenuStatus();
        }
        settingMenuAdapter.notifyDataSetChanged();
    }

    private void waterPurifierMenuStatus() {
        // 童锁是否是打开状态
        boolean isChildLockOn = WaterServiceFactory.getInstance().getWaterService().getChildLock();
        String lockStatus = isChildLockOn ? getResources().getString(R.string.menuitem_status_open) : getResources().getString(R.string.menuitem_status_close);
        lockEntity = new SettingMenuEntity();
        lockEntity.setRoutePath(ViomiRouterConstant.WATER_FRAGMENT_CHILD_LOCK);
        settingMenuEntities.get(settingMenuEntities.indexOf(lockEntity)).setStatus(lockStatus);
        //矿物质状态
        String mimeralLevelSting = WaterServiceFactory.getInstance().getWaterService().getMineralLevel();
        SettingMenuEntity mineralEntity = new SettingMenuEntity();
        mineralEntity.setRoutePath(ViomiRouterConstant.WATER_FRAGMENT_MINERAL);
        settingMenuEntities.get(settingMenuEntities.indexOf(mineralEntity)).setStatus(mimeralLevelSting);
    }

    private void initSettingFragment(String fragmentRouterName) {
        Log.i(TAG, "initSettingFragment: " + fragmentRouterName);
        Pair<Class<?>, Bundle> fragmentPair = ViomiRouter.getInstance().build(fragmentRouterName).getProviderPage();
        Log.i(TAG, "initView: fragmentRouterName: " + fragmentPair);
        Fragment targetFragment = (Fragment) RefInvoke.createObject(fragmentPair.first);
        Log.i(TAG, "initSettingContent: targetFragment : " + targetFragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.setting_content, targetFragment, targetFragment.getClass().getName())
                .commitAllowingStateLoss();
    }

    @Override
    public void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.settingCleanlog.setOnClickListener(view -> {
            Log.i(TAG, "initListener: clean log");
            VLogUtil.cleanLogFile();
        });
        settingMenuAdapter.setOnItemLongClickListener((parent, view, position, id) -> false);
        settingMenuAdapter.setOnItemClickListener((parent, view, position, id) -> {
            String routePath = settingMenuEntities.get(position).getRoutePath();
            Log.i(TAG, "onItemClick: routePath: " + routePath);
            if (isShowSettingContent) {
                Pair<Class<?>, Bundle> fragmentPair = ViomiRouter.getInstance().build(routePath).getProviderPage();
                Fragment targetFragment = (Fragment) RefInvoke.createObject(fragmentPair.first);
                Log.i(TAG, "initSettingContent: targetFragment: " + targetFragment);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.setting_content, targetFragment, targetFragment.getClass().getName()).commitAllowingStateLoss();
                settingMenuEntities.get(beforeIndex).setSelected(false);
                settingMenuEntities.get(position).setSelected(true);
                beforeIndex = position;
                settingMenuAdapter.notifyDataSetChanged();
                return;
            }
            Log.i(TAG, "onItemClick: ");
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_CONTAINER)
                    .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, routePath)
                    .navigation();
        });
        mCompositeDisposable = new CompositeDisposable();
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(ovenRxBusEvent -> {
            int msgId = ovenRxBusEvent.getMsgId();
            Object msgObj = ovenRxBusEvent.getMsgObject();
            Log.i(TAG, "accept: msgId: " + msgId);
            if (msgId == ModuleSetingEventConstant.MSG_VIOMI_LOGOUT || msgId == ModuleSetingEventConstant.MSG_VIOMI_LOGIN) {
                refreshUserInfo((UserInfoDb) ovenRxBusEvent.getMsgObject());
                return;
            }
            if (msgId == CommonConstant.MSG_CHILDLOCK_SWITCH) {
                boolean isSwitch = (boolean) msgObj;
                String lockStatus = isSwitch ? getResources().getString(R.string.menuitem_status_open) : getResources().getString(R.string.menuitem_status_close);
                settingMenuEntities.get(settingMenuEntities.indexOf(lockEntity)).setStatus(lockStatus);
                settingMenuAdapter.notifyDataSetChanged();
            } else if (msgId == ModuleSetingEventConstant.MSG_WIFI_SWITCH_CHANGE) {
                boolean isWifiEnable = (boolean) msgObj;
                Log.i(TAG, "initListener: isWifeEnable: " + isWifiEnable);
                String wifiStatus = isWifiEnable ? getResources().getString(R.string.menuitem_status_open) : getResources().getString(R.string.menuitem_status_close);
                Log.i(TAG, "initListener: wifiStatus: "+wifiStatus);
                settingMenuEntities.get(settingMenuEntities.indexOf(wifiEntity)).setStatus(wifiStatus);
                settingMenuAdapter.notifyDataSetChanged();
            } else if (msgId == ModuleSetingEventConstant.MSG_WIFI_SWITCH_CHANGE) {
                boolean isWifiEnable = (boolean) msgObj;
                Log.i(TAG, "initListener: isWifeEnable: " + isWifiEnable);
                String wifiStatus = isWifiEnable ? getResources().getString(R.string.menuitem_status_open) : getResources().getString(R.string.menuitem_status_close);
                Log.i(TAG, "initListener: wifiStatus: "+wifiStatus);
                settingMenuEntities.get(settingMenuEntities.indexOf(wifiEntity)).setStatus(wifiStatus);
                settingMenuAdapter.notifyDataSetChanged();
            }else if (msgId == ModuleSetingEventConstant.MSG_SOFT_AP_CHANGED) {
                boolean isSofeOpen = (boolean) msgObj;
                Log.i(TAG, "initListener: isWifeEnable: " + isSofeOpen);
                String wifiStatus = isSofeOpen ? getResources().getString(R.string.menuitem_status_open) : getResources().getString(R.string.menuitem_status_close);
                Log.i(TAG, "initListener: wifiStatus: "+wifiStatus);
                settingMenuEntities.get(settingMenuEntities.indexOf(softEntity)).setStatus(wifiStatus);
                settingMenuAdapter.notifyDataSetChanged();
            }
        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume ");
        initStatusBackground();
    }

    public void refreshUserInfo(UserInfoDb userInfoDb) {
        Log.i(TAG, "refreshUserInfo: userInfoDb");
        if (userInfoDb == null) {
            return;
        }
        Log.i(TAG, "refreshUserInfo: " + userInfoDb.getNickname() + "  " + userInfoDb.getHeadImgUrl());
        settingMenuEntities.get(HEAD_INDEX).setName(userInfoDb.getNickname());
        settingMenuEntities.get(HEAD_INDEX).setIconName(userInfoDb.getHeadImgUrl());
        settingMenuAdapter.updateDate(settingMenuEntities);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
        // 为了避免内存中的数据 变化，导致界面错乱,界面虽然销毁了，但是 menuEnity 还在内存中
        settingMenuEntities.get(beforeIndex).setSelected(false);
    }

}

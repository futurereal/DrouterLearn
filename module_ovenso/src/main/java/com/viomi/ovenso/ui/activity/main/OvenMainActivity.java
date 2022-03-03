package com.viomi.ovenso.ui.activity.main;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.OvenSoService;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityMainOvenBinding;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.ui.activity.ModeListActivity;
import com.viomi.ovenso.util.preference.OvenPreference;
import com.viomi.ovensocommon.BasePresenterActivity;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.utils.ApplicationManager;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ljh on 2020/11/10.
 * Description:首页
 */
@Route(path = ViomiRouterConstant.OVENSO_MAIN)
public class OvenMainActivity extends BasePresenterActivity<ActivityMainOvenBinding, MainPresenter> implements MainContract.View, View.OnClickListener {
    private static final String TAG = "OvenMainActivity";
    private boolean isSoSeven;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_oven;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        viewDataBinding.ovenmainWifistatus.setVisibility(View.VISIBLE);
        // WIFI 网络是否连接
        boolean isNetConnect = NetworkUtils.isConnected();
        Log.i(TAG, "initView:isNetConnect:  " + isNetConnect);
        refreshNetState(isNetConnect);
        // 更新主题的设置
        refreshTheme();
        isSoSeven = TextUtils.equals(getPackageName(), ApplicationManager.OVENSO_PACKAGE);
        Log.i(TAG, "initView: isSoSeven： " + isSoSeven);
        if (isSoSeven) {
            viewDataBinding.ovenmainPannelCamera.setImageResource(R.drawable.main_camera);
        } else {
            viewDataBinding.ovenmainPannelCamera.setImageResource(R.drawable.basetitle_pannel_selector);
        }
        viewDataBinding.ovenmainTestview.showBuildInfo(isSoSeven);
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.ovenmainWifistatus.setOnClickListener(this);
        viewDataBinding.ovenmainMessage.setOnClickListener(this);
        viewDataBinding.ovenmainLight.setOnClickListener(this);
        viewDataBinding.ovenmainPannelCamera.setOnClickListener(this);
        viewDataBinding.menuCook.setOnClickListener(this);
        viewDataBinding.menuAssistant.setOnClickListener(this);
        viewDataBinding.menuMyFood.setOnClickListener(this);
        viewDataBinding.menuTuijian.setOnClickListener(this);
        viewDataBinding.menuSet.setOnClickListener(this);
    }

    private void stopOVenCook() {
        Log.i(TAG, "stopOVenCook: ");
        // 获取烹饪状态并且发送一个结束指令
        new CompositeDisposable().add(Observable.timer(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(aLong -> {
                    // 解决 在烹饪或者预约过程中， 屏端闪退，回到主界面 无法进入烹饪的问题
                    // 获取烹饪状态
                    int workStatus = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.WORK_STATUS.siid, OvenPropEnum.WORK_STATUS.piid, 0);
                    Log.i(TAG, "stopOVenCook: workStatus: " + workStatus);
                    if (workStatus == OvenWorkStatusEnum.WORKING.value) {
                        OvenSerialManager.getInstance().doStandardAction(OvenActionEnum.ACTION_OVER);
                    } else if (workStatus == OvenWorkStatusEnum.BOOKED.value) {
                        OvenSerialManager.getInstance().doOtherCustomAction(OvenActionEnum.ACTION_CANCLE_PREPARE, true, null);
                    }
                }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initData() {
        Log.i(TAG, "initData: ");
        //由于 和 moduleSetting applicaiton 初始化先后 问题，暂时放在main 里面
        OvensoServiceFactory.getInstance().setOvenService(new OvenSoService());
        ModuleSettingServiceFactory.getInstance().getViotService().setMenuArrayId(R.array.menu_ovenso);
        ModuleSettingServiceFactory.getInstance().getViotService().setScreenOffArrayId(R.array.screenofftime_ovenso);
        ModuleSettingServiceFactory.getInstance().getViotService().setAgingRoutPath(ViomiRouterConstant.OVENSO_AGING);
        ModuleSettingServiceFactory.getInstance().getViotService().setShowMiotBind(true);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick");
        if (isRepeatedClick(v.getId())) {
            return;
        }
        int id = v.getId();
        if (id == R.id.ovenmain_wifistatus) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_COMMON_SETTING)
                    .withInt(ViomiRouterConstant.SETTING_KEY_MENUINDEX, CommonConstant.MENU_INDEX_WIFI)
                    .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_WLAN)
                    .navigation();
        } else if (id == R.id.ovenmain_message) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MESSAGE_LIST).navigation();
        } else if (id == R.id.ovenmain_Light) {
            basePresenter.cmdLight(!v.isSelected());
        } else if (id == R.id.ovenmain_pannel_camera) {
            if (isSoSeven) {
                ViomiRouter.getInstance().build(ViomiRouterConstant.OVENS0_CAMERA)
                        .withBoolean(ViomiRouterConstant.CAMERA_KEY_COOKING, false)
                        .withString(ViomiRouterConstant.CAMERA_KEY_MODEID, String.valueOf(0))
                        .navigation();
            } else {
                basePresenter.cmdPannel(!v.isSelected());
            }
        } else if (id == R.id.menuCook) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MODE_LIST)
                    .withString(ModeListActivity.MODE_KEY, ModeListActivity.MODE_COOK)
                    .navigation();
        } else if (id == R.id.menuAssistant) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MODE_LIST)
                    .withString(ModeListActivity.MODE_KEY, ModeListActivity.MODE_ASSISTANT)
                    .navigation();
        } else if (id == R.id.menuTuijian) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_RECIPE_LIST).navigation();
        } else if (id == R.id.menuMyFood) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MY_RECIPE).navigation();
        } else if (id == R.id.menuSet) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_COMMON_SETTING)
                    .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_ACCOUNT)
                    .navigation();
        }
    }

    @Override
    public void refreshLight(boolean on) {
        viewDataBinding.ovenmainLight.setSelected(on);
    }

    @Override
    public void refreshPannel(boolean on) {
        viewDataBinding.ovenmainPannelCamera.setSelected(on);
    }

    @Override
    public void refreshNetState(boolean isWifiConnect) {
        Log.i(TAG, "refreshWifi: " + isWifiConnect);
        viewDataBinding.ovenmainWifistatus.setSelected(isWifiConnect);
    }

    @Override
    public void refreshTheme() {
        String themeName = OvenPreference.getInstance().getString(OvenConstants.KEY_THEME_OVENSO, OvenConstants.THEME_NAME_DEFAULT);
        viewDataBinding.getRoot().setBackgroundResource(ResourceUtils.getDrawableIdByName(themeName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopOVenCook();
        Log.i(TAG, "onResume: ");
    }

    @Override
    public void updatePropertyView() {
        if (!CommonConstant.SHOW_DEBUG_INFO) {
            viewDataBinding.ovenmainTestview.setVisibility(View.GONE);
            return;
        }
        viewDataBinding.ovenmainTestview.setVisibility(View.VISIBLE);
        viewDataBinding.ovenmainTestview.updateProperty();
    }

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
    }
}


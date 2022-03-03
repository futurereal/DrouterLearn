package com.viomi.ovenso.common;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityBaseTitleBinding;
import com.viomi.ovenso.microwave.databinding.ViewstubBasetitleBinding;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.ui.activity.MessageListActivity;
import com.viomi.ovenso.ui.activity.OvenSoCameraActivity;
import com.viomi.ovenso.util.preference.OvenPreference;
import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ModelUtil;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.ovensocommon.utils.FragmentUtils;
import com.viomi.router.core.ViomiRouter;

import io.reactivex.disposables.Disposable;

/**
 * Describe:左侧标题栏(时间、返回按钮、标题、侧边栏菜单悬浮)
 **/
public abstract class BaseTitleActivity<VDB extends ViewDataBinding> extends BaseActivity<ActivityBaseTitleBinding> {
    private static final String TAG = "BaseTitleActivity";
    private ViewstubBasetitleBinding viewStubDataBinding;
    protected VDB childViewBinding;
    private View viewStubView;
    OnClickListener baseTitleListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: view : " + view.getClass().getSimpleName());
            if (isRepeatedClick(view.getId())) {
                return;
            }
            int clickViewId = view.getId();
            FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
            String topActivityName = fragmentActivity.getClass().getName();
            Log.i(TAG, "onClick: topActivityName: " + topActivityName);
            if (clickViewId == R.id.basetitle_back) {
                Activity topActivity = ActivityUtils.getTopActivity();
                topActivity.finish();
            } else if (clickViewId == R.id.basetitle_show_menu) {
                showViewStubMenu();
            } else if (clickViewId == R.id.basetitle_wifi) {
                ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_COMMON_SETTING)
                        .withInt(ViomiRouterConstant.SETTING_KEY_MENUINDEX, CommonConstant.MENU_INDEX_WIFI)
                        .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_WLAN)
                        .navigation();
            } else if (clickViewId == R.id.basetitle_message) {
                if (TextUtils.equals(topActivityName, MessageListActivity.class.getName())) {
                    ViomiToastUtil.showToastCenter(getString(R.string.messagelist_currenttip));
                    return;
                }
                ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MESSAGE_LIST)
                        .navigation();
            } else if (clickViewId == R.id.basetitle_pannel_camera) {
                if (TextUtils.equals(topActivityName, OvenSoCameraActivity.class.getName())) {
                    // 相机预览界面fragment是window 所以toast 也需要window  闪退
//                    ViomiToastUtil.showWindowToast(getString(R.string.camera_tip));
                    Log.i(TAG, "onClick: noneed luanch  camera");
                    return;
                }
                ViomiRouter.getInstance().build(ViomiRouterConstant.OVENS0_CAMERA)
                        .withBoolean(ViomiRouterConstant.CAMERA_KEY_COOKING, false)
                        .withString(ViomiRouterConstant.CAMERA_KEY_MODEID, String.valueOf(0))
                        .navigation();
            } else if (clickViewId == R.id.basetitle_Light) {
                boolean isLightOn = !view.isSelected();
                OvenSerialManager.getInstance().setLightProperty(isLightOn);
            } else if (clickViewId == R.id.basetitle_close) {
                viewStubView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_base_title;
    }

    @Override
    protected void initView() {
        int childContentViewId = getChildContentViewId();
        Log.i(TAG, "initView:childContentViewId :  " + childContentViewId);
        //添加主内容布局
        if (childContentViewId != View.NO_ID) {
            childViewBinding = DataBindingUtil.inflate(getLayoutInflater(), childContentViewId, null, false);
            View childView = childViewBinding.getRoot();
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            childView.setLayoutParams(layoutParams);
            Log.i(TAG, "initView: addChildView  " + childView);
            // 把布局加到容器里面
            viewDataBinding.basetitleContainer.addView(childView);
        } else { // 使用fragment 来处理
            String targetFragmentRouter = ViomiRouterConstant.CAMERA_FRAGMENT;
            FragmentUtils.loadFragment(R.id.basetitle_container, targetFragmentRouter);
        }
        initIntentData();
        initChildUi();
        initTitle();
        boolean isShowTheme = showTheme();
        Log.i(TAG, "initView: isShowTheme: " + isShowTheme);
        if (isShowTheme) {
            String themeName = OvenPreference.getInstance().getString(OvenConstants.KEY_THEME_OVENSO, OvenConstants.THEME_NAME_DEFAULT);
            viewDataBinding.getRoot().setBackgroundResource(ResourceUtils.getDrawableIdByName(themeName));
        }
        initTitleListener();
    }

    protected boolean showTheme() {
        return true;
    }

    protected void initIntentData() {

    }

    private void initTitle() {
        String titleName = getTitleName();
        if (!TextUtils.isEmpty(titleName)) {
            viewDataBinding.basetitleTitle.setText(titleName);
        }
    }

    protected void initTitleListener() {
        Log.i(TAG, "initTitleListener: ");
        viewDataBinding.basetitleBack.setOnClickListener(baseTitleListener);
        viewDataBinding.basetitleShowMenu.setOnClickListener(baseTitleListener);
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Object msgObj = viomiRxBusEvent.getMsgObject();
            switch (msgId) {
                case CommonConstant.MSG_WIFI_STATUS_CHANGE:
                    // 判断有没有被初始化
                    if (viewStubDataBinding == null) {
                        return;
                    }
                    boolean isNetConnect = (boolean) msgObj;
                    Log.i(TAG, "initData: isNetConnect: " + isNetConnect);
                    viewStubDataBinding.basetitleWifi.setSelected(isNetConnect);
                    break;
                case OvenBusEventConstants.MSG_UPDATE_LIGHT:
                    // 判断有没有被初始化
                    if (viewStubDataBinding == null) {
                        return;
                    }
                    boolean isLightOn = (boolean) msgObj;
                    viewStubDataBinding.basetitleLight.setSelected(isLightOn);
                    break;
            }
        });
        addDisposable(disposable);
    }

    protected abstract int getChildContentViewId();

    protected abstract void initChildUi();

    protected abstract String getTitleName();

    private void showViewStubMenu() {
        if (viewStubView != null) {
            viewStubView.setVisibility(View.VISIBLE);
            return;
        }
        viewDataBinding.basetitleMenu.getViewStub().setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                viewStubDataBinding = DataBindingUtil.bind(inflated);
                viewStubDataBinding.basetitleWifi.setOnClickListener(baseTitleListener);
                viewStubDataBinding.basetitleMessage.setOnClickListener(baseTitleListener);
                viewStubDataBinding.basetitleLight.setOnClickListener(baseTitleListener);
                viewStubDataBinding.basetitlePannelCamera.setOnClickListener(baseTitleListener);
                viewStubDataBinding.basetitleClose.setOnClickListener(baseTitleListener);
                String modelName = ModelUtil.getModelName();
                boolean isSoSeven = TextUtils.equals(modelName, ModelUtil.DEVICE_MODEL_OVENSO7);
                if (isSoSeven) {
                    viewStubDataBinding.basetitlePannelCamera.setImageResource(R.drawable.basetitle_camera);
                } else {
                    viewStubDataBinding.basetitlePannelCamera.setImageResource(R.drawable.basetitle_pannel_selector);
                }
                // wifi 的状态
                viewStubDataBinding.basetitleWifi.setSelected(NetworkUtils.isWifiConnected());
                boolean isLightOn = (boolean) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.LIGHT.siid, OvenPropEnum.LIGHT.piid, false);
                Log.i(TAG, "onInflate: isLightOn： " + isLightOn);
                viewStubDataBinding.basetitleLight.setSelected(isLightOn);
            }
        });
        viewStubView = viewDataBinding.basetitleMenu.getViewStub().inflate();
    }

    protected void setImgBackVisibility(int visibility) {
        viewDataBinding.basetitleBack.setVisibility(visibility);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

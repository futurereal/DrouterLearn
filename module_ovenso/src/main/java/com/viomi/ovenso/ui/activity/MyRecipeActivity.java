package com.viomi.ovenso.ui.activity;

import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import androidx.databinding.DataBindingUtil;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovenso.custommode.CustomeModeUtils;
import com.viomi.ovenso.helper.ModesHelper;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityMyRecipeBinding;
import com.viomi.ovenso.microwave.databinding.LayoutNotLoginBinding;
import com.viomi.ovenso.ui.adapter.MyRecipeAdapter;
import com.viomi.ovenso.util.Base64Util;
import com.viomi.ovenso.util.ItemDecorationUtil;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.db.CookParamEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

/**
 * Created by Ljh on 2020/11/12.
 * Description:我的菜谱
 * 包含 八个自定义菜谱 和 八个自定义模式
 */
@Route(path = ViomiRouterConstant.OVENSO_MY_RECIPE)
public class MyRecipeActivity extends BaseTitleActivity<ActivityMyRecipeBinding> {
    private static final String TAG = "MyRecipeActivity";
    MyRecipeAdapter myRecipeAdapter;
    private View llNotLogin;
    private View llNoRecipe;
    private final ArrayList<ModeTypeEntity> allCustomList = new ArrayList<>(16);

    @Override
    protected int getChildContentViewId() {
        return R.layout.activity_my_recipe;
    }

    @Override
    protected void initChildUi() {
        Log.i(TAG, "initUi: ");
        myRecipeAdapter = new MyRecipeAdapter();
        childViewBinding.myrecipeList.setAdapter(myRecipeAdapter);
        childViewBinding.myrecipeList.addItemDecoration(ItemDecorationUtil.linearHorDecor(ApplicationUtils.getContext(),
                24));
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.ovenso_my_recipe);
    }

    @Override
    protected void initListener() {
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(busEvent -> {
            switch (busEvent.getMsgId()) {
                case OvenBusEventConstants.MSG_MIOT_LOGIN://米家登录成功
                case OvenBusEventConstants.MSG_VIOMI_LOGIN://云米登录成功
                    updateNotLoginView(false);
                    updateReipeList();
                    break;
                case OvenBusEventConstants.MSG_UPDATE_RECIPE:
                    updateReipeList();
                    break;
                default:
                    break;
            }
        });
        addDisposable(disposable);
        myRecipeAdapter.setOnItemClickListener((parent, view, position, id) -> {
            ModeTypeEntity modeTypeEntity = allCustomList.get(position);
            String modeName = Base64Util.decode(modeTypeEntity.getName());
            ArrayList<CookParamEntity> cookParamEntities = modeTypeEntity.getCookParamEntityList();
            ArrayList<ModeTypeEntity> childModeTypeEntityList = new ArrayList<>();
            for (CookParamEntity cookParamEntity : cookParamEntities) {
                ModeTypeEntity childModeEntity = ModesHelper.getModeEntityById(cookParamEntity.getModeId());
                childModeEntity.getCookParamEntityList().get(0).setDefineFirepower(cookParamEntity.getDefineFirepower());
                childModeEntity.getCookParamEntityList().get(0).setDefineTime(cookParamEntity.getDefineTime());
                childModeEntity.setModeType(modeTypeEntity.getModeType());
                childModeEntity.setModeId(modeTypeEntity.getModeId());
                childModeTypeEntityList.add(childModeEntity);
            }
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MODE_DETAIL)
                    .withString(ModeDetailActivity.KEY_MODE_NAME, modeName)
                    .withString(ModeDetailActivity.KEY_CUSTOM_TYPE, CustomeModeUtils.CUSTOM_NAME_MODE)
                    .withParcelableArrayList(ModeDetailActivity.KEY_MODETYPE_LIST, childModeTypeEntityList).navigation();
        });
    }

    @Override
    protected void initData() {
        // 监听云米的绑定状态
        Log.i(TAG, "initData: ");
        boolean isViomiBind = ModuleSettingServiceFactory.getInstance().getViotService().isDeviceBind();
        Log.i(TAG, "initUi: isNotLoginIot: " + isViomiBind);
        if (isViomiBind) {
            updateReipeList();
        } else {
            updateNotLoginView(true);
        }
    }

    /**
     * 显示没有登录的界面
     *
     * @param isShow 是否显示菜谱
     */
    private void updateNotLoginView(boolean isShow) {
        Log.i(TAG, "updateNotLoginView: isShow : " + isShow);
        if (llNotLogin == null) {
            ViewStub noLoginViewStub = childViewBinding.myrecipeStubNotLogin.getViewStub();
            noLoginViewStub.setOnInflateListener((stub, inflated) -> {
                LayoutNotLoginBinding notLoginBinding = DataBindingUtil.bind(inflated);
                notLoginBinding.tvToLogin.setOnClickListener(view -> {
                    ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_COMMON_SETTING)
                            .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_ACCOUNT)
                            .navigation();
                });
            });
            llNotLogin = noLoginViewStub.inflate();
        }
        int visibleCode = isShow ? View.VISIBLE : View.GONE;
        llNotLogin.setVisibility(visibleCode);
    }

    /**
     * 显示菜谱 页面
     */
    private void updateReipeList() {
        Log.i(TAG, "updateReipeList: " + allCustomList.size());
        // 为了避免多次回调，导致数据比较多的问题
        allCustomList.clear();
        ArrayList<ModeTypeEntity> customRecipeList = CustomeModeUtils.getEntitiesByType(CustomeModeUtils.CUSTOM_NAME_RECIPE, CustomeModeUtils.CUSTOME_RECIPE_COUNT);
        if (customRecipeList.size() == 0) {
//            customRecipeList = CustomeModeUtils.testCustomeRecipe();
        }
        ArrayList<ModeTypeEntity> customModeList = CustomeModeUtils.getEntitiesByType(CustomeModeUtils.CUSTOM_NAME_MODE, CustomeModeUtils.CUSTOME_RECIPE_COUNT);
        if (customModeList.size() == 0) {
//            customModeList = CustomeModeUtils.testCustomeMode();
        }
        allCustomList.addAll(customRecipeList);
        allCustomList.addAll(customModeList);
        int modeTypeListSize = allCustomList.size();
        Log.i(TAG, "updateReipeList: modeTypeListSize: " + modeTypeListSize);
        if (llNoRecipe == null) {
            ViewStub noRecipeViewStub = childViewBinding.myrecipeStubNoRecipe.getViewStub();
            llNoRecipe = noRecipeViewStub.inflate();
        }
        if (modeTypeListSize == 0) {
            llNoRecipe.setVisibility(View.VISIBLE);
            childViewBinding.myrecipeList.setVisibility(View.GONE);
        } else {
            llNoRecipe.setVisibility(View.GONE);
            childViewBinding.myrecipeList.setVisibility(View.VISIBLE);
            myRecipeAdapter.setRecipeModeList(allCustomList);
        }
    }
}


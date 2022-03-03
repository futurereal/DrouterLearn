package com.viomi.ovenso.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovenso.custommode.RecipeUtil;
import com.viomi.ovenso.helper.ModesHelper;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityRecipeDetailBinding;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.ui.activity.running.CookRunningActivity;
import com.viomi.ovenso.util.OvenTestUtil;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovenso.view.ScrollListenView;
import com.viomi.ovensocommon.CommonAffirmFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.db.CookParamEntity;
import com.viomi.ovensocommon.db.RecommendRecipe;
import com.viomi.ovensocommon.db.RecommendRecipeDao;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Ljh on 2020/11/12.
 * Description:菜谱详情
 */
@Route(path = ViomiRouterConstant.OVENSO_RECIPE_DETAIL)
public class RecipeDetailActivity extends BaseTitleActivity<ActivityRecipeDetailBinding> {
    private static final String TAG = "RecipeDetailActivity";
    // 当前菜谱
    RecommendRecipe currentRecommendRecipe;
    private RecommendRecipeDao recommendRecipeDao;
    private boolean isStartCook = false;

    @Override
    protected int getChildContentViewId() {
        return R.layout.activity_recipe_detail;
    }

    @Override
    protected void initIntentData() {
        super.initIntentData();
        currentRecommendRecipe = getIntent().getParcelableExtra(ViomiRouterConstant.KEY_PARCEABLE);
    }

    @Override
    protected String getTitleName() {
        return currentRecommendRecipe.getRecipeName();
    }

    @Override
    protected void initChildUi() {
        // 预置菜谱
        childViewBinding.recipedetailTvcontent.setText(currentRecommendRecipe.getMaterial());
        int imgResourceId = ApplicationUtils.getContext().getResources().getIdentifier("dish_id_" + currentRecommendRecipe.getRecipeId(), "drawable",
                ApplicationUtils.getContext().getPackageName());
        childViewBinding.recipedetailRecipephoto.setImageResource(imgResourceId);
        // 初始化 的必须有监听的方法
        childViewBinding.recipedetailGroupcontent.setOnScrollChangeListener(new ScrollListenView.OnScrollChangeListener() {
            @Override
            public void onScrollToStart() {
                childViewBinding.topShader.setVisibility(View.GONE);
            }

            @Override
            public void onScrollToEnd() {
                childViewBinding.bottomShader.setVisibility(View.GONE);
            }

            @Override
            public void onScroll() {
            }

            @Override
            public void canScroll(boolean can) {
            }
        });
        childViewBinding.recipedetailGroupcontent.initLayoutListener();
    }

    @Override
    protected void initListener() {
        childViewBinding.recipedetailGrouptitle.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.recipedetail_materialtitle) {
                childViewBinding.recipedetailTvcontent.setText(currentRecommendRecipe.getMaterial());
                childViewBinding.recipedetailMaterialtitle.setTextSize((32));
                childViewBinding.recipedetailMethodtitle.setTextSize((28));
            } else if (checkedId == R.id.recipedetail_methodtitle) {
                childViewBinding.recipedetailTvcontent.setText(currentRecommendRecipe.getCookMethod());
                childViewBinding.recipedetailMethodtitle.setTextSize((28));
                childViewBinding.recipedetailMaterialtitle.setTextSize((32));
            }
            Log.d(TAG, "setOnCheckedChangeListener");
            childViewBinding.recipedetailGroupcontent.scrollTo(0, 0);
            childViewBinding.topShader.setVisibility(View.GONE);
            childViewBinding.bottomShader.setVisibility(View.GONE);
            childViewBinding.recipedetailGroupcontent.canScrollCallback();
        });
        childViewBinding.includeStart.getRoot().setOnClickListener(v -> {
            if (currentRecommendRecipe.isPreHeating()) {
                showPreHeartDialog(false);
                return;
            }
            startCook();
        });
        childViewBinding.includeAppoint.getRoot().setOnClickListener(v -> {
            if (currentRecommendRecipe.isPreHeating()) {
                showPreHeartDialog(true);
                return;
            }
            showAppointFragment();
        });
        // 收藏和取消收藏的逻辑
        childViewBinding.includeCollect.getRoot().setOnClickListener(v -> {
            String collectTipStr = childViewBinding.includeCollect.collectTip.getText().toString();
            Log.i(TAG, "onClick:collectTipStr: " + collectTipStr);
            boolean isCollect;
            int collectTipId;
            // 收藏
            if (TextUtils.equals(collectTipStr, getString(R.string.oven_collect))) {
                isCollect = true;
                collectTipId = R.string.oven_collected;
            } else {
                isCollect = false;
                collectTipId = R.string.oven_collect;
            }
            Log.i(TAG, "onClick: " + isCollect);
            RecipeUtil.getRecommendRecipeById(currentRecommendRecipe.getRecipeId()).setCollected(isCollect);
            childViewBinding.includeCollect.collectTip.setText(collectTipId);
            childViewBinding.includeCollect.collectIcon.setSelected(isCollect);
            currentRecommendRecipe.setCollected(isCollect);
            recommendRecipeDao.update(currentRecommendRecipe);
        });

        // 监听启动
        Disposable customeModeDisposable = ViomiRxBus.getInstance().subscribeUi(busEvent -> {
            switch (busEvent.getMsgId()) {
                case CommonConstant.MSG_DOWNWRITE_SUCCESS:
                    if (!isStartCook) {
                        Log.i(TAG, "initListener: isBook");
                        return;
                    }
                    OvenSerialManager.getInstance().doStandardAction(OvenActionEnum.ACTION_START);
                    isStartCook = false;
                    break;
                case OvenBusEventConstants.MSG_COOK_STATUSCHANGE:
                    if (!isActivityResumed) {
                        Log.i(TAG, "initListener: isActivityResume false");
                        return;
                    }
                    int statusEnumValue = (int) busEvent.getMsgObject();
                    if (statusEnumValue != OvenWorkStatusEnum.WORKING.value && statusEnumValue != OvenWorkStatusEnum.BOOKED.value) {
                        Log.i(TAG, "initListener: not start");
                        return;
                    }
                    ModeTypeEntity modeTypeEntity = new ModeTypeEntity();
                    int modeId = currentRecommendRecipe.getModeId();
                    modeTypeEntity.setModeId(modeId);
                    modeTypeEntity.setResIdBg("dish_id_" + currentRecommendRecipe.getRecipeId());
                    CookParamEntity cookParamEntity = new CookParamEntity();
                    cookParamEntity.setModeId(modeId);
                    cookParamEntity.setDefineTime(currentRecommendRecipe.getCookTime());
                    cookParamEntity.setDefineFirepower(currentRecommendRecipe.getCookFirepower());
                    cookParamEntity.setDefineFirepowerTwo(currentRecommendRecipe.getCookFirepowerTwo());
                    String modeTYpe = ModesHelper.getModeEntityById(modeId).getCookParamEntityList().get(0).getModeType();
                    cookParamEntity.setModeType(modeTYpe);
                    ArrayList<CookParamEntity> cookParamEntityArrayList = new ArrayList<>();
                    cookParamEntityArrayList.add(cookParamEntity);
                    modeTypeEntity.setCookParamEntityList(cookParamEntityArrayList);
                    ArrayList<ModeTypeEntity> modeTypeEntityArrayList = new ArrayList<>();
                    modeTypeEntityArrayList.add(modeTypeEntity);
                    Log.i(TAG, "initListener: startCookRunning " + statusEnumValue);
                    ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_COOK_RUNNING)
                            .withString(CookRunningActivity.KEY_RECIPENAME, currentRecommendRecipe.getRecipeName())
                            .withInt(CookRunningActivity.KEY_STAUSENUMS_VALUE, statusEnumValue)
                            .withParcelableArrayList(CookRunningActivity.KEY_MODETYPE_LIST, modeTypeEntityArrayList)
                            .navigation();
                    break;

            }
        });
        addDisposable(customeModeDisposable);
    }

    private void showAppointFragment() {
        List<PropertyEntity> propertyEntities = getPropertyEntities();
        float cookTime = currentRecommendRecipe.getCookTime();
        OvenUtil.showAppointFragment(cookTime, currentRecommendRecipe.getRecipeId(), propertyEntities);
    }

    @Override
    protected void initData() {
        Log.i(TAG, "initData: ");
        //读取菜谱是否收藏
        boolean isViotBind = ModuleSettingServiceFactory.getInstance().getViotService().isDeviceBind();
        Log.i(TAG, "initData: isViotBind: " + isViotBind);
        if (isViotBind) {
            childViewBinding.includeCollect.getRoot().setVisibility(View.VISIBLE);
            showCollect(currentRecommendRecipe.isCollected());
        } else {
            childViewBinding.includeCollect.getRoot().setVisibility(View.GONE);
        }
        recommendRecipeDao = ViomiRoomDatabase.getDatabase().recommendRecipeDao();
    }

    private void showCollect(boolean collected) {
        String collectStatus = collected ? getString(R.string.oven_collected) : getString(R.string.oven_collect);
        childViewBinding.includeCollect.collectTip.setText(collectStatus);
        childViewBinding.includeCollect.collectTip.getRootView().setSelected(collected);
    }

    private void showPreHeartDialog(boolean isAppoint) {
        Log.i(TAG, "showPreHeartDialog: ");
        CommonAffirmFragment dialog = new CommonAffirmFragment();
        dialog.setCancelable(false);
        Bundle bundle = CommonAffirmFragment.getBundle(getString(R.string.oven_preheart_tip), getString(R.string.oven_preheart_content), "", getString(R.string.ovenso_ok), false, 0);
        dialog.setArguments(bundle);
        dialog.setPositiveClickListener(dialog1 -> {
            if (isAppoint) {
                showAppointFragment();
            } else {
                startCook();
            }
            dialog1.dismiss();
        });
        dialog.show(getSupportFragmentManager(), "info");
    }

    private void startCook() {
        OvenTestUtil.testCookingUI(OvenWorkStatusEnum.WORKING);
        Log.i(TAG, "startCook");
        if (isStatusError()) {
            Log.i(TAG, "startCook: error return");
            return;
        }
        isStartCook = true;
        ViomiToastUtil.showToastNormal(getString(R.string.oven_cookparam_launching), Toast.LENGTH_SHORT);
        List<PropertyEntity> propertyEntities = getPropertyEntities();
        Log.w(TAG, "time__ setProp start  :");
        OvenSerialManager.getInstance().writePropertyList(propertyEntities);
        OvenTestUtil.testCookingUI(OvenWorkStatusEnum.WORKING);
    }

    @NonNull
    private List<PropertyEntity> getPropertyEntities() {
        List<PropertyEntity> propertyEntities = new ArrayList<>();
        //自定义菜谱
        PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, currentRecommendRecipe.getRecipeId());
        propertyEntities.add(propertyEntity);
        return propertyEntities;
    }

    /**
     * 状态是否有错误
     *
     * @return 错误信息
     */
    private boolean isStatusError() {
        int curTemp = (Integer) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.TEMPER.siid, OvenPropEnum.TEMPER.piid, 0);
        if ((curTemp - 50 > currentRecommendRecipe.getCookFirepower() && currentRecommendRecipe.getCookFirepower() != 0)
                || (curTemp - 50 > currentRecommendRecipe.getCookFirepower() && currentRecommendRecipe.getCookFirepower() != 0)) {
            Log.i(TAG, "isStatusError over temp too high");
            ViomiToastUtil.showToastNormal(getString(R.string.oven_cookparam_tmphigh), Toast.LENGTH_SHORT);
            return true;
        }
        return false;
    }
}


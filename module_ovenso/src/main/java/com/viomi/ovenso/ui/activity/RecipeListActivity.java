package com.viomi.ovenso.ui.activity;

import android.util.Log;

import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.common.BaseTitleActivity;
import com.viomi.ovenso.custommode.RecipeUtil;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityRecipeListBinding;
import com.viomi.ovenso.ui.adapter.RecipeListAdapter;
import com.viomi.ovenso.util.ItemDecorationUtil;
import com.viomi.ovenso.util.preference.OvenPreference;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.db.RecommendRecipe;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ljh on 2020/11/10.
 * Description:推荐菜谱
 */
@Route(path = ViomiRouterConstant.OVENSO_RECIPE_LIST)
public class RecipeListActivity extends BaseTitleActivity<ActivityRecipeListBinding> {
    private static final String TAG = "RecipeListActivity";
    private List<RecommendRecipe> mRecommendRecipeList;
    private RecipeListAdapter mRecipeListAdapter;

    @Override
    protected int getChildContentViewId() {
        return R.layout.activity_recipe_list;
    }

    @Override
    protected String getTitleName() {
        return getString(R.string.oven_recommand);
    }

    @Override
    protected void initChildUi() {
        mRecommendRecipeList = new ArrayList<>();
        mRecipeListAdapter = new RecipeListAdapter(mRecommendRecipeList);
        childViewBinding.recipeList.setAdapter(mRecipeListAdapter);
        childViewBinding.recipeList.addItemDecoration(ItemDecorationUtil.linearHorDecor(this, 24));
        mRecipeListAdapter.setOnItemClickListener((parent, view, position, id) -> {
            RecommendRecipe currentRecommendRecipe = mRecommendRecipeList.get(position);
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_RECIPE_DETAIL)
                    .withParcelable(ViomiRouterConstant.KEY_PARCEABLE, currentRecommendRecipe).navigation();
        });
    }

    @Override
    protected void initData() {
        mRecommendRecipeList = RecipeUtil.getAssetRecipes();
        Log.i(TAG, "initData: " + mRecommendRecipeList.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecipeListAdapter.update(mRecommendRecipeList);
    }
}


package com.viomi.ovenso.custommode;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenApplication;
import com.viomi.ovenso.util.FileUtil;
import com.viomi.ovenso.util.GsonUtil;
import com.viomi.ovensocommon.db.RecommendRecipe;
import com.viomi.ovensocommon.db.RecommendRecipeDao;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenPropEnum;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/11/19.
 * Description:菜谱工具类
 */
public class RecipeUtil {
    private static final String TAG = "RecipeUtil";
    //预置菜谱的设置
    private static final String DATA_FILE_NAME_SO6 = "DefaultDish.json";
    private static final String DATA_FILE_NAME_SO7 = "DefaultDishSo7.json";
    private static final String PACKAGE_SO6 = "com.viomi.ovenso";
    private static final String PACKAGE_SO7 = "com.viomi.ovenso.microwave";
    // 自定义菜谱的key
    private static final String KEY_RECIPE_CUSTOMER = "recipe_";
    //自定义菜谱的最大数量
    public static final int MAX_CUSTOME_RECIPE = 8;
    // 自定义菜谱的 开始索引
    public static final int CUSTOME_RECIPE_ID_BEGAIN = 1;
    // 表示空的自定义菜谱
    public static final String RECIPE_INFO_EMPTY = "null";

    private static List<RecommendRecipe> originRecommendRecipeList;
    private static RecommendRecipeDao recommendRecipeDao;

    /**
     * 获取推荐菜谱的名字
     * 1 、数据库里面取菜谱
     * 2 、数据库没有的话，从asset 文件里面解析菜谱并且存入数据库和缓存
     */
    public static List<RecommendRecipe> getAssetRecipes() {
        if (originRecommendRecipeList != null) {
            Log.i(TAG, "getAssetRecipes:  return recipe from memory");
            return originRecommendRecipeList;
        }
        recommendRecipeDao = ViomiRoomDatabase.getDatabase().recommendRecipeDao();
        originRecommendRecipeList = recommendRecipeDao.getAllRecommendRecipes();
        Log.i(TAG, "getAssetRecipes: " + originRecommendRecipeList);
        if (originRecommendRecipeList != null && originRecommendRecipeList.size() > 0) {
            Log.i(TAG, "getAssetRecipes: return recipe from db");
            return originRecommendRecipeList;
        }
        String packageName = ApplicationUtils.getContext().getPackageName();
        String recipeDataFileName = "";
        if (TextUtils.equals(packageName, PACKAGE_SO7)) {
            recipeDataFileName = DATA_FILE_NAME_SO7;
        }
        if (TextUtils.equals(packageName, PACKAGE_SO6)) {
            recipeDataFileName = DATA_FILE_NAME_SO6;
        }
        Log.i(TAG, "getAssetRecipes: " + recipeDataFileName);
        String recipeContentJson = FileUtil.getJsonString(OvenApplication.getContext(), recipeDataFileName);
        Type recipeType = new TypeToken<ArrayList<RecommendRecipe>>() {
        }.getType();
        originRecommendRecipeList = GsonUtil.fromJson(recipeContentJson, recipeType);
        Log.i(TAG, "getDefaultRecipes: originRecipeList: " + originRecommendRecipeList.size());
        recommendRecipeDao.insert(originRecommendRecipeList);
        return originRecommendRecipeList;
    }

    public static String getRecipeName() {
        int recipeId = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0);
        Log.i(TAG, "getRecipeNameById: " + recipeId);
        if (recommendRecipeDao == null) {
            recommendRecipeDao = ViomiRoomDatabase.getDatabase().recommendRecipeDao();
        }
        String recipeName = "";
        if (recipeId < 100) {
            RecommendRecipe recommendRecipe = recommendRecipeDao.getRecommendRecipeById(recipeId);
            if (recommendRecipe != null) {
                recipeName = recommendRecipe.getRecipeName();
            }
        } else {
            recipeName = CustomeModeUtils.getCustomeRecipeName(recipeId);
        }
        if (TextUtils.isEmpty(recipeName)) {
            recipeName = CustomeModeUtils.RECIPE_INFO_EMPTY;
        }
        Log.i(TAG, "getRecipeNameById: recipeName: " + recipeName);
        return recipeName;
    }

    public static RecommendRecipe getRecommendRecipeById(int recipeId) {
        List<RecommendRecipe> recommendRecipeList = getAssetRecipes();
        for (RecommendRecipe recommendRecipe : recommendRecipeList) {
            if (recommendRecipe.getRecipeId() == recipeId)
                return recommendRecipe;
        }
        return null;
    }

    public static void setRecipeListNull() {
        if (originRecommendRecipeList != null) {
            originRecommendRecipeList = null;
        }
    }

}


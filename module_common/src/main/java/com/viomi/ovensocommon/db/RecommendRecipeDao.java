package com.viomi.ovensocommon.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecommendRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<RecommendRecipe> recommendRecipeList);

    @Update
    void update(RecommendRecipe recommendRecipe);

    @Query("SELECT * FROM tb_recommend_recipe")
    List<RecommendRecipe> getAllRecommendRecipes();

    @Query("SELECT * FROM TB_RECOMMEND_RECIPE WHERE recipeId = :recipeId")
    RecommendRecipe getRecommendRecipeById(int recipeId);

    @Query("UPDATE TB_RECOMMEND_RECIPE SET collected = :collected")
    void updateAllCollected(boolean collected);
}

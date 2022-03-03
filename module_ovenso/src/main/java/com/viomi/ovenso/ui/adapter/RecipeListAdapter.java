package com.viomi.ovenso.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ItemRecipeListBinding;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;
import com.viomi.ovensocommon.db.RecommendRecipe;

import java.util.List;

/**
 * Created by Ljh on 2020/11/12
 * Description:
 */
public class RecipeListAdapter extends BaseRecyclerViewAdapter<RecipeListAdapter.Holder> {
    private static final String TAG = "RecipeListAdapter";
    private List<RecommendRecipe> recommendRecipeList;

    public RecipeListAdapter(List<RecommendRecipe> recommendRecipeList) {
        this.recommendRecipeList = recommendRecipeList;
    }

    public void update(List<RecommendRecipe> recommendRecipeList) {
        this.recommendRecipeList = recommendRecipeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeListBinding itemRecipeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_recipe_list, null, false);
        return new Holder(itemRecipeBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(recommendRecipeList.get(position));
    }

    @Override
    public int getItemCount() {
        return recommendRecipeList == null ? 0 : recommendRecipeList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private final ItemRecipeListBinding itemRecipeBinding;

        Holder(ItemRecipeListBinding itemRecipeBinding, RecipeListAdapter adapter) {
            super(itemRecipeBinding.getRoot());
            this.itemRecipeBinding = itemRecipeBinding;
            itemRecipeBinding.getRoot().setOnClickListener(v -> adapter.onItemHolderClick(this, 250));
        }

        private void bind(RecommendRecipe recommendRecipe) {
            if (recommendRecipe.getRecipeName().length() > 6) {
                StringBuilder stringBuilder = new StringBuilder(recommendRecipe.getRecipeName());
                itemRecipeBinding.recipeName.setText(stringBuilder.replace(5, recommendRecipe.getRecipeName().length(), "..."));
            } else {
                itemRecipeBinding.recipeName.setText(recommendRecipe.getRecipeName());
            }
            int imgResourceId = ApplicationUtils.getContext().getResources().getIdentifier("dish_id_" + recommendRecipe.getRecipeId(), "drawable",
                    ApplicationUtils.getContext().getPackageName());
            itemRecipeBinding.tvTmLength.setText(String.valueOf(recommendRecipe.getCookTime()));
            itemRecipeBinding.recipeBg.setActualImageResource(imgResourceId);
            itemRecipeBinding.tvCollect.setVisibility(recommendRecipe.isCollected() ? View.VISIBLE : View.GONE);
        }
    }
}

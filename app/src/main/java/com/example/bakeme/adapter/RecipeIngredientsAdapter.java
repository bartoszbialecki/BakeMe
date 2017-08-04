package com.example.bakeme.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakeme.R;
import com.example.bakeme.model.Ingredient;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.ViewHolder> {
    // region VARIABLES
    private List<Ingredient> mIngredients;
    private NumberFormat mQuantityFormatter = new DecimalFormat("#.##");
    // endregion

    // region CONSTRUCTORS
    public RecipeIngredientsAdapter() {
        mIngredients = new ArrayList<>();
    }
    // endregion

    // region PUBLIC METHODS
    public void addIngredients(List<Ingredient> ingredients) {
        if (mIngredients == null) {
            mIngredients = new ArrayList<>();
        }

        mIngredients.addAll(ingredients);
        notifyDataSetChanged();
    }
    // endregion

    // region ADAPTER METHODS

    @Override
    public RecipeIngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredients_content, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeIngredientsAdapter.ViewHolder holder, int position) {
        Ingredient ingredient = mIngredients.get(position);

        holder.ingredientNameTextView.setText(ingredient.getIngredient());
        holder.ingredientQuantityTextView.setText(mQuantityFormatter.format(ingredient.getQuantity()));
        holder.ingredientMeasureTextView.setText(ingredient.getMeasure());
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }
    // endregion

    // region VIEW HOLDER
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_name_text_view)
        TextView ingredientNameTextView;
        @BindView(R.id.ingredient_quantity_text_view)
        TextView ingredientQuantityTextView;
        @BindView(R.id.ingredient_measure_text_view)
        TextView ingredientMeasureTextView;

        ViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
    // endregion
}

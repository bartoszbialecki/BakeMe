package com.example.bakeme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bakeme.R;
import com.example.bakeme.model.Ingredient;
import com.example.bakeme.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {
    // region VARIABLES
    private List<Recipe> mRecipes;
    private OnItemClickListener mListener;
    // endregion

    // region CONSTRUCTORS
    public RecipeListAdapter() {
        mRecipes = new ArrayList<>();
    }
    // endregion

    // region LISTENERS
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // endregion

    // region GETTERS AND SETTERS
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    // endregion

    // region PUBLIC METHODS
    public void addRecipes(List<Recipe> recipes) {
        if (mRecipes == null) {
            mRecipes = new ArrayList<>();
        }

        mRecipes.addAll(recipes);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mRecipes != null) {
            mRecipes.clear();
            notifyDataSetChanged();
        }
    }

    public Recipe getRecipe(int position) {
        if (mRecipes != null) {
            if (position < mRecipes.size()) {
                return mRecipes.get(position);
            }
        }

        return null;
    }
    // endregion

    // region ADAPTER METHODS

    @Override
    public RecipeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_content, parent, false);

        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(RecipeListAdapter.ViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);

        Context context = holder.itemView.getContext();

        holder.recipeNameTextView.setText(recipe.getName());

        List<Ingredient> ingredients = recipe.getIngredients();
        int numberOfIngredients = 0;

        if (ingredients != null) {
            numberOfIngredients = ingredients.size();
        }

        holder.detailTextView.setText(context.getResources().getQuantityString(R.plurals.number_of_ingredients, numberOfIngredients, numberOfIngredients));
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
    // endregion

    // region VIEW HOLDER
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_name_text_view)
        TextView recipeNameTextView;
        @BindView(R.id.detail_text_view)
        TextView detailTextView;

        private OnItemClickListener mListener;

        ViewHolder(final View itemView, OnItemClickListener listener) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mListener = listener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }
    // endregion
}

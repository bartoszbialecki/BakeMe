package com.example.bakeme.data;

import com.example.bakeme.model.Recipe;
import com.example.bakeme.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class RecipesRepository implements Repository {
    // region VARIABLES
    private List<Recipe> mCachedRecipes;
    private boolean mLoading;
    // endregion

    // region CONSTGRUCTORS
    public RecipesRepository() {

    }
    // endregion

    // region REPOSITORY METHODS
    public Single<List<Recipe>> getRecipes(boolean forceLoad) {
        if (mCachedRecipes != null && !forceLoad) {
            return Single.just(mCachedRecipes);
        }

        if (mLoading) {
            return Single.just((List<Recipe>) new ArrayList<Recipe>());
        }

        mLoading = true;

        return ApiClient.getInstance().getRecipes()
                .doOnSuccess(new Consumer<List<Recipe>>() {
                    @Override
                    public void accept(@NonNull List<Recipe> recipes) throws Exception {
                        mLoading = false;

                        if (mCachedRecipes == null) {
                            mCachedRecipes = new ArrayList<>();
                        }

                        mCachedRecipes.addAll(recipes);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mLoading = false;
                    }
                });
    }

    public void refreshList() {
        mCachedRecipes = null;
    }

    public Recipe getRecipe(int recipeId) {
        if (mCachedRecipes != null) {
            for (Recipe recipe : mCachedRecipes) {
                if (recipe.getId() == recipeId) {
                    return recipe;
                }
            }
        }

        return null;
    }
    // endregion
}

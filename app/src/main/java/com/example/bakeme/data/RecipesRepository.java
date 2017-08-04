package com.example.bakeme.data;

import com.example.bakeme.model.Recipe;
import com.example.bakeme.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class RecipesRepository {
    // region VARIABLES
    private static RecipesRepository sInstance;
    private List<Recipe> mCahcedRecipes;
    private boolean mLoading;
    // endregion

    // region CONSTGRUCTORS
    public RecipesRepository() {

    }
    // endregion

    // region PUBLIC METHODS
    public static RecipesRepository getInstance() {
        if (sInstance == null) {
            sInstance = new RecipesRepository();
        }

        return sInstance;
    }

    public Single<List<Recipe>> getRecipes(boolean forceLoad) {
        if (mCahcedRecipes != null && !forceLoad) {
            return Single.just(mCahcedRecipes);
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

                        if (mCahcedRecipes == null) {
                            mCahcedRecipes = new ArrayList<>();
                        }

                        mCahcedRecipes.addAll(recipes);
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
        mCahcedRecipes = null;
    }

    public Recipe getRecipe(int recipeId) {
        if (mCahcedRecipes != null) {
            for (Recipe recipe : mCahcedRecipes) {
                if (recipe.getId() == recipeId) {
                    return recipe;
                }
            }
        }

        return null;
    }
    // endregion
}

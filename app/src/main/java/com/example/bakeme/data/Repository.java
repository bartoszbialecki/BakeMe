package com.example.bakeme.data;

import com.example.bakeme.model.Recipe;

import java.util.List;

import io.reactivex.Single;

public interface Repository {
    Single<List<Recipe>> getRecipes(boolean forceLoad);
    void refreshList();
    Recipe getRecipe(int recipeId);
}

package com.example.bakeme;

import com.example.bakeme.data.Repository;
import com.example.bakeme.model.Ingredient;
import com.example.bakeme.model.Recipe;
import com.example.bakeme.model.Step;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

public class MockRepository implements Repository {
    private List<Recipe> mRecipes = new ArrayList<>();

    @Override
    public Single<List<Recipe>> getRecipes(boolean forceLoad) {
        return Single.create(new SingleOnSubscribe<List<Recipe>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<List<Recipe>> e) throws Exception {
                if (!e.isDisposed()) {
                    List<Recipe> recipes = new ArrayList<>();
                    Recipe recipe = new Recipe();
                    recipe.setId(1);
                    recipe.setName("Nutella Pie");

                    List<Ingredient> ingredients = new ArrayList<>();
                    Ingredient ingredient = new Ingredient();
                    ingredient.setIngredient("Graham Cracker crumbs");
                    ingredient.setMeasure("CUP");
                    ingredient.setQuantity(2.0);
                    ingredients.add(ingredient);
                    recipe.setIngredients(ingredients);

                    List<Step> steps = new ArrayList<>();

                    Step step1 = new Step();
                    step1.setId(0);
                    step1.setDescription("Recipe Introduction");
                    step1.setShortDescription("Recipe Introduction");
                    step1.setVideoURL("https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4");
                    steps.add(step1);

                    Step step2 = new Step();
                    step2.setId(1);
                    step2.setDescription("1. Preheat the oven to 350°F. Butter a 9\" deep dish pie pan.");
                    step2.setShortDescription("Starting prep");
                    steps.add(step2);

                    recipe.setSteps(steps);

                    recipes.add(recipe);

                    mRecipes = recipes;

                    e.onSuccess(recipes);
                }
            }
        });
    }

    @Override
    public void refreshList() {

    }

    @Override
    public Recipe getRecipe(int recipeId) {
        if (mRecipes != null) {
            for (Recipe recipe : mRecipes) {
                if (recipe.getId() == recipeId) {
                    return recipe;
                }
            }
        }

        return null;
    }
}

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

/**
 * Created by Bartosz Bialecki on 08.08.2017.
 */

public class MockRepository implements Repository {
    private List<Recipe> mRecipes;

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
                    ingredient.setIngredient("cukier");
                    ingredient.setMeasure("lyzka");
                    ingredient.setQuantity(1.0);
                    ingredients.add(ingredient);
                    recipe.setIngredients(ingredients);

                    List<Step> steps = new ArrayList<>();
                    Step step = new Step();
                    step.setId(1);
                    step.setDescription("opis");
                    step.setShortDescription("krotki opis");
                    steps.add(step);
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

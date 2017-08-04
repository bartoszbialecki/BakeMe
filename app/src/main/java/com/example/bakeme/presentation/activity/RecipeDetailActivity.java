package com.example.bakeme.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.bakeme.R;
import com.example.bakeme.presentation.fragment.RecipeDetailFragment;
import com.example.bakeme.presentation.fragment.RecipeStepFragment;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnRecipeStepSelectedListener {
    // region VARIABLES
    private boolean mTwoPane = false;
    private int mRecipeId = -1;
    // endregion

    // region LIFE CYCLE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        setupActionBar();
        initArguments();

        if (savedInstanceState == null) {
            if (mRecipeId > -1) {
                RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(mRecipeId);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipe_detail_container, fragment)
                        .commit();
            }
        }

        if (findViewById(R.id.recipe_step_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                if (mRecipeId > -1) {
                    RecipeStepFragment fragment = RecipeStepFragment.newInstance(mRecipeId, -1);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_step_container, fragment)
                            .commit();
                }
            }
        } else {
            mTwoPane = false;
        }
    }
    // endregion

    // region MENU METHODS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, RecipeListActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region RECIPE STEP SELECTED LISTENER
    @Override
    public void onStepSelected(int recipeId, int stepId) {
        if (mTwoPane) {
            RecipeStepFragment fragment = RecipeStepFragment.newInstance(recipeId, stepId);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(RecipeStepFragment.ARG_RECIPE_ID, recipeId);
            intent.putExtra(RecipeStepFragment.ARG_STEP_ID, stepId);
            startActivity(intent);
        }
    }
    // endregion

    // region PRIVATE METHODS
    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initArguments() {
        Bundle extras = getIntent().getExtras();

        if (null != extras) {
            if (extras.containsKey(RecipeDetailFragment.ARG_RECIPE_ID)) {
                mRecipeId = extras.getInt(RecipeDetailFragment.ARG_RECIPE_ID, 0);
            }
        }
    }
    // endregion
}

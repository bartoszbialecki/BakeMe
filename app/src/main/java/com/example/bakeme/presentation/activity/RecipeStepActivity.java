package com.example.bakeme.presentation.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.bakeme.R;
import com.example.bakeme.presentation.fragment.RecipeStepFragment;

public class RecipeStepActivity extends AppCompatActivity {
    // region VARIABLES
    private int mRecipeId = -1;
    private int mStepId = -1;
    // endregion

    // region LIFE CYCLE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_step);

        initArguments();

        if (savedInstanceState == null) {
            if (mRecipeId > -1 && mStepId > -1) {
                RecipeStepFragment fragment = RecipeStepFragment.newInstance(mRecipeId, mStepId);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipe_step_container, fragment)
                        .commit();
            }
        }
    }
    // endregion

    // region MENU METHODS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region PRIVATE METHODS
    private void initArguments() {
        Bundle extras = getIntent().getExtras();

        if (null != extras) {
            if (extras.containsKey(RecipeStepFragment.ARG_RECIPE_ID)) {
                mRecipeId = extras.getInt(RecipeStepFragment.ARG_RECIPE_ID, -1);
            }

            if (extras.containsKey(RecipeStepFragment.ARG_STEP_ID)) {
                mStepId = extras.getInt(RecipeStepFragment.ARG_STEP_ID, -1);
            }
        }
    }
    // endregion
}

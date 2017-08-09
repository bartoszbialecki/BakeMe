package com.example.bakeme;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.bakeme.app.App;
import com.example.bakeme.presentation.activity.RecipeDetailActivity;
import com.example.bakeme.presentation.activity.RecipeListActivity;
import com.example.bakeme.presentation.fragment.RecipeDetailFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {
    private static final String PACKAGE_NAME = "com.example.bakeme";

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityRule = new ActivityTestRule<>(
            RecipeListActivity.class, true, false);

    @Rule
    public IntentsTestRule<RecipeDetailActivity> mIntentsRule =
            new IntentsTestRule<>(RecipeDetailActivity.class);

    @Test
    public void onClickRecipe_openRecipeDetails() {
        App app = (App)getAppContext().getApplicationContext();
        app.setRepository(new MockRepository());

        mActivityRule.launchActivity(new Intent());

        onView(withRecyclerView(R.id.recipe_list).atPosition(0))
                .check(matches(hasDescendant(withText("Nutella Pie"))));

        onView(withRecyclerView(R.id.recipe_list).atPosition(0)).perform(click());

        //onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(allOf(
                hasComponent(hasShortClassName(".presentation.activity.RecipeDetailActivity")),
                toPackage(PACKAGE_NAME),
                hasExtraWithKey(RecipeDetailFragment.ARG_RECIPE_ID)));
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    private Context getAppContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}

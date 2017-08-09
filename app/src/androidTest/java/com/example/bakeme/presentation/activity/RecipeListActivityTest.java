package com.example.bakeme.presentation.activity;


import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.bakeme.MockRepository;
import com.example.bakeme.R;
import com.example.bakeme.RecyclerViewMatcher;
import com.example.bakeme.app.App;
import com.example.bakeme.presentation.fragment.RecipeDetailFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {
    private static final String PACKAGE_NAME = "com.example.bakeme";

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityRule = new ActivityTestRule<>(
            RecipeListActivity.class, true, false);

    @Rule
    public IntentsTestRule<RecipeDetailActivity> mIntentsRule =
            new IntentsTestRule<>(RecipeDetailActivity.class);

    @Before
    public void setUp() {
        App app = (App)getAppContext().getApplicationContext();
        app.setRepository(new MockRepository());

        mActivityRule.launchActivity(new Intent());
    }

    @Test
    public void onClickRecipe_openRecipeDetails() {
        onView(withRecyclerView(R.id.recipe_list).atPosition(0))
                .check(matches(hasDescendant(withText("Nutella Pie"))));

        onView(withRecyclerView(R.id.recipe_list).atPosition(0)).perform(click());

        //onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(allOf(
                hasComponent(hasShortClassName(".presentation.activity.RecipeDetailActivity")),
                toPackage(PACKAGE_NAME),
                hasExtraWithKey(RecipeDetailFragment.ARG_RECIPE_ID)));
    }

    @Test
    public void recipeListActivityTest() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recipe_list), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textView = onView(
                allOf(withText("Nutella Pie"),
                        childAtPosition(
                                allOf(withId(R.id.detail_toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar),
                                                0)),
                                1),
                        isDisplayed()));
        //textView.check(matches(withText("Nutella Pie")));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.ingredients_recycler_view),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        recyclerView2.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.ingredient_name_text_view), withText("Graham Cracker crumbs"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredients_recycler_view),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Graham Cracker crumbs")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.ingredient_quantity_text_view), withText("2"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredients_recycler_view),
                                        0),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("2")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.ingredient_measure_text_view), withText("CUP"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ingredients_recycler_view),
                                        0),
                                2),
                        isDisplayed()));
        textView4.check(matches(withText("CUP")));

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.recipe_steps_recycler_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.scroll_view),
                                        0),
                                2),
                        isDisplayed()));
        recyclerView3.check(matches(isDisplayed()));

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.recipe_step_card_view),
                        childAtPosition(
                                allOf(withId(R.id.recipe_steps_recycler_view),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                2)),
                                0),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.recipe_step_description_text_view), withText("Recipe Introduction"),
                        childAtPosition(
                                allOf(withId(R.id.recipe_step_card_view),
                                        childAtPosition(
                                                withId(R.id.recipe_steps_recycler_view),
                                                0)),
                                0),
                        isDisplayed()));
        textView5.check(matches(withText("Recipe Introduction")));
    }

    private static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    private Context getAppContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

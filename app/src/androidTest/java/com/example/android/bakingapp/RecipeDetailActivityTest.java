package com.example.android.bakingapp;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.activity.RecipeDetailActivity;
import com.example.android.bakingapp.model.RecipesContract;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeDetailActivityTest
{
    private static final String RECIPE_NAME = "Brownies";
    private static final String STRING_TO_BE_CHECK = "0. Recipe Introduction";
    private static final int RECIPE_ID = 1;

    /* Sometimes an {@link Activity} requires a custom start {@link android.content.Intent} to receive data
     * from the source Activity. ActivityTestRule has a feature which let's you lazily start the
     * Activity under test, so you can control the Intent that is used to start the target Activity.
     */
    @Rule
    public ActivityTestRule<RecipeDetailActivity> mRecipeDetailActivityTestRule =
            new ActivityTestRule<>(RecipeDetailActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);


    /**
     * Setup your test fixture with a fake recipe id. The {@link RecipeDetailActivity} is started with
     * a particular recipe id and recipe name.
     */
    @Before
    public void intentWithStubbedRecipeIdAndRecipeName()
    {
        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(RecipesContract.RecipeEntry.COLUMN_RECIPE_ID, RECIPE_ID);
        startIntent.putExtra("recipeName", RECIPE_NAME);
        mRecipeDetailActivityTestRule.launchActivity(startIntent);
    }

    @Test
    public void recipeDetails_DisplayedInUi() throws Exception
    {
        // Match the text  and check that it's displayed.
        onView(ViewMatchers.withId(R.id.recyclerview_steps)).check(matches(isDisplayed()));
        onView(withText(STRING_TO_BE_CHECK)).check(matches(isDisplayed()));
    }

}
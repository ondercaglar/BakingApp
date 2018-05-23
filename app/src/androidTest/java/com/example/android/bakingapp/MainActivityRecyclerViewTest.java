package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityRecyclerViewTest
{
    private static final String STRING_TO_BE_CHECK = "Brownies";
    private static final int ITEM_POSITION = 1;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Convenience method to register an IdlingResources with Espresso. IdlingResource resource is
     * a great way to tell Espresso when your app is in an idle state. This helps Espresso to
     * synchronize your test actions, which makes tests significantly more reliable.
     */
    @Before
    public void registerIdlingResource()
    {
        Espresso.registerIdlingResources(mActivityRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void scrollToPosition_checkItsText()
    {
        // First scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recyclerview_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM_POSITION, click()));

        // Match the text  and check that it's displayed.
        onView(withText(STRING_TO_BE_CHECK)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.recyclerview_steps)).check(matches(isDisplayed()));
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource()
    {
        Espresso.unregisterIdlingResources(mActivityRule.getActivity().getCountingIdlingResource());
    }
}
package com.example.android.bakingapp.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.bakingapp.IngredientsWidgetProvider;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.DetailPartFragment;
import com.example.android.bakingapp.fragments.MasterListFragment;
import com.example.android.bakingapp.model.RecipesContract;
import com.example.android.bakingapp.model.Step;

public class RecipeDetailActivity extends AppCompatActivity implements MasterListFragment.OnStepClickListener
{
    private String recipeName ="";
    private int recipeID;

    private static final String STATE_KEY_NAME = "state_key_name";

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Determine if you're creating a two-pane or single-pane display
        if(findViewById(R.id.detail_container) != null)
        {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;

            if (savedInstanceState == null)
            {
                recipeID   = getIntent().getIntExtra(RecipesContract.RecipeEntry.COLUMN_RECIPE_ID, 0);
                recipeName = getIntent().getStringExtra("recipeName");

                // In two-pane mode, add initial BodyPartFragments to the screen
                FragmentManager fragmentManager = getSupportFragmentManager();

                MasterListFragment masterListFragment = new MasterListFragment();
                masterListFragment.setRecipeID(recipeID);
                masterListFragment.setTwoPane(mTwoPane);
                // Add the fragment to its container using a transaction
                fragmentManager.beginTransaction().add(R.id.head_container, masterListFragment).commit();

                DetailPartFragment detailPartFragment = new DetailPartFragment();
                // Add the fragment to its container using a transaction
                fragmentManager.beginTransaction().add(R.id.detail_container, detailPartFragment).commit();
            }
            else
            {
                recipeName = savedInstanceState.getString(STATE_KEY_NAME);
            }
        }
        else
        {
            mTwoPane = false;

            if (savedInstanceState == null)
            {
                recipeID   = getIntent().getIntExtra(RecipesContract.RecipeEntry.COLUMN_RECIPE_ID, 0);
                recipeName = getIntent().getStringExtra("recipeName");

                // In two-pane mode, add initial BodyPartFragments to the screen
                FragmentManager fragmentManager = getSupportFragmentManager();

                MasterListFragment masterListFragment = new MasterListFragment();
                masterListFragment.setRecipeID(recipeID);
                masterListFragment.setTwoPane(mTwoPane);
                // Add the fragment to its container using a transaction
                fragmentManager.beginTransaction().add(R.id.head_container, masterListFragment).commit();
            }
            else
            {
                recipeName = savedInstanceState.getString(STATE_KEY_NAME);
            }
        }

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(recipeName);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_KEY_NAME, recipeName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.widget_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        SharedPreferences sharedPreferences   = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mPrefsEditor = sharedPreferences.edit();

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.set_widget:
                mPrefsEditor.putInt("addwidget", recipeID);
                mPrefsEditor.apply();

                Intent intent = new Intent(this, IngredientsWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(getApplication()).
                        getAppWidgetIds(new ComponentName(getApplication(), IngredientsWidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);

                Toast.makeText(this, getResources().getString(R.string.widget_info_success), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStepSelected(int position, Step selectedStep)
    {
       // Handle the two-pane case and replace existing fragments right when a new image is selected from the master list
        if (mTwoPane)
        {
            // Create two=pane interaction
            DetailPartFragment newFragment = new DetailPartFragment();
            newFragment.setStep(selectedStep);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, newFragment)
                    .commit();
        }
        else
        {
            Intent intent = new Intent(getBaseContext(), RecipeStepDetailActivity.class);
            intent.putExtra("recipeID",   recipeID);
            intent.putExtra("position",   position);
            intent.putExtra("recipeName", recipeName);
            startActivity(intent);
        }
    }

}

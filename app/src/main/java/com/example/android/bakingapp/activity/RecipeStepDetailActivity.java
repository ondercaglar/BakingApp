package com.example.android.bakingapp.activity;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;


import com.example.android.bakingapp.adapter.CategoryAdapter;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.RecipesContract;

import static com.example.android.bakingapp.fragments.MasterListFragment.MAIN_STEP_PROJECTION;


public class RecipeStepDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {

    private int recipeID;
    private static final int ID_DETAIL_STEP_LOADERS = 341;
    private CategoryAdapter adapter;
    private int position;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        recipeID   = getIntent().getIntExtra("recipeID", 0);
        position   = getIntent().getIntExtra("position", 0);
        String recipeName = getIntent().getStringExtra("recipeName");

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = findViewById(R.id.viewpager);
        // Create an adapter that knows which fragment should be shown on each page
        adapter = new CategoryAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        getSupportLoaderManager().initLoader(ID_DETAIL_STEP_LOADERS, null, this);

        /*

        if (savedInstanceState == null)
        {
             step = getIntent().getParcelableExtra("selectedStep");
             stepDescription = step.getShortDescription();

             DetailPartFragment detailPartFragment = new DetailPartFragment();
             detailPartFragment.setStep(step);

            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.detail_container, detailPartFragment).commit();
        }*/


        setActionBarTitle(recipeName);

    }


    private void setActionBarTitle(String title)
    {
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null)
        {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(title);
            ab.setElevation(0);
        }
     }




    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args)
    {
        switch (loaderId) {

            case ID_DETAIL_STEP_LOADERS:
                /* URI for all rows of recipe data in our recipes table */
                Uri stepQueryUri = RecipesContract.RecipeEntry.buildStepUriWithID(recipeID);

                return new CursorLoader(this,
                        stepQueryUri,
                        MAIN_STEP_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data)
    {
        adapter.swapSteps(data);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
    {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        adapter.swapSteps(null);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}

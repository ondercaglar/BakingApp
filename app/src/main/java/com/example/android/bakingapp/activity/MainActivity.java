package com.example.android.bakingapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.IdlingResource.EspressoIdlingResource;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapter.RecipeAdapter;
import com.example.android.bakingapp.model.RecipesContract;
import com.example.android.bakingapp.sync.BakingAppSyncUtils;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        RecipeAdapter.RecipeAdapterOnClickHandler
{
    private RecipeAdapter mRecipesAdapter;
    private static final int ID_RECIPE_LOADER = 44;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorText;


    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * recipe data.
     */
    private static final String[] MAIN_RECIPE_PROJECTION = {
            RecipesContract.RecipeEntry.COLUMN_RECIPE_ID,
            RecipesContract.RecipeEntry.COLUMN_RECIPE_NAME,
            RecipesContract.RecipeEntry.COLUMN_SERVINGS,
            RecipesContract.RecipeEntry.COLUMN_RECIPE_IMAGE
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_RECIPE_ID    = 0;
    public static final int INDEX_RECIPE_NAME  = 1;
    //public static final int INDEX_SERVINGS     = 2;
    //public static final int INDEX_RECIPE_IMAGE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        mRecyclerView     = findViewById(R.id.recyclerview_recipes);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorText        = findViewById(R.id.error_message_display);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mRecipesAdapter = new RecipeAdapter(this, this);
        mRecyclerView.setAdapter(mRecipesAdapter);

        showLoading();

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);

        BakingAppSyncUtils.initialize(this);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args)
    {
        switch (loaderId)
        {
            case ID_RECIPE_LOADER:

                /* URI for all rows of recipe data in our recipes table */
                Uri recipeQueryUri = RecipesContract.RecipeEntry.CONTENT_URI_RECIPES;

                return new CursorLoader(this,
                        recipeQueryUri,
                        MAIN_RECIPE_PROJECTION,
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
        mRecipesAdapter.swapRecipes(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0)
        {
            showRecipeDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
    {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mRecipesAdapter.swapRecipes(null);
    }


    @VisibleForTesting
    public IdlingResource getCountingIdlingResource()
    {
        return EspressoIdlingResource.getIdlingResource();
    }



    @Override
    public void onClickRecipeAdapter(int recipeID, String recipeName)
    {
        Intent recipeDetailIntent = new Intent(MainActivity.this, RecipeDetailActivity.class);
        recipeDetailIntent.putExtra(RecipesContract.RecipeEntry.COLUMN_RECIPE_ID, recipeID);
        recipeDetailIntent.putExtra("recipeName", recipeName);
        startActivity(recipeDetailIntent);
    }

    /**
     * This method will make the loading indicator visible and hide the recipe View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading()
    {
        /* Then, hide the recipe data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }


    /**
     * This method will make the View for the recipe data visible and hide the error message and
     * loading indicator.
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showRecipeDataView()
    {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the recipe data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
    }



    private void showErrorMessage()
    {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
    }

}

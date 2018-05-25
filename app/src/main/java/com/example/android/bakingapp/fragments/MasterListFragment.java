package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapter.StepAdapter;
import com.example.android.bakingapp.model.RecipesContract;
import com.example.android.bakingapp.model.Step;


public class MasterListFragment extends Fragment implements StepAdapter.StepAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>
{
    private StepAdapter stepAdapter;
    private TextView  ingredientTxt;
    private RecyclerView recyclerviewSteps;
    private int mPosition = RecyclerView.NO_POSITION;

    private int recipeID;

    private static final int ID_DETAIL_STEP_LOADER = 353;
    private static final int ID_DETAIL_INGREDIENTS_LOADER = 367;

    private Cursor mCursor;
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean twoPane;

    /*
     * The columns of data that we are interested in displaying within our MasterListFragment's list of
     * recipe data.
     */
    public static final String[] MAIN_STEP_PROJECTION =
            {
                    RecipesContract.RecipeEntry.COLUMN_RECIPE_ID,
                    RecipesContract.RecipeEntry.COLUMN_STEP_ID,
                    RecipesContract.RecipeEntry.COLUMN_SHORT_DESCRIPTION,
                    RecipesContract.RecipeEntry.COLUMN_DESCRIPTION,
                    RecipesContract.RecipeEntry.COLUMN_VIDEO_URL,
                    RecipesContract.RecipeEntry.COLUMN_THUMBNAIL_URL
            };

    public static final String[] MAIN_INGREDIENTS_PROJECTION =
            {
                    RecipesContract.RecipeEntry.COLUMN_RECIPE_ID,
                    RecipesContract.RecipeEntry.COLUMN_INGREDIENTS,
                    RecipesContract.RecipeEntry.COLUMN_MEASURE,
                    RecipesContract.RecipeEntry.COLUMN_QUANTITY
            };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_RECIPE_ID         = 0;
    public static final int INDEX_STEP_ID           = 1;
    public static final int INDEX_SHORT_DESCRIPTION = 2;
    public static final int INDEX_DESCRIPTION       = 3;
    public static final int INDEX_VIDEO_URL         = 4;
    public static final int INDEX_THUMBNAIL_URL     = 5;

    public static final int INDEX_INGREDIENTS     = 1;
    public static final int INDEX_MEASURE         = 2;
    public static final int INDEX_QUANTITY        = 3;


    private OnStepClickListener mCallback;
    public interface OnStepClickListener
    {
        void onStepSelected(int position, Step step);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mCallback = (OnStepClickListener)context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement OnStepClickListener");
        }
    }

    public MasterListFragment()
    {}



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(ID_DETAIL_STEP_LOADER, null, this);
        getLoaderManager().initLoader(ID_DETAIL_INGREDIENTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the Android-Me fragment layout
        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        recyclerviewSteps = rootView.findViewById(R.id.recyclerview_steps);
        ingredientTxt     = rootView.findViewById(R.id.ingredient_txt);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerviewSteps.setLayoutManager(layoutManager);
        recyclerviewSteps.setHasFixedSize(true);

        stepAdapter = new StepAdapter(getContext(), this);
        recyclerviewSteps.setAdapter(stepAdapter);

        return rootView;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {

        switch (loaderId)
        {
            case ID_DETAIL_STEP_LOADER:

                Uri mUri = RecipesContract.RecipeEntry.buildStepUriWithID(recipeID);
                if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

                return new CursorLoader(getActivity(),
                        mUri,
                        MAIN_STEP_PROJECTION,
                        null,
                        null,
                        null);

            case ID_DETAIL_INGREDIENTS_LOADER:

                Uri mUri_Ingredients = RecipesContract.RecipeEntry.buildIngredientsUriWithID(recipeID);
                if (mUri_Ingredients == null) throw new NullPointerException("URI for DetailActivity cannot be null");

                return new CursorLoader(getActivity(),
                        mUri_Ingredients,
                        MAIN_INGREDIENTS_PROJECTION,
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
        switch (loader.getId())
        {
            case ID_DETAIL_STEP_LOADER:
                stepAdapter.swapStep(data);
                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                recyclerviewSteps.smoothScrollToPosition(mPosition);
                mCursor = data;
                //if (data.getCount() != 0) showWeatherDataView();

                /*
                if (twoPane)
                {
                    onClickStepAdapter(0);
                }*/

                break;

            case ID_DETAIL_INGREDIENTS_LOADER:
                boolean cursorHasValidData = false;
                if (data != null && data.moveToFirst())
                {
                    /* We have valid data, continue on to bind the data to the UI */
                    cursorHasValidData = true;
                }

                if (!cursorHasValidData)
                {
                    /* No data to display, simply return and do nothing */
                    return;
                }

                final StringBuilder builder = new StringBuilder("Ingredients:");
                builder.append("<br/>");

                do {
                    String mIngredient = data.getString(INDEX_INGREDIENTS);
                    String mMeasure    = data.getString(INDEX_MEASURE);
                    Float  mQuantity   = data.getFloat(INDEX_QUANTITY);

                    String upperIngredientString = mIngredient.substring(0,1).toUpperCase() + mIngredient.substring(1);

                    builder.append("√ ").append(upperIngredientString).append(" (").append(mQuantity).append(" ").append(mMeasure).append(" ) <br/>");

                }while (data.moveToNext());

                final String allIngredients = builder.toString();
                ingredientTxt.setText(Html.fromHtml(allIngredients));
                break;


            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
    {
        stepAdapter.swapStep(null);
    }



    public void setRecipeID(int mRecipeID)
    {
        recipeID = mRecipeID;
    }


    public void setTwoPane(boolean mTwoPane)
    {
        twoPane = mTwoPane;
    }


    @Override
    public void onClickStepAdapter(int clickedItemIndex)
    {
        mCursor.moveToPosition(clickedItemIndex);
        Step mStep = new Step();
        mStep.setId(mCursor.getInt(MasterListFragment.INDEX_STEP_ID));
        mStep.setShortDescription(mCursor.getString(MasterListFragment.INDEX_SHORT_DESCRIPTION));
        mStep.setDescription(mCursor.getString(MasterListFragment.INDEX_DESCRIPTION));
        mStep.setVideoURL(mCursor.getString(MasterListFragment.INDEX_VIDEO_URL));
        mStep.setThumbnailURL(mCursor.getString(MasterListFragment.INDEX_THUMBNAIL_URL));

        mCallback.onStepSelected(clickedItemIndex, mStep);
    }
}

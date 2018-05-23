
package com.example.android.bakingapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingapp.IdlingResource.EspressoIdlingResource;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.RecipesContract;
import com.example.android.bakingapp.model.Step;
import com.example.android.bakingapp.rest.RecipeAPIService;
import com.example.android.bakingapp.utilities.InternetConnectionDetector;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class BakingAppSyncTask
{
        /**
     * Performs the network request for updated recipe, parses the JSON from that request by using
     * Retrofit Library, and inserts the new recipe information into our ContentProvider.
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncRecipes(final Context context)
    {

        EspressoIdlingResource.increment(); // App is busy until further notice.

        try
        {

            // creating connection detector class instance
            InternetConnectionDetector cd = new InternetConnectionDetector(context);
            Boolean isInternetPresent = cd.isConnectingToInternet();

            if(!isInternetPresent)
            {
                Toast.makeText(context, context.getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                return;
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://d17h27t6h515a5.cloudfront.net/topher/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RecipeAPIService service = retrofit.create(RecipeAPIService.class);

            final Call<List<Recipe>> recipes = service.listRecipes();
            recipes.enqueue(new Callback<List<Recipe>>()
            {
                @Override
                public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response)
                {
                    ArrayList<Recipe> recipesList = (ArrayList<Recipe>) response.body();


                    if (recipesList == null)
                    {
                        Log.d("BakingAppSyncTask", "Recipelist is null");
                        return;
                    }

                    /* Parse the JSON into a list of recipe values */
                    ContentValues[] recipeContentValues = new ContentValues[recipesList.size()];
                    List<ContentValues>  ingredientsContentValueList = new ArrayList<>();
                    List<ContentValues>  stepsContentValueList      = new ArrayList<>();

                    for (int i = 0; i < recipesList.size(); i++)
                    {
                        ContentValues recipeValues = new ContentValues();
                        recipeValues.put(RecipesContract.RecipeEntry.COLUMN_RECIPE_ID,    recipesList.get(i).getId());
                        recipeValues.put(RecipesContract.RecipeEntry.COLUMN_RECIPE_NAME , recipesList.get(i).getName());
                        recipeValues.put(RecipesContract.RecipeEntry.COLUMN_SERVINGS,     recipesList.get(i).getServings());
                        recipeValues.put(RecipesContract.RecipeEntry.COLUMN_RECIPE_IMAGE, recipesList.get(i).getImage());

                        ArrayList<Ingredient> ingredientList = (ArrayList<Ingredient>) recipesList.get(i).getIngredients();
                        ArrayList<Step>        stepArrayList = (ArrayList<Step>)       recipesList.get(i).getSteps();

                        for (int j = 0; j < ingredientList.size(); j++)
                        {
                            ContentValues ingredientsValues = new ContentValues();
                            ingredientsValues.put(RecipesContract.RecipeEntry.COLUMN_RECIPE_ID,    recipesList.get(i).getId());
                            ingredientsValues.put(RecipesContract.RecipeEntry.COLUMN_INGREDIENTS , ingredientList.get(j).getIngredient());
                            ingredientsValues.put(RecipesContract.RecipeEntry.COLUMN_MEASURE,      ingredientList.get(j).getMeasure());
                            ingredientsValues.put(RecipesContract.RecipeEntry.COLUMN_QUANTITY,     ingredientList.get(j).getQuantity());

                            ingredientsContentValueList.add(ingredientsValues);
                        }

                        for (int k = 0; k < stepArrayList.size(); k++)
                        {
                            ContentValues stepsValues = new ContentValues();
                            stepsValues.put(RecipesContract.RecipeEntry.COLUMN_RECIPE_ID,         recipesList.get(i).getId());
                            stepsValues.put(RecipesContract.RecipeEntry.COLUMN_STEP_ID ,          stepArrayList.get(k).getId());
                            stepsValues.put(RecipesContract.RecipeEntry.COLUMN_SHORT_DESCRIPTION, stepArrayList.get(k).getShortDescription());
                            stepsValues.put(RecipesContract.RecipeEntry.COLUMN_DESCRIPTION,       stepArrayList.get(k).getDescription());
                            stepsValues.put(RecipesContract.RecipeEntry.COLUMN_VIDEO_URL,         stepArrayList.get(k).getVideoURL());
                            stepsValues.put(RecipesContract.RecipeEntry.COLUMN_THUMBNAIL_URL,     stepArrayList.get(k).getThumbnailURL());

                            stepsContentValueList.add(stepsValues);
                        }

                        recipeContentValues[i] = recipeValues;
                    }

                    /*
                     * In cases where our JSON contained an error code, onResponse
                     * would have returned null. We need to check for those cases here to prevent any
                     * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
                     * there isn't any to insert.
                     */

                    if (recipeContentValues.length != 0)
                    {
                        /* Get a handle on the ContentResolver to delete and insert data */
                        ContentResolver bakingAppContentResolver = context.getContentResolver();

                        /* Delete old recipe data because we don't need to keep multiple data */
                        bakingAppContentResolver.delete(
                                RecipesContract.RecipeEntry.CONTENT_URI_RECIPES,
                                null,
                                null);

                        /* Insert our new recipe data into BakingApp's ContentProvider */
                        bakingAppContentResolver.bulkInsert(
                                RecipesContract.RecipeEntry.CONTENT_URI_RECIPES,
                                recipeContentValues);


                        if (ingredientsContentValueList.size() != 0)
                        {
                            ContentValues[] ingredientsContentValues = new ContentValues[ingredientsContentValueList.size()];
                            ingredientsContentValueList.toArray(ingredientsContentValues);

                            /* Delete old ingredients data because we don't need to keep multiple data */
                            bakingAppContentResolver.delete(
                                    RecipesContract.RecipeEntry.CONTENT_URI_INGREDIENTS,
                                    null,
                                    null);

                            /* Insert our new ingredients data into BakingApp's ContentProvider */
                            bakingAppContentResolver.bulkInsert(
                                    RecipesContract.RecipeEntry.CONTENT_URI_INGREDIENTS,
                                    ingredientsContentValues);
                        }


                        if (stepsContentValueList.size() != 0)
                        {
                            ContentValues[] stepsContentValues = new ContentValues[stepsContentValueList.size()];
                            stepsContentValueList.toArray(stepsContentValues);

                            /* Delete old steps data because we don't need to keep multiple data */
                            bakingAppContentResolver.delete(
                                    RecipesContract.RecipeEntry.CONTENT_URI_STEPS,
                                    null,
                                    null);

                            bakingAppContentResolver.bulkInsert(
                                    RecipesContract.RecipeEntry.CONTENT_URI_STEPS,
                                    stepsContentValues);
                        }
                        /* If the code reaches this point, we have successfully performed our sync */

                        EspressoIdlingResource.decrement(); // App is idle.

                    }
                 }

                @Override
                public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t)
                {
                    Log.d("BakingAppSyncTask", "error loading from API");
                }
            });
        }
        catch (Exception e)
        {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }
}
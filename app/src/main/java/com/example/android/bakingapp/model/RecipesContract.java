package com.example.android.bakingapp.model;

import android.net.Uri;
import android.provider.BaseColumns;


public class RecipesContract {


    private RecipesContract() {}

    /*
    * The "Content authority" is a name for the entire content provider, similar to the
    * relationship between a domain name and its website. A convenient string to use for the
    * content authority is the package name for the app, which is guaranteed to be unique on the
    * Play Store.
    */
    public static final String CONTENT_AUTHORITY = "com.example.android.bakingapp";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for BakingApp.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that BakingApp
     * can handle. For instance,
     *
     *     content://com.example.android.bakingapp/recipe/
     *     [           BASE_CONTENT_URI         ][ PATH_RECIPE ]
     *
     * is a valid path for looking at recipe data.
     *
     *      content://com.example.android.bakingapp/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_RECIPES     = "recipe";
    public static final String PATH_INGREDIENTS = "ingredient";
    public static final String PATH_STEPS       = "step";

    /* Inner class that defines the table contents of the recipe table */
    public static final class RecipeEntry implements BaseColumns
    {

        /* The base CONTENT_URI used to query the Recipe table from the content provider */
        public static final Uri CONTENT_URI_RECIPES = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPES)
                .build();


        public static final Uri CONTENT_URI_INGREDIENTS = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENTS)
                .build();


        public static final Uri CONTENT_URI_STEPS = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STEPS)
                .build();


        /* Used internally as the name of our recipe table. */
        public static final String TABLE_NAME_RECIPES        = "recipe";
        public static final String COLUMN_RECIPE_ID          = "recipe_id";
        public static final String COLUMN_RECIPE_NAME        = "recipe_name";
        public static final String COLUMN_SERVINGS           = "servings";
        public static final String COLUMN_RECIPE_IMAGE       = "recipe_image";


        /* Used internally as the name of our ingredients table. */
        public static final String TABLE_NAME_INGREDIENTS    = "ingredient";
        public static final String COLUMN_INGREDIENTS        = "ingredients";
        public static final String COLUMN_MEASURE            = "measure";
        public static final String COLUMN_QUANTITY           = "quantity";



        /* Used internally as the name of our steps table. */
        public static final String TABLE_NAME_STEPS          = "step";
        public static final String COLUMN_STEP_ID            = "step_id";
        public static final String COLUMN_SHORT_DESCRIPTION  = "short_description";
        public static final String COLUMN_DESCRIPTION        = "description";
        public static final String COLUMN_VIDEO_URL          = "video_url";
        public static final String COLUMN_THUMBNAIL_URL      = "thumbnail_url";



        public static Uri buildRecipeUriWithID(long id)
        {
            return CONTENT_URI_RECIPES.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }


        public static Uri buildIngredientsUriWithID(long id)
        {
            return CONTENT_URI_INGREDIENTS.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static Uri buildStepUriWithID(long id)
        {
            return CONTENT_URI_STEPS.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }
}

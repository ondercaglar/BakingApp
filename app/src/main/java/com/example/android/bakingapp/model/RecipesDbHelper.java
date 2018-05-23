package com.example.android.bakingapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.bakingapp.model.RecipesContract.RecipeEntry.COLUMN_RECIPE_ID;
import static com.example.android.bakingapp.model.RecipesContract.RecipeEntry.TABLE_NAME_INGREDIENTS;
import static com.example.android.bakingapp.model.RecipesContract.RecipeEntry.TABLE_NAME_RECIPES;
import static com.example.android.bakingapp.model.RecipesContract.RecipeEntry.TABLE_NAME_STEPS;


class RecipesDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    private static final String DATABASE_NAME = "bakingapp.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 1;

    public RecipesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our movier data.
         */
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_NAME_RECIPES + " (" +

                /*
                 * RecipeEntry did not explicitly declare a column called "_ID". However,
                 * RecipeEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                RecipesContract.RecipeEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_ID                                 + " INTEGER NOT NULL, "                  +
                RecipesContract.RecipeEntry.COLUMN_RECIPE_NAME   + " TEXT NOT NULL, "                     +
                RecipesContract.RecipeEntry.COLUMN_SERVINGS      + " INTEGER NOT NULL, "                  +
                RecipesContract.RecipeEntry.COLUMN_RECIPE_IMAGE  + " TEXT, "                              +

                /*
                 * To ensure this table can only contain one movie entry per id, we declare
                 * the movie id column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a movie entry for a certain movie id and we attempt to
                 * insert another movie entry with that id, we replace the old movie entry.
                 */
                " UNIQUE (" + COLUMN_RECIPE_ID + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_NAME_INGREDIENTS + " (" +

                RecipesContract.RecipeEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_ID                                 + " INTEGER NOT NULL, "                  +
                RecipesContract.RecipeEntry.COLUMN_INGREDIENTS   + " TEXT NOT NULL, "                     +
                RecipesContract.RecipeEntry.COLUMN_MEASURE       + " TEXT, "                              +
                RecipesContract.RecipeEntry.COLUMN_QUANTITY      + " FLOAT, "                             +

                " FOREIGN KEY (" + COLUMN_RECIPE_ID   + ") REFERENCES " + TABLE_NAME_RECIPES  + "(" + COLUMN_RECIPE_ID + "))";


        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + TABLE_NAME_STEPS + " (" +

                RecipesContract.RecipeEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_ID                                     + " INTEGER NOT NULL, "                  +
                RecipesContract.RecipeEntry.COLUMN_STEP_ID           + " INTEGER NOT NULL, "                  +
                RecipesContract.RecipeEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, "                     +
                RecipesContract.RecipeEntry.COLUMN_DESCRIPTION       + " TEXT NOT NULL, "                     +
                RecipesContract.RecipeEntry.COLUMN_VIDEO_URL         + " TEXT, "                              +
                RecipesContract.RecipeEntry.COLUMN_THUMBNAIL_URL     + " TEXT, "                              +

                " FOREIGN KEY (" + COLUMN_RECIPE_ID   + ") REFERENCES " + TABLE_NAME_RECIPES  + "(" + COLUMN_RECIPE_ID + "))";


        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STEP_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECIPES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_INGREDIENTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_STEPS);
        onCreate(sqLiteDatabase);
    }
}


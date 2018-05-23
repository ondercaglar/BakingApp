package com.example.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.RemoteViews;

import com.example.android.bakingapp.activity.MainActivity;
import com.example.android.bakingapp.model.RecipesContract;

import static com.example.android.bakingapp.fragments.MasterListFragment.INDEX_INGREDIENTS;
import static com.example.android.bakingapp.fragments.MasterListFragment.INDEX_MEASURE;
import static com.example.android.bakingapp.fragments.MasterListFragment.INDEX_QUANTITY;
import static com.example.android.bakingapp.fragments.MasterListFragment.MAIN_INGREDIENTS_PROJECTION;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {

        String allIngredients;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int recipeID = sharedPreferences.getInt("addwidget", 0);

        if (recipeID == 0)
        {
            allIngredients = "Please first open app and Set Widget";
        }
        else
        {
            /* Get a handle on the ContentResolver to delete and insert data */
            ContentResolver resolver = context.getContentResolver();

            Uri mUri_Ingredients = RecipesContract.RecipeEntry.buildIngredientsUriWithID(recipeID);
            if (mUri_Ingredients == null)
                throw new NullPointerException("URI for IngredientsWidgetProvider cannot be null");

            Cursor data = resolver.query(
                    mUri_Ingredients,
                    MAIN_INGREDIENTS_PROJECTION,
                    null,
                    null,
                    null);

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

            allIngredients = builder.toString();


        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        views.setTextViewText(R.id.update, Html.fromHtml(allIngredients));

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.update, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}


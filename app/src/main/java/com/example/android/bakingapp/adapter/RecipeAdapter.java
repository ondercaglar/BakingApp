package com.example.android.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.activity.MainActivity;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder>
{

    private final Context mContext;
    final private RecipeAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;



    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeAdapterOnClickHandler
    {
        void onClickRecipeAdapter(int recipeID, String recipeName);
    }




    public RecipeAdapter(Context context, RecipeAdapterOnClickHandler clickHandler)
    {
        mContext = context;
        mClickHandler = clickHandler;
    }


    @NonNull
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int layoutId = R.layout.recipe_card;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapterViewHolder recipeAdapterViewHolder, int position)
    {
        mCursor.moveToPosition(position);

        String name = mCursor.getString(MainActivity.INDEX_RECIPE_NAME);

        if (!TextUtils.isEmpty(name))
        {
            recipeAdapterViewHolder.recipeText.setText(name);
        }
    }


    @Override
    public int getItemCount()
    {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    /**
     * Swaps the Movies used by the MoviesAdapter for its movie data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the movie data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newRecipes the new cursor to use as ForecastAdapter's data source
     */
    public void swapRecipes(Cursor newRecipes)
    {
        mCursor = newRecipes;
        notifyDataSetChanged();
    }


    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final CardView cv;
        final TextView recipeText;


        RecipeAdapterViewHolder(View itemView)
        {
            super(itemView);
            cv = itemView.findViewById(R.id.card_view);
            recipeText = itemView.findViewById(R.id.recipe_text);
            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v)
        {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int recipeID = mCursor.getInt(MainActivity.INDEX_RECIPE_ID);
            String recipeName = mCursor.getString(MainActivity.INDEX_RECIPE_NAME);
            mClickHandler.onClickRecipeAdapter(recipeID, recipeName);
        }

    }

}

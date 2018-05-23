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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.MasterListFragment;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder>
{

    private final Context mContext;
    final private StepAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;


    /**
     * The interface that receives onClick messages.
     */
    public interface StepAdapterOnClickHandler
    {
        void onClickStepAdapter(int clickedItemIndex);
    }




    public StepAdapter(Context context, StepAdapterOnClickHandler clickHandler)
    {
        mContext = context;
        mClickHandler = clickHandler;
    }


    @NonNull
    @Override
    public StepAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int layoutId = R.layout.step_card;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new StepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepAdapterViewHolder stepAdapterViewHolder, int position)
    {

        mCursor.moveToPosition(position);
        String shortDescription  = mCursor.getString(MasterListFragment.INDEX_SHORT_DESCRIPTION);
        String  videoURL         = mCursor.getString(MasterListFragment.INDEX_VIDEO_URL);

        if (TextUtils.isEmpty(videoURL))
        {
            stepAdapterViewHolder.playButton.setVisibility(View.GONE);
        }

        final String oneStep = String.valueOf(position) + ". " + shortDescription;

        stepAdapterViewHolder.personName.setText(oneStep);
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
     * @param newStep the new cursor to use as ForecastAdapter's data source
     */
    public void swapStep(Cursor newStep)
    {
        mCursor = newStep;
        notifyDataSetChanged();
    }


    class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final CardView cv;
        final TextView personName;
        final ImageView playButton;

        StepAdapterViewHolder(View itemView)
        {
            super(itemView);
            cv = itemView.findViewById(R.id.card_view);
            personName = itemView.findViewById(R.id.recipe_text);
            playButton = itemView.findViewById(R.id.play_imageView);
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
            mClickHandler.onClickStepAdapter(adapterPosition);
        }

    }

}

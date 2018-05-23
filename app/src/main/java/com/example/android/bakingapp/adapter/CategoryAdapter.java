package com.example.android.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.DetailPartFragment;
import com.example.android.bakingapp.fragments.MasterListFragment;
import com.example.android.bakingapp.model.Step;


public class CategoryAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private Cursor mCursor;

    public CategoryAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position)
    {
        mCursor.moveToPosition(position);
        Step mStep = new Step();
        mStep.setId(mCursor.getInt(MasterListFragment.INDEX_STEP_ID));
        mStep.setShortDescription(mCursor.getString(MasterListFragment.INDEX_SHORT_DESCRIPTION));
        mStep.setDescription(mCursor.getString(MasterListFragment.INDEX_DESCRIPTION));
        mStep.setVideoURL(mCursor.getString(MasterListFragment.INDEX_VIDEO_URL));
        mStep.setThumbnailURL(mCursor.getString(MasterListFragment.INDEX_THUMBNAIL_URL));
        return DetailPartFragment.newInstance(position + 1, mStep);
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount()
    {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    public void swapSteps(Cursor newSteps)
    {
        mCursor = newSteps;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        // Generate title based on item position
        String stepGeneric = mContext.getResources().getString(R.string.step_numerification);
        return stepGeneric + position;
    }
}

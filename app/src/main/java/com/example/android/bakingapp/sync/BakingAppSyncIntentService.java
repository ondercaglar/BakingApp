package com.example.android.bakingapp.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class BakingAppSyncIntentService extends IntentService {

    public BakingAppSyncIntentService() {
        super("BakingAppSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        BakingAppSyncTask.syncRecipes(this);
    }
}
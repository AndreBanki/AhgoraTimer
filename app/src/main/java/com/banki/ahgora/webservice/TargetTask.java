package com.banki.ahgora.webservice;

import android.os.AsyncTask;

import com.banki.ahgora.controller.AsyncResponse;

public class TargetTask  extends AsyncTask<String, Void, Float> {

    public AsyncResponse delegate = null;

    public TargetTask(AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected Float doInBackground(String... params) {
        String id = params[0];
        TargetWS ws = new TargetWS();
        return ws.getTimeSpent(id);
    }

    protected void onPostExecute(Float timeSpent) {
        delegate.processFinishTarget(timeSpent);
    }

}
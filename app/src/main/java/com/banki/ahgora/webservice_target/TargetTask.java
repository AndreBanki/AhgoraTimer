package com.banki.ahgora.webservice_target;

import android.os.AsyncTask;

import com.banki.ahgora.controller.AsyncResponse;

public class TargetTask  extends AsyncTask<String, Void, Float> {

    public AsyncResponse delegate = null;

    public TargetTask(AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected Float doInBackground(String... params) {
        String login = params[0];

        UsersWS userWS = new UsersWS();
        String id = userWS.getUserId(login);
        if (id == null)
            return new Float(0);
        else {
            TimesWS ws = new TimesWS();
            return ws.getTimeSpent(id);
        }
    }

    protected void onPostExecute(Float timeSpent) {
        delegate.processFinishTarget(timeSpent);
    }

}
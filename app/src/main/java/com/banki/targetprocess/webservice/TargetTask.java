package com.banki.targetprocess.webservice;

import android.os.AsyncTask;

public class TargetTask  extends AsyncTask<String, Void, Float> {

    public ITargetResponse delegate = null;

    public TargetTask(ITargetResponse asyncResponse) {
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
        delegate.processFinish(timeSpent);
    }

}
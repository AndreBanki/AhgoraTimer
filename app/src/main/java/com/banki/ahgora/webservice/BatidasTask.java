package com.banki.ahgora.webservice;

import android.os.AsyncTask;

import com.banki.ahgora.controller.AsyncResponse;
import com.banki.ahgora.model.Batidas;

public class BatidasTask extends AsyncTask<String, Void, Void> {

    public AsyncResponse delegate = null;
    private Batidas batidas = null;

    public BatidasTask(AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    protected Void doInBackground(String... params) {
        String pis = params[0];
        BatidasWS ws = new BatidasWS();
        batidas = ws.obtemListaBatidas(pis);
        return null;
    }

    protected void onPostExecute(Void param) {
        delegate.processFinish(batidas);
    }

}

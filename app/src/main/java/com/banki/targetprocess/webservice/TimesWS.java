package com.banki.targetprocess.webservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimesWS extends TargetWS {

    private static Locale PT_BR = new Locale("pt","BR");
    private static String DATEFORMAT_ATREQUEST = "yyyy-MM-dd";

    private static final String TIMES_URL = TargetWS.BASE_URL + "/Times/?include=[spent,user]&format=json&take=100";

    public float getTimeSpent(String id) {
        URL url = montaRequest(id);
        JSONObject json = obtemResponse(url);
        if (json == null)
            return 0;
        else
            return parseJson(json);
    }

    private URL montaRequest(String id) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT_ATREQUEST, PT_BR);
        Calendar hoje = Calendar.getInstance();
        String data = dateFormat.format(hoje.getTime());
        String dateClause = "(Date%20eq%20%22" + data + "%22)";
        String userClause = "(User.Id%20eq%20" + id + ")";
        String whereCondition = "&where=" + dateClause + "and" + userClause;

        URL url = null;
        try {
            url = new URL(TIMES_URL + TargetWS.TOKEN + whereCondition);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public float parseJson(JSONObject json) {
        float timeSpent = 0;
        try {
            JSONArray jsonArray = json.getJSONArray("Items");
            for (int i = 0; i < jsonArray.length() ; i++) {
                JSONObject jsonEntry = jsonArray.getJSONObject(i);
                try {
                    timeSpent += Float.parseFloat(jsonEntry.getString("Spent"));
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return timeSpent;
    }
}

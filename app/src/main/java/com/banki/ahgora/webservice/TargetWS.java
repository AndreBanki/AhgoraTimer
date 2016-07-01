package com.banki.ahgora.webservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TargetWS {

    private static Locale PT_BR = new Locale("pt","BR");
    private static String DATEFORMAT_ATREQUEST = "yyyy-MM-dd";

    private static final String BASE_URL = "http://gp.altoqi.com.br/api/v1";
    private static final String TIMES_URL = BASE_URL + "/Times/?include=[spent,user]&format=json&take=100";
    private static final String TOKEN = "&token=Mzo5REUzQjM4QkQ5OTg5NUQzQTA5NjBDRUI5N0E3QTI3Ng==";

    HttpURLConnection urlConnection;

    public float getTimeSpent(String id) {
        URL url = montaRequest(id);
        JSONObject json = obtemResponse(url);
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
            url = new URL(TIMES_URL + TOKEN + whereCondition);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private JSONObject obtemResponse(URL url) {
        JSONObject json = null;
        StringBuilder result = new StringBuilder();
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
            try {
                json = new JSONObject(result.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
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

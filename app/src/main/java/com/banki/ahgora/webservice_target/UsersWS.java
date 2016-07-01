package com.banki.ahgora.webservice_target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class UsersWS extends TargetWS {

    private static final String USERS_URL = TargetWS.BASE_URL + "/Users/?format=json&take=100";

    public String getUserId(String login) {
        URL url = montaRequest(login);
        JSONObject json = obtemResponse(url);
        if (json == null)
            return null;
        else
            return parseJson(json);
    }

    private URL montaRequest(String login) {
        String userClause = "(Login%20eq%20%22" + login + "%22)";
        String whereCondition = "&where=" + userClause;

        URL url = null;
        try {
            url = new URL(USERS_URL + TargetWS.TOKEN + whereCondition);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public String parseJson(JSONObject json) {
        String id = "";
        try {
            JSONArray jsonArray = json.getJSONArray("Items");
            for (int i = 0; i < jsonArray.length() ; i++) {
                JSONObject jsonEntry = jsonArray.getJSONObject(i);
                id = jsonEntry.getString("Id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
}


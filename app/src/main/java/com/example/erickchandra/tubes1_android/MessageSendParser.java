package com.example.erickchandra.tubes1_android;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erickchandra on 3/26/16.
 */
public class MessageSendParser {
    JSONObject jsonObject;

    MessageSendParser(String _com, String _nim, String _answer, String _lat, String _lng, String _token) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put("com", _com);
            jsonObject.put("nim", _nim);
            jsonObject.put("answer", _answer);
            jsonObject.put("longitude", _lng);
            jsonObject.put("latitude", _lat);
            jsonObject.put("token", _token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getJSONObject() {
        return jsonObject.toString();
    }
}

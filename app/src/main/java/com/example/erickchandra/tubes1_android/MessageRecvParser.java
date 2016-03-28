package com.example.erickchandra.tubes1_android;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erickchandra on 3/26/16.
 */
public class MessageRecvParser {
    JSONObject jsonObject;
    String recvMsg;

    MessageRecvParser(String recvMsg) {
        this.recvMsg = recvMsg;
        try {
            jsonObject = new JSONObject(recvMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return jsonObject.optString("status");
    }

    public String getNIM() {
        return jsonObject.optString("nim");
    }

    public String getLat() {
        return jsonObject.optString("latitude");
    }

    public String getLng() {
        return jsonObject.optString("longitude");
    }

    public String getToken() {
        return jsonObject.optString("token");
    }
}

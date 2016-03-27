package com.example.asus.locationfinder;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ASUS on 25/03/2016.
 */
public class Target {
    private LatLng latLng;
    private String token;
    public Target(){
        latLng = new LatLng(0,0);
        token = "";
    }

    public void update(String json)
    {
        try
        {
            JSONObject obj = new JSONObject(json);
            double latitude = obj.getDouble("longitude");
            double longitude = obj.getDouble("latitude");
            String token = obj.getString("token");
            latLng = new LatLng(latitude,longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LatLng getLatLng()
    {
        return latLng;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String s){
        token = s;
    }
}


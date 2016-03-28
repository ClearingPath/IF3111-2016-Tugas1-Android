package com.example.husni.mapapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;

public class SetupActivity extends AppCompatActivity implements Response {

    private static final String TAG = SetupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        requestLocation();
    }

    public void requestLocation() {
        JSONObject message = new JSONObject();

        message.put("com", "req_loc");
        message.put("nim", "13513022");

        Log.d(TAG, "Message to send: " + message.toString());

        Client client = new Client(this, message);
        client.execute();
    }

    @Override
    public void requestDone(JSONObject res) {
        String status = (String) res.get("status");
        Log.d(TAG, "Status: " + status);
        double latitude = (double) res.get("longitude");
        Log.d(TAG, "Latitude: " + latitude);
        double longitude = (double) res.get("latitude");
        Log.d(TAG, "Longitude: " + longitude);
        String token = (String) res.get("token");
        Log.d(TAG, "Token: " + token);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("status", status);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("token", token);
        startActivity(intent);
    }
}

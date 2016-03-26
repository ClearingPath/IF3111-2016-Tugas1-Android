package com.example.husni.mapapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.simple.JSONObject;


public class SubmitAnswerActivity extends AppCompatActivity implements Response {

    public static final String TAG = MapsActivity.class.getSimpleName();
    Spinner locationSpinner;

    String status;
    double latitude;
    double longitude;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);

        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        status = (String) bundle.get("status");
        longitude = (double) bundle.get("longitude");
        latitude = (double) bundle.get("latitude");
        token = (String) bundle.getString("token");
    }

    public void submitAnswerButton(View view) {
        Context context = getApplicationContext();
        String location = locationSpinner.getSelectedItem().toString();

        String ans = "";
        switch (location) {
            case "GKU Barat":
                ans = "gku_barat";
                break;
            case "GKU Timur":
                ans = "gku_timur";
                break;
            case "Intel":
                ans = "intel";
                break;
            case "CC Barat":
                ans = "cc_barat";
                break;
            case "CC Timur":
                ans = "cc_timur";
                break;
            case "DPR":
                ans = "dpr";
                break;
            case "Sunken":
                ans = "sunken";
                break;
            case "Perpustakaan":
                ans = "perpustakaan";
                break;
            case "PAU":
                ans = "pau";
                break;
            case "Kubus":
                ans = "kubus";
                break;
        }

        JSONObject answerJson = new JSONObject();
        answerJson.put("com", "answer");
        answerJson.put("nim", "13513022");
        answerJson.put("answer", ans);
        answerJson.put("longitude", latitude);
        answerJson.put("latitude", longitude);
        answerJson.put("token", token);

        Client client = new Client(this, answerJson);
        client.execute();
    }

    @Override
    public void requestDone(JSONObject res) {
        String status = (String) res.get("status");
        String token = (String) res.get("token");

        if (status.equals("ok")) {
            Toast.makeText(getApplicationContext(), "Your answer is correct!", Toast.LENGTH_SHORT).show();

            double latitude = (double) res.get("latitude");
            double longitude = (double) res.get("longitude");

            Intent intent = new Intent (this, MapsActivity.class);
            intent.putExtra("status", status);
            intent.putExtra("latitude", longitude);
            intent.putExtra("longitude", latitude);
            intent.putExtra("token", token);
            startActivity(intent);

        } else if (status.equals("wrong_answer")) {
            Toast.makeText(getApplicationContext(), "Sorry, your answer is wrong", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
        } else if (status.equals("finish")) {
            Toast.makeText(getApplicationContext(), "Congratulation, You've completed all the quest!", Toast.LENGTH_SHORT).show();

            double latitude = (double) res.get("latitude");
            double longitude = (double) res.get("longitude");

            Intent intent = new Intent (this, MapsActivity.class);
            intent.putExtra("status", status);
            intent.putExtra("latitude", longitude);
            intent.putExtra("longitude", latitude);
            intent.putExtra("token", token);
            startActivity(intent);
        }


    }
}

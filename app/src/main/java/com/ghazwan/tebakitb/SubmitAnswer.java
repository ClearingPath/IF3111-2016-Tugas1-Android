package com.ghazwan.tebakitb;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONObject;

public class SubmitAnswer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);

        // Location Spinner
        Spinner locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);
    }

    public void submitAnswer(View view) {
        JSONObject json = new JSONObject();
        Spinner spinner = (Spinner) findViewById(R.id.locationSpinner);
        String answer = (String) spinner.getSelectedItem();
        try {
            Bundle bundle = getIntent().getExtras();
            JSONObject responsejson = new JSONObject(bundle.getString("response"));
            json.put("com", "answer");
            json.put("nim", responsejson.getString("nim"));
            json.put("answer", answer.toLowerCase());
            json.put("longitude", responsejson.getString("longitude"));
            json.put("latitude", responsejson.getString("latitude"));
            json.put("token", responsejson.getString("token"));
            Log.i("SubmitAnswerActivity", "Message: " + json.toString());
        } catch (org.json.JSONException e) {
            // nothing
        }
        Client myClient = new Client(json, getApplicationContext());
        myClient.execute();
    }
}

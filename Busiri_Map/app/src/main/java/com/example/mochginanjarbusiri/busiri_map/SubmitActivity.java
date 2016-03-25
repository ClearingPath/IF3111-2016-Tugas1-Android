package com.example.mochginanjarbusiri.busiri_map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class SubmitActivity extends AppCompatActivity {
    private String[] states;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        states = getResources().getStringArray(R.array.location);
        spinner = (Spinner) findViewById(R.id.location_spinner);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, states);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void Submit(View view)
    {
        sendAnswer();
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void sendAnswer()
    {
        /*JSONObject json = new JSONObject();
        SocketClient socket = new SocketClient();

        try
        {
            json.put("com", "answer");
            json.put("nim", "13513111");
            json.put("answer", spinner.getOnItemSelectedListener());
            json.put("longitude",socket.json.optDouble("longitude"));
            json.put("latitude", socket.json.optDouble("latitude"));
            json.put("token", socket.json.optString("token"));
            SocketClient socketP = new SocketClient(json.toString());
            socketP.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}

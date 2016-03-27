package com.example.atia.tubes1android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SubmitActivity extends AppCompatActivity {

    private double lat;
    private double lng;
    private String token;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Submit Answer");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getDouble("LAT_VALUE");
            lng = extras.getDouble("LONG_VALUE");
            token = extras.getString("TOKEN");
        }

        Spinner spinner = (Spinner) findViewById(R.id.loc_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.loc_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String answer = ((Spinner) findViewById(R.id.loc_spinner)).getSelectedItem().toString();
                new SubmitLocation().execute(answer);
            }
        });
    }

    private class SubmitLocation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... answer) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("com", "answer");
                obj.put("nim", "13512017");
                obj.put("answer", answer[0]);
                obj.put("longitude", lng);
                obj.put("latitude", lat);
                obj.put("token", token);

                Log.i("log", "Submit Request: " + obj.toString());

                Socket socket = new Socket(MapActivity.ServerIP, MapActivity.SERVERPORT);

                OutputStream os = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(os);
                out.writeUTF(obj.toString());

                InputStream is = socket.getInputStream();
                DataInputStream in = new DataInputStream(is);
                response = in.readUTF();

                Log.i("log", "Server Response: " + response);

                socket.close();

                return response;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonresponse = new JSONObject(response);
                String status = jsonresponse.getString("status");

                if (status.equals("ok")) {
                    lat = jsonresponse.getDouble("latitude");
                    lng = jsonresponse.getDouble("longitude");
                    token = jsonresponse.getString("token");

                    LatLng latlng = new LatLng(lat,lng);

                    MapActivity.setParam(lat, lng, token);
                    MapActivity.setMarker(latlng);

                    Toast.makeText(SubmitActivity.this, "Location marker set", Toast.LENGTH_SHORT).show();

                } else if (status.equals("wrong_answer")) {
                    Toast.makeText(SubmitActivity.this, "Wrong answer", Toast.LENGTH_LONG).show();
                } else if (status.equals("err")) {
                    Toast.makeText(SubmitActivity.this, "No NIM or no com", Toast.LENGTH_LONG).show();
                } else if (status.equals("finish")) {
                    Toast.makeText(SubmitActivity.this, "Finish", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

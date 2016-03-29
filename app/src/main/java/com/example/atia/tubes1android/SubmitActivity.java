package com.example.atia.tubes1android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SubmitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private double lat;
    private double lng;
    private String token;
    private String response;
    private String answerstr;

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
        spinner.setOnItemSelectedListener(this);

        final Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String answer = ((Spinner) findViewById(R.id.loc_spinner)).getSelectedItem().toString();
                new SubmitLocation().execute(answerstr);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                answerstr = "gku_barat";
                break;
            case 1:
                answerstr = "gku_timur";
                break;
            case 2:
                answerstr = "intel";
                break;
            case 3:
                answerstr = "cc_barat";
                break;
            case 4:
                answerstr = "cc_timur";
                break;
            case 5:
                answerstr = "dpr";
                break;
            case 6:
                answerstr = "oktagon";
                break;
            case 7:
                answerstr = "perpustakaan";
                break;
            case 8:
                answerstr = "pau";
                break;
            case 9:
                answerstr = "kubus";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class SubmitLocation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... answer) {

            response = null;

            try {

                JSONObject obj = new JSONObject();
                obj.put("com", "answer");
                obj.put("nim", "13512017");
                obj.put("answer", answerstr);
                obj.put("longitude", lng);
                obj.put("latitude", lat);
                obj.put("token", token);

                Log.i("log", "Submit Request: " + obj.toString());

                Socket socket = new Socket(MapActivity.ServerIP, MapActivity.SERVERPORT);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(obj);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                response = in.readLine();

                Log.i("log", "Server Response: " + response);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(SubmitActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                });

                socket.close();

                return response;

            } catch (JSONException e) {
                Log.e("log", "jso");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("log", "ioexception");
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

package com.bomba.conte.tubes1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 26/03/2016.
 */
public class SubmitActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar titlebar;
    Button submit;
    Spinner answerSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        titlebar = (android.support.v7.widget.Toolbar)findViewById(R.id.titlebar);
        setSupportActionBar(titlebar);
        getSupportActionBar().setTitle("Submit Answer");
        answerSpinner = (Spinner)findViewById(R.id.answer);

        List<String> answers = new ArrayList<String>();
        answers.add("gku_barat");
        answers.add("gku_timur");
        answers.add("intel");
        answers.add("cc_barat");
        answers.add("cc_timur");
        answers.add("dpr");
        answers.add("sunken");
        answers.add("perpustakaan");
        answers.add("pau");
        answers.add("kubus");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, answers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        answerSpinner.setAdapter(dataAdapter);

        submit = (Button)findViewById(R.id.submitButton);
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View v) {
                String answer = answerSpinner.getSelectedItem().toString();
                new AnswerSubmitter().execute(answer);
            }
        });
    }

    private class AnswerSubmitter extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... answer) {

            double lat = 0;
            double lng = 0;
            String response = "";
            Socket socket = null;
            PrintWriter printer;
            BufferedReader reader;
            SharedPreferences storage = getSharedPreferences("Maps", Activity.MODE_PRIVATE);
            Looper.prepare();

            try {
                socket = new Socket(MapsActivity.serverAddress, MapsActivity.serverPort);

                LocationManager locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                LocationListener locLis = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {


                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                try {
                    locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locLis);
                    Location loc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    lat = loc.getLatitude();
                    lng = loc.getLongitude();

                } catch (SecurityException e) {
                    Log.d("MyMap", e.toString());
                }

                JSONObject answerJSON = new JSONObject();
                answerJSON.put("com", "answer");
                answerJSON.put("nim", "13513013");
                answerJSON.put("answer", answer[0]);
                answerJSON.put("longitude", lng);
                answerJSON.put("latitude", lat);
                answerJSON.put("token", storage.getString("Token", "EmptyToken"));

                printer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printer.println(answerJSON.toString());
                Log.d("CommLog", answerJSON.toString());
                printer.flush();
                response = reader.readLine();
                reader.close();
                Log.d("CommLog", response);

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject resultJSON = null;
            String status;
            SharedPreferences storage = getSharedPreferences("Maps", Activity.MODE_PRIVATE);
            SharedPreferences.Editor writer = storage.edit();
            try {
                resultJSON = new JSONObject(result);
                status = resultJSON.getString("status");
                writer.putString("Status", status);
                writer.putString("Token", resultJSON.getString("token"));
                if (status.equals("ok")) {
                    writer.putLong("Longitude", resultJSON.getLong("longitude"));
                    writer.putLong("Latitude", resultJSON.getLong("latitude"));
                    setResult(MapsActivity.OK);
                }
                else if (status.equals("wrong_answer")) {
                    setResult(MapsActivity.WRONG);
                }
                else if (status.equals("finish")) {
                    setResult(MapsActivity.DONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            writer.commit();
            Toast.makeText(SubmitActivity.this, result, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}

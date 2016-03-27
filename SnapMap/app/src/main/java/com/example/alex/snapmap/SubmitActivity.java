package com.example.alex.snapmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
import java.net.UnknownHostException;

public class SubmitActivity extends AppCompatActivity {
    private Button b3;
    private JSONObject json;
    private JSONObject jsonResponse;
    private double Lat,Lng;
    private String token;
    SharedPreferences myprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        b3 = (Button) findViewById(R.id.button);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(v);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{"GKU Barat", "GKU Timur", "Intel", "CC Barat", "CC Timur", "DPR", "Oktagon", "Perpustakaan", "PAU", "Kubus"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    public void submitAnswer(View v) {
        new connectSocket().execute();
    }

    private class connectSocket extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            String option = spinner.getSelectedItem().toString();
            switch(option) {
                case "GKU Barat" :
                    option = "gku_barat";
                    break;
                case "GKU Timur" :
                    option = "gku_timur";
                    break;
                case "Intel" :
                    option = "intel";
                    break;
                case "CC Barat" :
                    option = "cc_barat";
                    break;
                case "CC Timur" :
                    option = "cc_timur";
                    break;
                case "DPR" :
                    option = "dpr";
                    break;
                case "Oktagon" :
                    option = "oktagon";
                    break;
                case "Perpustakaan" :
                    option = "perpustakaan";
                    break;
                case "PAU" :
                    option = "pau";
                    break;
                case "Kubus" :
                    option = "kubus";
                    break;
            }

            myprefs= getSharedPreferences("user", Context.MODE_PRIVATE);
            token= myprefs.getString("token", null);

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location locationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if ( GPSLocationTime - NetLocationTime > 0 ) {
                assert locationGPS != null;
                Lng = locationGPS.getLongitude();
                Lat = locationGPS.getLatitude();
            }
            else {
                assert locationNet != null;
                Lng = locationNet.getLongitude();
                Lat = locationNet.getLatitude();
            }

            Socket socket = null;
            OutputStream dataOutputStream = null;
            InputStream dataInputStream = null;

            try {
                json = new JSONObject();
                json.put("com", "answer");
                json.put("nim", "13513023");
                json.put("answer", option);
                json.put("longitude", "-6.890356");
                json.put("latitude", "107.610359");
                //json.put("longitude", String.valueOf(Lng));
                //json.put("latitude", String.valueOf(Lat));
                json.put("token", token);
                Log.d("APP", json.toString());
            } catch (Throwable t) {
                Log.e("MyApp", "Could not parse malformed JSON: \"" + json.toString() + "\"");
            }

            Log.d("COMMLOG", json);

            try {
                socket = new Socket("167.205.34.132", 3111);
                dataOutputStream = socket.getOutputStream();
                dataInputStream = socket.getInputStream();
                PrintWriter writer = new PrintWriter(dataOutputStream);
                writer.println(json.toString());
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
                String response = reader.readLine();

                return response;
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jsonResponse = new JSONObject(result);
                if(jsonResponse.getString("status").equals("ok")) {
                    Lat = jsonResponse.getDouble("latitude");
                    Lng = jsonResponse.getDouble("longitude");
                }
                token = jsonResponse.getString("token");
                Log.d("MYAPP", jsonResponse.toString());
            } catch (Throwable t) {
                Log.e("MyApp", "Could not parse malformed JSON: \"" + jsonResponse.toString() + "\"");
            }

            myprefs.edit().putString("token", token).commit();

            try {
                myprefs.edit().putString("latitude", String.valueOf(Lat)).commit();
                myprefs.edit().putString("longitude", String.valueOf(Lng)).commit();
            } catch (Throwable t) {
                Log.e("MyApp", "Could not parse malformed JSON: \"" + jsonResponse.toString() + "\"");
            }

            Log.d("COMMLOG", result);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    SubmitActivity.this);

            // set title
            alertDialogBuilder.setTitle("Response");
            // set dialog message
            alertDialogBuilder
                    .setMessage(result)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

            try {
                if (jsonResponse.getString("status").equals("ok")) {
                    setResult(MapsActivity.OK);
                }
                else if (jsonResponse.getString("status").equals("wrong_answer")) {
                    setResult(MapsActivity.WRONG);
                }
                else if (jsonResponse.getString("status").equals("finish")) {
                    setResult(MapsActivity.FINISH);
                }
            } catch (Throwable t) {
                Log.e("MyApp", "Could not parse malformed JSON: \"" + jsonResponse.toString() + "\"");
            }
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}


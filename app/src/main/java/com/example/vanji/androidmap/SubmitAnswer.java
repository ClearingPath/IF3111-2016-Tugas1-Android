package com.example.vanji.androidmap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SubmitAnswer extends AppCompatActivity {
    Button buttonSubmit;
    Spinner spinnerAnswer;
    private String answer;
    private final String ServerIP = "167.205.34.132";
    private final int ServerPort = 3111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submitans);

        spinnerAnswer = (Spinner)findViewById(R.id.spinner1);
        buttonSubmit = (Button)findViewById(R.id.button1);
        buttonSubmit.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                answer = String.valueOf(spinnerAnswer.getSelectedItem());
                new Submit().execute();
            }});
    }


    public class Submit extends AsyncTask<Void, Void, String> {
        private String status, token;
        private double latitude, longitude;

        @Override
        protected String doInBackground(Void... args) {

            Socket socket = null;
            try {
                socket = new Socket(ServerIP, ServerPort);
                try {
                    Looper.prepare();
                    LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
                    LocationListener ll = new LocationListener() {
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
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
                    Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                JSONObject reader = new JSONObject();
                reader.put("answer", answer);
                reader.put("com", "answer");
                reader.put("nim", "13513052");
                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                reader.put("token", sp.getString("token", null));
                reader.put("longitude", longitude);
                reader.put("latitude", latitude);

                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(reader.toString());
                out.flush();
                System.err.println(reader);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String res = in.readLine();
                System.err.println(res);
                return res;

        /*
         * notice:
         * inputStream.read() will block if no data return
         */
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if(socket != null){
                    try {
                        socket.close();
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
                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();

                JSONObject response = new JSONObject(result);
                status = response.getString("status");
                if (status.equals("ok")){
                    longitude = response.getDouble("longitude");
                    latitude = response.getDouble("latitude");
                    edit.putString("longitude", Double.toString(longitude));
                    edit.putString("latitude", Double.toString(latitude));
                    Toast.makeText(getApplicationContext(), "Jawaban benar", Toast.LENGTH_LONG).show();
                }
                else if (status.equals("wrong_answer")){
                    Toast.makeText(getApplicationContext(), "Jawaban salah", Toast.LENGTH_LONG).show();
                }
                else { //finish
                    Toast.makeText(getApplicationContext(), "Selesaaiii!!", Toast.LENGTH_LONG).show();
                }
                token = response.getString("token");

                edit.putString("token", token);
                edit.commit();
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}

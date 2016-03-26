package com.example.user.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SubmitMessage extends AppCompatActivity {
    private Spinner spinner1;
    private Button submitButton;
    private Toolbar tb;
    private DateFormat df;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_message);
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Submit Answer");
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        df = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm:ss");
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);

    }

    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        submitButton = (Button) findViewById(R.id.btnSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Container.setAnswer(String.valueOf(spinner1.getSelectedItem()));
                new sendAnswer().execute();
                Toast.makeText(SubmitMessage.this,
                        "Answer selected : " + String.valueOf(spinner1.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private class sendAnswer extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Looper.prepare();
            Socket socket = null;
            String response = "";
            JSONObject jsonRequest = new JSONObject();
            try {
                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                LocationListener mlocListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location loc) {
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle bundle) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }

                };
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
                Location loc = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                Container.setLtd(loc.getLatitude());
                Container.setLng(loc.getLongitude());

                jsonRequest.put("com", "answer");
                jsonRequest.put("nim", "13513003");
                jsonRequest.put("answer", Container.getAnswer());
                jsonRequest.put("longitude", loc.getLongitude());
                jsonRequest.put("latitude", loc.getLatitude());
                jsonRequest.put("token", Container.getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch(SecurityException e){
                e.printStackTrace();
            }
            try {
                socket = new Socket(Container.getServerIP(), Container.getPort());
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream);
                writer.println(jsonRequest.toString());
                writer.flush();
                String date = df.format(Calendar.getInstance().getTime());
                GoogleMapsActivity.actLog.add(jsonRequest.toString() + " ,time: " + date.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                response = reader.readLine();
                socket.close();
                date = df.format(Calendar.getInstance().getTime());
                GoogleMapsActivity.actLog.add(response+" ,time: " + date.toString());
                return response;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                String date = df.format(Calendar.getInstance().getTime());
                Log.i("response : ", result + " ,time: " + date.toString());
                Toast.makeText(SubmitMessage.this, "response : " + result + " ,time: " + date.toString(), Toast.LENGTH_SHORT).show();
            }
            try {
                if(result != null) {
                    JSONObject jsonResponse = new JSONObject(result);
                    Container.setStatus(jsonResponse.getString("status"));
                    if (Container.getStatus().equals("finish"))
                        Container.setCheck(jsonResponse.getInt("check"));
                    else if (Container.getStatus().equals("ok")) {
                        Container.setLtd(jsonResponse.getDouble("latitude"));
                        Container.setLng(jsonResponse.getDouble("longitude"));
                    }
                    Container.setToken(jsonResponse.getString("token"));
                }
                else{
                    Toast.makeText(SubmitMessage.this, "Failed to receive response", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SubmitMessage.this, "Sending another request", Toast.LENGTH_SHORT).show();
                    new sendAnswer().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finish();
        }
    }
}

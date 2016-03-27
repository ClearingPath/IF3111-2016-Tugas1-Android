package com.example.yoga.tubes1android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class SubmitAnswer extends AppCompatActivity {
    private Socket socket;
    private Spinner spinner;
    private Button button;
    static private double targetlatitude, targetlongitude;
    static private String token, nim, status;
    static String ans;
    PrintWriter out;
    BufferedReader input;
    String json, response;
    double mylongitude, mylatitude;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
    };

    String ip="167.205.34.132";
    int port=3111;
    Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addListenerOnButton();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent lastintent = getIntent();
        token = lastintent.getStringExtra("token");
        nim = lastintent.getStringExtra("nim");
        targetlatitude = lastintent.getDoubleExtra("latitude", 1);
        targetlongitude = lastintent.getDoubleExtra("longitude", 1.0);
        try {
            locationManager = (LocationManager)
                    getSystemService(getApplicationContext().LOCATION_SERVICE);
            locationListener = new MyLocationListener();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, LOCATION_PERMS, 1);
                return;
            }


            Location loc = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
            mylatitude=loc.getLatitude();
            mylongitude=loc.getLongitude();

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            case 2:
                boolean writeAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

                break;

        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            mylongitude = loc.getLongitude();
            mylatitude = loc.getLatitude();
        }


        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }


    public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(String.valueOf(spinner.getSelectedItem())){
                    case "GKU Barat":ans="gku_barat";
                        break;
                    case "GKU Timur":ans="gku_timut";
                        break;
                    case "Intel":ans="intel";
                        break;
                    case "CC Barat":ans="cc_barat";
                        break;
                    case "CC Timur":ans="cc_timur";
                        break;
                    case "DPR":ans="dpr";
                        break;
                    case "Oktagon":ans="oktagon";
                        break;
                    case "Perpustakaan":ans="perpustakaan";
                        break;
                    case "PAU":ans="pau";
                        break;
                    case "Kubus":ans="kubus";
                        break;
                }
                new Connect().execute("");


            }

        });
    }

    private class Connect extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                input= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("com", "answer");
                jsonObject.put("nim", nim);
                jsonObject.put("answer", ans);
                jsonObject.put("latitude", String.valueOf(mylatitude));
                jsonObject.put("longitude", String.valueOf(mylongitude));

                jsonObject.put("token", token);
                json = jsonObject.toString();
                System.out.println(json);
                out.println(json);
                response=input.readLine();
                System.out.println(response);
                final JSONObject obj=new JSONObject(response);
                status=obj.getString("status");
                if(status.equals("ok")){
                    token=obj.getString("token");
                    nim=obj.getString("nim");
                    targetlatitude=obj.getDouble("latitude");
                    targetlongitude=obj.getDouble("longitude");
                }else{
                    token=obj.getString("token");
                    nim=obj.getString("nim");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("token", token);
            resultIntent.putExtra("nim",nim);
            resultIntent.putExtra("latitude", targetlatitude);
            resultIntent.putExtra("longitude", targetlongitude);
            resultIntent.putExtra("json",c.getTime().toString()+" "+json);
            resultIntent.putExtra("response",c.getTime().toString()+" "+response);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        }


    }


}

package com.example.atia.tubes1android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Socket socket;
    private static GoogleMap mMap;
    private boolean first = true;
    public static final int SERVERPORT = 3111;
//    private static final String ServerIP = "167.205.34.132";
    public static final String ServerIP = "192.168.1.9";
    private static String token;
    private static double lat;
    private static double lng;
    private String response;
    private static Marker marker;
    pri

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Map");

            // show map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            final ImageButton ibCam = (ImageButton) findViewById(R.id.ibCamera);
            ibCam.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                Snackbar.make(v, "Camera Snackbar", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    dispatchTakePictureIntent();
                }
            });

            final ImageButton ibMes = (ImageButton) findViewById(R.id.ibMessage);
            ibMes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent subIntent = new Intent(MapActivity.this, SubmitActivity.class);
                    subIntent.putExtra("LAT_VALUE", lat);
                    subIntent.putExtra("LONG_VALUE", lng);
                    subIntent.putExtra("TOKEN", token);
                    MapActivity.this.startActivity(subIntent);
                }
            });

        if (first) {
            new FirstRequest().execute();
            first = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // check if GPS is enabled
        checkGPSStatus(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        // Add a marker
//        LatLng itb = new LatLng(-6.891453, 107.610642);
//        mMap.addMarker(new MarkerOptions().position(itb).title("Your school dumbass"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(itb));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            // nothing happens?
            // app will crash if data is null / user presses back key without taking anything... for some reason?
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void checkGPSStatus(final Activity activity) {

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "Please go to Settings to enable GPS";

            builder.setMessage(message).setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int id) {
                    activity.startActivity(new Intent(action));
                    d.dismiss();
                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int id) {
                            d.cancel();
                        }
                    });
            builder.create().show();
        }
    }

    public static void setMarker(LatLng latlong) {
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(latlong).title("The Location"));
    }

    public static void setParam(double newlat, double newlng, String newtoken) {
        lat = newlat;
        lng = newlng;
        token = newtoken;
    }

    private class FirstRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... args) {
            Socket socket = null;
            response = null;

            try {
                socket = new Socket(ServerIP, SERVERPORT);

                JSONObject obj = new JSONObject();
                obj.put("com", "req_loc");
                obj.put("nim", "13512017");

                Log.i("log","requesting first location");

                OutputStream os = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(os);
                out.writeUTF(obj.toString());

                InputStream is = socket.getInputStream();
                DataInputStream in = new DataInputStream(is);
                response = in.readUTF();

                socket.close();

                return response;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonresponse = new JSONObject(response);
                lat = jsonresponse.getDouble("latitude");
                lng = jsonresponse.getDouble("longitude");
                LatLng latlng = new LatLng(lat, lng);

                Toast.makeText(MapActivity.this, "requesting location", Toast.LENGTH_LONG).show();
                Toast.makeText(MapActivity.this, "Location: " + lat + " " + lng, Toast.LENGTH_LONG).show();

                Log.i("log", "Location: " + lat + "," + lng);
                setMarker(latlng);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    private void processRequest(String response) {
//        JSONObject jsonresponse = null;
//        try {
//            jsonresponse = new JSONObject(response);
//            String status = jsonresponse.getString("status");
//            token = jsonresponse.getString("token");
//
//            if (status.equals("ok")) {
//                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
//                lat = jsonresponse.getDouble("latitude");
//                lng = jsonresponse.getDouble("longitude");
//                LatLng latlng = new LatLng(lat,lng);
//                setMarker(latlng);
//            } else if (status.equals("wrong_answer")) {
//                Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show();
//            } else if (status.equals("finish")) {
//                Toast.makeText(this, "FINISH", Toast.LENGTH_LONG).show();
//            } else if (status.equals("err")) {
//                Toast.makeText(this, "No NIM or no com", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

}

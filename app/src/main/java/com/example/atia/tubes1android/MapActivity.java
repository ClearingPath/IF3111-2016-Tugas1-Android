package com.example.atia.tubes1android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener
{

    private Socket socket;
    private static GoogleMap mMap;
    static final boolean FIRST_VAL = true;
    private boolean first;
    public static final int SERVERPORT = 3111;
//    public static final String ServerIP = "167.205.34.132";
    public static final String ServerIP = "192.168.43.122";
    private static String token;
    private static double lat;
    private static double lng;
    private String response;
    private static Marker marker;

    private ImageView compassImage;

    private SensorManager mSensorManager;
    private float curDegree = 0f;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Map");

            if (savedInstanceState != null) {
                first = savedInstanceState.getBoolean("FIRST_VAL");
            } else {
                first = true;
            }

            // show map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mapFragment.setRetainInstance(true);

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

        compassImage = (ImageView) findViewById(R.id.compass);

        // initialize sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
        if (marker != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 7));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        curDegree = -degree;
        compassImage.setRotation(curDegree);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("FIRST_VAL", first);
        super.onSaveInstanceState(savedInstanceState);
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

                Log.i("log", "requesting first location");

                OutputStream os = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(os);
                out.writeUTF(obj.toString());

                Handler handler1 = new Handler(Looper.getMainLooper());
                handler1.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MapActivity.this, "Requesting location", Toast.LENGTH_SHORT).show();
                    }
                });

                InputStream is = socket.getInputStream();
                DataInputStream in = new DataInputStream(is);
                response = in.readUTF();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MapActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                });


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

                Log.i("log", "Location: " + lat + "," + lng);
                setMarker(latlng);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

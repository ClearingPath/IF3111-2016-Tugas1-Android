package com.example.asus.locationfinder;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int ANSWER = 3111;
    public static final int OK = 1;
    public static final int WRONG = 2;
    public static final int FINISH = 3;
    public static final String ServerIP = "167.205.34.132";
    public static final int PORT = 3111;
    private GoogleMap mMap;
    private boolean init = true;
    public static Target tgt;
    private static SensorManager sensorService;
    private Sensor sensor;
    private ImageView compass;
    private float currentDegree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        compass = (ImageView) findViewById(R.id.compass);
        currentDegree = 0f;
        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (init) {
            new RequestLocation().execute();
            init = false;
            Toast.makeText(MapsActivity.this,
                    "Requesting first location.",
                    Toast.LENGTH_SHORT).show();
        }

        if (sensor != null) {
            sensorService.registerListener(mySensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");
        } else {
            Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
            Toast.makeText(this, "ORIENTATION Sensor not found",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // angle between the magnetic north direction
            // 0=North, 90=East, 180=South, 270=West
            float azimuth = event.values[0];
            currentDegree = -azimuth;
            compass.setRotation(currentDegree);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        sensorService.registerListener(mySensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorService.unregisterListener(mySensorEventListener);
    }

    private class RequestLocation extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... args) {
            try {
                JSONObject request = new JSONObject();
                request.put("com", "req_loc");
                request.put("nim", "13511008");
                Log.i("Log",android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ssaa", new java.util.Date()) + " " + request.toString());
                Socket socket = new Socket(ServerIP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(request.toString());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.flush();
                String result = in.readLine();
                socket.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.i("Log", android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ssaa", new java.util.Date()) + " " + response);
            tgt = new Target();
            tgt.update(response);
            setMarker();
        }
    }

    public void setMarker(){
        mMap.addMarker(new MarkerOptions().position(tgt.getLatLng()).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tgt.getLatLng()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
        LatLng pos = new LatLng(-6.890356, 107.610359);
        mMap.addMarker(new MarkerOptions().position(pos).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ANSWER) {
            if (resultCode == OK) {
                AlertDialog alert = CreateAlert("Correct!","Search next location");
                alert.show();
                setMarker();
            } else if (resultCode == WRONG) {
                AlertDialog alert = CreateAlert("Wrong answer!","Try again");
                alert.show();
            } else if (resultCode == FINISH) {
                AlertDialog alert = CreateAlert("Congratulation!","Last location found");
                alert.show();
                mMap.clear();
            }
        }
    }

    public AlertDialog CreateAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(title);
        alert.setMessage(message);
        return alert;
    }

    public void setCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void sendAnswer(View view) {
        Intent intent = new Intent(this, AnswerActivity.class);
        startActivityForResult(intent, ANSWER);
    }
}

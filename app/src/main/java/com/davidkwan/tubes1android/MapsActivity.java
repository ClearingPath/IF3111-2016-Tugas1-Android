package com.davidkwan.tubes1android;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // For compass
    private ImageView pointer;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] rotation = new float[9];
    private float[] orientation = new float[3];
    private float currentDegree = 0f;

    // For request location
    public static double latitude;
    public static double longitude;
    public static String token;
    public static String ip = "167.205.34.132";
    public static int port = 3111;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            System.out.println("Location changed");

            latitude = location.getLatitude();
            longitude = location.getLongitude();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        pointer = (ImageView) findViewById(R.id.compass);

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // When googleMap is ready, request location and put marker to the next location
        new RequestLocation().execute();
    }

    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotation, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotation, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            pointer.startAnimation(ra);
            currentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSubmitAnswer(View view){
        Intent intent = new Intent(this, SubmitAnswerActivity.class);
        startActivity(intent);
    }

    private class RequestLocation extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params) {

            try {
                Socket socket = new Socket(ip, port);
                JSONObject request = new JSONObject();
                request.put("com", "req_loc");
                request.put("nim", "13513019");

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                PrintWriter out = new PrintWriter(outputStream);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                System.out.println("Request sent: " + request.toString());
                out.println(request.toString());
                out.flush();

                String result = in.readLine();
                System.out.println("Response received: " + result);

                inputStream.close();
                outputStream.close();
                socket.close();

                return result;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Add a marker from the received response
            JSONObject jsonResult = null;
            try {
                jsonResult = new JSONObject(result);
                String status = jsonResult.getString("status");
                if(status.equals("ok")) {
                    latitude = jsonResult.getDouble("latitude");
                    longitude = jsonResult.getDouble("longitude");
                    token = jsonResult.getString("token");

                    // LatLng nextLocation = new LatLng(latitude, longitude);
                    LatLng nextLocation = new LatLng(longitude, latitude);
                    mMap.addMarker(new MarkerOptions().position(nextLocation).title("Next Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nextLocation, 18.0f));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

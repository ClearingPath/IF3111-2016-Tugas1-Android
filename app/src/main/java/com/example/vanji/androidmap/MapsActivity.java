package com.example.vanji.androidmap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private final String ServerIP = "167.205.34.132";
    private final int ServerPort = 3111;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private String status, token;
    private double longitude, latitude;
    private boolean firstTime = true;
    private ImageView compassImage;

    public float initialDegree = 0f;
    private SensorManager manager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portrait);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        compassImage = (ImageView) findViewById(R.id.imageViewCompass);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        compassImage = (ImageView) findViewById(R.id.imageViewCompass);
        if(firstTime == true)
            new connectServer().execute();
        else {
            SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
            latitude = Double.parseDouble(sp.getString("latitude", null));
            longitude = Double.parseDouble(sp.getString("longitude", null));
        }
    }

    public void sendAnswer(View view) {
        Intent intent = new Intent(this, SubmitAnswer.class);
        startActivity(intent);
    }

    public void camera(View view){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        manager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause() {
        super.onPause();

        manager.unregisterListener(this, mAccelerometer);
        manager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
             mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation rotate = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            rotate.setDuration(250);
            rotate.setFillAfter(true);
            compassImage.startAnimation(rotate);
            mCurrentDegree = -azimuthInDegress;
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class connectServer extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... args) {

            Socket socket = null;

            try {
                socket = new Socket(ServerIP, ServerPort);
                JSONObject reader = new JSONObject();
                reader.put("com", "req_loc");
                reader.put("nim", "13513052");
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
                JSONObject response = new JSONObject(result);
                status = response.getString("status");
                longitude = response.getDouble("longitude");
                latitude = response.getDouble("latitude");
                token = response.getString("token");
                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("token",token);
                edit.commit();
                firstTime = false;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

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
        mMap.getUiSettings().setCompassEnabled(false);
        // Add a marker in Sydney and move the camera
        LatLng map = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(map).title("Dimana hayoo?"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(map));
        CameraPosition campos = new CameraPosition(map, 17, 0, 0);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(campos));
    }
}

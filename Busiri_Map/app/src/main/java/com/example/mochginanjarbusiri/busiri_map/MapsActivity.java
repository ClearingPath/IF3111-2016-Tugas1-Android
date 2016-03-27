package com.example.mochginanjarbusiri.busiri_map;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int SUBMIT_REQUEST_CODE = 200;
    private double longitude;
    private double latitude;
    private String token;
    private String status;
    private String response = "";
    private String nim = "13513111";
    private ImageView icon_compass;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private Bundle data = new Bundle();
    private boolean harusSend;
    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null)
        {
            token = savedInstanceState.getString("token");
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");
            status = savedInstanceState.getString("status");
            harusSend = false;
            data = savedInstanceState;
        }
        else
        {
            harusSend = true;
        }




        ImageButton button_camera = (ImageButton)findViewById(R.id.button_camera);
        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // start the image capture Intent
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        });

        ImageButton button_submit = (ImageButton)findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubmitActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("longitude", longitude);
                bundle.putDouble("latitude", latitude);
                bundle.putString("token", token);
                intent.putExtras(bundle);
                startActivityForResult(intent, SUBMIT_REQUEST_CODE);
            }
        });

        icon_compass = (ImageView)findViewById(R.id.icon_compass);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save data
        savedInstanceState.putString("nim", nim);
        savedInstanceState.putDouble("longitude", longitude);
        savedInstanceState.putDouble("latitude", latitude);
        savedInstanceState.putString("token", token);
        savedInstanceState.putString("status", status);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener((SensorEventListener) this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener((SensorEventListener) this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener((SensorEventListener) this, mAccelerometer);
        mSensorManager.unregisterListener((SensorEventListener) this, mMagnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
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
            RotateAnimation ra = new RotateAnimation(mCurrentDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);
            icon_compass.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

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
        if (harusSend)
            sendRequest();

        Marker(data);
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //DO Nothing
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == SUBMIT_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Intent intent = data;
                Bundle bundle = intent.getExtras();
                if (bundle != null)
                {
                    token = bundle.getString("token");
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                }
            }
        }
    }

    public void sendRequest()
    {
        JSONObject json = new JSONObject();

        try {
            json.put("com", "req_loc");
            json.put("nim", this.nim);
            Log.d("Mengirim ke Server", json.toString() + " " + mydate);
            this.response = new SocketClient(json.toString(), mMap).execute().get();
            Toast.makeText(getApplicationContext(), "RESPONSE: " + response, Toast.LENGTH_LONG).show();
            JSONObject responseResult = new JSONObject(this.response);
            if (responseResult.has("latitude"))
                this.latitude = responseResult.getDouble("latitude");

            if (responseResult.has("longitude"))
                this.longitude = responseResult.getDouble("longitude");

            this.token = responseResult.getString("token");
            this.status = responseResult.getString("status");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void Marker(Bundle result)
    {
            double lat = result.getDouble("latitude");
            double longit = result.getDouble("longitude");
            LatLng itb = new LatLng(longit, lat);
            mMap.addMarker(new MarkerOptions().position(itb).title("Institut Teknologi Bandung"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(itb));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(itb, 20.0f));
    }


}

package com.example.erickchandra.tubes1_android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;

    // For Map LatLng

    // For Intent Information Passing
    Intent myIntent;
    String intentMsg;
    String cStatus, cNIM, cLat, cLng, cToken;
    double cLatDouble, cLngDouble;
    MessageRecvParser cMRP;
    LatLng cLatLng;

    // For Compass
    private ImageView mPointer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle("Map");

        // For Camera Button
        ImageButton cameraButton = (ImageButton) findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // For Message Submit Button
        ImageButton msgSubmitButton = (ImageButton) findViewById(R.id.button_message);
        msgSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMsgSubmit();
            }
        });

        // For Compass
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.pointer);

        // For Intent Information Passing
        myIntent = getIntent();
        intentMsg = myIntent.getStringExtra("Message");
        Toast.makeText(getApplicationContext(), "Received Intent Message: " + intentMsg, Toast.LENGTH_SHORT).show();
        Log.d(this.getClass().toString(), "Received Intent Message: " + intentMsg);
        cMRP = new MessageRecvParser(intentMsg);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    // Sensor Implementation
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
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360)%360;

            RotateAnimation ra;
//            System.out.println("ORIENTATION: " + getResources().getConfiguration().orientation + "   " + ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                if (this.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0) {
                    ra = new RotateAnimation(
                            mCurrentDegree,
                            -azimuthInDegrees,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f);
                }
                else {
                    ra = new RotateAnimation(
                            mCurrentDegree + 180,
                            -azimuthInDegrees,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f);
                }
            }
            else { // SCREEN_ORIENTATION_LANDSCAPE
                if (this.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90) {
                    ra = new RotateAnimation(
                            mCurrentDegree - 90,
                            -azimuthInDegrees,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f);
                }
                else {
                    ra = new RotateAnimation(
                            mCurrentDegree + 90,
                            -azimuthInDegrees,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f);
                }
            }

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegrees;
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Passing Intent LatLng
//        if (cMRP.getStatus() == "ok") {
//            cLng = cMRP.getLat();
//            cLngDouble = Double.parseDouble(cLng);
//            cLat = cMRP.getLng();
//            cLatDouble = Double.parseDouble(cLat);
//        }

        // Set up Google Maps initial position
//        LatLng default_itb = new LatLng(cLatDouble, cLngDouble);
//        mMap.addMarker(new MarkerOptions().position(default_itb).title("Guess Place"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(default_itb, 16.0f));
        cMRP = new MessageRecvParser(intentMsg);
        Toast.makeText(this.getApplicationContext(), "On Create, Intent Message", Toast.LENGTH_LONG).show();
        Log.d(this.getClass().toString(), "On Create, Intent Message");

        // Passing Intent LatLng
        cLng = cMRP.getLat(); // SWAP HERE!!!
        cLngDouble = Double.parseDouble(cLng);
        cLat = cMRP.getLng(); // SWAP HERE!!!
        cLatDouble = Double.parseDouble(cLat);

        Toast.makeText(getApplicationContext(), "Current LatDouble: " + cLatDouble + "\nCurrent LngDouble: " + cLngDouble, Toast.LENGTH_SHORT).show();
        Log.d(this.getClass().toString(), "Current LatDouble: " + cLatDouble + "\nCurrent LngDouble" + cLngDouble);

        cLatLng = new LatLng(cLatDouble, cLngDouble);
        mMap.addMarker(new MarkerOptions().position(cLatLng).title("Guess Place!"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cLatLng, 16.0f));
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void launchMsgSubmit() {
        Intent msgSubmitIntent = new Intent(this, SubmitActivity.class);
        msgSubmitIntent.putExtra("Message", intentMsg);
        startActivityForResult(msgSubmitIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getStringExtra("SubmitReplyMessage") != null) {
                cMRP = new MessageRecvParser(data.getStringExtra("SubmitReplyMessage"));
                Toast.makeText(this.getApplicationContext(), data.getStringExtra("SubmitReplyMessage"), Toast.LENGTH_LONG).show();
                Log.d(this.getClass().toString(), "SUBMIT REPLY MESSAGE: " + data.getStringExtra("SubmitReplyMessage"));

                // Passing Intent LatLng
                if (cMRP.getStatus().equals("ok")) {
                    cLng = cMRP.getLat(); // SWAP HERE!!!
                    cLngDouble = Double.parseDouble(cLng);
                    cLat = cMRP.getLng(); // SWAP HERE!!!
                    cLatDouble = Double.parseDouble(cLat);

                    // Set up Google Maps initial position
                    cLatLng = new LatLng(cLngDouble, cLatDouble);
                    mMap.addMarker(new MarkerOptions().position(cLatLng).title("Guess Place!"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cLatLng, 16.0f));
                }

                Toast.makeText(getApplicationContext(), "Current LatDouble: " + cLatDouble + "\nCurrent LngDouble: " + cLngDouble, Toast.LENGTH_SHORT).show();
                Log.d(this.getClass().toString(), "Current LatDouble: " + cLatDouble + "\nCurrent LngDouble" + cLngDouble);


                // Log for Status Check
                Log.d(this.getClass().toString(), "STATUS: OK == " + cMRP.getStatus().equals("ok"));
                Log.d(this.getClass().toString(), "STATUS: WRONG ANSWER == " + cMRP.getStatus().equals("wrong_answer"));
                Log.d(this.getClass().toString(), "STATUS: FINISH == " + cMRP.getStatus().equals("finish"));

                // Check status
                if (cMRP.getStatus().equals("ok")) {
                    Toast.makeText(getApplicationContext(), "You submitted correct answer.", Toast.LENGTH_SHORT).show();
                    Log.d(this.getClass().toString(), "REPLY STATUS: CORRECT ANSWER.");
                }
                else if (cMRP.getStatus().equals("wrong_answer")) {
                    Toast.makeText(getApplicationContext(), "You submitted correct answer.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Please retry.", Toast.LENGTH_SHORT).show();
                    Log.d(this.getClass().toString(), "REPLY STATUS: WRONG ANSWER.");
                }
                else if (cMRP.getStatus().equals("finish")) {
                    Toast.makeText(getApplicationContext(), "Congratulation! You have finished!", Toast.LENGTH_SHORT).show();
                    finish();
                    Log.d(this.getClass().toString(), "REPLY STATUS: FINISH.");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // If there is no result
            }
        }
    }
}

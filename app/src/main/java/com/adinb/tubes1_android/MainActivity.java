package com.adinb.tubes1_android;


import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap googleMap;
    private Location currentLocation;
    private String json;
    private String imagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private LinearLayout lin;
    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;

    final int LOCATION_REQUEST_CODE = 1;
    final int STORAGE_REQUEST_CODE = 2;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            Log.d("ORI", "Landscape");
            lin = (LinearLayout) findViewById(R.id.buttonsgroup);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lin.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lin.setPadding(0, 0, 16, 0);
            lin.setOrientation(LinearLayout.VERTICAL);
            lin.setLayoutParams(params);
            View v1 = findViewById(R.id.v1);
            View v2 = findViewById(R.id.v2);
            View v3 = findViewById(R.id.v3);

            LinearLayout.LayoutParams viewparams = (LinearLayout.LayoutParams) v1.getLayoutParams();
            viewparams.width = 1;
            viewparams.height = 0;
            viewparams.weight = 1;
            v1.setLayoutParams(viewparams);
            v2.setLayoutParams(viewparams);
            v3.setLayoutParams(viewparams);


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Potrait
            Log.d("ORI", "Potrait");
            lin = (LinearLayout) findViewById(R.id.buttonsgroup);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lin.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lin.setOrientation(LinearLayout.HORIZONTAL);
            lin.setPadding(0,0,0,16);
            lin.setLayoutParams(params);

            View v1 = findViewById(R.id.v1);
            View v2 = findViewById(R.id.v2);
            View v3 = findViewById(R.id.v3);

            LinearLayout.LayoutParams viewparams = (LinearLayout.LayoutParams) v1.getLayoutParams();
            viewparams.width = 0;
            viewparams.height = 1;
            viewparams.weight = 1;
            v1.setLayoutParams(viewparams);
            v2.setLayoutParams(viewparams);
            v3.setLayoutParams(viewparams);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.map_title);

        // Initial layout setup
        lin = (LinearLayout) findViewById(R.id.buttonsgroup);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lin.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lin.setOrientation(LinearLayout.HORIZONTAL);
        lin.setPadding(0,0,0,16);
        lin.setLayoutParams(params);

        View v1 = findViewById(R.id.v1);
        View v2 = findViewById(R.id.v2);
        View v3 = findViewById(R.id.v3);

        LinearLayout.LayoutParams viewparams = (LinearLayout.LayoutParams) v1.getLayoutParams();
        viewparams.width = 0;
        viewparams.height = 1;
        viewparams.weight = 1;
        v1.setLayoutParams(viewparams);
        v2.setLayoutParams(viewparams);
        v3.setLayoutParams(viewparams);

        // Setup map fragment
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);

        // Setup compass
        image = (ImageView) findViewById(R.id.imageViewCompass);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


    }

    public void takePhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = savePhoto();
            } catch (IOException e){
                Log.d("IO",e.toString());
            }

            if (photoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(imagePath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }
    }

    public void submitAnswer(View view){
        Intent submitIntent = new Intent(this, SubmitAnswer.class).putExtra(Intent.EXTRA_TEXT, json);
        startActivity(submitIntent);
    }

    private File savePhoto() throws IOException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            // Ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_REQUEST_CODE);
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagePath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Enable location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(false);
            uiSettings.setCompassEnabled(true);
            Log.d("COMPASS", String.valueOf(uiSettings.isCompassEnabled()));
        } else {
            // Ask for permission
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST_CODE);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        } else {
            // Ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }


        if (currentLocation != null) {
            onLocationChanged(currentLocation);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    // Ask for permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);
                }
            }

            } else {
                // Permission was denied. Display an error message.
            }

        }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
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

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);

        image.startAnimation(ra);
        currentDegree = -degree;
        Log.d("Degree:", Float.toString(degree));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

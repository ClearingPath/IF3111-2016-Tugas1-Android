package com.mfikria.itblocator;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity
        extends
        FragmentActivity
        implements
        OnMapReadyCallback,
        SensorEventListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    @SuppressWarnings("unused")
    private static final double
            ITB_LAT = -6.8918184,
            ITB_LNG = 107.610651;

    private static final int
            DEFAULT_ZOOM = 17;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;


    private ImageView compass;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] orientation = new float[3];
    private float currentDegree = 0f;

    private LatLng currentMarkerLoc;

    private LocationManager locationManager;
    private Location currentLocation;
    private GoogleMap googleMap;
    private Marker marker;

    private JSONObject response;
    private JSONObject request;
    int step;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        if(!bundle.isEmpty()) {
            String receivedString = bundle.getString("response");
            try {
                response = new JSONObject(receivedString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (isServicesAvaliable()) {
            setContentView(R.layout.activity_main);

            if (initMap()) {
                Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
            }
            initCompass();
        }
        ImageButton cameraButton = (ImageButton) findViewById(R.id.buttonCamera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean isServicesAvaliable() {
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, isAvailable, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onStart() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        super.onStart();

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        if(!bundle.isEmpty()) {
            String receivedString = bundle.getString("response");
            try {
                response = new JSONObject(receivedString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.mfikria.itblocator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        client.disconnect();
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.mfikria.itblocator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

    }

    private void setMarker(LatLng ll, String title) {
        if(marker != null) marker.remove();
        MarkerOptions markerOptions = new MarkerOptions().title(title).position(ll);
        marker = googleMap.addMarker(markerOptions);
        gotoLocation(ll, DEFAULT_ZOOM);
    }
    private boolean initMap() {
        if (googleMap == null) {
            SupportMapFragment mapFrag;
            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
        return googleMap != null;
    }

    private void gotoCurrentLocation() {

        Toast.makeText(this,"Your location:\n" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        gotoLocation(ll, DEFAULT_ZOOM);
    }

    private void gotoLocation(LatLng ll, int zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        googleMap.animateCamera(cameraUpdate);
    }


    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        if(!bundle.isEmpty()) {
            String receivedString = bundle.getString("response");
            try {
                response = new JSONObject(receivedString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng ll = new LatLng(ITB_LAT, ITB_LNG);

        googleMap.setMyLocationEnabled(true);
        gotoLocation(ll, DEFAULT_ZOOM);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        Toast.makeText(getApplicationContext(), "Response:\n" + response.toString(), Toast.LENGTH_SHORT).show();

        try {
            if(response.getString("status").equals("finish"))
                Toast.makeText(this, "Congratulation. You finish it!", Toast.LENGTH_SHORT).show();
            if(response.getString("status").equals("finish") || response.getString("status").equals("ok")){
                LatLng ll2 = new LatLng(response.getDouble("longitude"),response.getDouble("latitude"));
                setMarker(ll2, "Target Location");
                currentMarkerLoc = new LatLng(response.getDouble("longitude"),response.getDouble("latitude"));
            }else {
                setMarker(currentMarkerLoc, "Target Location");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initCompass() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        compass = (ImageView) findViewById(R.id.pointer);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
        Bundle bundle = new Bundle();
        try {
            request = new JSONObject(response.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.remove("status");

        try {
//            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                request.put("latitude", currentLocation.getLatitude());
//                request.put("longitude", currentLocation.getLongitude());
//            }
            request.put("com", "answer");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        if(request.has("check")) {
//            request.remove("check");
//        }
        bundle.putString("request", request.toString());
        intent.putExtras(bundle);

        startActivityForResult(intent, 2);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK){
                try {
                    response = new JSONObject(data.getStringExtra("response"));
                    googleMap.clear();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onMapReady(googleMap);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.gotoCurrentLocation:
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    gotoCurrentLocation();
                else
                    Toast.makeText(this,"Please enable your gps.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            SensorManager.getRotationMatrix(mR, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(mR, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            float degree;
            if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                degree = currentDegree;
            } else {
                degree = currentDegree + 90;
            }
            Animation ra = new RotateAnimation(
                    degree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            compass.startAnimation(ra);
            currentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            googleMap.clear();
            onMapReady(googleMap);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        
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
                takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

}
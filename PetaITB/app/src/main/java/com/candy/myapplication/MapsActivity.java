package com.candy.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Candy Olivia Mawalim on 27/03/2016.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ImageButton cameraButton, answerButton, logButton;
    private String log;
    private GoogleMap mMap;
    private boolean firstRun = true;
    private boolean cek = false;
    private ImageView mImageView;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView compassImage;
    // record the compass picture angle turned
    private float[] gData = new float[3];
    private float[] mData = new float[3];
    private float[] rMat = new float[9];
    private float[] iMat = new float[9];
    private float degree = 0f;
    private float currentDegree = 0f;
    private float[] orientation = new float[9];

    private String ipAddress = "167.205.34.132";
    private String port = "3111";
    private String res = "";
    private String token = "";
    private String status = "";
    private Double latitude;
    private Double longitude;
    private double currentLat;
    private double currentLng;
    private Marker marker;
    private Marker curMarker;

    // device sensor manager
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cameraButton = (ImageButton) findViewById(R.id.camera);
        answerButton = (ImageButton) findViewById(R.id.answer);
        logButton = (ImageButton) findViewById(R.id.log);
        mImageView = (ImageView) findViewById(R.id.image);
        compassImage = (ImageView) findViewById(R.id.compass);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Get User Location
        if (checkGooglePlayServices()) {
            buildGoogleApiClient();

            //prepare connection request
            createLocationRequest();
        }

        final JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("com", "req_loc");
            jsonData.put("nim", "13513031");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Client myClient = new Client(ipAddress, Integer.parseInt(port));
        myClient.execute(jsonData);

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(new Date());
        if (log != null) {
            log += "(Client)" + timeStamp + "   : " + jsonData + "\n\n";
        } else {
            log = "(Client)" + timeStamp + "   : " + jsonData + "\n\n";
        }



        //Camera Button onClickListener
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //Answer Button onClickListener
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer();
            }
        });

        //Log Button onClickListener
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog();
            }
        });


        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            log = bundle.getString("log");
        }

    }

    //LogButton clicked method
    private void showLog() {
        Intent intent = new Intent(this, LogPage.class);
        intent.putExtra("log", log);
        startActivity(intent);

    }

    //Submit answer method
    private void submitAnswer() {
        Intent intent = new Intent(this, SubmitAnswerActivity.class);
        intent.putExtra("longitude", currentLat);
        intent.putExtra("latitude", currentLng);
        intent.putExtra("token", token);
        intent.putExtra("log",log);

        startActivity(intent);
        cek = true;
    }

    //Camera intent
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void updateMap() {
        if (marker != null) {
            marker.remove();
        }

        LatLng position = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions().position(position).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (longitude != null && latitude != null) {
            LatLng position = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
            marker = mMap.addMarker(new MarkerOptions().position(position).title("Marker"));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gData = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mData = event.values;
        }

        if (gData != null && mData != null) {
            if (SensorManager.getRotationMatrix(rMat, iMat, gData, mData)) {
                SensorManager.remapCoordinateSystem(rMat, SensorManager.AXIS_X, SensorManager.AXIS_Z, rMat);
                degree = (float) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            }

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            compassImage.startAnimation(ra);
            currentDegree = -degree;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    private boolean checkGooglePlayServices() {

        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
              /*
               * google play services is missing or update is required
               *  return code could be
               * SUCCESS,
               * SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
               * SERVICE_DISABLED, SERVICE_INVALID.
               */
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {

            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /* Second part*/

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        currentLat = mLastLocation.getLatitude();
        currentLng = mLastLocation.getLongitude();
        if (curMarker != null) {
            curMarker.remove();
        }

        LatLng curPosition = new LatLng(currentLat,currentLng);
        curMarker = mMap.addMarker(new MarkerOptions().position(curPosition).title("Current Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }


    }

    class Client extends AsyncTask<JSONObject, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        JSONObject jsonData;


        Client(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            jsonData = params[0];

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                out.println(jsonData);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                res = in.readLine();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
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
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!res.equals("")) {
                Toast.makeText(MapsActivity.this, res, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapsActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
            }

            String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(new Date());
            if ((log != null)&&(!res.equals("")) ){
                log += "(Server)" + timeStamp + "   : " + res + "\n\n";
            } else if ((log == null)&&(!res.equals(""))){
                log = "(Server)" + timeStamp + "   : " + res + "\n\n";
            }

            try {
                JSONObject jsonObj = new JSONObject(res);
                status = jsonObj.getString("status").toString();
                if (status.equals("ok")) {
                    longitude = Double.parseDouble(jsonObj.optString("longitude").toString());
                    latitude = Double.parseDouble(jsonObj.optString("latitude").toString());
                    token = jsonObj.optString("token").toString();
                    updateMap();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

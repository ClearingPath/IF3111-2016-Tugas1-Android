package com.adinb.tubes1_android;


import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
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
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {

    public static final int FINISH = 1;
    private LocationManager locationManager;
    private GoogleMap googleMap;
    private Location currentLocation;
    private String imagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private LinearLayout lin;
    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private Marker target;
    final String address = "167.205.34.132";
    final int port = 3111;

    String request = "{\"com\":\"req_loc\",\"nim\":\"13513058\"}";
    String response;

    Toast toast;
    JSONObject responseJson;
    JSONObject answerJson;

    Double longitude;
    Double lat;

    final int LOCATION_REQUEST_CODE = 1;
    final int STORAGE_REQUEST_CODE = 2;
    final int LOCATION_RESULT = 3;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.adinb.tubes1_android/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
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
                Uri.parse("android-app://com.adinb.tubes1_android/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class RequestTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = null;
            try {
                socket = new Socket(address, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(request); // Send request
                response = br.readLine(); // Receive response

            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d("HOST", "UHOST");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("IO", "IOException:" + e.toString());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Toast
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Log.d("RESPONSE", response);
            if (response != null) {
                Log.d("Response2", response);
                toast = Toast.makeText(context, response, duration);
                toast.show();
            } else {
                Log.d("Response2", "NULL");
                toast = Toast.makeText(context, "NULL", duration);
                toast.show();
            }

            if (response != null) {
                try {
                    responseJson = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    // TODO Terbalik
                    lat = responseJson.getDouble("longitude");
                    longitude = responseJson.getDouble("latitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                target = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longitude)).title("Next Target"));
            }
        }
    }

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
            lin.setPadding(0, 0, 0, 16);
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
        lin.setPadding(0, 0, 0, 16);
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


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = savePhoto();
            } catch (IOException e) {
                Log.d("IO", e.toString());
            }

            if (photoFile != null) {
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

    public void submitAnswer(View view) {
        Intent submitIntent = new Intent(this, SubmitAnswer.class).putExtra(Intent.EXTRA_TEXT, response);
        startActivityForResult(submitIntent, LOCATION_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_RESULT) {
            String result = data.getStringExtra("json");
            if (resultCode == FINISH) {
                String status = null;
                try {
                    status = new JSONObject(result).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equals("finish")){
                    target.remove();
                    Toast toast2 = Toast.makeText(getApplicationContext(), "FINISH!", Toast.LENGTH_LONG);
                    toast2.show();
                    setTitle("FINISH!");
                }
                else if (status.equals("ok")){
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Good Job! Continue with next target.", Toast.LENGTH_LONG);
                    toast2.show();

                    try {
                        responseJson = new JSONObject(result);
                        target.setPosition(new LatLng(responseJson.getDouble("latitude"), responseJson.getDouble("longitude")));
                        answerJson = new JSONObject();

                        // TODO terbalik
                        answerJson.put("longitude", responseJson.getDouble("latitude"));
                        answerJson.put("latitude", responseJson.getDouble("longitude"));
                        answerJson.put("token", responseJson.getString("token"));

                        response = answerJson.toString();
                        Log.d("RESPONSE", response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (status.equals("wrong_answer")){
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Sorry, wrong answer.", Toast.LENGTH_LONG);
                    toast2.show();
                }
                else if (status.equals("back")){
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Why going back?", Toast.LENGTH_LONG);
                    toast2.show();
                }

            } else {
            }
        }
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
        new RequestTask().execute(); // First request

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
    protected void onPause() {
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

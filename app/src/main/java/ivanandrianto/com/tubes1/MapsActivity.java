package ivanandrianto.com.tubes1;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import com.google.android.gms.location.LocationListener;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,SensorEventListener {

    /* Google Maps */
    private GoogleMap map;
    private double latitude, longitude;

    /* Server */
    private String serverResponse;
    private String address;
    private int port;
    private String response = "";
    private String token = "";
    private String status = "";
    final String nim = "13513039";
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLng cur_location;

    /* Censored */
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean accelerometerSet = false;
    private boolean magnetometerSet = false;
    private float[] accelerometerValue = new float[3];
    private float[] magnetometerValue = new float[3];
    private float curDegree = 0f;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private ImageView compass;

    private double currentLatitude, currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /* Check Connection */
        if(!checkInternetConenction()){
            open();
            Log.i("Application", "Not Connected");
        } else {
            Log.i("Application", "Connected");
            /* Get initial latitude and longitude from server */
            address = getResources().getString(R.string.address);
            port = Integer.parseInt(getResources().getString(R.string.port));
            JSONObject json = new JSONObject();
            try{
                if(savedInstanceState == null){
                    String mydate;
                    json.put("com", "req_loc");
                    json.put("nim", "13513039");
                    mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.i("Activity", "Client: " + json.toString() + " " + "date : " + mydate);
                    String response = new SocketClient(address,port,json).execute().get();
                    Toast.makeText(getApplicationContext(), "Response: " + response, Toast.LENGTH_LONG).show();
                    mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.i("Activity", "Server: " +  response + " " + "date : " + mydate);

                    JSONObject jsonObject = new JSONObject(response);
                    //sementara dibalik
                    latitude = Double.parseDouble(jsonObject.optString("longitude").toString());
                    longitude = Double.parseDouble(jsonObject.optString("latitude").toString());
                    token = jsonObject.optString("token").toString();
                    status = jsonObject.optString("status").toString();
                    if(status.equals("err")){
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                    if(token.length()<1){
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }

                } else {
                    latitude = savedInstanceState.getDouble("latitude");
                    longitude = savedInstanceState.getDouble("longitude");
                    token = savedInstanceState.getString("token");
                    status = savedInstanceState.getString("status");
                }
            } catch (JSONException e){
                e.printStackTrace();
            } catch (ExecutionException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        /* Button camera */
        ImageButton btnCamera = (ImageButton)findViewById(R.id.button1);
        btnCamera.setImageResource(R.drawable.camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Camera.class);
                startActivity(intent);
            }
        });

        /* Button message */
        ImageButton btnMessage = (ImageButton)findViewById(R.id.button2);
        btnMessage.setImageResource(R.drawable.message);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("finish")) {
                    Toast.makeText(getApplicationContext(), "Anda sudah finish", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("latitude", String.valueOf(latitude));
                    extras.putString("longitude", String.valueOf(longitude));
                    extras.putString("token", token);
                    intent.putExtras(extras);
                    startActivityForResult(intent, 1);
                }
            }
        });

        /* Sensor */
        compass = (ImageView)findViewById(R.id.compass_pointer);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        /* Fragment */
        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.map);
        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
        map = supportmapfragment.getMap();
        supportmapfragment.getMapAsync(this);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putDouble("longitude", longitude);
        savedInstanceState.putDouble("latitude", latitude);
        savedInstanceState.putString("token", token);
        savedInstanceState.putString("status", status);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        LatLng itb = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(itb).title("Marker in ITB"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(itb).zoom(16).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    protected void onStart(){
        super.onStart();
    }

    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
        //stopLocationUpdates();
    }

    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "reverse_portrait";
            default:
                return "reverse_landscape";
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor == accelerometer){
            System.arraycopy(event.values, 0, accelerometerValue, 0, event.values.length );
            accelerometerSet = true;
        } else if(event.sensor == magnetometer){
            System.arraycopy(event.values, 0, magnetometerValue, 0, event.values.length );
            magnetometerSet = true;
        }
        if(accelerometerSet && magnetometerSet){
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValue, magnetometerValue);
            SensorManager.getOrientation(rotationMatrix,orientation);
            float radian = orientation[0];
            float degree = (float)(Math.toDegrees(radian)+360)%360;

            String orientation = getRotation(this);
            switch(orientation){
                case "landscape":
                    degree+=90;
                    break;
                case "reverse_landscape":
                    degree-=90;
                    break;
                case "reverse_portrait":
                    degree-=180;
                    break;
            }

            RotateAnimation ra = new RotateAnimation(
                    curDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            );
            ra.setDuration(250);
            ra.setFillAfter(true);
            compass.startAnimation(ra);
            curDegree = -degree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = data;
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        token = bundle.getString("token");
                        latitude = Double.parseDouble(bundle.getString("latitude"));
                        longitude = Double.parseDouble(bundle.getString("longitude"));
                        status = (bundle.getString("status"));
                        LatLng itb = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(itb).title("Marker in ITB"));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(itb).zoom(16).build();
                        map.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                    }
                }
                break;
            }
        }
    }

    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork!=null){
            // Check for network connections
            if ( activeNetwork.getState() == android.net.NetworkInfo.State.CONNECTED ||
                    activeNetwork.getState() == android.net.NetworkInfo.State.CONNECTING ) {
                Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
                return true;
            } else if ( activeNetwork.getState() == android.net.NetworkInfo.State.DISCONNECTED ) {
                Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
                return false;
            } else {
                return false;
            }
        } else {
            Toast.makeText(this, " No active network ", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("No connection. Retry?");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
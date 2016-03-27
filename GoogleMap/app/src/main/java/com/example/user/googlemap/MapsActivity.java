package com.example.user.googlemap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {

    private GoogleMap mMap; 
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private ImageView arrowMap;
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

    Socket socket = null;
    private final String ServerIP = "167.205.34.132";
    private final int ServerPort = 3111;
    private String token, status;
    private double longitude, latitude;
    private boolean firstConnect = true;
    Marker answerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        arrowMap = (ImageView) findViewById(R.id.arrowMap);

        if(firstConnect == true)
            new connection().execute();
        else {
            SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
            longitude = Double.parseDouble(sp.getString("latitude", null));
            latitude = Double.parseDouble(sp.getString("longitude", null));
        }
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
        mMap.getUiSettings().setCompassEnabled(false);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
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

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
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
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation ra = new RotateAnimation(mCurrentDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            arrowMap.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void openCamera(View view) {
        Toast.makeText(MapsActivity.this, "Open The camera", Toast.LENGTH_SHORT).show();

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void openAnswerForm(View view) {
        Toast.makeText(MapsActivity.this, "Open the Answer Form", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,AnswerActivity.class);
        startActivityForResult(intent, 18);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        String value = "";
        if (requestCode == 18) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    value = extras.getString("status");
                }

                if (value.equals("ok")){
                    SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                    longitude = Double.parseDouble(sp.getString("latitude", null));
                    latitude = Double.parseDouble(sp.getString("longitude", null));

                    LatLng map = new LatLng(latitude, longitude);

                    answerLocation.remove();
                    answerLocation = mMap.addMarker(new MarkerOptions().position(map).title("Dimana hayoo?"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(map));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        }
    }

    public class connection extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... args) {

            try {
                socket = new Socket(ServerIP, ServerPort);
                JSONObject json = new JSONObject();
                json.put("com", "req_loc");
                json.put("nim", "13513005");
                System.err.println("Connected");

                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                if (firstConnect){
                    writeToFile("Client:\n" + json + "\n" + date + "\n");
                } else {
                    String log = readFromFile();
                    writeToFile(log + "\nClient:\n" + json + "\n" + date + "\n");
                }

                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(json.toString());
                out.flush();
                System.err.println(json);

                String res = in.readLine();
                System.err.println(res);
                return res;

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
        protected void onPostExecute(String response) {
            try {
                (Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG)).show();

                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                String log = readFromFile();
                writeToFile(log + "\nServer:\n" + response + "\n" + date + "\n");

                JSONObject jsonResponse = new JSONObject(response);
                status = jsonResponse.getString("status");
                latitude = jsonResponse.getDouble("longitude");
                longitude = jsonResponse.getDouble("latitude");
                token = jsonResponse.getString("token");

                date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                log = readFromFile();
                writeToFile(log + "\nServer:\n" + jsonResponse + "\n" + date + "\n");

                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("token",token);
                edit.commit();
                firstConnect = false;

                LatLng map = new LatLng(latitude, longitude);
                answerLocation = mMap.addMarker(new MarkerOptions().position(map).title("Answer Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(map));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void writeToFile(String data)
    {
        String path = Environment.getExternalStorageDirectory() + File.separator  + "PBD";
        // Create the folder.
        File folder = new File(path);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, "log.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        String path = Environment.getExternalStorageDirectory() + File.separator  + "PBD";

        //Get the text file
        File file = new File(path,"log.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        ret = text.toString();
        return ret;
    }
}

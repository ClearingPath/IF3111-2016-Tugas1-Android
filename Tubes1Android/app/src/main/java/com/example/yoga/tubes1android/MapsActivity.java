package com.example.yoga.tubes1android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener,
        SensorEventListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private GoogleMap mMap;
    private double targetlongitude, targetlatitude;
    private String status, nim, token;
    private Socket socket;
    PrintWriter out;
    BufferedReader input;
    String json, response;
    double mylongitude, mylatitude;
    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton one = (ImageButton) findViewById(R.id.imageButton);
        one.setOnClickListener(this); // calling onClick() method
        ImageButton two = (ImageButton) findViewById(R.id.imageButton2);
        two.setOnClickListener(this);

        image = (ImageView) findViewById(R.id.imageViewCompass);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //new Connect().execute("");

    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageButton:
                dispatchTakePictureIntent();
                break;

            case R.id.imageButton2:

                // Do something in response to button
                Intent intent = new Intent(this, SubmitAnswer.class);
                //Bundle b = new Bundle();
                intent.putExtra("token", token);
                intent.putExtra("nim",nim);
                intent.putExtra("latitude", targetlatitude);
                intent.putExtra("longitude", targetlongitude);
                startActivityForResult(intent, 1);
                LatLng target = new LatLng(targetlongitude, targetlatitude);
                mMap.addMarker(new MarkerOptions().position(target).title("target"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 17));
                break;


            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    token=data.getStringExtra("token");
                    nim=data.getStringExtra("nim");
                    targetlatitude=data.getDoubleExtra("latitude", 1);
                    targetlongitude=data.getDoubleExtra("longitude",1.0);
                }
                break;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        RotateAnimation ra = new RotateAnimation(currentDegree,-degree,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class Connect extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

                try {
                        socket = new Socket("167.205.34.132", 3111);
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        input= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                try {
                    json = "";
                    //build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("com", "req_loc");
                    jsonObject.put("nim", "13513030");
                    //convert JSONObject to JSON to String
                    json = jsonObject.toString();
                    System.out.println(json);
                    out.println(json);
                    response=input.readLine();
                    System.out.println(response);
                    final JSONObject obj=new JSONObject(response);
                    token=obj.getString("token");
                    status=obj.getString("status");
                    nim=obj.getString("nim");
                    targetlatitude=obj.getDouble("latitude");
                    targetlongitude=obj.getDouble("longitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            LatLng target = new LatLng(targetlongitude, targetlatitude);
            mMap.addMarker(new MarkerOptions().position(target).title("target"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target,17));
            Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

        }
    }

}

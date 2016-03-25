package com.example.mochginanjarbusiri.busiri_map;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MARKER = 200;
    String response = "";
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        sendRequest();
    }

    public void openSubmit(View view)
    {
        Intent intent = new Intent(this, SubmitActivity.class);
        startActivity(intent);
    }

    public void captureImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        /*} else if (requestCode == MARKER) {
            if (resultCode == RESULT_OK) {
                *//*lat = json.getDouble("latitude");
                longit = json.getDouble("longitude");
                Log.d("Laatitude", lat.toString());
                Log.d("Longitude", longit.toString());*//*
                LatLng itb = new LatLng(-6.890356, 107.610359);
                mMap.addMarker(new MarkerOptions().position(itb).title("Institut Teknologi Bandung"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(itb));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(itb, 20.0f));
                Intent intent = new Intent(this, MapsActivity.class);
                startActivityForResult(intent, MARKER);
            }*/
        }
    }

    public void sendRequest()
    {
        JSONObject json = new JSONObject();

        try {
            json.put("com", "req_loc");
            json.put("nim", "13513111");
            SocketClient socket = new SocketClient(json.toString(), mMap);
            socket.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

package com.luqman.androidmap;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import android.provider.MediaStore;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        longitude = Double.parseDouble((String) bundle.getString("longitude"));
        latitude = Double.parseDouble((String) bundle.getString("latitude"));
        token = (String) bundle.getString("token");
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
        LatLng marker = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(marker).title("Marker in some place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
    }

    /** Called when the user clicks the Answer button */
    public void answer(View view) {
        Intent intent = new Intent(this, Answer.class);
        startActivity(intent);
    }

    /** Called when user want to use the camera */
    public void capture(View view) {
        int REQUEST_IMAGE_CAPTURE = 1;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean success = takePictureIntent.resolveActivity(getPackageManager()) != null;
        Log.d(TAG, "camera opened " + success);
        if (success) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}

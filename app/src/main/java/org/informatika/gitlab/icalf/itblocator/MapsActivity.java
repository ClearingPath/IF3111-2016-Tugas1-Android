package org.informatika.gitlab.icalf.itblocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
  private GoogleMap mMap;
  private UiSettings mUiSettings;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    mUiSettings = mMap.getUiSettings();

    mUiSettings.setZoomControlsEnabled(true);
    mUiSettings.setCompassEnabled(true);

    LatLng sydney = new LatLng(-6.892134, 107.609959);
    mMap.addMarker(new MarkerOptions().position(sydney).title("Challenge location"));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
  }

  public void callCamera(View view) {
    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, 0);
  }

  public void answerLocation(View view) {
    Intent intent = new Intent(this, AnswerActivity.class);
    startActivity(intent);
  }
}

package org.informatika.gitlab.icalf.itblocator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MapsActivity extends FragmentActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
  private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

  private GoogleMap mMap;
  private UiSettings mUiSettings;
  private Marker location;
  private String token;
  private boolean mPermissionDenied = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                                          .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    // TODO : MAKE ASYNC TASK FOR NET JOB
    // <<<<<< BEGIN
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    // >>>>>> END
  }

  private void refreshLocation(JSONObject response) throws JSONException {
    double longitude = response.getDouble("longitude");
    double latitude = response.getDouble("latitude");
    token = response.getString("token");

    location.setPosition(new LatLng(longitude, latitude));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(location.getPosition()));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1) {
      if (resultCode == Activity.RESULT_OK) {
        String responseJSON = data.getStringExtra("result");

        try {
          JSONObject response = (JSONObject) new JSONTokener(responseJSON).nextValue();

          String status = response.getString("status");
          if (status.equals("wrong_answer")) {
            notify("Wrong answer. Try again");
            token = response.getString("token");
          } else {
            if (status.equals("ok")) {
              refreshLocation(response);
              notify("Nice guess. Try another one");
            } else {
              notify("Great! You just finished the quest!");
              finish();
            }
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void notify(String notification) {
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(this, notification, duration);
    toast.show();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mUiSettings = mMap.getUiSettings();
    mUiSettings.setZoomControlsEnabled(true);
    mUiSettings.setCompassEnabled(true);

    try {
      Socket sock = new Socket(AnswerActivity.host, AnswerActivity.port);
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
      InputStream in = sock.getInputStream();

      /* Initial request */
      String request =
        new JSONObject()
                .put("com", "req_loc")
          .put("nim", AnswerActivity.NIM)
          .toString();

      out.write(request);
      out.newLine();
      out.flush();

      String responseJSON = "";
      ByteArrayOutputStream stream = new ByteArrayOutputStream(AnswerActivity.MAX);
      byte[] buf = new byte[AnswerActivity.MAX];
      int bufBytes;
      while ((bufBytes = in.read(buf)) != -1) {
        stream.write(buf, 0, bufBytes);
        responseJSON += stream.toString();
      }

      JSONObject response = (JSONObject) new JSONTokener(responseJSON).nextValue();

      out.close();
      in.close();
      sock.close();

      location = mMap.addMarker(new MarkerOptions()
        .position(new LatLng(0.0, 0.0))
        .title("Challenge location"));
      refreshLocation(response);
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    enableMyLocation();
  }

  public void callCamera(View view) {
    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, 0);
  }

  public void answerLocation(View view) {
    Intent intent = new Intent(this, AnswerActivity.class);
    intent.putExtra("token", token);
    startActivityForResult(intent, 1);
  }

  private void enableMyLocation() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      // Permission to access the location is missing.
      PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
              Manifest.permission.ACCESS_FINE_LOCATION, true);
    } else if (mMap != null) {
      // Access to the location has been granted to the app.
      mMap.setMyLocationEnabled(true);
    }
  }

  @Override
  public boolean onMyLocationButtonClick() {
    return false;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
      return;
    }

    if (PermissionUtils.isPermissionGranted(permissions, grantResults,
            Manifest.permission.ACCESS_FINE_LOCATION)) {
      // Enable the my location layer if the permission has been granted.
      enableMyLocation();
    } else {
      // Display the missing permission error dialog when the fragments resume.
      mPermissionDenied = true;
    }
  }

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    if (mPermissionDenied) {
      // Permission was not granted, display error dialog.
      showMissingPermissionError();
      mPermissionDenied = false;
    }
  }

  /**
   * Displays a dialog with error message explaining that the location permission is missing.
   */
  private void showMissingPermissionError() {
    PermissionUtils.PermissionDeniedDialog
            .newInstance(true).show(getSupportFragmentManager(), "dialog");
  }
}

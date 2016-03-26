package org.informatika.gitlab.icalf.itblocator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.View;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
  private GoogleMap mMap;
  private UiSettings mUiSettings;
  private Marker location;
  private String token;

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

          if (response.getString("status").equals("ok")) {
            refreshLocation(response);
            // TODO : MAKE TOAST LOCATION CORRECT MOVE TO THE NEXT CHALLENGE
          } else {          // status = wrong_answer
            // TODO : MAKE TOAST WRONG ANSWER
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mUiSettings = mMap.getUiSettings();
    mUiSettings.setZoomControlsEnabled(true);
    mUiSettings.setCompassEnabled(true);

    try {
      Socket sock = new Socket(AnswerActivity.host, AnswerActivity.port);
      DataOutputStream out = new DataOutputStream(sock.getOutputStream());
      DataInputStream in = new DataInputStream(sock.getInputStream());

      String request =
        new JSONObject()
          .put("com", "req_loq")
          .put("nim", AnswerActivity.NIM)
          .toString();
      System.out.println(request);
      out.writeUTF(request);
      String responseJSON = in.readUTF();
      System.out.println(responseJSON);
      JSONObject response = (JSONObject) new JSONTokener(responseJSON).nextValue();

      location = mMap.addMarker(new MarkerOptions()
        .position(new LatLng(0.0, 0.0))
        .title("Challenge location"));
      refreshLocation(response);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
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
}

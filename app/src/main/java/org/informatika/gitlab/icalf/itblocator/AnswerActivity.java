package org.informatika.gitlab.icalf.itblocator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class AnswerActivity extends AppCompatActivity {
  public static final int MAX = 2048;
  public static final long NIM = 13513004;
  public static final String host = "167.205.34.132";
  public static final short port = 3111;

  private String token;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_answer);
  }

  public void submitLocation(View view) {
    String[] locationList =
            {"gku_barat", "gku_timur", "intel", "cc_barat", "cc_timur", "dpr", "sunken", "perpustakaan", "pau", "kubus"};
    Spinner spinnerLocation = (Spinner) findViewById(R.id.spinner_location);
    String choice = locationList[spinnerLocation.getSelectedItemPosition()];
    String responseJSON = "";

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
      /* Get best last location based on all providers */
      LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
      List<String> providers = service.getProviders(true);
      Location curLocation = null;
      for (String provider : providers) {
        Location l = service.getLastKnownLocation(provider);
        if (l == null) {
          continue;
        }
        if (curLocation == null || l.getAccuracy() < curLocation.getAccuracy()) {
          curLocation = l;
        }
      }

      try {
        Socket sock = new Socket(AnswerActivity.host, AnswerActivity.port);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        InputStream in = sock.getInputStream();

        String request =
                new JSONObject()
                        .put("com", "answer")
                        .put("nim", AnswerActivity.NIM)
                        .put("longitude", curLocation.getLongitude())
                        .put("latitude", curLocation.getLatitude())
                        .put("answer", choice)
                        .put("token", token)
                        .toString();

        out.write(request);
        out.newLine();
        out.flush();

        ByteArrayOutputStream stream = new ByteArrayOutputStream(MAX);
        byte[] buf = new byte[MAX];
        int bufBytes;
        while ((bufBytes = in.read(buf)) != -1) {
          stream.write(buf, 0, bufBytes);
          responseJSON += stream.toString();
        }

        out.close();
        in.close();
        sock.close();
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Intent returnIntent = new Intent();
    returnIntent.putExtra("result", responseJSON);
    setResult(Activity.RESULT_OK, returnIntent);
    finish();
  }
}

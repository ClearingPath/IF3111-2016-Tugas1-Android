package com.example.calvin.pbd;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private GoogleMap mMap;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new SocketTask().execute();

        /*
        ((Button)findViewById(R.id.requestButton)).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("MyApp", "requesting");
                    }
                }
        );
        */

        ((ImageButton)findViewById(R.id.cameraButton)).setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }
        );

        ((ImageButton)findViewById(R.id.answerButton)).setOnClickListener(
                new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MapsActivity.this, AnswerActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    private class SocketTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... args) {
            try {
                Socket socket = new Socket("167.205.34.132", 3111);

                JSONObject request = new JSONObject();
                request.put("com", "req_loc");
                request.put("nim", "13513077");

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                PrintWriter out = new PrintWriter(outputStream);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                out.println(request.toString());
                out.flush();
                String response = in.readLine();
                return response;
            }
            catch (UnknownHostException e) {
                Log.d("MyApp", e.toString());
            }
            catch (IOException e) {
                Log.d("MyApp", e.toString());
            }
            catch (JSONException e) {
                Log.d("MyApp", e.toString());
            }
            return null;
        }

        protected void onPostExecute(String response) {
            try {
                JSONObject responseJSON = new JSONObject(response);
            }
            catch (JSONException e) {
                Log.d("MyApp", e.toString());
            }
        }
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "PBD");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("PBD", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng itb = new LatLng(-6.8899, 107.6100);
        mMap.addMarker(new MarkerOptions().position(itb).title("Institut Teknologi Bandung"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(itb));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Image saved\n", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                //
            } else {
                Toast.makeText(this, "Image capture failed\n", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

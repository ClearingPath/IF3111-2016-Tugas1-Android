package com.example.calvin.pbd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
    public static final int ANSWER_CODE = 2;
    public static final int OK = 3;
    public static final int FINISH = 4;
    public static final int WRONG = 5;
    private GoogleMap mMap;
    private Uri fileUri;
    private boolean firstRequest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (firstRequest)
            new RequestLocation().execute();

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
                    startActivityForResult(intent, ANSWER_CODE);
                }
            }
        );
    }

    private class RequestLocation extends AsyncTask<Void, Void, String> {
        @Override
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
                socket.close();
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

        @Override
        protected void onPostExecute(String response) {
            try {
                Toast.makeText(MapsActivity.this, response, Toast.LENGTH_LONG).show();
                JSONObject responseJSON = new JSONObject(response);

                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("token", responseJSON.getString("token"));
                editor.commit();

                firstRequest = false;
                setLocation(responseJSON.getDouble("longitude"), responseJSON.getDouble("latitude")); //Jangan lupa dibalik sebelum merge
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
        mMap.getUiSettings().setCompassEnabled(false);
    }

    public void setLocation(double latitude, double longitude) {
        LatLng loc = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(loc).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Image saved\n", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Capture image canceled\n", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Image capture failed\n", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == ANSWER_CODE) {
            if (resultCode == FINISH) {
                mMap.clear();
                Toast.makeText(this, "Finish!\n", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == WRONG)
                Toast.makeText(this, "Wrong answer, try again\n", Toast.LENGTH_SHORT).show();
            else if (resultCode == OK) {
                mMap.clear();
                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                setLocation(Double.longBitsToDouble(sp.getLong("latitude", -1)), Double.longBitsToDouble(sp.getLong("longitude", -1)));
                Toast.makeText(this, "Answer correct, move to next location\n", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
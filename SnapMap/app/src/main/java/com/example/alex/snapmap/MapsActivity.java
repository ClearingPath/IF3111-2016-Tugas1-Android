package com.example.alex.snapmap;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    private ImageButton b1, b2;
    private Spinner spinner;
    private ImageView image;
    private JSONObject json;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        image = (ImageView) findViewById(R.id.imageViewCompass);
        b1 = (ImageButton) findViewById(R.id.camera);
        b2 = (ImageButton) findViewById(R.id.chat);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswerActivity(v);
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
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
        String jsonParam = "{\"status\":\"ok\", \"nim\":\"13512999\",\"longitude\":\"107.610101\",\"latitude\":\"-6.890535\",\"token\":\"21nu2f2n3rh23diefef23hr23ew\"}";
        try {
            json = new JSONObject();
            json.put("nim", "13513023");
            json.put("com", "req_loc");
        } catch (Throwable t) {
            Log.e("MyApp", "Could not parse malformed JSON: \"" + json.toString() + "\"");
        }

        new connectSocket().execute();
           /* socket = new Socket("167.205.34.132", 3111);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF(json.toString());

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Response");

            // set dialog message
            alertDialogBuilder
                    .setMessage(dataInputStream.readUTF())
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();*/

        double Lat = 0;
        double Lng = 0;
        try {
            JSONObject obj = new JSONObject(jsonParam);
            Lat = obj.getDouble("latitude");
            Lng = obj.getDouble("longitude");
        } catch (Throwable t) {
            Log.e("MyApp", "Could not parse malformed JSON: \"" + jsonParam + "\"");
        }



        GoogleMap mMap = googleMap;
        // Add a marker in Labtek V and move the camera
        LatLng labtekv = new LatLng(Lat, Lng);
        Log.d("MyApp", String.valueOf(Lat));
        Log.d("MyApp", String.valueOf(Lng));
        mMap.addMarker(new MarkerOptions().position(labtekv).title("Marker in Labtek V"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(labtekv, 15));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void submitAnswerActivity(View view) {
        Intent intent = new Intent(this, SubmitActivity.class);
        startActivity(intent);
    }

    private class connectSocket extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            Log.d("MYAPP", "TEST");
            try {
                socket = new Socket("167.205.34.132", 3111);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(json.toString());
                return dataInputStream.readUTF();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MapsActivity.this);

            // set title
            alertDialogBuilder.setTitle("Response");
            Log.d("MYAPP", result);
            Log.d("MYAPP", "TEST");

            // set dialog message
            alertDialogBuilder
                    .setMessage(result)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}

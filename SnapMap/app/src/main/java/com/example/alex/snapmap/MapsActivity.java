package com.example.alex.snapmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    private GoogleMap mMap;
    private ImageButton b1, b2;
    private ImageView image;
    private JSONObject json;
    private JSONObject jsonResponse;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private double Lat,Lng = 0;
    private String token = null;
    private SharedPreferences myprefs;
    private boolean init = true;
    public static final int ANSWER_CODE = 2;
    public static final int OK = 3;
    public static final int FINISH = 4;
    public static final int WRONG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        image = (ImageView) findViewById(R.id.imageViewCompass);
        b1 = (ImageButton) findViewById(R.id.camera);
        b2 = (ImageButton) findViewById(R.id.chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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

        Log.d("TEST", String.valueOf(init));

        if(init)
            new connectSocket().execute();
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
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    private void setLocation() {
        LatLng marker = new LatLng(Lat, Lng);
        mMap.addMarker(new MarkerOptions().position(marker).title("Designated Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
    }

    private void setLocation2() {
        myprefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        Lat = Double.valueOf(myprefs.getString("latitude", null));
        Lng = Double.valueOf(myprefs.getString("longitude", null));
        Log.d("TEST",String.valueOf(Lat));
        LatLng marker = new LatLng(Lat, Lng);
        mMap.addMarker(new MarkerOptions().position(marker).title("Designated Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void submitAnswerActivity(View view) {
        Intent intent = new Intent(this, SubmitActivity.class);
        startActivityForResult(intent, ANSWER_CODE);
    }




    private class connectSocket extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Socket socket = null;
            OutputStream dataOutputStream = null;
            InputStream dataInputStream = null;

            try {
                json = new JSONObject();
                json.put("nim", "13513023");
                json.put("com", "req_loc");
            } catch (Throwable t) {
                Log.e("MyApp", "Could not parse malformed JSON: \"" + json.toString() + "\"");
            }

            Log.d("COMMLOG", json);
            try {
                socket = new Socket("167.205.34.132", 3111);
                dataOutputStream = socket.getOutputStream();
                dataInputStream = socket.getInputStream();
                PrintWriter writer = new PrintWriter(dataOutputStream);
                writer.println(json.toString());
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
                String response = reader.readLine();

                return response;
            } catch (UnknownHostException e) {
                Log.d("MyApp", e.toString());
                cancel(true);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("MyApp", e.toString());
                cancel(true);
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Log.d("MyApp", e.toString());
                        cancel(true);
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
        protected void onCancelled() {
            new AlertDialog.Builder(MapsActivity.this)
                    .setTitle("Error")
                    .setMessage("Cannot connect to the server, restarting app now ..")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            recreate();
                        }
                    })
                    .show();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jsonResponse = new JSONObject(result);
                Lng = jsonResponse.getDouble("longitude");  // TERBALIK
                Lat = jsonResponse.getDouble("latitude");
            } catch (Throwable t) {
                Log.e("MyApp", "Could not parse malformed JSON: \"" + json.toString() + "\"");
            }


            setLocation();


            SharedPreferences myprefs= getSharedPreferences("user", Context.MODE_PRIVATE);
            try {
                myprefs.edit().putString("token", jsonResponse.getString("token")).commit();
            } catch (Throwable t) {
                Log.e("MyApp", "Could not parse \"" + jsonResponse.toString() + "\"");
            }

            Log.d("COMMLOG", result);
            init = false;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MapsActivity.this);

            // set title
            alertDialogBuilder.setTitle("Response");
            // set dialog message
            alertDialogBuilder
                    .setMessage(result)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FINISH) {
            mMap.clear();
            Toast.makeText(this, "Finish!", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == WRONG) {
            Toast.makeText(this, "Wrong answer, try again", Toast.LENGTH_SHORT).show();
        } else if (resultCode == OK) {
            mMap.clear();
            setLocation2();
            Toast.makeText(this, "Answer correct, move to next location", Toast.LENGTH_SHORT).show();
        }
    }
}

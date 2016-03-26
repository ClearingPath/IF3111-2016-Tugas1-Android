package com.example.calvin.pbd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int ANSWER_CODE = 2;
    public static final int OK = 3;
    public static final int FINISH = 4;
    public static final int WRONG = 5;
    public static ArrayList<String> commLog = new ArrayList<>();
    public static String serverIP = "167.205.34.132";

    private boolean firstRequest = true;
    private GoogleMap mMap;
    private Uri fileUri;
    private boolean logVisible = false;
    private ImageView image;
    private double currentDegree = 0f;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    private float valuesAccelerometer[];
    private float valuesMagneticField[];
    private float matrixR[];
    private float matrixI[];
    private float matrixValues[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setTitle("Map");

        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];
        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];

        image = (ImageView) findViewById(R.id.compass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        ((Button) findViewById(R.id.logButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, commLog);
                ListView lv = (ListView)findViewById(R.id.logList);
                lv.setBackgroundColor(Color.WHITE);
                lv.setAdapter(arrayAdapter);
                if (!logVisible)
                    lv.setVisibility(View.VISIBLE);
                else
                    lv.setVisibility(View.INVISIBLE);
                logVisible = !logVisible;
            }
        });

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

        if (firstRequest)
            new RequestLocation().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, sensorAccelerometer);
        sensorManager.unregisterListener(this, sensorMagneticField);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: for (int i = 0; i < 3; i++) valuesAccelerometer[i] = event.values[i]; break;
            case Sensor.TYPE_MAGNETIC_FIELD: for(int i = 0; i < 3; i++) valuesMagneticField[i] = event.values[i]; break;
        }

        boolean success = SensorManager.getRotationMatrix(matrixR, matrixI, valuesAccelerometer, valuesMagneticField);
        if(success) {
            SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_Z, matrixR);
            SensorManager.getOrientation(matrixR, matrixValues);
        }

        double degree = Math.toDegrees(matrixValues[0]);
        RotateAnimation ra = new RotateAnimation(Float.parseFloat(Double.toString(currentDegree)), Float.parseFloat(Double.toString(-degree)), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not used
    }

    private class RequestLocation extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... args) {
            try {
                Socket socket = new Socket(serverIP, 3111);

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

                Calendar c = Calendar.getInstance();
                c.getTime().toString();

                commLog.add(c.getTime().toString() + " " + request.toString());
                commLog.add(c.getTime().toString() + " " + response);
                return response;
            }
            catch (UnknownHostException e) {
                Log.d("MyApp", e.toString());
                cancel(true);
            }
            catch (IOException e) {
                Log.d("MyApp", e.toString());
                cancel(true);
            }
            catch (JSONException e) {
                Log.d("MyApp", e.toString());
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                Toast.makeText(MapsActivity.this, response, Toast.LENGTH_LONG).show();
                JSONObject responseJSON = new JSONObject(response);

                double latitude = responseJSON.getDouble("latitude");
                double longitude = responseJSON.getDouble("longitude");

                SharedPreferences sp = getSharedPreferences("PBD", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("token", responseJSON.getString("token"));
                editor.commit();

                setLocation(latitude, longitude);
                firstRequest = false;
            }
            catch (JSONException e) {
                Log.d("MyApp", e.toString());
            }
        }

        @Override
        protected void onCancelled() {
            new AlertDialog.Builder(MapsActivity.this)
                    .setTitle("Error")
                    .setMessage("Something went wrong, restarting the app...")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            recreate();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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
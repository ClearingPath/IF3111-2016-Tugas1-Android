package com.bomba.conte.tubes1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int SUBMIT_CODE = 2;
    public static final int OK = 3;
    public static final int WRONG = 4;
    public static final int DONE = 5;
    public static String serverAddress = "167.205.34.132";
    public static int serverPort = 3111;
    private JSONObject obj;
    private boolean firstReq = true;
    private float currentDegree = 0f;
    String response = "";

    ImageButton camera, chat;
    ImageView compass;
    SensorManager manager;
    String currentPhotoPath;
    android.support.v7.widget.Toolbar titlebar;

    public void changeMarker(double latitude, double longitude, String tag) {
        LatLng loc = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(loc).title(tag));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);

        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);

        compass.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    private class Mediator extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {

            Socket socket = null;
            PrintWriter printer;
            BufferedReader reader;

            try {
                socket = new Socket(serverAddress, serverPort);

                printer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printer.println(obj.toString());
                Log.d("CommLog", obj.toString());
                printer.flush();
                response = reader.readLine();
                reader.close();
                Log.d("CommLog", response);

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreferences storage = getSharedPreferences("Maps", Activity.MODE_PRIVATE);
            SharedPreferences.Editor writer = storage.edit();

            try {
                JSONObject resultJSON;

                resultJSON = new JSONObject(result);

                writer.commit();
                writer.putString("Token", resultJSON.getString("token"));
                writer.putLong("Latitude", Double.doubleToRawLongBits(resultJSON.getDouble("latitude")));
                writer.putLong("Longitude", Double.doubleToRawLongBits(resultJSON.getDouble("longitude")));
                changeMarker(Double.longBitsToDouble(storage.getLong("Latitude", 53)), Double.longBitsToDouble(storage.getLong("Longitude", 13)), "Target");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            writer.commit();

            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("LESPON ALART!!!")
                    .setMessage(result)
                    .setPositiveButton("Tararengkiu Pak Seper", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alart = builder.create();
            alart.show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        titlebar = (android.support.v7.widget.Toolbar)findViewById(R.id.titlebar);
        setSupportActionBar(titlebar);
        getSupportActionBar().setTitle("Map");

        compass = (ImageView)findViewById(R.id.compassArrow);
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        chat = (ImageButton)findViewById(R.id.chatButton);
        chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent submitIntent = new Intent(MapsActivity.this, SubmitActivity.class);
                startActivityForResult(submitIntent, SUBMIT_CODE);
            }
        });

        camera = (ImageButton)findViewById(R.id.cameraButton);
        camera.setOnClickListener(new View.OnClickListener() {
                                     public void onClick(View v) {
                                         Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                             // Create the File where the photo should go
                                             File photoFile = null;
                                             try {
                                                 photoFile = createImageFile();
                                             } catch (IOException ex) {
                                                 // Error occurred while creating the File
                                             }
                                             // Continue only if the File was successfully created
                                             if (photoFile != null) {
                                                 takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                         Uri.fromFile(photoFile));
                                                 startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                                                 galleryAddPic();
                                             }
                                         }
                                     }
                                  }

        );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (firstReq) {
            try {
                obj = new JSONObject();
                obj.put("com", "req_loc");
                obj.put("nim", "13513013");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new Mediator().execute();
            firstReq = false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUBMIT_CODE) {
            if (resultCode == OK) {
                mMap.clear();
                Toast.makeText(this, "Correct! NEW LOCATION!", Toast.LENGTH_SHORT).show();
                SharedPreferences storage = getSharedPreferences("Maps", Activity.MODE_PRIVATE);
                changeMarker(Double.longBitsToDouble(storage.getLong("Latitude", -1)), Double.longBitsToDouble(storage.getLong("Longitude", -1)), "Target");
            }
            else if (resultCode == WRONG) {
                mMap.clear();
                Toast.makeText(this, "Too Bad! TRY AGAIN!", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == DONE) {
                mMap.clear();
                Toast.makeText(this, "Well Done", Toast.LENGTH_SHORT).show();
                SharedPreferences storage = getSharedPreferences("Maps", Activity.MODE_PRIVATE);
                changeMarker(Double.longBitsToDouble(storage.getLong("Latitude", -1)), Double.longBitsToDouble(storage.getLong("Longitude", -1)), "Safe House");
            }
        }
    }
}

package com.example.yoga.tubes1android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener,
        SensorEventListener {
    private GoogleMap mMap;
    private double targetlongitude, targetlatitude;
    private String status, nim, token;
    private Socket socket;
    PrintWriter out;
    BufferedReader input;
    String json, response;
    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    float[] gData = new float[3]; // accelerometer
    float[] mData = new float[3]; // magnetometer
    float[] rMat = new float[9];
    float[] iMat = new float[9];
    float[] orientation = new float[3];
    private float mAzimuth = 0f;
    String ip="167.205.34.132";
    int port=3111;
    String[] storageperms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    static ArrayList<String> log=new ArrayList<>();
    Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton one = (ImageButton) findViewById(R.id.imageButton);
        one.setOnClickListener(this); // calling onClick() method
        ImageButton two = (ImageButton) findViewById(R.id.imageButton2);
        two.setOnClickListener(this);
        Button log = (Button) findViewById(R.id.log);
        log.setOnClickListener(this);

        image = (ImageView) findViewById(R.id.imageViewCompass);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        new Connect().execute("");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, storageperms, 2);
            return;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                    return;
                }
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                return;
                }

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageButton:
                dispatchTakePictureIntent();
                break;

            case R.id.imageButton2:

                // Do something in response to button
                Intent intent = new Intent(this, SubmitAnswer.class);
                //Bundle b = new Bundle();
                intent.putExtra("token", token);
                intent.putExtra("nim",nim);
                intent.putExtra("latitude", targetlatitude);
                intent.putExtra("longitude", targetlongitude);
                startActivityForResult(intent, 1);
                LatLng target = new LatLng(targetlongitude, targetlatitude);
                mMap.addMarker(new MarkerOptions().position(target).title("target"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 17));
                break;

            case R.id.log:
                for(int i=0;i<log.size();i++){
                    Toast.makeText(getApplicationContext(), log.get(i), Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    token=data.getStringExtra("token");
                    nim=data.getStringExtra("nim");
                    targetlatitude=data.getDoubleExtra("latitude", 1);
                    targetlongitude=data.getDoubleExtra("longitude",1.0);
                    log.add(data.getStringExtra("json"));
                    log.add(data.getStringExtra("response"));
                }
                break;
            }
            case (2) :{
                    if (resultCode == RESULT_OK) {

                    }

                break;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Toast.makeText(getApplicationContext(), "photophile", Toast.LENGTH_LONG).show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 2);

            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = new File(storageDir.getPath()+File.separator+"IMG_" + timeStamp + ".jpg");

        Toast.makeText(getApplicationContext(), image.getPath(), Toast.LENGTH_LONG).show();

        return image;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] data;
        switch ( event.sensor.getType() ) {
            case Sensor.TYPE_ACCELEROMETER:
                gData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mData = event.values.clone();
                break;
            default: return;
        }

        if ( SensorManager.getRotationMatrix( rMat, iMat, gData, mData ) ) {
            mAzimuth= (float) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -mAzimuth,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            ra.setDuration(250);

            ra.setFillAfter(true);

            image.startAnimation(ra);
            currentDegree=mAzimuth*-1;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class Connect extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

                try {
                        socket = new Socket(ip, port);
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        input= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    } catch (UnknownHostException e) {
                    Toast.makeText(getApplicationContext(), "koneksi error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    finish();
                    startActivity(getIntent());
                    } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "koneksi error", Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                    finish();
                    startActivity(getIntent());
                    }

                try {
                    json = "";
                    //build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("com", "req_loc");
                    jsonObject.put("nim", "13513030");
                    //convert JSONObject to JSON to String
                    json = jsonObject.toString();
                    out.println(json);
                    log.add(c.getTime().toString()+" "+json);
                    response=input.readLine();
                    log.add(c.getTime().toString()+" "+response);
                    final JSONObject obj=new JSONObject(response);
                    token=obj.getString("token");
                    status=obj.getString("status");
                    nim=obj.getString("nim");
                    targetlatitude=obj.getDouble("latitude");
                    targetlongitude=obj.getDouble("longitude");
                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "koneksi error", Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                    finish();
                    startActivity(getIntent());
                }


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            LatLng target = new LatLng(targetlongitude, targetlatitude);
            mMap.addMarker(new MarkerOptions().position(target).title("target"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target,17));
            Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

        }
    }

}

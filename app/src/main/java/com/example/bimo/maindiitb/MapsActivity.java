package com.example.bimo.maindiitb;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;

    static final String Address = "167.205.34.132";
    static final int Porting = 3111;

    private double Lat;
    private double Lon;

    private ImageView mPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;


    private GoogleMap mMap;
    TextView response;
    private JSONObject json;
    //EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    private String token;
    private String nim;
    private String LOGLOG = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getResources().getConfiguration();

//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Cek orientasi layar dan bertindak seperlunya
        Intent intent = getIntent();
//        Log.d(LOGLOG, "NIMNIMNIM : " + intent.getStringExtra("EXTRA_NIM"));

        Bundle b = intent.getBundleExtra("EXTRA_BUNDEL");
        nim = b.getString("EXTRA_NIM");
        Lat = Double.parseDouble(b.getString("EXTRA_LAT"));
        Lon = Double.parseDouble(b.getString("EXTRA_LON"));
        token = b.getString("EXTRA_TOKEN");

        Toast.makeText(getApplicationContext(), "Token : " + token + " , NIM : " + nim + ", Latitude : " + Lat + ", Longitude : " + Lon, Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        response = (TextView) findViewById(R.id.textResponse);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
//        savedInstanceState.putInt(SOME_VALUE, someIntValue);
//        savedInstanceState.putString(SOME_OTHER_VALUE, someStringValue);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor == mAccelerometer){
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        }else if (event.sensor == mMagnetometer){
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
     }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void dispatchTakePictureIntent(){
        Intent takePictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureintent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                takePictureintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureintent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
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

        // Add a marker in Sydney and move the camera

        LatLng tempat = new LatLng(Lat, Lon);
        mMap.addMarker(new MarkerOptions().position(tempat).title("Marker in ITB"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tempat));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    public void sendPerintah(View view){
//        Intent intent = new Intent(this, CameraActivity.class);
//        Button button = (Button) findViewById(R.id.buttonfoto);
//        startActivity(intent);

        dispatchTakePictureIntent();

    }

    public void sendServer(View view){
        Intent intent = new Intent(this, AnswerActivity.class);
        Bundle b = new Bundle();
        b.putString("EXTRA_NIM", nim);
        b.putString("EXTRA_TOKEN", token);
        b.putDouble("EXTRA_LAT", Lat);
        b.putDouble("EXTRA_LON", Lon);
        intent.putExtra("EXTRA_BUNDEL", b);
        startActivity(intent);
    }
}

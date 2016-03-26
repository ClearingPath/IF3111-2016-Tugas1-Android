package ivanandrianto.com.tubes1;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,SensorEventListener {

    /* Google Maps */
    private GoogleMap map;
    private double latitude, longitude;

    /* Server */
    private String serverResponse;
    final private String address = "167.205.34.132";
    final private int port = 3111;
    private String response = "";
    private String token = "";
    private String status = "";
    final String nim = "13513039";

    /* Censored */
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean accelerometerSet = false;
    private boolean magnetometerSet = false;
    private float[] accelerometerValue = new float[3];
    private float[] magnetometerValue = new float[3];
    private float curDegree = 0f;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private ImageView compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        /* Button camera */
        ImageButton btnCamera = (ImageButton)findViewById(R.id.button1);
        btnCamera.setImageResource(R.drawable.camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Camera.class);
                startActivity(intent);
            }
        });

        /* Button message */
        ImageButton btnMessage = (ImageButton)findViewById(R.id.button2);
        btnMessage.setImageResource(R.drawable.message);
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
                Bundle extras = new Bundle();
                extras.putString("latitude", String.valueOf(latitude));
                extras.putString("longitude", String.valueOf(longitude));
                extras.putString("token", token);
                intent.putExtras(extras);
                startActivityForResult(intent, 1);
            }
        });

        /* Get initial latitude and longitude from server */
        JSONObject json = new JSONObject();
        try{
            if(savedInstanceState == null){
                json.put("com", "req_loc");
                json.put("nim", "13513039");
                String response = new SocketClient(address,port,json).execute().get();
                Toast.makeText(getApplicationContext(), "zzz" + response, Toast.LENGTH_LONG).show();
                JSONObject jsonObject = new JSONObject(response);
                /*latitude = -6.8914906;
                longitude = 107.6084704;*/

                //sementara dibalik
                latitude = Double.parseDouble(jsonObject.optString("longitude").toString());
                longitude = Double.parseDouble(jsonObject.optString("latitude").toString());
                token = jsonObject.optString("token").toString();
                status = jsonObject.optString("status").toString();
            } else {
                latitude = savedInstanceState.getDouble("latitude");
                longitude = savedInstanceState.getDouble("longitude");
                token = savedInstanceState.getString("token");
                status = savedInstanceState.getString("status");
            }

        } catch (JSONException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        /* Sensor */
        compass = (ImageView)findViewById(R.id.compass_pointer);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        /* Fragment */
        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.map);
        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
        map = supportmapfragment.getMap();
        supportmapfragment.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putDouble("longitude", longitude);
        savedInstanceState.putDouble("latitude",latitude);
        savedInstanceState.putString("token", token);
        savedInstanceState.putString("status",status);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng itb = new LatLng(latitude,longitude);
        map.addMarker(new MarkerOptions().position(itb).title("Marker in ITB"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(itb).zoom(15).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    public String getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "reverse_portrait";
            default:
                return "reverse_landscape";
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor == accelerometer){
            System.arraycopy(event.values, 0, accelerometerValue, 0, event.values.length );
            accelerometerSet = true;
        } else if(event.sensor == magnetometer){
            System.arraycopy(event.values, 0, magnetometerValue, 0, event.values.length );
            magnetometerSet = true;
        }
        if(accelerometerSet && magnetometerSet){
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValue, magnetometerValue);
            SensorManager.getOrientation(rotationMatrix,orientation);
            float radian = orientation[0];
            float degree = (float)(Math.toDegrees(radian)+360)%360;

            String orientation = getRotation(this);
            switch(orientation){
                case "landscape":
                    degree+=90;
                    break;
                case "reverse_landscape":
                    degree-=90;
                    break;
                case "reverse_portrait":
                    degree-=180;
                    break;
            }

            RotateAnimation ra = new RotateAnimation(
                    curDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            );
            ra.setDuration(250);
            ra.setFillAfter(true);
            compass.startAnimation(ra);
            curDegree = -degree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = data;
                    Bundle bundle = intent.getExtras();
                    if(bundle!=null) {
                        token = bundle.getString("token");
                        latitude = Double.parseDouble(bundle.getString("latitude"));
                        longitude = Double.parseDouble(bundle.getString("longitude"));
                        LatLng itb = new LatLng(latitude,longitude);
                        map.addMarker(new MarkerOptions().position(itb).title("Marker in ITB"));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(itb).zoom(15).build();
                        map.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                    }
                }
                break;
            }
        }
    }
}
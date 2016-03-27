package ga.wiwit.itbmap;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] resultAccelerometer = new float[3];
    private float[] resultMagnetometer = new float[3];
    private boolean issetAccelerometer = false;
    private boolean issetMagnetometer = false;
    private float currentDegree = 0f;
    private ImageView compass;
    private Communicator comm ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        compass = (ImageView)findViewById(R.id.compass_arrow);
        comm = Communicator.getInstance();
        comm.req_loc();
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        comm.setContext(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == accelerometer.getType()) {
            System.arraycopy(event.values, 0, resultAccelerometer, 0, event.values.length);
            issetAccelerometer = true;
        }
        else {
            System.arraycopy(event.values, 0, resultMagnetometer, 0, event.values.length);
            issetMagnetometer = true;
        }
        if(issetAccelerometer && issetMagnetometer) {
            float[] tempR = new float[9];
            SensorManager.getRotationMatrix(tempR, null, resultAccelerometer, resultMagnetometer);
            float[] orientation = new float[3];
            SensorManager.getOrientation(tempR, orientation);
            float azimuthRad = orientation[0];
            float azimuthDeg = (float)(Math.toDegrees(azimuthRad)+360)%360;

            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            int orientDisplay = display.getRotation();
            if(orientDisplay == 1) {
                azimuthDeg += 90;
            } else if(orientDisplay == 3) {
                azimuthDeg -= 90;
            } else {

            }

            RotateAnimation anim = new RotateAnimation(
                    currentDegree,
                    -azimuthDeg,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            anim.setDuration(250);

            anim.setFillAfter(true);

            compass.startAnimation(anim);
            currentDegree = -azimuthDeg;
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        LatLng itb = new LatLng(-6.891323, 107.610445);
        mMap.addMarker(new MarkerOptions().position(itb).title("Marker in ITB"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itb, 17));
        comm.setMap(mMap);
    }

    public void goCamera(View view) {
        final int REQUEST_IMAGE_PICTURE = 1;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_IMAGE_PICTURE);
    }

    public void goAnswer(View view) {
        Intent intent = new Intent(this, AnswerActivity.class);
        if(intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
}
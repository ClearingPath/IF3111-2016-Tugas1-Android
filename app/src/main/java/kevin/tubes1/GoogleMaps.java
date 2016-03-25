package kevin.tubes1;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.design.widget.FloatingActionButton;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class GoogleMaps extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    static final LatLng defaultLocation = new LatLng(-6.8899f, 107.6100f);
    private GoogleMap googleMap;

    private ImageView compass;
    private float compassDeg = 0f;
    private SensorManager mSensorManager;

    private float mDeviceOrientation;
    private float phoneOrientation = 0;

    private OrientationEventListener mOrientationEventListener;

    private Message msg = Message.getInstance();
    static final int REQUEST_IMAGE_CAPTURE = 1;

    RotateAnimation ra = new RotateAnimation(
            compassDeg,
            0,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f);

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("GoogleMap");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        compass = (ImageView) findViewById(R.id.iv_compass);

        OrientationEventListener mOrientationEventListener =
                new OrientationEventListener(this, SensorManager.SENSOR_DELAY_GAME) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        mDeviceOrientation = orientation;
                    }
                };

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(i);*/
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
    }

    public void setUpMap(){
        googleMap.clear();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15.0f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if ( getResources().getConfiguration().orientation == 1 ) {
            googleMap.setPadding(20, 0, 20, 120);
        } else {
            googleMap.setPadding(0, 0, 120, 10);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start) {
            try {
                JSONObject receivedProblem = new JSONObject((msg.getSock()).Send("{\"com\":\"req_loc\",\"nim\":\"" + msg.getNim() + "\"}"));
                msg.setStarted(true);

                /* Saving the token for future requests */
                msg.setToken(receivedProblem.optString("token"));

                /* Get the first Lat Long */
                msg.setLatLng(receivedProblem.optDouble("latitude"), receivedProblem.optDouble("longitude"));
                LatLng detectedLocation = new LatLng((msg.getLat()),(msg.getLng()));

                /* Add Market to Maps */
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(detectedLocation)
                        .title("Detected Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(detectedLocation, 18.0f));

                String toToast = "Directed to Lat(" + detectedLocation.latitude + ") Long(" + detectedLocation.longitude + ")";
                msg.setLatLng(receivedProblem.optDouble("longitude"), receivedProblem.optDouble("latitude"));
                Toast toast = Toast.makeText(getApplicationContext(), toToast, Toast.LENGTH_SHORT);
                toast.show();
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        if (msg.getStarted()) {
            LatLng detectedLocation = new LatLng((msg.getLat()),(msg.getLng()));

            /* Add Market to Maps */
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(detectedLocation)
                    .title("Detected Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(detectedLocation, 16.0f));
        } else {
            if ( googleMap != null ) {
                googleMap.clear();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16.0f));
            }
        }
    }


    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        float orientation = (90*Math.round(mDeviceOrientation/90))%360;

        boolean isOrientationEnabled;

        try {
            isOrientationEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION) == 1;
        } catch (Settings.SettingNotFoundException e) {
            isOrientationEnabled = false;
        }

        if ( ( getResources().getConfiguration().orientation != 1 ) && ( isOrientationEnabled ) ) {
            if ( (orientation == 270) || (phoneOrientation == 270) ) {
                if(phoneOrientation == 90) {
                    degree += 90;
                }
                phoneOrientation = 270;
                degree += 90;
            }
            if ( (orientation == 90) || (phoneOrientation == 90) ){
                if(phoneOrientation == 270) {
                    degree -= 90;
                }
                phoneOrientation = 90;
                degree -= 90;
            }
        } else {
            phoneOrientation = 0;
            degree += 0;
        }

        ra = new RotateAnimation(
                compassDeg,
                (-degree),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(200);
        ra.setFillAfter(true);

        compass.startAnimation(ra);
        compassDeg = -degree;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            compass = (ImageView) findViewById(R.id.iv_compass);
            compass.startAnimation(ra);
        }
    }



}

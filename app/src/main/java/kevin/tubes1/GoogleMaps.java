package kevin.tubes1;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback {
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
                    public void onOrientationChanged(int orientation){
                        mDeviceOrientation = orientation;
                    }
                };

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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
}

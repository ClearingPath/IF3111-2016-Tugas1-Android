package nizami_13512501.tubes1_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private LocationSource.OnLocationChangedListener mapLocationListener = null;
    private LocationManager locMgr = null;

    private GoogleMap mMap;

    private LatLng targetLatLng;
    private LatLng currentLatLng;

    public static final String EXTRAS_SERVERIP = "nizami_13512501.Tubes1-Android.MapsActivity.Extras.ServerIP";
    public static final String EXTRAS_SERVERPORT = "nizami_13512501.Tubes1-Android.MapsActivity.Extras.ServerPort";
    public static final String EXTRAS_NIM = "nizami_13512501.Tubes1-Android.MapsActivity.Extras.ServerNIM";


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SUBMIT_ACTIVITY_REQUEST_CODE = 101;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private ServerAsistenClient serverAsistenClient;

    private String serverasistenIp;
    private int serverasistenPort;
    private String serverasistenNim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent callerIntent = getIntent();

        serverasistenIp = callerIntent.getExtras().getString(EXTRAS_SERVERIP,getString(R.string.serverasisten_ip));
        serverasistenPort = callerIntent.getExtras().getInt(EXTRAS_SERVERPORT,getResources().getInteger(R.integer.serverasisten_port));
        serverasistenNim = callerIntent.getExtras().getString(EXTRAS_NIM,getString(R.string.serverasisten_nim));

        serverAsistenClient = new ServerAsistenClient(serverasistenIp, serverasistenPort, this);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        targetLatLng = new LatLng(0,0);
        serverAsistenClient.doFirstRequest(serverasistenNim);

        locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();

    }

    public void updateTargetLatLng(LatLng latLng){
        targetLatLng = latLng;
        updateMarkers();
        updateCamera();
    }

    //http://stackoverflow.com/questions/3932502/calcute-angle-between-two-latitude-longitude-points
    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }


    public void updateMarkers(){
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(targetLatLng).title("target"));
        if (currentLatLng!=null) {
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(300)).position(currentLatLng).title("your location"));

            //untuk arah panah
            float r = - (float) angleFromCoordinate(currentLatLng.latitude,currentLatLng.longitude,targetLatLng.latitude,targetLatLng.longitude);
            ImageView arrowImageView = (ImageView) findViewById(R.id.arrow);
            arrowImageView.setRotation(r);
            arrowImageView.invalidate();
            //TODO
        }
    }

    public void updateCamera(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(targetLatLng);
        if (currentLatLng!=null)
            builder.include(currentLatLng);
        LatLngBounds bounds = builder.build();

        int padding=12;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    Toast toast;
    public void notifyUser(String response, int duration){
        Context context = getApplicationContext();
        CharSequence text = response;

        if (toast!=null)toast.cancel();
        toast = Toast.makeText(context, text,duration);
        toast.show();
    }

    public void submitAnswer(View view){
        Intent intent = new Intent(this,SubmitAnswerActivity.class);
        startActivityForResult(intent, SUBMIT_ACTIVITY_REQUEST_CODE);
    }

    public void camera(View view){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                galleryAddPic();
                Toast.makeText(getApplicationContext(),
                        "Photo saved!", Toast.LENGTH_SHORT)
                        .show();
            } else {

            }
        }
        if (requestCode == SUBMIT_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                String answer = data.getStringExtra("result");
                serverAsistenClient.submitAnswer(serverasistenNim,answer,targetLatLng);
            }else{

            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(fileUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TubesPBD");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("TubesPBD", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + "_"+serverasistenNim+"_"+targetLatLng.latitude+"_"+targetLatLng.longitude+".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("LOC", "IN ON LOCATION CHANGE, lat=" + location.getLatitude()+ ", lon=" + location.getLongitude());
        currentLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        updateMarkers();
        updateCamera();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //TODO
    }

    @Override
    public void onProviderEnabled(String provider) {
        notifyUser("location provider enabled", Toast.LENGTH_SHORT);
        //TODO
    }

    @Override
    public void onProviderDisabled(String provider) {
        notifyUser("location provider disabled. please enable GPS or other location providers", Toast.LENGTH_SHORT);
        //TODO
    }
}

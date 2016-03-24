package com.example.user.gps;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
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
    private Button cameraButton;
    private Button messageButton;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int IMAGE_CAPTURED = 100;
    public static final int RESPONSE_RECEIVED = 200;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        mPointer = (ImageView) findViewById(R.id.pointer);
        initializeMap();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        cameraButton = (Button) findViewById(R.id.buttonCamera);
        messageButton = (Button) findViewById(R.id.buttonMessage);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, IMAGE_CAPTURED);
            }
        });
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleMapsActivity.this, SubmitMessage.class);
                startActivityForResult(intent, RESPONSE_RECEIVED);
            }
        });
        if(Container.getisFirst()) {
            new requestService().execute();
            Container.setisFirst(false);
            Toast.makeText(GoogleMapsActivity.this,
                    "Requesting first location.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == IMAGE_CAPTURED)
        {
            if (resultCode == RESULT_OK)
            {
                String pathToInternallyStoredImage = saveToInternalStorage(this, imageUri);
                Toast.makeText(GoogleMapsActivity.this,
                                "Image saved to "+pathToInternallyStoredImage,
                        Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(GoogleMapsActivity.this,
                                "No image was saved",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == RESPONSE_RECEIVED){
            if(Container.getStatus().equals("no_status")){
                //Do Nothing
            }
            else if(Container.getStatus().equals("ok")){
                mMap.clear();
                Toast.makeText(GoogleMapsActivity.this,
                        "Correct Answer, Please proceed to next location.",
                        Toast.LENGTH_SHORT).show();
                setMarker(Container.getLng(), Container.getLtd());           //dibalik
            }else if(Container.getStatus().equals("wrong_answer") && !(Container.getCheck() == 1)){
                Toast.makeText(GoogleMapsActivity.this,
                        "Wrong Answer, Please try again.",
                        Toast.LENGTH_SHORT).show();
            }else if(Container.getStatus().equals("finish") && (Container.getCheck() == 1)){
                Toast.makeText(GoogleMapsActivity.this,
                        "Congratulations, You have reached the final location.",
                        Toast.LENGTH_SHORT).show();
                mMap.clear();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void setMarker(double ltd, double lng){
        LatLng marker = new LatLng(ltd,lng);
        mMap.addMarker(new MarkerOptions().position(marker).title("Designated Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 18));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    private class requestService extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            Socket socket = null;
            String response = "";
            JSONObject jsonRequest = new JSONObject();
            try {
                jsonRequest.put("com", "req_loc");
                jsonRequest.put("nim", "13513003");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                socket = new Socket("167.205.34.132", 3111);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream);
                writer.println(jsonRequest.toString());
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                response = reader.readLine();
                socket.close();
                return response;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("response : ",result);
            try {
                if(result != null) {
                    JSONObject jsonResponse = new JSONObject(result);
                    Container.setLtd(jsonResponse.getDouble("latitude"));
                    Container.setLng(jsonResponse.getDouble("longitude"));
                    Container.setToken(jsonResponse.getString("token"));
                    setMarker(Container.getLng(), Container.getLtd());          //dibalik
                }
                else{
                    Toast.makeText(GoogleMapsActivity.this,
                            "Failed to receive response",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}
    public static Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type)
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "camera");

        createMediaStorageDir(mediaStorageDir);

        return createFile(type, mediaStorageDir);
    }

    private static File getOutputInternalMediaFile(Context context, int type)
    {
        File mediaStorageDir = new File(context.getFilesDir(), "myInternalPicturesDir");

        createMediaStorageDir(mediaStorageDir);

        return createFile(type, mediaStorageDir);
    }

    private static void createMediaStorageDir(File mediaStorageDir)
    {
        if (!mediaStorageDir.exists())
        {
            mediaStorageDir.mkdirs();
        }
    }

    private static File createFile(int type, File mediaStorageDir )
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir .getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        }
        return mediaFile;
    }

    public static String saveToInternalStorage(Context context, Uri tempUri)
    {
        InputStream in = null;
        OutputStream out = null;

        File sourceExternalImageFile = new File(tempUri.getPath());
        File destinationInternalImageFile = new File(getOutputInternalMediaFile(context,MEDIA_TYPE_IMAGE).getPath());

        try
        {
            destinationInternalImageFile.createNewFile();

            in = new FileInputStream(sourceExternalImageFile);
            out = new FileOutputStream(destinationInternalImageFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return destinationInternalImageFile.getPath();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
}

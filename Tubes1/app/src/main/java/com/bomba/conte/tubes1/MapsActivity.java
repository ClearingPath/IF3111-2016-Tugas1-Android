package com.bomba.conte.tubes1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String serverAddress = "167.205.24.132";
    private int serverPort = 3111;
    private JSONObject obj;

    ImageButton camera, chat;
    String currentPhotoPath;

    private class Mediator extends AsyncTask<Void, Void, String> {

        String response = "";
        TextView textResponse;

        @Override
        protected String doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream output;
            DataInputStream input;

            try {
                socket = new Socket(serverAddress, serverPort);

                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                output.writeUTF(obj.toString());
                return input.readUTF();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            Log.d("Result", result);
            builder.setTitle("RESUPONSU ALART!!!")
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

        chat = (ImageButton)findViewById(R.id.chatButton);
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

        try {
            obj = new JSONObject();
            obj.put("com", "req_loc");
            obj.put("nim", "13513013");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Mediator().execute();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

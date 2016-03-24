package com.example.yoga.tubes1android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private GoogleMap mMap;
    private double targetlongitude, targetlatitude;
    private static boolean first=true;
    private String status,nim,token;
    private Socket socket;
    private static final int SERVERPORT = 3111;
    private static final String SERVER_IP = "167.205.34.132";
    PrintWriter out;
    BufferedReader input;

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
        new Thread(new ClientThread()).start();

        if(first) {
            try {
                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("com","req_loc");
                jsonObject.accumulate("nim","13513030");


                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();
                out.print(json);
                String response=input.readLine();
                Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                final JSONObject obj=new JSONObject(response);
                token=obj.getString("token");
                status=obj.getString("status");
                nim=obj.getString("status");
                targetlatitude=obj.getDouble("latitude");
                targetlongitude=obj.getDouble("longitude");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            first=false;

        }else{
            Bundle b = getIntent().getExtras();
            token=b.getString("token");
            nim=b.getString("nim");
            targetlatitude=b.getFloat("latitude");
            targetlongitude=b.getFloat("longitude");
        }
    }

    class ClientThread implements  Runnable{
        @Override
        public  void run(){
            InetAddress serverAddr = null;
            try {
                serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                input= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        LatLng target = new LatLng(targetlatitude, targetlongitude);
        mMap.addMarker(new MarkerOptions().position(target).title("target"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target,17));
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
                Bundle b = new Bundle();
                b.putString("token", token);
                b.putString("nim",nim);
                b.putDouble("latitude", targetlatitude);
                b.putDouble("longitude",targetlongitude);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
                break;


            default:
                break;
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}

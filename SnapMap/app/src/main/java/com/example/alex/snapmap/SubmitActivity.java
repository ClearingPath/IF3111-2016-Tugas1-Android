package com.example.alex.snapmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SubmitActivity extends AppCompatActivity {
    private Button b3;
    private JSONObject json;
    private double Lat,Lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        b3 = (Button) findViewById(R.id.button);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(v);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{"GKU Barat", "GKU Timur", "Intel", "CC Barat", "CC Timur", "DPR", "Sunken", "Perpustakaan", "PAU", "Kubus"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    public void submitAnswer(View v) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String option = spinner.getSelectedItem().toString();
        switch(option) {
            case "GKU Barat" :
                option = "gku_barat";
                break;
            case "GKU Timur" :
                option = "gku_timur";
                break;
            case "Intel" :
                option = "intel";
                break;
            case "CC Barat" :
                option = "cc_barat";
                break;
            case "CC Timur" :
                option = "cc_timur";
                break;
            case "DPR" :
                option = "dpr";
                break;
            case "Sunken" :
                option = "sunken";
                break;
            case "Perpustakaan" :
                option = "perpustakaan";
                break;
            case "PAU" :
                option = "pau";
                break;
            case "Kubus" :
                option = "kubus";
                break;
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( GPSLocationTime - NetLocationTime > 0 ) {
            assert locationGPS != null;
            Lat = locationGPS.getLongitude();
            Lng = locationGPS.getLatitude();
        }
        else {
            assert locationNet != null;
            Lat = locationNet.getLongitude();
            Lng = locationNet.getLatitude();
        }


        //String json = "{\"status\":\"ok\", \"nim\":\"13512999\",\"longitude\":\"107.610101\",\"latitude\":\"-6.890535\",\"token\":\"21nu2f2n3rh23diefef23hr23ew\"}";
        try {
            json = new JSONObject();
            json.put("com", "answer");
            json.put("nim", "13513023");
            json.put("answer", option);
            json.put("longitude", Lng);
            json.put("latitude", Lat);
            json.put("token", 123123);
        } catch (Throwable t) {
            Log.e("MyApp", "Could not parse malformed JSON: \"" + json.toString() + "\"");
        }

        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;

        try {
            socket = new Socket("167.205.34.132", 3111);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF(json.toString());

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Response");

            // set dialog message
            alertDialogBuilder
                    .setMessage(dataInputStream.readUTF())
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (dataOutputStream != null){
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (dataInputStream != null){
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


    }
}

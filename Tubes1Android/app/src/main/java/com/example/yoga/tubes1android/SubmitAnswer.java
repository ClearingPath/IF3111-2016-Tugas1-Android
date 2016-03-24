package com.example.yoga.tubes1android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

public class SubmitAnswer extends AppCompatActivity {
    private Socket socket;
    private Spinner spinner;
    private Button button;
    static private double targetlatitude, targetlongitude;
    static private String token, nim,status;
    static String ans;
    PrintWriter out;
    BufferedReader input;
    String json,response;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addListenerOnButton();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent lastintent=getIntent();
        token=lastintent.getStringExtra("token");
        nim=lastintent.getStringExtra("nim");
        targetlatitude=lastintent.getDoubleExtra("latitude", 1);
        targetlongitude=lastintent.getDoubleExtra("longitude",1.0);
        Toast.makeText(getApplicationContext(), String.valueOf(targetlatitude), Toast.LENGTH_LONG).show();
    }


    public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "wowww", Toast.LENGTH_LONG).show();
                switch(String.valueOf(spinner.getSelectedItem())){
                    case "GKU Barat":ans="gku_barat";
                        break;
                    case "GKU Timur":ans="gku_timut";
                        break;
                    case "Intel":ans="intel";
                        break;
                    case "CC Barat":ans="cc_barat";
                        break;
                    case "CC Timur":ans="cc_timur";
                        break;
                    case "DPR":ans="dpr";
                        break;
                    case "Sunken":ans="sunken";
                        break;
                    case "Perpustakaan":ans="perpustakaan";
                        break;
                    case "PAU":ans="pau";
                        break;
                    case "Kubus":ans="kubus";
                        break;
                }
                new Connect().execute("");


            }

        });
    }

    private class Connect extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                socket = new Socket("167.205.34.132", 3111);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                input= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("com", "answer");
                jsonObject.put("nim", nim);
                jsonObject.put("answer", ans);
                jsonObject.put("latitude", String.valueOf(targetlatitude));
                jsonObject.put("longitude", String.valueOf(targetlongitude));

                jsonObject.put("token", token);
                json = jsonObject.toString();
                System.out.println(json);
                out.println(json);
                response=input.readLine();
                System.out.println(response);
                final JSONObject obj=new JSONObject(response);
                status=obj.getString("status");
                if(status.equals("ok")){
                    token=obj.getString("token");
                    nim=obj.getString("nim");
                    targetlatitude=obj.getDouble("latitude");
                    targetlongitude=obj.getDouble("longitude");
                }else{
                    token=obj.getString("token");
                    nim=obj.getString("nim");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("token", token);
            resultIntent.putExtra("nim",nim);
            resultIntent.putExtra("latitude", targetlatitude);
            resultIntent.putExtra("longitude", targetlongitude);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();

        }


    }

}

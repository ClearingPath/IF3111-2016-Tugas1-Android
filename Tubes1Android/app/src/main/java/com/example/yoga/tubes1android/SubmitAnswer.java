package com.example.yoga.tubes1android;

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
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "10.0.2.2";
    private Spinner spinner;
    private Button button;
    static private double targetlatitude, targetlongitude;
    static private String token, nim,status;
    static String ans;
    PrintWriter out;
    BufferedReader input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addListenerOnButton();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        token=b.getString("token");
        nim=b.getString("nim");
        targetlatitude=b.getFloat("latitude");
        targetlongitude=b.getFloat("longitude");

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


    public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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

                try {
                    String json = "";

                    // 3. build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("com","answer");
                    jsonObject.accumulate("nim",nim);
                    jsonObject.accumulate("answer",ans);
                    jsonObject.accumulate("longitude",targetlongitude);
                    jsonObject.accumulate("latitude",targetlatitude);
                    jsonObject.accumulate("token",token);


                    // 4. convert JSONObject to JSON to String
                    json = jsonObject.toString();
                    out.print(json);
                    String response=input.readLine();
                    Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
                    final JSONObject obj=new JSONObject(response);
                    status=obj.getString("status");
                    if(status.equals("ok")){
                        token=obj.getString("token");
                        nim=obj.getString("status");
                        targetlatitude=obj.getDouble("latitude");
                        targetlongitude=obj.getDouble("longitude");
                    }else{
                        token=obj.getString("token");
                        nim=obj.getString("status");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                Bundle b = new Bundle();
                b.putString("token", token);
                b.putString("nim",nim);
                b.putDouble("latitude",targetlatitude);
                b.putDouble("longitude",targetlongitude);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
            }

        });
    }

}

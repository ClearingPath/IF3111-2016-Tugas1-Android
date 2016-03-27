package com.example.bimo.maindiitb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public final static String NIMNYA = "";
    private JSONObject json;
    static final String Address = "167.205.34.132";
    static final int Porting = 3111;
    String response="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(NIMNYA, "NIMNIMNIM : " + "135150");

        setContentView(R.layout.activity_main);
        json = new JSONObject();

    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
//         Do something in response to button
        this.sendServer();

        // minta response yang inputannya adalah addr, port dan json (lama)

//        String respon = new myClient.execute().get();
        //dapet response berupa json dlm bentuk string

        try{
            String myClient = new Client(Address, Porting, response, json).execute().get();

            json = new JSONObject(myClient);
        //membuat objek json(baru) dari string yang didapat
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.d(NIMNYA, "NIMNIMNIM : " + "135150");

        // mengirim intent ke map activity
        Intent intent = new Intent(this, MapsActivity.class);

        try{
            // memasukkan json(baru) ke MapActivity
            Bundle b = new Bundle();
            b.putString("EXTRA_NIM", json.getString("nim"));
            b.putString("EXTRA_LAT", json.getString("latitude"));
            b.putString("EXTRA_LON", json.getString("longitude"));
            b.putString("EXTRA_TOKEN", json.getString("token"));
            intent.putExtra("EXTRA_BUNDEL", b);
            startActivity(intent);
        }catch(JSONException e){
            e.printStackTrace();
        }

        // kirim semua yang udah bentuk string ke mapactivity


        }

    public void sendServer(){
        try {
            String message = "13513075";

            json.accumulate("com", "req_loc");
            json.accumulate("nim", message);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    }
//
//}

package com.example.bimo.maindiitb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerActivity extends AppCompatActivity {
    private String selected;

    private JSONObject json;
    static final String Address = "167.205.34.132";
    static final int Porting = 3111;
    String response="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

// Create an ArrayAdapter using the string array and a default spinner layout

//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner

        selected = String.valueOf(spinner.getSelectedItem());
    }

    public void sendJawab(View view){
        // masukin string ke json untuk request terlebih dahulu
        Intent intent = getIntent();

        Bundle b = intent.getBundleExtra("EXTRA_BUNDEL");
        double Lat = b.getDouble("EXTRA_LAT");
        double Lon = b.getDouble("EXTRA_LON");
        String Token = b.getString("EXTRA_TOKEN");
        JSONObject json2 = new JSONObject();
        try{
            json2.accumulate("com", "answer");
            json2.accumulate("nim", "13513075");
            json2.accumulate("answer", selected);
            json2.accumulate("longitude", Lon);
            json2.accumulate("latitude", Lat);
            json2.accumulate("token", Token);
        }catch(JSONException e){
            e.printStackTrace();
        }

        try{
            String myClient = new Client(Address, Porting, response, json2).execute().get();
            //mendapat jawaban berupa reponse nih, itu json dalam bentuk string

            json = new JSONObject(myClient);
            //membuat objek json(baru) dari string yang didapat

            Toast.makeText(getApplicationContext(), json.toString(), Toast.LENGTH_LONG).show();
//            if(json.getString("check").equals("1")){
//                Intent intent2 = new Intent(this, MainActivity.class);
//
//                startActivity(intent2);
//            }else{
//                Intent intent2 = new Intent(this, MapsActivity.class);
//
//                Bundle bun = new Bundle();
//
//                bun.putString("EXTRA_TOKEN", json.getString("token"));
//                bun.putString("NEXT_STATUS", json.getString("status"));
//                bun.putString("EXTRA_NIM", json.getString("nim"));
//                bun.putString("EXTRA_LAT", json.getString("latitude"));
//                bun.putString("EXTRA_LON", json.getString("longitude"));
//                intent2.putExtra("EXTRA_BUNDEL", bun);
//                startActivity(intent2);
//            }

        }catch(Exception e){
            e.printStackTrace();
        }


    }
}

package com.example.x450.maps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Submit extends AppCompatActivity implements AdapterView.OnItemSelectedListener,ResponseHandlerInterface {
    private JSONObject result;
    private Spinner location_spinner;
    private String selectedLoc;
    private String latitude;
    private String longitude;
    private String token;
    private RW rw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rw = new RW();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        addItemsOnLocSpinner();
        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        Log.d("latitude",latitude);
        longitude = intent.getStringExtra("longitude");
        Log.d("longitude",longitude);
        token = intent.getStringExtra("token");

    }

    public void addItemsOnLocSpinner() {

        location_spinner = (Spinner) findViewById(R.id.location_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        location_spinner.setAdapter(adapter);
        location_spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Log.d("selected ",(String) parent.getItemAtPosition(pos));
        selectedLoc= (String) parent.getItemAtPosition(pos);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    @Override
    public void onResponse(JSONObject result){
        String status = null;
        String nextLong = null;
        String nextLat = null;
        String token = null;
        this.result = result;
        Log.d("SubmitResResult ",result.toString());
        try {
            status = result.getString("status");
            if(status.equals("finish") || status.equals("wrong_answer")){
                token = result.getString("token");
            }
            else{
                nextLong = result.getString("longitude");
                nextLat = result.getString("latitude");
                token = result.getString("token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String tempStat = status;
        final Context curContext = this;
        Log.d("tempStat",status);
        if (status.equals("wrong_answer")){
            rw.generateNoteOnSD(this,"Server: "+result.toString());
            Toast t = Toast.makeText(curContext,tempStat, Toast.LENGTH_LONG);
            t.show();
        }else {
            rw.generateNoteOnSD(this, "Server: " + result.toString());
            Toast t = Toast.makeText(curContext,result.toString(), Toast.LENGTH_LONG);
            t.show();
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("status", status);
            if (status.equals("ok")){
                intent.putExtra("nextLong", nextLong);
                intent.putExtra("nextLat", nextLat);
            }
            intent.putExtra("token", token);
            setResult(Activity.RESULT_OK, intent);
            Log.d("move", "to map");
//            startActivityForResult(intent,1);
            finish();

        }
    }

    public void onSubmit(View view) throws JSONException {
//        new AsyncAction(this,this).execute();
        JSONObject tempJson = new JSONObject();
        tempJson.put("com","answer");
        tempJson.put("nim", "13513087");
        tempJson.put("answer", selectedLoc);
        tempJson.put("longitude", longitude);
        tempJson.put("latitude", latitude);
        tempJson.put("token", token);
        Log.d("tempJson", tempJson.toString());
        rw.generateNoteOnSD(this,"Client: " + tempJson.toString());

        new AsyncAction(this,this).execute(tempJson.toString());
    }
}

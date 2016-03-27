package com.example.mochginanjarbusiri.busiri_map;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SubmitActivity extends AppCompatActivity {
    private String[] states;
    private Spinner spinner;
    private GoogleMap mMap;
    private double latitude, longitude;
    private String token;
    private String response = "";
    private final String nim = "13513111";
    private String location;
    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        spinner = (Spinner) findViewById(R.id.location_spinner);

        List<String> locations = new ArrayList<String>();
        locations.add("gku_barat");
        locations.add("gku_timur");
        locations.add("intel");
        locations.add("cc_barat");
        locations.add("cc_timur");
        locations.add("dpr");
        locations.add("oktagon");
        locations.add("perpustakaan");
        locations.add("pau");
        locations.add("kubus");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinner.getSelectedItem().toString();
                Toast.makeText(SubmitActivity.this, selected, Toast.LENGTH_SHORT).show();
                location = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView tv = (TextView)findViewById(R.id.textview);

        Button button_submit = (Button)findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAnswer();
                //finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            this.latitude = bundle.getDouble("latitude");
            this.longitude = bundle.getDouble("longitude");
            this.token = bundle.getString("token");
        }
    }

    public void sendAnswer()
    {
        JSONObject answer = new JSONObject();
        try {
            answer.put("com", "answer");
            answer.put("nim", this.nim);
            answer.put("answer", this.location);
            answer.put("longitude", this.longitude);
            answer.put("latitude", this.latitude);
            answer.put("token", this.token);
            Log.d("Mengirim ke Server", answer.toString() + " " + mydate);

            this.response = new SocketClient(answer.toString(), mMap).execute().get();
            Toast.makeText(getApplicationContext(), "RESPONSE: " + response, Toast.LENGTH_LONG).show();
            JSONObject json = new JSONObject(response);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("token", json.getString("token"));

            if (json.getString("status").equals("ok"))
            {
                bundle.putDouble("latitude", json.getDouble("latitude"));
                bundle.putDouble("longitude", json.getDouble("longitude"));
                Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_LONG).show();
            }
            else if (json.getString("status").equals("wrong_answer"))
            {
                Toast.makeText(getApplicationContext(), "Wrong Answer", Toast.LENGTH_LONG).show();
            }
            else if (json.getString("status").equals("finish")) {
                Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_LONG).show();
            }
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

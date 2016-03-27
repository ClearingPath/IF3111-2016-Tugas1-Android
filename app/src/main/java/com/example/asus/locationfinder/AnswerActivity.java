package com.example.asus.locationfinder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.MacSpi;

public class AnswerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle("Send Answer");

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.loc_name, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void submitAnswer(View view) {
        String answer = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();
        new SendaLocationGetaLocation().execute(answer);
    }

    private class SendaLocationGetaLocation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... answer) {
            try {
                JSONObject request = new JSONObject();
                request.put("com", "answer");
                request.put("nim", "13511008");
                request.put("answer", answer[0]);
                request.put("latitude", MapsActivity.tgt.getLatLng().latitude);
                request.put("longitude", MapsActivity.tgt.getLatLng().longitude);
                request.put("token", MapsActivity.tgt.getToken());
                Log.i("Log", android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ssaa", new java.util.Date()) + " " + request.toString());
                Socket socket = new Socket(MapsActivity.ServerIP, MapsActivity.PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(request.toString());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.flush();
                String result = in.readLine();
                socket.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                Log.i("Log", android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ssaa", new java.util.Date()) + " " + response);
                JSONObject json = new JSONObject(response);
                String status = json.getString("status");

                if (status.equals("ok")) {
                    MapsActivity.tgt.update(response);
                    setResult(MapsActivity.OK);
                }
                else if (status.equals("wrong_answer")) {
                    MapsActivity.tgt.setToken(json.getString("token"));
                    setResult(MapsActivity.WRONG);
                }
                else {
                    MapsActivity.tgt.setToken(json.getString("token"));
                    setResult(MapsActivity.FINISH);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
    }
}
